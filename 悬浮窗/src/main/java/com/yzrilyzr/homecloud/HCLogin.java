package com.yzrilyzr.homecloud;
import android.content.*;
import android.view.*;
import com.yzrilyzr.floatingwindow.*;
import com.yzrilyzr.myclass.*;

import com.yzrilyzr.floatingwindow.Window;

public class HCLogin
{
	Window w;
	public HCLogin(Context c,Intent e){
		w=new Window(c,util.px(250),util.px(250))
		.setTitle("登录屋里云帐户")
		.setBar(0,8,0)
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.homecloud_client_login);
	}
}
