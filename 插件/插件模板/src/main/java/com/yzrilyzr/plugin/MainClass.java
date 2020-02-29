package com.yzrilyzr.plugin;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import com.yzrilyzr.floatingwindow.pluginapi.Window;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.content.BroadcastReceiver;

public class MainClass implements Window.OnButtonDown
{

	private Context ctx;
	//必须实现的构造器
	//Context是主程序的Context,Intent是从MainActivity启动并发过来的
	public MainClass(Context c,Intent e){
		//使用ProxyApi
		ctx=c;
		final Window w=new Window(c,API.px(150),API.px(100))
		.setTitle("title")
		.setOnButtonDown(this)
		.show();
		//只能用这种方式载入xml中的layout
		//xml可以使用com.yzrilyzr.ui包
		//包名为插件的包名，路径是xml在apk中的路径
		ViewGroup v=(ViewGroup) API.parseXmlViewFromFile(ctx,"com.yzrilyzr.plugin","res/layout/main.xml");
		w.addView(v);
		v.getChildAt(1).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				//调用程序并返回结果
				API.startServiceForResult(ctx,new BroadcastReceiver(){
					@Override
					public void onReceive(Context p1, Intent p2)
					{
						Toast.makeText(ctx,"返回颜色:"+Integer.toHexString(p2.getIntExtra("color",0)),0).show();
						//w.setColor(p2.getIntExtra("color",0));
					}
				},"com.yzrilyzr.floatingwindow.apps.ColorPicker");
			}
		});
		//获取传入的值
		Toast.makeText(ctx,"key:"+e.getStringExtra("key"),0).show();
	}
	//实现的接口
	@Override
	public void onButtonDown(int p1)
	{
		Toast.makeText(ctx,"Code:"+p1,0).show();
	}
}
