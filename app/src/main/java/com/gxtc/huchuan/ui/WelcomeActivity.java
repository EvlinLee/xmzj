package com.gxtc.huchuan.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gxtc.commlibrary.utils.LogUtil;

/**
    如果继承AppCompatActivity，会引发：java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity. 异常
    从错误提示中提到Theme.AppCompat theme，这是因为我们的activity一定是继承了兼容包中的类，
    比如我这里就无意中继承了ActionBarActivity或AppCompatActivity，它来自android.support.v7.app.ActionBarActivity。
    所以就要使用与其配合的AppCompat的theme才行。
 .  如果不是那么强烈需要继承自ActionBarActivity或AppCompatActivity，就直接继承Activity吧。问题自然搞定！
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 用户在使用app时用户点击Home键切出应用到桌面，再从桌面点击应用程序图标试图切回刚刚打开的界面时，
         * 应用会重新启动，而从后台菜单选项中点击进入不会重新打开，经过查找也试过很多种方法，
         * 设置activity的launchMode等都不能解决此问题。其实原因很简单，利用程序安装器打开程序，
         * 启动的Intent是没有带Category，而我们自己打开程序是带了Category,所以只需要在配置Intent.ACTION_MAIN
         * 的Activity判断下有无Category。然后放到通知栏的时候要更具有无Category 来生成启动的Intent。
         * http://blog.csdn.net/u014172743/article/details/50719188
         */
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }
}
