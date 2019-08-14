package com.yzrilyzr.engine2d;

import android.view.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

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

	//uimainmenu=1
	private Ui mainmenubuttback,mainmenutitle,mainmenustart,mainmenucustom,mainmenututorial,mainmenubugicon,mainmenusettings,mainmenuabout;

	//uiselectlevel=2

	//uimyjb=3

	//uiganemain=4
	Map map=null;
	final static CopyOnWriteArrayList<Shape> bugs=new CopyOnWriteArrayList<Shape>();
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
		for(int i=ui.size()-1;i>=0;i--)
		{
			Ui s=ui.get(i);
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
		run=true;
		if(rb==null)
		{
			new Thread(new Runnable(){
				@Override
				public void run()
				{
					Shape.scale=sv.getWidth()/1600f;
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
						bmpc[i]=Bitmap.createBitmap(Shape.p(1600),Shape.p(900),Bitmap.Config.ARGB_8888);
						cvsc[i]=new Canvas(bmpc[i]);
					}

					//uiGameMain();
					mainmenu();
					Matrix m=new Matrix();
					while(run)
					{
						try
						{
							if(!pause)
							{
								if(!lock)
								{
									which=!which;
									Canvas c=cvsc[which?0:1];
									c.drawColor(0xff333333);
									//uimainmeuu
									if(curui==1){
										
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

											c.drawBitmap(map.background,m,p);
											float s=(float)Shape.p(900)*scale/(float)map.size;
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
												}
											c.drawLine(0,0,-deltax*scale,-deltay*scale,p);
											p.setColor(0xff00ff00);
											c.drawPoint(-deltax,-deltay,p);

										}
									}
									for(Shape s:sh)s.onDraw(c);
									for(Shape s:ui)s.onDraw(c);
									for(int i=0;i<Shape.p(1600);i+=Shape.p(50))c.drawLine(i,0,i,Shape.p(900),p);
									for(int u=0;u<Shape.p(900);u+=Shape.p(50))c.drawLine(0,u,Shape.p(1600),u,p);
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
							}
							else Thread.sleep(20);
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
						banner=VECfile.createBitmap(ctx,"banner",Shape.p(900),Shape.p(200));
					}
					catch (Exception e)
					{}
					Paint p=new Paint();
					while(run)
					{
						try
						{
							Canvas cs=hd.lockCanvas();
							lock=true;
							int w=which?1:0;
							if(bmpc[w]!=null)cs.drawBitmap(bmpc[w],0,0,p);
							lock=false;
							Matrix m=new Matrix();
							m.postRotate(270);
							//m.postTranslate(0,0);
							cs.drawBitmap(banner,m,p);
							if(cs!=null)hd.unlockCanvasAndPost(cs);
							Thread.sleep(0,1000);
						}
						catch (Exception e)
						{}
					}
				}
			}).start();
			/*
			 new Thread(new Runnable(){
			 @Override
			 public void run()
			 {
			 while(run)
			 try
			 {
			 }
			 catch(Throwable e)
			 {}
			 }
			 }).start();
			 new Thread(new Runnable(){
			 @Override
			 public void run()
			 {
			 while(run)
			 try
			 {

			 }
			 catch(Exception e)
			 {}
			 }
			 }).start();*/
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
		if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
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
				shadowcover.visible=!shadowcover.visible;
				exitdialog.visible=shadowcover.visible;
				buttonok.visible=shadowcover.visible;
				buttoncancel.visible=shadowcover.visible;
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
					Thread.sleep(1000);

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
			mainmenubuttback=new Ui("mainmenubuttback",825,350,400,500);
			mainmenutitle=new Ui("mainmenutitle",400,50,800,300);
			mainmenuabout=new Ui(null,1025,700,175,100){
				@Override
				public void onTouch(MotionEvent e){
					uiabout();
				}
			};
		}
		mainmenubuttback.visible=true;
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(1000);

				}
				catch (Exception e)
				{
					//toast(e);
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
			uiabout=new Ui("uiabout",cx-400,cy-350,800,700);
			uiaboutok=new Ui("uiaboutok",cx-75,cy+220,150,90){
				@Override
				public void onTouch(MotionEvent e)
				{
					uiabout.visible=false;
					uiaboutbesto.visible=uiabout.visible;
					uiaboutyzr.visible=uiabout.visible;
					uiaboutok.visible=uiabout.visible;
				}
			};
			uiaboutyzr=new Ui("yzrilyzr",cx+20,cy+100,200,92);
			uiaboutbesto=new Ui("bestodesign",cx+220,cy+100,100,100);
		}
		uiabout.visible=true;
		uiaboutbesto.visible=uiabout.visible;
		uiaboutyzr.visible=uiabout.visible;
		uiaboutok.visible=uiabout.visible;
	}
}
