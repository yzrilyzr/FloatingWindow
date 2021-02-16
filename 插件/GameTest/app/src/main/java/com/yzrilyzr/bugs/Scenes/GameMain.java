package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;
import java.util.concurrent.*;

import com.yzrilyzr.game.Vector;

public class GameMain extends Scene
{
	//游戏对象列表
	public CopyOnWriteArrayList<Bug> bugs=new CopyOnWriteArrayList<Bug>();
	public CopyOnWriteArrayList<Tower> towers=new CopyOnWriteArrayList<Tower>();
	public CopyOnWriteArrayList<Bullet> bullets=new CopyOnWriteArrayList<Bullet>();
	//材质缓存表
	public Bitmap[] tiles=new Bitmap[100];
	public Bitmap[] towersicon=new Bitmap[10];
	public Bitmap[] bugsicon=new Bitmap[20];
	//游戏内容列表
	public ArrayList<Wave> waves=new ArrayList<Wave>();
	//public ArrayList<Wave> curwaves=new ArrayList<Wave>();
	public ArrayList<Point[]> wpmap=new ArrayList<Point[]>();
	public ArrayList<ArrayList<Point>> wpwaypoint=new ArrayList<ArrayList<Point>>();
	//游戏基本数据
	public String name="";
	public int lives,money,score;
	public Wave nextwave;
	int curwave=0;
	//绘图数据
	public Bitmap background=null;
	public VECfile backvec;
	VECfile waveVec;
	int[][] map=null;
	Tower tmptower=null;
	float sendnowcool=-1000;
	float tilew;
	public CopyOnWriteArrayList<TowerInfo> towerInfo=new CopyOnWriteArrayList<TowerInfo>();
	public GameMain(String id,String data)
	{
		super(id);
		try
		{
			loadMap(data);
			tilew=Utils.getHeight()/map[0].length;
			loadTextures();
			findWayPoint();
			loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamemain.txt"));
			waveVec=findUi("rightmenu").setVecRealTimeRender(true);
			for(int i=0;i<10;i++)	
			{
				String r=Integer.toString(i);
				loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamerightmenuitems.txt"),r,(i%2==0?"0":"100s"),(i/2)*100+"s",r,r,r);
				Ui u=findUi("rightmenuitem"+r);
				VECfile v=VECfile.readFileFromIs(Utils.ctx.getAssets().open("vec/game/rightmenuitems.vec"));
				CopyOnWriteArrayList<Shape> hs=v.getShapes();
				hs.get(0).txt="$"+Integer.toString((int)Tower.moneys[i]);
				//u.vec=v;
				u.bmp=VECfile.createBitmap(v,(int)u.width,(int)u.height);

			}
		}
		catch (Exception e)
		{
			Utils.alert(e);
			Utils.loadScene(new LevelSelect("levelselect"));
			return;
		}
	}
	//购买塔，右边列表
	public void buytower(Ui s)
	{
		String id=s.id.replaceAll("(item|rightmenuitem)","");
		tmptower=new Tower(Integer.parseInt(id),0,0);
		tmptower.x=-99;
		tmptower.y=-99;
		towerinfo();
	}
	//设置按钮
	public void setting(Ui s)
	{
		Utils.loadScene(new Settings("settings"));
	}
	//现在出动
	public void sendnow(Ui s)
	{
		if(nextwave!=null)nextwave.sec=0;
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamemain_sendnow.txt"));
		sendnowcool=5300;
	}
	//触摸
	@Override
	public boolean onTouch(MotionEvent p2)
	{
		if(super.onTouch(p2))return true;
		float x=p2.getX(),y=p2.getY();
		switch(p2.getAction())
		{
			case MotionEvent.ACTION_DOWN:

			case MotionEvent.ACTION_MOVE:
				if(tmptower!=null&&!towers.contains(tmptower))
				{
					tmptower.x=(int)(x/tilew);
					tmptower.y=(int)(y/tilew);
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(tmptower!=null&&!towers.contains(tmptower))
				{
					tmptower.x=(int)(x/tilew);
					tmptower.y=(int)(y/tilew);
					if(!isWall((int)tmptower.x,(int)tmptower.y))
					{
						//放置塔
						towers.add(tmptower);
						if(!findWayPoint())
						{
							towers.remove(tmptower);
							tmptower=null;
						}
						//else towerinfo();
					}
					else tmptower=null;
					towerinfo();
				}
				else
				{
					//选中地图的塔
					tmptower=null;
					for(Tower t:towers)
					{
						if(t.x==(int)(x/tilew)&&t.y==(int)(y/tilew))
						{
							tmptower=t;
							break;
						}
					}
					towerinfo();
				}
				break;	
		}
		return true;
	}
//绘图
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		if(sendnowcool>0)sendnowcool-=Utils.getDtMs();
		else if(sendnowcool!=-1000)
		{
			removeGUI("sendnowmask");
			sendnowcool=-1000;
		}
		c.drawBitmap(background,0,0,p);
		for(Tower t:towers)
		{
			c.drawBitmap(towersicon[t.id],t.x*tilew,t.y*tilew,p);
		}
		if(tmptower!=null)
		{
			p.setStyle(Paint.Style.STROKE);
			p.setStrokeWidth(tilew/10f);
			p.setStrokeMiter(tilew/10f);
			p.setColor(0xff000000);
			c.drawCircle(tmptower.x*tilew+tilew/2,tmptower.y*tilew+tilew/2,tmptower.r*tilew,p);
			p.setStyle(Paint.Style.FILL);
			c.drawBitmap(towersicon[tmptower.id],tmptower.x*tilew,tmptower.y*tilew,p);
		}
		for(Bug b:bugs)
		{
			Bitmap bm=bugsicon[b.id];
			c.drawBitmap(bm,b.x*bm.getWidth(),b.y*bm.getHeight(),p);
		}
		for(int i=0;i<map.length;i++)
		{
			for(int j=0;j<map[i].length;j++)
			{
				int id=map[i][j];
				if(map[i][j]!=0)c.drawBitmap(tiles[id],i*tilew+tilew/2-tiles[id].getWidth()/2,j*tilew+tilew-tiles[id].getHeight(),p);
			}
		}
		for(Point pp:wpwaypoint.get(0))
			c.drawBitmap(tiles[5],pp.x*tilew,pp.y*tilew,p);
		if(nextwave!=null)
		{
			waveVec.shapes.get(7).txt=String.format("×%d",nextwave.count);
			waveVec.shapes.get(8).txt=String.format("%d秒",(int)Math.round(nextwave.sec/1000));
			nextwave.sec-=Utils.getDtMs();
			if(nextwave.sec<=0)nextwave=null;
		}
		else if(curwave<waves.size()){
			nextwave=waves.get(curwave++);
		}
		else{
			waveVec.shapes.get(7).txt="没有啦";
			waveVec.shapes.get(8).txt="(´・ω・`)";
			
		}
		super.onDraw(c);
	}
	//右上角信息
	private void towerinfo()
	{
		for(TowerInfo x:towerInfo)x.exitAnim();
		if(tmptower!=null)
		{
			TowerInfo t=new TowerInfo("tinfo");
			t.id+=t.hashCode();
			Utils.loadScene(t);
			towerInfo.add(t);
		}
	}
	//载入所有材质
	public void loadTextures() throws Exception
	{
		tiles[0]=null;
		tiles[1]=tile("wall");
		tiles[2]=tile("tree",1.8f);
		tiles[3]=tile("start");
		tiles[4]=tile("finish");
		tiles[5]=tile("waypoint");
		tiles[6]=tile("tallwall",1.5f);
		background=VECfile.createBitmap(backvec,(int)(tilew*map.length),(int)(tilew*map[0].length));
		for(int i=0;i<towersicon.length;i++)towersicon[i]=VECfile.createBitmap(Utils.ctx,"towers/"+i,(int)tilew,(int)tilew);
		for(int i=0;i<bugsicon.length;i++)bugsicon[i]=VECfile.createBitmap(Utils.ctx,"bugs/"+i,(int)tilew,(int)tilew);
		//mapcanvas=new Canvas[2];
		//mapcache=new Bitmap[2];
		/*for(int i=0;i<2;i++)
		 {
		 mapcache[i]=Bitmap.createBitmap(background.getWidth(),background.getHeight(),Bitmap.Config.ARGB_8888);
		 mapcanvas[i]=new Canvas(mapcache[i]);
		 }*/
	}
	//找寻所有可能的路点
	public ArrayList<Point> findAllPossibleWayPoints()
	{
		ArrayList<Point> po=new ArrayList<Point>();
		for(int x=0;x<map.length;x++)
			for(int y=0;y<map[x].length;y++)
			{
				if(!isWall(x,y))continue;
				else
				{
					if(!isWall(x-1,y-1)&&!isWall(x,y-1)&&!isWall(x-1,y))po.add(new Point(x-1,y-1));
					if(!isWall(x,y-1)&&!isWall(x+1,y-1)&&!isWall(x+1,y))po.add(new Point(x+1,y-1));
					if(!isWall(x-1,y)&&!isWall(x-1,y+1)&&!isWall(x,y+1))po.add(new Point(x-1,y+1));
					if(!isWall(x+1,y)&&!isWall(x,y+1)&&!isWall(x+1,y+1))po.add(new Point(x+1,y+1));
				}
			}
		return po;
	}
	//设置地图大小
	public void setSize(int xsize,int ysize)
	{
		map=new int[xsize][ysize];
	}
//填充地图数据
	private void fillData(int y,String line)
	{
		//if(line.length()!=)return;
		for(int i=0;i<line.length();i++)
		{
			int id=Integer.parseInt(line.substring(i,i+1));
			map[i][y]=id;
		}
	}
	//寻路算法
	private boolean findWayPoint()
	{
		ArrayList<ArrayList<Point>> wpwaypointt=new ArrayList<ArrayList<Point>>();
		ArrayList<Point> possible=findAllPossibleWayPoints();
		for(Point[] asp:wpmap)//寻找对应路点，刷出顺序:1,2,3…
		{
			ArrayList<Point> sort=sortWayPoints(possible, asp),opt=null;
			if(sort==null||sort.size()==0)return false;
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

		for(ArrayList<Point>m:wpwaypointt)wpwaypoint.add(m);
		return true;
	}
	//选择排序
	private ArrayList<Point> sortWayPoints(ArrayList<Point> possible, Point[] asp)
	{
		ArrayList<Point> mpossible=new ArrayList<Point>();
		ArrayList<Point> sort=new ArrayList<Point>();
		ArrayList<Point> reach=new ArrayList<Point>();
		mpossible.addAll(possible);
		mpossible.add(asp[1]);
		final Point start=asp[0];
		final Point finish=asp[1];
		Point p=start;
		sort.add(start);
		int findcount=0;
		while(p!=finish&&findcount++<1000)
		{
			for(Point p1:mpossible)
				if(reachable(p,p1))reach.add(p1);
			final Point g=p;
			Collections.sort(reach,new Comparator<Point>(){
					@Override
					public int compare(Point p1, Point p2)
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
						float f1=g(p1.x,p1.y,start.x,start.y)+h(p1.x,p1.y,finish.x,finish.y)-hastower(p1.x,p1.y);
						float f2=g(p2.x,p2.y,start.x,start.y)+h(p2.x,p2.y,finish.x,finish.y)-hastower(p2.x,p2.y);
						return f1>f2?1:-1;
					}
					public float hastower(int x,int y)
					{
						float to=0;
						for(Tower t:towers)
						{
							if(x+1==t.x&&y+1==t.y)to++;
							if(x+1==t.x&&y-1==t.y)to++;
							if(x-1==t.x&&y-1==t.y)to++;
							if(x-1==t.x&&y+1==t.y)to++;
						}
						return to;
					}
					public float h(float x1,float y1,float x2,float y2)
					{
						return 1.5f*(float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
					}
					public float g(float x1,float y1,float x2,float y2)
					{
						return (float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
					}
				});
			if(reach.size()==0)
			{
				sort.remove(p);
				if(sort.size()-1<0)return null;
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
	//优化多余路点
	private ArrayList<Point> optimize(ArrayList<Point> c)
	{
		ArrayList<Point> u=new ArrayList<Point>();
		Point a=c.get(0),d=null;
		u.add(a);
		for(int i=c.size()-1;i>=0;i--)
		{
			Point b=c.get(i);
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
	private boolean isWall(int x,int y)
	{
		if(x<0||y<0||x>=map.length||y>=map[x].length)return true;
		int id=map[x][y];
		if(id!=0&&id!=3&&id!=4&&id!=5)return true;
		for(Tower t:towers)if((int)t.x==x&&(int)t.y==y)return true;
		return false;
	}
	private boolean reachable(Point a,Point b)
	{
		int x1=Math.min(a.x,b.x);
		int x2=Math.max(a.x,b.x);
		int y1=Math.min(a.y,b.y);
		int y2=Math.max(a.y,b.y);
		if(y2-y1==0)
		{
			for(int x=x1;x<=x2;x++)
				if(isWall(x,y1))return false;
		}
		else if(x2-x1==0)
		{
			for(int y=y1;y<=y2;y++)
				if(isWall(x1,y))return false;
		}
		else
		{
			for(int x=x1;x<=x2;x++)
				for(int y=y1;y<=y2;y++)
					if(isWall(x,y))
					{
						float vx=b.x-a.x,vy=b.y-a.y;
						//检测的向量
						Vector v=new Vector(a.x,a.y,-vy,vx).normalize();//法向量
						//法向量
						Vector q=new Vector(a.x,a.y,0.5f,0.5f);
						Vector w=new Vector(a.x,a.y,-0.5f,0.5f);
						Vector e=new Vector(a.x,a.y,0.5f,-0.5f);
						Vector r=new Vector(a.x,a.y,-0.5f,-0.5f);//顶点向量
						float rr=(float)Math.sqrt(0.5);
						float min=Math.min(q.cos(v),Math.min(w.cos(v),Math.min(e.cos(v),r.cos(v))))*rr;
						float max=Math.max(q.cos(v),Math.max(w.cos(v),Math.max(e.cos(v),r.cos(v))))*rr;
						//自身投影
						Vector t=new Vector(a.x,a.y,x-a.x+0.5f,y-a.y+0.5f);
						Vector o=new Vector(a.x,a.y,x-a.x-0.5f,y-a.y+0.5f);
						Vector u=new Vector(a.x,a.y,x-a.x+0.5f,y-a.y-0.5f);
						Vector i=new Vector(a.x,a.y,x-a.x-0.5f,y-a.y-0.5f);//顶点向量
						float min2=Math.min(t.cos(v)*t.mod(),Math.min(o.cos(v)*o.mod(),Math.min(u.cos(v)*u.mod(),i.cos(v)*i.mod())));
						float max2=Math.max(t.cos(v)*t.mod(),Math.max(o.cos(v)*o.mod(),Math.max(u.cos(v)*u.mod(),i.cos(v)*i.mod())));
						if(max2>=min&&max2<=max||min2>=min&&min2<=max)return false;
					}
		}
		return true;
	}
	//载入地图材质
	private Bitmap tile(String name) throws Exception
	{
		int s=(int)tilew;
		return VECfile.createBitmap(Utils.ctx,"tiles/"+name,s,s);
	}
	private Bitmap tile(String name,float f) throws Exception
	{
		int s=(int)(tilew*f);
		return VECfile.createBitmap(Utils.ctx,"tiles/"+name,s,s);
	}
	//读取地图
	private void loadMap(String st)throws Exception
	{
		int mapi=-1;
		boolean fw=false,wp=false;
		String[] fg=st.split("\n");
		for(String l:fg)
		{
			if(l.startsWith("#"))continue;
			else if(l.startsWith("name:"))name=(l.substring(5));
			else if(l.startsWith("size:"))
			{
				String[] p=l.substring(5).split("x");
				setSize(Integer.parseInt(p[0]),Integer.parseInt(p[1]));
			}
			else if(l.startsWith("money:"))money=(Integer.parseInt(l.substring(6)));
			else if(l.startsWith("lives:"))lives=(Integer.parseInt(l.substring(6)));
			else if(l.startsWith("background:"))
			{
				String b=l.substring(11);
				if(b.startsWith("@"))backvec=VECfile.readFileFromIs(Utils.ctx.getAssets().open(b.substring(1)));
				else backvec=VECfile.readFile(Utils.mainDir+"地图/"+name+"/"+b);
			}
			else if(l.startsWith("map:"))mapi=0;
			else if(l.startsWith("mapend"))mapi=-1;
			else if(l.startsWith("wave:"))fw=true;
			else if(l.startsWith("waveend"))fw=false;
			else if(l.startsWith("wpmap:"))wp=true;
			else if(l.startsWith("wpmapend"))wp=false;
			else if(mapi!=-1)fillData(mapi++,l);
			else if(fw)waves.add(new Wave(l));
			else if(wp)
			{
				String[] f=l.split(" ");
				Point a=new Point(Integer.parseInt(f[0]),Integer.parseInt(f[1]));
				Point b=new Point(Integer.parseInt(f[2]),Integer.parseInt(f[3]));
				wpmap.add(new Point[]{a,b});
			}
		}
	}
	//每波攻击
	private static class Wave
	{
		int id,count;
		float sec;
		//float cd;
		public Wave(String pas)
		{
			String[] f=pas.split(" ");
			id=Integer.parseInt(f[0]);
			count=Integer.parseInt(f[1]);
			sec=Integer.parseInt(f[2])*1000f;
		}
	}
	//右上角信息
	private class TowerInfo extends Scene
	{
		public TowerInfo(String id)
		{
			super(id);
			try
			{
				loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gametowerinfo.txt"),Integer.toString(tmptower.id));
				Ui u=findUi("gametowerinfo");
				VECfile v=u.setVecRealTimeRender(true);
				CopyOnWriteArrayList<Shape> hs=v.getShapes();
				hs.get(1).txt="范围:"+Integer.toString((int)(tmptower.r*10f));
				hs.get(2).txt="伤害:"+Integer.toString((int)tmptower.dmg);
				hs.get(3).txt="攻速:"+Integer.toString((int)(10f/tmptower.dtime));

				v=VECfile.readFileFromIs(Utils.ctx.getAssets().open("towers/"+tmptower.id+".vec"));
				u=findUi("gametowerinfoicon");
				u.bmp=VECfile.createBitmap(v,(int)u.width,(int)u.height);

				if(towers.contains(tmptower))
				{
					Ui up=findUi("gameupgrade");
					up.show=true;
					up.setVecRealTimeRender(true).getShapes().get(1).txt="升级:"+Integer.toString((int)tmptower.moneys[tmptower.id]);

					Ui se=findUi("gamesell");
					se.show=true;
					se.setVecRealTimeRender(true).getShapes().get(1).txt="出售:"+Integer.toString((int)tmptower.moneys[tmptower.id]);
				}
				else
				{
					findUi("gameupgrade").show=false;
					findUi("gamesell").show=false;
				}
			}

			catch(Throwable e)
			{
				Utils.alert(e);
			}
		}
	}
}
