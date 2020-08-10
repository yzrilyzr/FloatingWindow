package com.yzrilyzr.floatingwindow.view;

import android.content.*;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.myclass.*;
import com.yzrilyzr.ui.*;

public class mImageView extends View
{
	private Bitmap img;
	private Paint pa=new Paint();
	public float deltax=0,deltay=0,scale=1,lscale=1,lpointLen;
	private boolean moved=false,touch=false;
	public float ddx,ddy;
	public mImageView(Context c)
	{
		super(c);
		setLayerType(View.LAYER_TYPE_SOFTWARE,null);
		
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(img!=null&&!img.isRecycled())
		{
			pa.setColor(0xff000000);
			Matrix m=new Matrix();
			m.postTranslate(deltax,deltay);
			m.postScale(scale,scale);
			canvas.drawBitmap(img,m,pa);
			if(moved){
				pa.setTextAlign(Paint.Align.CENTER);
				String st=Integer.toString((int)(scale*100f));
				pa.setTextSize(util.px(uidata.TEXTSIZE));
				float wu=pa.measureText(st);
				float cx=getWidth()/2,cy=getHeight()/2;
				float hh=pa.getTextSize();
				pa.setColor(uidata.MAIN);
				pa.setShadowLayer(util.px(1.5f),0,util.px(2),0x50000000);
				float mg=util.px(3);
				canvas.drawRoundRect(new RectF(cx-wu/2-mg,cy-hh/2-mg,cx+wu/2+mg,cy+hh/2+mg),util.px(uidata.UI_RADIUS),util.px(uidata.UI_RADIUS),pa);
				
				pa.setColor(uidata.TEXTBACK);
				canvas.drawText(st,cx,cy+hh/2.3f,pa);
			}
		}
	}
	public void update(){
		postInvalidate();
	}
	public void setImage(Bitmap b)
	{
		img=b;
		invalidate();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int a=event.getAction();
		if(event.getPointerCount()==1)
		{
			if(a==MotionEvent.ACTION_DOWN)moved=false;
			if(a==MotionEvent.ACTION_UP)moved=false;
		}
		else if(event.getPointerCount()==2)
		{
			float x1=event.getX(1),y1=event.getY(1);
			float x=event.getX(0),y=event.getY(0);
			if(!moved)
			{
				ddx=(x+x1)/2;
				ddy=(y+y1)/2;
				lpointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
				lscale=scale;
				moved=true;
			}
			else
			{
				float pointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
				float llsc=scale;
				scale=lscale*pointLen/lpointLen;
				float cx=(x+x1)/2f,cy=(y+y1)/2f;
				deltax=(deltax-cx/llsc)+cx/scale;
				deltay=(deltay-cy/llsc)+cy/scale;
				deltax-=(ddx-(x+x1)/2)/scale;
				deltay-=(ddy-(y+y1)/2)/scale;
				ddx=(x+x1)/2;
				ddy=(y+y1)/2;
			}
		}
		invalidate();
		return true;
	}
}
