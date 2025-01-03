package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(LogoutController.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("User logout initiated");
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
            logger.info("User session invalidated");
        }
        response.sendRedirect("/WEB-INF/jsp/index.jsp");
        logger.info("User redirected to index.jsp after logout");
    }
}

