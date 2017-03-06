package com.project.map.server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.project.map.bean.EventBusBean;
import com.project.map.utils.HnConstant;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class HnLocationService extends Service {

    private static String TAG="HnLocationService";
    private AMapLocationClient locationClient;//定位客户端
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();//参数配置
    private static SimpleDateFormat sdf = null;
    private MyReceiver receiver;


    private static  int  stop_location=1;//停止定位
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:

                    break;
                case 1:
                    if(locationClient!=null){
                        locationClient.stopLocation();
                        locationClient.onDestroy();
                        locationClient=null;
                        locationListener=null;
                    }
                    Log.e(TAG,"停止定位");
                    break;
            }

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadBreceiver();
        initLocation();
        startLocation();
        Log.e(TAG,"定位服务启动");
    }

    private void registerBroadBreceiver() {
        receiver=new  MyReceiver();
        IntentFilter  filter=new IntentFilter();
        filter.addAction(HnConstant.SETTING.NETWORK);
        filter.addAction(HnConstant.SETTING.NOT_NETWORK);
        registerReceiver(receiver,filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        Log.e(TAG,"定位服务销毁");
    }


    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20*1000);
        //设置定位参数
        locationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(3000);//可选，设置定位间隔。默认为3秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                getLocationStr(loc);
            } else {
                Log.d(TAG,"定位失败");
            }
        }
    };
    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();

    }

    /**
     * 根据定位结果返回定位信息的字符串
     * @param
     * @return
     */
    public synchronized String getLocationStr(AMapLocation location){
        if(null == location){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if(location.getErrorCode() == 0){

            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            if (location.getProvider().equalsIgnoreCase(
                    android.location.LocationManager.GPS_PROVIDER)) {
                // 以下信息只有提供者是GPS时才会有
                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : "
                        + location.getSatellites() + "\n");
            } else {
                // 提供者是GPS时是没有以下信息的
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss:sss") + "\n");
            }
            String city=location.getCity();
            String pro=location.getProvider();

            DecimalFormat df   = new DecimalFormat("######0.000000");
            double   latitude=   location.getLatitude();
            double   longitude= (float) location.getLongitude();
            String latitudeResult=df.format(latitude);
            String longitudeResult=df.format(longitude);
            //停止定位
             stopLocation();



        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        //定位之后的回调时间
         sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss:sss") + "\n");
         Log.e(TAG,"定位信息:"+sb.toString());
         EventBus.getDefault().post(new EventBusBean(0,HnConstant.SETTING.CITY,sb.toString()));
        return sb.toString();
    }


    public synchronized static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        if (l <= 0l) {
            l = System.currentTimeMillis();
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }


    /**
     *广播接收器
     */
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.i(TAG,"接后到的广播："+action);
            if(HnConstant.SETTING.NETWORK.equals(action)){//接收到有网络状态
                   rsetLocaton();
            }else if(HnConstant.SETTING.NOT_NETWORK.equals(action)){//无网络状态

                  startLocation();
            }
        }
    }

    /**
     * 定位地址
     */
    private void rsetLocaton() {
            initLocation();
            startLocation();

    }

    /**
     * 停止定位
     */
    public void stopLocation(){
        Message msg = handler.obtainMessage(stop_location);
        handler.sendMessage(msg);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
