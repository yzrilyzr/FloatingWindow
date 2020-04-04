package com.yzrilyzr.engine2d;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.icondesigner.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class GameActivity extends Activity implements Runnable,View.OnTouchListener,SurfaceHolder.Callback,Eg.GameCBK
{
	Thread renderThread;
	boolean isPause,Running;
	public SurfaceView sv;
	SurfaceHolder hd;
	public Paint p;
	//触摸
	float ppx,ppy,lxx,lyy;
	//视图相关
	Ui tui;
	//多缓冲绘制
	static int cachecount=5;
	static Bitmap[] bmpc=new Bitmap[cachecount];
	static Canvas[] cvsc=new Canvas[cachecount];
	static int[] bmpcuseage=new int[cachecount];
	static int[] bmpcuseage2=new int[cachecount];
	static int curdraw=0,lock=0;
	//场景列表
	public CopyOnWriteArrayList<Scene> mSceneList=new CopyOnWriteArrayList<Scene>();

	public float getAbsHeight()
	{
		return bmpc[0].getHeight();
	}

	public float getAbsWidth()
	{
		return bmpc[0].getWidth();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		Eg.setContext(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		VECfile.VTypeface.DEFAULT=Typeface.createFromAsset(getAssets(),"font.ttf");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(sv=new SurfaceView(this));
		sv.setLongClickable(true);
		hd=sv.getHolder();
		sv.setOnTouchListener(this);
		hd.addCallback(this);
		if(renderThread==null)
		{
			renderThread=new Thread(this);
			renderThread.start();
		}
		else
		{
			toast("游戏正在运行");
		}
	}
	public void startScene(Scene e)
	{
		mSceneList.add(e);
		e.start();
	}
	@Override
	public boolean onTouch(View p1, MotionEvent event)
	{
		event.setLocation(Eg.getAbsWidth()*event.getX()/sv.getWidth(),Eg.getAbsHeight()*event.getY()/sv.getHeight());
		ppx=event.getX()/Eg.getAbsWidth()*100f;
		ppy=event.getY()/Eg.getAbsHeight()*100f;
		for(int ii=mSceneList.size()-1;ii>=0;ii--)
		{
			Scene sc=mSceneList.get(ii);
			for(int i=sc.uis.size()-1;i>=0;i--)
			{
				Ui s=sc.uis.get(i);
				if(s.anim.size()>0)continue;
				if(Ui.down(event))
				{
					lxx=event.getX();
					lyy=event.getY();
					if(s.contains(event.getX(),event.getY()))
					{
						tui=s;
						tui.onDown(event);
						break;
					}
					else tui=null;
				}
				else if(Ui.move(event)&&tui!=null&&Math.abs(event.getX()-lxx)>Eg.p(0.5f)&&Math.abs(event.getY()-lyy)>Eg.p(0.5f))
				{
					tui.onMove(event);
					return true;
				}
				else if(Ui.up(event)&&tui!=null&&tui.contains(event.getX(),event.getY()))
				{
					tui.onClick(event);
					tui=null;
					return false;
				}
			}
			if(tui!=null)break;
		}
		return true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		// TODO: Implement this method
	}
	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		super.onPause();
		isPause=true;
	}
	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		isPause=false;
	}
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		Running=false;
		stop();
		for(Scene s:mSceneList)s.stop();
		Eg.cache.clear();
		mSceneList.clear();
		super.onDestroy();
		//System.exit(0);
	}

	public void toast(final Throwable e)
	{
		new Handler(getMainLooper()).post(new Runnable(){
				@Override
				public void run()
				{
					ByteArrayOutputStream b=new ByteArrayOutputStream();
					PrintStream p=new PrintStream(b);
					e.printStackTrace(p);
					new AlertDialog.Builder(GameActivity.this)
						.setTitle("错误")
						.setMessage(b.toString())
						.show();
				}
			});
	}
	public void toast(final String s)
	{
		new Handler(getMainLooper()).post(new Runnable(){
				@Override
				public void run()
				{
					Toast.makeText(GameActivity.this,s,1).show();
				}
			});
	}
	@Override
	public void run()
	{
		Eg.delay(500);
		Running=true;
		long ns=System.nanoTime(),dt=0;
		//Matrix m=new Matrix();
		Runtime ru=Runtime.getRuntime();
		int lt=0,fps=0;
		float avgfps=0;
		int ram=0;
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(0xffff0000);
		p.setTypeface(VECfile.VTypeface.DEFAULT);
		Canvas c=null;
		Eg.setPaint(p);
		//多缓冲初始化
		for(int i=0;i<bmpc.length;i++)
		{
			bmpc[i]=Bitmap.createBitmap(sv.getWidth(),sv.getHeight(),Bitmap.Config.ARGB_8888);
			cvsc[i]=new Canvas(bmpc[i]);
			bmpcuseage[i]=0;
			bmpcuseage2[i]=0;
		}
		start();
		//多缓冲线程
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					/*Bitmap banner=null;
					 try
					 {
					 Thread.sleep(300);
					 banner=VECfile.createBitmap(ctx,"banner",sv.getHeight(),2*sv.getHeight()/9);
					 }
					 catch (Exception e)
					 {}*/
					/*
					 Matrix m=new Matrix();
					 m.postTranslate(-banner.getWidth()/2,-banner.getHeight()/2);
					 m.postRotate(270);
					 m.postTranslate(banner.getHeight()/2,banner.getWidth()/2);
					 m.postTranslate(16*sv.getHeight()/9,0);*/
					Matrix m2=new Matrix();
					int lres=0;
					Paint p=new Paint();
					while(Running)
					{
						try
						{
							if(isPause)
							{
								Thread.sleep(20);
								continue;
							}
							Canvas cs=hd.lockCanvas();
							if(cs!=null)
							{

								lock=curdraw-1;
								if(lock<0)lock=bmpc.length-1;
								int w=lock;
								if(bmpc[w]!=null)
								//if(sv.getHeight()==bmpc[w].getHeight())cs.drawBitmap(bmpc[w],0,0,p);
								//else
								{
									if(lres!=bmpc[w].getHeight())
									{
										m2.reset();
										m2.postScale((float)sv.getWidth()/bmpc[0].getWidth(),(float)sv.getHeight()/bmpc[0].getHeight());
										lres=bmpc[w].getHeight();
									}
									cs.drawBitmap(bmpc[w],m2,p);
									bmpcuseage[w]++;
								}
								hd.unlockCanvasAndPost(cs);
							}
							//cs.drawBitmap(banner,m,p);
							lock=-1;
							Thread.sleep(0,1000);
						}
						catch (Exception e)
						{
							toast(e);
							break;
						}
					}
				}
			}).start();

		//主线程
		while(Running)
		{
			try
			{
				//暂停
				if(isPause)
				{
					Thread.sleep(20);
					ns=System.nanoTime();
					continue;
				}
				//获取缓冲区
				if(++curdraw>=cvsc.length)
				{
					curdraw=0;
					for(int i=0;i<bmpcuseage.length;i++)
					{
						bmpcuseage2[i]=bmpcuseage[i];
						bmpcuseage[i]=0;
					}
				}
				if(curdraw==lock)continue;
				//if(curdraw==lock)curdraw++;
				//if(curdraw>=cvsc.length)curdraw=0;
				c=cvsc[curdraw];
				//重置计数器
				Eg.drawcount=0;
				//绘制
				c.drawColor(Eg.bgcolor);
				render(c,dt/1000000f);
				for(Scene s:mSceneList)s.render(c,dt/1000000f);
				//for(Ui u:uis)u.render(c,dt/1000000f);
				p.setStyle(Paint.Style.STROKE);
				//显示网格
				if(Eg.showgrid)
				{
					p.setStrokeWidth(1);
					float k=getAbsWidth()/20;
					for(int i=0;i<20;i++)
					{
						p.setColor(i%4==0?0xff00ff00:0xffff0000);
						c.drawLine(i*k,0,i*k,getAbsHeight(),p);
					}
					k=getAbsHeight()/20;
					for(int u=0;u<20;u++)
					{
						p.setColor(u%4==0?0xff00ff00:0xffff0000);
						c.drawLine(0,u*k,getAbsWidth(),u*k,p);
					}
					p.setColor(0xff0000ff);
					c.drawLine(0,ppy*getAbsHeight()/100f,getAbsWidth(),ppy*getAbsHeight()/100f,p);
					c.drawLine(ppx*getAbsWidth()/100f,0,ppx*getAbsWidth()/100f,getAbsHeight(),p);
				}
				p.setStyle(Paint.Style.FILL);
				//fps相关
				if(Eg.showfps)
				{
					if(dt==0)dt=1;
					p.setTextAlign(Paint.Align.LEFT);
					lt++;
					if(lt<=10)avgfps+=1000000000l/dt;
					else
					{
						fps=(int)(avgfps/10f);
						lt=0;
						avgfps=0;
					}
					p.setColor(0xffff0000);
					p.setTextSize(Eg.p(5));
					ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
					c.drawText(String.format("FPS:%d Draw:%d Cache:%d RAM:%dMB Size:%dx%d x:%d y:%d",fps,Eg.drawcount,Eg.cache.size(),ram,(int)getAbsWidth(),(int)getAbsHeight(),(int)ppx,(int)ppy),0,Eg.p(5),p);
					int to=0;
					for(int i=0;i<bmpcuseage2.length;i++)to+=bmpcuseage2[i];
					c.drawLine(0,Eg.p(10),bmpcuseage2.length*Eg.p(5),Eg.p(10),p);
					if(to!=0)
						for(int i=0;i<bmpcuseage2.length;i++)
						{
							c.drawRect(i*Eg.p(5),Eg.p(15)-bmpcuseage2[i]*Eg.p(5)/to,(i+1)*Eg.p(5),Eg.p(15),p);
						}
				}
				dt=System.nanoTime()-ns;
				if(dt<1000000000/Eg.fpslimit)Thread.sleep((int)((float)(1000000000/Eg.fpslimit-(int)dt)/1000000f));
				dt=System.nanoTime()-ns;
				ns=System.nanoTime();
				//if(map!=null)map.lock=false;
			}
			catch(Throwable e)
			{
				//System.exit(0);。
				toast(e);
				break;
			}
		}
		Running=false;
		renderThread=null;
	}
}
