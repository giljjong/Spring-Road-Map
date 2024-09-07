package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

// @Import 앞서 설정한 MemoryConfig를 설정 파일로 사용한다.
// scanBasePackages -> 여기서는 컨트롤러만 컴포넌트 스캔을 하고 나머지는 직접 수동 등록한다.
// 지정하지 않으면 모든 패키지에서 컴포넌트 스캔 한다.
@Import(MemoryConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	// Profile("local") 특정 프로필의 경우에만 해당 스프링 빈을 등록한다. properteis 등
	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

}
