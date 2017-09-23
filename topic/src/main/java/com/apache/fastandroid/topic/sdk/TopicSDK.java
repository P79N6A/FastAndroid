package com.apache.fastandroid.topic.sdk;


import com.apache.fastandroid.artemis.retrofit.BaseHttpUtilsV2;
import com.apache.fastandroid.artemis.rx.DefaultHttpResultObserver;
import com.apache.fastandroid.artemis.rx.ICallback;
import com.apache.fastandroid.topic.TopicConstans;
import com.apache.fastandroid.topic.bean.TopicBean;
import com.apache.fastandroid.topic.bean.TopicBeans;
import com.apache.fastandroid.topic.bean.TopicContent;
import com.apache.fastandroid.topic.bean.TopicReplyBean;
import com.tesla.framework.FrameworkApplication;
import com.tesla.framework.network.biz.ABizLogic;
import com.tesla.framework.network.http.HttpConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by 01370340 on 2017/9/2.
 */

public class TopicSDK extends ABizLogic {
    @Override
    protected HttpConfig configHttpConfig() {
        return new HttpConfig();
    }

    public static TopicSDK newInstance(){
        return new TopicSDK();
    }

    public TopicBeans getTopicsList(String type, Integer node_id, int offset, int limit) throws Exception{
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), TopicConstans.BASE_URL);
        TopicApiService apiService = httpUtils.getRetrofit().create(TopicApiService.class);
        Call<List<TopicBean>> call =  apiService.getTopicsList(type,node_id,offset,limit);
        if (call != null){
            Response<List<TopicBean>> response = call.execute();
            if (response != null && response.isSuccessful()){
                List<TopicBean> list =  response.body();
                TopicBeans beans = new TopicBeans(list);
                return beans;
            }
        }
        return null;
    }
    public Observable<List<TopicBean>> getTopicsListV2(String type, Integer node_id, int offset, int limit) throws Exception{
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), TopicConstans.BASE_URL);
        TopicApiService apiService = httpUtils.getRetrofit().create(TopicApiService.class);
        Observable<List<TopicBean>> observable =  apiService.getTopicsListV2(type,node_id,offset,limit);
        return observable;
    }

    public Observable<TopicContent> getTopicsDetail(int id, final ICallback<TopicContent> callback) {
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), TopicConstans.BASE_URL);
        TopicApiService apiService = httpUtils.getRetrofit().create(TopicApiService.class);
        Observable<TopicContent> observable =  apiService.getTopic(id);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new DefaultHttpResultObserver<>(callback));
        return observable;
    }

    public ArrayList<TopicReplyBean> getReplyList(int id, Integer offset, Integer limit)throws Exception {
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), TopicConstans.BASE_URL);
        TopicApiService apiService = httpUtils.getRetrofit().create(TopicApiService.class);
        Call<List<TopicReplyBean>> call =  apiService.getTopicRepliesList(id,offset,limit);
        if (call != null){
            Response<List<TopicReplyBean>> response = call.execute();
            if (response != null && response.isSuccessful()){
                List<TopicReplyBean> list =  response.body();
                ArrayList result = new ArrayList(list);
                return result;
            }
        }

        return null;
    }


}