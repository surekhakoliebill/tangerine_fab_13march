package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalletAccountTransactionLogVo implements DataModel, Serializable {

    public Long transactionId;

    public String walletId;

    public String walletName;

    public String transcationType;

    public String transactionInfo;

    public Date transactedOn;

    public String requestedOn;

    public Boolean isRequest;

    public Long invoiceId;

    public Float invoiceAmount;

    public Float transactionAmount;

    public Float commissionPercent;

    public String resellerName;

    public Float previousBalance;

    public Float topUpAmount;

    public Float currentBalance;

    public Float discountPercentage;

    public String comments;

    public String creditControllerApprovedName;

    public Date creditControllerApprovedDate;

    public Date financeAssistantApprovedDate;

    public String financeAssistantApprovedBy;

    public String financeManagerApprovedBy;

    public Date financeManagerApprovedDate;

    public String toReseller;

    public List<WalletAccountTransactionLogVo> walletRequests;
    public List<WalletAccountTransactionLogVo> productRequests;

    public String status;

    // for physical Product requests
    public String requestId;

    public String requestedDate;

    public String fromBinName;

    public String toBinName;

    public String requestStatus;

    public Long doNumber;
    public String deliveryNoteUrl;

    public String stockInfo;
    public Boolean isCancelled;
    public List<StockMovementLogVo> movementRequests;

    public static class StockMovementLogVo implements Serializable {

        public Long logId;

        public Boolean inboundStock;

        public String fromBin;

        public String toBin;

        public String fromBinName;

        public String toBinName;

        public String lotId;

        public String logNarration;

        public String productCategory;

        public String productListingName;

        public String productListingId;

        public String invoiceId;

        public Float inwardPrice;

        public Integer inwardQuantity;

        public Float outwardPrice;

        public Date createdOn;

        public Date approvedOn;

        public Float taxIncurred;

        public String approvalStatus;

        public String rejectReason;
    }


    public static List<DataModel> parseETopupRequestsJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> availableMSISDNList = new ArrayList<DataModel>();

       WalletAccountTransactionLogVo transactionLogVo = new WalletAccountTransactionLogVo();

        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                transactionLogVo.status = reader.nextString();
            }else if (name.equals("walletRequests") && reader.peek() != JsonToken.NULL) {
                List<WalletAccountTransactionLogVo> traList = new ArrayList<WalletAccountTransactionLogVo>();
                    reader.beginArray();
                    while(reader.hasNext()) {
                        reader.beginObject();
                        WalletAccountTransactionLogVo transactionLogVo1 = readRequestsObject(reader);
                        traList.add(transactionLogVo1);
                        reader.endObject();
                    }
                    transactionLogVo.walletRequests = traList;
                    reader.endArray();

            }else if (name.equals("stockMovementRequests") && reader.peek() != JsonToken.NULL) {
                List<WalletAccountTransactionLogVo> traList = new ArrayList<WalletAccountTransactionLogVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    WalletAccountTransactionLogVo transactionLogVo1 = readProductRequestsObject(reader);
                    traList.add(transactionLogVo1);
                    reader.endObject();
                }
                transactionLogVo.productRequests = traList;
                reader.endArray();

            }else{
                reader.skipValue();
            }
        }
        availableMSISDNList.add(transactionLogVo);
        reader.endObject();

        return availableMSISDNList;
    }

    public static WalletAccountTransactionLogVo readProductRequestsObject(JsonReader reader)throws IOException {
        WalletAccountTransactionLogVo request = new WalletAccountTransactionLogVo();

        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("requestId")&& reader.peek() != JsonToken.NULL) {
                request.requestId = reader.nextString();
            }else if (name.equals("requestedDate")&& reader.peek() != JsonToken.NULL) {
                request.requestedDate = reader.nextString();
            } else if (name.equals("fromBinName")&& reader.peek() != JsonToken.NULL) {
                request.fromBinName = reader.nextString();
            }else if (name.equals("toBinName")&& reader.peek() != JsonToken.NULL) {
                request.toBinName = reader.nextString();
            }else if (name.equals("requestStatus")&& reader.peek() != JsonToken.NULL) {
                request.requestStatus = reader.nextString();
            }else if (name.equals("doNumber")&& reader.peek() != JsonToken.NULL) {
                request.doNumber = Long.parseLong(reader.nextString());
            }else if (name.equals("stockInfo")&& reader.peek() != JsonToken.NULL) {
                request.stockInfo = reader.nextString();
            }else if (name.equals("deliveryNoteUrl")&& reader.peek() != JsonToken.NULL) {
                request.deliveryNoteUrl = reader.nextString();
            }else if (name.equals("movementRequests")&& reader.peek() != JsonToken.NULL) {

                List<StockMovementLogVo> traList = new ArrayList<StockMovementLogVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    StockMovementLogVo stockMovementLogVo = readStockMovementLogVoObject(reader);
                    traList.add(stockMovementLogVo);
                    reader.endObject();
                }
                request.movementRequests = traList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        return request;
    }

    private static StockMovementLogVo readStockMovementLogVoObject(JsonReader reader) throws IOException {
        StockMovementLogVo request = new StockMovementLogVo();

        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("logNarration")&& reader.peek() != JsonToken.NULL) {
                request.logNarration = reader.nextString();
            }else if (name.equals("inwardQuantity")&& reader.peek() != JsonToken.NULL) {
                request.inwardQuantity = Integer.valueOf(reader.nextString());
            }else{
                reader.skipValue();
            }
        }
        return request;
    }

    public static WalletAccountTransactionLogVo readRequestsObject(JsonReader reader)throws IOException {
        WalletAccountTransactionLogVo request = new WalletAccountTransactionLogVo();

        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("transactionId")&& reader.peek() != JsonToken.NULL) {
               request.transactionId = reader.nextLong();
            }else if (name.equals("transcationType")&& reader.peek() != JsonToken.NULL) {
                request.transcationType = reader.nextString();
            } else if (name.equals("walletName")&& reader.peek() != JsonToken.NULL) {
                request.walletName = reader.nextString();
            }else if (name.equals("transactionAmount")&& reader.peek() != JsonToken.NULL) {
                request.transactionAmount = Float.parseFloat(reader.nextString());
            }else if (name.equals("transactionInfo")&& reader.peek() != JsonToken.NULL) {
                request.transactionInfo = reader.nextString();
            }else if (name.equals("walletId")&& reader.peek() != JsonToken.NULL) {
                request.walletId = reader.nextString();
            }else if (name.equals("requestedOn")&& reader.peek() != JsonToken.NULL) {
                request.requestedOn = reader.nextString();
            }else if (name.equals("isRequest")&& reader.peek() != JsonToken.NULL) {
                request.isRequest = reader.nextBoolean();
            }else if (name.equals("isCancelled")&& reader.peek() != JsonToken.NULL) {
                request.isCancelled = reader.nextBoolean();
            }else{
                reader.skipValue();
            }
        }
        return request;
    }

    @Override
    public DataType getDataType() {
        return DataType.WalletAccountTransactionLogVo;
    }



}
