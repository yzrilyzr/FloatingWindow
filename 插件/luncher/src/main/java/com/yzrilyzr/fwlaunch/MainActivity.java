package com.yzrilyzr.fwlaunch;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		TextView t=(TextView) findViewById(R.id.mainTextView1);
		try
		{
			PackageManager p=getPackageManager();
			try
			{
				PackageInfo in=p.getPackageInfo("com.yzrilyzr.floatingwindow",PackageInfo.INSTALL_LOCATION_AUTO);
				ApplicationInfo ai=p.getApplicationInfo("com.yzrilyzr.floatingwindow",PackageManager.GET_UNINSTALLED_PACKAGES);
				if(!"悬浮窗".equals(ai.loadLabel(p)))
				{
					t.append("修改的软件");
					return;
				}
			}
			catch (Exception e)
			{
				t.append("无法找到主程序");
				return;
			}
		}
		catch(Throwable e)
		{
			t.append(e+"");
		}
    }
public void start(View v){
	Intent intent=new Intent();
	intent.setAction("com.yzrilyzr.Service");
	intent.setPackage("com.yzrilyzr.floatingwindow");
	intent.putExtra("pkg",getPackageName());
	intent.putExtra("class",getPackageName()+".Main");
	startService(intent);
}
public void stop(View v){
	Intent intent=new Intent();
	intent.setAction("com.yzrilyzr.Service");
	intent.setPackage("com.yzrilyzr.floatingwindow");
	intent.putExtra("APP_STOP",true);
	startService(intent);
}
}
