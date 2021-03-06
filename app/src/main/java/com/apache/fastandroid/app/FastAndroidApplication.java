package com.apache.fastandroid.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.apache.fastandroid.BuildConfig;
import com.apache.fastandroid.SplashActivity;
import com.apache.fastandroid.artemis.BaseApp;
import com.apache.fastandroid.artemis.http.GlobalHttp;
import com.apache.fastandroid.artemis.support.bean.OAuth;
import com.apache.fastandroid.topic.support.exception.FastAndroidExceptionDelegateV2;
import com.apache.fastandroid.topic.support.imageloader.GlideImageLoader;
import com.tesla.framework.common.setting.SettingUtility;
import com.tesla.framework.common.util.log.Logger;
import com.tesla.framework.common.util.log.NLog;
import com.tesla.framework.common.util.sp.SPUtil;
import com.tesla.framework.component.imageloader.IImageLoaderstrategy;
import com.tesla.framework.component.imageloader.ImageLoaderManager;
import com.tesla.framework.network.task.TaskException;
import com.tesla.framework.support.crash.TUncaughtExceptionHandler;
import com.tesla.framework.support.db.FastAndroidDB;
import com.tesla.framework.ui.activity.BaseActivity;
import com.tesla.framework.ui.widget.swipeback.SwipeActivityHelper;

import java.io.File;

/**
 * Created by jerryliu on 2017/3/26.
 */

public class FastAndroidApplication extends Application {
    public static final String TAG = FastAndroidApplication.class.getSimpleName();
    private static Context mContext;

    private static final String client_id = "7024a413";
    private static final String client_secret = "8404fa33ae48d3014cfa89deaa674e4cbe6ec894a57dbef4e40d083dbbaa5cf4";

    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)){
            return;
        }*/
        //freeline
        //FreelineCore.init(this);
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化


        mContext = this;
        BaseApp.onCreate(this);

        SPUtil.init(getApplicationContext(),getPackageName() +"_share");
        //初始化日志
        initLog();
        //初始化crash统计
        initCrashAndAnalysis();

        //初始化模块间通讯
        initModuleBridge();


        //初始化异常处理
        TaskException.config(new FastAndroidExceptionDelegateV2());
        //初始化db
        FastAndroidDB.setDB();

        BaseActivity.setHelper(SwipeActivityHelper.class);

        //初始化图片加载
        IImageLoaderstrategy loaderstrategy = configImageLoader();
        if (loaderstrategy != null){
            ImageLoaderManager.getInstance().setImageLoaderStrategy(loaderstrategy);
            ImageLoaderManager.getInstance().init(getApplicationContext());
        }

        initAuth();
        initHttp();
        //监测内存泄漏
        //initLeakCanry();




    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       // MultiDex.install(base);
    }

    private void initHttp(){

        /**
         * 全局请求的统一配置
         */
        GlobalHttp
                .getInstance()
                //开启全局配置
                //.config()
                //全局的BaseUrl
                .setBaseUrl("https://api.douban.com/")
                //开启缓存策略
                .setCache()
                //全局的请求头信息
                //.setHeaders(headerMaps)
                //全局持久话cookie,保存本地每次都会携带在header中
                .setCookie(false)
                //全局ssl证书认证
                //信任所有证书,不安全有风险
                .setSslSocketFactory()
                //使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(getAssets().open("your.cer"))
                //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(getAssets().open("your.bks"), "123456", getAssets().open("your.cer"))
                //全局超时配置
                .setReadTimeout(10)
                //全局超时配置
                .setWriteTimeout(10)
                //全局超时配置
                .setConnectTimeout(10)
                //全局是否打开请求log日志
                .setLog(true);




        // TODO: 2017/6/26 如果以上配置满足不了你，传入自己的 OkHttpClient 自行设置
       /* OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS);

        GlobalHttp
                .getInstance()
                //.config()
                .setBaseUrl("https://api.douban.com/")
                .setOkHttpClient(builder.build());*/
    }


    private void initLog(){
        if (BuildConfig.LOG_DEBUG){
            NLog.setDebug(true, Logger.DEBUG);
        }
    }

    private void initModuleBridge(){
        //主模块
//        ModularizationDelegate.registerComponent("com.apache.fastandroid.topic.applike.MainAppLike",getApplicationContext());
//        //用户模块
//        ModularizationDelegate.registerComponent("com.apache.fastandroid.user.applike.UserCenterAppLike",getApplicationContext());
//        //主题模块
//        ModularizationDelegate.registerComponent("com.apache.fastandroid.topic.applike.TopicAppLike",getApplicationContext());
//        //小说模块
//        ModularizationDelegate.registerComponent("com.apache.fastandroid.novel.applike.NovelAppLike",getApplicationContext());
    }


    private void initCrashAndAnalysis(){

        //bugly统计
        //CrashReport.initCrashReport(getApplicationContext(),BuildConfig.BUGLY_APP_ID,BuildConfig.LOG_DEBUG);
        //本地crash日志收集  使用bulgy时不能在本地手机日志
        TUncaughtExceptionHandler.getInstance(getApplicationContext(),configCrashFilePath()).init(this, BuildConfig.DEBUG, false, 0, SplashActivity.class);

    }

    private String configCrashFilePath(){
        String path = "";
        try {
            String storageStr = Environment.getExternalStorageState();
            if (storageStr != null && storageStr.equals(Environment.MEDIA_MOUNTED)) {
                File file = this.getExternalFilesDir(null);
                if (file != null) {
                    path = file + File.separator + "crash"; // Android/data/pkgName/files/crash
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }



    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            //强制字体不随着系统改变而改变
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                createConfigurationContext(newConfig);
            } else {
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
        }
        return res;
    }


    private void initAuth(){
        OAuth.client_id = client_id;
        OAuth.client_secret = client_secret;
    }

    /*private RefWatcher mRefWatcher;

    private void initLeakCanry(){

        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        FastAndroidApplication application = (FastAndroidApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
*/
    private IImageLoaderstrategy configImageLoader(){
        String imageLoaderClassName = SettingUtility.getStringSetting("imageLoader");
        if (!TextUtils.isEmpty(imageLoaderClassName)){
            try {
                return (IImageLoaderstrategy) Class.forName(imageLoaderClassName).newInstance();
            }catch (Exception e){
                return new GlideImageLoader();
            }
        }
        //如果没有配置，默认使用glide加载
        return new GlideImageLoader();
    }

}
