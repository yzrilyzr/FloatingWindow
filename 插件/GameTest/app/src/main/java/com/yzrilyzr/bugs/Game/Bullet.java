package com.yzrilyzr.bugs.Game;
import android.graphics.*;
import com.yzrilyzr.icondesigner.*;
import com.yzrilyzr.game.*;
import java.util.*;

public class Bullet extends GObj
{
	Tower t;
	Bug b;
	//VECfile icon;
	public float brtime=0;//滞留时间，>0启用 -1接触后消失
	public float branimtime=-1;//消失动画时间
	float vel,cdtime;
	//固定的bug位置
	float fbx,fby,rby;
	private Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final float[] btimes=new float[]{-1,-1,-1,1000,400,1000,700,1000,500,500};
	private static final float[] batimes=new float[]{200,200,500,500,200,1000,700,1000,500,500};
	public Bullet(Bug pb,Tower p/*, float r,float v,float br*/){
		b=pb;
		t=p;
		brtime=btimes[t.id];
		fbx=b.x;
		fby=b.y;
		rby=new Random().nextFloat()/20f;
		x=t.x;
		y=t.y;
		//branimtime=batimes[t.id];
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
	private void startBrAnim(){
		if(branimtime==-1)branimtime=batimes[t.id];
	}
	public void onDraw(Canvas c,float tilew,float dt)
	{
		// TODO: Implement this method
		float d=tilew/2;
		if(t.id==0||t.id==1){
			if(!contains(t.x,t.y,t.r)){
				startBrAnim();
				vel=0;
			}
			else if(t.id==0)vel=2f;
			else if(t.id==1)vel=4f;
			float r=(float)Math.sqrt(Math.pow(b.x-x,2)+Math.pow(b.y-y,2));
			x+=(b.x-x)/r*vel*dt/1000f;
			y+=(b.y-y)/r*vel*dt/1000f;
			//p.setStrokeWidth(tilew/20f);
			p.setStyle(Paint.Style.FILL);
			p.setColor(0xffff8927);
			if(branimtime>=0)p.setAlpha((int)(branimtime/batimes[t.id]*255f));
			c.drawCircle((x+0.5f)*tilew,(y+0.5f)*tilew,tilew/15,p);
			//c.drawCircle((b.x+0.5f)*tilew,(b.y+0.5f)*tilew,tilew/2,p);
			//攻击
			if(contains(b.x,b.y,0.5f)&&branimtime==-1){
				b.hp-=t.dmg;
				startBrAnim();
			}
		}
		else if(t.id==4){
			if(!b.contains(t.x,t.y,t.r))brtime=Math.min(brtime,200);
			p.setPathEffect(new DiscretePathEffect(tilew/5,tilew/5));
			p.setStrokeWidth(tilew/10f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xA07f25ff);
			p.setAlpha((int)(brtime/btimes[t.id]*180f));
			c.drawLine(t.x*tilew+d,t.y*tilew+tilew/8,b.x*tilew+d,b.y*tilew+d,p);
		}
		else if(t.id==5){
			float r=(float)Math.sqrt(Math.pow(fbx-t.x,2)+Math.pow(fby-t.y,2));
			p.setPathEffect(new DiscretePathEffect(r*tilew/3,tilew/5));
			p.setStrokeWidth(tilew/10f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xffff2020);
			p.setAlpha((int)(brtime/btimes[t.id]*220f));
			c.drawLine(t.x*tilew+d,t.y*tilew+tilew/8,fbx*tilew+d,fby*tilew+d+rby,p);
		}
		else if(t.id==9){
			p.setPathEffect(new DiscretePathEffect(tilew/10,tilew/10));
			p.setStrokeWidth(tilew/20f);
			p.setStyle(Paint.Style.STROKE);
			p.setColor(0xff000000);
			p.setAlpha((int)(brtime/btimes[t.id]*255f));
			c.drawLine(t.x*tilew,t.y*tilew,(t.x+0.5f)*tilew,(t.y+0.5f)*tilew,p);
			
		}
	}
	
}
