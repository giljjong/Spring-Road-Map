package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {

    // 의존 관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제가 있다.
    // MemberRepository memberRepository는 인터페이스를 의존 하지만 실제 할당하는 new MemoryMemberRepository()가 구현체를 의존한다.
    private final MemberRepository memberRepository;

    // 자동으로 의존관계 주입을 해준다.
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        // 생성자 주입으로 받음으로써 memberRepository의 할당(역할 부여, 구현체)에 관여하지 않게 되었다.
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        // 다형성에 의해서 memoryMemberRepository의 save가 호출된다.
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
