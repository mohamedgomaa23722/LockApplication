package com.finger.lockapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.finger.lockapp.Helpers.AdsHelper;
import com.finger.lockapp.R;
import com.finger.lockapp.Utils.Utils;
import com.finger.lockapp.adapter.AppsAdapter;
import com.finger.lockapp.databinding.ActivityHomePageBinding;
import com.finger.lockapp.model.AppsItem;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomePage";
    private ActivityHomePageBinding binding;
    private AppsAdapter adapter;
    private AdsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        initView();
        SearchAlgorithm();
        InitAds();
    }

    private void initView() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        //Handle onclick
        binding.SettingsBtn.setOnClickListener(this);
        binding.enablePermission.setOnClickListener(this);
    }

    private void SearchAlgorithm() {
        binding.SearchViewEdx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.setAppItemList(Filter(getAllApps(), s.toString()));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<AppsItem> Filter(List<AppsItem> appsinfo, String AppName) {

        List<AppsItem> results = new ArrayList<>();
        if (AppName.length() > 0) {
            for (int i = 0; i < appsinfo.size(); i++) {
                if (appsinfo.get(i).getName().toLowerCase().contains(AppName.toLowerCase())) {
                    results.add(appsinfo.get(i));
                }
            }
            return results;
        } else {
            return getAllApps();
        }
    }

    private void InitAds() {
        helper = new AdsHelper(this);
        helper.InitializeInterstitialAds();
        helper.BannerAds(binding.homeadView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SettingsBtn) {
            Intent intent = new Intent(HomePage.this, Settings.class);
            startActivity(intent);
            finish();
            helper.showinsad();
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private void Refresh() {
        LoadApp loadApp = new LoadApp();
        loadApp.execute(PackageManager.GET_META_DATA);
    }

    private List<AppsItem> getAllApps() {
        List<AppsItem> results = new ArrayList<>();
        PackageManager pk = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pk.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            results.add(new AppsItem(activityInfo.loadIcon(pk), activityInfo.loadLabel(pk).toString(), activityInfo.packageName));
        }
        return results;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Utils.checkPermission(this)) {
                binding.PerimssionSetter.setVisibility(View.VISIBLE);
            } else {
                binding.PerimssionSetter.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {

        Log.d(TAG, "gomaa onResume: ");
        Refresh();
        super.onResume();
    }

    class LoadApp extends AsyncTask<Integer, Integer, List<AppsItem>> {
        @Override
        protected List<AppsItem> doInBackground(Integer... integers) {
            List<AppsItem> results = new ArrayList<>();
            PackageManager pk = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = pk.queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : resolveInfos) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                results.add(new AppsItem(activityInfo.loadIcon(pk), activityInfo.loadLabel(pk).toString(), activityInfo.packageName));
            }

            return results;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.swipeRefresh.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<AppsItem> appInfos) {
            super.onPostExecute(appInfos);
            binding.swipeRefresh.setRefreshing(false);
            binding.AppsRecycler.setHasFixedSize(true);
            binding.AppsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new AppsAdapter(getApplicationContext());
            adapter.setAppItemList(appInfos);
            binding.AppsRecycler.setAdapter(adapter);
        }
    }

}