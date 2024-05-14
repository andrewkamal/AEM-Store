package com.aemstore.core.servlets;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/sessiontest",
                ServletResolverConstants.SLING_SERVLET_NAME + "=session storage Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET
        }
)
public class HomeServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(CustomLogInServlet.class);

    @Override
    protected void doGet( SlingHttpServletRequest request,  SlingHttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        JSONObject jsonObject = new JSONObject();

//        // Create ObjectMapper
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Convert Java object to JSON string
//        String jsonString = mapper.writeValueAsString(obj);

        try {

            // Store data in the session
            String username = (String) session.getAttribute("username");
            jsonObject.put("status", "success");
            jsonObject.put("username", username);


            log.info("\n ============== SESSION DATA  ==========");
            log.info("\n ============== username: " + username);

        }catch (Exception e){
            response.getWriter().write("Error Login: " + e.getMessage());
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonObject.toString());
        response.setStatus(SlingHttpServletResponse.SC_OK);

    }



}
