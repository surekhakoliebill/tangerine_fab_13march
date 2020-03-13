package com.aryagami.tangerine.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.tangerine.fragments.CameraSelectorDialogFragment;
import com.aryagami.tangerine.fragments.FormatSelectorDialogFragment;
import com.aryagami.tangerine.fragments.MessageDialogFragment;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.apache.commons.codec.binary.Base64;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FullScannerActivity extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    ScanData scannedResult = new ScanData();
    DateFormat formatter = null;
    Date convertedDate = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, false);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);

        setupFormats();
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAspectTolerance(0.5f);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);

        CheckNetworkConnection.cehckNetwork(FullScannerActivity.this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult != null) {
            if (!rawResult.getText().isEmpty()) {

                if (RegistrationData.getIsScanICCID()) {
                    FullScannerActivity.this.finish();
                    String iccidData[] = rawResult.toString().split(",");
                    if (iccidData[0] != null) {
                        if (!iccidData[0].isEmpty()) {
                            RegistrationData.setScanICCIDData(iccidData[0].toString());
                            FullScannerActivity.this.finish();
                        } else {
                            MyToast.makeMyToast(FullScannerActivity.this, "Please Rescan!", Toast.LENGTH_SHORT);
                            FullScannerActivity.this.finish();
                            Intent intent = new Intent(FullScannerActivity.this, BarcodeScannerActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        MyToast.makeMyToast(FullScannerActivity.this, "Please Rescan, Details are Empty!", Toast.LENGTH_SHORT);
                        FullScannerActivity.this.finish();
                        Intent intent = new Intent(FullScannerActivity.this, BarcodeScannerActivity.class);
                        startActivity(intent);
                    }

                } else {
                    decodeScanResult(rawResult.getText());
                }
            } else {
                MyToast.makeMyToast(FullScannerActivity.this, "Data Empty.", Toast.LENGTH_SHORT).show();
            }
        } else {
            MyToast.makeMyToast(FullScannerActivity.this, "Please...", Toast.LENGTH_SHORT).show();
        }
    }

    private void decodeScanResult(String rawResult) {

        String[] ninResult = rawResult.split(";");
        if (ninResult != null) {
            byte[] byteArray;
            if (ninResult.length > 9) {
                for (int i = 0; i < 9; i++) {
                    switch (i) {
                        case 0:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setLastName(decodedString);
                            }
                            break;
                        case 1:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setFirstName(decodedString);
                            }
                            break;
                        case 2:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setOtherName(decodedString);
                            }
                            break;

                        case 3:
                            String ddMMyyyy = ninResult[3].toString();
                            SimpleDateFormat dt = new SimpleDateFormat("ddmmyyyy");
                            try {
                                convertedDate = dt.parse(ddMMyyyy);
                                formatter = new SimpleDateFormat("dd/mm/yyyy");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            scannedResult.setDateOfBirth(formatter.format(convertedDate).toString());
                            break;

                        case 6:
                            scannedResult.setPassportNo(ninResult[6].toString());
                            break;

                        case 7:
                            scannedResult.setDocumentId(ninResult[7].toString());
                            break;

                        case 8:
                            byteArray = Base64.decodeBase64(ninResult[8].trim().getBytes());
                            scannedResult.setScannedFingerData(byteArray);
                            scannedResult.setEncodedFingerData(ninResult[i].trim().getBytes());
                            scannedResult.scannedFingerIndex = 1;
                            break;

                        default:
                    }

                }
                scannedResult.setMatched(false);
                ScanData.setScannedBarcodeData(scannedResult);
                Intent intent = new Intent(FullScannerActivity.this, BarcodeScanResultActivity.class);
                startActivity(intent);
                FullScannerActivity.this.finish();
            } else {
                alertMessage();
            }
        } else {
            alertMessage();
        }
    }

    private void alertMessage() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FullScannerActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Message!");
        alertDialog.setMessage("Please, rescan the document. Reason: Empty Data.");
        alertDialog.setNeutralButton(FullScannerActivity.this.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        RegistrationData.setIsScanICCID(false);
                        ScanData.setScannedBarcodeData(null);
                        Intent intent = new Intent(FullScannerActivity.this, BarcodeScannerActivity.class);
                        startActivity(intent);
                        FullScannerActivity.this.finish();
                    }
                });
        alertDialog.show();
    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
        fragment.show(getFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }

    public void onTrimMemory(int level) {
        System.gc();
    }
}