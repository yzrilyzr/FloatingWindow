package com.yzrilyzr.bugs.Game;
import android.graphics.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;
import java.util.concurrent.*;

public class Map
{
	public int[][] map;//x y
	public int size=0;
	public String name="";
	public Bitmap background=null;
	public Bitmap[] mapcache=null;
	public VECfile backvec;
	public Bitmap[] tiles=new Bitmap[100];
	public float mscale=1,tilew;
	public Canvas[] mapcanvas;
	public ArrayList<Wave> waves=new ArrayList<Wave>();
	public ArrayList<Wave> curwaves=new ArrayList<Wave>();
	public ArrayList<AstarPoint[]> wpmap=new ArrayList<AstarPoint[]>();
	public ArrayList<ArrayList<AstarPoint>> wpwaypoint=new ArrayList<ArrayList<AstarPoint>>();
	public int wpindex=0;
	public int lives,money,score;
	public Wave nextwave;
	public int curwaveindex=-1,tobugs;
	public Tower selectedTower;
	public float sendnowcd;
	public boolean lock=false,which=false;
	public CopyOnWriteArrayList<Bug> bugs=new CopyOnWriteArrayList<Bug>();
	public CopyOnWriteArrayList<Tower> towers=new CopyOnWriteArrayList<Tower>();
	public CopyOnWriteArrayList<Bullet> bullets=new CopyOnWriteArrayList<Bullet>();
	public void loadTiles(float ms) throws Exception
	{
		this.mscale=ms;
		tiles[0]=null;
		tiles[1]=tile("wall");
		tiles[2]=tile("tree",1.8f);
		tiles[3]=tile("start");
		tiles[4]=tile("finish");
		tiles[5]=tile("waypoint");
		background=VECfile.createBitmap(backvec,(int)Utils.px(900f*ms),(int)Utils.px(900f*ms));
		mapcanvas=new Canvas[2];
		mapcache=new Bitmap[2];
		for(int i=0;i<2;i++)
		{
			mapcache[i]=Bitmap.createBitmap(background.getWidth(),background.getHeight(),Bitmap.Config.ARGB_8888);
			mapcanvas[i]=new Canvas(mapcache[i]);
		}
		tilew=(float)mapcache[0].getWidth()/(float)size;
	}
	public static Map loadMap(String st)throws Exception
	{
		Map tmp=new Map();
		int mapi=-1;
		boolean fw=false,wp=false;
		String[] fg=st.split("\n");
		for(String l:fg)
		{
			if(l.startsWith("#"))continue;
			else if(l.startsWith("name:"))tmp.name=(l.substring(5));
			else if(l.startsWith("size:"))tmp.setSize(Integer.parseInt(l.substring(5)));
			else if(l.startsWith("money:"))tmp.money=(Integer.parseInt(l.substring(6)));
			else if(l.startsWith("lives:"))tmp.lives=(Integer.parseInt(l.substring(6)));
			else if(l.startsWith("background:"))
			{
				String b=l.substring(11);
				if(b.startsWith("@"))tmp.backvec=VECfile.readFileFromIs(Utils.ctx.getAssets().open(b.substring(1)));
				else tmp.backvec=VECfile.readFile(Utils.mainDir+"地图/"+tmp.name+"/"+b);
			}
			else if(l.startsWith("map:"))mapi=0;
			else if(l.startsWith("mapend"))mapi=-1;
			else if(l.startsWith("wave:"))fw=true;
			else if(l.startsWith("waveend"))fw=false;
			else if(l.startsWith("wpmap:"))wp=true;
			else if(l.startsWith("wpmapend"))wp=false;
			else if(mapi!=-1)tmp.fillData(mapi++,l);
			else if(fw)tmp.waves.add(new Wave(l));
			else if(wp)
			{
				String[] f=l.split(" ");
				AstarPoint a=new AstarPoint(Integer.parseInt(f[0]),Integer.parseInt(f[1]));
				AstarPoint b=new AstarPoint(Integer.parseInt(f[2]),Integer.parseInt(f[3]));
				tmp.wpmap.add(new AstarPoint[]{a,b});
			}
		}
		return tmp;
	}
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
	public ArrayList<AstarPoint> findAllPossibleWayPoints()
	{
		ArrayList<AstarPoint> po=new ArrayList<AstarPoint>();
		for(int x=0;x<map.length;x++)
			for(int y=0;y<map[x].length;y++)
			{
				if(!isWall(x,y))continue;
				else
				{
					if(!isWall(x-1,y-1)&&!isWall(x,y-1)&&!isWall(x-1,y))po.add(new AstarPoint(x-1,y-1));
					if(!isWall(x,y-1)&&!isWall(x+1,y-1)&&!isWall(x+1,y))po.add(new AstarPoint(x+1,y-1));
					if(!isWall(x-1,y)&&!isWall(x-1,y+1)&&!isWall(x,y+1))po.add(new AstarPoint(x-1,y+1));
					if(!isWall(x+1,y)&&!isWall(x,y+1)&&!isWall(x+1,y+1))po.add(new AstarPoint(x+1,y+1));
				}
			}
		return po;
	}
	public ArrayList<AstarPoint> sortWapPoint(ArrayList<AstarPoint> possible,AstarPoint[] asp){
		ArrayList<AstarPoint> mpossible=new ArrayList<AstarPoint>();
		//ArrayList<AstarPoint> sort=new ArrayList<AstarPoint>();
		//ArrayList<AstarPoint> reach=new ArrayList<AstarPoint>();
		mpossible.addAll(possible);
		//mpossible.add(asp[1]);
		final AstarPoint start=asp[0];
		final AstarPoint finish=asp[1];
		mpossible.add(finish);
		//AstarPoint p=start;
		//sort.add(start);
		explore(mpossible,start);
		ch(start,finish);
		return null;
	}
	public void ch(AstarPoint a,AstarPoint finish){
		for(AstarPoint b:a.child)ch(b,finish);
		if(a==finish);
	}
	public void explore(ArrayList<AstarPoint> mpossible,AstarPoint p){
		//p能到达的点
		ArrayList<AstarPoint> reach=getReachPoints(mpossible,p);
		p.child.addAll(reach);
		//剩下有用的点
		ArrayList<AstarPoint> q=new ArrayList<AstarPoint>();
		q.addAll(mpossible);
		q.removeAll(reach);
		//在能到达的点下探索剩下有用的点
		for(AstarPoint f:reach){
			explore(q,f);
		}
	}
	public ArrayList<AstarPoint> getReachPoints(ArrayList<AstarPoint> mpossible,AstarPoint p){
		ArrayList<AstarPoint> reach=new ArrayList<AstarPoint>();
		for(AstarPoint p1:mpossible)
			if(reachable(p,p1)){
				reach.add(p1);
				//p1.parent=p;
			}
		return reach;
	}
	public boolean findWayPoint()
	{
		ArrayList<ArrayList<AstarPoint>> wpwaypointt=new ArrayList<ArrayList<AstarPoint>>();
		ArrayList<AstarPoint> possible=findAllPossibleWayPoints();
		for(AstarPoint[] asp:wpmap)//寻找对应路点，刷出顺序:1,2,3…
		{
			ArrayList<AstarPoint> sort=sortWayPoints(possible, asp),opt=null;
			while((opt=optimize(sort)).size()<sort.size())sort=opt;
			wpwaypointt.add(opt);
//			ArrayList<AstarPoint> c=new ArrayList<AstarPoint>();
//			ArrayList<AstarPoint> u=new ArrayList<AstarPoint>();
//			AstarPoint np=start;
//
//
//			while(true)
//			{
//				AstarPoint fmin=null;
//				int l=np.x-1,t=np.y-1,r=np.x+1,b=np.y+1;
//				if(l>=0&&isWall(l,np.y))l=np.x;
//				if(r<size&&isWall(r,np.y))r=np.x;
//				if(t>=0&&isWall(np.x,t))t=np.y;
//				if(b<size&&isWall(np.x,b))b=np.y;
//				for(int x=l;x<=r;x++)
//				{
//					for(int y=t;y<=b;y++)
//					{
//						//◆如果它是不可抵达的或者它在 close list 中，忽略它
//						if(x==np.x&&y==np.y)continue;
//						if(x<0||x>=size||y<0||y>=size)continue;
//						if(isWall(x,y))continue;
//						if(containPoint(c,x,y)/*||containPoint(u,x,y)*/)continue;
//						AstarPoint tmp=new AstarPoint(x,y,distancePoint(x,y,np),/*(distancePoint(x,y,finish))/4*/0,np);
//						if(fmin==null||tmp.g+tmp.h<fmin.g+fmin.h)fmin=tmp;
//						//if(tmp==null)MainActivity.toast(tmp+"");
//					}
//				}
//				if(fmin==null)
//				{
//					u.add(np);
//					np=np.parent;
//					if(np==null)return false;
//				}
//				else
//				{
//					np=fmin;
//					c.add(np);
//				}
//				try
//				{
//					if(np==null)return false;
//					if(np.x==finish.x&&np.y==finish.y)break;
//				}
//				catch(Throwable tt)
//				{
//					break;
//				}
//			}
//			for(AstarPoint x:u)c.remove(x);
//			AstarPoint a=new AstarPoint(start.x,start.y),d=null;
//			u.clear();
//			u.add(a);
//			for(int i=c.size()-1;i>=0;i--)
//			{
//				AstarPoint b=c.get(i);
//				if(a==b)break;
//				if(reachable(a,b))
//				{
//					u.add(b);
//					i=c.size()-1;
//					a=b;
//				}
//			}
//			u.add(new AstarPoint(finish.x,finish.y));
//			wpwaypointt.add(u);
		}
		wpwaypoint.clear();
		
		for(ArrayList<AstarPoint>m:wpwaypointt)wpwaypoint.add(m);
		return true;
	}

	private ArrayList<AstarPoint> sortWayPoints(ArrayList<AstarPoint> possible, AstarPoint[] asp)
	{
		ArrayList<AstarPoint> mpossible=new ArrayList<AstarPoint>();
		ArrayList<AstarPoint> sort=new ArrayList<AstarPoint>();
		ArrayList<AstarPoint> reach=new ArrayList<AstarPoint>();
		mpossible.addAll(possible);
		mpossible.add(asp[1]);
		final AstarPoint start=asp[0];
		final AstarPoint finish=asp[1];
		AstarPoint p=start;
		sort.add(start);
		int findcount=0;
		while(p!=finish&&findcount++<1000)
		{
			for(AstarPoint p1:mpossible)
				if(reachable(p,p1))reach.add(p1);
			final AstarPoint g=p;
			Collections.sort(reach,new Comparator<AstarPoint>(){
					@Override
					public int compare(AstarPoint p1, AstarPoint p2)
					{
						// TODO: Implement this method
						/*float sp1=distancePoint(start,p1),
							sp=distancePoint(start,g),
							ep=distancePoint(finish,g),
							ep1=distancePoint(finish,p1),
							pp1=distancePoint(g,p1),
							sp2=distancePoint(start,p2),
							ep2=distancePoint(finish,p2),
							pp2=distancePoint(g,p2);
							float f1=sp1-sp+ep-ep1-pp1;
							float f2=sp2-sp+ep-ep2-pp2;
						if(f1==f2)return getAngle(p1,start,finish)>getAngle(p2,start,finish)?1:-1;
						*/
						float f1=g(p1.x,p1.y,start.x,start.y)+h(p1.x,p1.y,finish.x,finish.y);
						float f2=g(p2.x,p2.y,start.x,start.y)+h(p2.x,p2.y,finish.x,finish.y);
						return f1>f2?1:-1;
					}
					public float h(float x1,float y1,float x2,float y2){
						return (float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
					}
					public float g(float x1,float y1,float x2,float y2){
						return (float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
					}
				});
			if(reach.size()==0)
			{
				sort.remove(p);
				p=sort.get(sort.size()-1);
			}
			else
			{
				p=reach.get(0);
				sort.add(p);
				mpossible.remove(p);
				reach.clear();
			}
			if(p==finish)
			{
				return sort;
			}
			if(mpossible.size()==0||p==start)return null;
		}
		return null;
	}
	public float getAngle(AstarPoint center,AstarPoint p1,AstarPoint p2){
		float x1=p1.x-center.x,y1=p1.y-center.y;
		float x2=p2.x-center.x,y2=p2.y-center.y;
		return (float)Math.acos((x1*x2+y1*y2)/(Math.sqrt(x1*x1+y1*y1)*Math.sqrt(x2*x2+y2*y2)));
	}
	public ArrayList<AstarPoint> optimize(ArrayList<AstarPoint> c)
	{
		ArrayList<AstarPoint> u=new ArrayList<AstarPoint>();
		AstarPoint a=c.get(0),d=null;
		u.add(a);
		for(int i=c.size()-1;i>=0;i--)
		{
			AstarPoint b=c.get(i);
			if(a==b)break;
			if(reachable(a,b))
			{
				u.add(b);
				i=c.size()-1;
				a=b;
			}
		}
		u.add(c.get(c.size()-1));
		return u;
	}
	boolean isWall(int x,int y)
	{
		if(x<0||y<0||x>=map.length||y>=map[x].length)return true;
		int id=map[x][y];
		if(id!=0&&id!=3&&id!=4&&id!=5)return true;
		for(Tower t:towers)if((int)t.x==x&&(int)t.y==y)return true;
		return false;
	}
	boolean containBug(int x,int y)
	{
		for(Bug t:bugs)
		{
			if(x<=Math.ceil(t.x)&&x>=Math.floor(t.x)&&
				y<=Math.ceil(t.y)&&y>=Math.floor(t.y))return true;
		}
		return false;
	}

	boolean reachable(AstarPoint a,AstarPoint b)
	{
		//if(a==null||b==null)return true;
		int x1=Math.min(a.x,b.x);
		int x2=Math.max(a.x,b.x);
		int y1=Math.min(a.y,b.y);
		int y2=Math.max(a.y,b.y);
		for(int x=x1;x<=x2;x++)
			for(int y=y1;y<=y2;y++)
				if(isWall(x,y))return false;
		/*for(int i=x1;i<=x2;i++)if(isWall(i,y1))return false;
		 for(int i=x1;i<=x2;i++)if(isWall(i,y2))return false;
		 for(int i=y1;i<=y2;i++)if(isWall(x1,i))return false;
		 for(int i=y1;i<=y2;i++)if(isWall(x2,i))return false;
		 */
		return true;
	}
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
	}
	private Bitmap tile(String name) throws Exception
	{
		int s=(int)(Utils.px(900*mscale)/size);
		return VECfile.createBitmap(Utils.ctx,"tiles/"+name,s,s);
	}
	private Bitmap tile(String name,float f) throws Exception
	{
		int s=(int)(Utils.px(900*mscale)*f/(float)size);
		return VECfile.createBitmap(Utils.ctx,"tiles/"+name,s,s);
	}
	public void setTileId(int id,Bitmap b)
	{
		tiles[id]=b;
	}
	public void setBackground(Bitmap background)
	{
		this.background = background;
	}

	public void setSize(int size)
	{
		this.size = size;
		map=new int[size][size];
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
		public ArrayList<AstarPoint> child=new ArrayList<AstarPoint>();
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
		int id,c,wpi;
		float sec;
		float cd;
		public Wave(String pas)
		{
			String[] f=pas.split(" ");
			id=Integer.parseInt(f[0]);
			c=Integer.parseInt(f[1]);
			sec=Integer.parseInt(f[2]);
		}
	}
}
