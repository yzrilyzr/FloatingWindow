package com.yzrilyzr.floatingwindow.view;

import android.view.*;
import android.content.*;
import java.io.*;
import java.util.*;
import android.graphics.*;
import com.yzrilyzr.ui.*;
import com.yzrilyzr.myclass.*;
import android.util.*;

public class HexView extends View
{
	public ArrayList<byte[]> data=new ArrayList<byte[]>();
	Paint pa;
	int bys=8;
	long size;
	float y=0,dy=0,py=0,lasty=0;
	boolean isSeek=false;
	public boolean changed=false;
	public HexView(Context ctx,AttributeSet a){
		super(ctx,a);
		pa=new Paint(Paint.ANTI_ALIAS_FLAG);
		pa.setTextSize(util.px(uidata.TEXTSIZE));
		pa.setColor(uidata.TEXTMAIN);
		pa.setTypeface(Typeface.MONOSPACE);
		
	}
	public HexView(Context ctx)
	{
		this(ctx,null);
	}

	public void setYOff(int p2)
	{
		y=-pa.getTextSize()*p2/bys;
	}
	public void loadStream(InputStream is) throws IOException
	{
		size=is.available();
		byte[] bu=new byte[1024];
		int index=0;
		byte[] bg=new byte[bys];
		data.clear();
		while((index=is.read(bu))!=-1)
		{
			for(int i=0;i<index;i+=bys)
			{
				if(i+bys<index)
				{
					bg=new byte[bys];
					System.arraycopy(bu,i,bg,0,bys);
					data.add(bg);
				}
				else
				{
					bg=new byte[index-i];
					System.arraycopy(bu,i,bg,0,index-i);
					data.add(bg);
				}
				
			}
		}
		is.close();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		System.out.println(y);
		// TODO: Implement this method
		super.onDraw(canvas);
		int sy=(int)(-y/pa.getTextSize());
		float drawy=0;
		if(y>0)y=0;
		float xo=pa.measureText(Long.toHexString(size));
		StringBuilder sbb=new StringBuilder();
		for(int u=0;u<bys;u++)sbb.append("000");
		float xo2=xo+pa.measureText(sbb.toString());
		for(int i=sy;i<sy+getHeight()/pa.getTextSize()&&i<data.size()&&i>=0;i++)
		{
			if(i>=data.size())break;
			byte[] b=data.get(i);
			StringBuffer sb=new StringBuffer();
			StringBuffer sb2=new StringBuffer();
			for(int t=0;t<b.length;t++)
			{
				String h=Integer.toHexString((int)b[t]+127).toUpperCase();
				if(h.length()==1)sb.append("0");
				sb.append(h);
				sb.append(" ");
				sb2.append((char)b[t]);
				sb2.append(" ");
			}
			pa.setStrokeWidth(1);
			pa.setStyle(Paint.Style.STROKE);
			pa.setColor(0xffaaaaaa);
			canvas.drawLine(xo,0,xo,getHeight(),pa);
			canvas.drawLine(xo2,0,xo2,getHeight(),pa);
			pa.setStyle(Paint.Style.FILL);
			pa.setColor(0xff4444ff);
			canvas.drawText("偏移",0,pa.getTextSize(),pa);
			canvas.drawText(Integer.toHexString(i*bys).toUpperCase(),0,pa.getTextSize()+(drawy+=pa.getTextSize()),pa);
			pa.setColor(uidata.TEXTMAIN);
			canvas.drawText("HEX",xo,pa.getTextSize(),pa);
			canvas.drawText(sb.toString(),xo,pa.getTextSize()+drawy,pa);
			canvas.drawText("Char",xo2,pa.getTextSize(),pa);
			canvas.drawText(sb2.toString(),xo2,pa.getTextSize()+drawy,pa);
			
		}
		pa.setColor(uidata.ACCENT);
		float r=util.px(3);
		canvas.drawRoundRect(new RectF(getWidth()-3*r,r,getWidth()-r,getHeight()-r),r/2f,r/2f,pa);
		canvas.drawCircle(getWidth()-2*r,util.limit(2*r-y*(getHeight()-4*r)/pa.getTextSize()/data.size(),2*r,getHeight()-2*r),2*r,pa);
		invalidate();
	}
	public long getSize(){
		return size;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float ppx=event.getX();
		float ppy=event.getY();
		float r=util.px(3);
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(ppx>getWidth()-4*r){
					isSeek=true;
					y=-pa.getTextSize()*data.size()*ppy/getHeight();
					break;
				}
				isSeek=false;
				py=ppy;
				lasty=y;
				break;
			case MotionEvent.ACTION_MOVE:
				if(isSeek){
					y=-pa.getTextSize()*data.size()*ppy/getHeight();
					break;
				}
				dy=ppy-py;
				y=lasty+dy;
				break;
		}
		if(y>0)y=0;
		return true;
	}

}
