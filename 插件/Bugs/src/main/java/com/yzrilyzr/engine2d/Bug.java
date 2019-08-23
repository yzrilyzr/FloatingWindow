package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.graphics.PointF;
import java.util.ArrayList;
import com.yzrilyzr.engine2d.Map.AstarPoint;
import android.graphics.Matrix;
import android.content.Context;

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
	public Bug(float x,float y,int size)
	{
		try
		{
			int s=Shape.pi(size);
			bugicon=VECfile.createBitmap(MainActivity.ctx,"bugs/"+Math.round(Math.random()*1),s,s);
			this.x=x;
			this.y=y;
			vel=p(1000);
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
		bugicon=VECfile.createBitmap(ico,s,s);
		bugdicon=VECfile.createBitmap(dico,s,s);
		if(id==0)
		{
			maxhp=100;hp=100;vel=5f;
			money=12;
			score=6;
			exp=2;
		}
		if(id==1)
		{
			maxhp=200;hp=200;vel=5f;
			money=25;
			score=15;
			exp=3;
		}

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
			MainActivity.canLevepUp();
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
			else if(f==-1){
				int ind=0;
				float mind=-1;
				for(int i=0;i<u.size();i++){
					Map.AstarPoint a=u.get(i);
					float dd=(float)Math.sqrt((a.x-x)*(a.x-x)+(a.y-y)*(a.y-y));
					if(mind==-1)mind=dd;
					if(dd<mind){
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
			dir=(float)(getArc(vx,vy,vel)*180f/Math.PI)+90f;
			ma.reset();
			ma.postTranslate(-bugicon.getWidth()/2,-bugicon.getHeight()/2);
			ma.postRotate(dir);
			ma.postTranslate(bugicon.getWidth()/2,bugicon.getHeight()/2);
			ma.postTranslate((int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2));
			c.drawBitmap(bugicon,ma,p);
			
			c.drawBitmap(bugicon,ma,p);
		}
	}

}
