package com.finger.lockapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finger.lockapp.Interfaces.AppOnClickListener;
import com.finger.lockapp.R;

public class AppViewHolder extends RecyclerView.ViewHolder  {
    public ImageView icon_app,lock_app;
    public TextView name_app,PackageAppName;
    private AppOnClickListener listener;

    public void setListener(AppOnClickListener listener) {
        this.listener = listener;
    }

    public AppViewHolder(@NonNull View itemView) {
        super(itemView);
        icon_app=itemView.findViewById(R.id.iconImage);
        lock_app=itemView.findViewById(R.id.Lock_Key);
        name_app=itemView.findViewById(R.id.titleTextView);
        PackageAppName=itemView.findViewById(R.id.PackageNametext);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selectedApp(getAdapterPosition());
            }
        });
    }


}
