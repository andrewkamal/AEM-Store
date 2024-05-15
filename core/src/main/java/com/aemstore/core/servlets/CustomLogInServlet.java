package com.aemstore.core.servlets;

import com.adobe.granite.crypto.CryptoSupport;
import com.aemstore.core.models.CustomLoginService;
import com.aemstore.core.models.User;
import com.aemstore.core.models.UserService;
import com.aemstore.core.util.Constants;
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
    private UserService userService;


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
            User user = userService.doLogin(email , password);
            if (user.getID() == -1){
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            }
            else {

                HttpSession session = request.getSession();

                session.setAttribute(Constants.ID_SESSION_KEY, user.getID());
                session.setAttribute(Constants.FIRST_NAME_SESSION_KEY, user.getFirstname());
                session.setAttribute(Constants.LAST_NAME_SESSION_KEY, user.getLastname());
                session.setAttribute(Constants.EMAIL_SESSION_KEY, user.getEmail());

                response.setStatus(SlingHttpServletResponse.SC_OK);
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
