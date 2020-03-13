package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aryagami on 10/10/17.
 */

public class UserRegistration implements DataModel, Serializable {

    public String userId;

    public String userName;

    public String fullName;

    public Boolean documentsUploadPending;

    public String otherNames;

    public String surname;

    public String surName;

    public String nationalIdentity;

    public String identityNumber;

    public String coiNumber;

    public String landLineNumber;

    public String email;

    public String company;

    public String phoneNumber;

    public String password;

    public String salesReference;

    public String currency;

    public String faxNumber;

    public String tinNumber;

    public String vatNumber;

    public String primaryPersonName;

    public String primaryPersonPhoneNumber;

    public String primaryPersonMobileNumber;

    public String alternatePersonName;

    public String alternatePhoneNumber;

    public String alternateLandLineNumber;

    public String resellerCode;

    public String physicalAddress;

    public String physicalAddress2;

    public String physicalAddressCity;

    public String physicalAddressCountry;

    public String correspondenceAddressCity;

    public String correspondenceAddressCountry;

    public String correspondenceAddress;

    public Boolean isActive;

    public String userGroup;

    public String userDoc;

    public String registrationType;

    public String alternatePersonEmailId;

    public String primaryPersonEmailId;

    public String nationality;

    public String refugeeIdentityNumber;

    public String profilePicture;

    public Boolean preRegisterUser;

    public String tempUserToken;

    public Long roleId;

    public String roleName;

    public String accountId;

    public String billingFrequency;

    public Long billingCycleId;

    public String discountType;

    public Float discountValue;

    public String newAccountId;

    public String documentId;

    public String dob;

    public String nin;
    public String userToken;
    public String filePrefix;

    /**
     *
     */
    public String givenNames;

    public String addressType;

    public String doorNumber;

    public String address;

    public String postalAddress;

    public String country;
    public String userDocType;
    public Boolean isDataSeparate;
    public Boolean fingerprintVerifed;

    public String city;
    public List<UserDocCommand> userDocs ;

    public List<UserAddressCommand> userAddressList;

    public String visaValidityDate;

    public String refugeeIssueDate;

    public String passportValidityDated;
    public String visaValidityType;
    public List<Subscription> userSubscriptions;
    public String resellerId;
    public String aggregator;

    // for refugee verification
    public String individualId;
    public String sex;
    public Integer yearOfBirth;
    public String fingerprint;



    public static class UserDocCommand implements Serializable {
        public String docType;

        public String docFiles;

        public String userId;

        public String docFormat;

        public String docId;

        public String reviewStatus;

        public String imageData;
        public String displayName;
        public String pdfRwaData;
        public String fileUrl;
        public String reason;
        public String tempUserToken;

        public static String getSimSwapDocCommandJSONArray(List<UserDocCommand> docCommand)throws IOException {

            StringWriter swriter = new StringWriter();
            JsonWriter jwriter = new JsonWriter(swriter);
            jwriter.setIndent(" ");

            jwriter.beginArray();
            for (UserDocCommand docCommand1 : docCommand){
                docCommand1.getUserDocJSON(jwriter);
            }
            jwriter.endArray();
            String json = swriter.toString();

            return json;
        }

        public void getUserDocJSON(JsonWriter jwriter) throws IOException {
            jwriter.beginObject();
            jwriter.name("docType").value(docType);
            jwriter.name("docFiles").value(docFiles);
            jwriter.name("docFormat").value(docFormat);
            jwriter.name("docId").value(docId);
            jwriter.name("reviewStatus").value(reviewStatus);
            jwriter.name("tempUserToken").value(tempUserToken);
            jwriter.endObject();
        }

        public String postUpdateDocumentJson() throws IOException {
            StringWriter swriter = new StringWriter();
            JsonWriter jwriter = new JsonWriter(swriter);
            jwriter.setIndent(" ");

            jwriter.beginObject();
            jwriter.name("docType").value(docType);
            jwriter.name("docFiles").value(docFiles);
            jwriter.name("docFormat").value(docFormat);
            jwriter.name("docId").value(docId);
            jwriter.name("reviewStatus").value(reviewStatus);
            jwriter.name("tempUserToken").value(tempUserToken);
            jwriter.name("userId").value(userId);
            jwriter.endObject();

            String json = swriter.toString();
            return json;
        }
    };

    public  static class UserAddressCommand implements Serializable {
        public Long addressId;
        public String userId;
        public String addressType;
        public String doorNumber;
        public String address;
        public String postalAddress;
        public String country;
        public String city;

    };

    public String getUserForeignUserInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("userId").value(userId);
        jwriter.name("visaValidityDate").value(visaValidityDate);
        jwriter.name("tempUserToken").value(tempUserToken);
        jwriter.name("userDocs");
        getVisaDocsListJSON(jwriter);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    public String getUserInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("userId").value(userId);
        jwriter.name("userName").value(userName);
        jwriter.name("fullName").value(fullName);
       // jwriter.name("picUpdated").value(picUpdated);
        jwriter.name("otherNames").value(otherNames);
        jwriter.name("surname").value(surname);
        jwriter.name("nationalIdentity").value(nationalIdentity);
        jwriter.name("identityNumber").value(identityNumber);
        jwriter.name("coiNumber").value(coiNumber);
        jwriter.name("landLineNumber").value(landLineNumber);
        jwriter.name("email").value(email);
        jwriter.name("company").value(company);
        jwriter.name("phoneNumber").value(phoneNumber);
        jwriter.name("password").value(password);
        jwriter.name("salesReference").value(salesReference);
        jwriter.name("currency").value(currency);
        jwriter.name("faxNumber").value(faxNumber);
        jwriter.name("tinNumber").value(tinNumber);
        jwriter.name("vatNumber").value(vatNumber);
        jwriter.name("primaryPersonName").value(primaryPersonName);
        jwriter.name("primaryPersonPhoneNumber").value(primaryPersonPhoneNumber);
        jwriter.name("primaryPersonMobileNumber").value(primaryPersonMobileNumber);
        jwriter.name("alternatePersonName").value(alternatePersonName);
        jwriter.name("alternatePhoneNumber").value(alternatePhoneNumber);
        jwriter.name("alternateLandLineNumber").value(alternateLandLineNumber);
        jwriter.name("resellerCode").value(resellerCode);
        jwriter.name("physicalAddress").value(physicalAddress);
        jwriter.name("physicalAddress2").value(physicalAddress2);
        jwriter.name("physicalAddressCity").value(physicalAddressCity);
        jwriter.name("physicalAddressCountry").value(physicalAddressCountry);
        jwriter.name("correspondenceAddressCity").value(correspondenceAddressCity);
        jwriter.name("correspondenceAddressCountry").value(correspondenceAddressCountry);
        jwriter.name("correspondenceAddress").value(correspondenceAddress);
        //jwriter.name("isActive").value(isActive);
        jwriter.name("userGroup").value(userGroup);
        jwriter.name("userDoc").value(userDoc);
        jwriter.name("registrationType").value(registrationType);
        jwriter.name("alternatePersonEmailId").value(alternatePersonEmailId);
        jwriter.name("primaryPersonEmailId").value(primaryPersonEmailId);
        jwriter.name("nationality").value(nationality);
        jwriter.name("refugeeIdentityNumber").value(refugeeIdentityNumber);
        jwriter.name("profilePicture").value(profilePicture);
      //  jwriter.name("preRegisterUser").value(preRegisterUser);
        jwriter.name("tempUserToken").value(tempUserToken);
        jwriter.name("userToken").value(userToken);
        jwriter.name("roleId").value(roleId);
        jwriter.name("roleName").value(roleName);
        jwriter.name("accountId").value(accountId);
        jwriter.name("billingFrequency").value(billingFrequency);
        jwriter.name("billingCycleId").value(billingCycleId);
        jwriter.name("discountType").value(discountType);
        jwriter.name("discountValue").value(discountValue);
        jwriter.name("newAccountId").value(newAccountId);
        jwriter.name("documentId").value(documentId);
        jwriter.name("dob").value(dob);
        jwriter.name("addressType").value(addressType);
        jwriter.name("doorNumber").value(doorNumber);
        jwriter.name("address").value(address);
        jwriter.name("postalAddress").value(postalAddress);
        jwriter.name("country").value(country);
        jwriter.name("city").value(city);
        jwriter.name("refugeeIssueDate").value(refugeeIssueDate);
        jwriter.name("visaValidityDate").value(visaValidityDate);
        jwriter.name("visaValidityType").value(visaValidityType);
        jwriter.name("userDocs");
        getUserDocsListJSON(jwriter);
        jwriter.name("userAddressList");
        getUserAddressListJSON(jwriter);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }


    public String postStaffUserInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();


        jwriter.name("accountId").value(accountId);
        jwriter.name("fullName").value(fullName);

         jwriter.name("coiNumber").value(coiNumber);
        jwriter.name("landLineNumber").value(landLineNumber);
        jwriter.name("email").value(email);
        jwriter.name("company").value(company);
        jwriter.name("phoneNumber").value(phoneNumber);
        jwriter.name("password").value(password);
        jwriter.name("currency").value(currency);

        jwriter.name("tinNumber").value(tinNumber);
        jwriter.name("primaryPersonName").value(primaryPersonName);
        jwriter.name("primaryPersonPhoneNumber").value(primaryPersonPhoneNumber);
        jwriter.name("resellerCode").value(resellerCode);
        jwriter.name("userGroup").value(userGroup);
        jwriter.name("registrationType").value(registrationType);
        jwriter.name("primaryPersonEmailId").value(primaryPersonEmailId);
       jwriter.name("roleId").value(roleId);
        jwriter.name("roleName").value(roleName);
        jwriter.name("userDocs");
        getUserDocsListJSON(jwriter);
        jwriter.name("userAddressList");
        getUserAddressListJSON(jwriter);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    public void getUserDocsListJSON(JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            if (userDocs != null) {
                for (UserDocCommand userDocCommand : userDocs) {
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
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void getVisaDocsListJSON(JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            if (userDocs != null) {
                for (UserDocCommand userDocCommand : userDocs) {
                    jwriter.beginObject();
                    jwriter.name("docType").value(userDocCommand.docType);
                    jwriter.name("docFiles").value(userDocCommand.docFiles);
                    jwriter.name("docFormat").value(userDocCommand.docFormat);
                    jwriter.name("docId").value(userDocCommand.docId);
                    jwriter.name("reviewStatus").value(userDocCommand.reviewStatus);
                    jwriter.name("reason").value(userDocCommand.reason);
                    jwriter.name("userId").value(userDocCommand.userId);
                    jwriter.name("tempUserToken").value(userDocCommand.tempUserToken);
                    jwriter.endObject();
                }
                jwriter.endArray();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }



    public String getUserNiraVerification() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");
        jwriter.beginObject();
        jwriter.name("nin").value(nin);
        jwriter.name("surName").value(surName);
        jwriter.name("surname").value(surname);
        jwriter.name("givenNames").value("");
        jwriter.name("otherNames").value("");
        jwriter.name("dob").value(dob);
        jwriter.name("documentId").value(documentId);
        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    public String postRefugeeVerificationJson() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");
        jwriter.beginObject();

        jwriter.name("individualId").value(individualId);
        jwriter.name("yearOfBirth").value(yearOfBirth);
        jwriter.name("sex").value(sex);
        jwriter.name("fingerprint").value(fingerprint);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    public DataModel.DataType getDataType() {
        return DataModel.DataType.UserRegistration;
    }


    public void getUserAddressListJSON(JsonWriter jwriter) throws IOException {
        jwriter.beginArray();
        if(userAddressList != null) {
            for (UserAddressCommand userAddressCommand : userAddressList) {
                jwriter.beginObject();
                jwriter.name("addressType").value(userAddressCommand.addressType);
                jwriter.name("address").value(userAddressCommand.address);
                jwriter.name("doorNumber").value(userAddressCommand.doorNumber);
                jwriter.name("postalAddress").value(userAddressCommand.postalAddress);
                jwriter.name("country").value(userAddressCommand.country);
                jwriter.name("city").value(userAddressCommand.city);
                jwriter.name("addressId").value(userAddressCommand.addressId);
                jwriter.endObject();
            }
            jwriter.endArray();
        }
    }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {

        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> userInfoList = new ArrayList<DataModel>();
        reader.beginObject();


        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String status = reader.nextString();
            }else if(name.equals("allUsers"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    UserRegistration userRegistration = readUserInfoObject(reader);
                    userInfoList.add(userRegistration);
                    reader.endObject();
                }
                reader.endArray();

            }
        }
        reader.endObject();

        return userInfoList;
    }

    public static UserRegistration readUserInfoObject(JsonReader reader) throws IOException {

        UserRegistration registration = new UserRegistration();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                registration.userId = reader.nextString();
            }else if (name.equals("tempUserToken") && reader.peek() != JsonToken.NULL) {
                registration.tempUserToken = reader.nextString();
            }else if (name.equals("userName") && reader.peek() != JsonToken.NULL) {
                registration.userName = reader.nextString();
            }else if (name.equals("fullName") && reader.peek() != JsonToken.NULL) {
                registration.fullName = reader.nextString();
            }else if (name.equals("otherNames") && reader.peek() != JsonToken.NULL) {
                registration.otherNames = reader.nextString();
            }else if (name.equals("surname") && reader.peek() != JsonToken.NULL) {
                registration.surname = reader.nextString();
            }else if (name.equals("surName") && reader.peek() != JsonToken.NULL) {
                registration.surName = reader.nextString();
            }else if (name.equals("nationalIdentity") && reader.peek() != JsonToken.NULL) {
                registration.nationalIdentity = reader.nextString();
            }else if (name.equals("identityNumber") && reader.peek() != JsonToken.NULL) {
                registration.identityNumber = reader.nextString();
            }else if (name.equals("coiNumber") && reader.peek() != JsonToken.NULL) {
                registration.coiNumber = reader.nextString();
            }else if (name.equals("landLineNumber") && reader.peek() != JsonToken.NULL) {
                registration.landLineNumber = reader.nextString();
            }else if (name.equals("email") && reader.peek() != JsonToken.NULL) {
                registration.email = reader.nextString();
            }else if (name.equals("company") && reader.peek() != JsonToken.NULL) {
                registration.company = reader.nextString();
            }else if (name.equals("phoneNumber") && reader.peek() != JsonToken.NULL) {
                registration.phoneNumber = reader.nextString();
            }else if (name.equals("password") && reader.peek() != JsonToken.NULL) {
                registration.password = reader.nextString();
            }else if (name.equals("currency") && reader.peek() != JsonToken.NULL) {
                registration.currency = reader.nextString();
            }else if (name.equals("userToken") && reader.peek() != JsonToken.NULL) {
                registration.userToken = reader.nextString();
            }else if (name.equals("tinNumber") && reader.peek() != JsonToken.NULL) {
                registration.tinNumber = reader.nextString();
            }else if (name.equals("primaryPersonName") && reader.peek() != JsonToken.NULL) {
                registration.primaryPersonName = reader.nextString();
            }else if (name.equals("physicalAddress") && reader.peek() != JsonToken.NULL) {
                registration.physicalAddress = reader.nextString();
            }else if (name.equals("physicalAddress2") && reader.peek() != JsonToken.NULL) {
                registration.physicalAddress2 = reader.nextString();
            }else if (name.equals("physicalAddressCity") && reader.peek() != JsonToken.NULL) {
                registration.physicalAddressCity = reader.nextString();
            }else if (name.equals("physicalAddressCountry") && reader.peek() != JsonToken.NULL) {
                registration.physicalAddressCountry = reader.nextString();
            }else if (name.equals("primaryPersonPhoneNumber") && reader.peek() != JsonToken.NULL) {
                registration.primaryPersonPhoneNumber = reader.nextString();
            }else if (name.equals("primaryPersonMobileNumber") && reader.peek() != JsonToken.NULL) {
                registration.primaryPersonMobileNumber = reader.nextString();
            }else if (name.equals("alternatePersonName") && reader.peek() != JsonToken.NULL) {
                registration.alternatePersonName = reader.nextString();
            }else if (name.equals("alternatePhoneNumber") && reader.peek() != JsonToken.NULL) {
                registration.alternatePhoneNumber = reader.nextString();
            }else if (name.equals("alternateLandLineNumber") && reader.peek() != JsonToken.NULL) {
                registration.alternateLandLineNumber = reader.nextString();
            }else if (name.equals("resellerCode") && reader.peek() != JsonToken.NULL) {
                registration.resellerCode = reader.nextString();
            }else if (name.equals("isActive") && reader.peek() != JsonToken.NULL) {
                registration.isActive = reader.nextBoolean();
            }else if (name.equals("userGroup") && reader.peek() != JsonToken.NULL) {
                registration.userGroup = reader.nextString();
            }else if (name.equals("registrationType") && reader.peek() != JsonToken.NULL) {
                registration.registrationType = reader.nextString();
            }else if (name.equals("alternatePersonEmailId") && reader.peek() != JsonToken.NULL) {
                registration.alternatePersonEmailId = reader.nextString();
            }else if (name.equals("primaryPersonEmailId") && reader.peek() != JsonToken.NULL) {
                registration.primaryPersonEmailId = reader.nextString();
            }else if (name.equals("nationality") && reader.peek() != JsonToken.NULL) {
                registration.nationality = reader.nextString();
            }else if (name.equals("userefugeeIdentityNumberrId") && reader.peek() != JsonToken.NULL) {
                registration.refugeeIdentityNumber = reader.nextString();
            }else if (name.equals("roleId") && reader.peek() != JsonToken.NULL) {
                registration.roleId = Long.parseLong(reader.nextString());
            }else if (name.equals("roleName") && reader.peek() != JsonToken.NULL) {
                registration.roleName = reader.nextString();
            }else if (name.equals("accountId") && reader.peek() != JsonToken.NULL) {
                registration.accountId = reader.nextString();
            }else if (name.equals("billingFrequency") && reader.peek() != JsonToken.NULL) {
                registration.billingFrequency = reader.nextString();
            }else if (name.equals("billingCycleId") && reader.peek() != JsonToken.NULL) {
                registration.billingCycleId = Long.parseLong(reader.nextString());
            }else if (name.equals("discountType") && reader.peek() != JsonToken.NULL) {
                registration.discountType = reader.nextString();
            }else if (name.equals("discountValue") && reader.peek() != JsonToken.NULL) {
                registration.discountValue = Float.parseFloat(reader.nextString());
            }else if (name.equals("givenNames") && reader.peek() != JsonToken.NULL) {
                registration.givenNames = reader.nextString();
            }else if (name.equals("dob") && reader.peek() != JsonToken.NULL) {
                registration.dob = reader.nextString();
            }else if (name.equals("documentId") && reader.peek() != JsonToken.NULL) {
                registration.documentId = reader.nextString();
            }else if (name.equals("visaValidityDate") && reader.peek() != JsonToken.NULL) {
                registration.visaValidityDate = reader.nextString();
            }else if (name.equals("refugeeIssueDate") && reader.peek() != JsonToken.NULL) {
                registration.refugeeIssueDate = reader.nextString();
            }else if (name.equals("refugeeIdentityNumber") && reader.peek() != JsonToken.NULL) {
                registration.refugeeIdentityNumber = reader.nextString();
            }else if (name.equals("passportValidityDated") && reader.peek() != JsonToken.NULL) {
                registration.passportValidityDated = reader.nextString();
            }else if (name.equals("userDoc") && reader.peek() != JsonToken.NULL) {
                registration.userDoc = reader.nextString();
            }else if (name.equals("userSubscriptions") && reader.peek() != JsonToken.NULL) {

                reader.beginArray();
                List<Subscription> subscriptionList = new ArrayList<Subscription>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    Subscription subscriptionCommand = readSubscriptionObject(reader);
                    subscriptionList.add(subscriptionCommand);
                    reader.endObject();
                }
                registration.userSubscriptions  = subscriptionList;
                reader.endArray();

            }else if (name.equals("visaValidityType") && reader.peek() != JsonToken.NULL) {
                registration.visaValidityType = reader.nextString();
            }else if (name.equals("userDocs") && reader.peek() != JsonToken.NULL) {
                reader.beginArray();
                List<UserDocCommand> docCommandList = new ArrayList<UserDocCommand>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    UserDocCommand command = readDocObject(reader);
                    docCommandList.add(command);
                    reader.endObject();
                }
                registration.userDocs  = docCommandList;
                reader.endArray();

            }else if (name.equals("userAddressList") && reader.peek() != JsonToken.NULL) {
                reader.beginArray();
                List<UserAddressCommand> userAddressList = new ArrayList<UserAddressCommand>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    UserAddressCommand command1 = readAddressObject(reader);
                    userAddressList.add(command1);
                    reader.endObject();
                }
                registration.userAddressList  = userAddressList;
                reader.endArray();
            }else{
                reader.skipValue();
            }


        }
        return registration;
    }


    public static Subscription readSubscriptionObject(JsonReader reader) throws IOException {
        Subscription subscription = new Subscription();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("invoiceId") && reader.peek() != JsonToken.NULL) {
                subscription.invoiceId = reader.nextString();
            }else if (name.equals("userName") && reader.peek() != JsonToken.NULL) {
                subscription.userName = reader.nextString();
            } else if (name.equals("servedMSISDN") && reader.peek() != JsonToken.NULL) {
                subscription.servedMSISDN = reader.nextString();
            } else if (name.equals("planGroupId") && reader.peek() != JsonToken.NULL) {
                subscription.planGroupId = reader.nextLong();
            } else if (name.equals("subscriptionId") && reader.peek() != JsonToken.NULL) {
                subscription.subscriptionId = reader.nextString();
            } else if (name.equals("planType") && reader.peek() != JsonToken.NULL) {
                subscription.planType = reader.nextString();
            } else if (name.equals("fullName") && reader.peek() != JsonToken.NULL) {
                subscription.fullName = reader.nextString();
            } else if (name.equals("billingCycleName") && reader.peek() != JsonToken.NULL) {
                subscription.billingCycleName = reader.nextString();
            } else if (name.equals("createdDate") && reader.peek() != JsonToken.NULL) {
                subscription.createdDate = reader.nextString();
            } else if (name.equals("resellerName") && reader.peek() != JsonToken.NULL) {
                subscription.resellerName = reader.nextString();
            } else if (name.equals("activatedDate") && reader.peek() != JsonToken.NULL) {
                subscription.activatedDate = reader.nextString();
            } else if (name.equals("billingDate") && reader.peek() != JsonToken.NULL) {
                subscription.billingDate = reader.nextString();
            } else if (name.equals("groupName") && reader.peek() != JsonToken.NULL) {
                subscription.groupName = reader.nextString();
            } else if (name.equals("deviceInfo") && reader.peek() != JsonToken.NULL) {
                subscription.deviceInfo = reader.nextString();
            } else if (name.equals("billingFrequency") && reader.peek() != JsonToken.NULL) {
                subscription.billingFrequency = reader.nextString();
            } else if (name.equals("freeRechargeUntil") && reader.peek() != JsonToken.NULL) {
                subscription.freeRechargeUntil = reader.nextString();
            }else if (name.equals("otherNames") && reader.peek() != JsonToken.NULL) {
                subscription.otherNames = reader.nextString();
            }else{
                reader.skipValue();
            }
        }

        return  subscription;
    }


    public static UserDocCommand readDocObject(JsonReader reader) throws IOException {

        UserDocCommand command = new UserDocCommand();
        while (reader.hasNext()){

            String name= reader.nextName();
            if (name.equals("docFiles") && reader.peek() != JsonToken.NULL) {
                command.docFiles = reader.nextString();
            }else if (name.equals("docFormat") && reader.peek() != JsonToken.NULL) {
                command.docFormat = reader.nextString();
            }else if (name.equals("docType") && reader.peek() != JsonToken.NULL) {
                command.docType = reader.nextString();
            }else if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                command.userId = reader.nextString();
            }else if (name.equals("docId") && reader.peek() != JsonToken.NULL) {
                command.docId = reader.nextString();
            }else if (name.equals("reviewStatus") && reader.peek() != JsonToken.NULL) {
                command.reviewStatus = reader.nextString();
            }else if (name.equals("tempUserToken") && reader.peek() != JsonToken.NULL) {
                command.tempUserToken = reader.nextString();
            }/*else if (name.equals("submitDate") && reader.peek() != JsonToken.NULL) {
                command.submitDate = reader.nextString();
            }else if (name.equals("statusUpdatedOn") && reader.peek() != JsonToken.NULL) {
                command.statusUpdatedOn = reader.nextString();
            }*/else{
                reader.skipValue();
            }
        }
        return command;
    }

    public static UserAddressCommand readAddressObject(JsonReader reader) throws IOException {

        UserAddressCommand command = new UserAddressCommand();
        while (reader.hasNext()){

            String name = reader.nextName();
            if (name.equals("addressType") && reader.peek() != JsonToken.NULL) {
                command.addressType = reader.nextString();
            }else if (name.equals("address") && reader.peek() != JsonToken.NULL) {
                command.address = reader.nextString();
            }else if (name.equals("country") && reader.peek() != JsonToken.NULL) {
                command.country = reader.nextString();
            }else if (name.equals("city") && reader.peek() != JsonToken.NULL) {
                command.city = reader.nextString();
            }else if (name.equals("doorNumber") && reader.peek() != JsonToken.NULL) {
                command.doorNumber = reader.nextString();
            }else if (name.equals("postalAddress") && reader.peek() != JsonToken.NULL) {
                command.postalAddress = reader.nextString();
            }else if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                command.userId = reader.nextString();
            }else if (name.equals("addressId") && reader.peek() != JsonToken.NULL) {
                command.addressId = Long.parseLong(reader.nextString());
            }else{
                reader.skipValue();
            }
        }

        return command;
    }


}
