package com.apache.fastandroid.user.delegate.impl;

import android.app.Activity;
import android.os.Bundle;

import com.apache.fastandroid.artemis.comBridge.DelegateException;
import com.apache.fastandroid.artemis.comBridge.IActionDelegate;
import com.apache.fastandroid.artemis.comBridge.ModularizationDelegate;
import com.apache.fastandroid.artemis.comBridge.ModuleConstans;
import com.apache.fastandroid.user.ui.LoginedListFragment;

/**
 * Created by 01370340 on 2017/9/3.
 */

public class StartLoginedListActivity implements IActionDelegate {
    @Override
    public void runAction(Bundle args, IActionCallback callback, Object... extras) throws DelegateException {
        if (extras != null && extras.length > 0 && extras[0] instanceof Activity){
            Activity from = (Activity) extras[0];
            LoginedListFragment.start(from);
        }
    }
}