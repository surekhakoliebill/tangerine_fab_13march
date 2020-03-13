package com.aryagami.data;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ResellerRequestCommand implements DataModel, Serializable {
   public String resellerName;

   public String resellerId;

   public String userDescription;

   public Float requestAmount;

   public String reason;

   public Boolean isResellerTransfer;

   public String fromResellerId;

   public List<ResellerRequestMapperCommand> requestedVouchers = new ArrayList<ResellerRequestMapperCommand>();

   public String requestType;

   public String resellerRequestId;

   public String approvedBy;

   public String verifiedBy;

   public String status;

   public Float discountPercent;

   List<RequestStockMovementCommand> rrStockMovementCommands;

   public Boolean isDigitalApproval;

   public Boolean isDigitalSignatureRequired;

   public String pin;

   @Override
   public DataType getDataType() {
       return DataType.ResellerRequestCommand;
   }

   public class RequestStockMovementCommand {
       Long productListingId;

       List<String> requestMapperids;

       List<Integer> requestQuantities;

       //List<WarehouseBinLotSubcommand> warehouseBinLots;
   }

   public static class ResellerRequestMapperCommand{
       // Only in Update
       public String reqMapperId;

       public String entityType;

       public String entityId;

       public String entityName;

       public String entitySubId;

       //for voucher transfer
       public String voucherBatchId;

       public Float airTimeVoucherValue = 0f;

       public Float airTimeVoucherDiscountPercent = 0f;

       public Long quantity = 0l;

       public String additionalStarterBundlePlan;

       public String additionalStarterBundleOption;

       public Float additionalStarterBundleDiscountPercent = 0f;

       public String planGroupName;

       public Float entityDiscountPercent = 0f;
   }


   public String getResellerRequestJSON() throws IOException {
       StringWriter swriter = new StringWriter();
       JsonWriter jwriter = new JsonWriter(swriter);
       jwriter.setIndent(" ");

       jwriter.beginObject();

       jwriter.name("resellerId").value(resellerId);
       jwriter.name("fromResellerId").value(fromResellerId);
       jwriter.name("requestType").value(requestType);
       jwriter.name("isResellerTransfer").value(isResellerTransfer);

       jwriter.name("requestedVouchers");
       if (requestedVouchers != null) {
           try {
               getRequestedJSON(jwriter);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       jwriter.endObject();

       String json = swriter.toString();
       return json;
   }

   public void getRequestedJSON(JsonWriter jwriter) {
       try {
           jwriter.beginArray();
           if (requestedVouchers != null) {
               for (ResellerRequestMapperCommand mapperCommand : requestedVouchers) {
                   jwriter.beginObject();

                   jwriter.name("entityType").value(mapperCommand.entityType);
                   jwriter.name("entitySubId").value(mapperCommand.entitySubId);
                   jwriter.name("entityId").value(mapperCommand.entityId);
                   jwriter.name("quantity").value(mapperCommand.quantity);
                   jwriter.name("voucherBatchId").value(mapperCommand.voucherBatchId);

                   jwriter.endObject();
               }
               jwriter.endArray();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }

   }
}
