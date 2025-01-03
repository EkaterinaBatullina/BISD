package org.example.controller;

import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.User;
import org.example.service.TaskService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/updateTaskStatus")
public class UpdateTaskStatusController extends BaseController {
    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        super.init();
        taskService = (TaskService) getServletContext().getAttribute("taskService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doPost method");
        Map<String, Object> model = createModel(request);
        try {
            String taskIdStr = request.getParameter("taskId");
            String statusStr = request.getParameter("newStatus");
            if (taskIdStr == null || statusStr == null) {
                logger.error("Missing taskId or newStatus parameters");
                model.put("errorMessage", "Missing taskId or newStatus parameters");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("tasks.ftl", model, response);
                return;
            }
            taskIdStr = taskIdStr.replaceAll("\\u00A0+", "");
            int taskId = Integer.parseInt(taskIdStr);
            TaskStatus status = TaskStatus.valueOf(statusStr);
            Task task = taskService.getTById(taskId);
            User currentUser = (User) request.getSession().getAttribute("user");
            if (task.getAssignedTo().getId() == currentUser.getId()) {
                taskService.updateStatus(taskId, status);
                logger.info("Task status updated for taskId: {} to status: {}", taskId, status);
                response.sendRedirect("tasks?projectId=" + task.getProject().getId());
            } else {
                logger.warn("User {} is not authorized to update taskId: {}", currentUser.getUsername(), taskId);
                model.put("errorMessage", "You are not authorized to update this task.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                renderTemplate("tasks.ftl", model, response);
            }
        } catch (Exception e) {
            logger.error("Error updating task status", e);
            model.put("errorMessage", "An error occurred while updating the task status.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("tasks.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        }
    }
}

