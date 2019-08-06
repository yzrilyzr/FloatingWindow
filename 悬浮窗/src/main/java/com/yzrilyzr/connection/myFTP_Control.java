package com.yzrilyzr.connection;
import android.content.Intent;
import android.content.Context;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.R;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Switch;
import android.view.View.OnClickListener;
import android.view.View;
import com.yzrilyzr.ui.myProgressBar;
import com.yzrilyzr.ui.myLoadingView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.text.TextWatcher;
import android.text.Editable;
import java.net.InetAddress;

public class myFTP_Control
{
	Window w;
	EditText editport,editu;
	Button b1,b2;
	Switch sw;
	myLoadingView lo;
	public myFTP_Control(Context c,Intent e)
	{
		SharedPreferences sp=util.getSPRead("myFtp");
		w=new Window(c,util.px(230),util.px(200))
		.setTitle("myFTP-服务器已关闭")
		.setIcon("ftp")
		.show();
		myFTP_Server.port=sp.getInt("port",3721);
		myFTP_Server.upath=sp.getString("upath",util.sdcard);
		myFTP_Server.enableU=sp.getBoolean("uenabled",false);
		ViewGroup vg=(ViewGroup) w.addView(R.layout.myftp_control);
		editport=(EditText) vg.findViewById(R.id.myftpcontrolEditText1);
		editu=(EditText) vg.findViewById(R.id.myftpcontrolEditText2);
		sw=(Switch)vg.findViewById(R.id.myftpcontrolSwitch1);
		b1=(Button)vg.findViewById(R.id.myftpcontrolButton1);
		b2=(Button)vg.findViewById(R.id.myftpcontrolButton2);
		lo=(myLoadingView)vg.findViewById(R.id.myftpcontrolProgressBar1);
		editport.setText(myFTP_Server.port+"");
		editu.setText(myFTP_Server.upath);
		editu.setEnabled(myFTP_Server.enableU);
		sw.setChecked(myFTP_Server.enableU);
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2)
			{
				myFTP_Server.enableU=p2;
				editu.setEnabled(p2);
				save();
			}
		});
		if(myFTP_Server.serverrun&&myFTP_Server.serverthread!=null)
		{
			new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						w.setTitle("myFTP-服务器已启动@"+InetAddress.getLocalHost().getHostName());
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}).start();
			b1.setText("停止服务器");
		}
		else
		{
			w.setTitle("myFTP-服务器已关闭");
			b1.setText("启动服务器");
		}
		b1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				if(!myFTP_Server.serverrun&&myFTP_Server.serverthread==null)
				{
					lo.setVisibility(0);
					myFTP_Server.startServer();
					lo.setVisibility(8);
					new Thread(new Runnable(){

						@Override
						public void run()
						{
							try
							{
								w.setTitle("myFTP-服务器已启动@"+InetAddress.getLocalHost().getHostAddress());
							}
							catch(Throwable e)
							{}
						}
					}).start();
					
					b1.setText("停止服务器");
				}
				else
				{
					//lo.setVisibility(0);
					myFTP_Server.serverrun=false;
					w.setTitle("myFTP-服务器已关闭");
					b1.setText("启动服务器");
				}
			}
		});
		editport.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				// TODO: Implement this method
			}

			@Override
			public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				try
				{
					myFTP_Server.port=Integer.parseInt(editport.getText()+"");
					save();
				}
				catch(Throwable e)
				{}
			}

			@Override
			public void afterTextChanged(Editable p1)
			{
				// TODO: Implement this method
			}
		});
		editu.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				// TODO: Implement this method
			}

			@Override
			public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				// TODO: Implement this method
				myFTP_Server.upath=editu.getText()+"";
				save();
			}

			@Override
			public void afterTextChanged(Editable p1)
			{
				// TODO: Implement this method
			}
		});
	}
	private void save()
	{
		util.getSPWrite("myFtp")
		.putBoolean("uenabled",myFTP_Server.enableU)
		.putString("upath",myFTP_Server.upath)
		.putInt("port",myFTP_Server.port)
		.commit();
	}
}
