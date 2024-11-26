package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.User;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("")
public class IndexPageServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        try (Writer writer = response.getWriter()) {

            Connection connection = DBConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(resultSet.getLong("id"), resultSet.getString("name")));
            }

            request.setAttribute("users", users);
            request.setAttribute("title_page", "Пользователи БД");

            resultSet.close();
            preparedStatement.close();

            request.getRequestDispatcher("index.ftl").forward(request, response);

        } catch (SQLException | IOException | ServletException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка обработки запроса", e);
        }
    }
}
