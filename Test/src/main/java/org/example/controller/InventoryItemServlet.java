package org.example.controller;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.InventoryItem;
import org.example.service.InventoryItemService;

@WebServlet("/item")
public class InventoryItemServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(InventoryItemServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InventoryItemService service = new InventoryItemService();
        try {
            List<InventoryItem> items = service.findAll();
            Map<String, Object> data = Map.of("items", items);
            response.setContentType("text/html;charset=UTF-8");

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setDirectoryForTemplateLoading(new File(getServletContext().getRealPath("/resources/template")));
            Template template = cfg.getTemplate("index.ftl");
            Writer out = response.getWriter();
            template.process(data, out);
        } catch (SQLException | TemplateException e) {
            throw new ServletException("Ошибка при обработке запроса", e);
        }
    }
}