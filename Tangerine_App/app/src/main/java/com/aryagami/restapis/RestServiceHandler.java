package com.aryagami.restapis;

import android.os.AsyncTask;
import android.os.Build;
import com.aryagami.data.Account;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.AirtimeValue;
import com.aryagami.data.ApproveReseller;
import com.aryagami.data.BugReportCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.DocumentTypes;
import com.aryagami.data.GetAllInfo;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.OrderNumberDetails;
import com.aryagami.data.PlanGroup;
import com.aryagami.data.ProductRequestCommand;
import com.aryagami.data.ResellerLoginInfo;
import com.aryagami.data.ResellerRequestCommand;
import com.aryagami.data.ResellerRequestVo;
import com.aryagami.data.ResellerStaff;
import com.aryagami.data.ResellerVoucherType;
import com.aryagami.data.Roles;
import com.aryagami.data.ServiceBundleDetailVo;
import com.aryagami.data.SimReplacementForm;
import com.aryagami.data.SimSwapList;
import com.aryagami.data.Subscription;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserInfo;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.data.VoucherTypesVo;
import com.aryagami.data.WalletAccountVo;
import com.aryagami.data.WarehouseBinLotVo;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.util.BugReport;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class
RestServiceHandler extends AsyncTask<Void, Void, Void> {


    static Map<String, List<RestServiceHandler>> outstandingServicesTable = new HashMap<>();
    private API api;
    private int method;
    private String url;
    private String params, params1;
    private Callback callback;
    private String response;
    private String data;
    private CacheCallback cachecallback;


    public enum ErrorCode {
        HTTP_ERROR
    }

    public interface CacheCallback {
        void cacheResponse(String responseString);

        void success(DataModel.DataType type, List<DataModel> data);

        void failure(ErrorCode error, String status);
    }

    public interface Callback {
        void success(DataModel.DataType type, List<DataModel> data);

        void failure(ErrorCode error, String status);
    }

    public enum API {
        UPLOAD_PICTURE,
        USER_SIGN_IN,
        GET_RESELLER_LOGIN_INFO,
        CHANGE_PASSWORD,
        POST_ACTIVATE_SUBSCRIPTION_STATUS,
        POST_DEACTIVATE_SUBSCRIPTION_STATUS,
        GET_DOCUMENT_TYPES,
        GET_ALL_USERS_BY_USERNAME,
        GET_ACCOUNT_DETAILS,
        GET_SUBSCRIPTIONS,
        GET_SUBSCRIPTION_BY_SERVEDMSISDN,
        CHECK_ISDN_ASSIGNMENT,
        CHECK_IMEI_AVAILABILITY,
        POST_AGGREGATOR_TOPUP,
        GET_PLANGROUP_BY_ID,
        LOGOUT,
        POST_USER_NIRA_VERFICATION,
        CHECK_SUBSCRIPTION,
        CHANGE_SUBSCRIBER_SIM,
        NEW_ORDER,
        CHECK_EXISTING_USER,
        GENERATE_REPORT_ETOPUP,
        DOCUMENT_UPLOAD_COMPLETE,
        GET_USER_REGISTERATION,
        GET_STAFF_USER_REGISTRATION,
        POST_APPROVE_RESELLER,
        POST_SIM_REPLACEMENT_FORM,
        GET_LIST_VOUCHER_TYPES,
        POST_REDEEM_VOUCHER,
        GET_ALL_ROLES,
        POST_FINISH_ORDER,
        GET_ORDER_NUMBER,
        GET_RESELLER_GODOWN_STOCK,
        GET_ALL_VOUCHERS_TYPES,
        POST_RESELLER_REQUEST,
        GET_RESELLER_WAREHOUSE_STOCK_BY_ID,
        GET_RESELLER_WALLET_BALANCE,
        GET_ALL_SIM,
        BUG_REPORT,
        POST_RESELLER_SERVICE_BUNDLE,
        GET_STAFF_BY_RESELLER_ID,
        POST_UPLOAD_PDF_SIM_SWAP,
        GET_AVAILABLE_MSISDN_LIST,
        CHECK_SPECIAL_NUMBER,
        GET_SIM_ICCID_LIST,
        GET_ETOPUP_REQUESTS_FROM_RESELLER,
        GET_VOUCHERS_REQUESTS_FROM_RESELLER,
        GET_PRODUCT_REQUESTS_FROM_RESELLER,
        POST_UPDATE_USER,
        GET_AIRTIME_VALUE_RECHARGE,
        GET_TODAYS_RESELLER_RECHARGES,
        GET_USER_PLANS_BY_ID;
    }


    @Override
    protected Void doInBackground(Void... params) {
        doServiceCall();
        return null;
    }

    private void executeService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (response == null)
            return;

        if (response.contains("Error-Code")) {
            if(callback != null) {
                callback.failure(ErrorCode.HTTP_ERROR, response);
            }else if(cachecallback != null){
                cachecallback.failure(ErrorCode.HTTP_ERROR, response);
            }
            return;
        }

        switch (api) {
            case GET_USER_REGISTERATION:
            case GET_STAFF_USER_REGISTRATION:
            case POST_USER_NIRA_VERFICATION:
            case CHECK_SUBSCRIPTION:
            case NEW_ORDER:
            case POST_FINISH_ORDER:
            case POST_ACTIVATE_SUBSCRIPTION_STATUS:
            case POST_DEACTIVATE_SUBSCRIPTION_STATUS:
            case USER_SIGN_IN:
            case CHANGE_SUBSCRIBER_SIM:
            case POST_UPDATE_USER:
            case POST_AGGREGATOR_TOPUP:
            case POST_APPROVE_RESELLER:
            case DOCUMENT_UPLOAD_COMPLETE:
            case POST_RESELLER_REQUEST:
            case GENERATE_REPORT_ETOPUP:
            case BUG_REPORT:
            case CHECK_EXISTING_USER:
            case POST_RESELLER_SERVICE_BUNDLE:
                try {
                    List<DataModel> login = UserLogin.parseJSONResponse(response);
                    callback.success(DataModel.DataType.UserLogin, login);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case CHECK_ISDN_ASSIGNMENT:
                try {
                    List<DataModel> subscriptionCommand = SubscriptionCommand.parseSubscriptionJSONInput(response);
                    callback.success(DataModel.DataType.SubscriptionCommand, subscriptionCommand);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_SUBSCRIPTION_BY_SERVEDMSISDN:
                try {
                    List<DataModel> subscriptionCommand = SubscriptionCommand.parseSubscriptionJSON(response);
                    callback.success(DataModel.DataType.SubscriptionCommand, subscriptionCommand);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_RESELLER_LOGIN_INFO:
                try {
                    List<DataModel> login = ResellerLoginInfo.parseJSONResponseforResellerId(response);
                    callback.success(DataModel.DataType.ResellerLoginInfo, login);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_ALL_SIM:
                try {
                    List<DataModel> simList = SimSwapList.parseJSONResponse(response);
                    callback.success(DataModel.DataType.SimSwapList, simList);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case CHANGE_PASSWORD:
                try {
                    List<DataModel> userInfo = (List<DataModel>) UserInfo.parseJSONResponseOfChangePassword(response);
                    callback.success(DataModel.DataType.UserRegistration, userInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_ORDER_NUMBER:
                try {
                    List<DataModel> orderDetails = OrderNumberDetails.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.GetOrderNumberDetails, orderDetails);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_RESELLER_WAREHOUSE_STOCK_BY_ID:
                try {
                    List<DataModel> warehouseStockList = WarehouseBinLotVo.parseJSONResponse(response);
                    callback.success(DataModel.DataType.WarehouseBinLotVo, warehouseStockList);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case POST_REDEEM_VOUCHER:
                try {
                    List<DataModel> subscriptionInfo = SubscriptionCommand.parseJSONResponse(response);
                    callback.success(DataModel.DataType.SubscriptionCommand, subscriptionInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_ALL_ROLES:
                try {
                    List<DataModel> roles = Roles.parseJSONResponse(response);
                    callback.success(DataModel.DataType.Roles, roles);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_LIST_VOUCHER_TYPES:
                try {
                    List<DataModel> voucherTypes = ResellerVoucherType.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.ResellerVoucherType, voucherTypes);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_ALL_USERS_BY_USERNAME:
                try {
                    List<DataModel> userRegistration = GetAllInfo.parseJSONResponse(response);
                    callback.success(DataModel.DataType.GetAllInfo, userRegistration);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_USER_PLANS_BY_ID:
            case GET_PLANGROUP_BY_ID:
                try {
                    List<DataModel> plansGroup = PlanGroup.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.PlanGroup, plansGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_DOCUMENT_TYPES:
                try {
                    List<DataModel> documentTypes = DocumentTypes.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.DocumentTypes, documentTypes);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case LOGOUT:

                cachecallback.cacheResponse(response);
                break;

            case GET_RESELLER_WALLET_BALANCE:
                try {
                    List<DataModel> walletInfo = WalletAccountVo.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.WalletAccountVo, walletInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_ACCOUNT_DETAILS:
                try {
                    List<DataModel> accountDetails = Account.parseJSONResponseArray(response);
                    callback.success(DataModel.DataType.Account, accountDetails);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_SUBSCRIPTIONS:
                try {
                    List<DataModel> userSubscriptions = Subscription.getSubscriptionArray(response);
                    callback.success(DataModel.DataType.Subscription, userSubscriptions);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case POST_UPLOAD_PDF_SIM_SWAP:
            case UPLOAD_PICTURE:
                // callback.success(DataModel.DataType.DataNull, null);
                try {
                    List<DataModel> login = UserLogin.parseJSONResponse(response);
                    callback.success(DataModel.DataType.UserLogin, login);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_RESELLER_GODOWN_STOCK:
                try {
                    List<DataModel> deviceOrder = DeviceOrder.parseJSONResponse(response);
                    callback.success(DataModel.DataType.DeviceOrder, deviceOrder);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_ALL_VOUCHERS_TYPES:
                try {
                    List<DataModel> voucherTypes = VoucherTypesVo.parseJSONResponse(response);
                    callback.success(DataModel.DataType.VoucherTypesVo, voucherTypes);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_AIRTIME_VALUE_RECHARGE:
                try {
                    List<DataModel> airtimeProducts = AirtimeValue.parseAirtimeValueListJSONResponse(response);
                    callback.success(DataModel.DataType.AirtimeValue, airtimeProducts);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case CHECK_IMEI_AVAILABILITY:
                try {
                    List<DataModel> deviceOrder = DeviceOrder.parseIMEIResponse(response);
                    callback.success(DataModel.DataType.DeviceOrder, deviceOrder);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_STAFF_BY_RESELLER_ID:
                try {
                    List<DataModel> resellerStaff = ResellerStaff.resellerStaffParseJSONResponse(response);
                    callback.success(DataModel.DataType.ResellerStaff, resellerStaff);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case POST_SIM_REPLACEMENT_FORM:
                try {
                    List<DataModel> simReplacement = SimReplacementForm.parseSimJSONResponse(response);
                    callback.success(DataModel.DataType.SimReplacementForm, simReplacement);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case CHECK_SPECIAL_NUMBER:
                try {
                    List<DataModel> simReplacement = SimReplacementForm.parseSpecialNumberJSONResponse(response);
                    callback.success(DataModel.DataType.SimReplacementForm, simReplacement);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_AVAILABLE_MSISDN_LIST:
                try {
                    List<DataModel> simReplacement = SimReplacementForm.parseAvailableMSISDNListJSONResponse(response);
                    callback.success(DataModel.DataType.SimReplacementForm, simReplacement);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;

            case GET_SIM_ICCID_LIST:
                try {
                    List<DataModel> simIccidList = ServiceBundleDetailVo.parseSimIccidListJSONResponse(response);
                    callback.success(DataModel.DataType.ServiceBundleDetailVo, simIccidList);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_PRODUCT_REQUESTS_FROM_RESELLER:
            case GET_ETOPUP_REQUESTS_FROM_RESELLER:
                try {
                    List<DataModel> etopupRequest = WalletAccountTransactionLogVo.parseETopupRequestsJSONResponse(response);
                    callback.success(DataModel.DataType.WalletAccountTransactionLogVo, etopupRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_VOUCHERS_REQUESTS_FROM_RESELLER:
                try {
                    List<DataModel> vouchersRequestsList = ResellerRequestVo.parseVouchersRequestsJSONResponse(response);
                    callback.success(DataModel.DataType.ResellerRequestVo, vouchersRequestsList);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;
            case GET_TODAYS_RESELLER_RECHARGES:
                try {
                    List<DataModel> resellerRechargesList = ActivateCommand.parseResellerRechargesJSONResponse(response);
                    callback.success(DataModel.DataType.ActivateCommand, resellerRechargesList);
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReportFromREST(Constants.emailId," "+e.getMessage()+" "+e.getCause(),"RestServiceHandler");
                }
                break;


            default:
        }
    }

    private void doServiceCall() {
        HttpHandler handler = new HttpHandler();

        if (method == HttpHandler.POST_MULTIPART) {
            if (params1 != null) {
                response = handler.makeServicePdfCall(url, method, params, params1, data);
            } else {
                response = handler.makeServiceCall(url, method, params, data);
            }
        } else {
            response = handler.makeServiceCall(url, method, params);
        }

    }

    public void uploadImage(String fileUrl, String data, Callback cback) {
        callback = cback;
        api = API.UPLOAD_PICTURE;
        url = Constants.serviceUrl + "image_upload/";
        method = HttpHandler.POST_MULTIPART;
        params = fileUrl;
        this.data = data;
        executeService();
    }

    public void uploadPdf(String fileFormat, String fileUrl, String data, Callback cback) {
        callback = cback;
        api = API.UPLOAD_PICTURE;
        url = Constants.serviceUrl + "document_upload/";
        method = HttpHandler.POST_MULTIPART;
        params = fileUrl;
        params1 = fileFormat;
        this.data = data;
        executeService();
    }

    public void uploadSimSwapPdf(String userId, List<UserRegistration.UserDocCommand> docCommand, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_UPLOAD_PDF_SIM_SWAP;
        url = Constants.serviceUrl + "upload_sim_swap_docs/" + userId + "/";
        method = HttpHandler.POST;
        params = UserRegistration.UserDocCommand.getSimSwapDocCommandJSONArray(docCommand);
        executeService();
    }


    public void userLogin(UserLogin login, Callback cback) throws IOException {
        callback = cback;
        api = API.USER_SIGN_IN;
        //url = Constants.serviceUrl + "user_signin/";
        //  url = Constants.serviceUrl + "admin_signin/";
        url = Constants.serviceUrl + "reseller_signin/";
        method = HttpHandler.POST;
        params = login.getUserRequestJSON();
        executeService();
    }

    public void getListVoucherTypes(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_LIST_VOUCHER_TYPES;
        url = Constants.serviceUrl + "list_voucher_types/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void redeemVoucherDirect(ResellerVoucherType resellerVoucherType, Callback cback) throws IOException {
        callback = cback;
        //api = API.POST_REDEEM_VOUCHER;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "redeem_voucher_direct/";
        method = HttpHandler.POST;
        params = resellerVoucherType.getUserInfoJSON();
        executeService();
    }

    public void getAllRoles(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ALL_ROLES;
        url = Constants.serviceUrl + "roles/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerLoginInfo(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_RESELLER_LOGIN_INFO;
        url = Constants.serviceUrl + "user_signin/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void checkSubscription(String servedMSISDN, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_SUBSCRIPTION;
        url = Constants.serviceUrl + "check_subscription/" + servedMSISDN + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postSIMSwap(UserLogin userLogin, Callback cback) throws IOException {
        callback = cback;
        api = API.CHANGE_SUBSCRIBER_SIM;
        url = Constants.serviceUrl + "request_sim_swap/";
        method = HttpHandler.POST;
        params = userLogin.getSimRequestJSON();
        executeService();
    }

    public void getPdfDocument(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_DOCUMENT_TYPES;
        url = Constants.serviceUrl + "get_document_types/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getAllUsersByUsername(String name, String userName, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ALL_USERS_BY_USERNAME;
        url = Constants.serviceUrl + "all_users_by_search/" + name + "/" + userName + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getAccountDetails(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ACCOUNT_DETAILS;
        url = Constants.serviceUrl + "get_active_consumer_accounts/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getMobileMoneyAccountDetails(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ACCOUNT_DETAILS;
        url = Constants.serviceUrl + "get_mblmny_accounts/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postUserRegisteration(UserRegistration userRegistration, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_USER_REGISTERATION;
        url = Constants.serviceUrl + "add_user/";
        method = HttpHandler.POST;
        params = userRegistration.getUserInfoJSON();
        executeService();
    }

    public void getResellerGodownStock(String aggregator, Callback cback) {
        callback = cback;
        api = API.GET_RESELLER_GODOWN_STOCK;
        // url = Constants.serviceUrl + "get_reseller_godown_stock_availability/" + aggregator + "/";
        url = Constants.serviceUrl + "get_reseller_godown_nonbundled_stock_availability/" + aggregator + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerProductStock(String aggregator, Callback cback) {
        callback = cback;
        api = API.GET_RESELLER_GODOWN_STOCK;
        url = Constants.serviceUrl + "get_reseller_godown_stock_availability/" + aggregator + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerVouchersStock(String aggregator, Callback cback) {
        callback = cback;
        api = API.GET_ALL_VOUCHERS_TYPES;
        url = Constants.serviceUrl + "reseller_aggregator_vouchers/" + aggregator + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postResellerRequest(ResellerRequestCommand resellerRequestCommand, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_RESELLER_REQUEST;
        url = Constants.serviceUrl + "reseller_request/";
        method = HttpHandler.POST;
        params = resellerRequestCommand.getResellerRequestJSON();
        executeService();
    }

    public void postProductRequest(List<ProductRequestCommand> requestCommand, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_RESELLER_REQUEST;
        url = Constants.serviceUrl + "create_main_godown_movement_request/";
        method = HttpHandler.POST;
        params = ProductRequestCommand.getProductRequestJSONArray(requestCommand);
        executeService();
    }
    public void postETopupRequest(String resellerId, Float transferAmount, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_RESELLER_REQUEST;
        url = Constants.serviceUrl + "reseller_request_wallet_transfer/"+resellerId + "/" + transferAmount;
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void postStaffUserRegistration(UserRegistration userRegistration, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_STAFF_USER_REGISTRATION;
        url = Constants.serviceUrl + "add_reseller_staff/";
        method = HttpHandler.POST;
        params = userRegistration.postStaffUserInfoJSON();
        executeService();
    }

    public void postNewOrder(NewOrderCommand newOrderCommand, Callback cback) throws Exception {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "new_order/";
        method = HttpHandler.POST;

        if (newOrderCommand.isNewAccount) {
            if (newOrderCommand.userInfo.registrationType.equalsIgnoreCase("retailer")) {
                params = newOrderCommand.getRetailerNewOrderJSON();

            } else {
                params = newOrderCommand.getNewOrderJSON();
            }
        } else {
            params = newOrderCommand.getNewOrderForExistingJSON();
        }

        executeService();
    }

    public void postDocumentUploadComplete(String userId, Callback cback) throws Exception {
        callback = cback;
        api = API.DOCUMENT_UPLOAD_COMPLETE;
        url = Constants.serviceUrl + "user_document_upload_complete/"+userId;
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void postDeviceNewOrder(NewOrderCommand newOrderCommand, Callback cback) throws Exception {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "new_order/";
        method = HttpHandler.POST;
        params = newOrderCommand.postDeviceOrderJSON();
        executeService();
    }

    public void postStaffNewOrder(NewOrderCommand newOrderCommand, Callback cback) throws Exception {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "new_order/";
        method = HttpHandler.POST;
        params = newOrderCommand.getStaffNewOrderJSON();
        executeService();
    }

    public void postUpdateUser(UserRegistration registration, Callback cback) throws Exception {
        callback = cback;
        api = API.POST_UPDATE_USER;
        url = Constants.serviceUrl + "update_user/";
        method = HttpHandler.POST;
        params = registration.getUserInfoJSON();
        executeService();
    }

    public void postUpdateVisaValidity(UserRegistration registration, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_UPDATE_USER;
        url = Constants.serviceUrl + "/update_visa_expiry/";
        method = HttpHandler.POST;
        params = registration.getUserForeignUserInfoJSON();
        // params = registration.getVisaExpiryJSON();
        executeService();
    }

    public void postUserNiraVerification(UserRegistration userRegistration, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_USER_NIRA_VERFICATION;
        url = Constants.serviceUrl + "verify_nira_info/";
        method = HttpHandler.POST;
        params = userRegistration.getUserNiraVerification();
        executeService();
    }

    public void changePassword(UserInfo userInfo, Callback cback) throws IOException {
        callback = cback;
        api = API.CHANGE_PASSWORD;
        url = Constants.serviceUrl + "change_user_password/";
        // url = Constants.serviceUrl + "update_existing_password/";
        method = HttpHandler.POST;
        params = userInfo.postJSONInfoForChangePassword();
        executeService();
    }

    public void activateSubscriptionStatus(ActivateCommand command, Callback cback) throws Exception {
        callback = cback;
        api = API.POST_ACTIVATE_SUBSCRIPTION_STATUS;
        url = Constants.serviceUrl + "status_activate/";
        method = HttpHandler.POST;
        params = command.getActivateJSON();
        executeService();
    }

    public void deactivateSubscriptionStatus(ActivateCommand command, Callback cback) throws Exception {
        callback = cback;
        api = API.POST_DEACTIVATE_SUBSCRIPTION_STATUS;
        url = Constants.serviceUrl + "deactivate/";
        method = HttpHandler.POST;
        params = command.getActivateJSON();
        executeService();
    }

    public void getUserAllSubscriptions(UserInfo userInfo, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_SUBSCRIPTIONS;
        url = Constants.serviceUrl + "all_user_subscriptions/" + userInfo.userId;
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getUserSubscriptionsByMSISDN(String servedMSISDN, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_SUBSCRIPTION_BY_SERVEDMSISDN;
        url = Constants.serviceUrl + "get_subscription/" + servedMSISDN + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }
    public void getCheckISDNServedMSISDN(String servedMSISDN, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_ISDN_ASSIGNMENT;
        url = Constants.serviceUrl + "check_isdn/" + servedMSISDN + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getCheckIccIdAvailability(String binRef, String iccid, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_ISDN_ASSIGNMENT;
        url = Constants.serviceUrl + "check_new_iccid_availability/" + binRef + "/" + iccid + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void getCheckIMEIAvailability(String binRef, Long productListingId, String imei, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_IMEI_AVAILABILITY;
        url = Constants.serviceUrl + "check_new_imie_availability/" + binRef + "/" + productListingId + "/"+ imei;
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void reserveMSISDN(String unreserveServedMSISDN, String reserveServedMSISDN, Callback cback) {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "reserve_msisdn/" + unreserveServedMSISDN + "/" + reserveServedMSISDN + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getAllAvailableMSISDNList(String limitCount, Callback cback) {
        callback = cback;
        api = API.GET_AVAILABLE_MSISDN_LIST;
        url = Constants.serviceUrl + "get_available_msisdns/" + limitCount + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getAllAvailableSpecialMSISDNList(String limitCount, Callback cback) {
        callback = cback;
        api = API.GET_AVAILABLE_MSISDN_LIST;
        url = Constants.serviceUrl + "get_special_numbers/" + limitCount + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }
    public void getCheckSpecialISDNServedMSISDN(String servedMSISDN, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_SPECIAL_NUMBER;
        url = Constants.serviceUrl + "search_special_numbers/" + servedMSISDN + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postAggregatorTopup(NewOrderCommand.LocationCoordinates coordinates,String resellerId, String subscriptionId, String etopupAmount, Boolean immediateRecharge,Callback cback) throws IOException {
        callback = cback;
        api = API.POST_AGGREGATOR_TOPUP;
        url = Constants.serviceUrl + "reseller_request_etopup/" + resellerId + "/" + subscriptionId + "/" + etopupAmount + "/" + immediateRecharge +"/";
        method = HttpHandler.POST;
        params = coordinates.getCoordinatesJSON();;
        executeService();
    }

    public void getPlanGroupByID(String planGroupID, Callback cback) {
        callback = cback;
        api = API.GET_PLANGROUP_BY_ID;
        url = Constants.serviceUrl + "plan_group_by_id/" + planGroupID + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerWarehouseStock(String resellerId, Callback cback) {
        callback = cback;
        api = API.GET_RESELLER_WAREHOUSE_STOCK_BY_ID;
        url = Constants.serviceUrl + "/get_reseller_warehouse_stock/" + resellerId + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void finishExitingOrder(NewOrderCommand postOrder, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_FINISH_ORDER;
        url = Constants.serviceUrl + "finish_order/";
        method = HttpHandler.POST;
        params = postOrder.postPostpaidExistingOrderJSON();
        executeService();
    }

    public void getOrderNumber(String orderNumber, Callback cback) {
        callback = cback;
        api = API.GET_ORDER_NUMBER;
        url = Constants.serviceUrl + "get_orderno/" + orderNumber + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerWalletBalance(String resellerId, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_RESELLER_WALLET_BALANCE;
        url = Constants.serviceUrl + "get_reseller_wallet/" + resellerId + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postApproveReseller(ApproveReseller approveReseller, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_APPROVE_RESELLER;
        url = Constants.serviceUrl + "approve_reseller/";
        method = HttpHandler.POST;
        params = approveReseller.getApproveResellerInfoJSON();
        executeService();
    }

    public void postReplaceMentForm(SimReplacementForm simReplacementForm, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_SIM_REPLACEMENT_FORM;
        url = Constants.serviceUrl + "/validate_ownership/";
        method = HttpHandler.POST;
        params = simReplacementForm.getApproveResellerInfoJSON();
        executeService();
    }

    public void postICCIDForSimSwap(SimReplacementForm replacementForm, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "sim_swap_process/";
        method = HttpHandler.POST;
        params = replacementForm.getJSONICCIDData();
        executeService();
    }

    public void postBugReport(BugReportCommand bugReportCommand, Callback cback) throws IOException {
        callback = cback;
        api = API.BUG_REPORT;
        url = Constants.serviceUrl + "/bug_invoke/";
        method = HttpHandler.POST;
        params = bugReportCommand.bugReportCommandJSON();
        executeService();
    }

    public void getStaffByReseller(String resellerId, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_STAFF_BY_RESELLER_ID;
        url = Constants.serviceUrl + "staff_by_resellerid/" + resellerId + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getAllSims(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ALL_SIM;
        url = Constants.serviceUrl + "/all_sim_swaps/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getSimIccidList(String binRef, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_SIM_ICCID_LIST;
        url = Constants.serviceUrl + "get_service_bundle_details/" + binRef + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void updateResellerLocation(NewOrderCommand.LocationCoordinates coordinates,String resellerId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "update_reseller_location/" + resellerId + "/";
        method = HttpHandler.POST;
        params = coordinates.getCoordinatesJSON();;
        executeService();
    }

    public void getETopupRequestsFromReseller(String resellerId, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_ETOPUP_REQUESTS_FROM_RESELLER;
        url = Constants.serviceUrl + "get_reseller_requests_from_wallet/" + resellerId +"/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerEtopupReports( String resellerId, String startDate, String endDate, Boolean generateReport, Callback cback) throws IOException{
        callback = cback;
        api = API.GENERATE_REPORT_ETOPUP;
        url = Constants.serviceUrlReport + "get_etopup_reseller_report/" +resellerId + "/"+ startDate + "/" + endDate + "/" + generateReport + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }


    public void getVouchersRequestsFromReseller(String resellerId, String startDate, String endDate, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_VOUCHERS_REQUESTS_FROM_RESELLER;
        url = Constants.serviceUrl + "reseller_transfer_requests_from_reseller/" + resellerId + "/" + startDate + "/"+ endDate + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getEtopupSalesReports( String resellerId, String startDate, String endDate, Boolean generateReport, Callback cback) throws IOException{
        callback = cback;
        api = API.GENERATE_REPORT_ETOPUP;
        url = Constants.serviceUrlReport + "get_etopup_sales_report/" +resellerId + "/"+ startDate + "/" + endDate + "/" + generateReport + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }


    public void getProductRequestsFromReseller(String resellerId, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_PRODUCT_REQUESTS_FROM_RESELLER;
        url = Constants.serviceUrl + "get_reseller_godown_movement_request_frombin/" + resellerId + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getVouchersSalesReports( String resellerId, String startDate, String endDate, Boolean generateReport, Callback cback) throws IOException{
        callback = cback;
        api = API.GENERATE_REPORT_ETOPUP;
        url = Constants.serviceUrlReport + "get_voucher_sales_report/" +resellerId + "/"+ startDate + "/" + endDate + "/" + generateReport + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }


    // get Reseller Requests
    public void getResellerStockRequestsList(String resellerId,String startDate, String endDate, Callback cback) throws IOException {
        callback = cback;
        api = API.GET_VOUCHERS_REQUESTS_FROM_RESELLER;
        url = Constants.serviceUrl + "get_requests_by_reseller_id/" + resellerId + "/all/" + startDate +"/" + endDate;
        method = HttpHandler.GET;
        params = null;
        executeService();

    }

    public void getSimSalesReports( String resellerId, String startDate, String endDate, Boolean generateReport, Callback cback) throws IOException{
        callback = cback;
        api = API.GENERATE_REPORT_ETOPUP;
        url = Constants.serviceUrlReport + "get_sim_sales_report/" +resellerId + "/"+ startDate + "/" + endDate + "/" + generateReport + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getVoucherAirtimeSalesReports( String resellerId, String startDate, String endDate, Boolean generateReport, Callback cback) throws IOException{
        callback = cback;
        api = API.GENERATE_REPORT_ETOPUP;
        url = Constants.serviceUrlReport + "get_voucher_airtime_sales_report/" +resellerId + "/"+ startDate + "/" + endDate + "/" + generateReport + "/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    // Approve Etopup request

    public void approveETopupRequest(String transactionId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "reseller_approve_wallet_transfer/" + transactionId + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void cancelETopupRequest(String transactionId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "cancel_reseller_etopup_request/" + transactionId + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void cancelResellerEVoucherOrAirtimeRequest(String requestId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "cancel_reseller_request/" + requestId + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }
    // Approve  & Reject Voucher Request by Reseller

    public void approveVoucherRequest( ResellerRequestVo requestVo, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "approve_reseller_voucher_transfer_request/" ;
        method = HttpHandler.POST;
        params = requestVo.postApproveVoucherRequestJson() ;
        executeService();
    }

    public void rejectProductRequest(String requestId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "reject_main_godown_movement_request/"+requestId+"/" ;
        method = HttpHandler.POST;
        params = null ;
        executeService();
    }
    public void cancelResellerProductRequest(String requestId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "cancel_main_godown_movement_request/"+requestId+"/" ;
        method = HttpHandler.POST;
        params = null ;
        executeService();
    }

    public void fulfillMainGodownProductRequest(String requestId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "fulfill_main_godown_movement_request/"+requestId+"/" ;
        method = HttpHandler.POST;
        params = null ;
        executeService();
    }

    public void finishProductRequest(String requestId, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "finish_godown_movement_request/"+requestId+"/" ;
        method = HttpHandler.POST;
        params = null ;
        executeService();
    }

    public void getPlanGroup(Callback cback) throws IOException {
        callback = cback;
        api = API.GET_USER_PLANS_BY_ID;
        url = Constants.serviceUrl + "get_all_plan_groups/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void postRefugeeVerification(UserRegistration userRegistration, Callback cback) throws IOException {
        callback = cback;
        api = API.POST_USER_NIRA_VERFICATION;
        url = Constants.serviceUrl + "validate_refugee/";
        method = HttpHandler.POST;
        params = userRegistration.postRefugeeVerificationJson();
        executeService();
    }

    public void postResellerServiceBundle(String resellerId, String subscriptionId, String planGroupId, Callback cback) throws IOException{
        callback = cback;
        api = API.POST_RESELLER_SERVICE_BUNDLE;
        url = Constants.serverURL + "reseller_service_bundle_topup" + resellerId + "/" + subscriptionId + "/" + planGroupId + "/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }


    public void updateDocument(UserRegistration.UserDocCommand docCommand, Callback cback) throws IOException {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "update_document/";
        method = HttpHandler.POST;
        params = docCommand.postUpdateDocumentJson();
        executeService();
    }

    public void checkExistingUser(String type, String idNumber, Callback cback) throws IOException {
        callback = cback;
        api = API.CHECK_EXISTING_USER;
        url = Constants.serviceUrl + "check_existing_user/"+ type + "/" + idNumber +"/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }

    public void getAirtimeProductsForRecharge(Callback cback) throws IOException{
        callback = cback;
        api = API.GET_AIRTIME_VALUE_RECHARGE;
        url = Constants.serviceUrl + "get_approved_airtime_products/";
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void getResellerRechargeSalesForToday(String resellerId, String startDate, Callback cback) throws IOException{
        callback = cback;
        api = API.GET_TODAYS_RESELLER_RECHARGES;
        url = Constants.serviceUrl + "reseller_sales/ALL/"+resellerId+"/"+startDate+"/"+startDate;
        method = HttpHandler.GET;
        params = null;
        executeService();
    }

    public void cancelRechargeRequest(ActivateCommand command, Callback cback) throws IOException {
        try {
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "recharge_cancel_request/";
        method = HttpHandler.POST;
        params = command.getCancelRechargeRequestJSON();
        executeService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postAirtimeDirectRecharge(String resellerId, String subscriptionId, float amount, Boolean immediateRecharge, Callback cback) throws IOException{
        callback = cback;
        api = API.NEW_ORDER;
        url = Constants.serviceUrl + "reseller_request_etopup_direct/"+ resellerId + "/"+ subscriptionId + "/" + amount + "/" +immediateRecharge;
        method = HttpHandler.POST;
        params = null;
        executeService();
    }
    public void logout(CacheCallback cback) throws IOException {
        cachecallback = cback;
        api = API.LOGOUT;
        url = Constants.serverURL + "/billing/logout/";
        method = HttpHandler.POST;
        params = null;
        executeService();
    }
}

