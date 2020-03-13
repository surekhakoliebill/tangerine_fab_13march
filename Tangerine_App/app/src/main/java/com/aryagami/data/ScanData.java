package com.aryagami.data;

import java.io.Serializable;

/**
 * Model class for display data of Passport & ID MRZ
 */

public class ScanData implements Serializable {


    public static ScanData getScanData() {
        return scanData;
    }

    public static void setScanData(ScanData scanData) {
        ScanData.scanData = scanData;
    }

    public static ScanData scanData;

    public int id, mrz;
    public String lastName;
    public String firstName;
    public String passportNo;
    public String country;
    public String gender;
    public String dateOfBirth;
    public String dateOfExpiry;
    public String documentType;
    public String address;

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Integer getScannedFingerIndex() {
        return scannedFingerIndex;
    }

    public void setScannedFingerIndex(Integer scannedFingerIndex) {
        this.scannedFingerIndex = scannedFingerIndex;
    }

    public String otherName;
    public String documentId;

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }

    public Boolean isMatched;

    public Integer scannedFingerIndex;

    private byte[] userPicture;

    public byte[] getScannedFingerData() {
        return scannedFingerData;
    }

    public void setScannedFingerData(byte[] scannedFingerData) {
        this.scannedFingerData = scannedFingerData;
    }

    public byte[] getEncodedFingerData() {
        return encodedFingerData;
    }

    public void setEncodedFingerData(byte[] encodedFingerData) {
        this.encodedFingerData = encodedFingerData;
    }

    public byte[] scannedFingerData;
    public byte[] encodedFingerData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public byte[] getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(byte[] userPicture) {
        this.userPicture = userPicture;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getMrz() {
        return mrz;
    }

    public void setMrz(int mrz) {
        this.mrz = mrz;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getUgandanNationalId() {
        return ugandanNationalId;
    }

    public void setUgandanNationalId(String ugandanNationalId) {
        this.ugandanNationalId = ugandanNationalId;
    }

    public String ugandanNationalId;

    public static ScanData getScannedBarcodeData() {
        return scannedBarcodeData;
    }

    public static void setScannedBarcodeData(ScanData scannedBarcodeData) {
        ScanData.scannedBarcodeData = scannedBarcodeData;
    }

    public static ScanData scannedBarcodeData= null;
}
