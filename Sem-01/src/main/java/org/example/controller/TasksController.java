package org.example.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.User;
import org.example.service.ProjectService;
import org.example.service.TaskService;
import org.example.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/tasks")
public class TasksController extends BaseController {
    private ProjectService projectService;
    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        projectService = (ProjectService) context.getAttribute("projectService");
        taskService = (TaskService) context.getAttribute("taskService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doGet method");
        Map<String, Object> model = createModel(request);
        try {
            String projectIdStr = request.getParameter("projectId");
            if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
                logger.error("Project ID parameter is missing");
                model.put("errorMessage", "Project ID is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("tasks.ftl", model, response);
                return;
            }
            int projectId = Integer.parseInt(projectIdStr.trim().replaceAll("\\u00A0+", ""));
            Project project = projectService.getById(projectId);
            if (project == null) {
                logger.error("Project with ID {} not found", projectId);
                model.put("errorMessage", "Project not found");
                model.put("projectId", projectId);
                model.put("projectName", "");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("tasks.ftl", model, response);
                return;
            }
            String projectName = project.getName();
            model.put("projectId", projectId);
            model.put("projectName", projectName);
            List<Task> tasks = taskService.getByProject(projectId);
            logger.info("{} tasks retrieved for projectId: {}", tasks.size(), projectId);
            model.put("tasks", tasks);
            model.put("currentUser", (User) request.getSession().getAttribute("user"));
            model.put("contextPath", request.getContextPath());
            response.setContentType("text/html; charset=UTF-8");
            renderTemplate("tasks.ftl", model, response);
        } catch (NumberFormatException | TemplateException e) {
            logger.error("Invalid projectId parameter", e);
            model.put("errorMessage", "Invalid projectId");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                renderTemplate("tasks.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doPost method");
        Map<String, Object> model = createModel(request);
        try {
            String taskIdStr = request.getParameter("taskId");
            String statusStr = request.getParameter("status");
            if (taskIdStr == null || statusStr == null) {
                logger.error("Missing taskId or status parameters");
                model.put("errorMessage", "Missing taskId or status parameters");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("tasks.ftl", model, response);
                return;
            }
            int taskId = Integer.parseInt(taskIdStr);
            logger.info("Updating task with taskId: {}", taskId);
            User currentUser = (User) request.getSession().getAttribute("user");
            Task task = taskService.getTById(taskId);
            if (task == null) {
                logger.warn("Task not found for taskId: {}", taskId);
                model.put("errorMessage", "Task not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("tasks.ftl", model, response);
                return;
            }
            if (task.getAssignedTo().getId() == currentUser.getId()) {
                taskService.updateStatus(taskId, TaskStatus.valueOf(statusStr));
                logger.info("Task status updated for taskId: {}", taskId);
                response.sendRedirect("tasks?projectId=" + task.getProject().getId());
            } else {
                logger.warn("User {} is not authorized to update taskId: {}", currentUser.getUsername(), taskId);
                model.put("errorMessage", "You are not authorized to update this task.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                renderTemplate("tasks.ftl", model, response);
            }
        } catch (NumberFormatException | TemplateException e) {
            logger.error("Invalid taskId or status parameter", e);
            model.put("errorMessage", "Invalid taskId or status");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                renderTemplate("tasks.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        }
    }
}
