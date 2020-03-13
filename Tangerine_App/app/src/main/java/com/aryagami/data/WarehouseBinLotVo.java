package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WarehouseBinLotVo implements DataModel {
    public String lotId;

    public String binId;

    public String lotName;

    public String lotDesc;

    public Float taxIncurred;

    public Float inwardPrice;

    public Integer inwardQuantity;

    public String productType;

    public String productSubType;

    public String productListing;

    public Integer outwardQuantity;

    public Float outwardPrice;

    public String approvalStatus;
    public String responseStatus;

    public static List<DataModel> parseJSONResponse(String response) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> resellerWarehouseStockList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;


        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();
            }else if(name.equals("warehouseBinLots"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    WarehouseBinLotVo lotVo = readResellerStockBinObject(reader);
                    resellerWarehouseStockList.add(lotVo);
                    reader.endObject();
                }
                reader.endArray();

            }
        }
        reader.endObject();

        return resellerWarehouseStockList;

    }

    public static WarehouseBinLotVo readResellerStockBinObject(JsonReader reader) throws IOException {
        WarehouseBinLotVo binLotVo = new WarehouseBinLotVo();

        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("approvalStatus") && reader.peek() != JsonToken.NULL){
                     binLotVo.approvalStatus = reader.nextString();
            }else if(name.equals("binId") && reader.peek() != JsonToken.NULL) {
                    binLotVo.binId = reader.nextString();
            }else if(name.equals("approvalStatus") && reader.peek() != JsonToken.NULL){
                    //binLotVo.inwardDate =
            }else if(name.equals("inwardPrice") && reader.peek() != JsonToken.NULL){
                binLotVo.inwardPrice = Float.parseFloat(reader.nextString());
            }else if(name.equals("inwardQuantity") && reader.peek() != JsonToken.NULL){
                binLotVo.inwardQuantity =  reader.nextInt();
            }else if(name.equals("lotDesc") && reader.peek() != JsonToken.NULL){
                binLotVo.lotDesc = reader.nextString();
            }else if(name.equals("binId") && reader.peek() != JsonToken.NULL){
                binLotVo.binId = reader.nextString();
            }else if(name.equals("lotId") && reader.peek() != JsonToken.NULL){
                binLotVo.lotId = reader.nextString();
            }else if(name.equals("lotName") && reader.peek() != JsonToken.NULL){
                binLotVo.lotName = reader.nextString();
            }else if(name.equals("outwardPrice") && reader.peek() != JsonToken.NULL){
                binLotVo.outwardPrice = Float.parseFloat(reader.nextString());
            }else if(name.equals("outwardQuantity") && reader.peek() != JsonToken.NULL){
                binLotVo.outwardQuantity = reader.nextInt();
            }else if(name.equals("productListing") && reader.peek() != JsonToken.NULL){
                binLotVo.productListing = reader.nextString();
            }else if(name.equals("productSubType") && reader.peek() != JsonToken.NULL){
                binLotVo.productSubType = reader.nextString();
            }else if(name.equals("productType") && reader.peek() != JsonToken.NULL){
                binLotVo.productType = reader.nextString();
            }else if(name.equals("taxIncurred") && reader.peek() != JsonToken.NULL){
                binLotVo.taxIncurred = Float.parseFloat(reader.nextString());
            }else{
                reader.skipValue();
            }
        }

        return binLotVo;
    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.WarehouseBinLotVo;
    }

}
