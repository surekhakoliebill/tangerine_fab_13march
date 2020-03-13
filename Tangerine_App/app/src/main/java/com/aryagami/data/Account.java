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

public class Account implements Serializable, DataModel {


    public String accountId;

    public String userId;

    public String currency;

    public String billingFrequency;

    public Long billingCycleId;

    public String discountType;

    public Float discountValue;

    public String username;

    public String primaryAccountUsername;
    public List<Account> accountList;
    public String status;


    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> AccountsList = new ArrayList<DataModel>();
        Account account = new Account();
        reader.beginObject();

        String status = null;

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                account.status = reader.nextString();
            } else if (name.equals("accounts")) {
                    List<Account> accounts = new ArrayList<Account>();
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    Account account1 = readAccountObject(reader);
                    accounts.add(account1);
                    reader.endObject();
                }
                account.accountList = accounts;
                reader.endArray();


            }
        }
        reader.endObject();
        AccountsList.add(account);
        return AccountsList;
    }

    private static Account readAccountObject(JsonReader reader) throws IOException {
        Account account = new Account();
        while (reader.hasNext()) {
            String name = null;
            try {
                name = reader.nextName();
                if (name.equals("accountId") && reader.peek() != JsonToken.NULL) {
                    account.accountId = reader.nextString();
                } else if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                    account.userId = reader.nextString();
                } else if (name.equals("currency") && reader.peek() != JsonToken.NULL) {
                    account.currency = reader.nextString();
                } else if (name.equals("username") && reader.peek() != JsonToken.NULL) {
                    account.username = reader.nextString();
                } else if (name.equals("primaryAccountUsername") && reader.peek() != JsonToken.NULL) {
                    account.primaryAccountUsername = reader.nextString();
                } else if (name.equals("discountValue") && reader.peek() != JsonToken.NULL) {
                    account.discountValue = Float.parseFloat(reader.nextString());
                } else if (name.equals("discountType") && reader.peek() != JsonToken.NULL) {
                    account.discountType = reader.nextString();
                } else if (name.equals("billingCycleId") && reader.peek() != JsonToken.NULL) {
                    account.billingCycleId = Long.parseLong(reader.nextString());
                } else if (name.equals("billingFrequency") && reader.peek() != JsonToken.NULL) {
                    account.billingFrequency = reader.nextString();
                } else {
                    reader.skipValue();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return account;
    }


    @Override
    public DataType getDataType() {
        return DataType.Account;
    }

}
