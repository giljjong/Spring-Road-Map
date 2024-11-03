package hello.springtx.apply;

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
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {

        // 시나리오 : 외부에서 호출이 오고 외부 메소드 실행 시 내부 호출 수행


        /**
         * external()은 @Transactional 애노테이션이 없어서 트랜잭션 없이 시작한다.
         * 그러나 내부에서 @Transactional이 있는 internal()을 호출하는 것을 확인할 수 없다.
         * 이 경우 external()은 트랜잭션이 없지만 internal()은 트랜잭션이 적용되는 것처럼 보인다.
         * 하지만 실제로 트랜잭션이 적용되지 않는다.
         *
         * 1. 클라이언트 테스트 코드는 callService.external()을 호출한다. 여기서 callService는 트랜잭션 프록시이다.
         * 2. callService의 트랜잭션 프록시가 호출된다.
         * 3. external() 메서드에는 @Transactional이 없다. 따라서 트랜잭션 프록시는 트랜잭션을 적용하지 않는다.
         * 4. 트랜잭션을 적용하지 않고 실제 callService 객체 인스턴스의 external()을 호출한다.
         * 5. external()은 내부에서 internal() 메서드를 호출한다. 하지만 internal에서 문제가 발생한다.
         *
         * 사유 : 자바 언어에서는 메서드 앞에 별도의 참조가 없으면 this 라는 뜻으로 자기 자신의 인스턴스를 가리킨다.
         *      자기 자신의 내부 메서드를 호출하는 this.internal()이 되는데
         *      여기서 this는 자기 자신을 가리키므로 실제 대상 객체('target')의 인스턴스를 뜻한다.
         *      결과적으로 이러한 내부 호출은 프록시를 거치지 않는다. 따라서 트랜잭션을 적용할 수 없다.
         *      즉, target에 있는 internal()을 직접 호출하게 된것이다.
         */
        public void external() {
            log.info("call external");
            printTxInfo();
            internal();
        }

        /**
         * 1. 클라이언트 테스트 코드가 callService.internal() 호출. callService는 프록시이다.
         * 2. callService의 트랜잭션 프록시가 호출
         * 3. internal() 메서드에 @Transactional이 붙어 있으므로 트랜잭션 프록시는 트랜잭션을 적용
         * 4. 트랜잭션 적용 후 실제 callService 객체 인스턴스의 internal() 호출
         * 실제 callService가 처리를 완료하면 응답이 트랜잭션 프록시로 돌아오고 트랜잭션 프록시는 트랜잭션을 완료
         */
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
