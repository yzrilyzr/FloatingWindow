package com.yzrilyzr.ui;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.myclass.*;

public class mySeekBarV extends SeekBar
{
	private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean ondown=false;
	public mySeekBarV(Context c, AttributeSet a)
	{
		super(c, a);	
	}
	public mySeekBarV(Context c)
	{
		this(c, null);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		float hei=getHeight(),wid=getWidth();
		float r=wid/2f,r3=wid*0.4f,r2=wid*0.05f;
		float drawy=r+(hei-2f*r)*(float)getProgress()/(float)getMax();
		float drawy2=r+(hei-2f*r)*(float)getSecondaryProgress()/(float)getMax();
		paint.setStyle(Paint.Style.FILL);
		if(isEnabled())
		{
			paint.setColor(uidata.getASColor());
			canvas.drawRoundRect(new RectF(r,r3,wid-r,hei-r3),r2,r2,paint);
			paint.setColor(uidata.getAFColor());
			canvas.drawRoundRect(new RectF(r,r3,drawy2,hei-r3),r2,r2,paint);
			paint.setColor(uidata.ACCENT);
			canvas.drawRoundRect(new RectF(r,r3,drawy,hei-r3),r2,r2,paint);
			paint.setColor(uidata.ACCENT);
			if(ondown)canvas.drawCircle(drawy,hei/2,hei*0.3f,paint);
			else canvas.drawCircle(drawy2,hei/2,hei*0.2f,paint);
		}
		else
		{
			paint.setColor(uidata.getESColor());
			canvas.drawRoundRect(new RectF(r,r3,drawy-hei*0.2f,hei-r3),r2,r2,paint);
			canvas.drawRoundRect(new RectF(hei*0.2f+drawy2,r3,wid-r,hei-r3),r2,r2,paint);
			paint.setColor(uidata.getEFColor());
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(util.px(2));
			canvas.drawCircle(drawy,hei/2,hei*0.12f,paint);
		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		int a=event.getAction();
		if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_MOVE)ondown=true;
		else ondown=false;
		float y=event.getY();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,util.px(30)), WidgetUtils.measure(heightMeasureSpec,-2));
	}
}
