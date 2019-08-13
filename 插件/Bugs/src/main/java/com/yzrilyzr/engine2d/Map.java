package com.yzrilyzr.engine2d;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.graphics.Point;

public class Map
{
	public int[][] map;//x y
	public int size=0;
	public String name="";
	public Bitmap background=null;
	public Bitmap[] tiles=new Bitmap[100];
	public Point start=new Point(),finish=new Point();
	public float mscale=1;
	public void loadTiles(float ms) throws Exception
	{
		this.mscale=ms;
		tiles[0]=null;
		tiles[1]=tile("wall");
		tiles[2]=tile("tree",1.8f);
		tiles[3]=tile("start");
		tiles[4]=tile("finish");
		tiles[5]=tile("waypoint");
	}
	public static Map loadMap(InputStream i) throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(i));
		String l=null;
		Map tmp=new Map();
		int mapi=-1;
		while((l=br.readLine())!=null)
		{
			if(l.startsWith("#"))continue;
			if(l.startsWith("name:"))tmp.setName(l.substring(5));
			else if(l.startsWith("size:"))tmp.setSize(Integer.parseInt(l.substring(5)));
			else if(l.startsWith("background:"))
			{
				String b=l.substring(11);
				if(b.startsWith("@"))
				{
					b=b.substring(1);
					if(b.startsWith("V"))tmp.setBackground(VECfile.createBitmap(MainActivity.ctx,b.substring(1),Shape.p(900),Shape.p(900)));
					else if(b.startsWith("D"))tmp.setBackground(BitmapFactory.decodeStream(MainActivity.ctx.getAssets().open(b.substring(1))));
				}
				else
				{
					if(b.startsWith("V"))tmp.setBackground(VECfile.createBitmap(VECfile.readFile(MainActivity.mainDir+"地图/"+tmp.name+"/"+b.substring(1)),Shape.p(900),Shape.p(900)));
					else if(b.startsWith("D"))tmp.setBackground(BitmapFactory.decodeFile(MainActivity.mainDir+"地图/"+tmp.name+"/"+b.substring(1)));
				}
			}
			else if(l.startsWith("map:"))mapi=0;
			else if(l.startsWith("mapend"))mapi=-1;
			else if(mapi!=-1)tmp.fillData(mapi++,l);
		}
		return tmp;
	}

	public class AstarPoint
	{
		public int x,y;
		public int g,h;
		public AstarPoint parent;

		public AstarPoint(int x, int y, int g, int h, AstarPoint parent)
		{
			this.x = x;
			this.y = y;
			this.g = g;
			this.h = h;
			this.parent = parent;
		}
		public AstarPoint(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	public ArrayList<Point> findWayPoint()
	{
		ArrayList<Point> p=new ArrayList<Point>();

		//ArrayList<AstarPoint> o=new ArrayList<AstarPoint>();
		ArrayList<AstarPoint> c=new ArrayList<AstarPoint>();
		ArrayList<AstarPoint> u=new ArrayList<AstarPoint>();
		AstarPoint fin=new AstarPoint(finish.x,finish.y);
		AstarPoint np=new AstarPoint(start.x,start.y);

		
		while(true)
		{
			AstarPoint fmin=null;
			int l=np.x-1,t=np.y-1,r=np.x+1,b=np.y+1;
			if(l>=0&&map[l][np.y]!=0&&map[l][np.y]!=4)l=np.x;
			if(r<size&&map[r][np.y]!=0&&map[r][np.y]!=4)r=np.x;
			if(t>=0&&map[np.x][t]!=0&&map[np.x][t]!=4)t=np.y;
			if(b<size&&map[np.x][b]!=0&&map[np.x][b]!=4)b=np.y;
			for(int x=l;x<=r;x++)
			{
				for(int y=t;y<=b;y++)
				{
					//◆如果它是不可抵达的或者它在 close list 中，忽略它
					if(x==np.x&&y==np.y)continue;
					if(x<0||x>=size||y<0||y>=size)continue;
					if(map[x][y]!=0&&map[x][y]!=4)continue;
					if(containPoint(c,x,y)/*||containPoint(u,x,y)*/)continue;
					AstarPoint tmp=new AstarPoint(x,y,distancePoint(x,y,np),distancePoint(x,y,fin),np);
					if(fmin==null||tmp.g+tmp.h<fmin.g+fmin.h)fmin=tmp;
					//if(tmp==null)MainActivity.toast(tmp+"");
				}
			}
			if(fmin==null)
			{
				u.add(np);
				np=np.parent;
			}
			else
			{
				np=fmin;
				c.add(np);
			}
			try
			{
				if(np.x==fin.x&&np.y==fin.y)break;
			}
			catch(Throwable e)
			{
				break;
			}
		}
		for(AstarPoint x:u)c.remove(x);
		/*AstarPoint a=new AstarPoint(start.x,start.y),d=null;
		u.clear();
		//u.add(a);
		for(int i=0;i<c.size();i++)
		{
			AstarPoint b=c.get(i);
			if(reachable(a,b))d=b;
			else
			{
				u.add(d);
				a=b;
				//i--;
			}
		}*/
		for(AstarPoint s:c)p.add(new Point(s.x,s.y));
		return p;
	}
	boolean reachable(AstarPoint a,AstarPoint b)
	{
		if(a.x-b.x==0||a.y-b.y==0)return true;
		return false;
	}
	int distancePoint(int x,int y,AstarPoint w)
	{
		return (int)(1000f*Math.sqrt((x-w.x)*(x-w.x)+(y-w.y)*(y-w.y)));
	}
	boolean containPoint(ArrayList<AstarPoint> l,int x,int y)
	{
		for(AstarPoint h:l)
			if(h.x==x&&h.y==y)
			{
				return true;
			}
		return false;
	}
	private Bitmap tile(String name) throws Exception
	{
		int s=Shape.p(900*mscale)/size;
		return VECfile.createBitmap(MainActivity.ctx,"tiles/"+name,s,s);
	}
	private Bitmap tile(String name,float f) throws Exception
	{
		int s=(int)((float)Shape.p(900*mscale)*f/(float)size);
		return VECfile.createBitmap(MainActivity.ctx,"tiles/"+name,s,s);
	}
	public void setTileId(int id,Bitmap b)
	{
		tiles[id]=b;
	}
	public void setBackground(Bitmap background)
	{
		this.background = background;
	}

	public Bitmap getBackground()
	{
		return background;
	}
	public void setSize(int size)
	{
		this.size = size;
		map=new int[size][size];
	}

	public int getSize()
	{
		return size;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	public void fillData(int y,String line)
	{
		if(line.length()!=size)return;
		for(int i=0;i<line.length();i++)
		{
			int id=Integer.parseInt(line.substring(i,i+1));
			map[i][y]=id;
			if(id==3)start=new Point(i,y);
			else if(id==4)finish=new Point(i,y);
		}
	}
}
