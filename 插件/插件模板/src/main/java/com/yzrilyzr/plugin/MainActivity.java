package com.yzrilyzr.plugin;

import android.app.*;
import android.os.*;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import android.content.Intent;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		//第一种:普通启动，字符串为启动的类
       	//API.startService(this,"com.yzrilyzr.plugin.MainClass");
		
		//第二种:含参数启动，字符串为启动的类
       	API.startService(this,
		new Intent().putExtra("key","value"),
		"com.yzrilyzr.plugin.MainClass");
		
		//防止卡屏
		finish();
    }
}
