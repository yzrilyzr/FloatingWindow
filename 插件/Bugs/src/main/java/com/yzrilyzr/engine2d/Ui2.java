package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import com.yzrilyzr.icondesigner.VECfile;

public class Ui2 extends Shape
{
	Bitmap b,btogg;
	boolean visible=true;
	float x1,y1,w1,h1,mill,alpha,alphamill;
	boolean isFrom=false,isAlphaFrom=false,isTo=false,isAlphaTo=false;
	long st,stalpha;
	boolean toggle=true;
	Matrix m=new Matrix();
	Ui2 parent;
	public Ui2(String v,float x,float y,int w,int h,boolean add)
	{
		this.x=p(x);
		this.y=p(y);
		this.w=p(w);
		this.h=p(h);
		r=0;
		if(v!=null)
			try
			{
				b=VECfile.createBitmap(MainActivity.ctx,v,pi(w),pi(h));
			}
			catch (Exception e)
			{
				MainActivity.toast(e);
			}
		else if(w!=0&&h!=0) b=Bitmap.createBitmap(pi(w),pi(h),Bitmap.Config.ARGB_8888);
		if(add)MainActivity.ui.add(this);
	}
	public Ui2(String v,float x,float y,int w,int h){
		this(v,x,y,w,h,true);
	}
	public Ui2(String v,String togg,float x,float y,int w,int h)
	{
		this(v,x,y,w,h);
		if(togg!=null)
			try
			{
				btogg=VECfile.createBitmap(MainActivity.ctx,togg,pi(w),pi(h));
			}
			catch (Exception e)
			{
				MainActivity.toast(e);
			}
		else btogg=Bitmap.createBitmap(pi(w),pi(h),Bitmap.Config.ARGB_8888);

	}
	public void toggle(){
		toggle=!toggle;
	}
	public boolean isAnim()
	{
		return visible&&(isAlphaTo||isAlphaFrom||isFrom||isTo);
	}
	public Ui2 tScFrom(float x,float y,float w,float h,float millis)
	{
		isFrom=true;
		isTo=false;
		visible=true;
		st=System.currentTimeMillis();
		mill=millis;
		x1=p(x);
		y1=p(y);
		w1=p(w);
		h1=p(h);
		return this;
	}
	public Ui2 tScTo(float x,float y,float w,float h,float millis)
	{
		isTo=true;
		isFrom=false;
		visible=true;
		st=System.currentTimeMillis();
		mill=millis;
		x1=p(x);
		y1=p(y);
		w1=p(w);
		h1=p(h);
		return this;
	}
	public Ui2 alphaFrom(float x,float m)
	{
		isAlphaFrom=true;
		isAlphaTo=false;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public Ui2 alphaTo(float x,float m)
	{
		isAlphaTo=true;
		isAlphaFrom=false;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public static float getNLinearValueByTime(float time,float starttime,float endtime){
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=limit((time-starttime)/sec,0f,1f);
		return NonLinearFunc(x);
	}
	public static float NonLinearFunc(float x){
		x=limit(x,0,1);
		float y=x<0.5?(float)Math.pow(x,2)*2f:-(float)Math.pow(x-1f,2)*2f+1f;
		return limit(y,0,1);
}
	public Ui2 setVisable(boolean b)
	{
		visible=b;
		return this;
	}
	@Override
	public void onDraw(Canvas c)
	{
		float tf=(float)(System.currentTimeMillis()-st)/mill;
		float ta=(float)(System.currentTimeMillis()-stalpha)/alphamill;
		if(ta>1)
		{
			isAlphaFrom=false;
			if(isAlphaTo)setVisable(false);
			isAlphaTo=false;
		}
		if(tf>1)
		{
			isFrom=false;
			if(isTo)setVisable(false);
			isTo=false;
		}
		ta=NonLinearFunc(ta);
		tf=NonLinearFunc(tf);
		if(isAlphaFrom)p.setAlpha((int)(2.55f*((100f-alpha)*ta+alpha)));
		else if(isAlphaTo)p.setAlpha((int)(2.55f*((alpha-100f)*ta+100f)));
		else p.setAlpha(255);
		Bitmap bb=toggle?b:btogg;
		if(bb==null)return;
		if(isFrom)
		{
			m.reset();
			m.postScale((1f-w1/w)*tf+w1/w,(1f-h1/h)*tf+h1/h);
			m.postTranslate((x-x1)*tf+x1,(y-y1)*tf+y1);
			c.drawBitmap(bb,m,p);
		}
		else if(isTo)
		{
			m.reset();
			m.postScale((w1/w-1f)*tf+1f,(h1/h-1f)*tf+1f);
			m.postTranslate((x1-x)*tf+x,(y1-y)*tf+y);
			c.drawBitmap(bb,m,p);
		}
		else if(visible)c.drawBitmap(bb,x,y,p);
	}

	public void onClick(MotionEvent e)
	{}
	public void onMove(MotionEvent e)
	{}
	public void onDown(MotionEvent e)
	{}
}
