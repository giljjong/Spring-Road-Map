package hello.springtx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    // REQUIRES_NEW를 사용함으로 물리 트랜잭션을 분리한다.
    // 에러가 발생하더라도 여기서 에러 처리 및 롤백이 되고 MemberService는 정상 작동 한다.
    // 또한 이를 사용할 때 MemberService의 db connection이 대기상태인 채로 신규 db connection을 사용하기 때문에
    // 성능이 중요한 곳에서는 이를 주의해서 사용해야 한다.
    // REQUIRES_NEW를 사용하지 않고 문제를 해결할 수 있다면 이전 강의와 같이 구조를 변경하여 따로 빼 사용하는 것이 좋다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage) {
        log.info("log 저장");
        em.persist(logMessage);

        if(logMessage.getMessage().contains("로그예외")) {
            log.info("log 저장시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }

    public Optional<Log> find(String message) {
        return em.createQuery("select l from Log l where l.message = :message", Log.class)
                .setParameter("message", message)
                // 만약 결과가 2개가 나와도 둘 중 하나만 찾아서 반환해준다.
                .getResultList().stream().findAny();
    }
}
