package com.aemstore.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.sql.DataSource;
import java.sql.*;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label" + " = Add Product Workflow Process",
                Constants.SERVICE_VENDOR + "=aemstore",
                Constants.SERVICE_DESCRIPTION + " = Add Product Workflow step"
        }
)
public class AddProductWorkflow implements WorkflowProcess {
    private static final String INSERT_QUERY = "INSERT INTO userData (userKey, keyData) VALUES (?, ?)";
    private static final Logger log = LoggerFactory.getLogger(TestCustomWorkflow.class);
    @Reference
    private DataSourcePool dataSourceService;
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        log.info("\n ================== Add Product Workflow ===========================");
        try {
            log.info("\n ============== TRY ==========");
            Connection con = ((DataSource) dataSourceService.getDataSource("mysqlDB")).getConnection();
            log.info("\n ============== CONNECTED!!! ==========");

            WorkflowData workflowData = workItem.getWorkflowData();
            if (workflowData.getPayloadType().equals("JCR_PATH")) {
                Session session = workflowSession.adaptTo(Session.class);
                String path = workflowData.getPayload().toString();
                Node node = (Node) session.getItem(path);

                MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
                String title = wfd.get("title", "string");
                log.info("\n Title= " + title);
                String imagePath = wfd.get("imagePath", "string");
                log.info("\n Image= " + imagePath);
                String folderName = wfd.get("folderName", "string");
                log.info("\n Folder= " + folderName);
                double price = Double.parseDouble(wfd.get("price", "0.0"));
                log.info("\n Price= " + price);
                String description = wfd.get("description", "string");
                log.info("\n Description= " + description);
                int quantity = Integer.parseInt(wfd.get("quantity", "0"));
                log.info("\n Quantity= " + quantity);
                String sellerEmail = wfd.get("sellerEmail", "string");
                log.info("\n Seller Email= " + sellerEmail);

                Node imageFolderNode = session.getNode(folderName);
                // Save product data as properties of the folder
                imageFolderNode.setProperty("title", title);
                imageFolderNode.setProperty("price", price);
                imageFolderNode.setProperty("description", description);
                imageFolderNode.setProperty("quantity", quantity);
                imageFolderNode.setProperty("imagepath", imagePath);
                imageFolderNode.setProperty("selleremail", sellerEmail);
                imageFolderNode.addMixin("mix:versionable"); // Add mixin for versioning
                session.save();

                // Save session
                session.save();
            }
            log.info("\n ================================= Task Completed Successfully ================================");
        } catch (Exception e) {
            log.error("\n An error occurred while executing the workflow process", e);
        }
    }
    // Configured in case we want to save anything to the DB
    private void postToDB(String key, String info, Connection conn) {
        try {
            PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
            stmt.setString(1, key); // Set the first parameter (userKey)
            stmt.setString(2, info); // Set the second parameter (keyValue)
            int rowsAffected = stmt.executeUpdate();
            log.info("\n {} row(s) affected", rowsAffected);
        } catch (SQLException e) {
            log.error("\n Error posting data to database", e);
        }
    }
}
