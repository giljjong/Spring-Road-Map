package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {
    // 단순화, 직접 만듬, 프레임워크에 종속적
    ModelView process(Map<String, String> paramMap);
}
