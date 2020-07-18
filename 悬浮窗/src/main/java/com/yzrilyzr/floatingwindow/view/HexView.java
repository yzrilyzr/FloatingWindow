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
	int ix,iy;
	float y=0,dy=0,py=0,px=0,lasty=0,lastx=0,spx=0,spy=0;
	boolean isSeek=false,skeyboard=false,inp=false;
	float skbms=0;
	long lasttime;
	ArrayList<RectF> keys=new ArrayList<RectF>();
	private final static String HEX = "0123456789ABCDEF";
	public boolean changed=false,isMove=false;
	public HexView(Context ctx,AttributeSet a)
	{
		super(ctx,a);
		pa=new Paint(Paint.ANTI_ALIAS_FLAG);
		pa.setTextSize(util.px(uidata.TEXTSIZE));
		pa.setColor(uidata.TEXTMAIN);
		pa.setTypeface(Typeface.MONOSPACE);
		setLayerType(View.LAYER_TYPE_SOFTWARE,null);

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
		y=0;
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
		// TODO: Implement this method
		super.onDraw(canvas);
		int sy=(int)(-y/pa.getTextSize());
		float drawy=0;
		if(y>0)y=0;
		float xo=pa.measureText(Long.toHexString(size));
		StringBuilder sbb=new StringBuilder();
		for(int u=0;u<bys;u++)sbb.append("000");
		float xo2=xo+pa.measureText(sbb.toString());
		if(spx>xo&&spx<xo2&&spy<getHeight()-util.px(100)){
			float ww=pa.measureText(" ");
			ix=util.limit((int)((spx-xo)/ww+1)/3,0,7);
			iy=util.limit((int)((spy-y)/pa.getTextSize()-1),0,data.size()-1);
			int yyy=(int)(iy+y/pa.getTextSize())+1;
			pa.setColor(0xff000000);
			canvas.drawRect(ix*3*ww+xo,yyy*pa.getTextSize(),xo+ix*3*ww+2*ww,(yyy+1)*pa.getTextSize(),pa);
		}
		for(int i=sy;i<sy+getHeight()/pa.getTextSize()&&i<data.size()&&i>=0;i++)
		{
			if(i>=data.size())break;
			byte[] b=data.get(i);
			StringBuffer sb=new StringBuffer();
			StringBuffer sb3=new StringBuffer();
			StringBuffer sb2=new StringBuffer();
			StringBuffer sb4=new StringBuffer();
			for(int t=0;t<b.length;t++)
			{
				//String h=Integer.toHexString((char)b[t]).toUpperCase();
				if(b[t]>=0x21&&b[t]<=0x7e)
				{
					sb.append(HEX.charAt((b[t]>>4)&0x0f)).append(HEX.charAt(b[t]&0x0f)).append(" ");
					sb3.append("   ");
					sb2.append((char)b[t]).append(" ");
					sb4.append("  ");
				}
				else
				{
					sb.append("   ");
					sb3.append(HEX.charAt((b[t]>>4)&0x0f)).append(HEX.charAt(b[t]&0x0f)).append(" ");
					sb4.append(". ");
					sb2.append("  ");
				}
			}
			pa.setStrokeWidth(1);
			pa.setStyle(Paint.Style.STROKE);
			pa.setColor(0xffaaaaaa);
			canvas.drawLine(xo,0,xo,getHeight(),pa);
			canvas.drawLine(xo2,0,xo2,getHeight(),pa);
			pa.setStyle(Paint.Style.FILL);
			pa.setColor(0xff44bbff);
			canvas.drawText("偏移",0,pa.getTextSize(),pa);
			canvas.drawText(Integer.toHexString(i*bys).toUpperCase(),0,pa.getTextSize()+(drawy+=pa.getTextSize()),pa);
			pa.setColor(uidata.TEXTMAIN);
			canvas.drawText("HEX",xo,pa.getTextSize(),pa);
			//21-7e
			canvas.drawText(sb.toString(),xo,pa.getTextSize()+drawy,pa);
			pa.setColor(0xffff5555);
			canvas.drawText(sb3.toString(),xo,pa.getTextSize()+drawy,pa);
			pa.setColor(uidata.TEXTMAIN);

			canvas.drawText("Char",xo2,pa.getTextSize(),pa);
			canvas.drawText(sb2.toString(),xo2,pa.getTextSize()+drawy,pa);
			pa.setColor(0xffff5555);
			canvas.drawText(sb4.toString(),xo2,pa.getTextSize()+drawy,pa);
			
		}
		pa.setColor(uidata.ACCENT);
		float r=util.px(3);
		canvas.drawRoundRect(new RectF(getWidth()-3*r,r,getWidth()-r,getHeight()-r),r/2f,r/2f,pa);
		canvas.drawCircle(getWidth()-2*r,util.limit(2*r-y*(getHeight()-4*r)/pa.getTextSize()/data.size(),2*r,getHeight()-2*r),2*r,pa);
		if(lasttime==0)lasttime=System.currentTimeMillis();
		float dt=System.currentTimeMillis()-lasttime;
		if(skeyboard&&skbms<500)skbms+=dt;
		if(!skeyboard&&skbms>=0)skbms-=dt;
		if(skbms>0)
		{
			pa.setColor(uidata.MAIN);
			canvas.drawRoundRect(new RectF(0,
					getHeight()-myAnim.getNLinearValueByTime(skbms,0,300)*util.px(100),
					getWidth(),getHeight()+util.px(uidata.UI_RADIUS)),util.px(uidata.UI_RADIUS),util.px(uidata.UI_RADIUS),pa);
			pa.setColor(uidata.BUTTON);
			if(keys.size()==0)
			{
				for(int i=0;i<8;i++)
				{
					//int al=(int)(255f*myAnim.getNLinearValueByTime(skbms,300+i*25,325+i*25));
					//pa.setShadowLayer(util.px(1.5f),0,util.px(2),al/2*0x01000000);
					//pa.setAlpha(al);
					//canvas.drawRoundRect(
					keys.add(new RectF(
							getWidth()/9*(i+1)-util.px(15),
							getHeight()-util.px(300/4)-util.px(15),
							getWidth()/9*(i+1)+util.px(15),
							getHeight()-util.px(300/4)+util.px(15)));
					//util.px(uidata.UI_RADIUS),util.px(uidata.UI_RADIUS),pa);
				}
				for(int i=0;i<8;i++)
				{
					//canvas.drawRoundRect(
					keys.add(new RectF(
							getWidth()/9*(i+1)-util.px(15),
							getHeight()-util.px(100/4)-util.px(15),
							getWidth()/9*(i+1)+util.px(15),
							getHeight()-util.px(100/4)+util.px(15)));
					//
				}
			}
			int i=0;
			pa.setTextAlign(Paint.Align.CENTER);
			for(RectF rr:keys)
			{
				int al=(int)(255f*myAnim.getNLinearValueByTime(skbms,300+i*12,312+i*12));
				pa.setShadowLayer(util.px(1.5f),0,util.px(2),(al/2)<<24);
				pa.setColor(uidata.BUTTON);
				pa.setAlpha(al);
				canvas.drawRoundRect(rr,util.px(uidata.UI_RADIUS),util.px(uidata.UI_RADIUS),pa);
				pa.setColor(uidata.TEXTMAIN);
				pa.setShadowLayer(0,0,0,0);
				pa.setAlpha(al);
				canvas.drawText(""+HEX.charAt(i),rr.centerX(),rr.centerY()+pa.getTextSize()/2,pa);
				i++;
			}
			pa.setTextAlign(Paint.Align.LEFT);

		}
		pa.setAlpha(255);
		lasttime=System.currentTimeMillis();
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO: Implement this method
		super.onSizeChanged(w, h, oldw, oldh);
		keys.clear();
		skbms=0;
	}

	public long getSize()
	{
		return size;
	}
	private void input(int i)
	{
		try{
		byte[] bp=data.get(util.limit(iy,0,data.size()-1));
		byte b=bp[util.limit(ix,0,bp.length-1)];
		System.out.println(Integer.toHexString(b));
		if(!inp){
			b=(byte)i;
			inp=true;
		}
		else{
			b=(byte)((byte)(b<<4)+(byte)i);
			inp=false;
		}
		data.get(iy)[ix]=b;
		}catch(Throwable e){
			util.toast("编辑失败");
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float ppx=event.getX();
		float ppy=event.getY();
		float r=util.px(3);
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(ppx>getWidth()-4*r)
				{
					isSeek=true;
					y=-pa.getTextSize()*data.size()*ppy/getHeight();
					break;
				}
				isSeek=false;
				isMove=false;
				py=ppy;
				px=ppx;
				lasty=y;
				lastx=ppx;
				break;
			case MotionEvent.ACTION_MOVE:
				if(isSeek)
				{
					y=-pa.getTextSize()*data.size()*ppy/getHeight();
					break;
				}
				if(Math.abs(px-ppx)>util.px(5)||Math.abs(py-ppy)>util.px(5))
				{
					isMove=true;
					skeyboard=false;
				}
				dy=ppy-py;
				y=lasty+dy;
				break;
			case MotionEvent.ACTION_UP:
				if(!isMove&&!isSeek)
				{
					skeyboard=true;
					if(skbms>=500&&ppy>getHeight()-util.px(100))
						for(int i=0;i<keys.size();i++)
						{
							if(keys.get(i).contains(ppx,ppy))
							{
								input(i);
								return false;
							}
						}
					else
					{
						spx=ppx;
						spy=ppy;
					}
				}
				break;
		}
		if(y>0)y=0;
		return true;
	}

}
