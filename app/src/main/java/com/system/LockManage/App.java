package com.system.LockManage;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.system.LockManage.bean.User;
import com.system.LockManage.util.UserUtil;

/**
 * Created by user on 2016/8/17.
 */
public class App extends Application {

    private static App instance;
    private static User user;
    public static final String videoPath = "file:///android_asset/test.mp4";

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        initUserInfo();
    }

    private void initUserInfo() {
        user = new User();
        user.setName( "Emmanuel");
        user.setPassword("男");
        user.setAdmin_areas( "zhang显个性, xu势待发");
        user.setDepartment("24");

        UserUtil.getInstance(this).putString(UserUtil.KEY_NAME, user.getName());
        UserUtil.getInstance(this).putString(UserUtil.KEY_SEX, user.getPassword());
        UserUtil.getInstance(this).putString(UserUtil.KEY_SIGNATURE, user.getDepartment());
    }

    public static User getUser(){
        if(user != null)
            return user;
        return null;
    }

}
