package com.yzrilyzr.engine2d;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.icondesigner.*;
import java.io.*;
import java.util.*;

public abstract class GameActivity extends Activity implements Runnable,View.OnTouchListener,SurfaceHolder.Callback,Eg.GameCBK
{
	Thread renderThread;
	boolean isPause,Running;
	public SurfaceView sv;
	SurfaceHolder hd;
	public Paint p;
	//多缓冲绘制
	static int cachecount=5;
	static Bitmap[] bmpc=new Bitmap[cachecount];
	static Canvas[] cvsc=new Canvas[cachecount];
	static int curdraw=0,lock=0;
	//场景列表
	public ArrayList<Scene> mSceneList=new ArrayList<Scene>();

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
		start();
	}
	public void startScene(Scene e)
	{
		for(Scene s:mSceneList)s.stop();
		mSceneList.add(e);
		e.start();
	}
	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		// TODO: Implement this method
		return false;
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
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		isPause=true;
		for(Scene s:mSceneList)s.pause();
		pause();
	}

	@Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
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
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		isPause=false;
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
			bmpc[i]=Bitmap.createBitmap(Eg.pi(sv.getWidth()),Eg.pi(sv.getHeight()),Bitmap.Config.ARGB_8888);
			cvsc[i]=new Canvas(bmpc[i]);
		}
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
								}
							//cs.drawBitmap(banner,m,p);
							if(cs!=null)hd.unlockCanvasAndPost(cs);
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
				if(++curdraw>=cvsc.length)curdraw=0;
				if(curdraw==lock)continue;
				//if(curdraw==lock)curdraw++;
				//if(curdraw>=cvsc.length)curdraw=0;
				c=cvsc[curdraw];
				//绘制
				c.drawColor(Eg.bgcolor);
				render(c,dt/1000000f);
				for(Scene s:mSceneList)s.render(c,dt/1000000f);
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
					p.setTextSize(Shape.p(50));
					c.drawText(String.format("FPS:%d Shape:d RAM:d Size:%dx%d x:d y:d",fps,(int)getAbsWidth(),(int)getAbsHeight()),0,Shape.p(50),p);
				}
				dt=System.nanoTime()-ns;
				ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
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


	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		Running=false;
		stop();
		for(Scene s:mSceneList)s.stop();
		super.onDestroy();
	}


}
