package com.aemstore.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.aemstore.core.models.EmailService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Iterator;
import java.util.Set;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label" + " = Order Mail Workflow Process",
                Constants.SERVICE_VENDOR + "=aemstore",
                Constants.SERVICE_DESCRIPTION + " = Order Mail Workflow step"
        }
)
public class OrderMailWorkflow implements WorkflowProcess {
    private static final String INSERT_QUERY = "INSERT INTO userOrder (clientEmail, sellerEmail, orderItem, quantity, price) VALUES (?, ?, ?, ?, ?)";
    private static final Logger log = LoggerFactory.getLogger(TestCustomWorkflow.class);
    @Reference
    private DataSourcePool dataSourceService;
    @Reference
    private EmailService emailService;
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        log.info("\n ================== Order Mail Workflow ===========================");
        try {
            log.info("\n ============== TRY ==========");
            Connection con = ((DataSource) dataSourceService.getDataSource("mysqlDB")).getConnection();
            log.info("\n ============== CONNECTED!!! ==========");

            WorkflowData workflowData = workItem.getWorkflowData();
            if (workflowData.getPayloadType().equals("JCR_PATH")) {
                Session session = workflowSession.adaptTo(Session.class);
                String path = workflowData.getPayload().toString();
                MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

                // Posting to the database
                String clientEmail = wfd.get("clientEmail").toString();
                String clientMessage = wfd.get("clientMessage").toString();
                String sellerEmail = wfd.get("sellerEmail").toString();
                String sellerMessage = wfd.get("sellerMessage").toString();
                String productItem = wfd.get("productItem").toString();
                String price = wfd.get("price").toString();
                String quantity = wfd.get("quantity").toString();
                postToDB(clientEmail,clientMessage, productItem, quantity, price, con);
                try {
                    // Send email to client
                    emailService.sendEmail(clientEmail, "Thank you for Ordering from AEM-Store!", clientMessage);
                    // Send email to product owner
                    emailService.sendEmail(sellerEmail, "New Order Placed in AEM-Store", sellerMessage);
                } catch (Exception e) {
                    log.error("\n An error occurred while sending the mail", e);
                }
            }
            con.close();
            log.info("\n ================================= Task Completed Successfully ================================");
        } catch (Exception e) {
            log.error("\n An error occurred while executing the workflow process", e);
        }
    }
    private void postToDB(String clientEmail, String sellerEmail, String product, String quantity, String price, Connection conn) {
        try {
            PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
            stmt.setString(1, clientEmail);
            stmt.setString(2, sellerEmail);
            stmt.setString(3, product);
            stmt.setString(4, quantity);
            stmt.setString(5, price);
            int rowsAffected = stmt.executeUpdate();
            log.info("\n {} row(s) affected", rowsAffected);
        } catch (SQLException e) {
            log.error("\n Error posting data to database", e);
        }
    }
}