package com.yzrilyzr.connection;
import android.content.Intent;
import android.content.Context;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.R;

public class myFTP_Control
{
	Window w;
	public myFTP_Control(Context c,Intent e){
		w=new Window(c,util.px(230),util.px(200))
		.setTitle("myFTP-服务器已关闭")
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.myftp_control);
	}
}
