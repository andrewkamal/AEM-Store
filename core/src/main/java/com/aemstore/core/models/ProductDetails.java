package com.aemstore.core.models;

import com.aemstore.core.util.Constants;

public class ProductDetails {
    private String folderName ;
    private String title;
    private String description;
    private String price;
    private String quantity;
    private String imagePath;

    public ProductDetails(String folderName, String title, String description, String price, String quantity) {
        this.folderName = folderName;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = String.format("%s/%s/image.jpg", Constants.PRODUCTS_PATH, folderName);
    }

    public String getFolderName() {
        return folderName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }
}
