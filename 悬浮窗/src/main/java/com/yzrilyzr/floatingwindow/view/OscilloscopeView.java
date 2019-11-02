package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.myclass.util;
import android.graphics.DashPathEffect;
import java.util.ArrayList;
import com.yzrilyzr.ui.uidata;

public class OscilloscopeView extends View
{
	int[][] data=new int[][]{
	new int[3000],
	new int[1000],
	new int[1000],
	new int[1000],
	new int[1000],
	new int[1000]
	};
	int[] inbuf=new int[192000*3];
	int inndex=0;
	Path path=new Path();
	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	boolean hold=false;
	private Runnable run;
	int x1=-1,y1,x2=-1,y2;
	//float ftmax=0,yaverage=0,scanX=0;
	int pointer=0;
	//Bitmap bmp=null;
	float xoff=0,yoff=0,trig=0;
	float timebase=1/*ms*/,gain=1;
	int cachecount=1;
	float sr=48000;
	boolean tdown=false;
	float xstart=0;
	Path xop=new Path(),yop=new Path(),trigp=new Path();
	float vmax,vmin,v2a,va;
	boolean grid=false;
	boolean more=false;
	ArrayList<Float> tind1=new ArrayList<Float>();
	ArrayList<Float> tind2=new ArrayList<Float>();
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

	public void setMore(boolean more)
	{
		this.more = more;
	}

	public boolean isMore()
	{
		return more;
	}

	public void setTdown(boolean tdown)
	{
		this.tdown = tdown;
	}

	public boolean isTdown()
	{
		return tdown;
	}

	public void setHold(boolean hold)
	{
		this.hold = hold;
	}

	public boolean isHold()
	{
		return hold;
	}

	public void setGrid(boolean grid)
	{
		this.grid = grid;
	}

	public boolean isGrid()
	{
		return grid;
	}
		/*public void setft()
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
	 x1=ft.length/3;
	 x2=ft.length*2/3;
	 bmp=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
	 Canvas c=new Canvas(bmp);
	 c.drawColor(0xff600000);
	 Paint p=new Paint();
	 p.setColor(0xff10ff86);
	 p.setStyle(Paint.Style.STROKE);
	 p.setStrokeWidth(1);
	 float lastx=0,lasty=0;
	 ftmax=0;
	 int[] data2=new int[240];
	 System.arraycopy(data,0,data2,0,240);
	 for(int i=0;i<ft.length&&run==this;i++)
	 {
	 float o=Math.abs(Pcm.ft(i*data.length/sr,data,32767));
	 //System.out.println(i);
	 ft[i]=(int)o;
	 ftmax=Math.max(ftmax,o);
	 c.drawPoint((bmp.getWidth()*i/8000f),lasty=(bmp.getHeight()-o/10f),p);
	 //c.drawLine(lastx,lasty,lastx=(bmp.getWidth()*i/8000f),lasty=(bmp.getHeight()-o/10f),p);
	 }
	 }
	 catch(Throwable e)
	 {
	 util.toast("分析失败");
	 }
	 }
	 }).start();
	 }
	 }*/
	public void append(int[] x,float sr)
	{
		if(hold)return;
		int reql=(int)(sr*timebase*3f*8f/1000f);
		if(x.length<reql)
		{
			System.arraycopy(x,0,inbuf,inndex,util.limit(Math.min(x.length,reql-inndex-1),0,reql));
			inndex+=x.length;
			if(reql<=inndex)
			{
				reSample(reql);
				inndex=0;
			}
		}
		else
		{
			System.arraycopy(x,0,inbuf,0,reql);
			reSample(reql);
		}
		/*if(!hold)
		 for(int i=0;i<cachecount;i++)
		 for(int u=0;u<data[i].length;i++)
		 {
		 data[i][u]=x[u];
		 }*/
	}
	public void reSample(int reql)
	{
		for(int i=0;i<data[0].length;i++)
			data[0][i]=inbuf[reql*i/data[0].length];
	}
	public void setSr(int x)
	{
		sr=x;
	}
	public void setScan(float freq)
	{
		timebase=freq;
		/* sfreq=freq;
		 if(sfreq<1)sfreq=1;
		 x1=data.length/3;
		 x2=data.length*2/3;*/
	}
	public void setGain(float sc)
	{
		gain=sc;
	}
	public void lx()
	{
		if(pointer==1)x1--;
		else if(pointer==2)x2--;
	}
	public void rx()
	{
		if(pointer==1)x1++;
		else if(pointer==2)x2++;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float c=getWidth();
		float gap=c/data.length;
		//if(run!=null)gap=c/(float)ft.length;
		float x=event.getX(),y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				/*if(util.inSectionOpen(x,x1*gap,util.px(10)))pointer=1;
				 else if(util.inSectionOpen(x,x2*gap,util.px(10)))pointer=2;
				 else pointer=0;*/
				pointer=0;
				RectF bounds1 = new RectF();
				xop.computeBounds(bounds1, true);
				RectF bounds2 = new RectF();
				yop.computeBounds(bounds2, true);
				RectF bounds3 = new RectF();
				trigp.computeBounds(bounds3, true);
				if(bounds1.contains(x,y))pointer=3;
				else if(bounds2.contains(x,y))pointer=4;
				else if(bounds3.contains(x,y))pointer=5;

				break;
			case MotionEvent.ACTION_MOVE:
				/*if(pointer==1)x1=(int)(x/gap);
				 else if(pointer==2)x2=(int)(x/gap);*/
				if(pointer==3)xoff=util.limit(x,0,getWidth());
				else if(pointer==4)yoff=util.limit(y,0,getHeight())-getHeight()/2;
				else if(pointer==5)trig=util.limit(y,0,getHeight())-getHeight()/2;
		}
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0xff000000);
		float s=util.px(8),w=getWidth(),h=getHeight(),hh=getHeight()/2;
		Path t=yop;
		t.reset();
		t.moveTo(0,s+yoff+hh);
		t.lineTo(s,s+yoff+hh);
		t.lineTo(s+s,yoff+hh);
		t.lineTo(s,-s+yoff+hh);
		t.lineTo(0,-s+yoff+hh);
		t.close();

		t=trigp;
		t.reset();
		t.moveTo(w,s+trig+hh);
		t.lineTo(w-s,s+trig+hh);
		t.lineTo(w-s-s,trig+hh);
		t.lineTo(w-s,-s+trig+hh);
		t.lineTo(w,-s+trig+hh);
		t.close();

		t=xop;
		t.reset();
		t.moveTo(xoff+s,0);
		t.lineTo(xoff+s,s);
		t.lineTo(xoff,s+s);
		t.lineTo(xoff-s,s);
		t.lineTo(xoff-s,0);
		t.close();
		p.setStyle(Paint.Style.STROKE);
		if(grid){
		path.reset();
		for(int i=(int)w/2;i<w;i+=data[0].length/3/8)
		{
			path.moveTo(i,0);
			path.lineTo(i,h);
		}
			for(int i=(int)w/2;i>=0;i-=data[0].length/3/8)
			{
				path.moveTo(i,0);
				path.lineTo(i,h);
			}for(int i=(int)hh;i<h;i+=data[0].length/3/8)
		{
			path.moveTo(0,i);
			path.lineTo(w,i);
		}
			for(int i=(int)hh;i>0;i-=data[0].length/3/8)
		{
			path.moveTo(0,i);
			path.lineTo(w,i);
		}
		}
		p.setColor(0xaaffffff);
		//p.setPathEffect(new DashPathEffect(new float[]{}));
		canvas.drawPath(path,p);
		p.setColor(0xffffff00);
		vmax=0;vmin=0;v2a=0;va=0;
		tind1.clear();tind2.clear();
		boolean isxs=false;
		xstart=data[0].length/3;
		path.reset();
		for(int i=0;i<cachecount;i++)
		{
			for(int u=data[i].length/3;u<data[i].length-1;u++)
			{
				float y=-(data[i][u]*gain*hh/32767f-yoff);
				float y1=-(data[i][u+1]*gain*hh/32767f-yoff);
				if(y<trig&&y1>trig)
				{
					if(tdown&&!isxs){
						xstart=u+0.5f;
						isxs=true;
					}
					tind1.add((float)u);
					if(tind2.size()%2==1)tind2.add((float)u);
				}
				else if(y>trig&&y1<trig)
				{
					if(!tdown&&!isxs){
						xstart=u+0.5f;
						isxs=true;
					}
					if(tind2.size()%2==0)tind2.add((float)u);
					
				}

			}

			for(int u=0;u<data[i].length;u++)
			{
				float yo=data[i][u];
				vmax=Math.max(vmax,yo);
				vmin=Math.min(vmin,yo);
				v2a+=yo*yo;
				va+=yo;
				float x=xoff-xstart+u;//getWidth()*u/data[i].length;
				float y=h-(yo*gain*hh/32767f-yoff+hh);
				if(x>=0&&x<=w)
				{
					if(u==0)path.moveTo(x,y);
					else path.lineTo(x,y);
				}
			}
		}
		canvas.drawPath(path,p);

		p.setStyle(Paint.Style.FILL);
		canvas.drawPath(yop,p);
		canvas.drawPath(xop,p);
		p.setColor(0xff00ffff);
		canvas.drawPath(trigp,p);
		p.setColor(0xffffffff);
		float vrms=(float)Math.sqrt(v2a/(float)cachecount/(float)data[0].length);
		canvas.drawText(String.format("Vpp %.1f",Math.abs(vmax-vmin)),0,h-p.getTextSize()*1.2f,p);
		canvas.drawText(String.format("Vrms %.1f",vrms),w/3,h-p.getTextSize()*1.2f,p);
		canvas.drawRect(0,h-util.px(10),(float)Math.log10(vrms)/4.5f*w,h,p);
		if(tind1.size()%2==1&&tind1.size()>0)tind1.remove(0);
		if(tind2.size()%2==1&&tind2.size()>0)tind2.remove(0);
		float T0=0,Tup=0;
		if(tind1.size()>=2){
			for(int i=0;i<tind1.size();i++){
				if(i%2==0)T0-=tind1.get(i);
				else T0+=tind1.get(i);
			}
		}
		if(tind2.size()>=2){
			for(int i=0;i<tind2.size();i++){
				if(i%2==0)Tup-=tind2.get(i);
				else Tup+=tind2.get(i);
			}
		}
		float tt=(timebase*(T0*2/tind1.size())/(data[0].length/3f/8f));
		canvas.drawText(String.format("Freq %.1fHz",T0==0?0:1000f/tt),w*2/3,h-p.getTextSize()*1.2f,p);
		if(more){
			float tr=(timebase*(Tup*2/tind2.size())/(data[0].length/3f/8f));
			canvas.drawText(String.format("Vmax %.1f",vmax),0,h-p.getTextSize()*2.4f,p);
			canvas.drawText(String.format("Vmin %.1f",vmin),w/3,h-p.getTextSize()*2.4f,p);
			canvas.drawText(String.format("Vavg %.1f",va/(float)cachecount/(float)data[0].length),w*2/3,h-p.getTextSize()*2.4f,p);
			canvas.drawText(String.format("Duty %.1f%s",100f*tr/tt,"%"),0,h-p.getTextSize()*3.6f,p);
			canvas.drawText(String.format("Time %.1fms",tt),w/3,h-p.getTextSize()*3.6f,p);
			canvas.drawText(String.format("Time+ %.1fms",tr),w*2/3,h-p.getTextSize()*3.6f,p);
			
		}
		/*if(run!=null&&bmp!=null)
		 {
		 p.setColor(0xffffffff);
		 p.setStyle(Paint.Style.FILL);
		 canvas.drawBitmap(bmp,0,0,p);
		 x1=util.limit(x1,0,ft.length);
		 x2=util.limit(x2,x1,ft.length);
		 int hz=0,yy=0;
		 int d=(int)((float)getWidth()*x1/(float)ft.length);
		 int e=(int)((float)getWidth()*x2/(float)ft.length);
		 for(int i=x1;i<x2;i++)yy=Math.max(yy,ft[i]);
		 for(int i=x1;i<x2;i++)
		 if(ft[i]==yy)
		 {
		 hz=i;
		 break;
		 }
		 p.setStyle(Paint.Style.FILL);
		 p.setColor(0xffff1086);
		 canvas.drawLine(d,0,d,getHeight(),p);
		 p.setColor(0xff8610ff);
		 canvas.drawLine(e,0,e,getHeight(),p);
		 p.setColor(0xff10ff86);
		 float ff=x1*data.length/sr;
		 canvas.drawText(String.format("p=%dHz,y=%d,y%s=%f,%f",hz,yy,"%",(float)yy*100f/ftmax,ff),0,getHeight()-p.getTextSize()*1.2f,p);
		 Path b=new Path();
		 b.moveTo(getWidth()/2,getHeight()/2);
		 for(int j=0;j<data.length;j++){
		 b.lineTo(getWidth()/2+
		 (data[j]+32767)/100f*(float)Math.sin((float)j/(float)data.length*ff*Math.PI*2f),
		 getHeight()/2+(data[j]+32767)/100f*(float)Math.cos((float)j/(float)data.length*ff*Math.PI*2f));
		 }
		 p.setStyle(Paint.Style.STROKE);
		 //canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,p);
		 canvas.drawPath(b,p);
		 p.setStyle(Paint.Style.FILL);

		 }
		 else
		 try
		 {
		 x1=util.limit(x1,0,data.length-1);
		 x2=util.limit(x2,0,data.length-1);
		 float c=getWidth(),d=getHeight();
		 if(!hold)
		 {
		 path.reset();
		 yaverage=0;
		 if(sfreq==1)
		 for(int i=0;i<data.length;i++)
		 {
		 /*int in=util.limit((int)util.limit(i*gap,0,avail),0,data.length-1);
		 ya+=Math.abs(data[(int)i]);
		 float y=util.limit(getHeight()/2+data[in]*gain/getHeight(),0,getHeight());
		 *//*
		 float y=util.limit(d/2+data[i]*gain/d,0,d);
		 yaverage+=Math.abs(data[i]);
		 if(i==0)path.moveTo(i,y);
		 else path.lineTo(i,y);
		 }
		 else
		 for(int i=0;i<data.length;i++)
		 {
		 float y=util.limit(d/2+data[i]*gain/d,0,d);
		 yaverage+=Math.abs(data[i]);
		 if(i==0)path.moveTo(scanX,y);
		 else path.lineTo(scanX,y);
		 scanX+=c*sfreq/sr;//2400 p/s =1hz
		 if(scanX>c)
		 {
		 scanX=0;
		 path.moveTo(0,getHeight()/2);
		 }
		 }
		 yaverage/=data.length;
		 }
		 y1=data[x1];
		 y2=data[x2];
		 //System.out.println(ya);
		 canvas.drawRect(0,d-util.px(10),util.limit(c*yaverage*3/32767f,0,c),d,p);
		 p.setColor(0xff10ff86);
		 p.setStyle(Paint.Style.STROKE);
		 //canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,p);
		 canvas.drawPath(path,p);
		 float gap=c/data.length;
		 p.setStyle(Paint.Style.FILL);
		 p.setColor(0xffff1086);
		 canvas.drawLine(x1*gap,0,x1*gap,getHeight(),p);
		 p.setColor(0xff8610ff);
		 canvas.drawLine(x2*gap,0,x2*gap,getHeight(),p);
		 p.setColor(0xff10ff86);
		 canvas.drawText(String.format("w=%fHz,dx=%fHz,y1=%d,y2=%d",
		 sfreq==1?(sr/(float)data.length):sfreq,
		 sfreq==1?sr/(float)Math.abs(x2-x1):sfreq*(float)data.length/(float)Math.abs(x2-x1),y1,y2),
		 0,getHeight()-p.getTextSize()*1.2f,p);
		 }
		 catch(Throwable e)
		 {
		 e.printStackTrace();
		 }*/
		invalidate();
	}
} 
