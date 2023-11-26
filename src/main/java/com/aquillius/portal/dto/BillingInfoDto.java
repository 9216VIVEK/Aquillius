package com.aquillius.portal.dto;

import com.aquillius.portal.entity.BillingInfo;
import lombok.Data;


public class BillingInfoDto{

    String fullName;
       String contactNumber;

        String addressLine1;

        String addressLine2;

        String billingEmail;

        public BillingInfoDto(){

        }

    public BillingInfoDto(BillingInfo billingInfo) {
        this.fullName=billingInfo.getFullName();
        this.contactNumber= billingInfo.getContactNumber();
        this.addressLine1=billingInfo.getAddressLine1();
        this.addressLine2=billingInfo.getAddressLine2();
        this.billingEmail=billingInfo.getBillingEmail();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }
}
