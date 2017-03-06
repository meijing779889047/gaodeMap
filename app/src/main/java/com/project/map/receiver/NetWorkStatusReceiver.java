package com.project.map.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.project.map.utils.HnConstant;
import com.project.map.utils.HnUtil;

import static com.project.map.utils.HnConstant.SETTING.NETWORK;


public class NetWorkStatusReceiver extends BroadcastReceiver {


    private ConnectivityManager mConnectivityManager;
    private String TAG="NetWorkStatusReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
         String action=intent.getAction();
         if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){//获取网络状态改变广播
             Log.i(TAG,"接收到网络状态改变广播");
             mConnectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
             NetworkInfo  mNetworkInfo=mConnectivityManager.getActiveNetworkInfo();
             if(mNetworkInfo!=null&&mNetworkInfo.isAvailable()){
                 HnUtil.sendBroadCastReceiver(context, NETWORK);
//                 //网络类型
//                 String name = mNetworkInfo.getTypeName();
//                 if(mNetworkInfo.getType()==ConnectivityManager.TYPE_WIFI){
//                     /////WiFi网络
//
//                 }else if(mNetworkInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
//                     /////有线网络
//
//                 }else if(mNetworkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
//
//                 }

             }else{
                 HnUtil.sendBroadCastReceiver(context, HnConstant.SETTING.NOT_NETWORK);
             }
         }
    }


}
