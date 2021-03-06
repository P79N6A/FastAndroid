package com.apache.fastandroid.topic.support.permission;

import android.Manifest;

import com.tesla.framework.support.action.IAction;
import com.tesla.framework.component.permission.ASinglePermissionsAction;
import com.tesla.framework.ui.activity.BaseActivity;


/**
 * SD卡读写权限检查
 *
 */
public class SdcardPermissionAction extends ASinglePermissionsAction {

    public SdcardPermissionAction(BaseActivity context, IAction parent) {
        super(context, parent, context.getActivityHelper(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onPermissionDenied(boolean alwaysDenied) {
        if (alwaysDenied) {
            ((BaseActivity) getContext()).showMessage("SD卡读写授权被禁用了，请去设置界面打开此权限");
        }
        else {
            ((BaseActivity) getContext()).showMessage("取消SD卡读写授权");
        }
    }

}
