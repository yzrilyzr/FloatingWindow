package com.yzrilyzr.engine2d;

import android.view.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import android.graphics.RectF;
import java.io.FileInputStream;
import android.graphics.Typeface;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener
{
	//global
	static SurfaceView sv;
	static SurfaceHolder hd;
	static boolean run=false,pause=false,inited=false;
	static Runnable rb=null;
	//final static CopyOnWriteArrayList<Shape> sh=new CopyOnWriteArrayList<Shape>();
    final static CopyOnWriteArrayList<Ui> ui=new CopyOnWriteArrayList<Ui>();
	static Context ctx;
	static int cachecount=5;
	static Bitmap[] bmpc=new Bitmap[cachecount];
	static Canvas[] cvsc=new Canvas[cachecount];
	static int curdraw=0,lock=0;
	Shape tui;
	static final String mainDir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/Bugs/";
	private int curui=0;
	private int ppx,ppy;
	float lxx,lyy;
	//data
	public static int plevel=0,exp=0,pscore=0,pbugs=0,pmoney=0,levelunlock=0,unlocktower=1,unlockulevel=0;//解锁至
	public static int musicv=70,musiceffv=80,fpslimit=30;
	public static int resolution=50;
	public static boolean backgrun=false,showfps=false,showgrid=false;
	public static float shadowCoverIndex=-1,shadowCoverTime=0,shadowCoverMode=0,shadowLayers=0;
	//uiload=0
	private Ui loadcode;
	//uimainmenu=1
	private Ui buttonmainmenu,mainmenubuttback,mainmenutitle,mainmenustart,mainmenucustom,mainmenututorial,mainmenubugicon,mainmenusettings,mainmenuabout,mainmenuyzr;
	public CopyOnWriteArrayList<Bug> mainmenubug=new CopyOnWriteArrayList<Bug>();
	//uiselectlevel=2
	private UiGroup uiLevelSelect;
	//private Ui levelselectmyjb,levelselectlist,levelselectmap,levelselectplayer;
	//private List levelselectllist;
	//uimyjb=3

	//uiganemain=4
	static Map map=null;
	private Ui gamerightmenu;
	private static Ui uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup;
	private int nowplevel=0;
	private float gamespeed=1;
	private Ui gamepause,gamesetting,gamex2,gamesendnow;

	//private float deltax=0,deltay=0,scale=1,lscale=1,lpointLen;
	//private boolean moved=false;
	//private float ddx,ddy;

	//uiselectmap=6

	//uimapedit=7;

	//uibugsicon=8

	//uitutorial=9

	//uisettings
	private Ui uisetting,uisettclose,uisetshadow;
	private Switch uishowfps,uibackrun,uishowgrid;
	private SeekBar uisetmv,uisetmfv,uisetfps,uisetres;
	//uiabout
	//private Ui uiabout,uiaboutok,uiaboutbesto,uiaboutyzr,shadowcover2;
	private UiGroup uiAbout;
	private UiGroup uiExit;
	//
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		ctx=this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(sv=new SurfaceView(this));
		sv.setLongClickable(true);
		hd=sv.getHolder();
		sv.setOnTouchListener(this);
		hd.addCallback(this);
	}
	@Override
	public boolean onTouch(View v,MotionEvent event)
	{
		ppx=(int)(event.getX()*1600/sv.getWidth());
		ppy=(int)(event.getY()*900/sv.getHeight());
		event.setLocation(Shape.p(1600)*event.getX()/sv.getWidth(),Shape.p(900)*event.getY()/sv.getHeight());
		for(int i=ui.size()-1;i>=0;i--)
		{
			if(i==shadowCoverIndex)break;
			Ui s=ui.get(i);
			if(s.isAlphaFrom||s.isAlphaTo||s.isFrom||s.isTo)continue;
			if(Shape.down(event))
			{
				lxx=event.getX();
				lyy=event.getY();
				if(s.contains(event.getX(),event.getY())&&(s).visible)
				{
					tui=s;
					tui.onDown(event);
					break;
				}
				else tui=null;
			}
			else if(Shape.move(event)&&tui!=null&&Math.abs(event.getX()-lxx)>Shape.p(10)&&Math.abs(event.getY()-lyy)>Shape.p(10))
			{
				tui.onMove(event);
				return true;
			}
			else if(Shape.up(event)&&tui!=null&&tui.contains(event.getX(),event.getY()))
			{
				tui.onClick(event);
				tui=null;
				return false;
			}
		}
		if((curui==1||curui==2||curui==3||curui==6||curui==8)&&event.getAction()==MotionEvent.ACTION_DOWN)
		{
			for(Bug bg: mainmenubug){
				if(bg.contains(event.getX(),event.getY())&&bg.hp>0){
					bg.hp=0;
					break;
				}
			}
		}
		if(curui==4)
		{
			if(map==null)return false;
			int a=event.getAction();
			float x=event.getX(),y=event.getY();
			int mx=(int) Math.floor((x-Shape.p(100))*map.size/Shape.p(900));
			int my=(int) Math.floor(y*map.size/Shape.p(900));
			if(a==MotionEvent.ACTION_UP&&
			map.selectedTower!=null&&
			!map.towers.contains(map.selectedTower)&&
			x>Shape.p(100)&&x<Shape.p(1000)&&
			map.money-map.selectedTower.money>=0)
			{
				if(map.isWall(mx,my)||map.containBug(mx,my))
				{
					toast("不能放于此处");
				}
				else{
					map.selectedTower.x=mx;
					map.selectedTower.y=my;
					map.towers.add(map.selectedTower);
					if(!map.findWayPoint())
					{
						map.towers.remove(map.selectedTower);
						map.selectedTower=null;
						toast("不能放于此处");
					}
					else map.money-=map.selectedTower.money;
					
				}
			}
			if(a==MotionEvent.ACTION_UP)
			{
				map.selectedTower=null;
				for(Tower t:map.towers)
					if(t.x==mx&&t.y==my)
					{
						map.selectedTower=t;
						break;
					}
			}
			/*try
			 {
			 if(event.getPointerCount()==1)
			 {
			 if(a==MotionEvent.ACTION_DOWN)moved=false;
			 else if(a==MotionEvent.ACTION_UP)
			 {
			 moved=false;
			 //map.loadTiles(scale);
			 }
			 }
			 else if(event.getPointerCount()==2)
			 {
			 float x1=event.getX(1),y1=event.getY(1);
			 float x=event.getX(0),y=event.getY(0);
			 if(!moved)
			 {
			 if(x1>Shape.p(1100)||x>Shape.p(1100))return false;

			 ddx=(x+x1)/2;
			 ddy=(y+y1)/2;
			 lpointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
			 lscale=scale;
			 moved=true;
			 }
			 else
			 {
			 float pointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
			 float llsc=scale;
			 scale=lscale*pointLen/lpointLen;
			 float cx=(x+x1)/2f,cy=(y+y1)/2f;
			 deltax=(deltax-cx/llsc)+cx/scale;
			 deltay=(deltay-cy/llsc)+cy/scale;
			 deltax-=(ddx-(x+x1)/2)/scale;
			 deltay-=(ddy-(y+y1)/2)/scale;
			 ddx=(x+x1)/2;
			 ddy=(y+y1)/2;
			 //deltax=limit(deltax,Shape.p(1100)-Shape.p(900f)*map.mscale*scale,0);
			 //deltay=limit(deltay,Shape.p(900)*map.mscale-Shape.p(900f)*map.mscale*scale,0);
			 /*if(scale<1100f/900f)deltax=Shape.p(100f)-Shape.p(100f)*(scale-1)/(1100f/900f-1);
			 if(scale<=1)
			 {
			 scale=1;
			 deltax=Shape.p(100);
			 deltay=0;
			 }
			 }
			 }
			 }
			 catch (Exception e)
			 {}*/
		}
		return true;
	}
	public static float limit(float x,float min,float max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static long limit(long x,long min,long max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int limit(int x,int min,int max)
	{
		return Math.max(Math.min(x,max),min);
	}

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		if(rb==null&&!run)start();
	}
	public void start()
	{
		run=true;
		if(rb==null)
		{
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					if(!inited)
					{
						SharedPreferences sp=ctx.getSharedPreferences("data",MODE_PRIVATE);
						resolution=sp.getInt("resolution",resolution);
						fpslimit=sp.getInt("fpslimit",fpslimit);
						musicv=sp.getInt("musicv",musicv);
						musiceffv=sp.getInt("musiceffv",musiceffv);
						backgrun=sp.getBoolean("backgrun",backgrun);
						showfps=sp.getBoolean("showfps",showfps);
						showgrid=sp.getBoolean("showgrid",showgrid);
						plevel=sp.getInt("plevel",plevel);
						exp=sp.getInt("exp",exp);
						pscore=sp.getInt("pscore",pscore);
						pbugs=sp.getInt("pbugs",pbugs);
						pmoney=sp.getInt("pmoney",pmoney);
						levelunlock=sp.getInt("levelunlock",levelunlock);
						unlocktower=sp.getInt("unlocktower",unlocktower);
						unlockulevel=sp.getInt("unlockulevel",unlockulevel);

						Shape.scale=sv.getHeight()*((float)resolution/100f)/900f;
						try
						{
							Thread.sleep(300);
						}
						catch (InterruptedException e)
						{}
						rb=this;
						//sh.clear();
						ui.clear();
						for(int i=0;i<bmpc.length;i++)
						{
							bmpc[i]=Bitmap.createBitmap(Shape.pi(1600),Shape.pi(900),Bitmap.Config.ARGB_8888);
							cvsc[i]=new Canvas(bmpc[i]);
						}
						File f=new File(mainDir);
						if(!f.exists())f.mkdirs();
						//uiGameMain();
						load();
						//mainmenu();
						//uiSelLevel();
						inited=true;
					}
					Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
					p.setColor(0xffff0000);
					p.setTypeface(VECfile.VTypeface.DEFAULT);
					//p.setFilterBitmap(true);
					long ns=System.nanoTime(),dt=0;
					//Matrix m=new Matrix();
					Runtime ru=Runtime.getRuntime();
					int lt=0,fps=0;
					float avgfps=0;
					int ram=0;
					while(run)
					{
						try
						{
							if(pause)
							{
								Thread.sleep(20);
								ns=System.nanoTime();
								continue;
							}
							if(map!=null)map.lock=true;
							//if(curdraw==lock)continue;
							if(++curdraw>=cvsc.length)curdraw=0;
							if(curdraw==lock)continue;
							//if(curdraw==lock)curdraw++;
							//if(curdraw>=cvsc.length)curdraw=0;
							Canvas c=cvsc[curdraw];
							c.drawColor(0xff333333);
							//uimainmeuu
							if(curui==1||curui==2||curui==3||curui==6||curui==8)
							{
								Random r=new Random();
								while(mainmenubug.size()<20)mainmenubug.add(new Bug(
									r.nextBoolean()?0:Shape.p(1600),
									r.nextBoolean()?0:Shape.p(900),
									75+r.nextInt(50)));
								float ro=Shape.p(50);
								float ddt=dt/1000000000f;
								for(Bug pp:mainmenubug)
								{
									if(pp.hp>0){
									pp.vx=limit(pp.vx+pp.ax*ddt,-pp.vel,pp.vel);
									pp.vy=limit(pp.vy+pp.ay*ddt,-pp.vel,pp.vel);
									pp.x+=pp.vx*ddt;
									pp.y+=pp.vy*ddt;
									if(pp.x<-ro)pp.x=Shape.p(1600)+ro;
									if(pp.y<-ro)pp.y=Shape.p(900)+ro;
									if(pp.x>Shape.p(1600)+ro)pp.x=-ro;
									if(pp.y>Shape.p(900)+ro)pp.y=-ro;
									pp.ax=(r.nextBoolean()?1:-1)*Shape.p(150f);
									pp.ay=(r.nextBoolean()?1:-1)*Shape.p(150f);
									}
									else if((pp.hp-=(dt/1000000f))<-2000)mainmenubug.remove(pp);
									pp.onDraw(c);
								}

							}
							//uigamemain
							else if(curui==4)
							{
								try
								{
									if(map!=null)
									{
										//deltax=limit(deltax,Shape.p(1100)-Shape.p(900f)*map.mscale*scale,0);
										//deltay=limit(deltay,Shape.p(900)*map.mscale-Shape.p(900f)*map.mscale*scale,0);
										/*if(scale<1100f/900f)deltax=Shape.p(100f)-Shape.p(100f)*(scale-1)/(1100f/900f-1);
										 if(scale<=1)
										 {
										 scale=1;
										 deltax=Shape.p(100);
										 deltay=0;
										 }*/
										p.setColor(0xffff0000);
										p.setStrokeWidth(Shape.p(4));
										if(map.mapcache!=null)
										{
											//m.postTranslate(-map.mapcache.getWidth()/2,-map.mapcache.getHeight()/2);
											//m.postScale(scale,scale);
											//m.reset();
											//m.postTranslate(deltax,deltay);
											//	m.postScale(scale,scale);
											c.drawBitmap(map.mapcache[map.which?1:0],Shape.p(1100)/2-map.mapcache[0].getWidth()/2,0,p);
										}

										//c.drawLine(0,0,-deltax*scale,-deltay*scale,p);
										p.setColor(0xbbffffff);
										//c.drawPoint(-deltax,-deltay,p);
										p.setTextAlign(Paint.Align.LEFT);
										p.setTextSize(Shape.p(40));
										//p.setColor(0xff22ff22);
										c.drawText(String.format("分数:%d",map.score),0,Shape.p(40),p);
										c.drawText(String.format("等级:%d",plevel),0,Shape.p(885),p);
										p.setTextAlign(Paint.Align.CENTER);
										c.drawText(String.format("生命:%d",map.lives),Shape.p(550),Shape.p(40),p);
										p.setTextAlign(Paint.Align.RIGHT);
										c.drawText(String.format("金钱:%d",map.money),Shape.p(1100),Shape.p(40),p);
										RectF rf=new RectF();
										rf.set(Shape.p(150),Shape.p(860),Shape.p(1050),Shape.p(890));
										p.setStyle(Paint.Style.STROKE);
										c.drawRoundRect(rf,Shape.p(3),Shape.p(3),p);
										p.setStyle(Paint.Style.FILL);
										rf.set(Shape.p(150),Shape.p(860),Shape.p(150)+Shape.p(900)*exp/(float)(200f*Math.pow(1.25,plevel)),Shape.p(890));
										c.drawRoundRect(rf,Shape.p(3),Shape.p(3),p);
										if(exp>200*Math.pow(1.25,plevel))
										{
											plevel++;
											exp=0;
											uiLevelUp();
										}
									}
								}
								catch(Throwable e)
								{
									toast(e);
								}
							}
							//for(Shape s:sh)s.onDraw(c);
							for(int i=0;i<ui.size();i++){
								if(i==shadowCoverIndex){
									c.drawARGB((int)(shadowLayers*100f*Ui.NonLinearFunc(shadowCoverTime/300f)),0,0,0);
									shadowCoverTime+=shadowCoverMode*dt/1000000f;
									if(shadowCoverTime<0)shadowCoverIndex=-1;
									else if(shadowCoverTime>300)shadowCoverTime=300;
								}
								ui.get(i).onDraw(c);
							}
							if(showgrid)
							{
								p.setStrokeWidth(1);
								for(int i=0;i<1600;i+=50)
								{
									p.setColor(i%250==0?0xff00ff00:0xffff0000);
									c.drawLine(Shape.p(i),0,Shape.p(i),Shape.p(900),p);
								}
								for(int u=0;u<900;u+=50)
								{
									p.setColor(u%250==0?0xff00ff00:0xffff0000);
									c.drawLine(0,Shape.p(u),Shape.p(1600),Shape.p(u),p);
								}
								p.setColor(0xff0000ff);
								c.drawLine(0,Shape.p(ppy),Shape.p(1600),Shape.p(ppy),p);
								c.drawLine(Shape.p(ppx),0,Shape.p(ppx),Shape.p(900),p);
							}
							if(showfps)
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
								c.drawText(String.format("FPS:%d Shape:%d RAM:%d Size:%dx%d x:%d y:%d",fps,/*sh.size()*/+ui.size(),ram,bmpc[0].getWidth(),bmpc[0].getHeight(),ppx,ppy/*,deltax,deltay,scale*/),0,Shape.p(50),p);
							}
							dt=System.nanoTime()-ns;
							ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
							if(fpslimit!=120&&dt<1000000000/fpslimit)Thread.sleep((int)((float)(1000000000/fpslimit-(int)dt)/1000000f));
							dt=System.nanoTime()-ns;
							ns=System.nanoTime();
							if(map!=null)map.lock=false;

						}
						catch(Throwable e)
						{
							//System.exit(0);。
							toast(e);
							break;
						}
					}
					rb=null;
				}}).start();
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					Bitmap banner=null;
					try
					{
						Thread.sleep(300);
						banner=VECfile.createBitmap(ctx,"banner",sv.getHeight(),2*sv.getHeight()/9);
					}
					catch (Exception e)
					{}
					Matrix m=new Matrix();
					m.postTranslate(-banner.getWidth()/2,-banner.getHeight()/2);
					m.postRotate(270);
					m.postTranslate(banner.getHeight()/2,banner.getWidth()/2);
					m.postTranslate(16*sv.getHeight()/9,0);
					Matrix m2=new Matrix();
					int lres=0;
					Paint p=new Paint();
					while(run)
					{
						try
						{
							if(pause)
							{
								Thread.sleep(20);
								continue;
							}
							Canvas cs=hd.lockCanvas();
							lock=curdraw-1;
							if(lock<0)lock=bmpc.length-1;
							int w=lock;
							if(bmpc[w]!=null)
								if(sv.getHeight()==bmpc[w].getHeight())cs.drawBitmap(bmpc[w],0,0,p);
								else
								{
									if(lres!=bmpc[w].getHeight())
									{
										m2.reset();
										m2.postScale((float)sv.getWidth()/bmpc[0].getWidth(),(float)sv.getHeight()/bmpc[0].getHeight());
										lres=bmpc[w].getHeight();
									}
									cs.drawBitmap(bmpc[w],m2,p);
								}
							cs.drawBitmap(banner,m,p);
							if(cs!=null)hd.unlockCanvasAndPost(cs);
							lock=-1;
							Thread.sleep(0,1000);
						}
						catch (Exception e)
						{}
					}
				}
			}).start();
//计算
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					long dt=0,ns=System.nanoTime();
					while(run)
						try
						{
							if(pause||curui!=4||map==null)
							{
								Thread.sleep(20);
								ns=System.nanoTime();
								continue;
							}
							if(map.lives<=0)
							{

								Ui u=new Ui("blackcover",0,0,1600,900).alphaFrom(0,2000);
								try
								{
									Thread.sleep(2000);
								}
								catch (InterruptedException e)
								{}
								uiSelLevel();
								ui.remove(u);
								pscore+=map.score;
								pmoney+=map.money;
								pbugs+=map.tobugs;
								saveData();
								map=null;

								continue;
							}
							else if((map.curwaveindex==map.waves.size())&&(map.bugs.size()==0))
							{
								Ui u=new Ui("blackcover",0,0,1600,900).alphaFrom(0,2000);
								try
								{
									Thread.sleep(2000);
								}
								catch (InterruptedException e)
								{}
								uiSelLevel();
								ui.remove(u);
								pscore+=map.score;
								pmoney+=map.money;
								pbugs+=map.tobugs;
								if(nowplevel==levelunlock)levelunlock++;
								saveData();
								map=null;


								continue;
							}
							float dty=dt*gamespeed/1000000000f;
							map.setUpBugs(dty);
							for(Bug b:map.bugs)b.compute(dty);
							for(Tower t:map.towers)t.compute(dty);
							for(Bullet t:map.bullets)t.compute(dty);
							dt=System.nanoTime()-ns;
							//if(dt<16666666)Thread.sleep((int)((float)(16666666-(int)dt)/1000000f));
							dt=System.nanoTime()-ns;
							ns=System.nanoTime();
						}
						catch(Throwable e)
						{
							//toast(e);
							continue;
						}
				}
			}).start();
			//绘图
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					Paint p=new Paint();
					p.setColor(0xff000000);
					while(run)
						try
						{
							if(pause||curui!=4||map==null)
							{
								Thread.sleep(20);
								continue;
							}
							if(map.lock)continue;
							map.which=!map.which;
							if(map.mapcanvas==null)continue;
							Canvas c=map.mapcanvas[map.which?0:1];
							if(c==null)continue;
							c.drawBitmap(map.background,0,0,p);
							for(int y=0;y<map.size;y++)
							{
								for(Bug b:map.bugs)if(Math.floor(b.y)==y)b.onDraw(c);
								for(Tower b:map.towers)if(Math.floor(b.y)==y)b.onDraw(c);
								for(int x=0;x<map.size;x++)
								{
									int id=map.map[x][y];
									if(id!=0)
									{
										Bitmap b=map.tiles[id];

										c.drawBitmap(b,x*map.tilew-(b.getWidth()-map.tilew)/2,y*map.tilew-b.getHeight()+map.tilew,p);
									}
								}
								for(Bullet b:map.bullets)if(Math.floor(b.y)==y)b.onDraw(c);
							}
							for(Map.AstarPoint al:map.wpwaypoint.get(0)){
									Bitmap b=map.tiles[5];

									c.drawBitmap(b,al.x*map.tilew-(b.getWidth()-map.tilew)/2,al.y*map.tilew-b.getHeight()+map.tilew,p);
									
							}
						}
						catch(Exception e)
						{
							//toast(e);
							continue;
						}
				}
			}).start();
//声音
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						AudioTrack t=new AudioTrack(
						AudioManager.STREAM_MUSIC,44100,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT,
						AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT)*10,
						AudioTrack.MODE_STREAM);
						t.play();
						String s="#BGM\n#yzrilyzr\nBPM:170\n#PWM:A width rise fall\nPWM:1 0.45 0.025 0.025\nPWM:0.8 0.45 0.025 0.025\nPWM:0.7 0 0.5 0.5\nPWM:0.7 -1 0 0.05\nPITCH:0\nPITCH:-24\nPITCH:-12\nPITCH:0\n\nPART:\n3 0 1._* 7_* 1._ 6 0 0 4_ 3_ 2 0 2_* 5_* 2_ \n3 0 0 1_ .7_ .6_ 0_ .7_ 1_ 2_ 0__ 3_ 0__ 4_ \n3_ 0_ 5_ 0_ 3_ 0__ 5_ 0__ 6_ 3_ 0_ 7_ 1._ 1._ 0_ 7_ 5_ 6_ 0_ 0-- \n\nEND\n\nPART:\n0_* 3._* 6._* 0_ 2._* 5._* 0_ 1._* 4._* 0_* 0 \n0_* 7_* 3._ 0_* 4._* 7._ 1.._* 7._* 6._* 0_* 0 \n0_ 3._ 6._ 3._ 0_ 2._ 5._ 2._ 0_ 1._ 4._ 1._ 3.* 3._ 0< 0__< 7_ 0_* \n0_ 7_ 3._ 7_ 0_ 2._ 5._ 2._ 0_ 3._ 6._ 3._ 6.-\nEND\n\nPART:\n1- .7- .6-- .5 .4- .7- 1-- .6 \n1- .7- .6- .5- .4- .7- 1- .6- \nEND\n\nPART:\nx x x y x x x y x x x y x x x y x x x y x x x y x x x y x x x y \nEND";
						int[] buff2=Sound.parse(s);
						byte[] buff=Sound.mono_PCM_16Bit(buff2);
						while(run)
							try
							{
								if(pause)Thread.sleep(1);
								t.write(buff,0,buff.length);
								t.flush();
							}
							catch(Throwable e)
							{}
						t.stop();
					}
					catch(final Throwable e)
					{
						new Handler(ctx.getMainLooper()).post(new Runnable(){
							@Override
							public void run()
							{
								Toast.makeText(MainActivity.this,e.toString(),0).show();
							}
						});

					}
				}
			});//.start();
		}
	}
	void saveData()
	{
		ctx.getSharedPreferences("data",MODE_PRIVATE).edit()
		.putInt("resolution",resolution)
		.putInt("fpslimit",fpslimit)
		.putInt("musicv",musicv)
		.putInt("musiceffv",musiceffv)
		.putBoolean("backgrun",backgrun)
		.putBoolean("showfps",showfps)
		.putBoolean("showgrid",showgrid)
		.putInt("plevel",plevel)
		.putInt("exp",exp)
		.putInt("pscore",pscore)
		.putInt("pbugs",pbugs)
		.putInt("pmoney",pmoney)
		.putInt("levelunlock",levelunlock)
		.putInt("unlocktower",unlocktower)
		.putInt("unlockulevel",unlockulevel)
		.commit();
	}
	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{

	}
	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
	}
	public static void toast(final Throwable e)
	{
		new Handler(ctx.getMainLooper()).post(new Runnable(){
			@Override
			public void run()
			{
				ByteArrayOutputStream b=new ByteArrayOutputStream();
				PrintStream p=new PrintStream(b);
				e.printStackTrace(p);
				new AlertDialog.Builder(ctx)
				.setTitle("错误")
				.setMessage(b.toString())
				.show();
			}
		});
	}
	public static void toast(final String s)
	{
		new Handler(ctx.getMainLooper()).post(new Runnable(){
			@Override
			public void run()
			{
				Toast.makeText(ctx,s,1).show();
			}
		});
	}
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		run=false;
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		if(!backgrun)pause=true;
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		pause=false;
	}
	static void upui(Ui... uis)
	{
		for(Ui u:uis)
		{
			ui.remove(u);
			ui.add(u);
		}
	}
	static void shadowIn(Ui u){
		shadowCoverIndex=ui.indexOf(u);
		shadowCoverTime=0;
		shadowCoverMode=1;
		shadowLayers++;
	}
	static void shadowOut(){
		shadowCoverTime=300;
		shadowCoverMode=-1;
		shadowLayers--;
	}
	static void show(Ui... uis)
	{
		for(Ui u:uis)u.visible=true;
	}
	static void showTA(float x,float y,float w,float h,float a,float m,Ui... uis)
	{
		for(Ui u:uis)u.tScFrom(x,y,w,h,m).alphaFrom(a,m);
	}
	static void dismissTA(float x,float y,float w,float h,float a,float m,Ui... uis)
	{
		for(Ui u:uis)u.tScTo(x,y,w,h,m).alphaTo(a,m);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0&&curui!=0)
		{
			if(!ui.contains(uiExit))
			{
				int cx=800,cy=450;
				uiExit=new UiGroup(
				new Ui("exitdialog",cx-250,cy-150,500,300,false),
				new Ui("buttoncancel",cx-190,cy+20,150,90,false){
					@Override
					public void onClick(MotionEvent e)
					{
						parent.alphaTo(0,300);
						parent.visible=false;
						shadowOut();
					}
				},
				new Ui("buttonok",cx+40,cy+20,150,90){
					@Override
					public void onClick(MotionEvent e)
					{
						finish();
						System.exit(0);
					}
				});
			}
			else uiExit.upui();
			if(!uiExit.visible){
				uiExit.visible=true;
				uiExit.alphaFrom(0,300);
			shadowIn(uiExit);
			}
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}
	void hideAllUi()
	{
		for(Ui ui:ui)
			if(ui.visible&&!ui.isAnim())//ui.visible=false;
				ui.alphaTo(0,500).tScTo(800,450,0,0,500);
	}
	void load()
	{
		hideAllUi();
		curui=0;
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					loadcode=new Ui("loadcode",0,0,1600,900);
					Thread.sleep(500);
					new Ui("bugs/0",1000,500,250,250).alphaFrom(0,100).tScFrom(1125,625,0,0,100);
					Thread.sleep(500);
					Ui u=new Ui("loadpp",900,400,400,400).setVisable(false);
					new Ui("loadp",900,400,700,700).tScFrom(1600,900,0,0,50);
					Thread.sleep(50);
					u.tScFrom(1100,600,0,0,10).alphaFrom(0,10);
					Thread.sleep(200);
					Random r=new Random();
					for(int i=0;i<20;i++)
					
					{
						int x=r.nextInt(1600),y=r.nextInt(900);
						Ui o=new Ui("bugs/"+i,x,y,250,250,false).alphaFrom(0,20).tScFrom(x+125,y+125,0,0,20);
						ui.add(3,o);
						Thread.sleep(50);
					}
					Thread.sleep(200);
					ui.clear();
					Ui g=new Ui("mainmenuyzr",350,0,900,975).alphaFrom(0,500);
					Thread.sleep(500);
					g.tScTo(100,250,600,650,500);
					Thread.sleep(500);
					ui.clear();
					mainmenu();
				}
				catch (Exception e)
				{
					//toast(e);
				}
			}
		}).start();
	}
	void mainmenu()
	{
		hideAllUi();
		curui=1;
		if(!ui.contains(mainmenubuttback))
		{
			mainmenubuttback=new Ui("mainmenubuttback",975,350,400,500)
			.tScFrom(975,400,400,500,250)
			.alphaFrom(0,500);
			mainmenutitle=new Ui("mainmenutitle",450,0,700,300)
			.tScFrom(400,0,800,300,250)
			.alphaFrom(0,500);
			mainmenustart=new Ui(null,1010,380,330,100){
				@Override
				public void onClick(MotionEvent e)
				{
					uiSelLevel();
				}
			};
			mainmenusettings=new Ui(null,1025,700,175,100){
				@Override
				public void onClick(MotionEvent e)
				{
					uisettings();
				}
			};
			mainmenuabout=new Ui(null,1175,700,175,100){
				@Override
				public void onClick(MotionEvent e)
				{
					uiabout();
				}
			};
			mainmenuyzr=new Ui("mainmenuyzr",100,250,600,650){
				String[] uh=null;
				Ui tip=null;
				public Ui init()
				{
					StringBuilder sb=new StringBuilder();
					try
					{
						String g=null;
						BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("tips.txt")));
						while((g=br.readLine())!=null)sb.append(g).append("\n");
						br.close();
					}
					catch(Throwable pe)
					{
						toast(pe);
					}
					uh=sb.toString().split("\n");	
					return this;
				}
				@Override
				public void onClick(MotionEvent e)
				{
					final String[] d=uh[new Random().nextInt(uh.length)].split("\\\\n");
					if(tip==null)
					{
						tip=new Ui("mainmenutip",100,50,350,300){
							@Override public void onDraw(Canvas c)
							{
								super.onDraw(c);
								if(!isAnim()&&visible)
								{
									p.setColor(0xff000000);
									p.setTextSize(p(28));
									for(int i=0;i<d.length;i++)
										c.drawText(d[i],x+p(25),y+(i+1)*p(50),p);
								}
							}
						};
						ui.remove(tip);
						ui.add(ui.indexOf(mainmenuyzr)+1,tip);
						tip.tScFrom(350,300,50,50,200)
						.alphaFrom(0,200);
						new Thread(new Runnable(){
							@Override
							public void run()
							{
								try
								{
									Thread.sleep(2000);
									if(tip.visible)tip.tScTo(350,300,50,50,200);
									Thread.sleep(200);
									ui.remove(tip);
									tip=null;
								}
								catch (InterruptedException e)
								{}
							}
						}).start();
					}
				}
			}.init();
		}
		else
		{
			mainmenubuttback.tScFrom(975,400,400,500,250).alphaFrom(0,500);
			mainmenutitle.tScFrom(400,0,800,300,250).alphaFrom(0,500);
			mainmenuyzr.alphaFrom(0,500);
			mainmenustart.tScFrom(975,400,400,500,250).alphaFrom(0,500);
			mainmenuabout.tScFrom(975,400,400,500,250).alphaFrom(0,500);
			mainmenusettings.tScFrom(975,400,400,500,250).alphaFrom(0,500);
		}
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				int lres=resolution;
				while(curui==1)
				{
					try
					{
						Ui t=new Ui("mainmenuw",625,350,100,100).tScFrom(600,350,0,0,1000).alphaTo(0,1000);
						ui.remove(t);
						ui.add(ui.indexOf(mainmenuyzr)+1,t);
						int i=0;
						while(i++<2000&&resolution==lres&&curui==1)Thread.sleep(1);
						if(resolution!=lres)break;
						ui.remove(t);
					}
					catch (Exception e)
					{
						break;
					}
				}
			}
		}).start();
	}
	void uiSelLevel()
	{
		hideAllUi();
		curui=2;
		if(!ui.contains(uiLevelSelect))
		{
			Ui[] uuu=new Ui[20];
			try
			{
				final Bitmap lo=VECfile.createBitmap(this,"lock",Shape.pi(100),Shape.pi(100));
				final Bitmap pl=VECfile.createBitmap(this,"play",Shape.pi(100),Shape.pi(100));
				for(int u=0;u<4;u++)
					for(int i=0;i<5;i++)
					{
						final int yt=i;
						uuu[u*5+i]=new Ui("levelselectent",770+i*100,u*100,100,100,false){
							@Override public void onDraw(Canvas c)
							{
								if(!isAnim()&&visible)
								{
									c.drawLine(x,y+h,x+w,y+h,p);
									p.setTextSize(p(50));
									p.setColor(0xffffffff);
									if(levelunlock<yt)c.drawBitmap(lo,x+p(250),y+p(50),p);
									else c.drawBitmap(pl,x+p(250),y+p(50),p);
									c.drawText(String.format("关卡 %d",yt+1),x+p(50),y+p(100),p);
								}
							}
							@Override public void onClick(MotionEvent e)
							{
								try
								{
									if(levelunlock>=yt)
									{
										RectF re=new RectF(x+p(250),y+p(50),x+p(350),y+p(150));
										if(re.contains(e.getX()-parent.x,e.getY()-parent.y)){
											uiGameMain(true,"maps/map"+yt);
											nowplevel=yt;
										}
										else{
											Map tmap=Map.loadMap(getAssets().open("maps/map"+yt));
											tmap.loadTiles(2f/3f);
											Bitmap mmp=VECfile.createBitmap(ctx,"maps/map"+yt,pi(600),pi(600));
											Canvas c=new Canvas(mmp);
											for(int y=0;y<tmap.size;y++)
											{
												for(int x=0;x<tmap.size;x++)
												{
													int id=tmap.map[x][y];
													if(id!=0)
													{
														Bitmap b=tmap.tiles[id];

														c.drawBitmap(b,x*tmap.tilew-(b.getWidth()-tmap.tilew)/2,y*tmap.tilew-b.getHeight()+tmap.tilew,p);
													}
												}
											}
											uiLevelSelect.uis.get(3).b=mmp;
											uiLevelSelect.uis.get(3).alphaFrom(0,500);
										}
									}
									else{
										uiLevelSelect.uis.get(3).b=VECfile.createBitmap(MainActivity.ctx,"levelselectmap",pi(600),pi(600));
										uiLevelSelect.uis.get(3).alphaFrom(0,500);
									}
								}
								catch (Exception ep)
								{}
							}
						};
					}
					}
			catch(Throwable e)
			{
				toast(e);
			}
			uiLevelSelect=new UiGroup(new Ui("levelselectmyjb",775,760,400,114){
				@Override public void onClick(MotionEvent e)
				{

				}
			}
			,buttonmainmenu=new Ui("buttonmainmenu",1175,760,400,114){
				@Override public void onClick(MotionEvent e)
				{
					mainmenu();
					map=null;
				}
			}
			,new Ui("levelselectlist",750,0,800,750)
			,new Ui("levelselectmap",75,50,600,600)
			,new Ui("levelselectplayer",50,675,650,200){
				RectF rf=new RectF();
				@Override public void onDraw(Canvas c)
				{
					super.onDraw(c);
					if(!isAnim()&&visible)
					{
						p.setColor(0xffffffff);
						p.setStyle(Paint.Style.FILL);
						p.setTextSize(p(30));
						p.setStrokeWidth(p(4));
						p.setTextAlign(Paint.Align.LEFT);
						c.drawText(String.format("等级:%d",plevel),x+p(35),y+p(78),p);
						p.setTextAlign(Paint.Align.CENTER);
						c.drawText(String.format("累计分:%d",pscore),x+w/6,y+p(150),p);
						c.drawText(String.format("消灭数:%d",pbugs),x+w/2,y+p(150),p);
						c.drawText(String.format("累计钱:%d",pmoney),x+w*5/6,y+p(150),p);
						rf.set(x+p(180),y+p(50),x+p(600),y+p(80));
						p.setStyle(Paint.Style.STROKE);
						c.drawRoundRect(rf,p(3),p(3),p);
						p.setStyle(Paint.Style.FILL);
						rf.set(x+p(180),y+p(50),x+p(180)+p(420)*exp/(float)(200f*Math.pow(1.25,plevel)),y+p(80));
						c.drawRoundRect(rf,p(3),p(3),p);
					}
				}
			}
			,new UiGroup(uuu));
			

		}
			uiLevelSelect.alphaFrom(0,200).tScFrom(
			800,900,400,114,200,
			1200,900,400,114,200,
			750,-900,800,750,200,
			-600,50,600,600,200,
			-650,1020,650,200,200,
			750,-900,800,750,200
			);
		

	}
	void uiMyJb()
	{
		hideAllUi();
		curui=3;
	}
	void uiGameMain(boolean isAsset,String path)
	{
		hideAllUi();
		curui=4;
		try
		{
			final Bitmap[]icos=new Bitmap[10];
			final Bitmap[] bcos=new Bitmap[20];
			if(icos[0]==null)
			{
				for(int i=0;i<icos.length;i++)
					icos[i]=VECfile.createBitmap(ctx,"towers/"+i,Shape.pi(100),Shape.pi(100));
				for(int i=0;i<bcos.length;i++)
					bcos[i]=VECfile.createBitmap(ctx,"bugs/"+i,Shape.pi(80),Shape.pi(80));
			}
			if(!ui.contains(gamerightmenu))
			{
				gamerightmenu=new Ui("gamerightmenu",1100,0,500,900){
					@Override public void onDraw(Canvas c)
					{
						super.onDraw(c);
						if(!visible)return;
						p.setColor(0xffffffff);
						if(map==null)return;
						int idn=ui.indexOf(this);

						try
						{
							if(map.selectedTower!=null)
							{
								if(map.towers.contains(map.selectedTower))
								{
									ui.get(idn+1).setVisable(true);
									ui.get(idn+2).setVisable(true);
								}
								else
								{
									ui.get(idn+1).setVisable(false);
									ui.get(idn+2).setVisable(false);
								}
								p.setTextSize(p(30));
								c.drawBitmap(icos[map.selectedTower.id],x+p(12),y+p(25),p);
								c.drawText(String.format("伤害:%.1f",(float)(map.selectedTower.dmg*Math.pow(1.1,map.selectedTower.level))),x+p(125),y+p(35),p);
								c.drawText(String.format("攻速:%.1f",(float)(1f/(map.selectedTower.dtime*Math.pow(1.1,-map.selectedTower.level)))),x+p(125),y+p(85),p);
								c.drawText(String.format("范围:%.1f",(float)(map.selectedTower.r*Math.pow(1.1,map.selectedTower.level))),x+p(125),y+p(135),p);
							}
						}
						catch(Throwable e)
						{}
						if(map.nextwave!=null)
						{
							p.setTextSize(p(30));
							c.drawBitmap(bcos[map.nextwave.id],x+p(25),y+p(725),p);
							c.drawText(String.format("x%d",map.nextwave.c),x+p(135),p(760),p);
							c.drawText(String.format("%d秒后",(int)map.nextwave.sec+1),x+p(135),p(790),p);
						}
						for(int i=0;i<10;i++)ui.get(i+idn+3).visible=i<unlocktower;
						p.setTextSize(p(70));
						p.setTextAlign(Paint.Align.CENTER);
						c.drawText(String.format("%d/%d",map.curwaveindex,map.waves.size()),x+p(375),p(790),p);
						p.setTextAlign(Paint.Align.LEFT);


					}
				};
				new Ui("gameupgrade",1400,0,200,75){
					@Override public void onDraw(Canvas c)
					{
						super.onDraw(c);
						if(map==null||!visible)return;
						if(map.selectedTower!=null)
						{
							//visible=true;
							p.setColor(0xffffffff);
							p.setTextSize(p(35));
							p.setTextAlign(Paint.Align.CENTER);
							c.drawText(map.selectedTower.level<unlockulevel?String.format("升级:%d",(int)(map.selectedTower.money*Math.pow(1.1,map.selectedTower.level+1)-map.selectedTower.money)):"(最高等级)",x+p(100),y+p(50),p);
						}
						else visible=false;
					}
					@Override public void onClick(MotionEvent e)
					{
						int m=(int)(map.selectedTower.money*Math.pow(1.1,map.selectedTower.level+1)-map.selectedTower.money);
						if(map.money-m>=0&&map.selectedTower.level<unlockulevel)
						{
							map.selectedTower.level++;
							map.money-=m;
						}
					}
				}.setVisable(false);
				new Ui("gamesell",1400,75,200,75){
					@Override public void onDraw(Canvas c)
					{
						super.onDraw(c);
						if(map==null||!visible)return;
						if(map.selectedTower!=null)
						{
							//visible=true;
							p.setColor(0xffffffff);

							p.setTextSize(p(35));
							p.setTextAlign(Paint.Align.CENTER);
							c.drawText(String.format("出售:%d",(int)(map.selectedTower.money*Math.pow(1.1,map.selectedTower.level)*0.5)),x+p(100),y+p(50),p);
						}
						else visible=false;
					}
					@Override public void onClick(MotionEvent e)
					{
						if(map.findWayPoint()){
						int m=(int)(map.selectedTower.money*Math.pow(1.1,map.selectedTower.level)*0.5);
						map.money+=m;
						map.towers.remove(map.selectedTower);
						map.selectedTower=null;
						}
					}
				}.setVisable(false);
				for(int i=0;i<10;i++)
				{
					final int yy=i;
					Ui o=new Ui("towers/"+i,1100+i%2*250,150+(int)(i/2)*100,100,100){
						Tower d;
						@Override public void onClick(MotionEvent e)
						{
							map.selectedTower=new Tower(yy,-1,-1);
						}
						@Override public void onDown(MotionEvent e)
						{
							map.selectedTower=new Tower(yy,-1,-1);
						}
						@Override public void onDraw(Canvas c)
						{
							super.onDraw(c);
							if(!visible)return;
							if(d==null&&map!=null&&map.tilew!=0)d=new Tower(yy,-1,-1);
							p.setTextSize(p(50));
							p.setColor(0xffffffff);
							if(d!=null)c.drawText(Integer.toString((int)d.money),x+p(125),y+p(65),p);
						}
					};
					o.w=Shape.p(250);
				}
				gamepause=new Ui("gamepause","gamecontinue",1390,650,70,70){
					float gs=gamespeed;
					@Override public void onClick(MotionEvent e)
					{
						if(toggle)
						{
							gs=gamespeed;
							gamespeed=0;
						}
						else gamespeed=gs;
						toggle();
					}
				};
				gamex2=new Ui("gamex1","gamex2",1460,650,70,70){
					@Override public void onClick(MotionEvent e)
					{
						if(toggle)gamespeed=2;
						else gamespeed=1;
						gamepause.toggle=true;
						toggle();
					}
				};
				gamesetting=new Ui("gamesetting",1530,650,70,70){
					@Override public void onClick(MotionEvent e)
					{
						uisettings();
						gamepause.toggle=false;
						gamespeed=0;
					}
				};
				gamesendnow=new Ui("gamesendnowavail","gamesendnowunav",1125,805,450,90){
					@Override public void onClick(MotionEvent e)
					{
						if(toggle)map.sendnow();
						toggle=false;
					}
					@Override public void onDraw(Canvas c)
					{
						super.onDraw(c);
						if(map!=null&&map.sendnowcd>0)toggle=false;
						else toggle=true;
					}
				};
			}
			show(gamerightmenu,gamepause,gamex2,gamesetting,gamesendnow);
			if(isAsset)map=Map.loadMap(getAssets().open(path));
			else map=Map.loadMap(new FileInputStream(path));
			map.loadTiles(1);
			gamespeed=1;
			gamex2.toggle=true;
			gamepause.toggle=true;
			if(!map.findWayPoint())toast("地图载入错误:无法寻找路点");
		}
		catch (Exception e)
		{
			toast(e);
		}
	}
	void uisettings()
	{
		final int pres=resolution;
		if(!ui.contains(uisetting))
		{
			uisetshadow=new Ui("shadowcover",0,0,1600,900).alphaFrom(0,200);

			uisetting=new Ui("uisetting",350,50,900,800)
			.tScFrom(350,-800,900,800,200)
			.alphaFrom(50,200);
			uisettclose=new Ui("uisettingclose",1100,100,80,80){
				@Override
				public void onClick(MotionEvent e)
				{
					uisetmv.tScTo(650,-800,550,50,200).alphaTo(50,200);
					uisetmfv.tScTo(650,-800,550,50,200).alphaTo(50,200);
					uisetfps.tScTo(650,-800,550,50,200).alphaTo(50,200);
					uisetres.tScTo(650,-800,550,50,200).alphaTo(50,200);
					uishowfps.tScTo(1050,-800,100,50,200).alphaTo(50,200);
					uishowgrid.tScTo(1050,-800,100,50,200).alphaTo(50,200);
					uibackrun.tScTo(1050,-800,100,50,200).alphaTo(50,200);
					uisetting.tScTo(350,-800,900,800,200).alphaTo(50,200);
					uisettclose.tScTo(1100,-800,80,80,200).alphaTo(50,200);
					uisetshadow.alphaTo(50,200);
					if(curui!=1)buttonmainmenu.alphaTo(0,200).tScTo(1200,900,400,114,200);
					if(pres!=resolution&&curui==1)
					{
						Shape.scale=sv.getHeight()*((float)resolution/100f)/900f;
						//sh.clear();
						ui.clear();
						mainmenubug.clear();
						for(int i=0;i<bmpc.length;i++)
						{
							bmpc[i]=Bitmap.createBitmap(Shape.pi(1600),Shape.pi(900),Bitmap.Config.ARGB_8888);
							cvsc[i]=new Canvas(bmpc[i]);
						}
						curui=-1;
						try
						{
							Thread.sleep(300);
						}
						catch (InterruptedException j)
						{}
						mainmenu();
					}

					saveData();
				}
			}.tScFrom(1100,-800,80,80,200).alphaFrom(0,200);
			uisetmv=(SeekBar) new SeekBar(650,218,550,50,musicv,100){
				@Override public void onMove(MotionEvent e)
				{
					super.onMove(e);
					musicv=pro;
				}
			}.tScFrom(650,-800,550,50,200)
			.alphaFrom(50,200);
			uisetmfv=(SeekBar) new SeekBar(650,218+81,550,50,musiceffv,100){
				@Override public void onMove(MotionEvent e)
				{
					super.onMove(e);
					musiceffv=pro;
				}
			}.tScFrom(650,-800,550,50,200)
			.alphaFrom(50,200);
			uisetfps=(SeekBar) new SeekBar(650,218+81*2,550,50,fpslimit-15,105){
				@Override public void onMove(MotionEvent e)
				{
					super.onMove(e);
					fpslimit=pro+15;
				}
			}.tScFrom(650,-800,550,50,200)
			.alphaFrom(50,200);
			uisetres=(SeekBar) new SeekBar(650,218+81*3,550,50,resolution-20,80){
				@Override public void onMove(MotionEvent e)
				{
					super.onMove(e);
					resolution=pro+20;
				}
			}.tScFrom(650,-800,550,50,200)
			.alphaFrom(50,200);
			uibackrun=(Switch) new Switch(1050,218+81*4,100,50,backgrun){
				@Override public void onClick(MotionEvent e)
				{
					super.onClick(e);
					backgrun=isOn;
				}
			}.
			tScFrom(1050,-800,100,50,200)
			.alphaFrom(50,200);
			uishowfps=(Switch) new Switch(1050,218+81*5,100,50,showfps){
				@Override public void onClick(MotionEvent e)
				{
					super.onClick(e);
					showfps=isOn;
				}
			}.
			tScFrom(1050,-800,100,50,200)
			.alphaFrom(50,200);
			uishowgrid=(Switch) new Switch(1050,218+81*6,100,50,showgrid){
				@Override public void onClick(MotionEvent e)
				{
					super.onClick(e);
					showgrid=isOn;
				}
			}.
			tScFrom(1050,-800,100,50,200)
			.alphaFrom(50,200);
		}
		else
		{
			upui(uisetshadow,uisetting,uisettclose,uisetmv,uisetmfv,uisetfps,uisetres,uishowfps,uishowgrid,uibackrun);
			uisetmv.tScFrom(650,-800,550,50,200).alphaFrom(50,200);
			uisetmfv.tScFrom(650,-800,550,50,200).alphaFrom(50,200);
			uisetfps.tScFrom(650,-800,550,50,200).alphaFrom(50,200);
			uisetres.tScFrom(650,-800,550,50,200).alphaFrom(50,200);
			uishowfps.tScFrom(1050,-800,100,50,200).alphaFrom(50,200);
			uishowgrid.tScFrom(1050,-800,100,50,200).alphaFrom(50,200);
			uibackrun.tScFrom(1050,-800,100,50,200).alphaFrom(50,200);
			uisetting.tScFrom(350,-800,900,800,200).alphaFrom(50,200);
			uisettclose.tScFrom(1100,-800,80,80,200).alphaFrom(50,200);
			uisetshadow.alphaFrom(50,200);
		}
		uisetmv.pro=musicv;
		uisetmfv.pro=musiceffv;
		uisetfps.pro=fpslimit-15;
		uisetres.pro=resolution-20;
		uishowfps.isOn=showfps;
		uibackrun.isOn=backgrun;
		uishowgrid.isOn=showgrid;
		if(curui!=1)
		{
			upui(buttonmainmenu);
			buttonmainmenu.alphaFrom(0,200).tScFrom(1200,900,400,114,200);
		}
	}

	void uiabout()
	{
		if(!ui.contains(uiAbout))
		{
			uiAbout=new UiGroup(
			new Ui("uiabout",400,100,800,700,false),
			new Ui("uiaboutok",725,670,150,90,false){
				@Override
				public void onClick(MotionEvent e)
				{
					parent.alphaTo(50,200).tScTo(1175,700,175,100,200);
					shadowOut();
				}
			},
			new Ui("yzrilyzr",850,500,100,46,false),
			new Ui("bestodesign",1000,500,150,150,false));
		}
		uiAbout.tScFrom(1175,700,175,100,200)
		.alphaFrom(50,200);
		shadowIn(uiAbout);
	}
	void uiLevelUp()
	{		
		final float gs=gamespeed;
		gamespeed=0;
		if(!ui.contains(uilevelup))
		{
			final Bitmap[]icos=new Bitmap[10];
			final String[] des=new String[]{
			"原谅塔",
			"突突塔\n可以最高的速度\n突突这些Bugs\n100一下听个响",
			"果鸟塔\n向Bugs喷射鸟果\n造成滞留伤害\n(鸟果好吃\n但不要贪狼哦～)",
			"帮帮投资者\n扔出一个点燃的帮帮\n然后嘭",
			"电塔\n不能用来发电\n电起来挺疼的\n与羊教授的有的一拼",
			"黑科技塔\n此塔乃塔中之上品\n可遇不可求\nyzrilyzr:多用这个塔\n秒速通关",
			"红红塔\n最炽热的塔\n最炽热的心\n(据说是核聚变的能量)\n别跟我杠\n为什么不是红色的",
			"火帮帮投资者\n我们的小帮帮带火\n(≧◇≦)",
			"紫气东来塔\n管他紫气是哪里来的\n是四面八方来的",
			"牛泪塔\n使周围的万有引力常数\n提高10^3"};
			if(icos[0]==null)
			{
				try
				{
					for(int i=0;i<icos.length;i++)
						icos[i]=VECfile.createBitmap(ctx,"towers/"+i,Shape.pi(200),Shape.pi(200));
				}
				catch (Exception e)
				{}
			}
			uilevelup=new Ui("uilevelup",200,50,1200,800);
			uilevelupmoney=new Ui("uilevelupmoney",290,200,300,510){
				@Override public void onClick(MotionEvent e){
					if(map!=null)map.money+=500;
					dismissTA(800,900,0,0,0,200,uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup);
					gamespeed=gs;
				}
			};
			uileveluptower=new Ui("uileveluptower","uilevelupmoney",650,200,300,510){
				@Override public void onClick(MotionEvent e){
					if(map!=null&&!toggle)map.money+=500;
					else unlocktower++;
					dismissTA(800,900,0,0,0,200,uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup);
					gamespeed=gs;
				}
				@Override public void onDraw(Canvas c){
					super.onDraw(c);
					if(toggle&&!isAnim()&&visible&&unlocktower<10){
						c.drawBitmap(icos[unlocktower],p(800-100),p(330-100),p);
						String[] bb=des[unlocktower].split("\n");
						p.setTextSize(p(25));
						for(int i=0;i<bb.length;i++)c.drawText(bb[i],p(680),p(530)+i*p(25),p);
					}
				}
			};
			uileveluptoerup=new Ui("uileveluptowerup","uilevelupmoney",1010,200,300,510){
				@Override public void onClick(MotionEvent e){
					if(map!=null&&!toggle)map.money+=500;
					else unlockulevel++;
					gamespeed=gs;
					dismissTA(800,900,0,0,0,200,uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup);
				}
			};
		}
		else
		{
			upui(uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup);
		}
		uileveluptower.toggle=unlocktower<10;
		uileveluptoerup.toggle=unlockulevel<4;
		showTA(800,900,0,0,0,200,uilevelup,uilevelupmoney,uileveluptower,uileveluptoerup);
	}
}
