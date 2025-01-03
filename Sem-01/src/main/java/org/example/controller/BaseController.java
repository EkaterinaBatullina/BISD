package org.example.controller;

import jakarta.servlet.http.HttpServlet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController extends HttpServlet {
    protected Configuration cfg;
    protected static final Logger logger = LogManager.getLogger(BaseController.class);

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        try {
            String templatePath = context.getRealPath("/WEB-INF/classes/template");
            cfg.setDirectoryForTemplateLoading(new File(templatePath));
            cfg.setDefaultEncoding("UTF-8");
        } catch (IOException e) {
            logger.error("Error initializing FreeMarker template directory", e);
        }
    }

    protected Map<String, Object> createModel(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("contextPath", request.getContextPath());
        return model;
    }

    protected void renderTemplate(String templateName, Map<String, Object> model, HttpServletResponse response) throws IOException, TemplateException {
        try (Writer out = response.getWriter()) {
            Template template = cfg.getTemplate(templateName);
            template.process(model, out);
        } catch (TemplateException e) {
            model.put("errorMessage", "An internal error occurred while processing the template.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (Writer out = response.getWriter()) {
                Template template = cfg.getTemplate(templateName);
                template.process(model, out);
            }
        } catch (IOException e) {
            model.put("errorMessage", "An internal server error occurred while rendering the page.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (Writer out = response.getWriter()) {
                Template template = cfg.getTemplate(templateName);
                template.process(model, out);
            }
        }
    }
}


