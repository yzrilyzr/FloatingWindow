package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.io.*;
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
	private boolean gamex2=false,pause=false,gameend=false;
	//绘图数据
	public Bitmap background=null;
	public VECfile backvec;
	VECfile waveVec;
	Ui nextwaveui;
	int[][] map=null;
	Tower tmptower=null;
	float sendnowcool=-1000;
	float tilew;
	Matrix ma=new Matrix();
	int mapid=0;
	public CopyOnWriteArrayList<TowerInfo> towerInfo=new CopyOnWriteArrayList<TowerInfo>();
	public GameMain(String id,int mapid,String data)
	{
		super(id);
		try
		{
			this.mapid=mapid;
			p.setTextSize(Utils.px(15));
			loadMap(data);
			tilew=Utils.getHeight()/map[0].length;
			loadTextures();
			findWayPoint();
			loadGUIPath("GUI/game/gamemain.txt");
			waveVec=findUi("rightmenu").setVecRealTimeRender(true);
			nextwaveui=findUi("nextwavebug");
			int[] ids=new int[]{0,3,1,4,2,5,6,7,8,9};
			for(int ip=0;ip<10;ip++)	
			{
				String r=Integer.toString(ip);
				loadGUIPath("GUI/game/gamerightmenuitems.txt",r,(ip%2==0?"0":"100s"),(ip/2)*100+"s");
				Ui u=findUi("rightmenuitem"+r);
				VECfile v=u.getVec();
				//VECfile.readFileFromIs(Utils.ctx.getAssets().open("vec/game/rightmenuitems.vec"));
				CopyOnWriteArrayList<Shape> hs=v.getShapes();
				hs.get(0).txt="$"+Integer.toString(Tower.moneys[ip]);
				u.reDrawVecBmp();
				//u.vec=v;
				//u.bmp=VECfile.createBitmap(v,(int)u.width,(int)u.height);
				if(Data.unlocktower<ids[ip])u.show=false;
			}
		}
		catch (Exception e)
		{
			Utils.alert(e);
			exitAnim();
			Utils.loadScene(new LevelSelect("levelselect"));
			return;
		}

	}
	//购买塔，右边列表
	public void buytower(Ui s)
	{
		String id=s.id.replaceAll("(item|rightmenuitem)","");
		tmptower=new Tower(Integer.parseInt(id));
		tmptower.x=-99;
		tmptower.y=-99;
		if(money-tmptower.money<0)tmptower=null;
		towerinfo();
	}
	public void x2(Ui s)
	{
		Ui x1=findUi("gamex1");
		gamex2=!gamex2;
		x1.show=gamex2;
		findUi("gamex2").resetAnim();
	}
	public void pause(Ui s)
	{
		Ui con=findUi("gamecontinue");
		pause=!pause;
		con.show=pause;
		findUi("gamepause").resetAnim();
	}
	//设置按钮
	public void setting(Ui s)
	{
		final boolean isp=pause;
		Data.save();
		Utils.loadScene(new Settings("settings"){
				@Override
				public void close(Ui s)
				{
					super.close(s);
					pause=isp;
				}
			});
		pause=true;
		s.resetAnim();
	}
	//现在出动
	public void sendnow(Ui s)
	{
		if(nextwave!=null)nextwave.sec=0;
		loadGUIPath("GUI/game/gamemain_sendnow.txt");
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
					boolean cbug=false;
					for(Bug b:bugs)
					{
						if(b.contains(tmptower.x,tmptower.y,0.5f))
						{
							cbug=true;
							break;
						}
					}
					if(!cbug&&!isWall((int)tmptower.x,(int)tmptower.y))
					{
						//放置塔
						towers.add(tmptower);
						if(!findWayPoint())
						{
							towers.remove(tmptower);
							tmptower=null;
						}
						else money-=tmptower.money;
						//tmptower=null;
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
		//立即出发的ui
		if(sendnowcool>0)sendnowcool-=Utils.getDtMs();
		else if(sendnowcool!=-1000)
		{
			removeGUI("sendnowmask");
			sendnowcool=-1000;
		}
		c.drawBitmap(background,0,0,p);
		float dt=Utils.getDtMs();
		if(gamex2)dt*=2;
		if(pause||gameend)dt=0;
		//画塔
		for(Tower t:towers)
		{
			ma.reset();
			ma.postTranslate(t.x*tilew,t.y*tilew);
			float sc=1+0.3f*t.cdtime/t.dtime;
			ma.preScale(sc,sc,tilew/2,tilew/2);
			c.drawBitmap(towersicon[t.id],ma,p);
			ma.reset();
			if(t.cdtime>0)t.cdtime-=dt;
			//bug摧毁塔 塔攻击bug
			t.inRbugs.clear();
			for(Bug b:bugs)
			{
				//bug摧毁塔
				if(b.contains(t.x,t.y,0.45f))
				{
					towers.remove(t);
					if(t==tmptower)
					{
						tmptower=null;
						towerinfo();
					}
					continue;
				}
				//加入攻击队列
				if(b.contains(t.x,t.y,t.r)&&t.inRbugs.size()<t.inRBugCount)
				{
					t.inRbugs.add(b);
				}
			}
			//攻击
			if(t.cdtime<=0&&t.inRbugs.size()>0)
			{
				for(Bug b:t.inRbugs)
				{
					b.frztime=Math.max(b.frztime,t.frztime[t.id]);
					if(t.attacktype==Tower.AttackType.INSTANT)
					{
						b.hp-=t.dmg;
						b.score+=t.money/20;
						Bullet et=new Bullet(b,t);
						bullets.add(et);
					}
					else
					{
						Bullet et=new Bullet(b,t);
						bullets.add(et);
					}
				}
				t.cdtime=t.dtime;
			}
		}
		//画选中的塔
		if(tmptower!=null)
		{
			p.setStyle(Paint.Style.STROKE);
			p.setStrokeWidth(tilew/10f);
			p.setStrokeMiter(tilew/10f);
			p.setColor(0xff000000);
			c.drawCircle(tmptower.x*tilew+tilew/2,tmptower.y*tilew+tilew/2,tmptower.r*tilew,p);
			p.setStyle(Paint.Style.FILL);
			if(!towers.contains(tmptower))c.drawBitmap(towersicon[tmptower.id],tmptower.x*tilew,tmptower.y*tilew,p);
		}
		//画地图
		for(int i=0;i<map.length;i++)
		{
			for(int j=0;j<map[i].length;j++)
			{
				int id=map[i][j];
				if(map[i][j]!=0)c.drawBitmap(tiles[id],i*tilew+tilew/2-tiles[id].getWidth()/2,j*tilew+tilew-tiles[id].getHeight(),p);
			}
		}

		//画bug
		for(Bug b:bugs)
		{
			//死亡的bug
			if(b.hp<=0)
			{
				bugs.remove(b);
				money+=b.money;
				score+=b.score;
				Data.tmoney+=b.money;
				Data.tscore+=b.score;
				Data.tbugs++;
				Data.exp+=b.exp;
				if(Data.exp>=Data.getNextlevelExp())
				{
					Data.exp=0;
					Data.level++;
					Utils.loadScene(new LevelUp("levelup"+Data.level));
					Data.save();
				}
				continue;
			}
			//冻结行动时间
			if(b.frztime>0)
			{
				b.frztime-=dt;
				b.vel=0;
			}
			else b.vel=Bug.vels[b.id];
			Bitmap bm=bugsicon[b.id];
			//选择路点组
			ArrayList<Point> wp=wpwaypoint.get(b.wayIndex);
			//这个 已 经过路点 和 它的index  和 目标路点
			Point ppc=b.curPoint;
			int ind=wp.indexOf(ppc);
			float vx=0,vy=0,r=0;
			//到结束点
			if(ind==wp.size()-1)
			{
				//扣生命值
				lives--;
				bugs.remove(b);
				Data.tlives++;
				Data.save();
				if(lives==0)
				{
					gameend();
				}
			}
			else
			{
				//寻找距离最近可通过路点
				Point pp=null;
				if(ind!=-1)pp=wp.get(ind+1);
				else
				{
					float mind=-1;
					for(int i=wp.size()-1;i>=0;i--)
					{
						Point a=wp.get(i);
						float dd=(float)Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
						if(mind==-1)
						{
							mind=dd;
							continue;
						}
						if(dd<mind)
						{
							ind=i;
							mind=dd;
							break;
						}
					}
					if(ind==-1)ind=0;
					pp=wp.get(ind);
				}
				//是否到达目标路点
				if(b.contains(pp.x,pp.y,0.05f))b.curPoint=pp;
				//↑↑↑↑初始化的时候直接向第二个路点走,cur=1;
				//前往目标
				//正交距离除以直线距离，即为sin和cos值，vx+=v*cos()，vy+=v*sin()
				r=(float)Math.sqrt(Math.pow(pp.x-b.x,2)+Math.pow(pp.y-b.y,2));
				b.x+=(vx=(pp.x-b.x))/r*b.vel*dt/3000f;
				b.y+=(vy=(pp.y-b.y))/r*b.vel*dt/3000f;
			}
			ma.reset();
			ma.preRotate((float)(90f+180f*(float)Utils.getArc(vx,vy,r)/Math.PI),tilew/2,tilew/2);
			ma.postTranslate(b.x*bm.getWidth(),b.y*bm.getHeight());
			c.drawBitmap(bm,ma,p);
			//血条
			p.setColor(0xffaaaaaa);
			c.drawRect(b.x*bm.getWidth(),(b.y-0.1f)*bm.getHeight(),(1+b.x)*bm.getWidth(),b.y*bm.getHeight(),p);
			p.setColor(0xff20ff20);
			c.drawRect(b.x*bm.getWidth(),(b.y-0.1f)*bm.getHeight(),b.x*bm.getWidth()+bm.getWidth()*b.hp/b.maxhp,b.y*bm.getHeight(),p);
		}
		//画子弹
		for(Bullet bu:bullets)
		{
			bu.onDraw(c,tilew,dt);
			if(bu.brtime!=-1)
				if(bu.brtime>0)bu.brtime-=dt;
				else bullets.remove(bu);
			if(bu.branimtime!=-1)
				if(bu.branimtime>0)bu.branimtime-=dt;
				else bullets.remove(bu);
		}
		//画路点
		//for(Point pp:wpwaypoint.get(0))
		//	c.drawBitmap(tiles[5],pp.x*tilew,pp.y*tilew,p);
		if(nextwave!=null)
		{
			//下一波倒计时
			waveVec.shapes.get(7).txt=String.format("×%d",nextwave.count);
			waveVec.shapes.get(8).txt=String.format("%d秒",(int)Math.floor(nextwave.sec/1000));
			waveVec.shapes.get(9).txt=String.format("%d/%d波",curwave-1,waves.size());
			nextwave.sec-=dt;
			if(nextwave.sec<=0)nextwave=null;
		}
		else if(curwave<waves.size())
		{
			//下一波
			nextwave=waves.get(curwave++);
			try
			{
				nextwaveui.bmp=VECfile.createBitmap(Utils.ctx,String.format("bugs/%s",nextwave.id),(int)nextwaveui.width,(int)nextwaveui.height);
			}
			catch (Exception e)
			{
				Utils.alert(e);
			}
			Data.save();
		}
		else
		{
			//波完
			nextwaveui.show=false;
			waveVec.shapes.get(7).txt="没有啦";
			waveVec.shapes.get(8).txt="(´・ω・`)";
			waveVec.shapes.get(9).txt="出完啦～";
			if(bugs.size()==0)
			{
				boolean oc=true;
				for(Wave w:waves)
					if(w.count>0)
					{
						oc=false;
						break;
					}
				if(oc&&!gameend)gameend();
			}
		}
		//刷出bug
		for(int i=0;i<curwave;i++)
		{
			Wave cur=waves.get(i);
			if(cur.sec<=0&&cur.count>0)
			{
				//到达间隔时间
				if(cur.cd<=0)
				{
					cur.cd=700;
					cur.count--;
					Bug b=new Bug(cur.id);
					//选择路点组
					b.wayIndex=cur.wpindex;
					//获取出生点
					if(b.wayIndex>=wpwaypoint.size())
						throw new IndexOutOfBoundsException(String.format("第%d波设置的出生点组%d不符合要求(0～%d)",i,b.wayIndex,wpwaypoint.size()-1));
					Point yu=wpwaypoint.get(b.wayIndex).get(0);
					//设置到出生点
					b.x=yu.x;
					b.y=yu.y;
					//设置目标到路点
					b.curPoint=wpwaypoint.get(b.wayIndex).get(0);

					bugs.add(b);
				}
				else cur.cd-=dt;
			}
		}
		//	p.setColor(0xffff0000);
		waveVec.shapes.get(10).txt=String.format("分数：%d",score);
		waveVec.shapes.get(11).txt=String.format("生命：%d",lives);
		waveVec.shapes.get(12).txt=String.format("金钱：%d",money);
		waveVec.shapes.get(13).txt=String.format("等级\n%d",Data.level);
		ArrayList<Point> pts=waveVec.shapes.get(15).pts;
		Point lt=pts.get(0),rb=pts.get(1),ed=pts.get(3);
		int nexp=Data.getNextlevelExp();
		int cx=(rb.x-lt.x)/2;
		ed.set(
			lt.x+cx+(int)(100f*cx*Math.sin(2f*(float)Data.exp/(float)nexp*Math.PI)),
			lt.y+cx+(int)(100f*-cx*Math.cos(2f*(float)Data.exp/(float)nexp*Math.PI)));

		//c.drawText(String.format("分数%d   生命：%d  金钱:%d",score,lives,money),0,p.getTextSize(),p);
		super.onDraw(c);
	}

	private void gameend()
	{
		gameend=true;
		if(lives>0)
		{
			if(mapid==Data.unlockmap)Data.unlockmap++;
			if(score>Data.scores[mapid])Data.scores[mapid]=score;
		}
		Data.save();
		Utils.unloadAll();
		Utils.loadScene(new LevelSelect("levelselect"));
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
			//对可能路点 按组 进行排序
			ArrayList<Point> sort=sortWayPoints(possible, asp),opt=null;
			//寻路失败 空数组或没有开始结束点
			if(sort==null||sort.size()<2)return false;
			//优化
			while((opt=optimize(sort)).size()<sort.size())sort=opt;
			//加入该组
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
		final Point start=asp[0];
		final Point finish=asp[1];
		mpossible.addAll(possible);
		if(mpossible.contains(start))mpossible.remove(start);
		if(!mpossible.contains(finish))mpossible.add(finish);
		Point p=start;
		sort.add(start);
		int findcount=0;
		while(p!=finish&&findcount++<1000)
		{
			for(Point p1:mpossible)
				if(reachable(p,p1))reach.add(p1);
			//final Point g=p;
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
			//到达结束点
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
		//从起点出发
		Point a=c.get(0);
		//终点
		Point finish=c.get(c.size()-1);
		//加入起点
		u.add(a);
		//从后往前
		for(int i=c.size()-1;i>=0;i--)
		{
			Point b=c.get(i);
			//b可以到达
			if(reachable(a,b))
			{
				//加入b
				u.add(b);
				a=b;//到b点
				//结束点就停止
				if(a==finish)break;
				//重置指针
				i=c.size();
			}
		}
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
		else if(x2-x1==y2-y1)
		{
			for(int d=0;d<=x2-x1;d++)
				if(isWall(x1+d,y1+d))return false;
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
	//id count sec wpindex
	private static class Wave
	{
		//bug 的id  数量  在第几个传送点组
		int id,count,wpindex;
		//倒计时
		float sec;
		float cd=0;
		public Wave(String pas)
		{
			String[] f=pas.split(" ");
			id=Integer.parseInt(f[0]);
			count=Integer.parseInt(f[1]);
			sec=Integer.parseInt(f[2])*1000f;
			if(f.length>=4)wpindex=Integer.parseInt(f[3]);
			else wpindex=0;
			if(sec<1000)sec=1000;
		}
	}

	//升级的界面
	private class LevelUp extends Scene
	{
		//CopyOnWriteArrayList<Shape> hs,hs1,hs2;
		public LevelUp(String id)
		{
			super(id);
			try
			{
				int[] ids=new int[]{1,3,5,6,7,8,9,9};
				pause=true;
				loadGUIPath("GUI/game/gamelevelup.txt",
					Data.unlocktower<9?Integer.toString(ids[Data.unlocktower-2]):"0",
					Data.unlocktower<9?"":"#",
					Data.unlocktower>=9?"money":"tower",
					Data.unlocklevel>=4?"money":"towerup"
				);
				Ui u=findUi("leveluptower");
				if(Data.unlocktower<9)
				{
					StringBuilder sb=new StringBuilder();
					String g=null;
					BufferedReader br=new BufferedReader(new InputStreamReader(Utils.ctx.getAssets().open("towers/info.txt")));
					while((g=br.readLine())!=null)sb.append(g).append("\n");
					br.close();
					String[] de=sb.toString().split("\n");
					VECfile v=u.setVecRealTimeRender(true);
					String gp=de[ids[Data.unlocktower-2]].replaceAll("\\\\n","\n");
					v.getShapes().get(2).txt=gp.substring(0,gp.indexOf("\n"));
				}
			}
			catch(Throwable e)
			{
				Utils.alert(e);
			}
		}
		public void money(Ui s)
		{
			int m=Utils.random(100,1000);
			money+=m;
			Data.tmoney+=m;
			Data.save();
			exitAnim();
			pause=false;
		}
		public void tower(Ui s)
		{
			if(Data.unlocktower>=9)
			{
				money(s);
				return;
			}
			Data.unlocktower++;
			Data.save();
			exitAnim();
			pause=false;
			int[] ids=new int[]{0,3,1,4,2,5,6,7,8,9};
			for(int ip=0;ip<10;ip++)	
			{
				String r=Integer.toString(ip);
				Ui u=GameMain.this.findUi("rightmenuitem"+r);
				if(Data.unlocktower>=ids[ip])u.show=true;
			}
		}
		public void towerup(Ui s)
		{
			if(Data.unlocklevel>=4)
			{
				money(s);
				return;
			}
			Data.unlocklevel++;
			Data.save();
			exitAnim();
			pause=false;
		}
	}
	//右上角信息
	private class TowerInfo extends Scene
	{
		CopyOnWriteArrayList<Shape> hs,hs1,hs2;
		public TowerInfo(String id)
		{
			super(id);
			try
			{
				loadGUIPath("GUI/game/gametowerinfo.txt",Integer.toString(tmptower.id));
				Ui u=findUi("gametowerinfo");
				VECfile v=u.setVecRealTimeRender(true);
				hs=v.getShapes();
				//hs.get(1).txt="范围:"+Integer.toString((int)(tmptower.r*10f));
				//hs.get(2).txt="伤害:"+Integer.toString((int)tmptower.dmg);
				//hs.get(3).txt="攻速:"+Integer.toString((int)(10000f/tmptower.dtime));

				v=VECfile.readFileFromIs(Utils.ctx.getAssets().open("towers/"+tmptower.id+".vec"));
				u=findUi("gametowerinfoicon");
				u.bmp=VECfile.createBitmap(v,(int)u.width,(int)u.height);

				if(towers.contains(tmptower))
				{
					Ui up=findUi("gameupgrade");
					up.show=true;
					hs1=up.setVecRealTimeRender(true).getShapes();//.get(1).txt=tmptower.getUpgradeMoney()==-1?"最大等级":"升级:"+Integer.toString(tmptower.getUpgradeMoney());

					Ui se=findUi("gamesell");
					se.show=true;
					hs2=se.setVecRealTimeRender(true).getShapes();//get(1).txt="出售:"+Integer.toString(tmptower.getSellMoney());
				}
				else
				{
					findUi("gameupgrade").show=false;
					findUi("gamesell").show=false;
				}
				update();
			}
			catch(Throwable e)
			{
				Utils.alert(e);
			}
		}
		public void towerupgrade(Ui s)
		{
			int m=tmptower.getUpgradeMoney();
			if(m!=-1&&money-m>=0)
			{
				money-=m;
				tmptower.levelup();
			}
			update();
		}
		private void update()
		{
			if(hs1!=null)hs1.get(1).txt=tmptower.getUpgradeMoney()==-1?"最大等级":"升级:"+Integer.toString(tmptower.getUpgradeMoney());
			if(hs2!=null)hs2.get(1).txt="出售:"+Integer.toString(tmptower.getSellMoney());
			hs.get(1).txt="范围:"+Integer.toString((int)(tmptower.r*10f));
			hs.get(2).txt="伤害:"+Integer.toString((int)tmptower.dmg);
			hs.get(3).txt="攻速:"+Integer.toString((int)(10000f/tmptower.dtime));
		}
		public void towersell(Ui s)
		{
			money+=tmptower.getSellMoney();
			towers.remove(tmptower);
			tmptower=null;
			exitAnim();
		}
	}
}
