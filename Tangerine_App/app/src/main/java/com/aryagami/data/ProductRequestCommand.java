package com.aryagami.data;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

public class ProductRequestCommand implements DataModel, Serializable {

    public Boolean inboundStock;

    public Boolean hasNewProductListing;

    public String fromBin;

    public String toBin;

    public String toBinRef;

    public String fromBinRef;

    public String lotId;

    public String lotName;

    public String lotDescription;

    public String logNarration;

    public String invoiceId;

    public String lotLocation;

    public Float taxIncurred;

    public Float inwardPrice;

    public Integer inwardQuantity;

    public Boolean isResellerTransfer;

    public Float outwardPrice;

    public String productCategory;

    public Long productListingId;

  //  public ProductListingCommand newProductListing;

    public Boolean isImmediateOutward = false;

    public Boolean approvalNotRequired = false;

    public Boolean inventoryIsReserved = false;

    public String deviceAdditionalInfo;

    public String rejectReason;

    List<String> serialNumbers;

  //  List<SimDetailCommand> simDetails;

   // List<MifiDetailCommand> mifiDetails;

    Long serialNumberStart;

    Long serialNumberEnd;

    public Boolean isReturnStockRequest = false;

    public Boolean isReturnedToOrginalBin = false;

    public Boolean resetReserveState = false;

    Float inventoryPrice;



    public static String getProductRequestJSONArray(List<ProductRequestCommand> reqCommand)throws IOException {

        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginArray();
        for (ProductRequestCommand productRequestCommand : reqCommand){
            productRequestCommand.getPostJSON(jwriter);
        }
        jwriter.endArray();
        String json = swriter.toString();

        return json;
    }

    public void getPostJSON(JsonWriter jwriter) throws IOException {
              jwriter.beginObject();

                jwriter.name("fromBinRef").value(fromBinRef);
                jwriter.name("inboundStock").value(inboundStock);
                jwriter.name("inwardQuantity").value(inwardQuantity);
                jwriter.name("isResellerTransfer").value(isResellerTransfer);
                jwriter.name("productListingId").value(productListingId);
                jwriter.name("toBinRef").value(toBinRef);

              jwriter.endObject();
    }

    @Override
    public DataType getDataType() {
        return DataType.ProductRequestCommand;
    }
}
