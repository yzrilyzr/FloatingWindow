package com.yzrilyzr.floatingwindow.view;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.myclass.Pcm;
import android.view.MotionEvent;
import com.yzrilyzr.floatingwindow.Window;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class OscilloscopeView extends View
{
	int[] data=new int[48000],ft=new int[8000];
	float period=1f,gain=1,sr=48000;
	Path path=new Path();
	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	float avail=48000;
	boolean hold=false;
	private Runnable run;
	float x1=-1,y1,x2=-1,y2;
	int pointer=0;
	Bitmap bmp=null;
	float scanX=0;
	public OscilloscopeView(Context c)
	{
		this(c,null);
	}
	public OscilloscopeView(Context c,AttributeSet a)
	{
		super(c,a);
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
		if(run!=null)run=null;
		else
		{ 
			new Thread(run=new Runnable(){
				@Override
				public void run()
				{
					try
					{
						bmp=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
						Canvas c=new Canvas(bmp);
						c.drawColor(0xff600000);
						Paint p=new Paint();
						p.setColor(0xff10ff86);
						p.setStyle(Paint.Style.STROKE);
						p.setStrokeWidth(1);
						//int[] data2=new int[x2-x1];
						//System.arraycopy(data,x1,data2,0,data2.length);
						float lastx=0,lasty=0;
						for(int i=0;i<ft.length&&run==this;i++)
						{
							float o=Math.abs(Pcm.ft(i,data,32767));
							ft[i]=(int)o;
							c.drawLine(lastx,lasty,lastx=(bmp.getWidth()*i/8000f),lasty=(bmp.getHeight()-o/10f),p);
							//if(i%10==0)postInvalidate();
						}
					}
					catch(Throwable e)
					{
						util.toast("分析失败");
					}
				}
			}).start();
		}
	}
	public void append(int[] x)
	{
		if(hold)return;
		System.arraycopy(x,0,data,0,Math.min(x.length,data.length));
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
		avail=sr*period;
		x1=avail/3;
		x2=avail*2/3;
	}
	public void setScan(float sc)
	{
		period=sc;
		if(period>1)period=1;
		setAvail();
	}
	public void setGain(float sc)
	{
		gain=sc;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float c=getWidth();
		float gap=c/avail;
		if(run!=null)gap=1;
		float x=event.getX();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(util.inSectionOpen(x,x1*gap,util.px(10)))pointer=1;
				else if(util.inSectionOpen(x,x2*gap,util.px(10)))pointer=2;
				else pointer=0;
				break;
			case MotionEvent.ACTION_MOVE:
				if(pointer==1)x1=(int)(x/gap);
				else if(pointer==2)x2=(int)(x/gap);
		}
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0xff000000);
		if(run!=null&&bmp!=null)
		{
			p.setColor(0xffffffff);
			p.setStyle(Paint.Style.FILL);
			canvas.drawBitmap(bmp,0,0,p);
			x1=util.limit(x1,0,getWidth());
			x2=util.limit(x2,x1,getWidth());
			int hz=0,yy=0;
			int d=(int)(x1*ft.length/(float)getWidth());
			int e=(int)(x2*ft.length/(float)getWidth());
			for(int i=d;i<e;i++)yy=Math.max(yy,ft[i]);
			for(int i=d;i<e;i++)
				if(ft[i]==yy)
				{
					hz=i;
					break;
				}
			p.setStyle(Paint.Style.FILL);
			p.setColor(0xffff1086);
			canvas.drawLine(x1,0,x1,getHeight(),p);
			p.setColor(0xff8610ff);
			canvas.drawLine(x2,0,x2,getHeight(),p);
			p.setColor(0xff10ff86);
			canvas.drawText(String.format("p=%dHz,y=%d",hz,yy),0,getHeight()-p.getTextSize()*1.2f,p);

		}
		else
			try
			{
				path.reset();
				x1=util.limit(x1,1,avail-1);
				x2=util.limit(x2,x1,avail-1);
				float c=getWidth();
				float gap=avail/c;
				/*for(float i=0;i<c;i++)
				 {
				 int in=util.limit((int)util.limit(i*gap,0,avail),0,data.length-1);
				 float y=util.limit(getHeight()/2+data[in]*gain/getHeight(),0,getHeight());
				 if(i==0)path.moveTo(i,y);
				 else path.lineTo(i,y);
				 }*/
				int ya=0;
				for(int i=0;i<c/*data.length*/;i++)
				{
					float y=util.limit(getHeight()/2+data[i]*gain/getHeight(),0,getHeight());
					ya+=Math.abs(data[i]);
					path.lineTo(scanX,y);
					//canvas.drawPoint(scanX,y,p);
					scanX+=c/sr/period;
					if(scanX>c)
					{
						scanX=0;
						path.moveTo(-1,getHeight()/2);
					}
				}
				canvas.drawRect(0,getHeight()-util.px(10),getWidth()*ya/327/data.length,getHeight(),p);
				p.setColor(0xff10ff86);
				p.setStyle(Paint.Style.STROKE);
				//canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,p);
				canvas.drawPath(path,p);
				y1=data[(int)x1];
				y2=data[(int)x2];
				p.setStyle(Paint.Style.FILL);
				p.setColor(0xffff1086);
				canvas.drawLine(x1/gap,0,x1/gap,getHeight(),p);
				p.setColor(0xff8610ff);
				canvas.drawLine(x2/gap,0,x2/gap,getHeight(),p);
				p.setColor(0xff10ff86);
				canvas.drawText(String.format("w=%fHz,dx=%fHz,y1=%f,y2=%f",(float)data.length/(float)avail,(float)data.length/(float)(x2-x1),y1,y2),0,getHeight()-p.getTextSize()*1.2f,p);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		invalidate();
	}
} 
