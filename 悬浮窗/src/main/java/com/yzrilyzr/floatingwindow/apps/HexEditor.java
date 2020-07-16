package com.yzrilyzr.floatingwindow.apps;
import android.content.*;
import android.view.*;
import android.view.View.*;
import com.yzrilyzr.floatingwindow.*;
import com.yzrilyzr.floatingwindow.view.*;
import com.yzrilyzr.icondesigner.*;
import com.yzrilyzr.myclass.*;
import com.yzrilyzr.ui.*;
import java.io.*;

import com.yzrilyzr.floatingwindow.Window;

public class HexEditor implements OnClickListener,Window.OnButtonDown
{
	String file;
	Window w;
	HexView hv;
	Context ctx;

	ViewGroup vg;
	public HexEditor(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(280),util.px(500))
			.setTitle("Hex编辑器")
			.setIcon("hex")
			.setOnButtonDown(this)
			.show();
		vg=(ViewGroup) w.addView(R.layout.window_hexeditor);
		hv=(HexView) vg.findViewById(R.id.windowhexeditorHexView1);
		(vg.findViewById(R.id.windowhexeditorVecView1)).setOnClickListener(this);
		(vg.findViewById(R.id.windowhexeditorVecView2)).setOnClickListener(this);
		(vg.findViewById(R.id.windowhexeditorVecView3)).setOnClickListener(this);
		try
		{
			String d="低音可能刻录机了都那么小多了佛罗里达客流量fiejnekfidemekslsfjeje461946734619137346😇😁😐😃😑😁😯😃☺😐😢😳😥";
			StringBuilder bd=new StringBuilder();
			for(int i=0;i<10;i++)bd.append(d);
			hv.loadStream(new ByteArrayInputStream(bd.toString().getBytes()));

		}
		catch (Exception pe)
		{
			util.toast(pe);
		}
	}
	@Override
	public void onClick(final View p1)
	{
		switch(p1.getId())
		{
			case R.id.windowhexeditorVecView1:
				API.startServiceForResult(ctx,w,new BroadcastReceiver(){
						@Override
						public void onReceive(Context c,Intent e)
						{
							try
							{
								file=e.getStringExtra("path");
								hv.loadStream(new FileInputStream(file));
							}
							catch(Exception ex)
							{
								util.toast("打开失败");
							}
						}
					},cls.EXPLORER);
				break;
			case R.id.windowlongtexteditorVecView2:
				if(new File(file).exists())
					new myDialog.Builder(ctx)
						.setMessage("文件已存在，是否覆盖？")
						.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								save();
							}
						})
						.setNegativeButton("取消",null)
						.show();
				else API.startServiceForResult(ctx,new Intent().putExtra("save",true).putExtra("savefile","新建文件.txt"),w,new BroadcastReceiver(){
							@Override
							public void onReceive(Context c,Intent e)
							{
								file=e.getStringExtra("path");
								if(new File(file).exists())
									new myDialog.Builder(ctx)
										.setMessage("文件已存在，是否覆盖？")
										.setPositiveButton("确定",new DialogInterface.OnClickListener(){
											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												save();
											}
										})
										.setNegativeButton("取消",new DialogInterface.OnClickListener(){
											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												file="";
											}
										})
										.show();
								else save();
							}
						},cls.EXPLORER);

				break;
		}
	}
	private void save()
	{
		try
		{
			File f=new File(file);
			w.setTitle(f.getName());
			BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(f));
			for(byte[] b:hv.data)os.write(b);
			os.flush();
			os.close();
			myToast.makeText(ctx,"保存成功",0).show();
			w.setTitle(w.getTitle());
			hv.changed=false;
		}
		catch (Exception e)
		{
			myToast.makeText(ctx,"保存失败",0).show();
		}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			if(hv.changed)
				new myDialog.Builder(ctx)
					.setMessage("是否保存对 \""+new File(file).getName()+"\" 的更改?")
					.setPositiveButton("保存",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							HexEditor.this.onClick(vg.findViewById(R.id.windowlongtexteditorVecView2));
							//destroy=true;
						}
					})
					.setNegativeButton("不保存",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							//destroy=true;
						}
					})
					.setNeutralButton("返回",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							w.show();
						}
					})
					.show()
					.setCancelable(false);
			//else destroy=true;
		}
	}
	
}
