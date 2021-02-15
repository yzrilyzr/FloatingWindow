package com.yzrilyzr.bugs.Game;
import android.graphics.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;
import java.util.concurrent.*;

public class Map
{
	/*
	public int[][] map;//x y
	//public int size=0;
		
	public float tilew;
	public Canvas[] mapcanvas;
	public int wpindex=0;
	public int curwaveindex=-1,tobugs;
	public Tower selectedTower;
	public float sendnowcd;
	public boolean lock=false,which=false;
	public CopyOnWriteArrayList<Bug> bugs=new CopyOnWriteArrayList<Bug>();
	public CopyOnWriteArrayList<Tower> towers=new CopyOnWriteArrayList<Tower>();
	public CopyOnWriteArrayList<Bullet> bullets=new CopyOnWriteArrayList<Bullet>();
	
	
	public void setUpBugs(float dt)
	{
		if(sendnowcd>0)sendnowcd-=dt;
		if(curwaveindex<waves.size())
		{
			if(nextwave==null||nextwave.sec<=0)
			{
				curwaveindex++;
				if(curwaveindex<waves.size())
				{
					nextwave=waves.get(curwaveindex);
					nextwave.wpi=wpindex;
					curwaves.add(nextwave);
					if(++wpindex>=wpmap.size())wpindex=0;
				}
			}
			else if(nextwave!=null)
			{
				nextwave.sec-=dt;
			}
		}
		else
		{
			nextwave=null;
			sendnowcd=3;
		}
		for(int t=0;t<curwaves.size();t++)
		{
			final Wave w=curwaves.get(t);
			if(w.sec<=0)
			{
				if(w.cd<=0)
				{
					if(w.c>0)
					{
						//bugs.add(new Bug(w.id,wpindex));
						w.c--;
						w.cd=0.7f;
					}
					else curwaves.remove(w);
				}
				else w.cd-=dt;
			}
		}
	}
	public void sendnow()
	{
		if(nextwave!=null&&sendnowcd<=0)
		{
			nextwave.sec=0;
			sendnowcd=3;
		}
	}
		/*public ArrayList<AstarPoint> getReachPoints(ArrayList<AstarPoint> mpossible,AstarPoint p){
		ArrayList<AstarPoint> reach=new ArrayList<AstarPoint>();
		for(AstarPoint p1:mpossible)
			if(reachable(p,p1)){
				reach.add(p1);
				//p1.parent=p;
			}
		return reach;
	}*/
	

	/*
	public float getAngle(AstarPoint center,AstarPoint p1,AstarPoint p2){
		float x1=p1.x-center.x,y1=p1.y-center.y;
		float x2=p2.x-center.x,y2=p2.y-center.y;
		return (float)Math.acos((x1*x2+y1*y2)/(Math.sqrt(x1*x1+y1*y1)*Math.sqrt(x2*x2+y2*y2)));
	}
	*/
	/*
	boolean containBug(int x,int y)
	{
		for(Bug t:bugs)
		{
			if(x<=Math.ceil(t.x)&&x>=Math.floor(t.x)&&
				y<=Math.ceil(t.y)&&y>=Math.floor(t.y))return true;
		}
		return false;
	}

	/*
	int distancePoint(int x,int y,AstarPoint w)
	{
		return (int)(1000f*Math.sqrt((x-w.x)*(x-w.x)+(y-w.y)*(y-w.y)));
	}
	int distancePoint(AstarPoint p1,AstarPoint p2)
	{
		return (int)(1000f*Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y)));
	}
	boolean containPoint(ArrayList<AstarPoint> l,int x,int y)
	{
		for(AstarPoint h:l)
			if(h.x==x&&h.y==y)
			{
				return true;
			}
		return false;
	}*//*
		public void setTileId(int id,Bitmap b)
	{
		tiles[id]=b;
	}
	public void setBackground(Bitmap background)
	{
		this.background = background;
	}

	
	

	*/
}
