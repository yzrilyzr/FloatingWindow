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
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import com.yzrilyzr.engine2d.Map.AstarPoint;
import java.util.Iterator;
import android.graphics.Canvas;

public class Map
{
	public int[][] map;//x y
	public int size=0;
	public String name="";
	public Bitmap background=null,back=null;
	public VECfile backvec;
	public Bitmap[] tiles=new Bitmap[100];
	public float mscale=1,tilew;
	public Canvas bc;
	public ArrayList<Wave> waves=new ArrayList<Wave>();
	public ArrayList<AstarPoint[]> wpmap=new ArrayList<AstarPoint[]>();
	public ArrayList<ArrayList<AstarPoint>> wpwaypoint=new ArrayList<ArrayList<AstarPoint>>();
	public void loadTiles(float ms) throws Exception
	{
		this.mscale=ms;
		tiles[0]=null;
		tiles[1]=tile("wall");
		tiles[2]=tile("tree",1.8f);
		tiles[3]=tile("start");
		tiles[4]=tile("finish");
		tiles[5]=tile("waypoint");
		background=VECfile.createBitmap(backvec,Shape.pi(900f*ms),Shape.pi(900f*ms));
		back=Bitmap.createBitmap(Shape.pi(900f*ms),Shape.pi(900f*ms),Bitmap.Config.ARGB_8888);
		bc=new Canvas(back);
		tilew=(float)back.getWidth()/(float)size;
	}
	public static Map loadMap(InputStream i) throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(i));
		String l=null;
		Map tmp=new Map();
		int mapi=-1;
		boolean fw=false,wp=false;
		while((l=br.readLine())!=null)
		{
			if(l.startsWith("#"))continue;
			else if(mapi!=-1)tmp.fillData(mapi++,l);
			else if(fw)tmp.waves.add(new Wave(l));
			else if(wp)
			{
				String[] f=l.split(" ");
				MainActivity.toast("=");
				AstarPoint a=new AstarPoint(Integer.parseInt(f[0]),Integer.parseInt(f[1]));
				AstarPoint b=new AstarPoint(Integer.parseInt(f[2]),Integer.parseInt(f[3]));
				tmp.wpmap.add(new AstarPoint[]{a,b});
			}
			else if(l.startsWith("name:"))tmp.setName(l.substring(5));
			else if(l.startsWith("size:"))tmp.setSize(Integer.parseInt(l.substring(5)));
			else if(l.startsWith("background:"))
			{
				String b=l.substring(11);
				if(b.startsWith("@"))tmp.backvec=VECfile.readFileFromIs(MainActivity.ctx.getAssets().open(b.substring(1)));
				else tmp.backvec=VECfile.readFile(MainActivity.mainDir+"地图/"+tmp.name+"/"+b);
			}
			else if(l.startsWith("map:"))mapi=0;
			else if(l.startsWith("mapend"))mapi=-1;
			else if(l.startsWith("wave:"))fw=true;
			else if(l.startsWith("waveend"))fw=false;
			else if(l.startsWith("wpmap:"))wp=true;
			else if(l.startsWith("wpmapend"))wp=false;
		}
		return tmp;
	}

	public void findWayPoint()
	{
		for(AstarPoint[] asp:wpmap)
		{
			MainActivity.toast("next");
			AstarPoint start=asp[0];
			AstarPoint finish=asp[1];
			ArrayList<AstarPoint> c=new ArrayList<AstarPoint>();
			ArrayList<AstarPoint> u=new ArrayList<AstarPoint>();
			AstarPoint np=start;


			while(true)
			{
				AstarPoint fmin=null;
				int l=np.x-1,t=np.y-1,r=np.x+1,b=np.y+1;
				if(l>=0&&isWall(l,np.y))l=np.x;
				if(r<size&&isWall(r,np.y))r=np.x;
				if(t>=0&&isWall(np.x,t))t=np.y;
				if(b<size&&isWall(np.x,b))b=np.y;
				for(int x=l;x<=r;x++)
				{
					for(int y=t;y<=b;y++)
					{
						//◆如果它是不可抵达的或者它在 close list 中，忽略它
						if(x==np.x&&y==np.y)continue;
						if(x<0||x>=size||y<0||y>=size)continue;
						if(isWall(x,y))continue;
						if(containPoint(c,x,y)/*||containPoint(u,x,y)*/)continue;
						AstarPoint tmp=new AstarPoint(x,y,distancePoint(x,y,np),distancePoint(x,y,finish),np);
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
					if(np==null||np.x==finish.x&&np.y==finish.y)break;
				}
				catch(Throwable tt)
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
					if(d==null)
					{
						MainActivity.toast(i+"");
						break;
					}
					a=d;
					u.add(a);
					i--;
				}
			}*/
			wpwaypoint.add(c);
			MainActivity.toast("u");
		}
	}
	boolean isWall(int x,int y)
	{
		int id=map[x][y];
		return id!=0&&id!=4&&id!=5;
	}
	boolean reachable(AstarPoint a,AstarPoint b)
	{
		//if(a==null||b==null)return true;
		int x1=Math.min(a.x,b.x);
		int x2=Math.max(a.x,b.x);
		int y1=Math.min(a.y,b.y);
		int y2=Math.max(a.y,b.y);
		for(int i=x1;i<=x2;i++)if(isWall(i,y1))return false;
		for(int i=x1;i<=x2;i++)if(isWall(i,y2))return false;
		for(int i=y1;i<=y2;i++)if(isWall(x1,i))return false;
		for(int i=y1;i<=y2;i++)if(isWall(x2,i))return false;
		return true;
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
		int s=Shape.pi(900*mscale)/size;
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
		}
	}
	public static class AstarPoint
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

	public static class Wave
	{
		int id1,c1,id2,c2,sec;
		public Wave(String pas)
		{
			String[] f=pas.split(" ");
			id1=Integer.parseInt(f[0]);
			c1=Integer.parseInt(f[1]);
			id2=Integer.parseInt(f[2]);
			c2=Integer.parseInt(f[3]);
			sec=Integer.parseInt(f[4]);
		}
	}
}
