package hello.exception.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 요청 온 사용자 구분
        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}][{}]", uuid, request.getDispatcherType(), requestURI);
            // chain을 해주지 않으면 바로 필터가 끝나게 된다.
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.info("EXCEPTION! {}", e.getMessage());
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}