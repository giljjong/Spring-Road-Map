package hello.itemservice;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    /**
     * 확인용 초기 데이터 추가
     * 애플리케이션을 실행할 때 초기 데이터를 저장한다.
     * 이것이 없으면 서버를 실행할때마다 데이터를 입력해야 된다.
     *
     * @EventListener(ApplicationReadyEvent.class) : 스프링 컨테이너가 완전히 초기화를 끝내고 실행 준비가 되었을 때 발생하는 이벤트이다.
     * 스프링이 이 시점에 해당 애노테이션이 붙은 initData() 메서드를 호출해준다.
     *
     * 이 기능 대신 @PostConstruct를 사용할 경우 AOP같은 부분이 아직 다 처리되지 않은 상태로 호출될 수 있다.
     * EventListener 는 AOP를 포함한 스프링 컨테이너가 완전히 초기화 된 이후에 호출되기 때문에 이런 문제가 발생하지 않는다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}