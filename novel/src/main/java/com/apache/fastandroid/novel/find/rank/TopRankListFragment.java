package com.apache.fastandroid.novel.find.rank;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.apache.fastandroid.novel.find.bean.RankingList;
import com.apache.fastandroid.novel.find.rank.view.RankItemViewCreator;
import com.apache.fastandroid.novel.support.NovelSdk;
import com.tesla.framework.network.task.TaskException;
import com.tesla.framework.network.task.WorkTask;
import com.tesla.framework.support.bean.RefreshConfig;
import com.tesla.framework.ui.activity.FragmentArgs;
import com.tesla.framework.ui.activity.FragmentContainerActivity;
import com.tesla.framework.ui.fragment.ARecycleViewFragment;
import com.tesla.framework.ui.fragment.itemview.IItemViewCreator;

import java.util.ArrayList;

/**
 * Created by 01370340 on 2017/9/24.
 * "排行榜"的Fragment
 */

public class TopRankListFragment extends ARecycleViewFragment<RankingList.MaleBean,ArrayList<RankingList.MaleBean>,RankingList.MaleBean> {

    public static void launch(Activity from) {
        FragmentArgs args =  new FragmentArgs();
        FragmentContainerActivity.launch(from,TopRankListFragment.class,args);
    }

    @Override
    protected void setUpRefreshConfig(RefreshConfig refreshConfig) {
        super.setUpRefreshConfig(refreshConfig);
        refreshConfig.disalbeFooterMore();
    }

    @Override
    protected IItemViewCreator<RankingList.MaleBean> configItemViewCreator() {
        return new RankItemViewCreator(getActivity());
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setToolbarTitle("排行榜");
    }

    @Override
    public void requestData() {
        super.requestData();

        new LoadRankListData().execute();
    }

    class LoadRankListData extends WorkTask<Void,Void,ArrayList<RankingList.MaleBean>>{


        @Override
        protected void onSuccess(ArrayList<RankingList.MaleBean> maleBeen) {
            super.onSuccess(maleBeen);
            setItems(maleBeen);
        }

        @Override
        public ArrayList<RankingList.MaleBean> workInBackground(Void... params) throws TaskException {
            return NovelSdk.newInstance().getRankingList();
        }
    }


}