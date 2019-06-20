package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.WidgetUtils;
import com.yzrilyzr.ui.uidata;
import java.util.ArrayList;

public class Graph extends View
{
	private Paint pa=new Paint(Paint.ANTI_ALIAS_FLAG);
	private ArrayList<Integer> ya=new ArrayList<Integer>();
	private Path path=new Path();
	private int max,pnum=50;
	public Graph(Context c,AttributeSet a)
	{
		super(c,a);
		pa.setStrokeWidth(util.px(2));
	}
	public Graph(Context c)
	{
		this(c,null);
	}
	public void setMaxPoints(int i){
		pnum=i;
	}
	public void setMax(int i)
	{
		max=i;
	}
	public void addPoint(int i)
	{
		ya.add(i);
	}
	public void update(){
		Looper l=util.ctx.getMainLooper();
		if(l.isCurrentThread())invalidate();
		else postInvalidate();
	}
	public void setLineWidth(float i){
		pa.setStrokeWidth(util.px(i));
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0);
		pa.setStyle(Paint.Style.STROKE);
		pa.setColor(uidata.ACCENT);
		float k=getWidth();
		path.reset();
		for(int i=ya.size()-1;i>=0;i--)
		{
			if(i==ya.size()-1)path.moveTo(k,getHeight()-(float)ya.get(i)*(float)getHeight()/(float)max);
			else path.lineTo(k,getHeight()-(float)ya.get(i)*(float)getHeight()/(float)max);
			k-=(float)getWidth()/(float)pnum;
		}
		canvas.drawPath(path,pa);
		canvas.drawRect(0,0,getWidth(),getHeight(),pa);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,-1),WidgetUtils.measure(heightMeasureSpec,util.px(100)));
	}

}
