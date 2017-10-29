package com.apache.fastandroid.novel.bookshelf.view;

import android.app.Activity;
import android.view.View;

import com.apache.fastandroid.novel.R;
import com.apache.fastandroid.novel.find.bean.RecommendBook;
import com.tesla.framework.ui.fragment.itemview.BaseItemViewCreator;
import com.tesla.framework.ui.fragment.itemview.IITemView;

/**
 * Created by 01370340 on 2017/9/24.
 */

public class RecommandViewCreator extends BaseItemViewCreator<RecommendBook> {
    public RecommandViewCreator(Activity context) {
        super(context);
    }

    @Override
    protected int inflateItemView(int viewType) {
        return R.layout.novel_item_recommand;
    }

    @Override
    public IITemView<RecommendBook> newItemView(View contentView, int viewType) {
        return new RecomandItemView(getContext(),contentView);
    }
}
