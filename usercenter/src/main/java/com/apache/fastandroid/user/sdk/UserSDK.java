package com.apache.fastandroid.user.sdk;

import com.apache.fastandroid.artemis.BaseBizLogic;
import com.apache.fastandroid.artemis.api.TokenService;
import com.apache.fastandroid.artemis.retrofit.BaseHttpUtilsV2;
import com.apache.fastandroid.artemis.support.bean.OAuth;
import com.apache.fastandroid.artemis.support.bean.Token;
import com.apache.fastandroid.artemis.support.bean.UserDetail;
import com.tesla.framework.FrameworkApplication;
import com.tesla.framework.network.http.HttpConfig;

import rx.Observable;

/**
 * Created by 01370340 on 2017/9/1.
 */

public class UserSDK extends BaseBizLogic {

    public static UserSDK newInstance(){
        return new UserSDK();
    }

    @Override
    protected HttpConfig configHttpConfig() {
        return new HttpConfig();
    }


    public Observable<Token> login(String user_name, String password) {
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), "https://diycode.cc/api/v3/");
        TokenService apiService = httpUtils.getRetrofit().create(TokenService.class);
        Observable<Token> observable =  apiService.getTokenV2(OAuth.client_id, OAuth.client_secret, OAuth.GRANT_TYPE_LOGIN, user_name, password);
        return observable;
    }

    public Observable<UserDetail> getMe(){
        BaseHttpUtilsV2 httpUtils = BaseHttpUtilsV2.getInstance(FrameworkApplication.getContext(), "https://diycode.cc/api/v3/");
        UserApi apiService = httpUtils.getRetrofit().create(UserApi.class);
        Observable<UserDetail> observable =  apiService.getMe();
        return observable;
    }






}
