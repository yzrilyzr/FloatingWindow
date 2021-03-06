package com.yzrilyzr.floatingwindow.apps;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myTextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ShareFW implements OnClickListener,Runnable
{
	public ShareFW(Context c,Intent e)
	{
		new Thread(this).start();
		myTextView mtv=new myTextView(c);
		mtv.setText("1.请打开手机蓝牙设置\n2.配对设备(对方需要打开可见性)\n3.点击下方的分享发送");
		myButton f=new myButton(c);
		f.setText("分享");
		f.setOnClickListener(this);
		new Window(c,util.px(200),util.px(140))
		.addView(mtv)
		.addView(f)
		.setIcon("loadout")
		.setTitle("分享本程序")
		.show();
	}

	public void run()
	{
		try
		{
			BluetoothManager b=(BluetoothManager) util.ctx.getSystemService(util.ctx.BLUETOOTH_SERVICE);
			BluetoothAdapter a=b.getAdapter();
			if(!a.isEnabled())a.enable();
			util.toast("请使用蓝牙分享");
		}
		catch(Throwable ex)
		{
			util.toast("无法打开蓝牙");
		}
	}
	@Override
	public void onClick(View p1)
	{
		try
		{
			String gf=util.ctx.getPackageManager().getPackageInfo(util.ctx.getPackageName(),PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
			FileChannel in=new FileInputStream(gf).getChannel();
			FileChannel out=new FileOutputStream(util.mainDir+"悬浮窗.apk").getChannel();
			in.transferTo(0,in.size(),out);
			in.close();
			out.close();
			File g=new File(util.mainDir+"悬浮窗.apk");
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setType(util.getMIMEType(g));
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(g));
			util.ctx.startActivity(intent);
		}
		catch (Throwable e)
		{}
	}
}
