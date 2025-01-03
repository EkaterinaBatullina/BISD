package org.example.controller;

import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.User;
import org.example.service.ProjectService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/updateProject")
public class UpdateProjectController extends BaseController {
    private ProjectService projectService;

    @Override
    public void init() throws ServletException {
        super.init();
        projectService = (ProjectService) getServletContext().getAttribute("projectService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Entering doGet method");
        Map<String, Object> model = createModel(request); // Создаем модель
        try {
            String projectIdStr = request.getParameter("projectId");
            if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
                logger.error("Project ID parameter is missing");
                model.put("errorMessage", "Project ID is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("updateProject.ftl", model, response);
                return;
            }
            int projectId = getIntId(request, "projectId");
            Project project = projectService.getById(projectId); // Получаем проект по ID
            if (project == null) {
                logger.error("Project with ID {} not found", projectId);
                model.put("errorMessage", "Project not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("updateProject.ftl", model, response);
                return;
            }
            model.put("project", project);
            response.setContentType("text/html; charset=UTF-8");
            renderTemplate("updateProject.ftl", model, response);

        } catch (Exception e) {
            logger.error("Error displaying update project page", e);
            model.put("errorMessage", "An unexpected error occurred while displaying the page. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("updateProject.ftl", model, response);
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
            String projectIdStr = request.getParameter("projectId");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String isPrivateStr = request.getParameter("isPrivate");
            if (projectIdStr == null || projectIdStr.trim().isEmpty() || name == null || description == null) {
                logger.error("Project ID, name, or description is missing");
                model.put("errorMessage", "Project ID, name, or description is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("update.ftl", model, response);
                return;
            }
            boolean isPrivate = "on".equals(isPrivateStr);
            int projectId = getIntId(request, "projectId");
            Project project = projectService.getById(projectId);
            if (project == null) {
                logger.error("Project with ID {} not found", projectId);
                model.put("errorMessage", "Project not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("updateProject.ftl", model, response);
                return;
            }
            project.setName(name);
            project.setDescription(description);
            project.setPrivate(isPrivate);
            projectService.update(project);
            response.sendRedirect("/Sem-01/projects");
        } catch (Exception e) {
            logger.error("Error updating project", e);
            model.put("errorMessage", "An error occurred while updating the project.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("updateProject.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doPost method");
        }
    }

    private int getIntId(HttpServletRequest request, String paramName) throws NumberFormatException {
        String IdStr = request.getParameter(paramName);
        return Integer.parseInt(IdStr.replaceAll("\\u00A0+", ""));
    }
}
