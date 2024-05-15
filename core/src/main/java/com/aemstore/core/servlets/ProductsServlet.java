package com.aemstore.core.servlets;

import com.aemstore.core.models.ProductDetails;
import com.aemstore.core.models.ProductsService;
import com.aemstore.core.models.impl.ProductsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/products",
                ServletResolverConstants.SLING_SERVLET_NAME + "=products Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET
        }
)
public class ProductsServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(ProductsServlet.class);

    @Reference
    private ProductsService productsService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        // Convert each object to JSON
        JsonArray jsonArray = new JsonArray();
        try {

            log.info("\n=========== PRODUCTS ===========");
            List<ProductDetails> productsList = productsService.getAllProducts(request.getResourceResolver());
            log.info("\n=========== PRODUCTS ARE RETRIEVED ! ===========");
            for (ProductDetails product : productsList ) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("folderName", product.getFolderName());
                jsonObject.addProperty("title", product.getTitle());
                jsonObject.addProperty("description", product.getDescription());
                jsonObject.addProperty("price", product.getPrice());
                jsonObject.addProperty("quantity", product.getQuantity());
                jsonObject.addProperty("imagePath", product.getImagePath());
                // Add more properties as needed
                jsonArray.add(jsonObject);
            }

            response.setStatus(SlingHttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.info("\n=========== ERROR ===========");
            log.info("\n ===========" + e.getMessage());
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // Write JSON array to response
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonArray);
        out.flush();
        response.setStatus(SlingHttpServletResponse.SC_OK);
    }


}
