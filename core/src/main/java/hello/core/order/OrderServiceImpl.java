package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository;
    /*
        OrderServiceImpl 내부 소스도 변경하게 되면서 구현체 클래스에도 의존하고 있는게 증명되었다.
        OrderServiceImpl이 DiscountPolicy를 의존하는 것 처럼 보이지만 FixDiscountPolicy와 RateDiscountPolicy를 함께 의존하고 있다.
        - DIP를 위반한다.
        그리고 OrderServiceImpl를 변경하는 것 자체도 OCP를 위반하는 행위이다.
     */
    // 1. private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // 2. private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    private DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        // 할인과 사용자에 대한 구조가 분리되어있기 때문에 단일체계 설계를 잘 지켜서 만들어짐.
        // OrderService는 할인정책을 알 필요 없이 결과만 받으면 된다.
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
