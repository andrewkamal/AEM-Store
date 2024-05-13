package com.aemstore.core.models;

import java.util.List;
import java.util.Map;

public interface Product {

    String getGridClass();
    boolean getIsVisible();

    List<Map<String, String>> getProductDetails();
}
