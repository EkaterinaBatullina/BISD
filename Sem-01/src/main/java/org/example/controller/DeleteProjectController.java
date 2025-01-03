package org.example.controller;

import freemarker.template.TemplateException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.ProjectService;

import java.io.IOException;
import java.util.Map;
@WebServlet("/deleteProject")
public class DeleteProjectController extends BaseController {
    private ProjectService projectService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        projectService = (ProjectService) context.getAttribute("projectService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Entering doPost method");
        Map<String, Object> model = createModel(request);
        try {
            String projectIdStr = request.getParameter("projectId");
            if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
                logger.error("Project ID parameter is missing");
                model.put("errorMessage", "Project ID is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("projects.ftl", model, response);
                return;
            }
            int projectId = Integer.parseInt(projectIdStr.trim().replaceAll("\\u00A0+", ""));
            projectService.delete(projectId);
            logger.info("Successfully deleted project with ID: {}", projectId);
            response.sendRedirect("projects");
        } catch (Exception e) {
            logger.error("Unexpected error deleting project", e);
            model.put("errorMessage", "An unexpected error occurred while deleting the project. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("projects.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doPost method");
        }
    }

}
