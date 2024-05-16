package com.aemstore.core.models;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface ProductsService {
    List<ProductDetails> getAllProducts(ResourceResolver resourceResolver);
}
