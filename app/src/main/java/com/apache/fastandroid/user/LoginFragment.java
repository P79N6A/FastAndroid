package com.apache.fastandroid.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.apache.fastandroid.MainActivity;
import com.apache.fastandroid.R;
import com.apache.fastandroid.base.BaseFragment;
import com.apache.fastandroid.user.bean.UserBean;
import com.tesla.framework.common.util.KeyGenerator;
import com.tesla.framework.common.util.view.ViewUtils;
import com.tesla.framework.network.task.TaskException;
import com.tesla.framework.network.task.WorkTask;
import com.tesla.framework.ui.activity.FragmentContainerActivity;

/**
 * Created by jerryliu on 2017/7/9.
 */

public class LoginFragment extends BaseFragment {

    private TextInputLayout mUserNameinputLayout;
    private TextInputLayout mPwdInputLayout;
    private Button btn_login;

    public static void start(Activity from) {
        FragmentContainerActivity.launch(from,LoginFragment.class,null);
    }

    @Override
    public int inflateContentView() {
        return R.layout.frament_user_login;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        setToolbarTitle("");


        btn_login = (Button) findViewById(R.id.btn_login);

        mUserNameinputLayout = (TextInputLayout) findViewById(R.id.inputLayout_username);
        mPwdInputLayout = (TextInputLayout) findViewById(R.id.inputLayout_pwd);

        mUserNameinputLayout.setHint("UserName");
        mPwdInputLayout.setHint("Password");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               doLogin();
            }
        });
    }

    public void doLogin(){
        String userName = mUserNameinputLayout.getEditText().getText().toString();
        String pwd = mPwdInputLayout.getEditText().getText().toString();
       /* if (!validateUserName(userName)){
            mUserNameinputLayout.setError("用户名不合法");
            return;
        }else if (!validatePwd(pwd)){
            mPwdInputLayout.setError("密码不合法");
            return;
        }else {
            mUserNameinputLayout.setErrorEnabled(false);
            mPwdInputLayout.setErrorEnabled(false);
        }*/
        if (checkUserNameAndPwdInvalidaty(userName,pwd)){
            new LoginTask().execute(userName,pwd);
        }
    }


    private boolean checkUserNameAndPwdInvalidaty(String userName,String pwd){
        if (TextUtils.isEmpty(userName)){
            mUserNameinputLayout.setError("用户名不合法");
            return false;
        }else if (TextUtils.isEmpty(pwd)){
            mUserNameinputLayout.setError("密码不合法");
            return false;
        }else {
            mUserNameinputLayout.setErrorEnabled(false);
            mPwdInputLayout.setErrorEnabled(false);
        }
        return true;
    }




    class LoginTask extends WorkTask<String,Void,UserBean>{

        @Override
        protected void onPrepare() {
            super.onPrepare();
            ViewUtils.createProgressDialog(getActivity(),"正在登录中...",0).show();
        }

        @Override
        public UserBean workInBackground(String... params) throws TaskException {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return UserSDK.newInstance().doLogin(params[0],params[1]);
        }

        @Override
        protected void onSuccess(UserBean userBean) {
            super.onSuccess(userBean);
            loginSuccess(userBean);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);
            loginFailed(exception);
        }

        @Override
        protected void onFinished() {
            super.onFinished();
            ViewUtils.dismissProgressDialog();
        }
    }

    public void loginSuccess(UserBean userBean) {
        showMessage("登录成功");
        //保存登陆信息
        String encypedUserName = KeyGenerator.generateMD5(userBean.getUserName());
        String encrypedPwd = KeyGenerator.generateMD5(userBean.getPassword());

        UserConfigManager.getInstance().setUserName(encypedUserName);
        UserConfigManager.getInstance().setPwd(encrypedPwd);

        MainActivity.launch(getActivity());
        getActivity().finish();
    }


    public void loginFailed(TaskException exception) {
        showMessage(exception.getMessage());
    }


}
