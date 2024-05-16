package com.aemstore.core.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Execute Order Mail Servlet",
                "sling.servlet.methods=" + "POST",
                "sling.servlet.paths=" + "/bin/ordermail"
        })
@SlingServletPaths(
        value = {"/bin/ordermail", "/aemstore/ordermail"} //check and update
)

public class OrderMailServlet extends SlingAllMethodsServlet {
    private static Logger log = LoggerFactory.getLogger(ProductCreationServlet.class);

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse res) throws ServletException, IOException{

        log.info("\n ======================================= Order Mail Servlet =============================");
        // Read JSON payload from the request
        Gson gson = new Gson();
        OrderMailServlet.UserOrderPayload Opayload = gson.fromJson(req.getReader(), OrderMailServlet.UserOrderPayload.class);
        // Extract all data from payload
        String clientEmail = Opayload.getClientEmail();
        log.info("\n Client Email= " + clientEmail);
        String sellerEmail = Opayload.getSellerEmail();
        log.info("\n Seller Email= " + sellerEmail);
        String folderName = Opayload.getFolderName();
        log.info("\n Folder Name= " + folderName);
        String title = Opayload.getTitle();
        log.info("\n Title= " + title);
        String price = Opayload.getPrice();
        log.info("\n Price= " + price);
        String quantity = Opayload.getQuantity();
        log.info("\n Quantity= " + quantity);
        String clientMessage = "Your order for the item " + title + "has been placed and will be delivered soon. \nTotal price: " + price + "EGP. \nPayment Method: COD. \nQuantity: " + quantity;
        log.info("\n Client Message= " + clientMessage);
        String sellerMessage = "An order for the item " + title + "has been placed by " +clientEmail + ". \nTotal price: " + price + "EGP. \nPayment Method: COD. \nQuantity: " + quantity;
        log.info("\n Seller Message= " + sellerMessage);
        String status = "Workflow Executing";
        final ResourceResolver resourceResolver = req.getResourceResolver();
        String payload = req.getRequestParameter("page").getString();
        try {
            if (StringUtils.isNotBlank(payload)) {
                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
                WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/ordermail");
                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payload);
                workflowData.getMetaDataMap().put("clientEmail", clientEmail);
                workflowData.getMetaDataMap().put("sellerEmail", sellerEmail);
                workflowData.getMetaDataMap().put("folderName", folderName);
                workflowData.getMetaDataMap().put("title", title);
                workflowData.getMetaDataMap().put("quantity", quantity);
                workflowData.getMetaDataMap().put("price", price);
                workflowData.getMetaDataMap().put("clientMessage", clientMessage);
                workflowData.getMetaDataMap().put("sellerMessage", sellerMessage);
                log.info("\n ...... Added all metadata items .....");
                status = workflowSession.startWorkflow(workflowModel, workflowData).getState();
            }
        } catch (Exception e) {
            log.error("\n Error in Workflow {}", e.getMessage());
        }
        res.setContentType("application/json");
        res.getWriter().write(status);
    }
    private static class UserOrderPayload {
        private String clientEmail;
        private String sellerEmail;
        private String folderName;
        private String title;
        private String price;
        private String quantity;
        public String getClientEmail() {
            return clientEmail;
        }
        public String getSellerEmail() {
            return sellerEmail;
        }
        public String getFolderName() {
            return folderName;
        }
        public String getTitle() {return title;}
        public String getPrice() {
            return price;
        }
        public String getQuantity() {
            return quantity;
        }
    }
}