package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResellerStaff implements DataModel {

  public  ResellerStaff(String userName, String roleName, String userId, String resellerId){
       super();
        this.fullName = userName;
        this.userGroup = roleName;
        this.userId = userId;
        this.resellerId = resellerId;
    }

    @Override
    public String toString() {
        return "ResellerStaff{" +
                "userId='" + userId + '\'' +
                ", isActive=" + isActive +
                ", resellerId='" + resellerId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userGroup='" + userGroup + '\'' +
                ", accountId='" + accountId + '\'' +
                '}';
    }

    public ResellerStaff(){
    }
    public String status;

    public String reason;

    public String resellerVerifyId;

    public String code;

    public Boolean isRetailer;

    public String distributorId;

    public String resellerName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResellerId() {
        return resellerId;
    }

    public void setResellerId(String resellerId) {
        this.resellerId = resellerId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String userId;

    public Boolean isActive;

    public String resellerId;

    public Boolean isAggregator;

    public Boolean isDistributor;

    public String aggregatorId;

    public Float creditLimit;

    public Float requestedRechargeCommission;

    public Float requestedPostPaidSignUpCommission;

    public Float requestedInvoiceCommission;

    public Float requestedSignUpCommission;

    public Float approvedRechargeCommission;

    public Float approvedPostPaidSignUpCommission;

    public Float approvedSignUpCommission;

    public Float approvedInvoiceCommission;

    public Integer immedidateServiceCutoffDays;

    public String prepaidTemplateId;

    public String postpaidTemplateId;

    public String namType;

    public String paymentType;

    public String createdOn;

    public String resellerCode;


    // DATE: 15/11/2019

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String userName;

    public String surname;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String fullName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String roleName;

    public String email;

    public String company;

    public String phoneNumber;

    public String currency;

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String userGroup;

    public String registrationType;

    public String nationality;

    public String accountId;

    public String pin;

    public List<ResellerStaff> distributorsList;
    public List<ResellerStaff> staffList;
    public List<ResellerStaff> retailersList;
    public NewOrderCommand.LocationCoordinates resellerSalesZone;


    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> requestByStaff = new ArrayList<DataModel>();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();

            }else if(name.equals("resellerId")) {

                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    requestByStaff.add(resellerStaff);
                    reader.endObject();
                }
                reader.endArray();
            }else if(name.equals("distributors")) {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    requestByStaff.add(resellerStaff);
                    reader.endObject();
                }
                reader.endArray();
            }else if(name.equals("retailers")) {

                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    requestByStaff.add(resellerStaff);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();

        return requestByStaff;
    }


    public static List<DataModel> resellerStaffParseJSONResponse(String response) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> staffByResellerStaffList = new ArrayList<DataModel>();
        reader.beginObject();

        ResellerStaff resellerStaff1 = new ResellerStaff();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                resellerStaff1.status  = reader.nextString();
            }else if(name.equals("distributors")) {
                reader.beginArray();
                List<ResellerStaff> distributorsList = new ArrayList<ResellerStaff>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    distributorsList.add(resellerStaff);
                    reader.endObject();
                }
                resellerStaff1.distributorsList = distributorsList;
                reader.endArray();

            }else if(name.equals("retailers")){
                reader.beginArray();
                List<ResellerStaff> retailersList = new ArrayList<ResellerStaff>();
                while(reader.hasNext()){
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    retailersList.add(resellerStaff);
                    reader.endObject();
                }
                resellerStaff1.retailersList = retailersList;
                reader.endArray();
            }else if(name.equals("staff")){
                reader.beginArray();
                List<ResellerStaff> staffList = new ArrayList<ResellerStaff>();

                while(reader.hasNext()){
                    reader.beginObject();
                    ResellerStaff resellerStaff = readRequestsObject(reader);
                    staffList.add(resellerStaff);
                    reader.endObject();
                }
                resellerStaff1.staffList = staffList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        staffByResellerStaffList.add(resellerStaff1);

        return staffByResellerStaffList;

    }

    public static ResellerStaff readRequestsObject(JsonReader reader) throws IOException {
        ResellerStaff request = new ResellerStaff();

        while(reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("status")&& reader.peek() != JsonToken.NULL) {
                request.status = reader.nextString();
            } else if(name.equals("reason")&& reader.peek() != JsonToken.NULL) {
                request.reason = reader.nextString();
            } else if(name.equals("resellerVerifyId")&& reader.peek() != JsonToken.NULL) {
                request.resellerVerifyId = reader.nextString();
            } else if(name.equals("distributorId")&& reader.peek() != JsonToken.NULL) {
                request.distributorId = reader.nextString();
            }else if(name.equals("code")&& reader.peek() != JsonToken.NULL) {
                request.code = reader.nextString();
            } else if(name.equals("resellerName")&& reader.peek() != JsonToken.NULL) {
                request.resellerName = reader.nextString();
            }else if(name.equals("userId")&& reader.peek() != JsonToken.NULL) {
                request.userId = reader.nextString();
            }else if(name.equals("isRetailer")&& reader.peek() != JsonToken.NULL) {
                request.isRetailer = reader.nextBoolean();
            }else if(name.equals("fullName")&& reader.peek() != JsonToken.NULL) {
                request.fullName = reader.nextString();
            }else if(name.equals("userGroup")&& reader.peek() != JsonToken.NULL) {
                request.userGroup = reader.nextString();
            }else if(name.equals("isActive")&& reader.peek() != JsonToken.NULL) {
                request.isActive = reader.nextBoolean();
            }else if(name.equals("createdOn")&& reader.peek() != JsonToken.NULL) {
                try {
                    Date date2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString());
                    // orderNumberDetails.approvedOn = date2;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    request.createdOn = format.format(date2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else if(name.equals("resellerId")&& reader.peek() != JsonToken.NULL) {
                request.resellerId = reader.nextString();
            }else if(name.equals("isAggregator")&& reader.peek() != JsonToken.NULL) {
                request.isAggregator = reader.nextBoolean();
            }else if(name.equals("resellerCode")&& reader.peek() != JsonToken.NULL) {
                request.resellerCode = reader.nextString();
            }else if(name.equals("isDistributor")&& reader.peek() != JsonToken.NULL) {
                request.isDistributor = reader.nextBoolean();
            }else if(name.equals("aggregatorId")&& reader.peek() != JsonToken.NULL) {
                request.aggregatorId = reader.nextString();
            }else if(name.equals("creditLimit")&& reader.peek() != JsonToken.NULL) {
                request.creditLimit = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedRechargeCommission")&& reader.peek() != JsonToken.NULL) {
                request.requestedRechargeCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedPostPaidSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                request.requestedPostPaidSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedInvoiceCommission")&& reader.peek() != JsonToken.NULL) {
                request.requestedInvoiceCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                request.requestedSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedRechargeCommission")&& reader.peek() != JsonToken.NULL) {
                request.approvedRechargeCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedPostPaidSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                request.approvedPostPaidSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                request.approvedSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedInvoiceCommission")&& reader.peek() != JsonToken.NULL) {
                request.approvedInvoiceCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("immedidateServiceCutoffDays")&& reader.peek() != JsonToken.NULL) {
                request.immedidateServiceCutoffDays = reader.nextInt();
            }else if(name.equals("prepaidTemplateId")&& reader.peek() != JsonToken.NULL) {
                request.prepaidTemplateId = reader.nextString();
            }else if(name.equals("postpaidTemplateId")&& reader.peek() != JsonToken.NULL) {
                request.postpaidTemplateId = reader.nextString();
            }else if(name.equals("namType")&& reader.peek() != JsonToken.NULL) {
                request.namType = reader.nextString();
            }else if(name.equals("paymentType")&& reader.peek() != JsonToken.NULL) {
                request.paymentType = reader.nextString();
            }else if(name.equals("roleName")&& reader.peek() != JsonToken.NULL) {
                request.roleName = reader.nextString();
            }else if(name.equals("resellerSalesZone")&& reader.peek() != JsonToken.NULL) {
                NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();
                reader.beginObject();
                while (reader.hasNext()) {
                       coordinates = readLocationObject(reader);
                }
                reader.endObject();
                request.resellerSalesZone = coordinates;
            }else{ // resellerSalesZone
                reader.skipValue();
            }
        }
        return request;

    }

    public static NewOrderCommand.LocationCoordinates readLocationObject(JsonReader reader)throws IOException {
        NewOrderCommand.LocationCoordinates coordinates1 = new NewOrderCommand.LocationCoordinates();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("latitudeValue") && reader.peek() != JsonToken.NULL) {
                coordinates1.latitudeValue = reader.nextDouble();
            }else if (name.equals("longitudeValue") && reader.peek() != JsonToken.NULL) {
                coordinates1.longitudeValue = reader.nextDouble();
            }else {
                reader.skipValue();
            }
        }

        return coordinates1;
    }
    @Override
    public DataType getDataType() {
        return DataModel.DataType.ResellerStaff;
    }

}
