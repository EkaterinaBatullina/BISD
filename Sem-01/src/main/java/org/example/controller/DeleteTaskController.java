package org.example.controller;

import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.ProjectService;
import org.example.service.TaskService;
import org.example.service.UserService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/deleteTask")
public class DeleteTaskController extends BaseController {
    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        super.init();
        taskService = (TaskService) getServletContext().getAttribute("taskService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> model = createModel(request);
        try {
            int taskId = getIntId(request,"taskId");
            int projectId = getIntId(request, "projectId");
            logger.info("Request to delete task with ID: {} for project ID: {}", taskId, projectId);
            taskService.deleteById(taskId);
            logger.info("Successfully deleted task with ID: {}", taskId);
            response.sendRedirect("tasks?projectId=" + projectId);

        } catch (NumberFormatException e) {
            logger.warn("Invalid task or project ID", e);
            model.put("errorMessage", "Invalid task ID or project ID.");
            try {
                renderTemplate("tasks.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        }
    }

    private int getIntId(HttpServletRequest request, String paramName) throws NumberFormatException {
        String IdStr = request.getParameter(paramName);
        return Integer.parseInt(IdStr.replaceAll("\\u00A0+", ""));
    }
}
