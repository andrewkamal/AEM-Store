package com.testsite.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.testsite.core.models.EmailService;
import org.apache.sling.api.SlingHttpServletResponse;
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
                "process.label" + " = Test Custom Workflow Process",
                Constants.SERVICE_VENDOR + "=testsite",
                Constants.SERVICE_DESCRIPTION + " = Custom test workflow step"
        }
)
public class TestCustomWorkflow implements WorkflowProcess {
    // JDBC connection parameters
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/aemDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String INSERT_QUERY = "INSERT INTO userData (userKey, keyData) VALUES (?, ?)";
    private static final Logger log = LoggerFactory.getLogger(TestCustomWorkflow.class);
    @Reference
    private DataSourcePool dataSourceService;
    @Reference
    private EmailService emailService;
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        log.info("\n ==============================================================");
        try {
            log.info("\n ============== TRY ==========");
            Connection con = ((DataSource) dataSourceService.getDataSource("mysqlDB")).getConnection();
            log.info("\n ============== CONNECTED!!! ==========");
//            // Create statement for INSERT query
//            Statement insertStmt = con.createStatement();
//            // Execute INSERT query
//            int rowsAffected = insertStmt.executeUpdate("INSERT INTO userData (userKey, keyData) VALUES ('Test2', 'Test2')");
//            // Check if the INSERT was successful
//            if (rowsAffected > 0) {
//                log.info("\n INSERT successful. Rows affected: " + rowsAffected);
//            } else {
//                log.info("\n INSERT failed. No rows affected.");
//            }
//            // Create statement for SELECT query
//            Statement selectStmt = con.createStatement();
//            // Execute SELECT query
//            ResultSet rs = selectStmt.executeQuery("SELECT * FROM userData");
//            // Process results
//            while (rs.next()) {
//                log.info("\n id: " + rs.getInt(1) + " key: " + rs.getString(2) + " data: " + rs.getString(3));
//            }
//
//            // Close resources
//            rs.close();
//            insertStmt.close();
//            selectStmt.close();
//            con.close();
//        }catch(Exception e){
//            log.info("\n ============== CATCH ==========");
//            log.error("\n An error occurred while executing the workflow process", e);
//        }
            WorkflowData workflowData = workItem.getWorkflowData();
            if (workflowData.getPayloadType().equals("JCR_PATH")) {
                Session session = workflowSession.adaptTo(Session.class);
                String path = workflowData.getPayload().toString();
                Node node = (Node) session.getItem(path);
                String[] processArgs = processArguments.get("PROCESS_ARGS", "string").toString().split(",");
                MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
                for (String wfArgs : processArgs) {
                    String[] args = wfArgs.split("=");
                    String prop = args[0];
                    String value = args[1];
                    if (node != null) {
                        wfd.put(prop, value);
                    }
                }
                // Posting to the database
                Set<String> keyset = wfd.keySet();

                String email = wfd.get("email").toString();
                String message = wfd.get("message").toString();
                postToDB(email,message,con);
                log.info("Email ->> " + email + "Message ->> " + message);
                try {
                    // Send email using EmailService
                    emailService.sendEmail(email, "Contact Form Submission", message);
                } catch (Exception e) {
                    log.error("\n An error occurred while sending the mail", e);
                }
//                for (String key : keyset) {
//                    log.info("\n ITEM Key - {}, value - {}", key, wfd.get(key));
//                    String keyVal = wfd.get(key).toString();
//                    postToDB(key, keyVal, con);
//                }
            }
            con.close();
            log.info("\n ================================= Task Completed Successfully ================================");
        } catch (Exception e) {
            log.error("\n An error occurred while executing the workflow process", e);
        }
    }

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
