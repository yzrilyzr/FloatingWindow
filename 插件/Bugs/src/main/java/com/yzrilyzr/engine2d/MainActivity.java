package com.yzrilyzr.engine2d;

import android.view.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import android.graphics.Typeface;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener
{
	//global
	static SurfaceView sv;
	static SurfaceHolder hd;
	static boolean run=false,pause=false,lock=false,which=false;
	static Runnable rb=null;
	final static CopyOnWriteArrayList<Shape> sh=new CopyOnWriteArrayList<Shape>();
    final static CopyOnWriteArrayList<Ui> ui=new CopyOnWriteArrayList<Ui>();
	static Context ctx;
	static int cachecount=2;
	static Bitmap[] bmpc=new Bitmap[cachecount];
	static Canvas[] cvsc=new Canvas[cachecount];
	Shape tui;
	static String mainDir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/Bugs/";
	private Ui exitdialog,buttoncancel,buttonok,shadowcover;
	private int curui=0;

	//uiload=0
	private Ui loadcode;
	//uimainmenu=1
	private Ui mainmenubuttback,mainmenutitle,mainmenustart,mainmenucustom,mainmenututorial,mainmenubugicon,mainmenusettings,mainmenuabout,mainmenuyzr;
	public ArrayList<Bug> mainmenubug=new ArrayList<Bug>();
	//uiselectlevel=2

	//uimyjb=3

	//uiganemain=4
	static Map map=null;
	final static CopyOnWriteArrayList<Bug> bugs=new CopyOnWriteArrayList<Bug>();
	final static CopyOnWriteArrayList<Tower> towers=new CopyOnWriteArrayList<Tower>();
	final static CopyOnWriteArrayList<Bullet> bullets=new CopyOnWriteArrayList<Bullet>();
	private Ui gamerightmenu;
	private float deltax=0,deltay=0,scale=1,lscale=1,lpointLen;
	private boolean moved=false;
	private float ddx,ddy;

	//uicustom=5

	//uiselectmap=6

	//uimapedit=7;

	//uibugsicon=8

	//uitutorial=9

	//uisettings

	//uiabout
	private Ui uiabout,uiaboutok,uiaboutbesto,uiaboutyzr;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		ctx=this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(sv=new SurfaceView(this));
		hd=sv.getHolder();
		sv.setOnTouchListener(this);
		hd.addCallback(this);
		File f=new File(mainDir);
		if(!f.exists())f.mkdirs();
	}
	@Override
	public boolean onTouch(View v,MotionEvent event)
	{
		event.setLocation(Shape.p(1600)*event.getX()/sv.getWidth(),Shape.p(900)*event.getY()/sv.getHeight());
		for(int i=ui.size()-1;i>=0;i--)
		{
			Ui s=ui.get(i);
			if(s.isAlpha||s.isAto||s.isFrom||s.isTo)continue;
			if(Shape.down(event))
				if(s.contains(event.getX(),event.getY())&&(s).visible)
				{
					tui=s;
					break;
				}
				else tui=null;
			else if(Shape.up(event)&&tui!=null&&tui.contains(event.getX(),event.getY()))
			{
				tui.onTouch(event);
				tui=null;
				return false;
			}
		}
		if(curui==4)
		{
			int a=event.getAction();
			try
			{
				if(event.getPointerCount()==1)
				{
					if(a==MotionEvent.ACTION_DOWN)moved=false;
					else if(a==MotionEvent.ACTION_UP)
					{
						moved=false;
						map.loadTiles(scale);
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
						deltax=limit(deltax,Shape.p(1100)-Shape.p(900f)*scale,0);
						deltay=limit(deltay,Shape.p(900)-Shape.p(900f)*scale,0);
						if(scale<1100f/900f)deltax=Shape.p(100f)-Shape.p(100f)*(scale-1)/(1100f/900f-1);
						if(scale<1)
						{
							scale=1;
							deltax=Shape.p(100);
							deltay=0;
						}
					}
				}
			}
			catch (Exception e)
			{}
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
					Shape.scale=sv.getHeight()/900f;
					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{}
					rb=this;
					sh.clear();
					long ns=System.nanoTime(),dt=0;
					Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
					p.setColor(0xffff0000);
					p.setTextSize(Shape.p(50));
					int lt=0,fps=0;
					float avgfps=0;
					for(int i=0;i<bmpc.length;i++)
					{
						bmpc[i]=Bitmap.createBitmap(Shape.pi(1600),Shape.pi(900),Bitmap.Config.ARGB_8888);
						cvsc[i]=new Canvas(bmpc[i]);
					}

					//uiGameMain();
					load();
					Matrix m=new Matrix();
					while(run)
					{
						try
						{
							if(pause)
							{
								Thread.sleep(20);
								continue;
							}
							if(lock)continue;
							which=!which;
							Canvas c=cvsc[which?0:1];
							c.drawColor(0xff333333);
							//uimainmeuu
							if(curui==1)
							{
								Random r=new Random();
								while(mainmenubug.size()<20)mainmenubug.add(new Bug(0,
									r.nextBoolean()?0:Shape.p(1600),
									r.nextBoolean()?0:Shape.p(900),
									20));
								float ro=Shape.p(50);
								for(Bug pp:mainmenubug)
								{
									pp.vx+=pp.ax;
									pp.vy+=pp.ay;
									pp.x+=pp.vx;
									pp.y+=pp.vy;
									if(pp.x<-ro)pp.x=Shape.p(1600)+ro;
									if(pp.y<-ro)pp.y=Shape.p(900)+ro;
									if(pp.x>Shape.p(1600)+ro)pp.x=-ro;
									if(pp.y>Shape.p(900)+ro)pp.y=-ro;
									if(pp.vx>pp.vel)pp.vx=pp.vel;
									if(pp.vx<-pp.vel)pp.vx=-pp.vel;
									if(pp.vy>pp.vel)pp.vy=pp.vel;
									if(pp.vy<-pp.vel)pp.vy=-pp.vel;
									pp.ax=(r.nextBoolean()?1:-1)*Shape.p(0.3f);
									pp.ay=(r.nextBoolean()?1:-1)*Shape.p(0.3f);
									pp.onDraw(c);
								}

							}
							//uigamemain
							else if(curui==4)
							{
								if(map!=null)
								{
									p.setColor(0xffff0000);
									p.setStrokeWidth(10);
									m.reset();
									m.postTranslate(deltax,deltay);
									m.postScale(scale,scale);

									c.drawBitmap(map.back,m,p);
									/*float s=(float)Shape.p(900)*scale/(float)map.size;
									 for(int i=0;i<map.map.length;i++)
									 for(int u=0;u<map.map[0].length;u++)
									 {
									 int id=map.map[i][u];
									 if(id!=0)
									 {
									 Bitmap b=map.tiles[id];
									 if(moved)
									 {
									 m.reset();
									 m.postScale(scale/lscale,scale/lscale);
									 //m.postTranslate(deltax*scale,deltay*scale);
									 m.postTranslate(+s*i-(b.getWidth()-s)/2,s*u-b.getHeight()+s);
									 c.drawBitmap(b,m,p);
									 }
									 else c.drawBitmap(b,deltax*scale+(s*i-(b.getWidth()-s)/2),deltay*scale+(s*u-b.getHeight()+s),p);
									 }
									 }*/
									c.drawLine(0,0,-deltax*scale,-deltay*scale,p);
									p.setColor(0xff00ff00);
									c.drawPoint(-deltax,-deltay,p);

								}
							}
							for(Shape s:sh)s.onDraw(c);
							for(Shape s:ui)s.onDraw(c);
							/*for(int i=0;i<1600;i+=50)
							 {
							 p.setColor(i%250==0?0xff00ff00:0xffff0000);
							 c.drawLine(Shape.p(i),0,Shape.p(i),Shape.p(900),p);
							 }
							 for(int u=0;u<900;u+=50)
							 {
							 p.setColor(u%250==0?0xff00ff00:0xffff0000);
							 c.drawLine(0,Shape.p(u),Shape.p(1600),Shape.p(u),p);
							 }*/
							if(dt!=0)
							{
								lt++;
								if(lt<=10)avgfps+=1000000000l/dt;
								else
								{
									fps=(int)(avgfps/10f);
									lt=0;
									avgfps=0;
								}
								p.setColor(0xffff0000);
								c.drawText(String.format("FPS:%d  Shape:%d dx:%f dy:%f sc:%f",fps,sh.size()+ui.size(),deltax,deltay,scale),0,Shape.p(50),p);
							}
							dt=System.nanoTime()-ns;
							if(dt<16666666)Thread.sleep((int)((float)(16666666-(int)dt)/1000000f));
							dt=System.nanoTime()-ns;
							ns=System.nanoTime();
							
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
						banner=VECfile.createBitmap(ctx,"banner",Shape.pi(900),Shape.pi(200));
					}
					catch (Exception e)
					{}
					Matrix m=new Matrix();
					m.postRotate(270);
					m.postTranslate(sv.getHeight()*16/9,sv.getHeight());
					Matrix m2=new Matrix();
					m2.postScale((float)sv.getWidth()/bmpc[0].getWidth(),(float)sv.getHeight()/bmpc[0].getHeight());
					boolean isFull=sv.getHeight()==Shape.p(900);
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
							lock=true;
							int w=which?1:0;
							if(bmpc[w]!=null)
								if(isFull)cs.drawBitmap(bmpc[w],0,0,p);
								else cs.drawBitmap(bmpc[w],m2,p);
							lock=false;
							cs.drawBitmap(banner,m,p);
							if(cs!=null)hd.unlockCanvasAndPost(cs);
							Thread.sleep(0,1000);
						}
						catch (Exception e)
						{}
					}
				}
			}).start();

			new Thread(new Runnable(){
				@Override
				public void run()
				{
					while(run)
						try
						{
							if(pause||curui!=4)
							{
								Thread.sleep(20);
								continue;
							}
							for(Bug b:bugs)
							{
								b.compute();
								for(Tower t:towers)t.compute(b);
								for(Bullet t:bullets)t.compute(b);
							}
						}
						catch(Throwable e)
						{
							toast(e);
							break;
						}
				}
			}).start();
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					Paint p=new Paint();
					p.setColor(0xff000000);
					while(run)
						try
						{
							if(pause||curui!=4)
							{
								Thread.sleep(20);
								continue;
							}
							Canvas c=map.bc;
							c.drawBitmap(map.background,0,0,p);
							for(int y=0;y<map.size;y++)
							{
								for(Bug b:bugs)if(Math.floor(b.y)==y)b.onDraw(c);
								for(Tower b:towers)if(Math.floor(b.y)==y)b.onDraw(c);
								for(int x=0;x<map.size;x++)
								{
									int id=map.map[x][y];
									if(id!=0)
									{
										Bitmap b=map.tiles[id];
										c.drawBitmap(b,x*map.tilew,y*map.tilew,p);
									}
								}
								for(Bullet b:bullets)if(Math.floor(b.y)==y)b.onDraw(c);
							}

						}
						catch(Exception e)
						{
							toast(e);
							break;
						}
				}
			}).start();
			/*
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
			 }).start();*/
		}
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
		pause=true;
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		pause=false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0&&curui!=0)
		{
			if(exitdialog==null)
			{
				int cx=800,cy=450;
				shadowcover=new Ui("shadowcover",0,0,1600,900);
				exitdialog=new Ui("exitdialog",cx-250,cy-150,500,300);
				buttoncancel=new Ui("buttoncancel",cx-190,cy+20,150,90){
					@Override
					public void onTouch(MotionEvent e)
					{
						shadowcover.visible=false;
						exitdialog.visible=shadowcover.visible;
						buttonok.visible=shadowcover.visible;
						buttoncancel.visible=shadowcover.visible;
					}
				};
				buttonok=new Ui("buttonok",cx+40,cy+20,150,90){
					@Override
					public void onTouch(MotionEvent e)
					{
						finish();
						System.exit(0);
					}
				};
			}
			else
			{
				ui.remove(shadowcover);
				ui.remove(exitdialog);
				ui.remove(buttoncancel);
				ui.remove(buttonok);
				shadowcover.visible=!shadowcover.visible;
				exitdialog.visible=shadowcover.visible;
				buttonok.visible=shadowcover.visible;
				buttoncancel.visible=shadowcover.visible;
				ui.add(shadowcover);
				ui.add(exitdialog);
				ui.add(buttoncancel);
				ui.add(buttonok);
			}
		}
		return true;
	}
	void hideAllUi()
	{
		for(Ui ui:ui)ui.visible=false;
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
					new Ui("bugs/bug",1000,500,250,250).alphaFrom(0,100).tScFrom(1125,625,0,0,100);
					Thread.sleep(500);
					Ui u=new Ui("loadpp",900,400,400,400).setVisable(false);
					new Ui("loadp",900,400,700,700).tScFrom(1600,900,0,0,50);
					Thread.sleep(50);
					u.tScFrom(1100,600,0,0,10).alphaFrom(0,10);
					Thread.sleep(200);
					Random r=new Random();
					for(int i=0;i<30;i++)
					{
						int x=r.nextInt(1600),y=r.nextInt(900);
						Ui o=new Ui("bugs/bug",x,y,250,250).alphaFrom(0,20).tScFrom(x+125,y+125,0,0,20);
						ui.remove(o);
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
		if(mainmenubuttback==null)
		{
			mainmenubuttback=new Ui("mainmenubuttback",825,350,400,500)
			.tScFrom(825,400,400,500,250)
			.alphaFrom(0,500);
			mainmenutitle=new Ui("mainmenutitle",450,0,700,300)
			.tScFrom(400,0,800,300,250)
			.alphaFrom(0,500);
			mainmenuabout=new Ui(null,1025,700,175,100){
				@Override
				public void onTouch(MotionEvent e)
				{
					uiabout();
				}
			};
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
			final String[] uh=sb.toString().split("\n");
			mainmenuyzr=new Ui("mainmenuyzr",100,250,600,650){
				@Override
				public void onTouch(MotionEvent e)
				{
					final String[] d=uh[new Random().nextInt(uh.length)].split("\\\\n");
					final Ui tip=new Ui("mainmenutip",100,50,350,300){
						@Override public void onDraw(Canvas c)
						{
							super.onDraw(c);
							if(!isFrom&&!isTo)
							{
								p.setColor(0xff000000);
								p.setTextSize(p(28));
								for(int i=0;i<d.length;i++)
									c.drawText(d[i],x+p(25),y+(i+1)*p(50),p);
							}
						}
					}.tScFrom(350,300,50,50,100)
					.alphaFrom(0,100);
					new Thread(new Runnable(){
						@Override
						public void run()
						{
							try
							{
								Thread.sleep(2000);
								tip.tScTo(350,300,50,50,100);
								Thread.sleep(100);
								ui.remove(tip);
							}
							catch (InterruptedException e)
							{}
						}
					}).start();
				}
			};
		}
		else
		{
			mainmenubuttback.tScFrom(825,400,400,500,250)
			.alphaFrom(0,500);
			mainmenutitle=new Ui("mainmenutitle",450,0,700,300)
			.tScFrom(400,0,800,300,250)
			.alphaFrom(0,500);
			mainmenuabout.visible=mainmenubuttback.visible;
		}
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				while(curui==1)
				{
					try
					{
						Ui t=new Ui("mainmenuw",625,350,100,100).tScFrom(600,350,0,0,1000).alphaTo(0,1000);
						ui.remove(t);
						ui.add(4,t);
						Thread.sleep(2000);
						ui.remove(t);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();
	}
	void uiGameMain()
	{
		hideAllUi();
		curui=4;
		try
		{
			if(gamerightmenu==null)
			{
				gamerightmenu=new Ui("gamerightmenu",1100,0,500,900);
			}
			gamerightmenu.visible=true;
			scale=1;
			lscale=1;
			deltax=0;
			deltay=0;
			map=Map.loadMap(getAssets().open("maps/map1"));
			map.loadTiles(1);
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						map.findWayPoint();
						Thread.sleep(1000);
						ArrayList<Map.AstarPoint> p=map.wpwaypoint.get(0);
						for(Map.AstarPoint c:p)
						{
							map.map[c.x][c.y]=5;

							Thread.sleep(50);

						}
					}
					catch (Exception e)
					{
						//toast(e);
					}
				}
			}).start();

		}
		catch (Exception e)
		{
			//toast(e);
		}
	}
	void uisettings()
	{}
	void uiabout()
	{
		if(uiabout==null)
		{
			int cx=800,cy=450;
			uiabout=new Ui("uiabout",cx-400,cy-350,800,700)
			.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutok=new Ui("uiaboutok",cx-75,cy+220,150,90){
				@Override
				public void onTouch(MotionEvent e)
				{

					uiabout.tScTo(1025,700,175,100,200)
					.alphaFrom(50,200);
					uiaboutok.tScTo(1025,700,175,100,200)
					.alphaFrom(50,200);
					uiaboutbesto.tScTo(1025,700,175,100,200)
					.alphaFrom(50,200);
					uiaboutyzr.tScTo(1025,700,175,100,200)
					.alphaFrom(50,200);
				}
			}.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutyzr=new Ui("yzrilyzr",850,500,100,46)
			.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutbesto=new Ui("bestodesign",1000,500,150,150)
			.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
		}
		else
		{
			uiabout.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutok.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutbesto.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
			uiaboutyzr.tScFrom(1025,700,175,100,200)
			.alphaFrom(50,200);
		}
	}
}
