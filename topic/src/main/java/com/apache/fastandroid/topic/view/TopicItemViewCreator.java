package com.apache.fastandroid.topic.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apache.fastandroid.topic.bean.TopicBean;
import com.tesla.framework.ui.fragment.itemview.IITemView;
import com.tesla.framework.ui.fragment.itemview.IItemViewCreator;

/**
 * Created by 01370340 on 2017/9/3.
 */

public class TopicItemViewCreator implements IItemViewCreator<TopicBean> {
    Activity context;
    public TopicItemViewCreator(Activity context){
        this.context = context;
    }

    @Override
    public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(TopicItemView.LAYOUT_RES,parent,false);
    }

    @Override
    public IITemView<TopicBean> newItemView(View contentView, int viewType) {
        return new TopicItemView(context,contentView);
    }
}