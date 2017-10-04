package com.apache.fastandroid.news.sdk;

import com.apache.fastandroid.artemis.BaseBizLogic;
import com.apache.fastandroid.artemis.retrofit.BaseHttpUtilsV2;
import com.apache.fastandroid.news.bean.NewsBean;
import com.apache.fastandroid.news.bean.NewsBeans;
import com.apache.fastandroid.topic.TopicConstans;
import com.tesla.framework.FrameworkApplication;
import com.tesla.framework.network.http.HttpConfig;
import com.tesla.framework.network.task.TaskException;

import java.util.List;

import retrofit2.Call;

/**
 * Created by 01370340 on 2017/9/3.
 */

public class NewsSDK extends BaseBizLogic {
    @Override
    protected HttpConfig configHttpConfig() {
        return new HttpConfig();
    }
    
    public static NewsSDK newInstance(){
        return new NewsSDK();
    }


    public NewsBeans getNewsList(Integer node_id, int offset, int limit)throws TaskException{
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), TopicConstans.BASE_URL);
        NewsApiService apiService = httpUtils.getRetrofit().create(NewsApiService.class);

        Call<List<NewsBean>> call =  apiService.getNewsList(node_id,offset,limit);

        List<NewsBean> list = checkCallResult(call);
        return new NewsBeans(list);
    }



}
