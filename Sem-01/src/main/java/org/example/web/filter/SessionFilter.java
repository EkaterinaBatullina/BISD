package org.example.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (httpServletRequest.getServletPath().startsWith("/static/") ||
                httpServletRequest.getServletPath().startsWith("/register") ||
                httpServletRequest.getServletPath().startsWith("/") ||
                httpServletRequest.getServletPath().startsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                filterChain.doFilter(request, response);
            } else {
                request.getRequestDispatcher("/").forward(request, response);
            }
    }

}

