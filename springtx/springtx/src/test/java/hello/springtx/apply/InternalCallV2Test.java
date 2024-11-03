package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        // 시나리오 : 외부에서 호출이 오고 외부 메소드 실행 시 내부 호출 수행

        // final로 선언 시 컴파일 시점에서 생성자에 주입되어야 함을 알려준다.
        private final InternalService internalService;
        public void external() {
            log.info("call external");
            printTxInfo();
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    /**
     * 호출 흐름
     * 1. 클라이언트 테스트 코드는 callService.external()을 호출한다.
     * 2. callService는 실제 callService 객체 인스턴스이다.
     * 3. callService는 주입 받은 internalService.internal()을 호출한다.
     * 4. internalService는 트랜잭션 프록시이다. internal() 메서드에 @Transactional이 붙어 있으므로
     *    트랜잭션 프록시는 트랜잭션을 적용한다.
     * 5. 트랜잭션 적용 후 실제 internalService 객체 인스턴스의 internal()을 호출한다.
     */
    static class InternalService {
        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
