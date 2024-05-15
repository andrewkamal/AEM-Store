package com.aemstore.core.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.dam.api.AssetManager;

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
import javax.servlet.http.Part;
import javax.jcr.Node;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Execute Add Product Servlet",
                "sling.servlet.methods=" + "POST",
                "sling.servlet.paths=" + "/bin/addproduct"
        })
@SlingServletPaths(
        value = {"/bin/addproduct", "/aemstore/addproduct"} //check and update
)

public class ProductCreationServlet extends SlingAllMethodsServlet {
        private static Logger log = LoggerFactory.getLogger(ProductCreationServlet.class);

        @Override
        protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse res) throws ServletException, IOException{

                log.info("\n ======================================= Product Creation Servlet =============================");

                // Extract all data from payload
                String title = req.getParameter("title");
                log.info("\n Title= " + title);
                String description = req.getParameter("description");
                log.info("\n Description= " + description);
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                log.info("\n Quantity= " + quantity);
                double price = Double.parseDouble(req.getParameter("price"));
                log.info("\n Price= " + price);
                String folderName = null;
                // Handle the image file
                Part imagePart = req.getPart("image");
                String imagePath = null;
                try (InputStream imageStream = imagePart.getInputStream()) {
                        if (imageStream != null) {
                                log.info("\n Size of Image= " + imagePart.getSize());
                                log.info("\n Name of Image= " + imagePart.getSubmittedFileName());

                                // Save image to DAM
                                AssetManager assetManager = req.getResourceResolver().adaptTo(AssetManager.class);
                                if (assetManager != null && imageStream != null) {
                                        folderName = "/content/dam/aemstore/products/" + title + "_" + UUID.randomUUID().toString();
                                        try {
                                                imagePath = assetManager.createAsset(folderName + "/image.jpg", imageStream, "image/jpeg", true).getPath();
                                                log.info("\n Image saved to DAM: " + imagePath);

                                                // Get the session and node for versioning
                                                Session session = req.getResourceResolver().adaptTo(Session.class);
                                                Node imageNode = session.getNode(imagePath);

                                                // Add versioning to the image node
                                                if (!imageNode.isNodeType("mix:versionable")) {
                                                        imageNode.addMixin("mix:versionable");
                                                        session.save();
                                                }
                                        } catch (Exception e) {
                                                log.error("\n Error saving image to DAM", e);
                                                // Handle the error appropriately, maybe return an error response
                                        }
                                } else {
                                        log.error("\n AssetManager is null or imageStream is null, cannot save image to DAM.");
                                }
                        }
                } catch (IOException e) {
                        log.error("Error processing image input stream", e);
                }

                String status = "Workflow Executing";
                final ResourceResolver resourceResolver = req.getResourceResolver();
                String payload = req.getRequestParameter("page").getString();
                try {
                        if (StringUtils.isNotBlank(payload)) {
                                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
                                WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/addproduct");
                                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payload);
                                workflowData.getMetaDataMap().put("title", title);
                                workflowData.getMetaDataMap().put("imagePath", imagePath);
                                workflowData.getMetaDataMap().put("folderName", folderName);
                                workflowData.getMetaDataMap().put("description", description);
                                workflowData.getMetaDataMap().put("quantity", String.valueOf(quantity));
                                workflowData.getMetaDataMap().put("price", String.valueOf(price));
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