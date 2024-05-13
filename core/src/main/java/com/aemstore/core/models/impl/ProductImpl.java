package com.aemstore.core.models.impl;


import com.aemstore.core.models.Product;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

import javax.inject.Inject;
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
    final protected static String RESOURCE_TYPE = "/aemstore/components/custom/productsGrid/";

    @Inject
    @Via("resource")
    private boolean visible;

    @Inject
    @Via("resource")
    private boolean gridClass;

    public String getGridClass(){
        return "product-grid-vertical";
    }
    @Override
    public boolean getIsVisible() {
        return visible;
    }


    @Override
    public List<Map<String, String>> getProductDetails() {
        List<Map<String, String>> bookDetailsMap = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            Map<String, String> bookMap = new HashMap<>();
            bookMap.put("productName", "Product" + i);
            bookMap.put("productDescription", "Product Description" + i);
            bookDetailsMap.add(bookMap);
        }
        return bookDetailsMap;
    }

}
