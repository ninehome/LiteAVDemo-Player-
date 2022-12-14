package com.tencent.liteav.demo;

import android.content.Context;

import com.tencent.live2.V2TXLivePremier;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveBaseListener;


public class TXCSDKService {
    private static final String TAG        = "TXCSDKService";
    // 如何获取License? 请参考官网指引 https://cloud.tencent.com/document/product/454/34750
//    private static final String licenceUrl =
//            "请替换成您的licenseUrl";
//    private static final String licenseKey = "请替换成您的licenseKey";

    public   static  final String  licenceUrl = "http://license.vod2.myqcloud.com/license/v1/e3919ba44468564d8b1dc0e73907fafc/TXLiveSDK.licence";
    public   static  final String  licenseKey = "f64948d3b2b3dd7eaa52483886f7038c";

    private TXCSDKService() {
    }

    /**
     * 初始化腾讯云相关sdk。
     * SDK 初始化过程中可能会读取手机型号等敏感信息，需要在用户同意隐私政策后，才能获取。
     *
     * @param appContext The application context.
     */
    public static void init(Context appContext) {
        TXLiveBase.getInstance().setLicence(appContext, licenceUrl, licenseKey);

        TXLiveBase.setListener(new TXLiveBaseListener() {
            @Override
            public void onUpdateNetworkTime(int errCode, String errMsg) {
                if (errCode != 0) {
                    TXLiveBase.updateNetworkTime();
                }
            }
        });
        TXLiveBase.updateNetworkTime();

        // 短视频licence设置

    }
}
