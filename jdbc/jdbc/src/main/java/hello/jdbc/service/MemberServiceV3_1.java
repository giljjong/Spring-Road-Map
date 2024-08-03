package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    // private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 비즈니스 로직 시작
            bizLogic(toId, money, fromId);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        // 커밋되거나 롤백될때 알아서 close 해준다.

    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                // 커넥션 풀에 반환되는 것을 고려 AutoCommit을 풀지 않으면 해당 커넥션은 계속해서 트랜잭션이 걸린다.
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e ) {
                // exception을 로그로 남길때에는 = {}를 쓰지 않는다.
                log.info("error", e);
            }
        }
    }

    private void bizLogic(String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        // 검증에 문제 발생 시 아래로 넘어가지 않고 에러를 발생시킨다.
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
