package com.aemstore.core.models.impl;

import com.aemstore.core.models.Product;
import com.aemstore.core.models.ProductDetails;
import com.aemstore.core.models.ProductsService;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Product.class,
        resourceType = ProductImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@JsonRootName("products")
public class ProductImpl implements Product {
    protected static final String RESOURCE_TYPE = "aemstore/components/custom/productsGrid";

    private static final Logger LOG = LoggerFactory.getLogger(ProductImpl.class);

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ProductsService productsService;


    @ValueMapValue
    @Via("resource")
    private boolean visible;

    @ValueMapValue
    @Via("resource")
    private String gridClass;

    @ValueMapValue
    @Via("resource")
    private long discounts;

    @ValueMapValue
    @Via("resource")
    private long taxes;

    @Override
    public String getGridClass() {
        return "product-grid-vertical";
    }

    @Override
    public boolean getIsVisible() {
        return visible;
    }

    public long getDiscounts() {
        return discounts;
    }

    public long getTaxes() {
        return taxes;
    }

    @Override
    public List<ProductDetails> getProductDetails() {
        LOG.info("Calling ProductsService to get all products...");
        List<ProductDetails> products = productsService.getAllProducts(request.getResourceResolver());
        LOG.info("ProductsService returned {} products", products.size());

        // Calculate and set the adjusted price for each product
        for (ProductDetails product : products) {
            double adjustedPrice = getAdjustedPrice(product.getPrice());
            product.setAdjustedPrice(adjustedPrice);
            LOG.debug("Product: {}, Adjusted Price: {}", product.getTitle(), adjustedPrice);
        }

        return products;
    }
    public double getAdjustedPrice(double price) {
        double discountAmount = price * discounts / 100;
        double taxAmount = price * taxes / 100;
        double adjustedPrice = price - discountAmount + taxAmount;

        // Format adjusted price to two decimal places
        DecimalFormat df = new DecimalFormat("#.00");
        adjustedPrice = Double.parseDouble(df.format(adjustedPrice));

        return adjustedPrice;
    }
}
