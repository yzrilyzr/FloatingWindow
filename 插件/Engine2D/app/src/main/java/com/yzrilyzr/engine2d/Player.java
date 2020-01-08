package com.yzrilyzr.engine2d;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Paint;

public class Player extends Shape
{
	private float lx,ly,ltx,lty,hx,hy;
	public int tt=0;
	boolean shoot=false;
	public float power=10;
	private MotionEvent ev;
	public Player()
	{
		this.r=0;
		p.setColor(0xff66ccff);
		p.setStrokeWidth(p(21));
	}

	public boolean hurt()
	{
		if(tt>0||MainActivity.ui.get(2).visible)return false;
		hx=x;
		hy=y;
		power/=2;
		if(power<10)power=10;
		x=p(450);
		y=p(1500);
		tt=180;
		lx=x;
		ly=y;
		if(ev==null)return true;
		ltx=ev.getX();
		lty=ev.getY();
		return true;
	}

	@Override
	public void onDraw(Canvas c)
	{
		if(tt>0)
		{
			tt--;
			if(tt%5==0)p.setAlpha(32);
			else p.setAlpha(128);
		}
		else p.setAlpha(255);
		if(!MainActivity.ui.get(2).visible){
			c.drawCircle(x,y,p(40),p);
			int f=p.getColor();
			p.setColor(0xff000000);
			c.drawCircle(x,y,p(3),p);
			p.setColor(f);
		}
		if(tt>120)
		{
			p.setStyle(Paint.Style.STROKE);
			p.setAlpha((tt-120)*255/60);
			c.drawCircle(hx,hy,p(180-tt)*8,p);
			p.setStyle(Paint.Style.FILL);
		}
		t++;
		if(t%5==0&&!MainActivity.ui.get(2).visible)
		{
			//Sound.shoot();
			l.add(new PlayerBullet(x-p(20),y,p(15),p(30),270));
			l.add(new PlayerBullet(x+p(20),y,p(15),p(30),270));
			if(power>100){
				l.add(new PlayerBullet(x-p(60),y+p(20),p(15),p(30),shoot?270:255));
				l.add(new PlayerBullet(x+p(60),y+p(20),p(15),p(30),shoot?270:285));
			}
			if(power>200&&t%10==0){
				PlayerBullet b=new PlayerBullet(x-p(100),y+p(60),p(25),p(20),shoot?270:265);
				b.p.setColor(0xff20ff66);
				l.add(b);
				b=new PlayerBullet(x+p(100),y+p(60),p(25),p(20),shoot?270:275);
				b.p.setColor(0xff20ff66);
				l.add(b);
			}
		}
	}
	@Override
	public void onTouch(MotionEvent e)
	{
		this.ev=e;
		if(down(e))
		{
			lx=x;
			ly=y;
			ltx=e.getX();
			lty=e.getY();
		}
		else
		{
			x=lx+e.getX()-ltx;
			y=ly+e.getY()-lty;
		}
		if(x<0)x=0;
		if(y<0)y=0;
		if(x>p(900))x=p(900);
		if(y>p(1600))y=p(1600);
	}
}
