package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();

    // 이 구조는 중간에 오류가 날 경우 마지막(삭제)까지 수행되지 않기 때문에 좋지 않은 로직이다.
    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV5", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member != findMEmber {}", member == findMember);
        // lombok의 @Data를 사용하면 해당 객체의 모든 필드를 사용해서 equals 메소드를 생성해준다. 고로 사용이 가능하다.
        log.info("member != findMEmber {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // update : money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }

}