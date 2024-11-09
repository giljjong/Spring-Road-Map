package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;
import java.rmi.UnexpectedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        // 커넥션 획득
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        // 트랜잭션 커밋
        txManager.commit(status);
        // 커밋 이후 커넥션을 풀에 되돌려줌
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        // 커넥션 획득
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        // 트랜잭션 롤백
        txManager.rollback(status);
        // 롤백 이후 커넥션을 풀에 되돌려줌
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        // 커넥션 획득
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋 시작");
        // 트랜잭션 커밋
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        // 커넥션 획득
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋 시작");
        // 트랜잭션 커밋
        txManager.commit(tx2);

        // 결과 : 동일한 커넥션(conn0)을 사용함
        /* 사유 : 커넥션 풀이 실제 커넥션을 그대로 반환하는 것이 아닌 내부 관리를 위해
         * 히카리 프록시 커넥션이라는 객체를 생성해서 반환한다.
         * 내부에는 실제 커넥션이 포함되어 있다.
         * 객체의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.
         */
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        // 커넥션 획득
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋 시작");
        // 트랜잭션 커밋
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        // 커넥션 획득
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백 시작");
        // 트랜잭션 커밋
        txManager.rollback(tx2);
    }

    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작"); // 외부 트랜잭션 시작 중 내부 트랜잭션이 시작됨
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        // 커밋은 하나만 수행하기 때문에 참여상태인 내부 트랜잭션은 실제론 커밋하지 않고 아무 일이 발생하지 않는다.
        txManager.commit(inner);
        // 따라서 내부 트랜잭션에서 DB 커넥션을 통한 물리 트랜잭션을 커밋하면 안된다.

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);

    }

    @Test
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작"); // 외부 트랜잭션 시작 중 내부 트랜잭션이 시작됨
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 커밋");
        // 커밋은 하나만 수행하기 때문에 참여상태인 내부 트랜잭션은 실제론 커밋하지 않고 아무 일이 발생하지 않는다.
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);
        // 결과 : 모두 롤백
    }

    @Test
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작"); // 외부 트랜잭션 시작 중 내부 트랜잭션이 시작됨
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 롤백");
        // 실제 물리 트랜잭션을 롤백하지는 않지만 기존 트랜잭션을 롤백 전용으로 표시한다.(rollback-only)
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        // 커밋을 호출했지만 전체 트랜잭션이 롤백 전용으로 표시되어있어서 물리 트랜잭션을 롤백한다.
        // txManager.commit(outer);
        // 기대한 결과가 다르기때문에 예상하지 못한 롤백 에러(UnexpectedRollbackException)라는 명확한 이유를 던져준다.
        assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
        // 결과 : 모두 롤백
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); // true

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", inner.isNewTransaction()); // true

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
    }
}
