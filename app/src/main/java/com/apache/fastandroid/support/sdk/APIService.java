package com.apache.fastandroid.support.sdk;

import com.apache.fastandroid.support.bean.ImageResultBeans;
import com.apache.fastandroid.update.UpdateResponseBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jerryliu on 2017/7/26.
 */

public interface APIService {

    @GET("/data/imgs")
    Call<ImageResultBeans> loadImages(@Query("col")String col, @Query("tag")String tag,
                                      @Query("pn")String pn,@Query("rn")String rn,
                                      @Query("from")String from);

    @GET("/checkAppVersion")
    Call<UpdateResponseBean> checkAppVersion(@Query("col")int versionCode);
}
