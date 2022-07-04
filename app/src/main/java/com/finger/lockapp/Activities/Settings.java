package com.finger.lockapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.finger.lockapp.Helpers.AdsHelper;
import com.finger.lockapp.Helpers.FIngerPrintHelper.FingerPrintChecker;
import com.finger.lockapp.Interfaces.FingerStatues;
import com.finger.lockapp.R;
import com.finger.lockapp.databinding.ActivitySettingsBinding;
import com.finger.lockapp.model.Password;
import com.finger.lockapp.ui.Dialogs.FingerPrintDialogAlert;

import static com.finger.lockapp.global.AppConstants.DISABLE;
import static com.finger.lockapp.global.AppConstants.ENABLE;
import static com.finger.lockapp.global.AppConstants.PATTERN;
import static com.finger.lockapp.global.AppConstants.PIN;
import static com.finger.lockapp.global.AppConstants.SETTINGS;

public class Settings extends AppCompatActivity implements View.OnClickListener, FingerStatues {

    private ActivitySettingsBinding binding;
    private AdsHelper helper;
    private Password password;
    private FingerPrintChecker fingerPrintChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            InitFingerPrint();
        }
        InitViews();
        InitToolBar();
        InitAds();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkFingerPrint();
        }
    }

    private void InitToolBar() {
        setSupportActionBar(binding.ToolBar);
    }

    private void InitViews() {

        password = new Password(this);
        if (password.isFingerEnable() == null) {
            password.setFingerEnable(DISABLE);
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }

        binding.enableFingerPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Set saved paper variable as true
                    //check the finger permission print
                    //if support or no get the supported statues
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (password.isFingerEnable().equals(DISABLE) && !fingerPrintChecker.isSupportedFingerPrint()) {
                            FingerPrintDialogAlert alert = new FingerPrintDialogAlert();
                            alert.show(getSupportFragmentManager(), "finger Dialog");
                            alert.setFingerStatues(Settings.this::GetStatues);
                        }else {
                            password.setFingerEnable(ENABLE);
                        }
                    }else{
                        FingerPrintDialogAlert alert = new FingerPrintDialogAlert();
                        alert.show(getSupportFragmentManager(), "finger Dialog");
                        alert.setFingerStatues(Settings.this::GetStatues);

                    }
                } else {
                    //Set saved paper variable as flase
                    password.setFingerEnable(DISABLE);
                }
            }
        });

        //Handle onclick listeners
        binding.ChangePin.setOnClickListener(this);
        binding.ChangePattern.setOnClickListener(this);

    }

    //Init Finger Print
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void InitFingerPrint() {
        //User dialog
        // user click on the switch
        //we check if the user device support or no
        //then if it supported
        //Do as change listener
        //else
        //show dialog to let user enable
        //waiting for the user enable
        //if he enable then it will be true
        //*****************************************************
        //Finger Steps (all of that depend on click on the switch)
        // 1-check the permission of the finger print
        // 2- if true then
        // 3- set the paper of enable finger true
        // 4- else
        // 5- show the Dialog with statues
        // 6- end
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        fingerPrintChecker = new FingerPrintChecker(fingerprintManager, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFingerPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (password.isFingerEnable().equals(ENABLE) && password.isFingerEnable() != null && fingerPrintChecker.isSupportedFingerPrint()) {
                password.setFingerEnable(ENABLE);
                binding.enableFingerPrint.setChecked(true);
            } else {
                password.setFingerEnable(DISABLE);
                binding.enableFingerPrint.setChecked(false);
            }
        }
    }

    //Init Ads
    private void InitAds() {
        helper = new AdsHelper(this);
        helper.InitializeInterstitialAds();
        helper.BannerAds(binding.adview1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ChangePin:
                //Steps
                // Enter The Old Pin
                // Check is the old one is true
                // Enter the new pin
                // confirm the new pin again
                // clear the old pin
                // store new pin into paper
                Intent PinIntent = new Intent(getApplicationContext(), MainActivity.class);
                PinIntent.putExtra(SETTINGS, SETTINGS);
                PinIntent.putExtra(PIN, PIN);
                startActivity(PinIntent);
                finish();
                helper.showinsad();
                break;
            case R.id.ChangePattern:
                //Steps
                // Enter The Old pattern
                // Check is the old one is true
                // Enter the new pattern
                // confirm the new pattern again
                // clear the old pattern
                // store new pattern into paper
                Intent PatternIntent = new Intent(getApplicationContext(), MainActivity.class);
                PatternIntent.putExtra(SETTINGS, SETTINGS);
                PatternIntent.putExtra(PATTERN, PATTERN);
                startActivity(PatternIntent);
                finish();
                helper.showinsad();
                break;

        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        super.onBackPressed();
    }


    @Override
    public void GetStatues(boolean result) {
        if (result) {
            password.setFingerEnable(ENABLE);
            binding.enableFingerPrint.setChecked(true);
        }else{
            password.setFingerEnable(DISABLE);
            binding.enableFingerPrint.setChecked(false);
        }
    }
}