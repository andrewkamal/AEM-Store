package com.aemstore.core.models.impl;

import com.aemstore.core.models.ProductDetails;
import com.aemstore.core.models.ProductsService;
import com.aemstore.core.util.Constants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component(service = ProductsService.class, immediate = true)
public class ProductsServiceImpl implements ProductsService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductsServiceImpl.class);

    @Reference
    QueryBuilder queryBuilder;

    public List<ProductDetails> getAllProducts(ResourceResolver resourceResolver){

        List<ProductDetails> products = new ArrayList<>();

        Map<String,String> queryMap = new HashMap<>();
        queryMap.put("path", Constants.PRODUCTS_PATH);
        queryMap.put("type","sling:OrderedFolder");

        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
        SearchResult result = query.getResult();
        for (com.day.cq.search.result.Hit hit : result.getHits()) {
            try {
                Node node = hit.getNode();
                // Get properties of the folder node
                String folderName = node.getName();
                String title = node.getProperty("title").getString();
                String description = node.getProperty("description").getString();
                Double price = node.getProperty("price").getDouble();
                int quantity = (int) node.getProperty("quantity").getLong();
                String sellerEmail = node.getProperty("selleremail").getString();
                products.add(new ProductDetails(folderName, title , description, price, quantity, sellerEmail));

            } catch (RepositoryException e) {
                LOG.info("\n=========== ERROR ===========");
                LOG.info("\n ==========="+e.getMessage() );
            }
        }
        return products;
    }
    public void modifyProduct(String folderName, String quantity, ResourceResolver resourceResolver) {
        LOG.info("\n========================== Modify Products ================================");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", Constants.PRODUCTS_PATH);
        queryMap.put("type", "sling:OrderedFolder");

        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
        SearchResult result = query.getResult();
        LOG.info("\n Will Enter the FOR LOOP");
        for (com.day.cq.search.result.Hit hit : result.getHits()) {
            try {
                LOG.info("\n ============= TRY ============");
                Node node = hit.getNode();
                if (Objects.equals(folderName, node.getName())) {
                    LOG.info("\n==========Product Found==========");
                    int oldQuantity = (int) node.getProperty("quantity").getLong();
                    int newQuantity = oldQuantity - parseInt(quantity);
                    node.setProperty("quantity", newQuantity);
                    LOG.info("\n=========== Done ===========");
                    LOG.info("" + newQuantity);
                }

            } catch (RepositoryException e) {
                LOG.info("\n=========== ERROR ===========");
                LOG.info("\n ===========" + e.getMessage());
            }
        }
    }
}
