package hello.core.scope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean02.class);
        PrototypeBean02 prototypeBean1 = ac.getBean(PrototypeBean02.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean02 prototypeBean2 = ac.getBean(PrototypeBean02.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
        System.out.println("find prototypeBean1");
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean02.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        // 1에서 생성한 빈이 유지되어 공유하기 때문에 2가 반환될것
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBean {
        // private final PrototypeBean02 prototypeBean; // 생성시점에 주입

        // ObjectFactory로 바꿔도 동작한다.
        @Autowired
        //private ObjectProvider<PrototypeBean02> prototypeBeanProvider;
        // javax.inject의 Provider를 사용해야 한다.
        private Provider<PrototypeBean02> prototypeBeanProvider;
        /*
        @Autowired
        public ClientBean(PrototypeBean02 prototypeBean) {
            this.prototypeBean = prototypeBean;
        }
        */

        public int logic() {
            // 스프링 빈 호출 시 찾아서 제공한다.
             PrototypeBean02 prototypeBean = prototypeBeanProvider.get();
             prototypeBean.addCount();
             int count = prototypeBean.getCount();
            return count;
        }

    }

    @Scope("prototype")
    static class PrototypeBean02 {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
