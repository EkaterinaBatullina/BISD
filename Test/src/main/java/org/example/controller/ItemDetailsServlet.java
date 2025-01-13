package org.example.controller;

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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/item/*")
public class ItemDetailsServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ItemDetailsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InventoryItemService service = new InventoryItemService();
        try {
            String pathInfo = request.getPathInfo();
            logger.debug("Received pathInfo: {}", pathInfo); // для отладки

            if (pathInfo == null || pathInfo.isEmpty()) {
                logger.warn("Invalid request path: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int itemId;
            try {
                String itemIdStr = pathInfo.substring(1);
                itemIdStr = itemIdStr.replaceAll("\\u00A0+", "");
                itemId = Integer.parseInt(itemIdStr);
            } catch (NumberFormatException e) {
                logger.error("Invalid item ID: {}", pathInfo.substring(1), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            logger.info("Processing request for item ID: {}", itemId);

            // Получение элемента из базы
            InventoryItem item = service.findById(itemId);
            if (item == null) {
                logger.info("Item not found for ID: {}", itemId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("item", item);
            response.setContentType("text/html;charset=UTF-8");

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setDirectoryForTemplateLoading(new File(getServletContext().getRealPath("/WEB-INF/classes/template")));
            Template template = cfg.getTemplate("details.ftl");

            Writer out = response.getWriter();
            template.process(data, out);

        } catch (SQLException | TemplateException e) {
            logger.error("Error processing request", e);
            throw new ServletException("Ошибка при обработке запроса", e);
        }
    }
}
