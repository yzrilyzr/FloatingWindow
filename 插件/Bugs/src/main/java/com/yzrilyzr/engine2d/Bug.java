package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.graphics.PointF;
import java.util.ArrayList;
import com.yzrilyzr.engine2d.Map.AstarPoint;
import android.graphics.Matrix;
import android.content.Context;
import java.util.Random;

public class Bug extends Shape
{
	Bitmap bugicon,bugdicon;
	float vx,vy;
	float ax,ay;
	float mscale=1;
	//float x,y;
	float vel;
	float hp,maxhp,frztime;
	float rdmg,rtime,rdtime,rdcdtime;
	float slow,dir;
	VECfile ico,dico;
	AstarPoint wayp;
	int wayIndex;
	int score,money,exp;
	Matrix ma=new Matrix();
	static final float[] hps=new float[]{50,100,400,200,1600,210,420,340,230,250,1024,512,768,384,256,448,256,832,512,8192};
	static final int[] moneys=new int[]{12,23,35,25,78,41,45,33,28,36,124,68,94,52,41,69,47,97,81,500};
	static final int[] exps=new int[]{1,2,4,6,10,3,8,6,3,7,15,8,9,7,5,6,4,12,9,23};
	static final int[] scores=new int[]{6,12,23,28,46,28,35,27,24,26,56,42,49,34,29,42,25,50,37,95};
	static final float[] vels=new float[]{5,5,5,8,3,9,5,6,5,9,5,5,5,5,5,6,5,6,5,3};
	public Bug(float x,float y,int size)
	{
		try
		{
			int s=Shape.pi(size);
			int id=new Random().nextInt(20);
			bugicon=VECfile.createBitmap(MainActivity.ctx,"bugs/"+id,s,s);
			this.x=x;
			this.y=y;
			vel=p(1000);
			r=s/2f;
			hp=1;
			try
			{
				bugdicon=VECfile.createBitmap(MainActivity.ctx,"bugs/d"+id,s,s);
			}
			catch (Exception e)
			{
				try
				{
					bugdicon=VECfile.createBitmap(MainActivity.ctx,"bugs/d0",s,s);
				}
				catch(Throwable pe)
				{}
			}

		}
		catch(Throwable e)
		{
			MainActivity.toast(e);
		}
	}
	public Bug(int id,int way)
	{
		wayp=MainActivity.map.wpwaypoint.get(wayIndex).get(0);
		x=wayp.x;
		y=wayp.y;
		int s=(int)MainActivity.map.tilew;
		Context c=MainActivity.ctx;
		wayIndex=way;
		try
		{
			ico=VECfile.readFileFromIs(c.getAssets().open("bugs/"+id+".vec"));
		}
		catch(Throwable e)
		{}
		try
		{
			dico=VECfile.readFileFromIs(c.getAssets().open("bugs/d"+id+".vec"));
		}
		catch (Exception e)
		{
			try
			{
				dico=VECfile.readFileFromIs(c.getAssets().open("bugs/d0.vec"));
			}
			catch(Throwable pe)
			{}
		}
		r=s/2f;
		bugicon=VECfile.createBitmap(ico,s,s);
		bugdicon=VECfile.createBitmap(dico,s,s);
		maxhp=hps[id];
		hp=maxhp;
		vel=vels[id];
		money=moneys[id];
		score=scores[id];
		exp=exps[id];
	}
	public void compute(float dt)
	{
		Map map=MainActivity.map;
		float tilew=map.tilew;
		if(hp<=0)
		{
			map.bugs.remove(this);
			map.money+=money;
			map.score+=score;
			map.tobugs++;
			MainActivity.exp+=exp;
			Canvas c=new Canvas(map.background);
			c.drawBitmap(bugdicon,x*tilew,y*tilew,p);
		}
		float d=(float)Math.sqrt((wayp.x-x)*(wayp.x-x)+(wayp.y-y)*(wayp.y-y));
		if(frztime>0)frztime-=dt;
		else if(d!=0)
		{
			float v2=vel*dt*limit(slow,0.3f,1);
			float dx=wayp.x-x,dy=wayp.y-y;
			x+=dx*v2/d;
			y+=dy*v2/d;
			dir=(float)(getArc(dx,dy,d)*180f/Math.PI)+90f;

		}
		if(d<0.05f)
		{
			ArrayList<Map.AstarPoint> u=map.wpwaypoint.get(wayIndex);
			int f=u.indexOf(wayp);
			if(f>=u.size()-1)
			{
				map.bugs.remove(this);
				map.lives--;
			}
			else if(f==-1)
			{
				int ind=0;
				float mind=-1;
				for(int i=0;i<u.size();i++)
				{
					Map.AstarPoint a=u.get(i);
					float dd=(float)Math.sqrt((a.x-x)*(a.x-x)+(a.y-y)*(a.y-y));
					if(mind==-1)mind=dd;
					if(dd<mind)
					{
						ind=i;
						mind=dd;
					}
				}
				wayp=u.get(ind);
			}
			else wayp=u.get(f+1);
		}
		if(rtime>0)
		{
			rtime-=dt;
			if(rdcdtime<=0)
			{
				rdcdtime=rdtime;
				hp-=rdmg;
			}
			else rdcdtime-=dt;
		}
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		p.setColor(0xff000000);
		if(MainActivity.map!=null)
		{
			Map map=MainActivity.map;
			float tilew=map.tilew;
			ma.reset();
			ma.postTranslate(-bugicon.getWidth()/2,-bugicon.getHeight()/2);
			ma.postRotate(dir);
			ma.postTranslate(bugicon.getWidth()/2,bugicon.getHeight()/2);
			ma.postTranslate(x*tilew,y*tilew);
			c.drawBitmap(bugicon,ma,p);
			p.setColor(0xffcccccc);
			c.drawRect(x*tilew,y*tilew-tilew/8,x*tilew+tilew,y*tilew,p);
			p.setColor(0xff00ff00);
			c.drawRect(x*tilew,y*tilew-tilew/8,x*tilew+tilew*hp/maxhp,y*tilew,p);
		}
		else
		{
			if(hp>0)
			{
				dir=(float)(getArc(vx,vy,vel/3f)*180f/Math.PI)+90f;
				ma.reset();
				ma.postTranslate(-bugicon.getWidth()/2,-bugicon.getHeight()/2);
				ma.postRotate(dir);
				ma.postTranslate(bugicon.getWidth()/2,bugicon.getHeight()/2);
				ma.postTranslate((int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2));
				c.drawBitmap(bugicon,ma,p);
			}
			else
			{
				ma.reset();
				float sc=Ui.NonLinearFunc(-hp/200);
				if(hp>-200){
					ma.postTranslate(-bugicon.getWidth()/2,-bugicon.getHeight()/2);
					ma.postScale(sc,sc);
					ma.postTranslate(bugicon.getWidth()/2,bugicon.getHeight()/2);
				}
				else if(hp<-1500)p.setAlpha((int)(255f*Ui.NonLinearFunc((2000+hp)/500)));
				ma.postTranslate(-bugicon.getWidth()/2,-bugicon.getHeight()/2);
				ma.postRotate(dir);
				ma.postTranslate(bugicon.getWidth()/2,bugicon.getHeight()/2);
				ma.postTranslate((int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2));
				c.drawBitmap(bugdicon,ma,p);
			}
		}
	}

}
