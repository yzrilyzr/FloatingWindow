package com.yzrilyzr.floatingwindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.yzrilyzr.myclass.util;
import java.util.HashMap;

public class mBroadcastReceiver extends BroadcastReceiver
{
	public static int index=1;
	public static final HashMap<Integer,Object> cbk=new HashMap<Integer,Object>();
    @Override
    public void onReceive(Context p1, Intent p2)
    {
		util.ctx=p1;
		Window.readData();
		//System.out.println(p2);
		String act=p2.getAction();
		if("com.yzrilyzr.close".equals(act))PluginService.fstop(p1);
		else if("com.yzrilyzr.callback".equals(act)){
			int code=p2.getIntExtra("rescode",0);
			if(code!=0){
				try
				{
					Object h=cbk.remove(code);
					if(h instanceof BroadcastReceiver)((BroadcastReceiver)h).onReceive(p1,p2);
					else h.getClass().getMethod("onReceive",Context.class,Intent.class).invoke(h,p1,p2);
				}
				catch (Throwable e)
				{}
			}
		}
		else if("com.yzrilyzr.sysprinter".equals(act)){
			String print=p2.getStringExtra("out");
			String err=p2.getStringExtra("err");
			if(print!=null)System.out.println(print);
			if(err!=null)System.err.println(err);
		}
		else if(Intent.ACTION_PACKAGE_ADDED.equals(act)||Intent.ACTION_PACKAGE_INSTALL.equals(act)){
			String packageName=p2.getData().getSchemeSpecificPart();
			p1.startActivity(new Intent(Intent.ACTION_DELETE,Uri.parse("package:"+packageName)));
		}
		else if(Intent.ACTION_BOOT_COMPLETED.equals(act)&&Window.startonboot)p1.startService(new Intent(p1,PluginService.class));
    }

}
