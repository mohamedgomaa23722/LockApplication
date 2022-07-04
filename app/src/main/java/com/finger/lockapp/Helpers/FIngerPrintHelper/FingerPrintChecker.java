package com.finger.lockapp.Helpers.FIngerPrintHelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrintChecker {
    private String SupportFingerPrintStatues;
    private boolean SupportedFingerPrint;

    public FingerPrintChecker(FingerprintManager fingerprintManager, Context context) {
        if (!fingerprintManager.isHardwareDetected()) {
            //Device Does not support finger print
            SupportFingerPrintStatues = "sorry your phone doesn't support Finger Print";
            SupportedFingerPrint = false;
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            //supported finger print but it doesn't set permission
            SupportFingerPrintStatues = "Please enable the fingerprint permission";
            SupportedFingerPrint = false;
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            //supported finger print but it doesn't set on settings
            SupportFingerPrintStatues = "No fingerprint configured. Please register at least one fingerprint in your device's Settings";
            // context.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            SupportedFingerPrint = false;
        } else {
            //Supported use fingerprint
            SupportFingerPrintStatues = "GOOD";
            SupportedFingerPrint = true;
        }
    }

    public String getSupportFingerPrintStatues() {
        return SupportFingerPrintStatues;
    }

    public boolean isSupportedFingerPrint() {
        return SupportedFingerPrint;
    }


}
