package com.apache.fastandroid.topic.site.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.apache.fastandroid.topic.site.bean.Siteable;
import com.apache.fastandroid.topic.site.bean.SitesItem;
import com.tesla.framework.support.inject.ViewInject;
import com.tesla.framework.ui.fragment.itemview.ARecycleViewItemViewHolder;

/**
 * Created by 01370340 on 2017/9/3.
 */

public class SizeItemHeaderView extends ARecycleViewItemViewHolder<Siteable> {

    @ViewInject(idStr = "tv_type")
    TextView tv_type;

    public SizeItemHeaderView(Activity context, View itemView) {
        super(context, itemView);


    }

    @Override
    public void onBindData(View convertView, Siteable bean, int position) {
        if (bean instanceof SitesItem){
            SitesItem item = (SitesItem) bean;
            tv_type.setText(item.getName());
        }
    }
}
