package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.UserDAO;
import org.example.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

@WebServlet("/usercheck")
public class UserCheckServlet extends HttpServlet {

    // Параметры для получения логина и пароля
    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String SECRET_KEY_NAME = "SECRET_KEY";
    private static final String FIXED_SECRET_KEY = "23131234134134";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String login = request.getParameter(LOGIN_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);

        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
        UserDAO userDAO = new UserDAO(connection);

        Map<UUID, Long> authentificationData = (Map<UUID, Long>) getServletContext()
                .getAttribute("AUTH_DATA");

        try {
            User user = userDAO.getUserByLogin(login);
            if (user != null && user.password().equals(password)) {


                UUID userId = UUID.randomUUID();
                Long timestamp = System.currentTimeMillis();
                // Время последней авторизации (можно использовать для дополнительных целей)

                // Добавляем данные о пользователе в структуру авторизации
                authentificationData.put(userId, timestamp);

                Cookie cookie = new Cookie(SECRET_KEY_NAME, FIXED_SECRET_KEY);
                response.addCookie(cookie);

                request.getRequestDispatcher("index.ftl").forward(request, response);
            } else {
                // Если данные неверные, перенаправляем на страницу логина с ошибкой
                request.setAttribute("error", "Неверный логин или пароль");
                request.getRequestDispatcher("login.ftl").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("error", "Ошибка при проверке данных пользователя");
            request.getRequestDispatcher("login.ftl").forward(request, response);
        }
    }
}
