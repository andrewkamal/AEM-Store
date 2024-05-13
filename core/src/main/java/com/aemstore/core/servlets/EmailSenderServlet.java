package com.testsite.core.servlets;

import com.google.gson.Gson;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.servlets.ServletResolverConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import java.io.IOException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.testsite.core.models.EmailService;
import org.apache.sling.api.servlets.ServletResolverConstants;

@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/emailsender",
                ServletResolverConstants.SLING_SERVLET_NAME + "=Email Sender Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST
        }
)
public class EmailSenderServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private EmailService emailService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4502");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Read JSON payload from the request
        Gson gson = new Gson();
        EmailPayload payload = gson.fromJson(request.getReader(), EmailPayload.class);

        // Extract email and message from payload
        String email = payload.getEmail();
        String message = payload.getMessage();

        try {
            // Send email using EmailService
            emailService.sendEmail(email, "Contact Form Submission", message);
            response.setStatus(SlingHttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error sending email: " + e.getMessage());
        }
    }

    private static class EmailPayload {
        private String email;
        private String message;

        public String getEmail() {
            return email;
        }

        public String getMessage() {
            return message;
        }
    }
}
