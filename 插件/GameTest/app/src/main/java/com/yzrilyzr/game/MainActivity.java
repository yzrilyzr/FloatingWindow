package com.yzrilyzr.game;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import com.yzrilyzr.icondesigner.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.concurrent.*;

public class MainActivity extends Activity implements /*SurfaceHolder.Callback,*/OnTouchListener
{
	//SurfaceView sv;
	public CopyOnWriteArrayList<Scene> scenes=new CopyOnWriteArrayList<Scene>();
	//Render renderRunn=null;
	mView sv;

	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	int lt,avgfps,fps,fps2,ram;
	public MIDIParser mp;
	Runtime ru=Runtime.getRuntime();
	float lowfps=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//getWindow().getAttributes().layout
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setContext(this);
		Typeface go=Typeface.createFromAsset(getAssets(),"showgothic.ttf");
		Typeface m=Typeface.createFromAsset(getAssets(),"m.ttf");
		VECfile.addTypefaceMap(0,'`',go);
		VECfile.addTypefaceMap('{','~',go);
		VECfile.addTypefaceMap(0x4E00,0x9FA5,m);
		VECfile.VTypeface.DEFAULT=m;
		//sv=new SurfaceView(this);
		//sv.getHolder().addCallback(this);
		//renderRunn=new Render();
		//sv.setOnTouchListener(this);
		//setContentView(sv);
		setContentView(sv=new mView(this));
		sv.getLayoutParams().width=-1;
		sv.getLayoutParams().height=-1;
		sv.setBackgroundColor(0x00000000);
		sv.setOnTouchListener(this);
		/*try{
		mp=new MIDIParser(getAssets().open("2_171.mid"));
		}catch(Throwable d){
			Utils.alert(d);
		}*/
		//sv.setLayerType(sv.LAYER_TYPE_SOFTWARE, null);
    }
	/*@Override
	 public void surfaceCreated(SurfaceHolder p1)
	 {
	 new Thread(renderRunn).start();
	 }

	 @Override
	 public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	 {
	 // TODO: Implement this method
	 }

	 @Override
	 public void surfaceDestroyed(SurfaceHolder p1)
	 {
	 renderRunn.render=false;
	 }

	 /*class Render implements Runnable
	 {
	 public boolean render=true;
	 public long nt=System.nanoTime();
	 public Paint p=new Paint();
	 int cachecount=3;
	 Bitmap[] bmpc=new Bitmap[cachecount];
	 Canvas[] cvsc=new Canvas[cachecount];
	 int[] bmpcuseage=new int[cachecount];
	 int[] bmpcuseage2=new int[cachecount];
	 int curdraw=0,lock=0;
	 int lt2=0,fps2=0,avgfps2=0;
	 long nt2=System.nanoTime(),dt2=1;
	 @Override
	 public void run()
	 {
	 try
	 {
	 Thread.sleep(500);
	 int width=(int)Utils.getWidth(),height=(int)Utils.getHeight();
	 if(height>width)height=width*width/height;
	 for(int i=0;i<bmpc.length;i++)
	 {
	 bmpc[i]=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
	 cvsc[i]=new Canvas(bmpc[i]);
	 bmpcuseage[i]=0;
	 bmpcuseage2[i]=0;
	 }
	 new Thread(new Runnable(){
	 @Override
	 public void run()
	 {
	 Matrix m2=new Matrix();
	 int lres=0;
	 Paint p=new Paint();
	 //SurfaceHolder h=sv.getHolder();
	 while(render)
	 {
	 try
	 {
	 /*if(isPause)
	 {
	 Thread.sleep(20);
	 continue;
	 }
	 Canvas cs=new Canvas();//h.lockCanvas();
	 if(cs!=null)
	 {

	 lock=curdraw-1;
	 if(lock<0)lock=bmpc.length-1;
	 lt2++;
	 if(lt2<=10)avgfps2+=1000000000l/dt2;
	 else
	 {
	 fps2=(int)(avgfps2/10f);
	 lt2=0;
	 avgfps2=0;
	 }

	 int w=lock;
	 if(bmpc[w]!=null)
	 //if(sv.getHeight()==bmpc[w].getHeight())cs.drawBitmap(bmpc[w],0,0,p);
	 //else
	 {
	 if(lres!=bmpc[w].getHeight())
	 {
	 //m2.reset();
	 //m2.postScale((float)sv.getWidth()/bmpc[0].getWidth(),(float)sv.getHeight()/bmpc[0].getHeight());
	 lres=bmpc[w].getHeight();
	 }
	 cs.drawBitmap(bmpc[w],m2,p);
	 bmpcuseage[w]++;
	 }
	 //h.unlockCanvasAndPost(cs);
	 dt2=System.nanoTime()-nt2;
	 nt2=System.nanoTime();
	 }
	 //cs.drawBitmap(banner,m,p);
	 lock=-1;
	 Thread.sleep(0,1000);
	 }
	 catch (Exception e)
	 {
	 Utils.alert(e);
	 break;
	 }
	 }
	 }
	 }).start();
	 int lt=0,avgfps=0,fps=0,ram=0;
	 Runtime ru=Runtime.getRuntime();
	 while(render)
	 {
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
	 Canvas c=cvsc[curdraw];
	 if(c==null)
	 {
	 Thread.sleep(20);
	 continue;
	 }
	 for(Scene sc:scenes)
	 sc.onDraw(c);
	 //c.drawText(String.format("FPS:%d,UIs:%d,Scenes:%d",fps=((int)(1000f/Utils.dt)+fps)/2,uis,scenes.size()),0,Utils.px(80),p);
	 if(Utils.showfps)
	 {
	 if(Utils.dt==0)Utils.dt=1;
	 p.setTextAlign(Paint.Align.LEFT);
	 lt++;
	 if(lt<=10)avgfps+=1000000000l/Utils.dt;
	 else
	 {
	 fps=(int)(avgfps/10f);
	 lt=0;
	 avgfps=0;
	 }
	 p.setColor(0xffff0000);
	 p.setTextSize(Utils.px(20));
	 ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
	 c.drawText(String.format("FPS(B):%d FPS(F):%d RAM:%dMB Size:%fx%f",fps,fps2,ram,Utils.getWidth(),Utils.getHeight()),0,Utils.px(20),p);
	 int to=0;
	 for(int i=0;i<bmpcuseage2.length;i++)to+=bmpcuseage2[i];
	 c.drawLine(0,Utils.px(30),bmpcuseage2.length*Utils.px(30),Utils.px(30),p);
	 if(to!=0)
	 for(int i=0;i<bmpcuseage2.length;i++)
	 {
	 c.drawRect(i*Utils.px(30),Utils.px(60)-bmpcuseage2[i]*Utils.px(30)/to,(i+1)*Utils.px(30),Utils.px(60),p);
	 }
	 }
	 Utils.dt=System.nanoTime()-nt;
	 if(Utils.dt<1000000000/Utils.fpslimit)Thread.sleep((int)((float)(1000000000/Utils.fpslimit-(int)Utils.dt)/1000000f));
	 Utils.dt=System.nanoTime()-nt;
	 nt=System.nanoTime();
	 }
	 }
	 catch(Throwable e)
	 {
	 Utils.alert(e);
	 }
	 render=true;
	 }
	 }
	 */
	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		try
		{
			for(int i=scenes.size()-1;i>=0;i--)
			{
				Scene u=scenes.get(i);
				if(u!=null&&u.onTouch(p2))return true;
			}
		}
		catch(Throwable e)
		{
			Utils.alert(e);
		}
		return false;
	}
	class mView extends View
	{
		long st=System.nanoTime();
		public mView(Context c)
		{
			super(c);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			// TODO: Implement this method
			//super.onDraw(canvas);
			Utils.draws=0;
			if(Utils.backcolor!=0)canvas.drawColor(Utils.backcolor);
			try
			{
				for(Scene sc:scenes)
					sc.onDraw(canvas);
			}
			catch(Throwable e)
			{
				scenes.clear();
				Utils.alert(e);
			}
			float dt=0;
		/*	for(MIDIParser.Event e:mp.events){
				p.setColor(0x80ff0000);
				dt+=e.dt/1000f+10;
				canvas.drawRect(dt,e.noteon*10,dt+10,10+e.noteon*10,p);
				p.setColor(0x1000ff00);
				canvas.drawRect(dt,e.keyaftertouch*10,dt+10,10+e.keyaftertouch*10,p);
			}*/
			if(Utils.showfps)
			{
				if(Utils.dt==0)Utils.dt=1;
				p.setTextAlign(Paint.Align.LEFT);
				lt++;
				if(lt<=10)avgfps+=1000000000l/Utils.dt;
				else
				{
					fps=(int)(avgfps/10f);
					lt=0;
					avgfps=0;
				}
				p.setColor(0xffff0000);
				p.setTextSize(Utils.px(20));
				ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
				if(lowfps>0){
					canvas.drawText("■LOW  FPS!",0,getHeight(),p);
					lowfps-=Utils.getDtMs();
				}
				canvas.drawText(String.format("FPS(B):%d FPS(F):%d RAM:%dMB Size:%fx%f Draws:%d",fps,fps2,ram,Utils.getWidth(),Utils.getHeight(),Utils.draws),0,Utils.px(20),p);
				float y=p.getTextSize(),yy=y;
				p.setTextSize(Utils.px(12));
				for(Scene s:scenes){
					canvas.drawText(s.id,0,y+=p.getTextSize(),p);
					for(Ui u:s.uis){
						canvas.drawText(u.id,getWidth()/2,yy+=p.getTextSize(),p);
					}
					
				}
				/*int to=0;
				 for(int i=0;i<bmpcuseage2.length;i++)to+=bmpcuseage2[i];
				 c.drawLine(0,Utils.px(30),bmpcuseage2.length*Utils.px(30),Utils.px(30),p);
				 if(to!=0)
				 for(int i=0;i<bmpcuseage2.length;i++)
				 {
				 c.drawRect(i*Utils.px(30),Utils.px(60)-bmpcuseage2[i]*Utils.px(30)/to,(i+1)*Utils.px(30),Utils.px(60),p);
				 }*/
			}
			long t=System.nanoTime()-st;
			st=System.nanoTime();
			if(t>33000000)lowfps=1000;
			Utils.dt=Math.min(t,20000000);
			invalidate();
		}

	}
}
