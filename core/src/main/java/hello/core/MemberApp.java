package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args) {
        // AppConfig를 통해서 객체 주입이 된다.
        // AppConfig appConfig = new AppConfig();
        // MemberService memberService =  appConfig.memberService();

        // Spring은 모든게 ApplicationContext에서 시작. 이것이 스프링 컨테이너
        // AppConfig.Class를 넣으면 AppConfig 내부의 설정 정보를 받아서 컨테이너에 등록한다.
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        // 사용자 1의 정보를 넣는다.
        Member member = new Member(1L, "memberA", Grade.VIP);
        // 사용자를 생성한다.
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("member = " + member.getName());
        System.out.println("findMember = " + findMember.getName());
    }

}
