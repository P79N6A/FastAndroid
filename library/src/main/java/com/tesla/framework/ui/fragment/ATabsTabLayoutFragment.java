package com.tesla.framework.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;

import com.tesla.framework.R;
import com.tesla.framework.support.bean.TabItem;
import com.tesla.framework.support.inject.ViewInject;


/**
 * 对TabLayout的封装
 *
 * Created by wangdan on 15/12/22.
 */
public abstract class ATabsTabLayoutFragment<T extends TabItem> extends ATabsFragment<T> {

    @ViewInject(idStr = "tabLayout")
    TabLayout mTabLayout;

    @Override
    public int inflateContentView() {
        return R.layout.comm_ui_tabs_tablayout;
    }



    @Override
    protected void setupViewPage(Bundle savedInstanceSate) {
        setupTabLayout(savedInstanceSate);
    }

    protected void setupTabLayout(Bundle savedInstanceSate) {
        super.setupViewPage(savedInstanceSate);

        getTablayout().setTabMode(TabLayout.MODE_FIXED);
        getTablayout().setTabTextColors(Color.parseColor("#b3ffffff"), Color.WHITE);
        getTablayout().setupWithViewPager(getViewPage());
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                getTablayout().setScrollPosition(getCurrentPosition(), 0, true);
            }

        }, 150);
    }

    public TabLayout getTablayout() {
        return mTabLayout;
    }

}
