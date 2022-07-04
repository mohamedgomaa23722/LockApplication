package com.finger.lockapp.ui.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.finger.lockapp.Helpers.FIngerPrintHelper.FingerPrintChecker;
import com.finger.lockapp.Helpers.FIngerPrintHelper.FingerprintHandler;
import com.finger.lockapp.Interfaces.FingerStatues;
import com.finger.lockapp.R;

import static android.content.Context.FINGERPRINT_SERVICE;

public class FingerPrintDialogAlert extends AppCompatDialogFragment {
    private FingerStatues fingerStatues;
    private FingerprintManager fingerprintManager;
    private FingerPrintChecker fingerPrintChecker;

    public void setFingerStatues(FingerStatues fingerStatues) {
        this.fingerStatues = fingerStatues;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Finger Print Permission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);
            fingerPrintChecker = new FingerPrintChecker(fingerprintManager, getActivity());
            builder.setMessage(fingerPrintChecker.getSupportFingerPrintStatues());
            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (fingerPrintChecker.isSupportedFingerPrint()) {
                        dialog.dismiss();
                        fingerStatues.GetStatues(true);
                    } else {
                        startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                        fingerStatues.GetStatues(false);

                    }
                }
            });
        } else {
            builder.setMessage("Soory your phone Does not support finger print.");
        }
        builder.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fingerStatues.GetStatues(false);
            }
        });


        return builder.create();
    }
}