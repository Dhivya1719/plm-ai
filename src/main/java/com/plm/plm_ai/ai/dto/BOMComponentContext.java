package com.plm.plm_ai.ai.dto;

public class BOMComponentContext {

    private Long bomLineId;

    private Long childRevisionId;

    private String childRevisionCode;

    private Integer quantity;

    private Long childItemId;

    private String childItemNumber;

    private String childItemName;

    private String childItemType;

    public BOMComponentContext() {
    }

    public Long getBomLineId() {
        return bomLineId;
    }

    public void setBomLineId(Long bomLineId) {
        this.bomLineId = bomLineId;
    }

    public Long getChildRevisionId() {
        return childRevisionId;
    }

    public void setChildRevisionId(Long childRevisionId) {
        this.childRevisionId = childRevisionId;
    }

    public String getChildRevisionCode() {
        return childRevisionCode;
    }

    public void setChildRevisionCode(String childRevisionCode) {
        this.childRevisionCode = childRevisionCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getChildItemId() {
        return childItemId;
    }

    public void setChildItemId(Long childItemId) {
        this.childItemId = childItemId;
    }

    public String getChildItemNumber() {
        return childItemNumber;
    }

    public void setChildItemNumber(String childItemNumber) {
        this.childItemNumber = childItemNumber;
    }

    public String getChildItemName() {
        return childItemName;
    }

    public void setChildItemName(String childItemName) {
        this.childItemName = childItemName;
    }

    public String getChildItemType() {
        return childItemType;
    }

    public void setChildItemType(String childItemType) {
        this.childItemType = childItemType;
    }
}