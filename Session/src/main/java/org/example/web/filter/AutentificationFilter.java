package org.example.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebFilter("/*")
public class AutentificationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (httpServletRequest.getServletPath().startsWith("/static/") ||
                httpServletRequest.getServletPath().equals("/login") ||
                httpServletRequest.getServletPath().equals("/usercheck")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            String secretKey = (String) session.getAttribute("SECRET_KEY");
            if (secretKey != null) {
                Map<UUID, Long> userSessions = (Map<UUID, Long>) httpServletRequest.getServletContext()
                        .getAttribute("USER_SESSIONS");
                UUID sessionId = UUID.fromString(secretKey);

                if (userSessions.containsKey(sessionId)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        httpServletResponse.sendRedirect("/login");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
