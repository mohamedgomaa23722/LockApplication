package com.finger.lockapp.model;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppsItem {
    private Drawable icons;
    private String name;
    private String PackageName;

    public AppsItem(Drawable icons, String name, String PackageName) {
        this.icons = icons;
        this.name = name;
        this.PackageName = PackageName;
    }

    public Drawable getIcons() {
        return icons;
    }

    public void setIcons(Drawable icons) {
        this.icons = icons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }
}
