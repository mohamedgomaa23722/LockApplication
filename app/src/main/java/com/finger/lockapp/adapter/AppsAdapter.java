package com.finger.lockapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finger.lockapp.Interfaces.AppOnClickListener;
import com.finger.lockapp.R;
import com.finger.lockapp.Utils.Utils;
import com.finger.lockapp.ViewHolder.AppViewHolder;
import com.finger.lockapp.model.AppsItem;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppViewHolder> {
    private Context context;
    private List<AppsItem> appItemList;
    private Utils utils;

    public AppsAdapter(Context context) {
        this.context = context;
        this.utils = new Utils(context);
    }

    public void setAppItemList(List<AppsItem> appItemList) {
        this.appItemList = appItemList;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_item, parent, false);

        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.name_app.setText(appItemList.get(position).getName().toString());
        holder.PackageAppName.setText(appItemList.get(position).getPackageName().toString());
        holder.icon_app.setImageDrawable(appItemList.get(position).getIcons());

        String pk = appItemList.get(position).getPackageName();
        if (utils.isLock(pk)) {
            holder.lock_app.setImageResource(R.drawable.close_lock);
        } else {
            holder.lock_app.setImageResource(R.drawable.open_lock);
        }
        holder.setListener(new AppOnClickListener() {
            @Override
            public void selectedApp(int pos) {
                if (utils.isLock(pk)) {
                    holder.lock_app.setImageResource(R.drawable.open_lock);
                    utils.unLock(pk);
                } else {
                    holder.lock_app.setImageResource(R.drawable.close_lock);
                    utils.lock(pk);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

}

