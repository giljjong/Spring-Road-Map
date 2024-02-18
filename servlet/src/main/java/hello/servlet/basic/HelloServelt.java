package hello.servlet.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServelt extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // servlet이 호출되면 이 service가 호출된다.
        // super.service(req, resp);

        System.out.println("HelloServelt.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        // request에 들어오는 parameter를 읽어준다.
        String username = request.getParameter("username");
        System.out.println("username = " + username);

        // HTTP 헤더 정보에 들어간다.
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        // HTTP 메시지 바디에 데이터가 들어간다.
        response.getWriter().write("hello " + username);
    }
}
