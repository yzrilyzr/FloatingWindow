package com.yzrilyzr.homecloud;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.yzrilyzr.floatingwindow.*;
import com.yzrilyzr.floatingwindow.viewholder.*;
import com.yzrilyzr.myclass.*;
import com.yzrilyzr.ui.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.yzrilyzr.floatingwindow.Window;

public class ClientMain implements OnClickListener
{
	private Context ctx;
	private Window w;
	private Client client;
	private User lastLogin;
	private static boolean isLogin=false;
	public ClientMain(Context c,Intent e){
		if(!isLogin){
			new HCLogin(c,e);
			return;
		}
		ctx=c;
		w=new Window(c,util.px(230),util.px(280))
			.setTitle("屋里云-客户端")
			.setIcon("homecloud/homecloud")
			.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.homecloud_client_main);
		View v=vg.findViewById(R.id.homecloudclientmainLinearLayout1);
		v.setClickable(true);
		v.setOnClickListener(this);
		v=vg.findViewById(R.id.homecloudclientmainLinearLayout2);
		v.setClickable(true);
		v.setOnClickListener(this);
		v=vg.findViewById(R.id.homecloudclientmainLinearLayout3);
		v.setClickable(true);
		v.setOnClickListener(this);
		v=vg.findViewById(R.id.homecloudclientmainLinearLayout4);
		v.setClickable(true);
		v.setOnClickListener(this);
		String ll=util.getSPRead("hcclient").getString("lastLogin",null);
		
	}
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId())
		{
			case R.id.homecloudclientmainLinearLayout1:
				final Window ww=new Window(ctx,util.px(240),util.px(410))
					.setTitle("选择服务器")
					.setIcon("homecloud/homecloud")
					.setParent(w)
					.show();
				ViewGroup vg1=(ViewGroup) ww.addView(R.layout.homecloud_client_file);
				View add=vg1.getChildAt(1);
				new myAnim(add,"t:0,0,100,0,0,200;a:0,1,0,200;");
				final ListView list=(ListView) vg1.findViewById(R.id.homecloudclientfileListView1);
				TreeSet<String> set=new TreeSet<String>();
				Set<String> sst=util.getSPRead("hcclient").getStringSet("servers",new TreeSet<String>());
				set.addAll(sst);
				final ArrayList<String> ar=new ArrayList<String>();
				ar.addAll(set);
				final ArrayList<Integer> state=new ArrayList<Integer>();
				for(String c:ar)state.add(0);
				add.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View p1)
						{
							myDialog.Builder builder = new myDialog.Builder(ctx);
							builder.setTitle("添加服务器");
							final myEditText edi=new myEditText(ctx);
							edi.setHint("地址:端口");
							LinearLayout l=new LinearLayout(ctx);
							l.setOrientation(1);
							builder.setView(l);
							l.addView(edi);
							util.setWeight(l);
							util.setWeight(edi);
							builder.setPositiveButton("添加",new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface d,int p)
									{
										String a=edi.getText().toString();
										TreeSet<String> set=new TreeSet<String>();
										Set<String> ss=util.getSPRead("hcclient").getStringSet("servers",new TreeSet<String>());
										set.addAll(ss);
										set.add(a);
										ar.add(a);
										state.add(0);
										util.getSPWrite("hcclient").putStringSet("servers",set).commit();
										((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
									}});
							builder.setNegativeButton("取消",null);

							builder.show();
						}
					});
				list.setAdapter(new BaseAdapter()
					{
						@Override
						public int getCount()
						{
							Collections.sort(ar);
							return ar.size();
						}
						@Override
						public Object getItem(int p1)
						{
							return null;
						}
						@Override
						public long getItemId(int p1)
						{
							return 0;
						}
						@Override
						public View getView(int p1, View convertView, ViewGroup p3)
						{
							HolderList holder;
							if(convertView==null)
							{
								holder=new HolderList(ctx);
								convertView=holder.vg;
								convertView.setTag(holder);
								myLoadingView ld=new myLoadingView(ctx);
								LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(util.px(25),util.px(25));
								int m=util.px(2);
								ip.setMargins(m,m,m,m);
								holder.vg.addView(ld,0,ip);
							}
							else holder=(HolderList) convertView.getTag();
							holder.text.setText(ar.get(p1));
							holder.v[0].setVisibility(state.get(p1)==0?8:0);
							holder.vg.getChildAt(0).setVisibility(state.get(p1)==0?0:8);
							holder.v[0].setImageVec("homecloud/"+(state.get(p1)==1?"hc_query_g":(state.get(p1)==-1?"hc_query_r":"hc_query_t")));

							return convertView;
						}
					});
				list.setOnItemClickListener(new ListView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
						{
							
						}
						
						
					});
				list.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4)
						{
							myDialog.Builder builder = new myDialog.Builder(ctx);
							builder.setTitle("编辑服务器");
							final myEditText edi=new myEditText(ctx);
							edi.setHint("地址:端口");
							edi.setText(ar.get(p3));
							LinearLayout l=new LinearLayout(ctx);
							l.setOrientation(1);
							builder.setView(l);
							l.addView(edi);
							util.setWeight(l);
							util.setWeight(edi);
							builder.setPositiveButton("修改",new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface d,int p)
									{
										String a=edi.getText().toString();
										TreeSet<String> set=new TreeSet<String>();
										Set<String> ss=util.getSPRead("hcclient").getStringSet("servers",new TreeSet<String>());
										set.addAll(ss);
										set.add(a);
										ar.add(a);
										state.add(0);
										util.getSPWrite("hcclient").putStringSet("servers",set).commit();
										((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
									}});
							builder.setNeutralButton("返回",null);
							builder.setNegativeButton("删除",new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										TreeSet<String> set=new TreeSet<String>();
										Set<String> ss=util.getSPRead("hcclient").getStringSet("servers",new TreeSet<String>());
										set.addAll(ss);
										set.remove(ar.remove(p3));
										state.remove(p3);
										util.getSPWrite("hcclient").putStringSet("servers",set).commit();
										((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
									}
								});

							builder.show();
							return true;
						}
					});
				new Thread(new Runnable(){
						@Override
						public void run()
						{
							while(ww.isShowing())
							{
								queryServer(ar,state,new Runnable(){
										@Override
										public void run()
										{
											((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
										}
									});
								try
								{
									Thread.sleep(5000);
								}
								catch (InterruptedException e)
								{
									break;
								}
							}
						}
					},"HCquery").start();
				break;
			case R.id.homecloudclientmainLinearLayout2:
				break;
			case R.id.homecloudclientmainLinearLayout3:
				break;
			case R.id.homecloudclientmainLinearLayout4:
				break;
		}
	}
	private void queryServer(ArrayList<String> ar,final ArrayList<Integer> state,final Runnable fin)
	{
		try
		{
			ByteArrayOutputStream da=new ByteArrayOutputStream();
			DataOutputStream os=new DataOutputStream(da);
			os.writeByte(C.QUERY);
			os.writeLong(client.CID);
			os.flush();
			os.close();
			final byte[] bd=da.toByteArray();
			int cc=0;
			for(final String s:ar)
			{
				final int vc=cc++;
				new Thread(new Runnable(){
						@Override
						public void run()
						{
							try
							{

								state.set(vc,0);
								new Handler(ctx.getMainLooper()).post(fin);
								String[] pp=s.split(":");
								int port=10002;
								if(pp.length>1)port=Integer.parseInt(pp[1]);
								InetSocketAddress ad=new InetSocketAddress(pp[0],port);
								DatagramPacket pk=new DatagramPacket(bd,bd.length);
								pk.setSocketAddress(ad);
								pk.setPort(port);
								DatagramSocket soc=new DatagramSocket();
								soc.send(pk);
								soc.setSoTimeout(1000);
								byte[] bb=new byte[1024];
								DatagramPacket pk2=new DatagramPacket(bb,bb.length);
								soc.receive(pk2);
								DataInputStream is=new DataInputStream(new ByteArrayInputStream(pk2.getData()));
								if(is.readByte()==C.QUERY&&is.readLong()==client.CID)
								{
									state.set(vc,1);
									System.out.println(is.readUTF());
								}
								else state.set(vc,-1);
							}
							catch(Exception e)
							{
								state.set(vc,-1);
							}
							new Handler(ctx.getMainLooper()).post(fin);
						}
					}
					,"HCquery:Receiver").start();
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
