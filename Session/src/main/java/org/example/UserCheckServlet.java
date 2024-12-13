package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.dao.UserDAO;
import org.example.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

@WebServlet("/usercheck")
public class UserCheckServlet extends HttpServlet {

    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String SECRET_KEY_NAME = "SECRET_KEY";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String login = request.getParameter(LOGIN_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);

        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
        UserDAO userDAO = new UserDAO(connection);

        Map<UUID, Long> userSessions = (Map<UUID, Long>) getServletContext()
                .getAttribute("USER_SESSIONS");

        try {
            User user = userDAO.getUserByLogin(login);
            if (user != null && user.password().equals(password)) {

                UUID sessionId = UUID.randomUUID();
                Long timestamp = System.currentTimeMillis();
                // Время последней активности

                // Добавляем сессию в карту сессий
                userSessions.put(sessionId, timestamp);

                // Создаем сессию для пользователя
                HttpSession session = request.getSession(true);
                session.setAttribute(SECRET_KEY_NAME, sessionId.toString());

                response.sendRedirect("index.ftl");

            } else {
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
