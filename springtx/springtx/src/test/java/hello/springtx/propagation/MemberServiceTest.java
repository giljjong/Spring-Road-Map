package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On
     */
    @Test
    void outerTxOff_success() {

        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On Exception
     */
    @Test
    void outerTxOff_fail() {

        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:Off
     * logRepository    @Transactional:Off
     */
    @Test
    void singleTx() {

        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On
     */
    @Test
    void outerTxOn_success() {

        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On Exception
     */
    @Test
    void outerTxOn_fail() {

        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // when : 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On Exception
     */
    @Test
    void recoverException_fail() {

        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // when : 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {

        // given
        String username = "로그예외_recoverException_success";

        // when
        memberService.joinV2(username);

        // when : member 저장, log 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

    }
}