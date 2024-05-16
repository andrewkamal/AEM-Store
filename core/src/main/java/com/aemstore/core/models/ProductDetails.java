package com.aemstore.core.models;

import com.aemstore.core.util.Constants;

public class ProductDetails {
    private String folderName ;
    private String title;
    private String description;
    private Double price;
    private int quantity;
    private String imagePath;
    private Double adjustedPrice = -1.0;
    private String sellerEmail;
    public ProductDetails(String folderName, String title, String description, Double price, int quantity, String sellerEmail) {
        this.folderName = folderName;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = String.format("%s/%s/image.jpg", Constants.PRODUCTS_PATH, folderName);
        this.sellerEmail = sellerEmail;
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

    public Double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Double getAdjustedPrice() { return adjustedPrice; }

    public void setAdjustedPrice(Double adjustedPrice) { this.adjustedPrice = adjustedPrice; }

    public String getSellerEmail() { return sellerEmail; };
}
