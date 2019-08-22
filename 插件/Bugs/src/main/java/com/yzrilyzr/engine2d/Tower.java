package com.yzrilyzr.engine2d;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.yzrilyzr.engine2d.Bug;
import com.yzrilyzr.icondesigner.VECfile;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tower extends Shape
{
	float dmg,dtime,cdtime;
	float rdmg,rtime,rdtime,brtime;
	float x,y,money,slow;
	VECfile ico;
	Bitmap bico;
	int level;
	Bug target;
	Matrix ma=new Matrix();
	CopyOnWriteArrayList<Bug> inRbugs=new CopyOnWriteArrayList<Bug>();
	//range r
	public Tower(int id,float x,float y)
	{
		this.x=x;
		this.y=y;
		try
		{
			Context c=MainActivity.ctx;
			int s=(int)MainActivity.map.tilew;
			ico=VECfile.readFileFromIs(c.getAssets().open("towers/"+id+".vec"));
			bico=VECfile.createBitmap(ico,s,s);
			dtime=1;
			r=2;
			dmg=1;
			money=10;
			if(id==0)
			{
				dmg=20f;
				dtime=0.8f;
				money=50;
				r=1.5f;

			}
			if(id==1)
			{
				dmg=8f;
				dtime=0.4f;
				money=80;
				r=1.5f;
				
			}
		}
		catch(Throwable e)
		{
			MainActivity.toast(e);
		}
	}
	public void compute(float dt)
	{
		Map map=MainActivity.map;
		float tilew=map.tilew;
		if(cdtime>0)cdtime-=dt;
		inRbugs.clear();
		for(Bug s:map.bugs)
		{
			if(!inRange(s))continue;
			inRbugs.add(s);
			if(target==null)target=s;			
		}
		if(target==null||target.hp<=0||!inRange(target)){
			target=null;
			return;
		}
		if(cdtime<=0&&target!=null)
		{
			cdtime=dtime*(float)Math.pow(1.25,-level);
			attack();
		}
	}
	public void attack()
	{
		target.hp-=dmg*Math.pow(1.25,level);
		for(Bug rg:inRbugs)rg.hp-=dmg/6*Math.pow(1.25,level);
		//if(target==s)
		{//单一目标
			//直接或范围
			//定向范围
			//~
			//map.bullets.add(new Bullet(s,this,tilew*0.7f,tilew*0.5f,brtime));
			//子弹
			//map.bullets.add(new Bullet(s,this,tilew*0.1f,tilew*2,-1));
			//远定
			//map.bullets.add(new Bullet(s,this,tilew*1.5f,tilew*0.5f,-1));
		}
	}
	public boolean inRange(Bug b)
	{
		float dx=b.x-x,dy=b.y-y;
		return dx*dx+dy*dy<r*r;
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(MainActivity.map!=null)
		{
			Map map=MainActivity.map;
			float tilew=map.tilew;
			ma.reset();
			ma.postTranslate(-bico.getWidth()/2,-bico.getHeight()/2);
			//ma.postRotate(0);
			ma.postScale(1+cdtime/dtime/7,1+cdtime/dtime/7);
			ma.postTranslate(bico.getWidth()/2,bico.getHeight()/2);
			ma.postTranslate(x*tilew,y*tilew);
			c.drawBitmap(bico,ma,p);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xff000000);
			p.setPathEffect(new DashPathEffect(new float[]{tilew/4,tilew/4},0));
			c.drawCircle(x*tilew+tilew/2,y*tilew+tilew/2,r*tilew,p);
			p.setStyle(Paint.Style.FILL);
		}
	}

	@Override
	public void onClick(MotionEvent e)
	{
		// TODO: Implement this method
		super.onClick(e);
	}

}
