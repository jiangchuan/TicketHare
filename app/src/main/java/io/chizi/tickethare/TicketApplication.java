package io.chizi.tickethare;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Jiangchuan on 5/19/17.
 */

public class TicketApplication extends Application {

    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }
}
