package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop class={}", basicService.getClass());
        // 이를 통해 aopProxy가 동작이 되는지 확인
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void txTest() {
        basicService.tx();
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxApplyBasicConfig {

        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        @Transactional
        public void tx() {
            // 트랜잭션을 사용할 수 있는지 확인하여 트랜잭션을 시작한 다음
            // 실제 호출이 끝나고 프록시로 제어가(리턴) 돌아오면 프록시는 트랜잭션 로직을 커밋하거나 롤백 후 트랜잭션 종료
            log.info("call tx");
            // 트랜잭션이 활성화 되었는지 확인 가능
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }

        public void nonTx() {
            // 트랜잭션을 사용할 수 있는지 확인하지만 적용대상이 아니기 때문에 그냥 호출 후 종료한다.
            log.info("call nonTx");
            // 트랜잭션이 활성화 되었는지 확인 가능
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("nonTx active={}", txActive);
        }
    }
}
