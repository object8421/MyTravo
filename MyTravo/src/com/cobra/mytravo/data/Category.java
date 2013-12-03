
package com.cobra.mytravo.data;

public enum Category {
    popular("热门游记"), nearby("查看附近"), mytravo("我的游记");
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
