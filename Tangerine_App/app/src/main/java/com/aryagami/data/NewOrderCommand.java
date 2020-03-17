package com.aryagami.data;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class NewOrderCommand implements Serializable, DataModel {

    public ContractCommand contract;
    public String creditScore;
    public Float creditLimit;
    public Float requestedCreditLimit;
    public Float totalValue;
    public Float depositValue;
    public String currentStatus;
    public PaymentCommand paymentInfo;
    public UserRegistration userInfo;
    public Boolean fulfillmentDone;
    public Boolean isPostpaid;
    public Boolean isNewAccount;
    public Float setupPrice;
    public Float totalPlanPrice;
    public String currencyType;
    public String registrationServiceType;
    public ResellerStaff addresellerCommand;


    public String orderNo;
    public String rejectReason;
    public Float requestedRechargeCommission;
    public Float requestedPostPaidSignUpCommission;
    public Float requestedInvoiceCommission;
    public Float requestedSignUpCommission;

    public String resellerCode;
    public List<Long> productListingIds;
    public List<Integer> productListingIdCounts;
    public List<Float> productListingPrice;
    public Float airtimeValue;
    public String ussd;
    public LocationCoordinates resellerLocation;
    public float resellerZoneRadiusKM ;
    public List<ProductListing> productListings;



    public List<SubscriptionCommand> subscriptions;
    public static NewOrderCommand getOnDemandNewOrderCommand() {
        return onDemandNewOrderCommand;
    }

    public static void setOnDemandNewOrderCommand(NewOrderCommand onDemandNewOrderCommand) {
        NewOrderCommand.onDemandNewOrderCommand = onDemandNewOrderCommand;
    }

    public static NewOrderCommand onDemandNewOrderCommand;

    public static Account[] getAccount() {
        return account;
    }

    public static void setAccount(Account[] account) {
        NewOrderCommand.account = account;
    }

    public static Account[] account;

    public static Account getExistingAccountDetails() {
        return existingAccountDetails;
    }

    public static void setExistingAccountDetails(Account existingAccountDetails) {
        NewOrderCommand.existingAccountDetails = existingAccountDetails;
    }

    public static Account existingAccountDetails = null;

    @Override
    public DataType getDataType() {
        return DataType.NewOrderCommand;
    }

    public static class ContractCommand implements Serializable {

        public Long periodNMonths;

        public String additionalTerms;

        public String additionalNotes;

        public String doucmentUrl;

        public String usedTemplate;
    };

    public static class ProductListing implements Serializable {
       public String serialNumber;

       public Long listingId;

       public Float listingPrice;
       public int count;
       public String imei;
       public Float inventoryPrice;
    };

    public static class PaymentCommand implements Serializable {
        public String paymentId;

        public String entityId;

        public Float amountPaid;

        public Date paymentDate;

        public Float creditAmount;

        public String creditDetails;

        public String paymentMethod;

        public String chequeNumber;

        public String receiptNumber;

        public String bankDetail;

        public String chequeIssuer;

        public String chequeDate;

        public String paymentStatus;

        public String entityType;

        public String userId;
    };


    public String getNewOrderJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        if(resellerCode != null){
            jwriter.name("resellerCode").value(resellerCode);
        }

        jwriter.name("productListingIds");
        if(productListingIds != null) {
            getProductListingIdsJSON(productListingIds, jwriter);
        }

        if (registrationServiceType.equals("Postpaid")) {

            if (creditLimit != null) {
                jwriter.name("creditLimit").value(creditLimit);
            }
            jwriter.name("fulfillmentDone").value(fulfillmentDone);
            jwriter.name("registrationServiceType").value(registrationServiceType);

            jwriter.name("subscriptions");
            if (subscriptions != null) {
                try {
                    getSubscriptionJSON(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jwriter.name("contract");
            if (contract != null) {
                getContractJSON(jwriter);
            }

            jwriter.name("resellerLocation");
            if (resellerLocation != null) {
                getLocationCoordinatesJSON(jwriter);
            }
            if (creditScore != null) {
                jwriter.name("creditScore").value(creditScore);
            }

            if (requestedCreditLimit != null) {
                jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
            } else {
                jwriter.name("requestedCreditLimit").value(0);
            }
            if (isNewAccount) {
                jwriter.name("userInfo");
                if (userInfo != null) {
                    try {
                        getUserRegistrationJSON(jwriter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jwriter.name("totalValue").value(totalValue);
            /* jwriter.name("paymentInfo");
            if(paymentInfo != null)
                getPaymentInfoJSON(jwriter);*/
            jwriter.name("currentStatus").value(currentStatus);
            if (depositValue != null) {
                jwriter.name("depositValue").value(depositValue);
            } else {
                jwriter.name("depositValue").value(0);
            }
// for postpaid
            jwriter.name("productListings");
            if(productListings != null) {
                getProductListingsJSON(productListings, jwriter);
            }
        } else {
            if (creditLimit != null) {
                jwriter.name("creditLimit").value(creditLimit);
            } else {
                jwriter.name("creditLimit").value(0);
            }
            jwriter.name("fulfillmentDone").value(fulfillmentDone);
            jwriter.name("registrationServiceType").value(registrationServiceType);

            jwriter.name("subscriptions");
            if (subscriptions != null) {
                try {
                    getSubscriptionJSON(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (creditScore != null) {
                jwriter.name("creditScore").value(creditScore);
            } else {
                jwriter.name("creditScore").value(0);
            }

            jwriter.name("resellerLocation");
            if (resellerLocation != null) {
                getLocationCoordinatesJSON(jwriter);
            }

            jwriter.name("contract");
            if (contract != null) {
                getContractJSON(jwriter);
            }
            if (requestedCreditLimit != null) {
                jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
            } else {
                jwriter.name("requestedCreditLimit").value(0);
            }
            if (isNewAccount) {
                jwriter.name("userInfo");
                if (userInfo != null) {
                    try {
                        getUserRegistrationJSON(jwriter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jwriter.name("totalValue").value(totalValue);

            jwriter.name("paymentInfo");
            if (paymentInfo != null)
                getPaymentInfoJSON(jwriter);
            jwriter.name("currentStatus").value(currentStatus);
            if (depositValue != null) {
                jwriter.name("depositValue").value(depositValue);
            } else {
                jwriter.name("depositValue").value(0);
            }

            jwriter.name("productListings");
            if(productListings != null) {
                getProductListingsJSON(productListings, jwriter);
            }

        }

        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public String getRetailerNewOrderJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        if(resellerCode != null){
            jwriter.name("resellerCode").value(resellerCode);
        }

        jwriter.name("productListingIds");
        if(productListingIds != null) {
            getProductListingIdsJSON(productListingIds, jwriter);
        }

        if (registrationServiceType.equals("Postpaid")) {

            if (creditLimit != null) {
                jwriter.name("creditLimit").value(creditLimit);
            }
            jwriter.name("fulfillmentDone").value(fulfillmentDone);
            jwriter.name("registrationServiceType").value(registrationServiceType);

            jwriter.name("subscriptions");
            if (subscriptions != null) {
                try {
                    getSubscriptionJSON(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jwriter.name("contract");
            if (contract != null) {
                getContractJSON(jwriter);
            }

            jwriter.name("resellerLocation");
            if (resellerLocation != null) {
                getLocationCoordinatesJSON(jwriter);
            }
            if (creditScore != null) {
                jwriter.name("creditScore").value(creditScore);
            }

            if (requestedCreditLimit != null) {
                jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
            } else {
                jwriter.name("requestedCreditLimit").value(0);
            }
            if (isNewAccount) {
                jwriter.name("userInfo");
                if (userInfo != null) {
                    try {
                        getUserRegistrationJSON(jwriter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jwriter.name("totalValue").value(totalValue);
            /* jwriter.name("paymentInfo");
            if(paymentInfo != null)
                getPaymentInfoJSON(jwriter);*/
            jwriter.name("currentStatus").value(currentStatus);
            if (depositValue != null) {
                jwriter.name("depositValue").value(depositValue);
            } else {
                jwriter.name("depositValue").value(0);
            }
// for postpaid
            jwriter.name("productListings");
            if(productListings != null) {
                getProductListingsJSON(productListings, jwriter);
            }
        } else {
            if (creditLimit != null) {
                jwriter.name("creditLimit").value(creditLimit);
            } else {
                jwriter.name("creditLimit").value(0);
            }
            jwriter.name("fulfillmentDone").value(fulfillmentDone);
            jwriter.name("registrationServiceType").value(registrationServiceType);

            jwriter.name("subscriptions");
            if (subscriptions != null) {
                try {
                    getSubscriptionJSON(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (creditScore != null) {
                jwriter.name("creditScore").value(creditScore);
            } else {
                jwriter.name("creditScore").value(0);
            }

            jwriter.name("resellerLocation");
            if (resellerLocation != null) {
                getLocationCoordinatesJSON(jwriter);
            }

            jwriter.name("contract");
            if (contract != null) {
                getContractJSON(jwriter);
            }
            if (requestedCreditLimit != null) {
                jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
            } else {
                jwriter.name("requestedCreditLimit").value(0);
            }
            if (isNewAccount) {
                jwriter.name("userInfo");
                if (userInfo != null) {
                    try {
                        getUserRegistrationJSON(jwriter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jwriter.name("totalValue").value(totalValue);

            jwriter.name("paymentInfo");
            if (paymentInfo != null)
                getPaymentInfoJSON(jwriter);
            jwriter.name("currentStatus").value(currentStatus);
            if (depositValue != null) {
                jwriter.name("depositValue").value(depositValue);
            } else {
                jwriter.name("depositValue").value(0);
            }

            jwriter.name("productListings");
            if(productListings != null) {
                getProductListingsJSON(productListings, jwriter);
            }

            jwriter.name("addresellerCommand");
            if (addresellerCommand != null) {
                try {
                    writeJsonResellerData(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public String getNewOrderForExistingJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();


        if (creditLimit != null) {
            jwriter.name("creditLimit").value(creditLimit);
        } else {
            jwriter.name("creditLimit").value(0);
        }

        jwriter.name("resellerCode").value(resellerCode);
        jwriter.name("subscriptions");
        if (subscriptions != null) {
            try {
                getSubscriptionJSON(jwriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (creditScore != null) {
            jwriter.name("creditScore").value(creditScore);
        } else {
            jwriter.name("creditScore").value(0);
        }
        jwriter.name("fulfillmentDone").value(fulfillmentDone);
        jwriter.name("registrationServiceType").value(registrationServiceType);

        jwriter.name("contract");
        if (contract != null) {
            getContractJSON(jwriter);
        }
        if (requestedCreditLimit != null) {
            jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
        } else {
            jwriter.name("requestedCreditLimit").value(0);
        }

        jwriter.name("userInfo");
        if (userInfo != null) {
            getUserRegistrationExitingJSON(jwriter);
        }
        jwriter.name("resellerLocation");
        if (resellerLocation != null) {
            getLocationCoordinatesJSON(jwriter);
        }
        jwriter.name("totalValue").value(totalValue);

        jwriter.name("paymentInfo");
        if (paymentInfo != null)
            getPaymentInfoJSON(jwriter);
        jwriter.name("currentStatus").value(currentStatus);

        if (depositValue != null) {
            jwriter.name("depositValue").value(depositValue);
        } else {
            jwriter.name("depositValue").value(0);
        }

        if(resellerCode != null){
            jwriter.name("resellerCode").value(resellerCode);
        }

        jwriter.name("productListings");
        if(productListings != null) {
            getProductListingsJSON(productListings, jwriter);
        }

        jwriter.name("productListingIds");
        if(productListingIds != null) {
            getProductListingIdsJSON(productListingIds, jwriter);
        }
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public String postPostpaidExistingOrderJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("subscriptions");
        if (subscriptions != null) {
            try {
                getPostpaidExistingOrderSubscriptionJSON(jwriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jwriter.name("fulfillmentDone").value(fulfillmentDone);
        jwriter.name("resellerCode").value(resellerCode);
        jwriter.name("orderNo").value(orderNo);
        jwriter.name("registrationServiceType").value("Postpaid");

        jwriter.name("totalValue").value(totalValue);

        jwriter.name("paymentInfo");
        if (paymentInfo != null)
            getPaymentInfoJSON(jwriter);

        if(depositValue != null){
            jwriter.name("depositValue").value(depositValue);
        }else{
            jwriter.name("depositValue").value(0);
        }

        jwriter.name("productListings");
        if(productListings != null) {
            getProductListingsJSON(productListings, jwriter);
        }
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public String postDeviceOrderJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();
        jwriter.name("subscriptions");
        if (subscriptions != null) {
            try {
                getPostpaidExistingOrderSubscriptionJSON(jwriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jwriter.name("creditScore").value(creditScore);
        jwriter.name("creditLimit").value(creditLimit);

        jwriter.name("fulfillmentDone").value(fulfillmentDone);
        jwriter.name("orderNo").value(orderNo);

        jwriter.name("registrationServiceType").value("");
        jwriter.name("resellerCode").value(resellerCode);

        jwriter.name("contract");
        if (contract != null) {
            getContractJSON(jwriter);
        }

        jwriter.name("requestedCreditLimit").value(requestedCreditLimit);

        jwriter.name("userInfo");
        if (userInfo != null) {
            getUserInfoForDeviceOrderJSON(jwriter);
        }

        jwriter.name("resellerLocation");
        if (resellerLocation != null) {
            getLocationCoordinatesJSON(jwriter);
        }
        jwriter.name("resellerCode").value(resellerCode);

        jwriter.name("totalValue").value(totalValue);

        jwriter.name("productListings");
        if(productListings != null) {
            getProductListingsJSON(productListings, jwriter);
        }

        jwriter.name("paymentInfo");
        if (paymentInfo != null)
            getPaymentInfoJSON(jwriter);

        jwriter.name("currentStatus").value("");

        jwriter.name("depositValue").value(depositValue);

        jwriter.name("productListingPrice");
        getProductListingPriceJSON(productListingPrice, jwriter);

        jwriter.name("productListingIds");
        getProductListingIdsJSON(productListingIds, jwriter);

        jwriter.name("productListingIdCounts");
        getProductListingIdCountsJSON(productListingIdCounts, jwriter);

        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public void getProductListingPriceJSON(List<Float> productListingPrice, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (Float productPrice : productListingPrice) {
                jwriter.value(productPrice);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProductListingIdsJSON(List<Long> productListingIds, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (Long productListingid : productListingIds) {
                jwriter.value(productListingid);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProductListingIdCountsJSON(List<Integer> productListingIdCounts, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (Integer productListingCount : productListingIdCounts) {
                jwriter.value(productListingCount);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfoForDeviceOrderJSON(JsonWriter jwriter) throws IOException {
        if (userInfo != null) {

            jwriter.beginObject();
            jwriter.name("userId").value(userInfo.userId);
            jwriter.name("accountId").value(userInfo.accountId);
            jwriter.endObject();
        }
    }


    public void getUserRegistrationExitingJSON(JsonWriter jwriter) throws IOException {
        if (userInfo != null) {

            jwriter.beginObject();
            jwriter.name("userId").value(userInfo.userId);
            jwriter.name("roleId").value(userInfo.roleId);
            jwriter.name("roleName").value(userInfo.roleName);
            jwriter.name("billingCycleId").value(1);
            jwriter.name("billingFrequency").value("Monthly");
            jwriter.name("accountId").value(userInfo.accountId);
            jwriter.name("resellerCode").value(resellerCode);
            jwriter.endObject();
        }
    }

    public void getUserRegistrationJSON(JsonWriter jwriter) throws Exception {


        if (userInfo != null) {

            jwriter.beginObject();
            jwriter.name("userName").value(userInfo.userName);
            jwriter.name("fullName").value(userInfo.fullName);
            jwriter.name("surname").value(userInfo.surname);
            jwriter.name("nationalIdentity").value(userInfo.nationalIdentity);
            jwriter.name("identityNumber").value(userInfo.identityNumber);
            jwriter.name("coiNumber").value(userInfo.coiNumber);
            jwriter.name("landLineNumber").value(userInfo.landLineNumber);
            jwriter.name("email").value(userInfo.email);
            jwriter.name("company").value(userInfo.company);
            jwriter.name("phoneNumber").value(userInfo.phoneNumber);
            jwriter.name("password").value(userInfo.password);
            jwriter.name("currency").value(userInfo.currency);
            jwriter.name("faxNumber").value(userInfo.faxNumber);
            jwriter.name("tinNumber").value(userInfo.tinNumber);
            jwriter.name("vatNumber").value(userInfo.vatNumber);
            jwriter.name("primaryPersonName").value(userInfo.primaryPersonName);
            jwriter.name("primaryPersonPhoneNumber").value(userInfo.primaryPersonPhoneNumber);
            jwriter.name("primaryPersonMobileNumber").value(userInfo.primaryPersonMobileNumber);
            jwriter.name("alternatePersonName").value(userInfo.alternatePersonName);
            jwriter.name("alternatePhoneNumber").value(userInfo.alternatePhoneNumber);
            jwriter.name("alternateLandLineNumber").value(userInfo.alternateLandLineNumber);
            jwriter.name("resellerCode").value(userInfo.resellerCode);
            jwriter.name("userGroup").value(userInfo.userGroup);
            jwriter.name("userDoc").value(userInfo.userDoc);
            jwriter.name("registrationType").value(userInfo.registrationType);
            jwriter.name("alternatePersonEmailId").value(userInfo.alternatePersonEmailId);
            jwriter.name("primaryPersonEmailId").value(userInfo.primaryPersonEmailId);
            jwriter.name("nationality").value(userInfo.nationality);
            jwriter.name("refugeeIdentityNumber").value(userInfo.refugeeIdentityNumber);
            jwriter.name("profilePicture").value(userInfo.profilePicture);
            jwriter.name("tempUserToken").value(userInfo.tempUserToken);
            jwriter.name("roleId").value(userInfo.roleId);
            jwriter.name("roleName").value(userInfo.roleName);
            jwriter.name("accountId").value(userInfo.accountId);
            jwriter.name("billingFrequency").value(userInfo.billingFrequency);
            jwriter.name("billingCycleId").value(userInfo.billingCycleId);
            jwriter.name("discountType").value(userInfo.discountType);
            jwriter.name("discountValue").value(userInfo.discountValue);
            jwriter.name("newAccountId").value(userInfo.newAccountId);
            jwriter.name("documentId").value(userInfo.documentId);
            jwriter.name("dob").value(userInfo.dob);
            jwriter.name("addressType").value(userInfo.addressType);
            jwriter.name("doorNumber").value(userInfo.doorNumber);
            jwriter.name("documentsUploadPending").value(userInfo.documentsUploadPending);
            jwriter.name("address").value(userInfo.address);
            jwriter.name("postalAddress").value(userInfo.postalAddress);
            jwriter.name("refugeeIssueDate").value(userInfo.refugeeIssueDate);
            jwriter.name("visaValidityDate").value(userInfo.visaValidityDate);
            jwriter.name("visaValidityType").value(userInfo.visaValidityType);
            jwriter.name("fingerprintVerifed").value(userInfo.fingerprintVerifed);
            jwriter.name("passportValidityDated").value(userInfo.passportValidityDated);
            jwriter.name("userAddressList");
            if (userInfo.userAddressList != null)
                getUserAddressListJSON(userInfo, jwriter);
            jwriter.name("country").value(userInfo.country);
            jwriter.name("city").value(userInfo.city);
            jwriter.name("userDocs");
            if (userInfo.userDocs != null)
                getUserDocsListJSON(userInfo, jwriter);

            jwriter.endObject();
        }
    }

    public void staffUserInfoJSON(JsonWriter jwriter) throws Exception {


        if (userInfo != null) {

            jwriter.beginObject();
            jwriter.name("fullName").value(userInfo.fullName);

            jwriter.name("coiNumber").value(userInfo.coiNumber);
            jwriter.name("landLineNumber").value(userInfo.landLineNumber);
            jwriter.name("email").value(userInfo.email);
            jwriter.name("company").value(userInfo.company);
            jwriter.name("phoneNumber").value(userInfo.phoneNumber);
            jwriter.name("password").value(userInfo.password);

            jwriter.name("currency").value(userInfo.currency);
            jwriter.name("faxNumber").value(userInfo.faxNumber);
            jwriter.name("tinNumber").value(userInfo.tinNumber);
            jwriter.name("vatNumber").value(userInfo.vatNumber);
            jwriter.name("primaryPersonName").value(userInfo.primaryPersonName);
            jwriter.name("primaryPersonPhoneNumber").value(userInfo.primaryPersonPhoneNumber);
            jwriter.name("primaryPersonMobileNumber").value(userInfo.primaryPersonMobileNumber);
            jwriter.name("alternatePersonName").value(userInfo.alternatePersonName);
            jwriter.name("alternatePhoneNumber").value(userInfo.alternatePhoneNumber);
            jwriter.name("alternateLandLineNumber").value(userInfo.alternateLandLineNumber);
            jwriter.name("resellerCode").value(userInfo.resellerCode);

            jwriter.name("userGroup").value(userInfo.userGroup);
            jwriter.name("userDoc").value(userInfo.userDoc);
            jwriter.name("registrationType").value(userInfo.registrationType);
            jwriter.name("alternatePersonEmailId").value(userInfo.alternatePersonEmailId);
            jwriter.name("primaryPersonEmailId").value(userInfo.primaryPersonEmailId);
            jwriter.name("refugeeIdentityNumber").value(userInfo.refugeeIdentityNumber);

            jwriter.name("roleId").value(userInfo.roleId);
            jwriter.name("roleName").value(userInfo.roleName);
            jwriter.name("accountId").value(userInfo.accountId);
            jwriter.name("tempUserToken").value(userInfo.tempUserToken);
            jwriter.name("documentsUploadPending").value(userInfo.documentsUploadPending);

             jwriter.name("userAddressList");
            if (userInfo.userAddressList != null)
                getUserAddressListJSON(userInfo, jwriter);
            jwriter.name("country").value(userInfo.country);
            jwriter.name("city").value(userInfo.city);
            jwriter.name("userDocs");
            if (userInfo.userDocs != null)
                getUserDocsListJSON(userInfo, jwriter);

            jwriter.endObject();
        }
    }


    public void getUserDocsListJSON(UserRegistration userInfo, JsonWriter jwriter) {
        try {
            jwriter.beginArray();
            if (userInfo.userDocs != null) {
                for (UserRegistration.UserDocCommand userDocCommand : userInfo.userDocs) {
                    jwriter.beginObject();
                    jwriter.name("docType").value(userDocCommand.docType);
                    jwriter.name("docFiles").value(userDocCommand.docFiles);
                    jwriter.name("docFormat").value(userDocCommand.docFormat);
                    jwriter.name("docId").value(userDocCommand.docId);
                    jwriter.name("reviewStatus").value(userDocCommand.reviewStatus);
                    jwriter.endObject();
                }
                jwriter.endArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserAddressListJSON(UserRegistration userInfo, JsonWriter jwriter) throws IOException {

        jwriter.beginArray();
        if (userInfo.userAddressList != null) {
            for (UserRegistration.UserAddressCommand userAddressCommand : userInfo.userAddressList) {
                jwriter.beginObject();
                jwriter.name("addressType").value(userAddressCommand.addressType);
                jwriter.name("address").value(userAddressCommand.address);
                jwriter.name("doorNumber").value(userAddressCommand.doorNumber);
                jwriter.name("postalAddress").value(userAddressCommand.postalAddress);
                jwriter.name("country").value(userAddressCommand.country);
                jwriter.name("city").value(userAddressCommand.city);
                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }

    public void getContractJSON(JsonWriter jwriter) {
        try {
            jwriter.beginObject();

            jwriter.name("periodNMonths").value(contract.periodNMonths);
            jwriter.name("additionalNotes").value(contract.additionalNotes);
            jwriter.name("additionalTerms").value(contract.additionalTerms);
            jwriter.name("doucmentUrl").value(contract.doucmentUrl);
            jwriter.name("usedTemplate").value(contract.usedTemplate);

            jwriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getProductListingsJSON(List<ProductListing> productListings, JsonWriter jwriter) {

            try {
                jwriter.beginArray();
                if (productListings != null) {
                    for (ProductListing productListing : productListings) {
                        jwriter.beginObject();
                        jwriter.name("serialNumber").value(productListing.serialNumber);
                        jwriter.name("listingPrice").value(productListing.listingPrice);
                        jwriter.name("listingId").value(productListing.listingId);
                        jwriter.endObject();
                    }
                    jwriter.endArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }



    public void getPaymentInfoJSON(JsonWriter jwriter) {
        try {
            if (paymentInfo != null) {
                jwriter.beginObject();

                jwriter.name("paymentId").value(paymentInfo.paymentId);
                jwriter.name("entityId").value(paymentInfo.entityId);
                jwriter.name("amountPaid").value(paymentInfo.amountPaid);
                // jwriter.name("paymentDate").value(paymentInfo.paymentDate);

                jwriter.name("creditAmount").value(paymentInfo.creditAmount);
                jwriter.name("creditDetails").value(paymentInfo.creditDetails);
                jwriter.name("paymentMethod").value(paymentInfo.paymentMethod);
                jwriter.name("chequeNumber").value(paymentInfo.chequeNumber);

                jwriter.name("receiptNumber").value(paymentInfo.receiptNumber);
                jwriter.name("bankDetail").value(paymentInfo.bankDetail);
                jwriter.name("chequeIssuer").value(paymentInfo.chequeIssuer);
                jwriter.name("chequeDate").value(paymentInfo.chequeDate);

                jwriter.name("paymentStatus").value(paymentInfo.paymentStatus);
                jwriter.name("entityType").value(paymentInfo.entityType);
                jwriter.name("userId").value(paymentInfo.userId);

                jwriter.endObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getSubscriptionJSON(JsonWriter jwriter) throws Exception {
        try {
            jwriter.beginArray();
            if (subscriptions != null) {
                for (SubscriptionCommand subscriptionCommand : subscriptions) {
                    jwriter.beginObject();
                    jwriter.name("subscriptionId").value(subscriptionCommand.subscriptionId);
                    jwriter.name("userId").value(subscriptionCommand.userId);
                    jwriter.name("billingCycleId").value(subscriptionCommand.billingCycleId);
                    jwriter.name("servedMSISDN").value(subscriptionCommand.servedMSISDN);
                    jwriter.name("planGroupId").value(subscriptionCommand.planGroupId);
                    jwriter.name("serviceBundleSerialNumber").value(subscriptionCommand.serviceBundleSerialNumber);
                    //jwriter.name("active").value(subscriptionCommand.active);
                    jwriter.name("deviceInfo").value(subscriptionCommand.deviceInfo);
                    jwriter.name("deviceType").value(subscriptionCommand.deviceType);
                    jwriter.name("discount").value(subscriptionCommand.discount);
                    jwriter.name("isFreeRecharge").value(subscriptionCommand.isFreeRecharge);
                    jwriter.name("freeRechargeUntil").value(String.valueOf(subscriptionCommand.freeRechargeUntil));
                    jwriter.name("billingFrequency").value(subscriptionCommand.billingFrequency);
                    jwriter.name("skipInventoryCheck").value(subscriptionCommand.skipInventoryCheck);
                    jwriter.name("subscriptionInfo").value(subscriptionCommand.subscriptionInfo);
                    jwriter.name("resellerId").value(subscriptionCommand.resellerId);
                    jwriter.name("periodicPlanId").value(subscriptionCommand.periodicPlanId);
                    jwriter.name("fixedIp").value(subscriptionCommand.fixedIp);
                    jwriter.name("macAddress").value(subscriptionCommand.macAddress);
                    jwriter.name("accessRoute").value(subscriptionCommand.accessRoute);
                    jwriter.name("isAccessRoute").value(subscriptionCommand.isAccessRoute);
                    jwriter.name("isSingleIp").value(subscriptionCommand.isSingleIp);
                    jwriter.name("discountType").value(subscriptionCommand.discountType);
                    jwriter.name("isAutoSelectIp").value(subscriptionCommand.isAutoSelectIp);
                    jwriter.name("subPlanAddonMappings");
                    getSubPlanAddonMappingsJSON(subscriptionCommand, jwriter);

                    jwriter.endObject();
                }
                jwriter.endArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

      public void getPostpaidExistingOrderSubscriptionJSON(JsonWriter jwriter) throws Exception {
        try {
            jwriter.beginArray();
            if (subscriptions != null) {
                for (SubscriptionCommand subscriptionCommand : subscriptions) {
                    jwriter.beginObject();
                    jwriter.name("servedMSISDN").value(subscriptionCommand.servedMSISDN);
                    jwriter.name("planGroupId").value(subscriptionCommand.planGroupId);
                    jwriter.name("serviceBundleSerialNumber").value(subscriptionCommand.serviceBundleSerialNumber);

                    jwriter.name("skipInventoryCheck").value(subscriptionCommand.skipInventoryCheck);
                    jwriter.name("subscriptionInfo").value(subscriptionCommand.subscriptionInfo);

                   jwriter.name("subPlanAddonMappings");
                    getSubPlanAddonMappingsJSON(subscriptionCommand, jwriter);

                    jwriter.endObject();
                }
                jwriter.endArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getSubPlanAddonMappingsJSON(SubscriptionCommand subscriptionCommand, JsonWriter jwriter) throws IOException {

        jwriter.beginArray();
        if (subscriptionCommand.subPlanAddonMappings != null) {
            for (SubscriptionCommand.SubscriptionPlanAddon subPlanAddon : subscriptionCommand.subPlanAddonMappings) {
                jwriter.beginObject();
                jwriter.name("planId").value(subPlanAddon.planId);
                if (subPlanAddon.planAddonIds != null) {
                    jwriter.name("planAddonIds");
                    getPlanAddonIdsJSON(subPlanAddon.planAddonIds, jwriter);
                }
                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }

    public void getPlanAddonIdsJSON(List<String> planAddonIds, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (String planAddonId : planAddonIds) {
                jwriter.value(planAddonId);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getStaffNewOrderJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        if (creditLimit != null) {
            jwriter.name("creditLimit").value(creditLimit);
        }
        jwriter.name("fulfillmentDone").value(fulfillmentDone);
        jwriter.name("registrationServiceType").value("Postpaid");

        jwriter.name("subscriptions");
        if (subscriptions != null) {
            try {
                getSubscriptionJSON(jwriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jwriter.name("contract");
        if (contract != null) {
            getContractJSON(jwriter);
        }

        jwriter.name("resellerLocation");
        if (resellerLocation != null) {
            getLocationCoordinatesJSON(jwriter);
        }
        if (creditScore != null) {
            jwriter.name("creditScore").value(creditScore);
        }

        if (requestedCreditLimit != null) {
            jwriter.name("requestedCreditLimit").value(requestedCreditLimit);
        } else {
            jwriter.name("requestedCreditLimit").value(0);
        }

            jwriter.name("userInfo");
            if (userInfo != null) {
                try {
                    staffUserInfoJSON(jwriter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        jwriter.name("totalValue").value(totalValue);
        jwriter.name("resellerZoneRadiusKM").value(resellerZoneRadiusKM);
            /* jwriter.name("paymentInfo");
            if(paymentInfo != null)
                getPaymentInfoJSON(jwriter);*/
        jwriter.name("currentStatus").value(currentStatus);
        if (depositValue != null) {
            jwriter.name("depositValue").value(depositValue);
        } else {
            jwriter.name("depositValue").value(0);
        }
        jwriter.name("productListingPrice");
        if (productListingPrice != null) {
            getproductListingPrice(productListingPrice,jwriter);
        }

        jwriter.name("productListingIds");
        if (productListingIds != null) {
            getproductListingIds(productListingIds,jwriter);
        }

        jwriter.name("requestedRechargeCommission").value(requestedRechargeCommission);
        jwriter.name("requestedPostPaidSignUpCommission").value(requestedPostPaidSignUpCommission);
        jwriter.name("requestedInvoiceCommission").value(requestedInvoiceCommission);
        jwriter.name("requestedSignUpCommission").value(requestedSignUpCommission);

        jwriter.name("productListingIdCounts");
        if (productListingIdCounts != null) {
            getproductListingIdCounts(productListingIdCounts, jwriter);
        }

        jwriter.name("addresellerCommand");
        if (addresellerCommand != null) {
            try {
                writeJsonResellerData(jwriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public  void getLocationCoordinatesJSON(JsonWriter jwriter) throws IOException {
        if(resellerLocation != null){
            jwriter.beginObject();

            jwriter.name("latitudeValue").value(resellerLocation.latitudeValue);
            jwriter.name("longitudeValue").value(resellerLocation.longitudeValue);

            jwriter.endObject();
        }
    }

    private void writeJsonResellerData(JsonWriter jwriter)throws IOException {
        if(addresellerCommand != null){
            jwriter.beginObject();

            jwriter.name("aggregatorId").value(addresellerCommand.aggregatorId);
            jwriter.name("immedidateServiceCutoffDays").value(addresellerCommand.immedidateServiceCutoffDays);
            jwriter.name("isAggregator").value(addresellerCommand.isAggregator);
            jwriter.name("isDistributor").value(addresellerCommand.isDistributor);
            jwriter.name("isRetailer").value(addresellerCommand.isRetailer);
            jwriter.name("paymentType").value(addresellerCommand.paymentType);
            jwriter.name("resellerName").value(addresellerCommand.resellerName);
            jwriter.name("status").value(addresellerCommand.status);
            jwriter.name("pin").value(addresellerCommand.pin);
            jwriter.endObject();

        }
    }


    public void getproductListingPrice(List<Float> orderCommand, JsonWriter jwriter) throws IOException {

        jwriter.beginArray();
        if (orderCommand != null) {
            for (Float x: orderCommand) {
                jwriter.beginObject();

                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }

    public void getproductListingIdCounts(List<Integer> orderCommand, JsonWriter jwriter) throws IOException {

        jwriter.beginArray();
        if (orderCommand != null) {
            for (Integer x: orderCommand) {
                jwriter.beginObject();

                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }

    public void getproductListingIds(List<Long> orderCommand, JsonWriter jwriter) throws IOException {

        jwriter.beginArray();
        if (orderCommand != null) {
            for (Long x: orderCommand) {
                jwriter.beginObject();

                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }


    public static class LocationCoordinates implements Serializable {
        public Double latitudeValue;
        public Double longitudeValue;

        public String getCoordinatesJSON() throws IOException {
            StringWriter swriter = new StringWriter();
            JsonWriter jwriter = new JsonWriter(swriter);
            jwriter.setIndent(" ");

            jwriter.beginObject();

            jwriter.name("latitudeValue").value(latitudeValue);
            jwriter.name("longitudeValue").value(longitudeValue);

            jwriter.endObject();

            String json = swriter.toString();
            return json;
        }
    }
}
