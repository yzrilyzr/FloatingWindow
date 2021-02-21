package com.yzrilyzr.bugs.Game;
import android.content.*;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.bugs.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;

import com.yzrilyzr.bugs.MainActivity;

public class Tower extends GObj
{
	//威力 攻击间隔 计算的攻击间隔
	public float dmg,dtime,cdtime;
	public int money;
	//float rdmg,rtime,rdtime,brtime;
	//float money,slow;
	//VECfile ico;
	//Bitmap bico;
	public int level=0;
	//Bug target;
	//Matrix ma=new Matrix();
	public int id;
	//攻击模式
	public int attacktype=0;
	//范围攻击用
	public int inRBugCount=1;
	public CopyOnWriteArrayList<Bug> inRbugs=new CopyOnWriteArrayList<Bug>();
	//range r
	public static final int[] moneys=new int[]{50,80,170,200,100,300,350,400,350,350};
	public static final float[] dmgs=new float[]{20,7,36,50,10,100,15,50,40,40};
	public static final float[] rs=new float[]{1.5f,1.4f,1.9f,2.5f,1.25f,2f,1.3f,2.5f,1.4f,1.4f};
	public static final float[] dts=new float[]{0.9f,0.2f,1f,1.5f,0.25f,1f,0.4f,1.5f,1.3f,1.3f};
	public static final float[] frztime=new float[]{0,0,0,150,50,500,0,150,100,700};
	public static final int[] attacktypes=new int[]{
		AttackType.BULLET,
		AttackType.BULLET,
		AttackType.BULLET,
		AttackType.PROJECTILE,
		AttackType.INSTANT,
		AttackType.INSTANT,
		AttackType.BULLET,
		AttackType.PROJECTILE,
		AttackType.INSTANT,
		AttackType.INSTANT
	};
	public static final int[] inRBugAtt=new int[]{1,1,1,1,3,1,1,1,15,15};
	public Tower(int id)
	{
		this.id=id;
		try
		{
			//Context c=Utils.ctx;
			//int s=(int)MainActivity.map.tilew;
			//ico=VECfile.readFileFromIs(c.getAssets().open("towers/"+id+".vec"));
			//bico=VECfile.createBitmap(ico,s,s);
			dtime=dts[id]*1000f;
			//r=rs[id];
			dmg=dmgs[id];
			money=moneys[id];
			r=rs[id];
			inRBugCount=inRBugAtt[id];
			attacktype=attacktypes[id];
		}
		catch(Throwable e)
		{
			//MainActivity.toast(e);
		}
	}
	public int getUpgradeMoney()
	{
		if(level==Data.unlocklevel)return -1;
		return (int)(money/1.5f);
	}
	public int getSellMoney()
	{
		return (int)(money*0.6f);
	}
	public void levelup()
	{
		level++;
		level=Utils.limit(level,0,Data.unlocklevel);
		money=(int)(moneys[id]*Math.pow(1.3,level));
		dmg=(float)(dmgs[id]*Math.pow(1.15,level));
		r=(float)(rs[id]*Math.pow(1.1,level));
		dtime=(float)(dts[id]*1000f*Math.pow(0.9,level));
	}
	/*public void onDraw(Canvas c,Bug b){
		if(id==4){
			p.setPathEffect(new DiscretePathEffect(50,50));
			c.drawLine(x,y,b.x,b.y,p);
		}
	}
	
	/*public void compute(float dt)
	{
		/*Map map=MainActivity.map;
		 float tilew=map.tilew;
		 if(cdtime>0)cdtime-=dt;
		 inRbugs.clear();
		 for(Bug s:map.bugs)
		 {
		 if(!inRange(s))continue;
		 inRbugs.add(s);
		 if(target==null)target=s;			
		 }
		 if(target==null||target.hp<=0||!inRange(target))
		 {
		 target=null;
		 return;
		 }
		 if(cdtime<=0&&target!=null)
		 {
		 cdtime=dtime*(float)Math.pow(1.1,-level);
		 attack();
		 }
	}
	/*public void attack()
	{
		//target.hp-=dmg*Math.pow(1.1,level);
		//for(Bug rg:inRbugs)rg.hp-=dmg/6*Math.pow(1.1,level);
		//if(target==s)
		{//单一目标
			//直接或范围
			//定向范围
			//~
			//map.bullets.add(new Bullet(s,this,tilew*0.7f,tilew*0.5f,brtime));
			//子弹
			//map.bullets.add(new Bullet(s,this,tilew*0.1f,tilew*2,-1));
			//远定
			//map.bullets.add(new Bullet(s,this,tilew*1.5f,tilew*0.5f,-1));
		}
	}
	public boolean inRange(Bug b)
	{
		float dx=b.x-x,dy=b.y-y;
		return dx*dx+dy*dy<r*r*Math.pow(1.1,level)*Math.pow(1.1,level);
	}
	/*@Override
	 public void onDraw(Canvas c)
	 {
	 // TODO: Implement this method
	 super.onDraw(c);
	 if(MainActivity.map!=null)
	 {
	 Map map=MainActivity.map;
	 float tilew=map.tilew;
	 ma.reset();
	 ma.postTranslate(-bico.getWidth()/2,-bico.getHeight()/2);
	 //ma.postRotate(0);
	 ma.postScale(1+cdtime/dtime/7,1+cdtime/dtime/7);
	 ma.postTranslate(bico.getWidth()/2,bico.getHeight()/2);
	 ma.postTranslate(x*tilew,y*tilew);
	 c.drawBitmap(bico,ma,p);
	 p.setTextSize(tilew/2);
	 p.setTextAlign(Paint.Align.CENTER);
	 p.setColor(0xffffffff);
	 c.drawText(String.format("LV:%d",level+1),x*tilew+tilew/2,y*tilew-tilew/8,p);

	 if(this==map.selectedTower)
	 {
	 p.setStyle(Paint.Style.STROKE);
	 p.setColor(0xffffffff);
	 p.setStrokeWidth(tilew/10);
	 p.setPathEffect(new DashPathEffect(new float[]{tilew/4,tilew/4},0));
	 c.drawCircle(x*tilew+tilew/2,y*tilew+tilew/2,(float)(r*tilew*Math.pow(1.1,level)),p);
	 p.setStyle(Paint.Style.FILL);
	 }
	 }
	 }

	 */
	public static final class AttackType
	{
		public static final int INSTANT=0,
		PROJECTILE=1,
		BULLET=2;
	}
}
