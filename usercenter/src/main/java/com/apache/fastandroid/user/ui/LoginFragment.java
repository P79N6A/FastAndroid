package com.apache.fastandroid.user.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.widget.Button;

import com.apache.fastandroid.artemis.base.BaseFragment;
import com.apache.fastandroid.artemis.comBridge.ModularizationDelegate;
import com.apache.fastandroid.artemis.comBridge.ModuleConstans;
import com.apache.fastandroid.artemis.support.bean.Token;
import com.apache.fastandroid.user.support.UserConfigManager;
import com.tesla.framework.network.task.TaskException;
import com.tesla.framework.support.inject.ViewInject;
import com.tesla.framework.ui.activity.FragmentArgs;
import com.tesla.framework.ui.activity.FragmentContainerActivity;


/**
 * Created by jerryliu on 2017/7/9.
 */

public class LoginFragment extends BaseFragment {
    @ViewInject(idStr = "inputLayout_username")
    private TextInputLayout mUserNameinputLayout;

    @ViewInject(idStr = "inputLayout_pwd")
    private TextInputLayout mPwdInputLayout;

    @ViewInject(idStr = "btn_login")
    private Button btn_login;

    public static void start(Context from) {
        Intent intent = new Intent(from, FragmentContainerActivity.class);
        intent.putExtra("className", LoginFragment.class.getName());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        from.startActivity(intent);
    }


    public static void start(Activity from) {
        FragmentContainerActivity.launch(from,LoginFragment.class,null);
    }


    private boolean fromTopicDetail;
    public static void start(Activity from,Boolean fromTopicDetail) {
        FragmentArgs args = new FragmentArgs();
        if (fromTopicDetail != null){
            args.add("fromTopicDetail",fromTopicDetail);
        }
        FragmentContainerActivity.launch(from,LoginFragment.class,args);
    }

    @Override
    public int inflateContentView() {
        //return R.layout.frament_user_login;
        return 0;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        setToolbarTitle("");
        if (savedInstanceSate == null){
            fromTopicDetail = getArguments().getBoolean("fromTopicDetail");
        }else {
            fromTopicDetail = savedInstanceSate.getBoolean("fromTopicDetail");
        }

       /* mUserNameinputLayout.setHint("UserName");
        mPwdInputLayout.setHint("Password");

        mUserNameinputLayout.getEditText().setText("liuxiangxiang1234@163.com");
        mPwdInputLayout.getEditText().setText("gree3502");
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               doLogin();
            }
        });*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fromTopicDetail", fromTopicDetail);
    }

    String pwd;
    private void doLogin(){
        /*final String userName = mUserNameinputLayout.getEditText().getText().toString();
        pwd = mPwdInputLayout.getEditText().getText().toString();
        if (checkUserNameAndPwdInvalidaty(userName,pwd)){

            final ActionCallback callback = new ActionCallback() {
                @Override
                public void onActionSuccess(Object... result) {
                    if (result != null && result.length > 0 && result[0] instanceof Token) {
                        Token token = (Token) result[0];
                        loginSuccess(token);
                    }
                }

                @Override
                public void onActionFailed(int code, String msg) {
                    loginFailed(new TaskException(msg));
                }
            };
            new LoginTask().doLogin(userName,pwd,callback);
        }*/


    }


    private boolean checkUserNameAndPwdInvalidaty(String userName,String pwd){
        /*if (TextUtils.isEmpty(userName)){
            mUserNameinputLayout.setError("用户名不合法");
            return false;
        }else if (TextUtils.isEmpty(pwd)){
            mUserNameinputLayout.setError("密码不合法");
            return false;
        }else {
            mUserNameinputLayout.setErrorEnabled(false);
            mPwdInputLayout.setErrorEnabled(false);
        }*/
        return true;
    }

    public void loginSuccess(Token token) {
        showMessage("登录成功");
        UserConfigManager.getInstance(getContext()).savePwd(pwd);
        if (fromTopicDetail){
            getActivity().finish();
        }else {
            try {
                ModularizationDelegate.getInstance().runStaticAction(ModuleConstans.MODULE_MAIN_NAME+":startMainActivity",null,null,new Object[]{getActivity()});
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().finish();
        }


    }

    public void loginFailed(TaskException exception) {
        showMessage(exception.getMessage());
    }


}
