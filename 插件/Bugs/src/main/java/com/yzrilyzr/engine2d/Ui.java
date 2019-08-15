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
	boolean isFrom=false,isAlpha=false;
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
			b=VECfile.createBitmap(MainActivity.ctx,v,p(w),p(h));
		}
		catch (Exception e)
		{}
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
	public Ui alphaFrom(float x,float m){
		isAlpha=true;
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
		float ta=(float)(System.currentTimeMillis()-st)/mill;
		if(ta>1)isAlpha=false;
		if(tf>1)isFrom=false;
		if(isAlpha)p.setAlpha((int)(2.55f*((100f-alpha)*ta+alpha)));
		else p.setAlpha(255);
		if(isFrom){
			m.reset();
			m.postScale((1f-w1/w)*tf+w1/w,(1f-h1/h)*tf+h1/h);
			m.postTranslate((x-x1)*tf+x1,(y-y1)*tf+y1);
			c.drawBitmap(b,m,p);
		}
		else if(visible&&b!=null)c.drawBitmap(b,x,y,p);
	}

	@Override
	public void onTouch(MotionEvent e)
	{
		
		super.onTouch(e);
	}
	
}
