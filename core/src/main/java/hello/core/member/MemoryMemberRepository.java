package hello.core.member;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemoryMemberRepository implements MemberRepository{
    // 현업에서 동시성 문제가 있을 수 있기 때문에 conquer hashmap를 사용해야 한다.
    private static Map<Long, Member> store = new HashMap<Long, Member>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
