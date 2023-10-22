package hello.core.member;

public class MemberServiceImpl implements MemberService {

    // 의존 관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제가 있다.
    // MemberRepository memberRepository는 인터페이스를 의존 하지만 실제 할당하는 new MemoryMemberRepository()가 구현체를 의존한다.
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(Member member) {
        // 다형성에 의해서 memoryMemberRepository의 save가 호출된다.
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
