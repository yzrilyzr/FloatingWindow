package com.yzrilyzr.engine2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.yzrilyzr.engine2d.MainActivity;
import java.util.List;
import android.content.res.Resources;
import java.util.Random;

public class Shape
{
	//static final List<Shape> l=MainActivity.sh;
	public float x,y,w,h,r,t=0;
	static float scale=1;
	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	public Shape(float x, float y, float w, float h, float r)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.r = r;
	}
	public Shape(){}
	public void onDraw(Canvas c){}
	public void onTouch(MotionEvent e){}
	public void onMove(MotionEvent e){}
	public void onDown(MotionEvent e){}
	public boolean contains(float x,float y){
		return r==0?x>this.x&&x<this.x+w&&y>this.y&&y<this.y+h:(x-this.x)*(x-this.x)+(y-this.y)*(y-this.y)<r*r;
	}
	public boolean contains(Shape o){
		return r==0?x>this.x&&x<this.x+w&&y>this.y&&y<this.y+h:(x-o.x)*(x-o.x)+(y-o.y)*(y-o.y)<(r+o.r)*(r+o.r);
	}
	public static boolean down(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_DOWN;
	}
	public boolean isOutOfScr(){
		return x<0||y<0||x>p(1600)||y>p(900);
	}
	public static boolean move(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_MASK;
	}
	public static boolean up(MotionEvent e){
		return e.getAction()==MotionEvent.ACTION_UP;
	}
	public static int pi(float m){
		return (int)(m*scale);
	}
	public static float p(float m){
		return m*scale;
	}
	public static int r(int r){
		return (int)(-r+Math.random()*2*r);
	}
	public static float limit(float x,float min,float max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static long limit(long x,long min,long max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int limit(int x,int min,int max)
	{
		return Math.max(Math.min(x,max),min);
	}

	
}
