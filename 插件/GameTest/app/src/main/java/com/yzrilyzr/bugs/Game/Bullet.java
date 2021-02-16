package com.yzrilyzr.bugs.Game;
import android.graphics.*;
import com.yzrilyzr.icondesigner.*;

public class Bullet extends GObj
{
	Tower t;
	Bug b;
	//VECfile icon;
	public float brtime=0;//滞留时间，>0启用 -1接触后消失
	float x,y,vx,vy,cdtime;
	//固定的bug位置
	float fbx,fby;
	private Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final float[] btimes=new float[]{-1,-1,-1,1000,400,1000,700,1000,500,500};
	public Bullet(Bug pb,Tower p/*, float r,float v,float br*/){
		b=pb;
		t=p;
		brtime=btimes[t.id];
		fbx=b.x;
		fby=b.y;
		//this.r=r;
		//brtime=br;
		//x=p.x;
		//y=p.y;
		/*float d=(float)Math.sqrt((b.x-p.x)*(b.x-p.x)+(b.y-p.y)*(b.y-p.y));
		//vx=v*(b.x-p.x)/d;
		vy=v*(b.y-p.y)/d;*/
	}
	/*public void compute(float dt){
		if(cdtime<=0)
		{
			cdtime=parent.dtime*(float)Math.pow(1.25,-parent.level);
			attack();
		}
		else cdtime-=dt;
		brtime-=dt;
		//if(brtime<=0)MainActivity.map.bullets.remove(this);
	}
	public void attack(){
		/*for(Bug s:MainActivity.map.bugs){
			if(inRange(s))s.hp-=parent.dmg*Math.pow(1.25,parent.level);
		}
	}
	/*public boolean inRange(Bug b)
	{
		return contains(b.x,b.y);
	}
	*/
	/*@Override*/
	public void onDraw(Canvas c,float tilew)
	{
		// TODO: Implement this method
		float d=tilew/2;
		if(t.id==4){
			if(b.hp<0||!b.contains(t.x,t.y,t.r))brtime=Math.min(brtime,200);
			p.setPathEffect(new DiscretePathEffect(tilew/5,tilew/5));
			p.setStrokeWidth(tilew/10f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xA07f25ff);
			p.setAlpha((int)(brtime/btimes[t.id]*180f));
			c.drawLine(t.x*tilew+d,t.y*tilew+tilew/8,b.x*tilew+d,b.y*tilew+d,p);
		}
		else if(t.id==5){
			//if(b.hp<0||!b.contains(t.x,t.y,t.r))return;
			p.setPathEffect(new DiscretePathEffect(tilew/5,tilew/5));
			p.setStrokeWidth(tilew/10f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xffff2020);
			p.setAlpha((int)(brtime/btimes[t.id]*220f));
			c.drawLine(t.x*tilew+d,t.y*tilew+tilew/8,fbx*tilew+d,fby*tilew+d,p);
		}
		else if(t.id==7){
			p.setPathEffect(new DiscretePathEffect(tilew/10,tilew/10));
			p.setStrokeWidth(tilew/20f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xff000000);
			p.setAlpha((int)(brtime/btimes[t.id]*255f));
			c.drawLine(t.x*tilew,t.y*tilew,(t.x+0.5f)*tilew,(t.y+0.5f)*tilew,p);
			
		}
	}
	
}
