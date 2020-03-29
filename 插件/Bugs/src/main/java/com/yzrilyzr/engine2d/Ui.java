package com.yzrilyzr.engine2d;
import android.graphics.*;
import android.view.*;

public class Ui implements Eg.GameCBK
{
	int gravity;
	float dx,dy,si;
	boolean anim;
	String vec;
	boolean visible=true;
	@Override
	public void render(Canvas c, float dt)
	{
		Eg.drawVec(c,vec,gravity,si,dx,dy,0,0,1,0,0,0,0,0,1);
	}

	@Override
	public void start()
	{
		// TODO: Implement this method
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
	public void add(){
		Eg.gameact.uis.add(this);
	}
	public void onClick(MotionEvent e){}
	public void onMove(MotionEvent e){}
	public void onDown(MotionEvent e){}
	
	public static boolean down(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_DOWN;
	}
	/*public boolean isOutOfScr(){
		return x<0||y<0||x>p(1600)||y>p(900);
	}*/
	public static boolean move(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_MASK;
	}
	public static boolean up(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_UP;
	}
	/*public boolean contains(float x,float y){
		return r==0?x>this.x&&x<this.x+w&&y>this.y&&y<this.y+h:(x-this.x)*(x-this.x)+(y-this.y)*(y-this.y)<r*r;
	}
	public boolean contains(Shape o){
		return r==0?x>this.x&&x<this.x+w&&y>this.y&&y<this.y+h:(x-o.x)*(x-o.x)+(y-o.y)*(y-o.y)<(r+o.r)*(r+o.r);
	}
	*/
	public Ui(String vec,int g,float size,float x,float y){
		this.vec=vec;
		gravity=g;
		si=size;
		dx=x;
		dy=y;
	}
	public Ui(int g,float x,float y,float w,float h){
		gravity=g;
		if(Eg.hasFlag(gravity,Gravity.CENTER))
		{
			x=(int)(100f-w)/2;
			y=(int)(100f-h)/2;
		}
		if(Eg.hasFlag(gravity,Gravity.LEFT))x=0;
		if(Eg.hasFlag(gravity,Gravity.TOP))y=0;
		if(Eg.hasFlag(gravity,Gravity.RIGHT))x=(int)(100f-w);
		if(Eg.hasFlag(gravity,Gravity.BOTTOM))y=(int)(100f-h);
		dx=x;
		dy=y;
		/*x*=Eg.getAbsWidth()/100f;
		y*=Eg.getAbsHeight()/100f;
		x+=getAbsWidth()*(dxperc+atransx)/100f;
		y+=getAbsHeight()*(dyperc+atransy)/100f;
		m.postScale(ascale,ascale,w*ascpx/100f,h*ascpy/100f);
		m.postRotate(arotate*360f,w*aropx/100f,h*aropy/100f);
		m.postTranslate(x,y);
		p.setAlpha((int)(aalpha*255f));
		c.drawBitmap(b,m,p);*/
		
	}
}
