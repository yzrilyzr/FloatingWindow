package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Matrix;

public class Ui extends Shape
{
	Bitmap b;
	boolean visible=true;
	float x1,y1,w1,h1,mill,alpha,alphamill;
	boolean isFrom=false,isAlpha=false,isTo=false,isAto=false;
	long st,stalpha;
	Matrix m=new Matrix();
	public Ui(String v,float x,float y,int w,int h){
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
		}
		else b=Bitmap.createBitmap(pi(w),pi(h),Bitmap.Config.ARGB_8888);
		MainActivity.ui.add(this);
	}
	public Ui tScFrom(float x,float y,float w,float h,float millis){
		isFrom=true;
		visible=true;
		st=System.currentTimeMillis();
		mill=millis;
		x1=p(x);
		y1=p(y);
		w1=p(w);
		h1=p(h);
		return this;
	}
	public Ui tScTo(float x,float y,float w,float h,float millis){
		isTo=true;
		visible=true;
		st=System.currentTimeMillis();
		mill=millis;
		x1=p(x);
		y1=p(y);
		w1=p(w);
		h1=p(h);
		return this;
	}
	public Ui alphaFrom(float x,float m){
		isAlpha=true;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public Ui alphaTo(float x,float m){
		isAto=true;
		visible=true;
		alpha=x;
		stalpha=System.currentTimeMillis();
		alphamill=m;
		return  this;
	}
	public Ui setVisable(boolean b){
		visible=b;
		return this;
	}
	@Override
	public void onDraw(Canvas c)
	{
		float tf=(float)(System.currentTimeMillis()-st)/mill;
		float ta=(float)(System.currentTimeMillis()-stalpha)/alphamill;
		if(ta>1){
			isAlpha=false;
			if(isAto)visible=false;
			isAto=false;
		}
		if(tf>1){
			isFrom=false;
			if(isTo)visible=false;
			isTo=false;
		}
		if(isAlpha)p.setAlpha((int)(2.55f*((100f-alpha)*ta+alpha)));
		else if(isAto)p.setAlpha((int)(2.55f*((alpha-100f)*ta+100f)));
		else p.setAlpha(255);
		if(isFrom){
			m.reset();
			m.postScale((1f-w1/w)*tf+w1/w,(1f-h1/h)*tf+h1/h);
			m.postTranslate((x-x1)*tf+x1,(y-y1)*tf+y1);
			c.drawBitmap(b,m,p);
		}
		else if(isTo){
			m.reset();
			m.postScale((w1/w-1f)*tf+1f,(h1/h-1f)*tf+1f);
			m.postTranslate((x1-x)*tf+x,(y1-y)*tf+y);
			c.drawBitmap(b,m,p);
		}
		else if(visible&&b!=null)c.drawBitmap(b,x,y,p);
	}

	@Override
	public void onTouch(MotionEvent e)
	{
		
		super.onTouch(e);
	}
	public void onMove(MotionEvent e){}
}
