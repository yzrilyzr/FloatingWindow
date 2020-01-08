package com.yzrilyzr.engine2d;

import android.view.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View.OnTouchListener;
import com.yzrilyzr.icondesigner.VECfile;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener
{
	static SurfaceView sv;
	static SurfaceHolder hd;
	static boolean run=false,pause=false,lock=false,which=false;
	static Runnable rb=null;
	final static CopyOnWriteArrayList<Shape> sh=new CopyOnWriteArrayList<Shape>();
    final static CopyOnWriteArrayList<Shape> enemys=new CopyOnWriteArrayList<Shape>();
    final static CopyOnWriteArrayList<Ui> ui=new CopyOnWriteArrayList<Ui>();
	final static Player player=new Player();
	static Context ctx;
	static int cachecount=2;
	static Bitmap[] bmpc=new Bitmap[cachecount];
	static Canvas[] cvsc=new Canvas[cachecount];
	Shape tui;
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
	}
	@Override
	public boolean onTouch(View v,MotionEvent event)
	{
		if(player!=null)player.onTouch(event);
		for(Shape s:ui)
		{
			if(Shape.down(event))
				if(s.contains(event.getX(),event.getY())&&((Ui)s).visible)
				{
					tui=s;
					break;
				}
				else tui=null;
			else if(Shape.up(event)&&tui!=null&&tui.contains(event.getX(),event.getY()))
			{
				tui.onTouch(event);
				tui=null;
				break;
			}
		}
		return true;
	}
	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		run=true;
		if(rb==null)
		{
			final int[] dta=new int[]{3,3,0,0,0};

			new Thread(new Runnable(){
				@Override
				public void run()
				{
					Shape.scale=sv.getWidth()/900f;
					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{}
					rb=this;
					sh.clear();
					sh.add(player);
					long ns=System.nanoTime(),dt=0;
					Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
					p.setColor(0xffff0000);
					p.setTextSize(Shape.p(50));
					int lt=0,fps=0;
					float avgfps=0;
					Bitmap heart=null,bomb=null,lbomb=null;
					try
					{
						heart=VECfile.createBitmap(ctx,"heart",Shape.p(90),Shape.p(90));
						bomb=VECfile.createBitmap(ctx,"bomb",Shape.p(90),Shape.p(90));
						lbomb=BitmapFactory.decodeStream(getAssets().open("bomb.jpg"));
					}
					catch (Exception e)
					{}
					new Ui("launchbomb",0,Shape.p(1240),Shape.p(180),Shape.p(180)){
						@Override
						public void onTouch(MotionEvent e)
						{
							if(!ui.get(2).visible&&dta[3]<10&&dta[1]-->0)
							{
								dta[3]=240;
								player.tt=120;
							}
							if(dta[1]<0)dta[1]=0;
						}
					};
					new Ui("shoot",0,Shape.p(1060),Shape.p(180),Shape.p(180)){
						@Override
						public void onTouch(MotionEvent e)
						{
							if(!ui.get(2).visible)player.shoot=!player.shoot;
						}
					};
					new Ui("start",Shape.p(258),Shape.p(1000),Shape.p(384),Shape.p(192)){
						@Override
						public void onTouch(MotionEvent e)
						{
							visible=false;
							dta[0]=3;
							dta[1]=3;
							dta[2]=0;
							dta[4]=0;
							l.clear();
							l.add(player);
							enemys.clear();
							player.x=Shape.p(450);
							player.y=Shape.p(1500);
						}
					};
					for(int i=0;i<bmpc.length;i++)
					{
						bmpc[i]=Bitmap.createBitmap(Shape.p(900),Shape.p(1600),Bitmap.Config.ARGB_8888);
						cvsc[i]=new Canvas(bmpc[i]);
					}
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
									for(Shape s:sh)s.onDraw(c);
									if(--dta[3]>0)
									{
										Matrix m=new Matrix();
										m.postTranslate(Shape.p(900-lbomb.getWidth()),Shape.p(1600-lbomb.getHeight())-Shape.p(240-dta[3]));
										m.postScale(Shape.scale,Shape.scale);
										p.setAlpha((int)(dta[3]*0.7f));
										c.drawBitmap(lbomb,m,p);
										p.setAlpha(255);
									}
									for(Shape s:ui)s.onDraw(c);

									for(int i=0;i<dta[0];i++)
										c.drawBitmap(heart,i*heart.getWidth(),Shape.p(1600)-heart.getHeight()*2,p);
									for(int i=0;i<dta[1];i++)
										c.drawBitmap(bomb,i*bomb.getWidth(),Shape.p(1600)-bomb.getHeight(),p);

									c.drawText(String.format("Power:%.2f",player.power/100f),Shape.p(900)/2,Shape.p(1600)-p.getTextSize()*1.2f,p);
									c.drawText(String.format("Score:%d",dta[2]),Shape.p(900)/2,Shape.p(1600),p);
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
										c.drawText(String.format("FPS:%d  Shape:%d",fps,sh.size()+ui.size()),0,Shape.p(50),p);
									}
									dta[4]++;
									if(dta[4]%(100-Math.floor(player.power/100f)*10)==0&&!ui.get(2).visible)
									{
										Random r=new Random();
										Enemy e=new Enemy(r.nextInt(Shape.p(900)),r.nextInt(Shape.p(1600)/2),Shape.p(30+r.nextInt(50)));
										sh.add(e);
										enemys.add(e);
										e.health=10+r.nextInt(300);
										e.max=e.health;
										e.type=1+r.nextInt(4);
									}
									if(dta[4]%2000==0&&!ui.get(2).visible)
									{
										Random r=new Random();
										Enemy e=new Enemy(Shape.p(900)/2,r.nextInt(Shape.p(1600)/4),Shape.p(200));
										e.p.setColor(0xffff66ff);
										sh.add(e);
										enemys.add(e);
										e.t=-600;
										e.health=4000+50*player.power;
										e.max=e.health;
										e.type=5;
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
							System.exit(0);
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
							cs.drawBitmap(banner,0,Shape.p(1600),p);
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
							for(Shape s:sh)
							{
								if(s instanceof PlayerBullet)
								{
									if(s.isOutOfScr())sh.remove(s);
									for(Shape s2:enemys)
										if(s.contains(s2))
										{
											Enemy e=(Enemy)s2;
											if(!e.hurt((PlayerBullet)s,player.power))dta[2]+=10;
											else if(!e.exp)
											{
												e.exp=true;
												dta[2]+=e.max;
												if(Math.random()<0.1)sh.add(new DropItem(e,Math.random()>=0.5?0:1));
												for(int i=0,l=(int)Math.min(e.max/30,10);i<l;i++)
													sh.add(new DropItem(e,Math.random()>=0.5?3:2));
											}
											break;
										}
								}
							}
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
							for(Shape s:sh)
							{
								if(s instanceof Bullet)
								{
									if(s.isOutOfScr())sh.remove(s);
									else if(dta[0]>0&&dta[3]<=0&&s.contains(player.x,player.y))
									{
										if(player.hurt())dta[0]--;
										else sh.remove(s);
										if(dta[0]==0)
										{
											ui.get(2).visible=true;
										}
									}
									else if(dta[3]>50)
									{
										Bullet b=(Bullet)s;
										b.d=BulletSpawn.getAngle(player.x,player.y,b.x,b.y);
										b.v=Shape.p(60);
										player.tt=30;
										//if(dta[3]==1)sh.remove(s);
									}
								}
								else if(s instanceof DropItem)
								{
									if(s.isOutOfScr())sh.remove(s);
									else if(dta[0]>0&&s.contains(player.x,player.y))
									{
										DropItem d=(DropItem)s;
										d.d=BulletSpawn.getAngle(player.x,player.y,s.x,s.y);
										d.v=Shape.p(20);
										if(Math.abs(s.x-player.x)<Shape.p(15)&&Math.abs(s.y-player.y)<Shape.p(15))
										{
											sh.remove(s);
											int t=d.type;
											if(t==0)dta[1]++;
											else if(t==1)dta[0]++;
											else if(t==2)dta[2]+=15;
											else if(t==3)player.power++;
										}
									}
								}
							}
						}
						catch(Exception e)
						{}
				}
			}).start();
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
			}).start();
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
		if(keyCode==KeyEvent.KEYCODE_BACK)return true;
		return super.onKeyDown(keyCode,event);
	}
}
