package org.example.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

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

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            Optional<Cookie> secretKeyCookie = Arrays.stream(cookies)
                    .filter(cookie -> "SECRET_KEY".equals(cookie.getName()))
                    .findFirst();

            if (secretKeyCookie.isPresent()) {
                String secretKey = secretKeyCookie.get().getValue();
                // Проверить в базе данных или в памяти, соответствует ли secretKey авторизованному пользователю
                // Если соответствует, пропускаем запрос
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Если кука не найдена или неверная, перенаправляем на страницу логина
        httpServletResponse.sendRedirect("/login");
    }
}
