package com.kumuluz.ee.product;


public class Order {


    private String id;
    private String description;
    private String[] itemIds;
    private int[] quantities;
    private String categoryId;
    private String discountId;

    public Order() {
    }


    public Order(String id, String description, String[] itemIds, int[] quantities, String categoryId, String discountId) {
        this.id = id;
        this.description = description;
        this.itemIds = itemIds;
        this.quantities = quantities;
        this.categoryId = categoryId;
        this.discountId = discountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getItemIds() {
        return itemIds;
    }

    public void setItemIds(String[] itemIds) {
        this.itemIds = itemIds;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public void setQuantities(int[] quantities) {
        this.quantities = quantities;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getDescription() { return description;  }

    public void setDescription(String description) { this.description = description; }
}
