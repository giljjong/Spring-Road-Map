package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            // false로 해주어야 트랜잭션이 가능하다.
            con.setAutoCommit(false);
            // 비즈니스 로직 시작
            bizLogic(con, toId, money, fromId);
            // 정상 수행 시 커밋
            con.commit();
        } catch (Exception e) {
            // 실패시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

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

    private void bizLogic(Connection con, String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        // 검증에 문제 발생 시 아래로 넘어가지 않고 에러를 발생시킨다.
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
