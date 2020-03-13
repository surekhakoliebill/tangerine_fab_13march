package com.aryagami.data;

/**
 * Created by aryagami on 22/5/16.
 */
public interface DataModel {


    public enum DataType {
        UserRegistration,
        UserLogin,
        PlanGroup,
        DocumentTypes,
        Subscription,
        NewOrderCommand,
        SubscriptionCommand,
        Account,
        ActivateCommand,
        WalletAccountVo,
        UserInfo,
        Roles,
        GetOrderNumberDetails,
        WarehouseBinLotVo,
        ResellerVoucherType,
        DeviceOrder,
        ResellerLoginInfo,
        ResellerStaff,
        ApproveReseller,
        SimSwapList,
        SimReplacementForm,
        SimDocumentUpload,
        ServiceBundleDetailVo,
        GetAllInfo,
        VoucherTypesVo,
        ResellerRequestCommand,
        ProductRequestCommand,
        WalletAccountTransactionLogVo,
        ResellerRequestVo,
        BugReportCommand;
    }
    public DataType getDataType();

}
