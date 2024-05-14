package com.aemstore.core.servlets;

import com.adobe.granite.crypto.CryptoSupport;
import com.aemstore.core.models.CustomLoginService;
import com.google.gson.Gson;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/v1login",
                ServletResolverConstants.SLING_SERVLET_NAME + "=Login Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST
        }
)
public class CustomLogInServlet extends SlingAllMethodsServlet {

    @Reference
    private CustomLoginService loginService;


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4502");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Read JSON payload from the request
        Gson gson = new Gson();
        UserLoginPayload payload = gson.fromJson(request.getReader(), UserLoginPayload.class);


        String email = payload.getEmail();
        String password = payload.getPassword();

        try {

            if (loginService.checkUser(email , password)){

                // Get the session object
                HttpSession session = request.getSession();
                session.setAttribute("username", "First AEMStore User");
                session.setAttribute("userID", "XXXID");

                //response.sendRedirect("http://localhost:4502/content/aemstore/language-masters/home.html?wcmmode=disabled")
                // response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
                response.setStatus(SlingHttpServletResponse.SC_OK);
            }
            else {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error Login: " + e.getMessage());
        }
    }

    private static class UserLoginPayload {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}
