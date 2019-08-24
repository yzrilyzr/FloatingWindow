package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import com.yzrilyzr.icondesigner.VECfile;

public class Ui extends Shape
{
	Bitmap b,btogg;
	boolean visible=true;
	float x1,y1,w1,h1,mill,alpha,alphamill;
	boolean isFrom=false,isAlphaFrom=false,isTo=false,isAlphaTo=false;
	long st,stalpha;
	boolean toggle=true;
	Matrix m=new Matrix();
	public Ui(String v,float x,float y,int w,int h)
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
		else b=Bitmap.createBitmap(pi(w),pi(h),Bitmap.Config.ARGB_8888);
		MainActivity.ui.add(this);
	}
	public Ui(String v,String togg,float x,float y,int w,int h)
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
	public Ui tScFrom(float x,float y,float w,float h,float millis)
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
	public Ui tScTo(float x,float y,float w,float h,float millis)
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
	public Ui alphaFrom(float x,float m)
	{
		isAlphaFrom=true;
		isAlphaTo=false;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public Ui alphaTo(float x,float m)
	{
		isAlphaTo=true;
		isAlphaFrom=false;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public Ui setVisable(boolean b)
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
			if(isAlphaTo)visible=false;
			isAlphaTo=false;
		}
		if(tf>1)
		{
			isFrom=false;
			if(isTo)visible=false;
			isTo=false;
		}
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
