package com.yzrilyzr.floatingwindow.view;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.myclass.Pcm;

public class OscilloscopeView extends View
{
	int[] data=new int[48000];
	float period=0.001f,gain=1,sr=48000;
	Path path=new Path();
	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	int avail=48000;
	boolean hold=false;
	private Runnable run;
	public OscilloscopeView(Context c)
	{
		this(c,null);
	}
	public OscilloscopeView(Context c,AttributeSet a)
	{
		super(c,a);
		p.setColor(0xff10ff86);
		p.setStrokeWidth(util.px(0.7f));
		p.setTextSize(util.px(12));
		p.setStrokeJoin(Paint.Join.ROUND);
	}

	public void setHold(boolean hold)
	{
		this.hold=hold;
	}
	public void setft()
	{
		new Thread(run=new Runnable(){
			@Override
			public void run()
			{
				int[] data2=new int[data.length];
				System.arraycopy(data,0,data2,0,data.length);
				for(int i=0;i<data.length&&run==this;i++)data[i]=-Math.abs(Pcm.ft(i,data2));
			}
		}).start();
	}
	public void append(int[] x)
	{
		if(hold)return;
		int cl=Math.min(avail,x.length);
		System.arraycopy(x,0,data,0,cl);
		//cur+=avail;
		//cur%=x.length;
	}
	public void setSr(int x)
	{
		sr=x;
		data=new int[x];
		setAvail();
	}
	void setAvail()
	{
		avail=(int)Math.floor(sr*period);
	}
	public void setScan(float sc)
	{
		period=sc;
		setAvail();
	}
	public void setGain(float sc)
	{
		gain=sc;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0xff000000);
		path.reset();
		try
		{
			float c=getWidth();
			float gap=(float)avail/c;
			for(float i=0;i<c;i++)
			{
				int in=util.limit((int)util.limit(i*gap,0,avail),0,data.length-1);
				float y=util.limit(getHeight()/2+data[in]*gain/getHeight(),0,getHeight());
				if(i==0)path.moveTo(i,y);
				else path.lineTo(i,y);
			}
			p.setStyle(Paint.Style.STROKE);
			//canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,p);
			canvas.drawPath(path,p);
			p.setStyle(Paint.Style.FILL);
			canvas.drawText(String.format("w=%fHz",(float)data.length/(float)avail),0,getHeight()-p.getTextSize()*1.2f,p);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		invalidate();
	}
} 
