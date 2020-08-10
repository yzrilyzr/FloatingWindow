package com.yzrilyzr.homecloud;
import android.widget.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myListView;
import com.yzrilyzr.ui.myLoadingView;
import com.yzrilyzr.ui.myTextView;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.*;
import java.net.*;

public class ServerMain
{
	Window w;
	EditText editport,editu,thr;
	Button b1,b2;
	Switch sw;
	myLoadingView lo;
	TextView ipadd;
	public ServerMain(final Context c,Intent e)
	{
		SharedPreferences sp=util.getSPRead("myFtp");
		w=new Window(c,util.px(230),util.px(270))
			.setTitle("屋里云-服务器已关闭")
			.setIcon("homecloud/hcserver")
			.show();
		FileServer.port=sp.getInt("port",3721);
		FileServer.upath=sp.getString("upath",util.sdcard);
		FileServer.enableU=sp.getBoolean("uenabled",false);
		FileServer.maxthread=sp.getInt("maxthread",10);
		FileServer.loadUser();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.homecloud_control);
		editport=(EditText) vg.findViewById(R.id.myftpcontrolEditText1);
		editu=(EditText) vg.findViewById(R.id.myftpcontrolEditText2);
		thr=(EditText) vg.findViewById(R.id.myftpcontrolEditText3);
		sw=(Switch)vg.findViewById(R.id.myftpcontrolSwitch1);
		b1=(Button)vg.findViewById(R.id.myftpcontrolButton1);
		b2=(Button)vg.findViewById(R.id.myftpcontrolButton2);
		lo=(myLoadingView)vg.findViewById(R.id.myftpcontrolProgressBar1);
		ipadd=(TextView)vg.findViewById(R.id.homecloudcontrolTextView1);
		editport.setText(FileServer.port+"");
		thr.setText(FileServer.maxthread+"");
		editu.setText(FileServer.upath);
		editu.setEnabled(FileServer.enableU);
		sw.setChecked(FileServer.enableU);
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					FileServer.enableU=p2;
					editu.setEnabled(p2);
					save();
				}
			});

		b1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					try
					{
						lo.setVisibility(0);
						if(!FileServer.serverrun&&FileServer.serverthread==null)
						{
							FileServer.startServer();
						}
						else
						{
							FileServer.serverrun=false;
							FileServer.server.close();
						}
						lo.setVisibility(8);
						updateData();
					}
					catch(Throwable e)
					{}
				}
			});
		b2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					final myListView list=new myListView(c);
					final myEditText edit=new myEditText(c);
					final ArrayList<FileServer.User> us=new ArrayList<FileServer.User>();
					edit.setHint("搜索用户…");
					new Window(c,util.px(235),util.px(352))
						.setTitle("用户管理")
						.setIcon("menu")
						.addView(edit)
						.addView(list)
						.show();
					list.setAdapter(new BaseAdapter(){
							@Override
							public int getCount()
							{
								// TODO: Implement this method
								return us.size()+1;
							}

							@Override
							public Object getItem(int p1)
							{
								// TODO: Implement this method
								return null;
							}

							@Override
							public long getItemId(int p1)
							{
								// TODO: Implement this method
								return 0;
							}

							@Override
							public View getView(int p1, View p2, ViewGroup p3)
							{
								if(p1==0)
								{
									myTextView t=new myTextView(util.ctx);
									t.setEllipsize(TextUtils.TruncateAt.END);
									int y=util.px(3);
									t.setPadding(y,y,y,y);
									t.setText("添加用户…");
									return t;
								}
								myTextView t=new myTextView(util.ctx);
								t.setEllipsize(TextUtils.TruncateAt.END);
								int y=util.px(3);
								t.setPadding(y,y,y,y);
								t.setText(us.get(p1-1).usr);
								return t;
							}
							@Override
							public void notifyDataSetChanged()
							{
								us.clear();
								Map<String,FileServer.User> m=FileServer.users;
								String ek=edit.getText().toString().toLowerCase();
								if("".equals(ek))for(FileServer.User d:m.values())us.add(d);
								else for(FileServer.User d:m.values())if(d.usr.toLowerCase().contains(ek))us.add(d);
								Collections.sort(us,new Comparator<FileServer.User>(){
										@Override
										public int compare(FileServer.User p1, FileServer.User p2)
										{
											// TODO: Implement this method
											return p1.usr.compareToIgnoreCase(p2.usr);
										}
									});
								super.notifyDataSetChanged();
							}
						});
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
					list.setOnItemClickListener(new OnItemClickListener(){

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4)
							{
								FileServer.User iu=null;
								if(p3==0)iu=new FileServer.User("","","");
								else iu=us.get(p3-1);
								final FileServer.User u=iu;
								myDialog.Builder builder = new myDialog.Builder(c);
								builder.setTitle("修改用户:"+u.usr);
								final myEditText edi=new myEditText(c);
								final myEditText edi2=new myEditText(c);
								final myEditText edi3=new myEditText(c);
								edi.setText(u.usr);
								edi3.setText(u.path);
								edi.setHint("用户名");
								edi2.setHint("重置密码");
								edi3.setHint("默认路径(留空则使用用户名为路径)");
								LinearLayout l=new LinearLayout(c);
								l.setOrientation(1);
								builder.setView(l);
								l.addView(edi);
								util.setWeight(l);
								util.setWeight(edi);
								l.addView(edi2);
								util.setWeight(edi2);
								l.addView(edi3);
								util.setWeight(edi3);
								builder.setPositiveButton("修改",new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface d,int p)
										{
											String a=edi.getText().toString();
											String b=edi2.getText().toString();
											String c=edi3.getText().toString();
											u.usr=a;
											u.pwd=FileServer.hash(b);
											u.path=c;
											FileServer.users.put(a,u);
											((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
											FileServer.saveUser();
										}});
								builder.setNegativeButton("取消",null);
								builder.show();
								builder.setNeutralButton("删除",new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface p1, int p2)
										{
											FileServer.users.remove(u.usr);
											((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
											FileServer.saveUser();
										}
									});
							}
						});
				}});
		editport.addTextChangedListener(new TextWatcher()
			{

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
						FileServer.port=Integer.parseInt(editport.getText()+"");
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
					FileServer.upath=editu.getText()+"";
					save();
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
		thr.addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
					try
					{
						FileServer.maxthread=Integer.parseInt(thr.getText()+"");
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
		updateData();
	}
	public void updateData()
	{
		if(FileServer.serverrun&&FileServer.serverthread!=null)
		{
			/*new Thread(new Runnable(){
			 @Override
			 public void run()
			 {*/
			try
			{
				w.setTitle("屋里云-服务器已启动");
				Enumeration all=NetworkInterface.getNetworkInterfaces();
				InetAddress ip=null;
				String ips=null;
				while(all.hasMoreElements()&&ips==null)
				{
					NetworkInterface nf=(NetworkInterface)all.nextElement();
					Enumeration addp=nf.getInetAddresses();
					while(addp.hasMoreElements())
					{
						ip=(InetAddress)addp.nextElement();
						if(ip!=null&&(ip instanceof Inet4Address))
						{
							ips=ip.getHostAddress();
							break;
						}
					}
				}
				if(ips==null)ips="未知";
				ipadd.setText("本机IP:"+ips);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			//}
			//}).start();
			b1.setText("停止服务器");
		}
		else
		{
			w.setTitle("屋里云-服务器已关闭");
			ipadd.setText("");
			b1.setText("启动服务器");
		}
	}
	private void save()
	{
		util.getSPWrite("homecloud_server_prop")
			.putBoolean("uenabled",FileServer.enableU)
			.putString("upath",FileServer.upath)
			.putInt("port",FileServer.port)
			.putInt("maxthread",FileServer.maxthread)
			.commit();
	}
}
