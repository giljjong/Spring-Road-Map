package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스에 의존
 */
@Slf4j
public class MemberServiceV4 {
    private final MemberRepository memberRepository;

    // PlatformTransactionManager을 두는 이유는 유연성이 생기기때문
    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(toId, money, fromId);
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

    private void bizLogic(String toId, int money, String fromId) {
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
