package com.aryagami.data;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RegistrationData implements Serializable {
    public static UserRegistration getRegistrationData() {
        return registrationData;
    }

    public static void setRegistrationData(UserRegistration registrationData) {
        RegistrationData.registrationData = registrationData;
    }

    static public UserRegistration registrationData;

    public static UserRegistration getOnDemandRegistrationData() {
        return onDemandRegistrationData;
    }

    public static void setOnDemandRegistrationData(UserRegistration onDemandRegistrationData) {
        RegistrationData.onDemandRegistrationData = onDemandRegistrationData;
    }

    static public UserRegistration onDemandRegistrationData;

    public static Drawable getImageDrawable() {
        return imageDrawable;
    }

    public static void setImageDrawable(Drawable imageDrawable) {
        RegistrationData.imageDrawable = imageDrawable;
    }

    public static Drawable imageDrawable;


    public static Drawable getSubscriberThumbImageDrawable() {
        return subscriberThumbImageDrawable;
    }

    public static void setSubscriberThumbImageDrawable(Drawable subscriberThumbImageDrawable) {
        RegistrationData.subscriberThumbImageDrawable = subscriberThumbImageDrawable;
    }

    public static Drawable subscriberThumbImageDrawable;



    public static Drawable getSubscriberIndexImageDrawable() {
        return subscriberIndexImageDrawable;
    }

    public static void setSubscriberIndexImageDrawable(Drawable subscriberIndexImageDrawable) {
        RegistrationData.subscriberIndexImageDrawable = subscriberIndexImageDrawable;
    }

    public static Drawable subscriberIndexImageDrawable;


    public static List<View> getNinIdImages() {
        return ninIdImages;
    }

    public static void setNinIdImages(List<View> ninIdImages) {
        RegistrationData.ninIdImages = ninIdImages;
    }

    public static List<View> ninIdImages = null;

    public static List<View> getPassportIdImages() {
        return passportIdImages;
    }

    public static void setPassportIdImages(List<View> passportIdImages) {
        RegistrationData.passportIdImages = passportIdImages;
    }

    public static List<View> passportIdImages = null;

    public static List<View> getProfileImages() {
        return profileImages;
    }

    public static void setProfileImages(List<View> profileImages) {
        RegistrationData.profileImages = profileImages;
    }

    public static List<View> profileImages = null;

    public static String getFilePrefix() {
        return filePrefix;
    }

    public static void setFilePrefix(String filePrefix) {
        RegistrationData.filePrefix = filePrefix;
    }

    public static String filePrefix = null;

    public static Boolean getIsFingerPrint() {
        return isFingerPrint;
    }

    public static void setIsFingerPrint(Boolean isFingerPrint) {
        RegistrationData.isFingerPrint = isFingerPrint;
    }

    public static Boolean isFingerPrint = false;


    public static Drawable getUserThumbImageDrawable() {
        return userThumbImageDrawable;
    }

    public static void setUserThumbImageDrawable(Drawable userThumbImageDrawable) {
        RegistrationData.userThumbImageDrawable = userThumbImageDrawable;
    }

    public static Drawable userThumbImageDrawable= null;

    public static Drawable getUserIndexImageDrawable() {
        return userIndexImageDrawable;
    }

    public static void setUserIndexImageDrawable(Drawable userIndexImageDrawable) {
        RegistrationData.userIndexImageDrawable = userIndexImageDrawable;
    }

    public static Drawable userIndexImageDrawable = null;


    public static List<Drawable> getUserFingerprintsList() {
        return userFingerprintsList;
    }

    public static void setUserFingerprintsList(List<Drawable> userFingerprintsList) {
        RegistrationData.userFingerprintsList = userFingerprintsList;
    }

    public static List<Drawable> userFingerprintsList = null;

    public static Boolean getIsUgandan() {
        return isUgandan;
    }

    public static void setIsUgandan(Boolean isUgandan) {
        RegistrationData.isUgandan = isUgandan;
    }

    public static Boolean isUgandan = false;

    public static Boolean getIsForeigner() {
        return isForeigner;
    }

    public static void setIsForeigner(Boolean isForeigner) {
        RegistrationData.isForeigner = isForeigner;
    }

    public static Boolean isForeigner = false;

    public static Boolean getIsRefugee() {
        return isRefugee;
    }

    public static void setIsRefugee(Boolean isRefugee) {
        RegistrationData.isRefugee = isRefugee;
    }

    public static Boolean isRefugee = false;

    public static List<View> getRefugeeImages() {
        return refugeeImages;
    }

    public static void setRefugeeImages(List<View> refugeeImages) {
        RegistrationData.refugeeImages = refugeeImages;
    }

    public static List<View> refugeeImages = null;

    public static List<View> getVisaImages() {
        return visaImages;
    }

    public static void setVisaImages(List<View> visaImages) {
        RegistrationData.visaImages = visaImages;
    }

    public static List<View> visaImages = null;

    public static List<View> getActivationImages() {
        return activationImages;
    }

    public static void setActivationImages(List<View> activationImages) {
        RegistrationData.activationImages = activationImages;
    }

    public static List<View> activationImages = null;

    public static UserRegistration getEditUserProfile() {
        return editUserProfile;
    }

    public static void setEditUserProfile(UserRegistration editUserProfile) {
        RegistrationData.editUserProfile = editUserProfile;
    }

    public static UserRegistration editUserProfile = null;

    public static UserRegistration getUpdateUserProfileData() {
        return updateUserProfileData;
    }

    public static void setUpdateUserProfileData(UserRegistration updateUserProfileData) {
        RegistrationData.updateUserProfileData = updateUserProfileData;
    }

    public static UserRegistration updateUserProfileData = null;


    public static Boolean getIsUpdatedUserScanData() {
        return isUpdatedUserScanData;
    }

    public static void setIsUpdatedUserScanData(Boolean isUpdatedUserScanData) {
        RegistrationData.isUpdatedUserScanData = isUpdatedUserScanData;
    }

    public static Boolean isUpdatedUserScanData = false;


    public static Boolean getIsForeignerUserUpdate() {
        return isForeignerUserUpdate;
    }

    public static void setIsForeignerUserUpdate(Boolean isForeignerUserUpdate) {
        RegistrationData.isForeignerUserUpdate = isForeignerUserUpdate;
    }

    public static Boolean isForeignerUserUpdate = false;

    public static Boolean getIsUgandanUserUpdate() {
        return isUgandanUserUpdate;
    }

    public static void setIsUgandanUserUpdate(Boolean isUgandanUserUpdate) {
        RegistrationData.isUgandanUserUpdate = isUgandanUserUpdate;
    }

    public static Boolean getIsRefugeeUserUpdate() {
        return isRefugeeUserUpdate;
    }

    public static void setIsRefugeeUserUpdate(Boolean isRefugeeUserUpdate) {
        RegistrationData.isRefugeeUserUpdate = isRefugeeUserUpdate;
    }

    public static Boolean isUgandanUserUpdate = false;
    public static Boolean isRefugeeUserUpdate = false;

    public static NewOrderCommand getPostStaffOrder() {
        return postStaffOrder;
    }

    public static void setPostStaffOrder(NewOrderCommand postStaffOrder) {
        RegistrationData.postStaffOrder = postStaffOrder;
    }

    public static NewOrderCommand postStaffOrder = null;


    public static byte[] getByteData() {
        return byteData;
    }

    public static void setByteData(byte[] byteData) {
        RegistrationData.byteData = byteData;
    }

    public static byte[] byteData = null;

    public static List<UserRegistration.UserDocCommand> getReUploadDocList() {
        return reUploadDocList;
    }

    public static void setReUploadDocList(List<UserRegistration.UserDocCommand> reUploadDocList) {
        RegistrationData.reUploadDocList = reUploadDocList;
    }

    public static List<UserRegistration.UserDocCommand> reUploadDocList = null;

    public static Roles[] getRoles() {
        return roles;
    }

    public static void setRoles(Roles[] roles) {
        RegistrationData.roles = roles;
    }

    public static Roles[] roles = null;


    public static Boolean getIsmatched() {
        return ismatched;
    }

    public static void setIsmatched(Boolean ismatched) {
        RegistrationData.ismatched = ismatched;
    }

    public static Boolean ismatched = false;

    public static Drawable getCapturedFingerprintDrawable() {
        return capturedFingerprintDrawable;
    }

    public static void setCapturedFingerprintDrawable(Drawable capturedFingerprintDrawable) {
        RegistrationData.capturedFingerprintDrawable = capturedFingerprintDrawable;
    }

    public static Drawable capturedFingerprintDrawable= null;

    public static UserRegistration getNavigationData() {
        return navigationData;
    }

    public static void setNavigationData(UserRegistration navigationData) {
        RegistrationData.navigationData = navigationData;
    }

    static public UserRegistration navigationData;

    public static Boolean getIsScanICCID() {
        return isScanICCID;
    }

    public static void setIsScanICCID(Boolean isScanICCID) {
        RegistrationData.isScanICCID = isScanICCID;
    }

    public static Boolean isScanICCID = false;

    public static String getScanICCIDData() {
        return scanICCIDData;
    }

    public static void setScanICCIDData(String scanICCIDData) {
        RegistrationData.scanICCIDData = scanICCIDData;
    }

    public static String scanICCIDData = "";

    public static List<UserRegistration.UserDocCommand> getPersonalRegistrationUserDocs() {
        return personalRegistrationUserDocs;
    }

    public static void setPersonalRegistrationUserDocs(List<UserRegistration.UserDocCommand> personalRegistrationUserDocs) {
        RegistrationData.personalRegistrationUserDocs = personalRegistrationUserDocs;
    }

    public static List<UserRegistration.UserDocCommand> personalRegistrationUserDocs = null;

    public static Double getPostLatitude() {
        return postLatitude;
    }

    public static void setPostLatitude(Double postLatitude) {
        RegistrationData.postLatitude = postLatitude;
    }

    public static Double postLatitude;

    public static Double getPostLongitude() {
        return postLongitude;
    }

    public static void setPostLongitude(Double postLongitude) {
        RegistrationData.postLongitude = postLongitude;
    }

    public static Double postLongitude;

    public static Double getCurrentLongitude() {
        return currentLongitude;
    }

    public static void setCurrentLongitude(Double currentLongitude) {
        RegistrationData.currentLongitude = currentLongitude;
    }

    public static Double currentLongitude;

    public static Double getCurrentLatitude() {
        return currentLatitude;
    }

    public static void setCurrentLatitude(Double currentLatitude) {
        RegistrationData.currentLatitude = currentLatitude;
    }

    public static Double currentLatitude;

    public static Map<String, NewOrderCommand.ProductListing> getMapList() {
        return mapList;
    }

    public static void setMapList(Map<String, NewOrderCommand.ProductListing> mapList) {
        RegistrationData.mapList = mapList;
    }

    public static Map<String, NewOrderCommand.ProductListing> mapList = null;


    public static List<PlanGroup> getBundleList() {
        return bundleList;
    }

    public static void setBundleList(List<PlanGroup> bundleList) {
        RegistrationData.bundleList = bundleList;
    }

    public static List<PlanGroup> bundleList = null;

    public static Drawable getRefugeeThumbImageDrawable() {
        return refugeeThumbImageDrawable;
    }

    public static void setRefugeeThumbImageDrawable(Drawable refugeeThumbImageDrawable) {
        RegistrationData.refugeeThumbImageDrawable = refugeeThumbImageDrawable;
    }

    public static Drawable refugeeThumbImageDrawable= null;

    public static String getRefugeeThumbEncodedData() {
        return refugeeThumbEncodedData;
    }

    public static void setRefugeeThumbEncodedData(String refugeeThumbEncodedData) {
        RegistrationData.refugeeThumbEncodedData = refugeeThumbEncodedData;
    }

    public static String refugeeThumbEncodedData = null;

    public static String getBundleRechargeMSISDN() {
        return bundleRechargeMSISDN;
    }

    public static void setBundleRechargeMSISDN(String bundleRechargeMSISDN) {
        RegistrationData.bundleRechargeMSISDN = bundleRechargeMSISDN;
    }

    public static String bundleRechargeMSISDN;

    public static Boolean getIsRefugeeMatched() {
        return isRefugeeMatched;
    }

    public static void setIsRefugeeMatched(Boolean isRefugeeMatched) {
        RegistrationData.isRefugeeMatched = isRefugeeMatched;
    }

    public static Boolean isRefugeeMatched = false;

    public static Boolean getIsPassportScan() {
        return isPassportScan;
    }

    public static void setIsPassportScan(Boolean isPassportScan) {
        RegistrationData.isPassportScan = isPassportScan;
    }

    public static Boolean isPassportScan = false;

    public static Boolean getEnableMobileMoneyReg() {
        return enableMobileMoneyReg;
    }

    public static void setEnableMobileMoneyReg(Boolean enableMobileMoneyReg) {
        RegistrationData.enableMobileMoneyReg = enableMobileMoneyReg;
    }

    public static Boolean enableMobileMoneyReg = false;

}
