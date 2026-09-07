package com.plm.plm_ai.item.dto;

public class WhereUsedResponse {

    private Long parentRevisionId;
    private String parentItemNumber;
    private String parentItemName;
    private String parentRevisionCode;
    private Integer quantity;

    public WhereUsedResponse() {
    }

    public WhereUsedResponse(
            Long parentRevisionId,
            String parentItemNumber,
            String parentItemName,
            String parentRevisionCode,
            Integer quantity) {

        this.parentRevisionId = parentRevisionId;
        this.parentItemNumber = parentItemNumber;
        this.parentItemName = parentItemName;
        this.parentRevisionCode = parentRevisionCode;
        this.quantity = quantity;
    }

    public Long getParentRevisionId() {
        return parentRevisionId;
    }

    public void setParentRevisionId(Long parentRevisionId) {
        this.parentRevisionId = parentRevisionId;
    }

    public String getParentItemNumber() {
        return parentItemNumber;
    }

    public void setParentItemNumber(String parentItemNumber) {
        this.parentItemNumber = parentItemNumber;
    }

    public String getParentItemName() {
        return parentItemName;
    }

    public void setParentItemName(String parentItemName) {
        this.parentItemName = parentItemName;
    }

    public String getParentRevisionCode() {
        return parentRevisionCode;
    }

    public void setParentRevisionCode(String parentRevisionCode) {
        this.parentRevisionCode = parentRevisionCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}