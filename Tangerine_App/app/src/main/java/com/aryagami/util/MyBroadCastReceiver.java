package com.aryagami.util;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aryagami.data.CacheNewOrderData;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;

import java.util.ArrayList;
import java.util.List;

public class MyBroadCastReceiver extends BroadcastReceiver {
    ProgressDialog progressDialog;
    int pdfDocUploadedCount = 0;
    int passportUploadSuccessCount = 0;
    int fingerprintUploadSuccessCount = 0;
    Context activity ;
    Boolean isUpdated = false;
    List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
    List<NewOrderCommand> updateOrdersList = new ArrayList<NewOrderCommand>();
    List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
    UserRegistration.UserDocCommand userDocsArray[];
    NewOrderCommand updateOrdersArray[];


    @Override
    public void onReceive(Context context, Intent intent) {

           this.activity = context;

//UPDATE NEW ORDER CACHE DATA

       /* *** commandList= CacheNewOrderData.loadNewOrderCacheList(context);

        if(commandList != null)
            if(commandList.size() != 0){
                NewOrderCommand[] orderCommandArray = new NewOrderCommand[commandList.size()];
                commandList.toArray(orderCommandArray);
                    for(int i=0;i<commandList.size(); i++) {
                        updateNewOrderCacheData(i,orderCommandArray[i]);
                    }
            }*/

       //UPDATE UPDATE USER CACHE DATA
       // *** uploadPendingUploadedData();

            //ReUpload Documents
        // reUploadUserDocuments();

    }

    private void reUploadUserDocuments() {
        userDocCommandList = CacheNewOrderData.loadUnUploadedDocList(activity);
        if(userDocCommandList != null)
            if(userDocCommandList.size() !=0){
                userDocsArray = new UserRegistration.UserDocCommand[userDocCommandList.size()];
                userDocCommandList.toArray(userDocsArray);
                for(int i = 0 ; i<= userDocCommandList.size(); i++){
                     uploadPendingDocument(i, userDocsArray[i]);
                }
            }

    }

    private void uploadPendingDocument(final int i, final UserRegistration.UserDocCommand docData) {
        if(docData != null){
            final String fileUrl = "documents/" + docData.docType + "/" + docData.userId + "/," + docData.docFiles;

            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

            uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin login1 = (UserLogin)data.get(0);
                    if(login1.status.equals("success")) {
                        removeUploadedDoc(activity, i);
                    }else{
                        MyToast.makeMyToast(activity, "Company Documents not Uploaded.", Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    MyToast.makeMyToast(activity, "Document Not Uploaded successfully, Please upload Documents. STATUS" + status + "     ERROR" + error, Toast.LENGTH_LONG);
                }
            });
        }

    }

    private void removeUploadedDoc(Context activity, int i) {

    }

    private void uploadPendingUploadedData() {

        updateOrdersList= CacheNewOrderData.loadUpdateUserCacheList(activity);

        if(updateOrdersList != null)
            if(updateOrdersList.size() != 0){
                updateOrdersArray = new NewOrderCommand[updateOrdersList.size()];
                updateOrdersList.toArray(updateOrdersArray);

                for(int i=0;i<updateOrdersList.size(); i++) {
                    updatePendingOrdersData(i,updateOrdersArray[i]);
                }
            }

    }

    private void updatePendingOrdersData(final int position, final NewOrderCommand rowItem) {
        RestServiceHandler serviceHandler = new RestServiceHandler();

        try {
            serviceHandler.postUpdateUser(rowItem.userInfo, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin orderDetails = (UserLogin) data.get(0);
                    if (orderDetails.status.equals("success")) {
                        uploadPendingDocuments(rowItem);
                        deleteUpdatedOrderData(activity,position);

                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void uploadPendingDocuments(NewOrderCommand rowItem) {
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        if (rowItem.userInfo.userDocs != null)
            if (rowItem.userInfo.userDocs.size() != 0) {
                userDocCommandList = rowItem.userInfo.userDocs;
            }
        final List<UserRegistration.UserDocCommand> profileList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> activationList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> ninList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> passportList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> visaList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> pdfList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> refugeeList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> fingerPrintsList = new ArrayList<UserRegistration.UserDocCommand>();


        if (rowItem.userInfo.registrationType.equals("company")) {
            for (UserRegistration.UserDocCommand docCommand : userDocCommandList) {
                switch (docCommand.docFormat) {
                    case "pdf":
                        pdfList.add(docCommand);
                        break;
                }
                switch (docCommand.docType) {
                    case "activation":
                        activationList.add(docCommand);
                        break;
                }
            }
        }else if(rowItem.userInfo.registrationType.equals("personal")){
            for (UserRegistration.UserDocCommand docCommand : userDocCommandList) {
                switch (docCommand.docType) {
                    case "profile":
                        profileList.add(docCommand);
                        break;
                    case "activation":
                        activationList.add(docCommand);
                        break;
                    case "nin":
                        ninList.add(docCommand);
                        break;
                    case "passport":
                        passportList.add(docCommand);
                        break;
                    case "refugee":
                        refugeeList.add(docCommand);
                        break;
                    case "visa":
                        visaList.add(docCommand);
                        break;
                    case "fingerprints":
                        fingerPrintsList.add(docCommand);
                        break;
                }
            }
        }

        if (rowItem.userInfo.registrationType.equals("company")) {
            if (pdfList != null || pdfList.size() != 0)
                for (UserRegistration.UserDocCommand docData : pdfList) {

                    if (docData != null) {
                        final String[] file =docData.docFiles.split(";");
                        final String filename = file[0];

                        String fileUrl = "documents/" + docData.docType + "/" + rowItem.userInfo.userId + "/," + filename;
                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                        uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin login1 = (UserLogin) data.get(0);
                                if (login1.status.equals("success")) {
                                    //if (++pdfDocUploadedCount == pdfList.size()) {
                                        MyToast.makeMyToast(activity, filename+" Documents Uploaded Successfully.", Toast.LENGTH_LONG);
                                   // }
                                } else {

                                    MyToast.makeMyToast(activity, filename+" Documents not Uploaded.", Toast.LENGTH_LONG);
                                }

                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Documents Not Uploaded successfully,Please upload Documents. STATUS" + status + "     ERROR" + error, Toast.LENGTH_LONG);
                              //  activity.finish();

                            }
                        });
                    }
                }

            if (activationList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/activation/" + rowItem.userInfo.userId + "/";


                if (activationList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : activationList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == activationList.size()) {
                                        activationList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
        } else if (rowItem.userInfo.registrationType.equals("personal")) {

            // upload NIN Images

            if (ninList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/nin/" + rowItem.userInfo.userId + "/";

                if (ninList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : ninList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == ninList.size()) {
                                        ninList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "NIN doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            // Upload Profile

            if (profileList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/profile/" + rowItem.userInfo.userId + "/";

                if (profileList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : profileList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == profileList.size()) {
                                        profileList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Profile doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            // upload Activation Doc
            if (activationList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/activation/" + rowItem.userInfo.userId + "/";


                if (activationList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : activationList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == activationList.size()) {
                                        activationList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            //upload Passport
            if (passportList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/passport/" + rowItem.userInfo.userId + "/";


                if (passportList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : passportList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == passportList.size()) {
                                        passportList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "passport doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            // upload Visa

            if (visaList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/visa/" + rowItem.userInfo.userId + "/";

                if (visaList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : visaList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == visaList.size()) {
                                        visaList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Visa doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            // upload Refugee
            if (refugeeList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/refugee/" + rowItem.userInfo.userId + "/";

                if (refugeeList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : refugeeList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == refugeeList.size()) {
                                        refugeeList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            if(fingerPrintsList.size() != 0){
                String passportPicDir = ":documents/fingerprints/" + rowItem.userInfo.userId + "/";

                for(UserRegistration.UserDocCommand docCommand: fingerPrintsList){

                    String[] file =docCommand.docFiles.split(";");
                    String filename = file[0];

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docCommand.imageData, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            if(++fingerprintUploadSuccessCount ==fingerPrintsList.size()){
                                fingerPrintsList.clear();
                                MyToast.makeMyToast(activity,"Fingerprint documents are uploaded successfully.", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            MyToast.makeMyToast(activity,"Status:"+status+"\n Error"+error, Toast.LENGTH_SHORT);
                        }
                    });
                }

            }


        }
    }


    private void deleteUpdatedNewOrderData(Context context, int pos) {

        if(commandList != null)
            if(commandList.size() != 0){
                commandList.remove(pos);
                CacheNewOrderData.savedNewOrderData(context,commandList);
            }
    }

    private void deleteUpdatedOrderData(Context context, int pos) {

        if(updateOrdersList != null)
            if(updateOrdersList.size() != 0){
                updateOrdersList.remove(pos);
                CacheNewOrderData.savedOrderData(context,updateOrdersList);
            }
    }

    private void updateNewOrderCacheData(final int position, final NewOrderCommand orderCommand) {

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.postNewOrder(orderCommand, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin orderDetails = (UserLogin) data.get(0);
                    if (orderDetails.status.equals("success")) {

                       // uploadDocuments(position,orderCommand, orderDetails.userId);
                      //  deleteUpdatedNewOrderData(activity,position);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReportFromREST(Constants.emailId,"STATUS:"+status+"ERROR:"+error,"Cache_new_Order");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            BugReport.postBugReportFromREST(Constants.emailId,"Message:"+e.getMessage()+",\nCause:"+e.getCause(),"Cache_new_Order");
        }

    }

    private void uploadDocuments(int position, NewOrderCommand rowItem, String userId) {
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        if (rowItem.userInfo.userDocs != null)
            if (rowItem.userInfo.userDocs.size() != 0) {
                userDocCommandList = rowItem.userInfo.userDocs;
            }
        final List<UserRegistration.UserDocCommand> profileList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> activationList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> ninList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> passportList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> visaList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> pdfList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> refugeeList = new ArrayList<UserRegistration.UserDocCommand>();
        final List<UserRegistration.UserDocCommand> fingerPrintsList = new ArrayList<UserRegistration.UserDocCommand>();

        if (rowItem.userInfo.registrationType.equals("company")) {
            for (UserRegistration.UserDocCommand docCommand : userDocCommandList) {
                switch (docCommand.docFormat) {
                    case "pdf":
                        pdfList.add(docCommand);
                        break;
                }
                switch (docCommand.docType) {
                    case "activation":
                        activationList.add(docCommand);
                        break;
                }
            }
        }else if(rowItem.userInfo.registrationType.equals("personal")){
            for (UserRegistration.UserDocCommand docCommand : userDocCommandList) {
                switch (docCommand.docType) {
                    case "profile":
                        profileList.add(docCommand);
                        break;
                    case "activation":
                        activationList.add(docCommand);
                        break;
                    case "nin":
                        ninList.add(docCommand);
                        break;
                    case "passport":
                        passportList.add(docCommand);
                        break;
                    case "refugee":
                        refugeeList.add(docCommand);
                        break;
                    case "visa":
                        visaList.add(docCommand);
                        break;
                    case "fingerprints":
                        fingerPrintsList.add(docCommand);
                        break;
                }
            }
        }

        if (rowItem.userInfo.registrationType.equals("company")) {
            if (pdfList != null || pdfList.size() != 0)
                for (UserRegistration.UserDocCommand docData : pdfList) {

                    if (docData != null) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        String fileUrl = "documents/" + docData.docType + "/" + userId + "/," + filename;
                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                        uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin login1 = (UserLogin) data.get(0);
                                if (login1.status.equals("success")) {
                                    if (++pdfDocUploadedCount == pdfList.size()) {
                                        pdfList.clear();
                                        MyToast.makeMyToast(activity, "Company Documents Uploaded Successfully.", Toast.LENGTH_LONG);
                                    }
                                } else {

                                    MyToast.makeMyToast(activity, "Company Documents not Uploaded.", Toast.LENGTH_LONG);
                                }

                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                // ProgressDialogUtil.stopProgressDialog(progressDialog);

                                MyToast.makeMyToast(activity, "Documents Not Uploaded successfully,Please upload Documents. STATUS" + status + "     ERROR" + error, Toast.LENGTH_LONG);


                            }
                        });
                    }
                }

            if (activationList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/activation/" + userId + "/";


                if (activationList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : activationList) {
                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];
                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == activationList.size()) {
                                        activationList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
        } else if (rowItem.userInfo.registrationType.equals("personal")) {

            // upload NIN Images

            if (ninList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/nin/" + userId + "/";

                if (ninList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : ninList) {
                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];
                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == ninList.size()) {
                                        ninList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "NIN doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            // Upload Profile

            if (profileList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/profile/" + userId + "/";

                if (profileList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : profileList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == profileList.size()) {
                                        profileList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Profile doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            // upload Activation Doc
            if (activationList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/activation/" + userId + "/";


                if (activationList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : activationList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == activationList.size()) {
                                        activationList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            //upload Passport
            if (passportList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/passport/" + userId + "/";


                if (passportList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : passportList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == passportList.size()) {
                                        passportList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "passport doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }
            // upload Visa

            if (visaList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/visa/" + userId + "/";

                if (visaList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : visaList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == visaList.size()) {
                                        visaList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Visa doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            // upload Refugee
            if (refugeeList.size() != 0) {
                int passportDocsNum = 0;
                String passportPicDir = ":documents/refugee/" + userId + "/";

                if (refugeeList.size() != 0)
                    for (UserRegistration.UserDocCommand docData : refugeeList) {

                        String[] file =docData.docFiles.split(";");
                        String filename = file[0];

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docData.imageData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    if (++passportUploadSuccessCount == refugeeList.size()) {
                                        refugeeList.clear();
                                        MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                    }

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                //   ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Activation doc not uploaded." + ", " + status, Toast.LENGTH_LONG);

                            }
                        });
                    }
            }

            if(fingerPrintsList.size() != 0){
                String passportPicDir = ":documents/fingerprints/" + userId + "/";

                for(UserRegistration.UserDocCommand docCommand: fingerPrintsList){

                    String[] file =docCommand.docFiles.split(";");
                    String filename = file[0];

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, docCommand.imageData, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            if(++fingerprintUploadSuccessCount ==fingerPrintsList.size()){
                                fingerPrintsList.clear();
                                MyToast.makeMyToast(activity,"Fingerprint documents are uploaded successfully.", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            MyToast.makeMyToast(activity,"Status:"+status+"\n Error"+error, Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        }

    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
