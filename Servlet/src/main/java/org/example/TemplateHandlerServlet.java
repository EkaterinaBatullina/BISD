package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@WebServlet("*.thtml")
public class TemplateHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String templateName = request.getRequestURI()
                .substring(request
                        .getRequestURI()
                        .lastIndexOf("/") + 1);
        String templatePath = "/template/" + templateName;
        //получаем из запроса запрашиваемый шаблон
        //через последнее вхождение /
        //со следующего символа берем подстроку - название шаблона

        InputStream inputStream = getServletContext()
                .getResourceAsStream("/WEB-INF/classes" + templatePath);
        //получаем объект ServletContext с информацией о развернутом приложении
        //предоставляем доступ к ресурсам доступным приложению
        //получаем ресурс в виде потока данных - в виде байтовой последовательности

        if (inputStream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Template not found");
            return;
        }
        //отправляем инт статус код 404 с сообщением о ненайденном шаблоне

        try {
            String templateContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            //шаблон считываем в строку

            for (String paramName : Collections.list(request.getParameterNames())) {
                String paramValue = request.getParameter(paramName);
                templateContent = templateContent.replace("${" + paramName + "}", paramValue);
            }
            //имена параметров(а не их значения) получаем в виде перечисления Enumeration<String>
            //в нашем случае param1 и param2
            //преобразуем перечисление в список
            //через имя получаем значение и заменяем

            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(templateContent);
            //отправляем обновленный шаблон

        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing template");
            //отправляем статусный код 500 - ошибку сервера
        }
    }

}