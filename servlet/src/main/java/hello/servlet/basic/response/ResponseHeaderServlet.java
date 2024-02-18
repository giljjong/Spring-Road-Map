package hello.servlet.basic.response;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // HTTP 응답 코드를 넣는다.
        // 직접 200 적는것 보다 매직넘버인 상수를 넣어주는게 좋다.
        // [status-line]
        response.setStatus(HttpServletResponse.SC_OK);
        // response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // [response-headers]
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        // 캐쉬 관련 사용하지 않음 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        // 커스텀 헤더
        response.setHeader("my-header", "hello");

        // [Header 편의 메서드]
       //  content(response);
        // cookie(response);
        redirect(response);

        PrintWriter writer = response.getWriter();
        writer.println("ok");
        // charset=utf-8 설정으로 응답에 한글이 사용 가능하다.
        writer.println("안녕하세요.");
    }

    private void content(HttpServletResponse response) {
        //Content-Type: text/plain;charset=utf-8
        //Content-Length: 2
//        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        // 이렇게 설정하면 위와 동일한 기능을 하며 자동으로 헤더에 넣어준다.
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
//        response.setContentLength(2); //(생략시 자동 생성)
    }

    private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600;
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); //600초
        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html

        // HTTP 기본 강의에서 배운 대로 302로 보낼 경우 redirect 시킨다.
        // response.setStatus(HttpServletResponse.SC_FOUND); //302
        // response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }
}
