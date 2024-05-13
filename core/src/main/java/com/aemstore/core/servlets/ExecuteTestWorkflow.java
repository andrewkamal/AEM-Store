package com.testsite.core.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.google.gson.Gson;
import com.testsite.core.models.EmailService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
property = {
Constants.SERVICE_DESCRIPTION + "=Execute Test Workflow Servlet",
        "sling.servlet.methods=" + "POST",
        "sling.servlet.paths=" + "/bin/executeworkflow"
        })
@SlingServletPaths(
        value = {"/bin/executeworkflow", "/testsite/executeworkflow"} //check and update
)

public class ExecuteTestWorkflow extends SlingAllMethodsServlet {
    private static Logger log = LoggerFactory.getLogger(ExecuteTestWorkflow.class);


    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse res) throws ServletException, IOException{
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:4502");
        res.setHeader("Access-Control-Allow-Methods", "POST");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Read JSON payload from the request
        Gson gson = new Gson();
        ExecuteTestWorkflow.EmailPayload Epayload = gson.fromJson(req.getReader(), ExecuteTestWorkflow.EmailPayload.class);

        // Extract email and message from payload
        String email = Epayload.getEmail();
        String message = Epayload.getMessage();


        String status="Workflow Executing";
        final ResourceResolver resourceResolver = req.getResourceResolver();
        String payload=req.getRequestParameter("page").getString();
        try{
            if(StringUtils.isNotBlank(payload)) {
                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
                WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/test");
                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payload);
                workflowData.getMetaDataMap().put("email", email);
                workflowData.getMetaDataMap().put("message", message);
                status = workflowSession.startWorkflow(workflowModel,workflowData).getState();
            }
        } catch (Exception e){
            log.error("\n Error in Workflow {}", e.getMessage());
        }
        res.setContentType("application/json");
        res.getWriter().write(status);
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


