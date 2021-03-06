package com.apache.fastandroid.artemis.rx;

import com.apache.fastandroid.artemis.mvp.presenter.BasePresenter;
import com.apache.fastandroid.artemis.mvp.view.IView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * Created by 01370340 on 2017/10/10.
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */

public class RxPresenter<V extends IView> extends BasePresenter<V> {
    private List<Disposable> disposables = new ArrayList<>();


    protected void unSubscribe(){
        if (disposables != null && !disposables.isEmpty()){
            for (Disposable disposable : disposables) {
                disposable.dispose();
                disposable = null;
            }
        }
    }

    protected void addSubscribe(Disposable d){
        if (!disposables.contains(d)){
            disposables.add(d);
        }
    }



}
