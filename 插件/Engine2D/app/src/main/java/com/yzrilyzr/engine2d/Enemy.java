package com.yzrilyzr.engine2d;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import android.graphics.Matrix;
import android.graphics.RectF;

public class Enemy extends Shape
{
	public float health=100,max=1;
	public Integer dt=30;
	Bitmap boss;
	public int type=0;
	public boolean exp=false;
	public Enemy(float x,float y,float r){
		this.x=x;
		this.y=y;
		this.r=r;
		p.setColor(0xff66ff66);
		p.setStrokeWidth(p(21));
		try
		{
			boss=BitmapFactory.decodeStream(MainActivity.ctx.getResources().getAssets().open("boss.png"));
		}
		catch (IOException e)
		{}
	}
	public boolean hurt(PlayerBullet b,float dmg){
		if(health>0){
			l.remove(b);
			health-=dmg;
			return false;
		}
		else return true;
	}
	@Override
	public void onDraw(Canvas c)
	{
		if(health>0){
			p.setAlpha(128);
			c.drawArc(new RectF(x-r*1.1f,y-r*1.1f,x+r*1.1f,y+r*1.1f),-90,360*health/max,true,p);
			p.setAlpha(255);
			c.drawCircle(x,y,r,p);
			p.setAlpha(128);
			Matrix m=new Matrix();
			m.postScale(r*2/boss.getWidth(),r*2/boss.getHeight());
			m.postTranslate(x-r,y-r);
			c.drawBitmap(boss,m,p);
			p.setAlpha(255);
		}
		else if(--dt>0){
			p.setStyle(Paint.Style.STROKE);
			p.setAlpha((dt-30)*255/30);
			c.drawCircle(x,y,(30-dt)*r/15,p);
			p.setStyle(Paint.Style.FILL);
		}
		else {
			l.remove(this);
			MainActivity.enemys.remove(this);
		}
		t++;
		
		if(t>180){
			y-=p(5);
			if(isOutOfScr()){
				l.remove(this);
				MainActivity.enemys.remove(this);
			}
		}
		if(t==30||t%240==0){
			if(type==1)BulletSpawn.扇形(this);
			else if(type==2)BulletSpawn.螺旋(this);
			else if(type==3)BulletSpawn.随机圆(this);
			else if(type==4)BulletSpawn.随机乱射(this);
			else if(type==5)BulletSpawn.Boss(this);
		}
	}
	
}
