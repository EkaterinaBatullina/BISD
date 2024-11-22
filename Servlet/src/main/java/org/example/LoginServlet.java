package org.example;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        if (name != null && !name.isEmpty() && password != null && !password.isEmpty()) {
            response.sendRedirect("/Servlet/welcome.thtml?name=" + name + "&message=Welcome%20" + name);
        } else {
            response.sendRedirect("/Servlet/index.thtml?param1=Error&param2=Invalid%20name%20or%20password");
        }
    }

}
