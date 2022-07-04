package com.finger.lockapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.finger.lockapp.Helpers.AdsHelper;
import com.finger.lockapp.Helpers.FIngerPrintHelper.FingerPrintChecker;
import com.finger.lockapp.R;
import com.finger.lockapp.Services.BackgroundManager;
import com.finger.lockapp.Utils.Utils;
import com.finger.lockapp.databinding.ActivityPatternLockBinding;
import com.finger.lockapp.model.Password;

import java.util.List;

import static com.finger.lockapp.global.AppConstants.CONFIRM_PATTERN;
import static com.finger.lockapp.global.AppConstants.CONFIRM_PIN;
import static com.finger.lockapp.global.AppConstants.DISABLE;
import static com.finger.lockapp.global.AppConstants.ENABLE;
import static com.finger.lockapp.global.AppConstants.NEW_PATTERN;
import static com.finger.lockapp.global.AppConstants.NEW_PIN;
import static com.finger.lockapp.global.AppConstants.OLD_PATTERN;
import static com.finger.lockapp.global.AppConstants.OLD_PIN;
import static com.finger.lockapp.global.AppConstants.PATTERN;
import static com.finger.lockapp.global.AppConstants.PIN;
import static com.finger.lockapp.global.AppConstants.SETTINGS;
import static com.finger.lockapp.global.AppConstants.SHEMA_FAILED;
import static com.finger.lockapp.global.AppConstants.STATUS_FIRST_STEP;
import static com.finger.lockapp.global.AppConstants.STATUS_Next_STEP;
import static com.finger.lockapp.global.AppConstants.STATUS_PASSWORD_CORRECT;
import static com.finger.lockapp.global.AppConstants.STATUS_PASSWORD_INCORRECT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ActivityPatternLockBinding binding;
    private String FirstPIN = "";
    private String SecondPIN = "";
    private FingerprintManager fingerprintManager;
    private Password utilsPassword;
    private String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BackgroundManager.getInstance().init(this).startService();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pattern_lock);
        if (getIntent().getStringExtra(SETTINGS) == null) {
            intitLayout();
            initPatternListener();
            TrackThePinCode();
            InitPinView();
            InitAds();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                InitFingerPrint();
            }
            InitIconApp();
        } else {
            //When the settings need to change pin
            //we should Initialize only pin view
            if (getIntent().getStringExtra(PIN) != null) {
                RunPinViewOnly();
            }
            //when the settings need to change pattern
            //we should Initialize only pattern view
            if (getIntent().getStringExtra(PATTERN) != null) {
                RunPatternViewOnly();
            }
        }
    }

    private void initPatternListener() {
        PatternLockView patternLock = findViewById(R.id.patternLockView);
        patternLock.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String PatternPassword = PatternLockUtils.patternToString(patternLock, pattern);
                if (PatternPassword.length() < 4) {
                    binding.PatternView.statusPassword.setText(SHEMA_FAILED);
                    patternLock.clearPattern();
                    return;
                }

                if (utilsPassword.getPatternPass() == null || getIntent().getStringExtra(SETTINGS) != null && utilsPassword.isCheckableOLd()) {
                    if (utilsPassword.isFirstStep()) {
                        userPassword = PatternPassword;
                        utilsPassword.setFirstStep(false);
                        if (getIntent().getStringExtra(SETTINGS) == null) {
                            binding.PatternView.statusPassword.setText(STATUS_Next_STEP);
                            binding.stepView.go(3, true);
                        } else {
                            binding.stepView.go(2, true);
                            binding.PatternView.statusPassword.setText(CONFIRM_PATTERN);
                        }
                    } else {
                        if (userPassword.equals(PatternPassword)) {
                            if (getIntent().getStringExtra(SETTINGS) == null) {
                                utilsPassword.setPatternPass(userPassword);
                            } else {
                                utilsPassword.ClearPatternPassword();
                                utilsPassword.setPatternPass(userPassword);
                            }
                            binding.PatternView.statusPassword.setText(STATUS_PASSWORD_CORRECT);
                            binding.stepView.done(true);
                            startAct();
                        } else {
                            binding.PatternView.statusPassword.setText(STATUS_PASSWORD_INCORRECT);
                        }
                    }

                } else {
                    if (utilsPassword.isPatternCorrect(PatternPassword)) {
                        if (getIntent().getStringExtra(SETTINGS) == null) {
                            binding.PatternView.statusPassword.setText(STATUS_PASSWORD_CORRECT);
                            startAct();
                        } else {
                            utilsPassword.setCheckableOLd(true);
                            binding.stepView.go(1, true);
                            binding.PatternView.statusPassword.setText(NEW_PATTERN);
                        }

                    } else {
                        binding.PatternView.statusPassword.setText(STATUS_PASSWORD_INCORRECT);
                    }
                }
                patternLock.clearPattern();
            }

            @Override
            public void onCleared() {

            }
        });
    }

    private void intitLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.PatternView.FingerPrintIcon.setVisibility(View.INVISIBLE);
            binding.PatternView.FIngerPrintInstruction.setVisibility(View.INVISIBLE);
        }
            utilsPassword = new Password(this);
        if (utilsPassword.getPatternPass() == null) {
            binding.PatternView.statusPassword.setText(STATUS_FIRST_STEP);
            binding.normalview.setVisibility(View.INVISIBLE);
            binding.stepView.setVisibility(View.VISIBLE);
            binding.PatternView.PinIcon.setVisibility(View.INVISIBLE);
            binding.stepView.setStepsNumber(4);
            binding.stepView.go(0, true);
        } else {
            if (getIntent().getStringExtra(SETTINGS) == null) {
                binding.root.transitionToEnd();
                binding.stepView.setVisibility(View.VISIBLE);
                binding.stepView.setVisibility(View.GONE);
                binding.PatternView.PinIcon.setVisibility(View.VISIBLE);
                binding.PatternView.statusPassword.setTextColor(Color.WHITE);
            } else {
                binding.stepView.setStepsNumber(3);
                binding.stepView.go(0, true);
            }
        }
    }

    private void InitIconApp() {
        if (getIntent().getStringExtra("broadcast_reciever") != null) {
            String current_app = new Utils(this).getLastApp();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = getPackageManager().getApplicationInfo(current_app, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            binding.appIcon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
        }
    }

    private void InitPinView() {
        //Handle Clicks
        binding.PatternView.PinIcon.setOnClickListener(this);
        //Daily numbers handle
        binding.pinView.numberlist.number0.setOnClickListener(this);
        binding.pinView.numberlist.number1.setOnClickListener(this);
        binding.pinView.numberlist.number2.setOnClickListener(this);
        binding.pinView.numberlist.number3.setOnClickListener(this);
        binding.pinView.numberlist.number4.setOnClickListener(this);
        binding.pinView.numberlist.number5.setOnClickListener(this);
        binding.pinView.numberlist.number6.setOnClickListener(this);
        binding.pinView.numberlist.number7.setOnClickListener(this);
        binding.pinView.numberlist.number8.setOnClickListener(this);
        binding.pinView.numberlist.number9.setOnClickListener(this);
        binding.pinView.numberlist.Delete.setOnClickListener(this);
    }

    private void TrackThePinCode() {
        binding.pinView.firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (utilsPassword.getPinPass() == null || getIntent().getStringExtra(SETTINGS) != null && utilsPassword.isCheckableOLd()) {
                    if (count == 5) {
                        if (utilsPassword.isFirstStep()) {
                            binding.pinView.firstPinView.setTextColor(Color.GREEN);
                            SecondPIN = FirstPIN;
                            FirstPIN = "";
                            binding.pinView.firstPinView.setText(FirstPIN);
                            if (getIntent().getStringExtra(SETTINGS) == null) {
                                binding.stepView.go(1, true);
                            } else {
                                binding.stepView.go(2, true);
                                binding.pinView.titleprogress.setText(CONFIRM_PIN);
                            }
                            utilsPassword.setFirstStep(false);
                        } else {
                            if (FirstPIN.equals(SecondPIN)) {
                                binding.pinView.firstPinView.setTextColor(Color.GREEN);
                                binding.pinView.titleprogress.setText("Pin set successfully");
                                if (getIntent().getStringExtra(SETTINGS) == null) {
                                    utilsPassword.setPinPass(FirstPIN);
                                } else {
                                    utilsPassword.ClearPinPassword();
                                    utilsPassword.setPinPass(FirstPIN);
                                }
                                utilsPassword.setFirstStep(true);
                                if (getIntent().getStringExtra(SETTINGS) == null) {
                                    binding.stepView.go(2, true);
                                    binding.root.transitionToEnd();
                                } else {
                                    binding.stepView.done(true);
                                    startAct();
                                }
                            } else {
                                binding.pinView.titleprogress.setText("Wrong Index");
                                FirstPIN = "";
                                binding.pinView.firstPinView.setText(FirstPIN);
                            }
                        }
                    } else {
                        binding.pinView.firstPinView.setLineColor(Color.WHITE);
                        binding.pinView.firstPinView.setTextColor(Color.YELLOW);
                    }
                } else {
                    if (utilsPassword.isPinCorrect(FirstPIN) && utilsPassword.getPatternPass() != null) {
                        if (getIntent().getStringExtra(SETTINGS) == null) {
                            binding.pinView.firstPinView.setTextColor(Color.GREEN);
                            binding.pinView.titleprogress.setText("Pin set successfully");
                            startAct();
                        } else {
                            binding.pinView.firstPinView.setTextColor(Color.GREEN);
                            FirstPIN = "";
                            binding.pinView.firstPinView.setText(FirstPIN);
                            utilsPassword.setCheckableOLd(true);
                            binding.stepView.go(1, true);
                            binding.pinView.titleprogress.setText(NEW_PIN);
                        }
                    } else {
                        if (s.length() == 5) {
                            binding.pinView.titleprogress.setText("Wrong Index");
                            FirstPIN = "";
                            binding.pinView.firstPinView.setText(FirstPIN);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void InitFingerPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            FingerPrintChecker fingerPrintChecker = new FingerPrintChecker(fingerprintManager, this);
            if (utilsPassword.isFingerEnable()==null){
                utilsPassword.setFingerEnable(DISABLE);
            }
            if (utilsPassword.getPinPass() != null && utilsPassword.getPatternPass() != null && fingerPrintChecker.isSupportedFingerPrint() &&utilsPassword.isFingerEnable().equals(ENABLE)) {
                //So we have all passwords enable into our apps
                binding.PatternView.FingerPrintIcon.setVisibility(View.VISIBLE);
                binding.PatternView.FIngerPrintInstruction.setVisibility(View.VISIBLE);
                FingerListener();
            }else{
                binding.PatternView.FingerPrintIcon.setVisibility(View.INVISIBLE);
                binding.PatternView.FIngerPrintInstruction.setVisibility(View.INVISIBLE);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void FingerListener() {
        CancellationSignal cancellationSignal = new CancellationSignal();

        FingerprintManager.AuthenticationCallback authenticationCallback = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Succeeded", Toast.LENGTH_SHORT).show();
                startAct();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                super.onAuthenticationFailed();
            }
        };
        fingerprintManager.authenticate(null, cancellationSignal, 0, authenticationCallback, null);
    }

    private void startAct() {
        if (getIntent().getStringExtra("broadcast_reciever") == null) {
            startActivity(new Intent(MainActivity.this, HomePage.class));
        }
        finish();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number0:
                setNumber("0");
                break;
            case R.id.number1:
                setNumber("1");
                break;
            case R.id.number2:
                setNumber("2");
                break;
            case R.id.number3:
                setNumber("3");
                break;
            case R.id.number4:
                setNumber("4");
                break;
            case R.id.number5:
                setNumber("5");
                break;
            case R.id.number6:
                setNumber("6");
                break;
            case R.id.number7:
                setNumber("7");
                break;
            case R.id.number8:
                setNumber("8");
                break;
            case R.id.number9:
                setNumber("9");
                break;
            case R.id.Delete:
                RemoveNumber();
                break;
            case R.id.PinIcon:
                binding.root.transitionToStart();
                break;
        }

    }

    private void setNumber(String number) {
        if (FirstPIN.length() <= 4) {
            FirstPIN += number;
            binding.pinView.firstPinView.setText(FirstPIN);
        } else {
            Toast.makeText(this, "sorry you must enter only five numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void RemoveNumber() {
        if (FirstPIN.length() > 0) {
            FirstPIN = FirstPIN.substring(0, FirstPIN.length() - 1);
            binding.pinView.firstPinView.setText(FirstPIN);
        }
    }

    @Override
    public void onBackPressed() {
        startCurrentHomePackage();
        if (utilsPassword.getPatternPass() == null || utilsPassword.getPinPass() == null) {
            utilsPassword.ClearPatternPassword();
            utilsPassword.ClearPinPassword();
        }
        finish();
        super.onBackPressed();
    }


    private void startCurrentHomePackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(componentName);
        startActivity(intent);
        new Utils(this).ClearLastApp();
    }

    //Settings Methods Only
    private void RunPinViewOnly() {
        binding.root.transitionToStart();
        binding.PatternView.statusPassword.setText(OLD_PIN);
        intitLayout();
        TrackThePinCode();
        InitPinView();
        //Visibality views
        binding.normalview.setVisibility(View.INVISIBLE);
        binding.stepView.setVisibility(View.VISIBLE);
        //
    }

    private void RunPatternViewOnly() {
        binding.PatternView.statusPassword.setText(OLD_PATTERN);
        binding.root.transitionToEnd();
        intitLayout();
        initPatternListener();
        //Visibality views
        binding.PatternView.PinIcon.setVisibility(View.INVISIBLE);
        binding.PatternView.FingerPrintIcon.setVisibility(View.INVISIBLE);
        binding.PatternView.FIngerPrintInstruction.setVisibility(View.INVISIBLE);
        binding.normalview.setVisibility(View.INVISIBLE);
        binding.stepView.setVisibility(View.VISIBLE);
    }

    //ads
    private void InitAds() {
        AdsHelper helper = new AdsHelper(this);
        helper.InitializeInterstitialAds();
        helper.BannerAds(binding.adview3);
    }

}