package com.aryagami.data;

public class Product {

    public String ProductName;
    public String ProductCategory;
    public  float ProductPrice;
    public int  ProductImage;
    public String PriceCurrency;
    public Long ProductListingId;
    public int  CartQuantity=0;

    public Product(String productName, String productCategory, float productPrice, int productImage, String priceCurrency, Long productListingId) {
        ProductName = productName;
        ProductCategory= productCategory;
        ProductPrice = productPrice;
        ProductImage = productImage;
        PriceCurrency = priceCurrency;
        ProductListingId = productListingId;
    }
}
