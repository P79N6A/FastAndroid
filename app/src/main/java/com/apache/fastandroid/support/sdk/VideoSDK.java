package com.apache.fastandroid.support.sdk;

import com.apache.fastandroid.support.bean.VideoResultBean;
import com.tesla.framework.common.setting.Setting;
import com.tesla.framework.network.biz.ABizLogic;
import com.tesla.framework.network.http.HttpConfig;
import com.tesla.framework.network.http.Params;
import com.tesla.framework.network.task.TaskException;

/**
 * Created by jerryliu on 2017/4/11.
 */

public class VideoSDK extends ABizLogic {
    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.baseUrl = ApiConstans.Urls.YOUKU_VIDEOS_URLS;
        return httpConfig;
    }

    public VideoSDK() {
    }

    public VideoSDK(CacheMode cacheMode) {
        super(cacheMode);
    }

    public static VideoSDK newInstance(){
        return new VideoSDK();
    }

    public static VideoSDK newInstance(CacheMode mode){
        return new VideoSDK(mode);
    }


    public VideoResultBean loadViedoData(String category, int pageNum) throws TaskException{
        Setting setting = newSetting("loadImages","v2/searches/video/by_keyword.json","加载图片");
        //setting.getExtras().put(CACHE_UTILITY, newSettingExtra("loadImages",PicCacheUtility.class.getName(),"加载图片缓存"));

        Params params = new Params();
        params.addParameter("keyword",category);
        params.addParameter("page",String.valueOf(pageNum));
        params.addParameter("count","10");
        params.addParameter("public_type","all");
        params.addParameter("paid", "0");
        params.addParameter("period", "today");
        params.addParameter("orderby","published");
        params.addParameter("client_id", "6ecd6970268b4c53");

//        sb.append("?keyword=");
//        try {
//            sb.append(URLEncoder.encode(category, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        sb.append("&page=");
//        sb.append(pageNum);
//        sb.append("&count=");
//        sb.append(PAGE_LIMIT);
//        sb.append("&public_type=all&paid=0&period=today&orderby=published&client_id=6ecd6970268b4c53");



        return doGet(configHttpConfig(),setting,params,VideoResultBean.class);
    }



}
