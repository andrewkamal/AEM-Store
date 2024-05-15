package com.aemstore.core.servlets;

import com.google.gson.Gson;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import com.aemstore.core.models.User;
import com.aemstore.core.models.UserService;
@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/register",
                ServletResolverConstants.SLING_SERVLET_NAME + "=Registration Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST
        }
)
public class RegisterServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterServlet.class);

    @Reference
    private UserService userService;
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Processing registration request...");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read JSON payload from the request
        Gson gson = new Gson();
        User data = gson.fromJson(request.getReader(), User.class);

        // Log received data
        LOGGER.info("Received registration data: Firstname={}, Lastname={}, Email={}, Password={}",
                data.getFirstname(), data.getLastname(), data.getEmail(), data.getPassword());

        // Check if the user already exists
        if (userService.userExists(data.getEmail())) {
            response.setStatus(SlingHttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"error\":\"User already exists\"}");
            return;
        }

        // Create a new user
        if (userService.createUser(data)) {
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Registration successful\"}");
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error creating user\"}");
        }
    }



}
