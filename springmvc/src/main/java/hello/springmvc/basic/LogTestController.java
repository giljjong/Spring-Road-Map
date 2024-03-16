package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @Slf4j는 Logger의 선언을 대신한다.
@Slf4j
// RestController 사용 시 해당 문자열이 바로 반환된다. RestAPI의 Rest이다.
@RestController
public class LogTestController {

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        log.trace("trace log={}", name);
        /*
            log.trace("trace log=", name);로 사용 시
            사용되지 않음에도 미리 연산을 진행하여 문자를 가지고 있는 상태를 유지하므로
            리소스의 낭비가 생긴다.
        */
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        return "ok";
    }

}
