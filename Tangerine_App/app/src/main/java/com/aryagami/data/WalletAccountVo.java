package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WalletAccountVo implements DataModel, Serializable {

    public String walletName;

    public String walletType;

    public Float walletBalance;

    public String status;

    @Override
    public DataType getDataType() {
        return DataModel.DataType.WalletAccountVo;
    }

    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> walletBalanceList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;
        WalletAccountVo accountVo = new WalletAccountVo();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                accountVo.status = reader.nextString();
            }else if(name.equals("wallets"))
            {
                reader.beginObject();
                accountVo = readWalletObject(reader);
                walletBalanceList.add(accountVo);
                reader.endObject();
            }else{
                reader.skipValue();
            }

        }
        reader.endObject();

        return walletBalanceList;
    }

    public static WalletAccountVo readWalletObject(JsonReader reader)throws IOException {

         WalletAccountVo walletAccountVo = new WalletAccountVo();

         while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("walletName") && reader.peek() != JsonToken.NULL) {
                walletAccountVo.walletName = reader.nextString();
            }else if (name.equals("walletBalance") && reader.peek() != JsonToken.NULL) {
                walletAccountVo.walletBalance = Float.parseFloat(reader.nextString());
            }else if (name.equals("walletType") && reader.peek() != JsonToken.NULL) {
                walletAccountVo.walletType = reader.nextString();
            }else {
                reader.skipValue();
            }
        }
        return walletAccountVo;
    }
}
