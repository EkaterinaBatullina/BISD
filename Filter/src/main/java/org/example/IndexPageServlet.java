package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/index")
public class IndexPageServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try{
            String username = (String) request.getSession(false)
                    .getAttribute("user");

            System.out.println(username);

            request.setAttribute("username", username);

            request.getRequestDispatcher("index.ftl").forward(request, response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

}
