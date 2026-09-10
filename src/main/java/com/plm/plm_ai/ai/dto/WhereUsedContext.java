package com.plm.plm_ai.ai.dto;

public class WhereUsedContext {

    private Long bomLineId;

    private Long parentRevisionId;
    private String parentRevisionCode;

    private Integer quantity;

    public WhereUsedContext() {
    }

    public Long getBomLineId() {
        return bomLineId;
    }

    public void setBomLineId(Long bomLineId) {
        this.bomLineId = bomLineId;
    }

    public Long getParentRevisionId() {
        return parentRevisionId;
    }

    public void setParentRevisionId(Long parentRevisionId) {
        this.parentRevisionId = parentRevisionId;
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