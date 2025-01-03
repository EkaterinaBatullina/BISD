package org.example.web.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.controller.CreateProjectController;
import org.example.service.ProjectService;
import org.example.service.TaskService;
import org.example.service.UserService;

@WebListener
public class ServiceListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ServiceListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        UserService userService = new UserService();
        ProjectService projectService = new ProjectService();
        TaskService taskService = new TaskService();
        context.setAttribute("userService", userService);
        context.setAttribute("projectService", projectService);
        context.setAttribute("taskService", taskService);
        userService.initialize(context);
        projectService.initialize(context);
        taskService.initialize(context);
        if (userService == null) {
            logger.error("UserService is null after initialization.");
        }
        if (projectService == null) {
            logger.error("ProjectService is null after initialization.");
        }
        if (taskService == null) {
            logger.error("TaskService is null after initialization.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        ProjectService projectService = (ProjectService) context.getAttribute("projectService");
        if (projectService != null){
            projectService.destroy();
        }
        context.removeAttribute("userService");
        context.removeAttribute("projectService");
        context.removeAttribute("taskService");
    }
}
