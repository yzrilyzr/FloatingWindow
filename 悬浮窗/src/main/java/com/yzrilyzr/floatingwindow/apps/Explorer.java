package com.yzrilyzr.floatingwindow.apps;

import android.widget.*;
import com.yzrilyzr.ui.*;
import java.io.*;
import java.util.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.Comparator;
import com.yzrilyzr.myclass.util;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Explorer implements AdapterView.OnItemClickListener,
AdapterView.OnItemLongClickListener,View.OnClickListener,
Window.OnButtonDown,Window.OnSizeChanged
{
	Context ctx;
	int rescode=0;
	List<Object> l=new ArrayList<Object>();
	Window w;
	String path;
	BaseAdapter adap=null;
	VecView[] butt;
	EditText pathtext;
	TextView info;
	VecView forw;
	myLoadingView searching;
	private boolean save,searchmode=false,select=false;
	AsyncTask searchThread;
	private EditText mFilename;
	private String category=null,tmpinfo;
	private GridView mlv;
	ArrayList<mFile> selected=new ArrayList<mFile>();
	protected static ArrayList<mFile> clip=new ArrayList<mFile>();
	private static boolean cut=false;
	CopyOnWriteArrayList<mFile> search=new CopyOnWriteArrayList<mFile>();
	HashMap<String,Integer> scrY=new HashMap<String,Integer>();
	private int iconsize=50,sorttype=0,sortstyle=2;
	static ConcurrentHashMap<String,Bitmap> ico=new ConcurrentHashMap<String,Bitmap>();
	static ConcurrentHashMap<String,Long> ico2=new ConcurrentHashMap<String,Long>();
	IcoTask icotask;
	protected static ArrayList<Explorer> exinst=new ArrayList<Explorer>();
	public Explorer(final Context c,Intent e)
	{
		exinst.add(this);
		ctx=c;
		path=e.getStringExtra("path");
		category=e.getStringExtra("category");
		w=new Window(c,util.px(280),util.px(330))
		.setIcon("explorer")
		.setOnSizeChanged(this)
		.setParent(e)
		.setOnButtonDown(this);
		final ViewGroup v=(ViewGroup) w.addView(R.layout.window_explorer);
		rescode=e.getIntExtra("rescode",0);
		save=e.getBooleanExtra("save",false);
		SharedPreferences sp=util.getSPRead();
		iconsize=sp.getInt("iconsize",iconsize);
		sorttype=sp.getInt("sorttype",sorttype);
		sortstyle=sp.getInt("sortstyle",sortstyle);
		if(save&&rescode!=0)
		{
			v.getChildAt(4).setVisibility(0);
			mFilename=(EditText)v.findViewById(R.id.windowexplorerEditText1);
			mFilename.setText(e.getStringExtra("savefile"));
			((View)v.findViewById(R.id.windowexplorerButton1)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					String name=mFilename.getText().toString();
					API.callBack(ctx,new Intent().putExtra("path",path+"/"+name),rescode);
					w.dismiss();
					exinst.remove(this);
				}
			});
		}
		searching=(myLoadingView) v.findViewById(R.id.windowexplorermyLoadingView1);
		pathtext=(EditText) v.findViewById(R.id.windowexplorermyEditText1);
		info=(TextView) v.findViewById(R.id.windowexplorerTextView1);
		info.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.7f));
		pathtext.setHint("输入路径以跳转");
		forw=(VecView) v.findViewById(R.id.windowexplorerVecView1);
		forw.setOnClickListener(this);
		adap=new BaseAdapter(){
			private int d=util.px(40);
			private Bitmap image=VECfile.createBitmap(ctx,"image",d,d),
			classz=VECfile.createBitmap(ctx,"class",d,d),
			music=VECfile.createBitmap(ctx,"music",d,d),
			video=VECfile.createBitmap(ctx,"video",d,d),
			mFile=VECfile.createBitmap(ctx,"file",d,d),
			unknown=VECfile.createBitmap(ctx,"unknownfile",d,d),
			internet=VECfile.createBitmap(ctx,"internet",d,d),
			folder=VECfile.createBitmap(ctx,"folder",d,d),
			packagee=VECfile.createBitmap(ctx,"package",d,d),
			sdcard=VECfile.createBitmap(ctx,"sdcard",d,d),
			android=VECfile.createBitmap(ctx,"android",d,d),
			ftp=VECfile.createBitmap(ctx,"ftp",d,d),
			sync=VECfile.createBitmap(ctx,"sync",d,d),
			removeable=VECfile.createBitmap(ctx,"removeable",d,d);
			@Override
			public int getCount()
			{
				return l.size();
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
			public void notifyDataSetChanged()
			{
				super.notifyDataSetChanged();
				info.setText(select?"已选择对象:"+selected.size():tmpinfo);
			}
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(c).inflate(R.layout.window_applist_entry,parent,false);
                    holder=new ViewHolder();
                    holder.text1 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView1);
                    holder.text2 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView2);
                    holder.icon = (VecView) convertView.findViewById(R.id.windowapplistentryImageView1);
                    convertView.setTag(holder);
                }
                else holder=(ViewHolder) convertView.getTag();
                Object o=l.get(position);
				String name="";
				Bitmap vec=unknown;
				if(o instanceof StorageInfo)
				{
					Explorer.StorageInfo si=(StorageInfo)o;
					if(!si.isRemoveable)vec=sdcard;
					else vec=removeable;
					name=new mFile(si.path).getName();
					((View)holder.icon.getParent()).setBackgroundColor(0);
				}
				else if(o instanceof mFile)
				{
					mFile f=(mFile)o;
					name=f.getName();
					if(f.isFile())
					{
						String m=util.getMIMEType(f);
						if(m.contains("image")){
							Bitmap d=ico.get(f.getAbsolutePath());
							Long c=ico2.get(f.getAbsolutePath());
							vec=d==null?image:d;
							if(d==null||c==null||c.longValue()!=f.lastModified()){
								ico.put(f.getAbsolutePath(),image);
								ico2.put(f.getAbsolutePath(),new Long(f.lastModified()));
								new IcoTask().execute(1,f);
							}
						}
						else if(m.contains("vec")){
							Bitmap d=ico.get(f.getAbsolutePath());
							Long c=ico2.get(f.getAbsolutePath());
							vec=d==null?classz:d;
							if(d==null||c==null||c.longValue()!=f.lastModified()){
								ico.put(f.getAbsolutePath(),image);
								ico2.put(f.getAbsolutePath(),new Long(f.lastModified()));
								new IcoTask().execute(3,f);
							}
						}
						else if(m.contains("filesync"))vec=sync;
						else if(m.contains("audio"))vec=music;
						else if(m.contains("video"))vec=video;
						else if(m.contains("text"))vec=mFile;
						else if(m.contains("android")){
							Bitmap d=ico.get(f.getAbsolutePath());
							Long c=ico2.get(f.getAbsolutePath());
							vec=d==null?android:d;
							if(d==null||c==null||c.longValue()!=f.lastModified()){
								ico.put(f.getAbsolutePath(),image);
								ico2.put(f.getAbsolutePath(),new Long(f.lastModified()));
								new IcoTask().execute(2,f);
							}
						}
						else if(
						m.contains("zip")||
						m.contains("tar")||
						m.contains("7z")||
						m.contains("gz")||
						m.contains("compress")||
						m.contains("gtar")||
						m.contains("tgz")
						
						)vec=packagee;
						else vec=unknown;
					}
					else if(f.isDirectory())vec=folder;
					((View)holder.icon.getParent()).setBackgroundColor(selected.contains(f)?uidata.ACCENT:0);
				}
				else if(o instanceof String)
				{
					String f=(String)o;
					name=f;
					/*if(f.contains("ftp://"))vec=ftp;
					//else if(f.contains("sync:"))vec=sync;
					else */if(f.contains("http://")||f.contains("https://"))vec=internet;
					((View)holder.icon.getParent()).setBackgroundColor(selected.contains(f)?uidata.ACCENT:0);
				}
                holder.text1.setText(name);
                holder.text2.setVisibility(8);
				holder.text1.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.8f)*(float)iconsize/50f);
				holder.icon.setImageBitmap(vec);
				LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams) holder.icon.getLayoutParams();
				int xx=(int)((float)iconsize/50f*util.px(40));
				lp.width=xx;
				lp.height=xx;
				return convertView;
			}
        };
		ViewGroup g=(ViewGroup) v.getChildAt(0),
		selectall=(ViewGroup) v.findViewById(R.id.windowexplorerLinearLayout1);
		butt=new VecView[12];
		for(int i=0,f=0;i<15;i+=2)
		{
			butt[f]=(VecView) g.getChildAt(i);
			butt[f++].setOnClickListener(this);
		}
		for(int i=0,f=8;i<7;i+=2)
		{
			butt[f]=(VecView) selectall.getChildAt(i);
			butt[f++].setOnClickListener(this);
		}
		mlv=(GridView)v.findViewById(R.id.windowexplorerGridLayout1);
        mlv.setAdapter(adap);
		mlv.setNumColumns(w.getLayoutParams().width/util.px(iconsize));
		mlv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        w.show();
        mlv.setOnItemClickListener(this);
		mlv.setOnItemLongClickListener(this);
		if(path!=null)
		{
			mFile f=new mFile(path);
			if(f.exists())
			{
				if(f.isDirectory())list();
				else if(f.isFile())
				{
					util.open(f.getAbsolutePath(),f.getPath());
					f=new mFile(f.getParent());
					path=f.getAbsolutePath();
					list();
				}
			}
			else
			{
				util.toast("文件不存在");
				w.dismiss();
				exinst.remove(this);
			}
		}
		else list();
	}
	class IcoTask extends AsyncTask
	{
		private int d=util.px(40);
		@Override
		protected Object doInBackground(Object[] p1)
		{
			int type=p1[0];
			mFile f=(Explorer.mFile) p1[1];
			if(type==1)
				try{
					Bitmap z=Bitmap.createBitmap(d,d,Bitmap.Config.ARGB_8888);
					Canvas android3=new Canvas(z);
					Bitmap b2=BitmapFactory.decodeFile(f.getAbsolutePath());
					Matrix mt=new Matrix();
					mt.postScale(d/(float)b2.getWidth(),d/(float)b2.getHeight());
					android3.drawBitmap(b2,mt,new Paint());
					ico.put(f.getAbsolutePath(),z);
				}catch(Throwable e)
				{
				}
			else if(type==2)
				try{
					Bitmap z=Bitmap.createBitmap(d,d,Bitmap.Config.ARGB_8888);
					Canvas android3=new Canvas(z);
					String absPath=f.getAbsolutePath();
					PackageManager pm = ctx.getPackageManager();    
					PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES);    
					if (pkgInfo != null) {    
						ApplicationInfo appInfo = pkgInfo.applicationInfo;        
						appInfo.sourceDir = absPath;    
						appInfo.publicSourceDir = absPath;    
						Drawable icon1 = appInfo.loadIcon(pm);
						icon1.setBounds(0,0,z.getWidth(),z.getHeight());
						icon1.draw(android3);
						ico.put(absPath,z);
					}
				}catch(Throwable e){
				}
			else if(type==3)
				try{
					VECfile vf=VECfile.readFile(f.getAbsolutePath());
					Bitmap b2=VECfile.createBitmap(vf,d,d);
					Canvas android3=new Canvas(b2);
					Bitmap clazz=VECfile.createBitmap(ctx,"class",d/4,d/4);
					android3.drawBitmap(clazz,d-clazz.getWidth(),d-clazz.getHeight(),new Paint());
					ico.put(f.getAbsolutePath(),b2);
				}catch(Throwable e){
					e.printStackTrace();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			adap.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE){
			exinst.remove(this);
			if(searchThread!=null)searchThread.cancel(true);
			
		}
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mlv.setNumColumns(w/util.px(iconsize));

	}

	@Override
	public void onClick(View p1)
	{
		try
		{
			if(p1==forw)
			{
				if(searchmode)
				{
					if(pathtext.getText().toString().equals(""))
					{
						if(searchThread!=null)searchThread.cancel(true);
					}
					else
					{
						if(searchThread!=null)searchThread.cancel(true);
						else searchThread=new AsyncTask(){
								int k=0;
								String key="";
								@Override
								public void onPreExecute()
								{
									key=pathtext.getText().toString().toLowerCase();
									pathtext.setEnabled(false);
									searching.setVisibility(0);
									forw.setImageVec("closewin");
									w.setTitle(String.format("在\"%s\"的搜索结果",path==null?"我的手机":new mFile(path).getName()));
								}
								@Override
								public void onPostExecute(Object s)
								{
									pathtext.setEnabled(true);
									searching.setVisibility(8);
									forw.setImageVec("forward");
									searchThread=null;
									publishProgress();
									if(search.size()==0)
									{
										info.setText("无搜索匹配项");
										util.toast("无搜索匹配项");
									}
								}
								@Override
								public void onCancelled(Object o)
								{
									pathtext.setEnabled(true);
									searching.setVisibility(8);
									forw.setImageVec("forward");
									searchThread=null;
									publishProgress();
									if(search.size()==0)
									{
										info.setText("无搜索匹配项");
										util.toast("无搜索匹配项");
									}
								}
								@Override
								protected void onProgressUpdate(Object[] o)
								{
									list();
									pathtext.setText(key);
									w.setTitle(String.format("在\"%s\"的搜索结果",path==null?"我的手机":new mFile(path).getName()));
									info.setText("匹配项:"+search.size());
								}
								private void mFileTraversal(mFile dir)
								{
									if(isCancelled())return;
									if(dir.isDirectory())
									{
										mFile[] fs=dir.listFiles();
										if(fs!=null)
											for(mFile x:fs)if(!isCancelled())mFileTraversal(x);
									}
									if(dir.exists()&&dir.getName().toLowerCase().contains(key))search.add(dir);
									if(k++%1000==0)publishProgress();
								}

								@Override
								protected Object doInBackground(Object[] p1)
								{
									search.clear();
									k=0;
									mFileTraversal(new mFile(path==null?"/storage":path));
									/*Collections.sort(search,new myComp<mFile>(){
									 @Override
									 public int compare(mFile p1, mFile p2)
									 {
									 return p1.getName().compareToIgnoreCase(p2.getName());
									 }
									 });*/
									return null;
								}
							};

						searchThread.execute();
					}
				}
				else
				{
					String f=pathtext.getText()+"";
					if("".equals(f)||"我的手机".equals(f))return;
					mFile g=new mFile(f);
					if(g.exists())
					{
						path=g.getAbsolutePath();
						list();
					}
					else util.toast("找不到指定的文件");
				}
			}
			else if(p1==butt[0])
			{
				setSearchMode(false);
				if(search.size()!=0)search.clear();
				else path=new mFile(path).getParent();
				list();
			}
			else if(p1==butt[1])
			{
				if(path==null)
				{
					myDialog.Builder builder = new myDialog.Builder(ctx);
					builder.setTitle("新建快捷方式");
					final myEditText edi=new myEditText(ctx);
					edi.setHint("路径");
					builder.setView(edi);
					builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface d,int p)
						{
							try
							{
								addShortcut(edi.getText()+"");
								list();
							}
							catch(Exception e)
							{
								util.toast("创建失败");
								e.printStackTrace();
							}
						}});
					builder.setNegativeButton("取消",null);
					builder.show();
					return;
				}
				final myRadioButton r1,r2;
				final int[] whi=new int[]{1};
				myDialog.Builder builder = new myDialog.Builder(ctx);
				builder.setTitle("新建");
				LinearLayout lay=new LinearLayout(ctx);
				final myEditText edi=new myEditText(ctx);
				edi.setText("新建文件夹");
				edi.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
				lay.addView(edi);
				LinearLayout lay2=new LinearLayout(ctx);
				r1=new myRadioButton(ctx);
				r2=new myRadioButton(ctx);
				r1.setChecked(true);
				TextView t1=new TextView(ctx),t2=new TextView(ctx);
				r1.setOnClickListener(new OnClickListener(){
					public void onClick(View p1)
					{
						r2.setChecked(false);
						edi.setText("新建文件夹");
						whi[0]=1;
					}});
				r2.setOnClickListener(new OnClickListener(){
					public void onClick(View p1)
					{
						r1.setChecked(false);
						edi.setText("新建文件.txt");
						whi[0]=2;
					}
				});
				t1.setText("文件夹");
				t2.setText("文件");
				LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(-2,-2);
				l.setMargins(0,0,150,0);
				t1.setLayoutParams(l);
				lay2.setGravity(17);
				lay2.setOrientation(0);
				lay.setOrientation(1);
				lay2.addView(r1);
				lay2.addView(t1);
				lay2.addView(r2);
				lay2.addView(t2);
				lay.addView(lay2);
				lay2.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
				builder.setView(lay);
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface d,int p)
					{
						try
						{
							mFile op=new mFile(path+"/"+edi.getText());
							if(op.exists())util.toast("文件已存在");
							else
							{
								if(whi[0]==1)op.mkdirs();
								else if(whi[0]==2)op.createNewFile();
								util.toast("创建成功");
								list();
							}
						}
						catch(Exception e)
						{
							util.toast("创建失败");
							e.printStackTrace();
						}
					}});
				builder.setNegativeButton("取消",null);
				builder.show();
			}
			else if(p1==butt[2])
			{
				setSearchMode(false);
				path=null;
				search.clear();
				list();
			}
			else if(p1==butt[3])
			{
				setSearchMode(!searchmode);
			}
			else if(p1==butt[4])
			{
				setSelectMode(!select);
			}
			else if(p1==butt[5])
			{
				list();
			}
			else if(p1==butt[6]){
				final ViewGroup vg=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.window_explorersort,null);
				new Window(ctx,util.px(260),util.px(233))
				.setTitle("视图和排序")
				.setBar(8,8,0)
				.setIcon("sort")
				.setCanResize(false)
				.setCanFocus(false)
				.setParent(w)
				.addView(vg)
				.setOnButtonDown(new Window.OnButtonDown(){
					@Override
					public void onButtonDown(int code)
					{
						util.getSPWrite()
						.putInt("iconsize",iconsize)
						.putInt("sortstyle",sortstyle)
						.putInt("sorttype",sorttype)
						.commit();
					}
				})
				.show();
				SeekBar sb=(SeekBar) vg.findViewById(R.id.windowexplorersortSeekBar1);
				final RadioButton a=(RadioButton) vg.findViewById(R.id.windowexplorersortmyRadioButton1);
				final RadioButton b=(RadioButton) vg.findViewById(R.id.windowexplorersortmyRadioButton2);
				final RadioButton c=(RadioButton) vg.findViewById(R.id.windowexplorersortmyRadioButton3);
				final LinearLayout q=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout1);
				final LinearLayout w=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout2);
				final LinearLayout e=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout3);
				final LinearLayout r=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout4);
				final LinearLayout t=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout5);
				final LinearLayout y=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout6);
				final LinearLayout u=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout7);
				final LinearLayout i=(LinearLayout) vg.findViewById(R.id.windowexplorersortLinearLayout8);
				if(sortstyle==0)a.setChecked(true);
				else if(sortstyle==1)b.setChecked(true);
				else if(sortstyle==2)c.setChecked(true);
				sb.setProgress(iconsize-10);
				if(sorttype==0)q.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==1)w.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==2)e.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==3)r.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==4)t.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==5)y.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==6)u.setBackgroundColor(uidata.ACCENT);
				else if(sorttype==7)i.setBackgroundColor(uidata.ACCENT);
				sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
					@Override
					public void onProgressChanged(SeekBar p1, int p2, boolean p3)
					{
						iconsize=p2+10;
						mlv.setNumColumns(mlv.getWidth()/util.px(iconsize));
						adap.notifyDataSetChanged();
					}

					@Override
					public void onStartTrackingTouch(SeekBar p1)
					{
					}

					@Override
					public void onStopTrackingTouch(SeekBar p1)
					{
					}
				});
				OnCheckedChangeListener ol=new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton p1, boolean p2)
					{
						if(p2){
							if(p1==a)sortstyle=0;
							else if(p1==b)sortstyle=1;
							else if(p1==c)sortstyle=2;
							adap.notifyDataSetChanged();
						}
					}
				};
				OnClickListener oc=new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						if(p1==q)sorttype=0;
						else if(p1==w)sorttype=1;
						else if(p1==e)sorttype=2;
						else if(p1==r)sorttype=3;
						else if(p1==t)sorttype=4;
						else if(p1==y)sorttype=5;
						else if(p1==u)sorttype=6;
						else if(p1==i)sorttype=7;
						q.setBackgroundColor(sorttype==0?uidata.ACCENT:0);
						w.setBackgroundColor(sorttype==1?uidata.ACCENT:0);
						e.setBackgroundColor(sorttype==2?uidata.ACCENT:0);
						r.setBackgroundColor(sorttype==3?uidata.ACCENT:0);
						t.setBackgroundColor(sorttype==4?uidata.ACCENT:0);
						y.setBackgroundColor(sorttype==5?uidata.ACCENT:0);
						u.setBackgroundColor(sorttype==6?uidata.ACCENT:0);
						i.setBackgroundColor(sorttype==7?uidata.ACCENT:0);
						list();
					}
				};
				a.setOnCheckedChangeListener(ol);
				b.setOnCheckedChangeListener(ol);
				c.setOnCheckedChangeListener(ol);
				q.setOnClickListener(oc);
				w.setOnClickListener(oc);
				e.setOnClickListener(oc);
				r.setOnClickListener(oc);
				t.setOnClickListener(oc);
				y.setOnClickListener(oc);
				u.setOnClickListener(oc);
				i.setOnClickListener(oc);
			}
			else if(p1==butt[7])
			{
				onItemLongClick(mlv,null,-2,0);
			}
			else if(p1==butt[8])
			{
				selected.clear();
				for(Object o:l)
					if(o instanceof mFile)
						selected.add((mFile)o);
				adap.notifyDataSetChanged();
			}
			else if(p1==butt[9])
			{
				for(Object o:l)
				{
					if(o instanceof mFile)
						if(selected.contains(o))selected.remove(o);
						else selected.add((mFile)o);
				}
				adap.notifyDataSetChanged();
			}
			else if(p1==butt[10])
			{
				int li=l.size(),ri=0;
				for(mFile f:selected)
				{
					int c=l.indexOf(f);
					li=Math.min(li,c);
					ri=Math.max(ri,c);
				}
				if(ri>li)
				{
					selected.clear();
					for(int i=li;i<ri+1;i++)
					{
						selected.add((mFile)l.get(i));
					}
					adap.notifyDataSetChanged();
				}
			}
			else if(p1==butt[11])
			{
				ArrayList<String> ty=new ArrayList<String>();
				for(mFile f:selected)
				{
					int x=f.getName().lastIndexOf(".");
					if(x<0)x=0;
					String type=f.getName().substring(x+1);
					if(!ty.contains(type))ty.add(type);
				}
				selected.clear();
				for(Object o:l)
				{
					if(o instanceof mFile)
					{
						mFile f=(mFile)o;
						int x=f.getName().lastIndexOf(".");
						if(x<0)x=0;
						String type=f.getName().substring(x+1);
						if(ty.contains(type))selected.add(f);
					}
				}
				adap.notifyDataSetChanged();
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private void setSelectMode(boolean select)
	{
		this.select=select;
		selected.clear();
		w.setTitle(select?w.getTitle()+"(多选模式)":path==null?"存储设备":new mFile(path).getName());
		butt[4].setBackgroundColor(select?uidata.ACCENT:uidata.BACK);
		adap.notifyDataSetChanged();
		((View)butt[8].getParent()).setVisibility(select?0:8);
		info.setText(select?"已选择对象:"+selected.size():tmpinfo);
	}

	private void setSearchMode(boolean m)
	{
		searchmode=m;
		butt[3].setBackgroundColor(searchmode?uidata.ACCENT:uidata.BACK);
		if(!m&&searchThread!=null)searchThread.cancel(true);
		pathtext.setText(m?"":path==null?"我的手机":path);
		pathtext.setHint(m?"搜索…":"输入路径以跳转");
	}
	private void addShortcut(String f)
	{
		if(f==null||"null".equals(f))return;
		Set<String> s=util.getSPRead().getStringSet("explorershortcuts",new TreeSet<String>());
		TreeSet<String> st=new TreeSet<String>();
		if(!select)st.add(f);
		else for(mFile k:selected)st.add(k.getAbsolutePath());
		for(String x:s)st.add(x);
		util.getSPWrite().putStringSet("explorershortcuts",st).commit();
	}
	public static class StorageInfo
	{  
		public String path;  
		public String state;  
		public boolean isRemoveable;  
		public StorageInfo(String path)
		{  
			this.path = path;  
		}  
		public boolean isMounted()
		{  
			return "mounted".equals(state);  
		}  
		@Override  
		public String toString()
		{  
			return "StorageInfo [path=" + path + ", state=" + state  
			+ ", isRemoveable=" + isRemoveable + "]";  
		}  
	}  
	public static List<StorageInfo> listAllStorage(Context context)
	{  
		ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();  
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);  
		try
		{  
			Class<?>[] paramClasses = {};  
			Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);  
			Object[] params = {};  
			Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);  

			if (invokes != null)
			{  
				StorageInfo info = null;  
				for (int i = 0; i < invokes.length; i++)
				{  
					Object obj = invokes[i];  
					Method getPath = obj.getClass().getMethod("getPath", new Class[0]);  
					String path = (String) getPath.invoke(obj, new Object[0]);  
					info = new StorageInfo(path);  

					Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);  
					String state = (String) getVolumeState.invoke(storageManager, info.path);  
					info.state = state;  

					Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);  
					info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();  
					storages.add(info);  
				}  
			}  
		}
		catch (Exception e)
		{  
			e.printStackTrace();  
		}  
		storages.trimToSize();  
		return storages;  
	}  

	public List<StorageInfo> getAvaliableStorage(List<StorageInfo> infos)
	{  
		List<StorageInfo> storages = new ArrayList<StorageInfo>();  
		for(StorageInfo info : infos)
		{  
			mFile mFile = new mFile(info.path);  
			if ((mFile.exists()) && (mFile.isDirectory()) && (mFile.canWrite()))
			{  
				if (info.isMounted())
				{  
					storages.add(info);  
				}  
			}  
		}  

		return storages;  
	}  
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		Object o=l.get(p3);
		if(o instanceof StorageInfo)
		{
			Explorer.StorageInfo si=(StorageInfo)o;
			path=si.path;
			list();
		}
		else if(o instanceof mFile)
		{
			mFile f=(mFile)o;
			if(select)
			{
				if(selected.contains(f))selected.remove(f);
				else selected.add(f);
				adap.notifyDataSetChanged();
				return;
			}
			if(f.isFile())
			{
				if(rescode==0)util.open(f.getAbsolutePath(),f.getPath());
				else if(!save)
				{
					API.callBack(ctx,new Intent().putExtra("path",f.getAbsolutePath()),rescode);
					w.dismiss();
					exinst.remove(this);
				}
				else mFilename.setText(f.getName());
			}
			else if(f.isDirectory())
			{
				scrY.put(path,mlv.getFirstVisiblePosition());
				path=f.getPath();
				list();
			}
			else if(!f.exists())util.toast("文件不存在");
		}
		else if(o instanceof String){
			String s=(String)o;
			/*if(s.contains("ftp://")){
				try{
					String[] f=s.split("\n");
					Matcher m=Pattern.compile("ftp://.*?:[0-9]*").matcher(s);
					String aa="";
					while(m.find())aa=m.group();
					System.out.println(aa);
					String[] addr=aa.substring(6,aa.length()).split(":");
					if(myftp!=null){
						//myftp.removeCallBack(this);
						myftp.stop();
						myftp=null;
					}
					path=f[0];
					myftp=new myFTP_Client(addr[0],Integer.parseInt(addr[1]));
					myftp.setReceive(Explorer.this);
					if(f.length==1)myftp.login("","");
					else myftp.login(f[f.length-2],f[f.length-1]);
					searching.setVisibility(0);
				}catch(Throwable e){
					util.toast("路径不合法");
					e.printStackTrace();
				}
			}
			else */if(s.contains("https://")||s.contains("http://"))
				API.startService(ctx,new Intent().putExtra("url",s),cls.WEBVIEWER);
			//else if(s.contains("sync:"));
		}
		setSearchMode(false);
	}
	/*@Override
	public void onReceive(byte c)
	{
		if(c==C.LOGINFAIL||c==C.TIMEOUT)
		{
			util.toast(c==C.LOGINFAIL?"登录失败":"连接超时");
			searching.setVisibility(8);
			if(myftp!=null){
				//myftp.removeCallBack(this);
				myftp.stop();
				myftp=null;
			}
			setSearchMode(false);
			path=null;
			search.clear();
			list();
		}
		else if(c==C.LOGINSUC)
		{
			util.toast("登录成功");
			searching.setVisibility(8);
			list();
		}
	}
	*/
	@Override
	public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if(select&&selected.size()==0)
		{
			util.toast("未选择文件");
			return true;
		}
		String omFile="打开方式…,剪切,复制,粘贴,删除,重命名,发送,属性,在新窗口中打开,添加到快捷方式,扫描媒体";
		String odisk="分析,属性";
		String oshort="编辑,删除快捷方式,在新窗口中打开";
		String odir="剪切,复制,粘贴,删除,重命名,属性,在新窗口中打开,添加到快捷方式,扫描媒体";
		Object o=null;
		if(p3>=0&&p3<l.size())o=l.get(p3);
		else
		{
			if(path==null)return true;
			o=new mFile(path);
		}
		String name="";
		mFile f=null;
		if(o instanceof StorageInfo)
		{
			Explorer.StorageInfo si=(StorageInfo)o;
			name=new mFile(si.path).getName();
		}
		else if(o instanceof mFile)
		{
			f=(mFile)o;
			name=f.getName();
		}
		else if(o instanceof String)
		{
			name=(String)o;
			f=new mFile(name);
		}
		final mFile g=f;
		new myDialog.Builder(ctx)
		.setTitle("操作:"+(select?selected.get(0).getName()+"…("+selected.size()+"项)":name))
		.setItems((f==null?odisk:path==null&&!searchmode?oshort:f.isFile()||select?omFile:odir).split(","),new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				if(g==null)
				{

				}
				else if(path==null&&!searchmode)
				{
					if(p2==0){
						myDialog.Builder builder = new myDialog.Builder(ctx);
						builder.setTitle("编辑快捷方式");
						final myEditText edi=new myEditText(ctx);
						edi.setHint("路径");
						edi.setText(g.getPath());
						builder.setView(edi);
						builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface d,int p)
							{
								try
								{
									Set<String> s=util.getSPRead().getStringSet("explorershortcuts",new TreeSet<String>());
									TreeSet<String> ss=new TreeSet<String>();
									for(String l:s)ss.add(l);
									ss.remove(g.getPath());
									ss.add(edi.getText()+"");
									util.getSPWrite().putStringSet("explorershortcuts",ss).commit();
									list();
								}
								catch(Exception e)
								{
									util.toast("编辑失败");
									e.printStackTrace();
								}
							}});
						builder.setNegativeButton("取消",null);
						builder.show();
					}
					if(p2==1)
					{
						Set<String> s=util.getSPRead().getStringSet("explorershortcuts",new TreeSet<String>());
						TreeSet<String> ss=new TreeSet<String>();
						for(String l:s)ss.add(l);
						if(!select)ss.remove(g.getPath());
						else for(mFile gg:selected)ss.remove(gg.getPath());
						util.getSPWrite().putStringSet("explorershortcuts",ss).commit();
						list();
					}
					else if(p2==2)
					{
						if(!select)API.startService(ctx,new Intent().putExtra("path",g.getPath()),cls.EXPLORER);
						else for(mFile gg:selected)API.startService(ctx,new Intent().putExtra("path",gg.getPath()),cls.EXPLORER);
					}
				}
				else if(g.isFile()||select)
				{
					if(p2==0){
						final Intent e=new Intent().putExtra("path",g.getAbsolutePath());
						new myDialog.Builder(ctx)
						.setTitle("选择打开方式")
						.setItems("文本,音频,视频,图片".split(","),new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								switch(p2)
								{
									case 0:
										API.startService(ctx,e,cls.TEXTEDITOR);
										break;
									case 1:
										API.startService(ctx,e,cls.PLAYER);
										break;
									case 2:
										API.startService(ctx,e,cls.PLAYER);
										break;
									case 3:
										API.startService(ctx,e.putExtra("type",1),cls.IMAGEVIEWER);
										break;
								}
							}
						})
						.setNegativeButton("取消",null)
						.show();
					}
					else if(p2==1)cut(g);
					else if(p2==2)copy(g);
					else if(p2==3)paste(g);
					else if(p2==4)delete(g);
					else if(p2==5)rename(g);
					else if(p2==6)
					{
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_SEND);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setType(util.getMIMEType(g));
						intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(g));
						ctx.startActivity(intent);
						w.setMinWin(true);
					}
					else if(p2==7)prop(g);
					else if(p2==8)
						if(!select)API.startService(ctx,new Intent().putExtra("path",g.getAbsolutePath()),cls.EXPLORER);
						else for(mFile gg:selected)API.startService(ctx,new Intent().putExtra("path",gg.getAbsolutePath()),cls.EXPLORER);
					else if(p2==9)
					{
						addShortcut(g.getAbsolutePath());util.toast("已添加到快捷方式");
					}
					else if(p2==10){
						util.scanMedia(g.getAbsolutePath());
						util.toast("扫描完毕");
					}
				}
				else
				{
					if(p2==0)cut(g);
					else if(p2==1)copy(g);
					else if(p2==2)paste(g);
					else if(p2==3)delete(g);
					else if(p2==4)rename(g);
					else if(p2==5)prop(g);
					else if(p2==6)
						if(!select)API.startService(ctx,new Intent().putExtra("path",g.getAbsolutePath()),cls.EXPLORER);
						else for(mFile gg:selected)API.startService(ctx,new Intent().putExtra("path",gg.getAbsolutePath()),cls.EXPLORER);
					else if(p2==7)
					{
						addShortcut(g.getAbsolutePath());util.toast("已添加到快捷方式");
					}
					else if(p2==8)new AsyncTask(){
							private void list(mFile f)
							{
								if(f.isDirectory())
								{
									for(mFile k:f.listFiles())list(k);
								}
								util.scanMedia(f.getAbsolutePath());
							}
							@Override
							protected Object doInBackground(Object[] p1)
							{
								if(select)for(mFile x:selected)list(x);
								else list(g);
								return null;
							}
							@Override
							protected void onPostExecute(Object result)
							{
								util.toast("扫描完毕");
							}
							@Override
							protected void onPreExecute()
							{
								util.toast("正在扫描");
							}
						}.execute();

				}
			}


		})
		.setNegativeButton("取消",null)
		.show();
		return true;
	}
	private void cut(mFile g)
	{
		copy(g);
		cut=true;
	}
	protected void copy(final mFile g)
	{
		cut=false;
		clip.clear();
		new AsyncTask(){
			private void list(mFile f)
			{
				if(f.isDirectory())
				{
					for(mFile k:f.listFiles())list(k);
				}
				clip.add(f);
			}
			@Override
			protected Object doInBackground(Object[] p1)
			{
				if(select)for(mFile x:selected)list(x);
				else list(g);
				return null;
			}
			@Override
			protected void onPostExecute(Object result)
			{
				util.toast("已复制到剪贴板");
				searching.setVisibility(8);
			}
			@Override
			protected void onPreExecute()
			{
				searching.setVisibility(0);
			}
		}.execute();
	}
	protected void paste(final mFile g)
	{
		if(clip.size()==0)
		{
			util.toast("剪贴板是空的");
			return;
		}
		final boolean[] run=new boolean[]{true};
		LinearLayout l=new LinearLayout(ctx);
		l.setOrientation(1);
		final TextView t=new myTextView(ctx);
		final ProgressBar p=new myProgressBar(ctx);
		l.addView(t);
		l.addView(p);
		p.setMax(clip.size());
		util.setWeight(t);
		//util.setWeight(p);
		final Window ww=new Window(ctx,util.px(220),util.px(100))
		.setTitle("正在复制")
		.setIcon("copy")
		.setBar(0,8,0)
		.setCanFocus(false)
		.addView(l)
		.setOnButtonDown(new Window.OnButtonDown(){
			@Override
			public void onButtonDown(int code)
			{
				if(code==Window.ButtonCode.CLOSE)run[0]=false;
			}
		})
		.show();
		util.setWeight(l);
		new AsyncTask(){
			String dst;
			boolean hasErr=false,cutc=false;
			@Override
			protected Object doInBackground(Object[] p1)
			{
				if(g.isFile())dst=g.getParent();
				else if(g.isDirectory())dst=g.getAbsolutePath();
				else return null;
				ArrayList<mFile> clip2=clip;
				cutc=cut;
				clip=new ArrayList<mFile>();
				int pr=0;
				String rep=clip2.get(clip2.size()-1).getParent();
				for(mFile kd:clip2)
					if(run[0])
					{
						mFile d=new mFile(kd.getAbsolutePath().replace(rep,dst));
						if(kd.isDirectory())
						{
							if(kd.exists()&&cutc)kd.delete();
							if(!d.exists())d.mkdirs();
						}
						else if(kd.isFile())
						{
							if(!d.getParentFile().exists())d.getParentFile().mkdirs();
							try
							{
								FileChannel in=new FileInputStream(kd).getChannel();
								FileChannel out=new FileOutputStream(d).getChannel();
								in.transferTo(0,in.size(),out);
								in.close();
								out.close();
								if(cutc)kd.delete();
							}
							catch(Throwable e)
							{
								e.printStackTrace();
								hasErr=true;
							}
						}
						publishProgress(kd.getName(),pr++);
					}
					else break;
				clip2.clear();
				return null;
			}
			@Override
			protected void onProgressUpdate(Object[] l)
			{
				t.setText((String)l[0]);
				p.setProgress((int)l[1]);
			}
			@Override
			protected void onPostExecute(Object result)
			{
				ww.dismiss();
				util.toast(run[0]?"操作完毕":"取消操作");
				if(run[0]&&cutc)for(Explorer w:exinst)
					w.list();
				if(hasErr)util.toast("复制时出现错误，详情查看控制台");
				list();
			}
		}.execute();
	}
	private void delete(final mFile g)
	{
		final ArrayList<mFile> del=new ArrayList<mFile>();
		final ArrayList<mFile> deldir=new ArrayList<mFile>();
		final boolean[] b=new boolean[1];
		b[0]=true;
		final myDialog.Builder bd=new myDialog.Builder(ctx)
		.setTitle("删除")
		.setPositiveButton("确定",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				final boolean[] run=new boolean[]{true};
				LinearLayout l=new LinearLayout(ctx);
				l.setOrientation(1);
				final TextView t=new myTextView(ctx);
				final ProgressBar p=new myProgressBar(ctx);
				l.addView(t);
				l.addView(p);
				p.setMax(del.size());
				util.setWeight(t);
				//util.setWeight(p);
				final Window ww=new Window(ctx,util.px(220),util.px(100))
				.setTitle("正在删除")
				.setIcon("delete")
				.setBar(0,8,0)
				.setCanFocus(false)
				.addView(l)
				.setOnButtonDown(new Window.OnButtonDown(){
					@Override
					public void onButtonDown(int code)
					{
						if(code==Window.ButtonCode.CLOSE)run[0]=false;
					}
				})
				.show();
				util.setWeight(l);
				new AsyncTask(){
					@Override
					protected Object doInBackground(Object[] p1)
					{
						int i=0;
						for(mFile k:del)if(run[0])
							{
								k.delete();
								publishProgress(k.getName(),i++);
								try
								{
									Thread.sleep(1000/del.size());
								}
								catch (Exception e)
								{}
							}
							else break;
						return null;
					}
					@Override
					protected void onProgressUpdate(Object[] l)
					{
						t.setText((String)l[0]);
						p.setProgress((int)l[1]);
					}
					@Override
					protected void onPostExecute(Object result)
					{
						ww.dismiss();
						util.toast(run[0]?"删除完毕":"取消操作");
						mFile x=new mFile(path);
						if(!x.exists())path=x.getParent();
						list();
					}
				}.execute();
			}
		})
		.setNeutralButton("防止创建文件夹",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				final boolean[] run=new boolean[]{true};
				LinearLayout l=new LinearLayout(ctx);
				l.setOrientation(1);
				final TextView t=new myTextView(ctx);
				final ProgressBar p=new myProgressBar(ctx);
				l.addView(t);
				l.addView(p);
				p.setMax(del.size());
				util.setWeight(t);
				//util.setWeight(p);
				final Window ww=new Window(ctx,util.px(220),util.px(100))
				.setTitle("正在删除")
				.setIcon("delete")
				.setBar(0,8,0)
				.setCanFocus(false)
				.addView(l)
				.setOnButtonDown(new Window.OnButtonDown(){
					@Override
					public void onButtonDown(int code)
					{
						if(code==Window.ButtonCode.CLOSE)run[0]=false;
					}
				})
				.show();
				util.setWeight(l);
				new AsyncTask(){
					@Override
					protected Object doInBackground(Object[] p1)
					{
						int i=0;
						for(mFile k:del)if(run[0])
							{
								k.delete();
								publishProgress(k.getName(),i++);
								try
								{
									Thread.sleep(1000/del.size());
								}
								catch (Exception e)
								{}
							}

						for(mFile k:deldir)if(run[0])
								try
								{
									k.createNewFile();
								}
								catch(Throwable e)
								{
									e.printStackTrace();
								}
							else break;
						return null;
					}
					@Override
					protected void onProgressUpdate(Object[] l)
					{
						t.setText((String)l[0]);
						p.setProgress((int)l[1]);
					}
					@Override
					protected void onPostExecute(Object result)
					{
						ww.dismiss();
						util.toast(run[0]?"删除完毕":"取消操作");
						mFile x=new mFile(path);
						if(!x.exists())path=x.getParent();
						list();
					}
				}.execute();
			}
		})

		.setNegativeButton("取消",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				b[0]=false;
			}
		}
	);
		bd.show();
		new AsyncTask(){
			long len=0;
			int fc=0,dc=0;
			private void list(mFile f)
			{
				if(!b[0])return;
				if(f.isFile())
				{len+=f.length();fc++;}
				if(f.isDirectory())
				{dc++;
					for(mFile k:f.listFiles())list(k);
				}
				del.add(f);
			}
			@Override
			protected Object doInBackground(Object[] p1)
			{
				if(select)
					for(mFile x:selected)
					{
						deldir.add(x);
						list(x);
					}
				else
				{
					list(g);dc--;
					deldir.add(g);
				}
				return null;
			}
			@Override
			protected void onPostExecute(Object result)
			{
				try{
					if(!b[0])return;
				bd.setMessage(String.format("确定要删除 %s%s\n%s大小：%s",
				select?selected.get(0).getName():g.getName(),
				(select?"…("+selected.size()+"项)":""),
				(select||g.isDirectory()?"包含:"+fc+"个文件 "+dc+"个文件夹，":""),
				util.getFileSizeStr(!select&&g.isFile()?g.length():len)));
				}catch(Throwable e){}
			}
		}.execute();
	}
	private void rename(final mFile g)
	{
		myDialog.Builder builder = new myDialog.Builder(ctx);
		builder.setTitle("重命名");
		final myEditText edi=new myEditText(ctx);
		edi.setHint("新的名称");
		builder.setView(edi);
		edi.setText(g.getName());
		edi.setSelection(0,edi.getText().length());
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface d,int p)
			{
				try
				{
					mFile x=new mFile(path);
					if(!x.exists())path=x.getParent();
					mFile pp=new mFile(g.getParent()+"/"+edi.getText().toString());
					if(pp.exists())throw new Exception("文件重名");
					if(!g.renameTo(pp))throw new Exception();
					list();
				}
				catch(Exception e)
				{
					util.toast("重命名失败");
					e.printStackTrace();
				}
			}});
		builder.setNegativeButton("取消",null);
		builder.show();
		util.openIME(edi);
	}
	public void list()
	{
		setSelectMode(false);
		String ss="";
		if(search.size()!=0)
		{
			l.clear();
			for(mFile f:search)if(f.isDirectory())l.add(f);
			if(category==null)
			{
				for(mFile f:search)if(f.isFile())l.add(f);
			}
			else
			{
				for(mFile f:search)if(f.isFile()&&util.getMIMEType(f).contains(category))l.add(f);
			}
			ss="匹配项:"+search.size();
		}
		else if(path==null)
		{
			w.setTitle("存储设备");
			pathtext.setText("我的手机");
			l.clear();
			List<Explorer.StorageInfo> ls=listAllStorage(ctx);
			for(StorageInfo z:ls)
				if(z.isMounted())l.add(z);
			ss="已挂载存储:"+l.size();
			ls.clear();
			Set<String> set=util.getSPRead().getStringSet("explorershortcuts",null);
			if(set!=null){
			String[] su=new String[set.size()];
			int i=0;
			for(String s:set)su[i++]=s;
			Arrays.sort(su);
			if(i!=0)for(String s:su){
					if(s.contains("ftp://")||s.contains("https://")||s.contains("http://")/*||s.contains("sync:")*/)l.add(s);
					else l.add(new mFile(s));
				}
			}
        }
		else
		{
			mFile pf=new mFile(path);
			mFile[] src=pf.listFiles();
			if(src==null)return;
			w.setTitle(pf.getName());
			pathtext.setText(pf.getAbsolutePath());
			ArrayList<mFile> dirs=new ArrayList<mFile>(),files=new ArrayList<mFile>();
			for(mFile f:src)if(f.isDirectory())
				{
					dirs.add(f);
				}
			if(category==null)
			{
				for(mFile f:src)if(f.isFile())
					{
						files.add(f);
					}
			}
			else
			{
				for(mFile f:src)
					if(f.isFile()&&util.getMIMEType(f).contains(category))
					{
						files.add(f);
					}
			}
			Comparator<mFile> cp=new Comparator<mFile>(){
				@Override
				public int compare(mFile p1, mFile p2)
				{
					return p1.getName().compareToIgnoreCase(p2.getName());
				}
			};
			Comparator<mFile> cp2=new Comparator<mFile>(){
				@Override
				public int compare(mFile p1, mFile p2)
				{
					if(sorttype==0)return p1.getName().compareToIgnoreCase(p2.getName());
					else if(sorttype==1)return getFileExt(p1).compareToIgnoreCase(getFileExt(p2));
					else if(sorttype==2)return util.compareL(p1.length(),p2.length());
					else if(sorttype==3)return util.compareL(p1.lastModified(),p2.lastModified());
					else if(sorttype==4)return -(p1.getName().compareToIgnoreCase(p2.getName()));
					else if(sorttype==5)return -(getFileExt(p1).compareToIgnoreCase(getFileExt(p2)));
					else if(sorttype==6)return -util.compareL(p1.length(),p2.length());
					else if(sorttype==7)return -util.compareL(p1.lastModified(),p2.lastModified());
					return 0;
				}
			};
			Collections.sort(files,cp2);
			if(sorttype==0||sorttype==3||sorttype==4||sorttype==7)Collections.sort(dirs,cp2);
			else Collections.sort(dirs,cp);
			l.clear();
			l.addAll(dirs);
			l.addAll(files);
			ss="文件夹:"+dirs.size()+",文件:"+files.size();
		}
		tmpinfo=ss;
		info.setText(ss);
		adap.notifyDataSetChanged();
		Object sy=scrY.get(path);
		if(sy!=null)mlv.setSelection((int)sy);
	}
	private String getFileExt(mFile f){
		String a=f.getName();
		int c=a.lastIndexOf(".");
		if(c==-1)return a;
		else if(c+1<a.length())return a.substring(c+1);
		else return "";
	}
	private void prop(final mFile f)
	{
		Window y=new Window(ctx,util.px(250),util.px(330))
		.setTitle((select?selected.get(0).getName()+"…("+selected.size()+"项)":f.getName())+" 属性")
		.setIcon("info")
		.setBar(0,8,0)
		.setCanResize(false)
		.show();
		final ViewGroup v=(ViewGroup) y.addView(R.layout.window_fileinfo);
		int d=util.px(40);
		String 类型="未知";
		try
		{
			Bitmap image=VECfile.createBitmap(ctx,"image",d,d),
			classz=VECfile.createBitmap(ctx,"class",d,d),
			music=VECfile.createBitmap(ctx,"music",d,d),
			video=VECfile.createBitmap(ctx,"video",d,d),
			mFile=VECfile.createBitmap(ctx,"mfile",d,d),
			mile=VECfile.createBitmap(ctx,"file",d,d),
			unknown=VECfile.createBitmap(ctx,"unknownfile",d,d),
			folder=VECfile.createBitmap(ctx,"folder",d,d),
			packagee=VECfile.createBitmap(ctx,"package",d,d),
			fsync=VECfile.createBitmap(ctx,"sync",d,d),
			android=VECfile.createBitmap(ctx,"android",d,d);
			Bitmap vec=unknown;
			if(f.isFile())
			{
				String m=util.getMIMEType(f);
				if(m.contains("image"))
				{vec=image;类型="图像";}
				else if(m.contains("vec"))
				{vec=classz;类型="图标设计 矢量图";}
				else if(m.contains("filesync"))
				{vec=fsync;类型="文件同步 配置文件";}
				else if(m.contains("audio"))
				{vec=music;类型="音频";}
				else if(m.contains("video"))
				{vec=video;类型="视频";}
				else if(m.contains("text"))
				{vec=mile;类型="文本";}
				else if(m.contains("android"))
				{vec=android;类型="安装包";}
				else if(m.contains("zip")||m.contains("tar"))
				{vec=packagee;类型="压缩文档";}
				else vec=unknown;
			}
			else if(f.isDirectory())
			{vec=folder;类型="文件夹";}
			if(select)
			{
				vec=mFile;类型="多个文件";
			}
			((ImageView)v.findViewById(R.id.windowfileinfoImageView1)).setImageBitmap(vec);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		final EditText name=(EditText)v.findViewById(R.id.windowfileinfomyEditText1);
		name.setText(select?"多个文件":f.getName());
		name.setEnabled(!select);
		name.setHint(f.getName());
		((TextView)v.getChildAt(2)).append(类型);
		((TextView)v.getChildAt(3)).append(select? f.getParent() :f.getAbsolutePath());
		((TextView)v.getChildAt(3)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				util.copy(select? f.getParent() :f.getAbsolutePath());
				util.toast("已复制路径至剪贴板");
			}
		});
		if(f.isFile()&&!select)((TextView)v.getChildAt(6)).append(util.getFileSizeStr(f.length())+"("+Long.toString(f.length())+" 字节)");
		((TextView)v.getChildAt(7)).append(select?"/":new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(f.lastModified())));
		final CheckBox cb1=(CheckBox) v.findViewById(R.id.windowfileinfomyCheckBox1);
		final CheckBox cb2=(CheckBox) v.findViewById(R.id.windowfileinfomyCheckBox2);
		cb1.setChecked(f.canRead());
		cb2.setChecked(f.canWrite());
		if(select)
		{
			cb1.setEnabled(false);
			cb2.setEnabled(false);
		}
		if(f.isDirectory()||select)
		{
			new AsyncTask(){
				long len=0;
				int fc=0,dc=0;
				private void list(mFile f)
				{
					if(f.isFile())
					{len+=f.length();fc++;}
					if(f.isDirectory())
					{dc++;
						mFile[] gg=f.listFiles();
						if(gg!=null)for(mFile k:gg)list(k);
					}
				}
				@Override
				protected Object doInBackground(Object[] p1)
				{
					if(select)for(mFile x:selected)list(x);
					else
					{list((mFile)p1[0]);dc--;}
					return null;
				}
				@Override
				protected void onPostExecute(Object result)
				{
					v.getChildAt(4).setVisibility(0);
					((TextView)v.getChildAt(4)).append(fc+"个文件 "+dc+"个文件夹");
					((TextView)v.getChildAt(6)).append(util.getFileSizeStr(len)+"("+Long.toString(len)+" 字节)");
				}
			}.execute(f);
		}
		OnCheckedChangeListener o=new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2)
			{
				if(p1==cb1)f.setReadable(p2);
				else if(p1==cb2)f.setWritable(p2);
			}
		};
		cb1.setOnCheckedChangeListener(o);
		cb2.setOnCheckedChangeListener(o);
		y.setOnButtonDown(new Window.OnButtonDown(){
			@Override
			public void onButtonDown(int code)
			{
				if(code==Window.ButtonCode.CLOSE)
				{
					if(!select&&!name.getText().toString().equals(f.getName()))
						try
						{
							if(!f.renameTo(new mFile(f.getParent()+"/"+name.getText().toString())))throw new Exception();
							list();
						}
						catch(Exception e)
						{
							util.toast("重命名失败");
							e.printStackTrace();
						}
				}
			}
		});
	}

	static class ViewHolder
	{
		TextView text1,text2;
		VecView icon;
	}

	public class mFile extends File{
		String path;
		//FtpFile ftp=null;
		public mFile(String f){
			super(f);
			path=f;
			/*if(isFTP()){
				if(myftp!=null)ftp=myftp.getFile(path);
				if(ftp==null)ftp=new FtpFile(f);
			}*/
		}
		/*public mFile(FtpFile f){
			super(f.getPath());
			path=f.getPath();
			ftp=f;
		}*/
		public mFile(File f){
			this(f.getAbsolutePath());
		}
		/*public boolean isFTP(){
			return ftp!=null||path.contains("ftp://");
		}*/
		@Override
		public String getPath()
		{
			//if(isFTP())return ftp.getPath();
			return path;
		}
		@Override
		public boolean canRead()
		{
			//if(isFTP())return ftp.canRead();
			return super.canRead();
		}

		@Override
		public boolean renameTo(File newPath)
		{
			//if(isFTP())return myftp.renameTo(ftp,newPath.getName());
			return super.renameTo(newPath);
		}
		@Override
		public long length()
		{
			//if(isFTP())return ftp.length();
			return super.length();
		}
		@Override
		public boolean mkdirs()
		{
			//if(isFTP())return myftp.mkdirs(ftp.getPath());
			return super.mkdirs();
		}

		@Override
		public boolean delete()
		{
			//if(isFTP())return myftp.delete(ftp.getPath());
			return super.delete();
		}

		@Override
		public boolean canWrite()
		{
			//if(isFTP())return ftp.canWrite();
			return super.canWrite();
		}

		
		@Override
		public boolean createNewFile() throws IOException
		{
			//if(isFTP())return myftp.createNewFile(ftp.getPath());
			return super.createNewFile();
		}

		@Override
		public String getParent()
		{
			//if(isFTP())return ftp.getParent();
			return super.getParent();
		}

		@Override
		public long lastModified()
		{
			//if(isFTP())return ftp.lastModified();
			return super.lastModified();
		}
		@Override
		public mFile[] listFiles()
		{
			/*if(isFTP()){
				FtpFile[] fg=myftp.list(ftp.getPath());
				if(fg==null){
					util.toast("访问出错");
					return null;
				}
				mFile[] m=new mFile[fg.length];
				for(int i=0;i<fg.length;i++)m[i]=new mFile(fg[i]);
				return m;
			}*/
			File[] sr=new File(path).listFiles();
			mFile[] src=null;
			if(sr==null)
				try
				{
					Process p=Runtime.getRuntime().exec("su");
					DataOutputStream os = new DataOutputStream(p.getOutputStream());
					os.writeBytes("ls "+path);
					os.flush();
					os.close();
					BufferedReader o=new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader e=new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String b=null;
					ArrayList<String> st=new ArrayList<String>();
					while((b=o.readLine())!=null)st.add(String.format("%s/%s",path,b));
					while((b=e.readLine())!=null)return null;
					p.waitFor();
					src=new mFile[st.size()];
					int y=0;
					for(String a:st)src[y++]=new mFile(a);
				}
				catch(Throwable e)
				{
					util.toast("访问出错");
					return null;
				}
			else{
				src=new mFile[sr.length];
				for(int i=0;i<src.length;i++)
					src[i]=new mFile(sr[i]);
			}
			return src;
		}

		@Override
		public File getParentFile()
		{
			//if(isFTP())return ftp.getParentFile();
			return super.getParentFile();
		}

		@Override
		public String getAbsolutePath()
		{
			//if(isFTP())return ftp.getAbsolutePath();
			return super.getAbsolutePath();
		}

		@Override
		public boolean exists()
		{
			//if(isFTP())return myftp.exists(ftp.getPath());
			return super.exists();
		}

		@Override
		public boolean isDirectory()
		{
			//if(isFTP())return ftp.isDirectory();
			return super.isDirectory();
		}

		@Override
		public boolean isFile()
		{
			//if(isFTP())return ftp.isFile();
			return super.isFile();
		}
		@Override
		public String getName(){
			//if(isFTP())return ftp.getName();
			int c=path.lastIndexOf("/");
			return path.substring(c+1);
		}
	}
}
