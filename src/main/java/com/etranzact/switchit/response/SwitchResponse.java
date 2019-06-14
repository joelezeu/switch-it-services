package com.etranzact.switchit.response;


/**
 * @author joel.eze
 */
public class SwitchResponse {

    private String reference;
    private Double amount;
    private String totalFailed;
    private String totalSuccess;
    private String error;
    private String message;
    private String otherReference;
    private String action;
    private String openingBalance;
    private String closingBalance;

    public SwitchResponse(String reference, Double amount, String totalFailed, String totalSuccess, String error, String message, String otherReference, String action, String openingBalance, String closingBalance) {
        this.reference = reference;
        this.amount = amount;
        this.totalFailed = totalFailed;
        this.totalSuccess = totalSuccess;
        this.error = error;
        this.message = message;
        this.otherReference = otherReference;
        this.action = action;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
    }


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(String totalFailed) {
        this.totalFailed = totalFailed;
    }

    public String getTotalSuccess() {
        return totalSuccess;
    }

    public void setTotalSuccess(String totalSuccess) {
        this.totalSuccess = totalSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherReference() {
        return otherReference;
    }

    public void setOtherReference(String otherReference) {
        this.otherReference = otherReference;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Override
    public String toString() {
        return "SwitchResponse{" + "reference=" + reference + ", amount=" + amount + ", totalFailed=" + totalFailed + ", totalSuccess=" + totalSuccess + ", error=" + error + ", message=" + message + ", otherReference=" + otherReference + ", action=" + action + ", openingBalance=" + openingBalance + ", closingBalance=" + closingBalance + '}';
    }

}
