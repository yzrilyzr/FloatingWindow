package com.yzrilyzr.engine2d;
import android.graphics.*;
import android.view.*;

public class Ui
{
	public int gravity;
	public float dx,dy,si;
	public boolean anim;
	public String vec;
	public float r=0;
	public RectF rect=new RectF();
	public Ui parent;
	public void draw(Canvas c)
	{
		Eg.drawVec(c,vec,gravity,si,dx,dy,rect,parent==null?null:parent.rect,0,0,0,0,1,0,0,0,0,0,1);
	}
	public void draw(Canvas c,float atransx,float atransy,float scale,float scalecx,float scalecy,float rotate,float rotatecx,float rotatecy,float alpha)
	{
		Eg.drawVec(c,vec,gravity,si,dx,dy,rect,parent==null?null:parent.rect,atransx,atransy,0,0,scale,scalecx,scalecy,rotate,rotatecx,rotatecy,alpha);
	}
	public void draw(Canvas c,float atransx,float atransy,float ms,int gr,float scale,float scalecx,float scalecy,float rotate,float rotatecx,float rotatecy,float alpha)
	{
		Eg.drawVec(c,vec,gravity,si,dx,dy,rect,parent==null?null:parent.rect,atransx,atransy,ms,gr,scale,scalecx,scalecy,rotate,rotatecx,rotatecy,alpha);
	}
	public void onClick(MotionEvent e)
	{}
	public void onMove(MotionEvent e)
	{}
	public void onDown(MotionEvent e)
	{}

	public static boolean down(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_DOWN;
	}
	/*public boolean isOutOfScr(){
	 return x<0||y<0||x>p(1600)||y>p(900);
	 }*/
	public static boolean move(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_MASK;
	}
	public static boolean up(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_UP;
	}
	public boolean contains(float x,float y)
	{
		return r==0?
			x>rect.left&&x<rect.right&&y>rect.top&&y<rect.bottom:
			Math.pow(x-rect.centerX(),2)+Math.pow(y-rect.centerY(),2)<r*r;
	}
	/*public boolean contains(Ui o){
	 return r==0?x>rect.left&&x<rect.left+w&&y>rect.top&&y<rect.top+h:(x-o.x)*(x-o.x)+(y-o.y)*(y-o.y)<(r+o.r)*(r+o.r);
	 }*/
	public Ui(String vec,int g,float size,float x,float y)
	{
		this.vec=vec;
		gravity=g;
		si=size;
		dx=x;
		dy=y;
	}
	public Ui(float x,float y,float w,float h){
		rect.set(x,y,x+w,y+h);
	 /*gravity=g;
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
