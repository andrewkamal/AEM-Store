package com.aemstore.core.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;

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

        // Extract all data from payload
        String clientEmail = req.getParameter("clientEmail");
        log.info("\n Client Email= " + clientEmail);
        String sellerEmail = req.getParameter("sellerEmail");
        log.info("\n Seller Email= " + sellerEmail);
        String productItem = req.getParameter("productItem");
        log.info("\n Product Item= " + productItem);
        double price = Double.parseDouble(req.getParameter("price"));
        log.info("\n Price= " + price);
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        log.info("\n Quantity= " + quantity);
        String clientMessage = "Your order for the item " + req.getParameter("title") + "has been placed and will be delivered soon. \nTotal price: " + price + "EGP. \nPayment Method: COD. \nQuantity: " + quantity;
        log.info("\n Client Message= " + clientMessage);
        String sellerMessage = "An order for the item " + req.getParameter("title") + "has been placed by " +clientEmail + ". \nTotal price: " + price + "EGP. \nPayment Method: COD. \nQuantity: " + quantity;
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
                workflowData.getMetaDataMap().put("productItem", productItem);
                workflowData.getMetaDataMap().put("quantity", String.valueOf(quantity));
                workflowData.getMetaDataMap().put("price", String.valueOf(price));
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
}