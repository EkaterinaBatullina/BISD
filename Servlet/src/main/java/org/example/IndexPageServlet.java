    package org.example;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    
    import java.io.IOException;

    @WebServlet("")
    //обработка корневого пути
    public class IndexPageServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String param1 = request.getParameter("param1");
            String param2 = request.getParameter("param2");

            request.setAttribute("param1", param1);
            request.setAttribute("param2", param2);
            //устанавливаем атрибуты для их использования в шаблоне

            request.getRequestDispatcher("/template/index.thtml").forward(request, response);
            //перенаправление с подстановкой
        }

        @Override
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
