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
			int s=Shape.pi(900*mscale)/size;
			bugicon=VECfile.createBitmap(MainActivity.ctx,"bugs/bug",s,s);
			this.x=x;
			this.y=y;
			vel=5;
		}
		catch(Throwable e)
		{
			MainActivity.toast(e);
		}
	}
	public Bug(int id,int way)
	{
		if(id==0)
		{
			maxhp=20;hp=20;vel=5f;
			wayIndex=way;
			money=80;
			score=50;
			exp=2;
			wayp=MainActivity.map.wpwaypoint.get(wayIndex).get(0);
			x=wayp.x;
			y=wayp.y;
			int s=(int)MainActivity.map.tilew;
			Context c=MainActivity.ctx;
			try
			{
				ico=VECfile.readFileFromIs(c.getAssets().open("bugs/bug.vec"));
				dico=VECfile.readFileFromIs(c.getAssets().open("bugs/bugdie1.vec"));
				bugicon=VECfile.createBitmap(ico,s,s);
				bugdicon=VECfile.createBitmap(dico,s,s);
				
			}
			catch (Exception e)
			{}

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
		else{
			c.drawBitmap(bugicon,(int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2),p);
		}
	}

}
