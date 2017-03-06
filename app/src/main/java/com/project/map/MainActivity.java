package com.project.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.project.map.bean.EventBusBean;
import com.project.map.server.HnLocationService;
import com.project.map.utils.HnConstant;
import com.project.map.utils.HnUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private TextView  tv_Info;
    private EventBus  eventBus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sHA1
        Log.e("SHa1",HnUtil.sHA1(this));
        eventBus.getDefault().register(this);
        tv_Info= (TextView) findViewById(R.id.tv_info);
        //启动定位服务
        startService(new Intent(this, HnLocationService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.getDefault().unregister(this);
    }

    @Subscribe
    public   void onCallBack(EventBusBean  event){
        if(event!=null){
            if(HnConstant.SETTING.CITY.equals(event.getType())){
                String info= (String) event.getData();
                tv_Info.setText(info);
            }
        }

    }
}
