package com.yzrilyzr.engine2d;
import java.util.List;
import java.util.Random;

public class BulletSpawn
{
	static final List<Shape> l=MainActivity.sh;
	static final Player p=MainActivity.player;
	public static final void 扇形(final Enemy e){
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				for(int j=0;j<5;j++){
					if(e.health<=0||e.isOutOfScr())break;
					float i=3;
					float r=15;
					float da=getAngle(p.x,p.y,e.x,e.y);
					l.add(new Bullet(e.x,e.y,r,i,da-20));
					l.add(new Bullet(e.x,e.y,r,i,da-10));
					l.add(new Bullet(e.x,e.y,r,i,da));
					l.add(new Bullet(e.x,e.y,r,i,da+10));
					l.add(new Bullet(e.x,e.y,r,i,da+20));
					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();
		
	}
	public static final void 随机乱射(final Enemy e){
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Random r=new Random();
				for(int j=0;j<30;j++){
					if(e.health<=0||e.isOutOfScr())break;
					float da=getAngle(p.x,p.y,e.x,e.y);
					Bullet b=new Bullet(e.x,e.y,20,2+r.nextInt(3),da-40+r.nextInt(80));
					b.p.setColor(0xffff9900);
					l.add(b);
					try
					{
						Thread.sleep(20);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();

	}
	public static final void 螺旋(final Enemy e){
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				for(int i=0;i<360;i+=10){
					if(e.health<=0||e.isOutOfScr())break;
					l.add(new Bullet(e.x,e.y,15,4,i+getAngle(p.x,p.y,e.x,e.y)));
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();
	}
	public static final void 随机圆(final Enemy e){
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				for(int i=0;i<5;i++){
					if(e.health<=0||e.isOutOfScr())break;
					Bullet b=new Bullet(e.x,e.y,35,2,i+getAngle(p.x,p.y,e.x,e.y));
					b.p.setColor(0xffffff66);
					l.add(b);
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();
	}
	public static final void Boss(final Enemy e){
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Random r=new Random();
				for(int j=0;j<50;j++){
					if(e.health<=0||e.isOutOfScr())break;
					float da=getAngle(p.x,p.y,e.x,e.y);
					Bullet b=new Bullet(e.x,e.y,r.nextInt(50),2+r.nextInt(3),da-40+r.nextInt(80));
					b.p.setColor(0xff66ccff);
					l.add(b);
					try
					{
						Thread.sleep(20);
					}
					catch (InterruptedException e)
					{}
				}
			}
		}).start();

	}
	public static float getAngle(float x1,float y1,float cx,float cy){
		float xx=x1-cx,yy=y1-cy;
		float rr=(float)Math.sqrt(Math.pow(xx,2)+Math.pow(yy,2));
		float cos=(float)Math.acos(yy/rr);
		float an=(float)(cos*180f/Math.PI);
		if(xx>0)an=360f-an;
		an+=90f;
		an%=360f;
		return an;
	}
}
