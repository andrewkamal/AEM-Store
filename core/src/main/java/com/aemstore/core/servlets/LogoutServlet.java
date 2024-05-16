package com.aemstore.core.servlets;

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
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/v1logout",
                ServletResolverConstants.SLING_SERVLET_NAME + "=Logout Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET
        }
)
public class LogoutServlet extends SlingAllMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        try {

            HttpSession session = request.getSession();
            log.info("\n====== Session Data =========");
            log.info("\n====== email: " + session.getAttribute(Constants.ID_SESSION_KEY));
            session.setAttribute(Constants.ID_SESSION_KEY, null);
            session.setAttribute(Constants.FIRST_NAME_SESSION_KEY, null);
            session.setAttribute(Constants.LAST_NAME_SESSION_KEY, null);
            session.setAttribute(Constants.EMAIL_SESSION_KEY, null);

            log.info("\n====== Session Data after clear =========");
            if (session.getAttribute(Constants.ID_SESSION_KEY) == null){
                log.info("\n====== email: it is null");
            }else {
                log.info("\n====== email not null: "+ session.getAttribute(Constants.ID_SESSION_KEY));
            }

            response.setStatus(SlingHttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error Logout: " + e.getMessage());
        }
    }
}
