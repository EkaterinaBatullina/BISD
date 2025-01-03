package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/")
public class IndexServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(IndexServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Entering doGet method");
        String contextPath = request.getContextPath();
        logger.info("Context path: " + contextPath);
        request.setAttribute("contextPath", contextPath);
        logger.info("Forwarding request to index.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }
}
