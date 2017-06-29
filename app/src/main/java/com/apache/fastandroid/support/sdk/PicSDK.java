package com.apache.fastandroid.support.sdk;

import com.apache.fastandroid.support.bean.ImageResultBeans;
import com.tesla.framework.common.setting.Setting;
import com.tesla.framework.network.biz.ABizLogic;
import com.tesla.framework.network.http.HttpConfig;
import com.tesla.framework.network.http.Params;
import com.tesla.framework.network.task.TaskException;

/**
 * Created by jerryliu on 2017/4/11.
 */

public class PicSDK extends ABizLogic {
    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.baseUrl = ApiConstans.Urls.BAIDU_IMAGES_URLS;
        return httpConfig;
    }

    public PicSDK() {
    }

    public PicSDK(CacheMode cacheMode) {
        super(cacheMode);
    }

    public static PicSDK newInstance(){
        return new PicSDK();
    }

    public static PicSDK newInstance(CacheMode mode){
        return new PicSDK(mode);
    }


    public ImageResultBeans loadImageData(String category, int pageNum) throws TaskException{
        Setting setting = newSetting("loadImages","data/imgs","加载图片");
        //setting.getExtras().put(CACHE_UTILITY, newSettingExtra("loadImages",PicCacheUtility.class.getName(),"加载图片缓存"));

        Params params = new Params();
        params.addParameter("col",category);
        params.addParameter("tag","全部");
        params.addParameter("pn",String.valueOf(pageNum));
        params.addParameter("rn","10");
        params.addParameter("from", "1");

        return doGet(configHttpConfig(),setting,params,ImageResultBeans.class);
    }



}