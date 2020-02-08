package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.yzrilyzr.ui.myAnim;

public class StarterView extends View
{
    private Paint paint;
    private int progress=-1;
	private long curtime;
    private boolean open=false,longcli=false,selmoved=false;
    private static float dd=util.px(50),ee=util.px(30);
    private float margin;
    private float kx,ky;
    private Listener listener;
    private static Bitmap[] bmp=new Bitmap[9];
    private RectF rect;
	private long longclick;
	private int SEL=-1,lSel;
	private static String[] tip=new String[]{"添加程序","添加程序","添加程序","添加程序","添加程序","添加程序","添加程序","退出"};
	private static String[] pkg=new String[7],cls=new String[7];
	private Matrix Matrix=new Matrix();
	public StarterView(Context c)
    {
        super(c);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
		margin=util.px(3);
        paint.setShadowLayer(margin,0,margin/3,0x50000000);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        int k=(int)ee;
        try
		{
			bmp[7]=VECfile.createBitmap(c,"exit",k,k);
			bmp[8]=VECfile.createBitmap(c,"class",(int)(k*1.5f),(int)(k*1.5f));
		}
		catch (Exception e)
		{}
		paint.setTextSize(util.px(uidata.TEXTSIZE*1.2f));
    }
	public String getPkg(int i)
	{
		return pkg[i];
	}
	public String getClass(int i)
	{
		return cls[i];
	}
	public static void load(Context ctx)
	{
		SharedPreferences s=util.getSPRead("pluginpicker");
		for(int i=0;i<7;i++)
		{
			pkg[i]=s.getString("pkg"+i,null);
			cls[i]=s.getString("cls"+i,null);
			tip[i]=s.getString("tip"+i,"添加程序");
			String tk=s.getString("ico"+i,null);
			try
			{
				if(tk!=null)
				{
					byte[] b=Base64.decode(tk,0);
					bmp[i]=BitmapFactory.decodeByteArray(b,0,b.length);
					if(bmp[i]==null)bmp[i]=VECfile.createBitmap(ctx,"add",(int)ee,(int)ee);
					else bmp[i]=bmp[i];
				}
				else bmp[i]=VECfile.createBitmap(ctx,"add",(int)ee,(int)ee);
			}
			catch (Exception e)
			{}
		}
	}

    public void open()
    {
		rect=new RectF(kx-2f*dd,ky-2f*dd,kx+2f*dd,ky+2f*dd);
		if(uidata.UI_USETYPEFACE)paint.setTypeface(uidata.UI_TYPEFACE);
        if(listener!=null)listener.onAnimStart();
        open=true;
		progress=0;
		curtime=System.currentTimeMillis();
		invalidate();
    }
    public void close()
    {
		curtime=System.currentTimeMillis();
		progress=500;
        open=false;
        invalidate();
    }
    public void setListener(Listener l)
    {
        listener=l;
    }
    public void toggle()
    {
        open=!open;
        if(open)open();
        else close();
    }
    public void setPosition(float x,float y)
    {
        kx=util.limit(x,2f*dd,util.getScreenWidth()-2f*dd);
		ky=util.limit(y,2f*dd,util.getScreenHeight()-2f*dd);
		rect=new RectF(kx-2f*dd,ky-2f*dd,kx+2f*dd,ky+2f*dd);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
		dd=util.px(50);
		ee=util.px(30);
		for(int i=0;i<8;i++)
		{
			float angle=(i+1)*(float)Math.PI/4f;
			float tt=myAnim.getNLinearValueByTime(progress,100,500);
			float r=1.5f*dd*tt;
			paint.setShadowLayer(margin,0,margin/3,0x50000000);
			paint.setColor(i==SEL?uidata.ACCENT:uidata.BACK);
			canvas.drawCircle(kx+(float)Math.cos(angle)*r,ky+(float)Math.sin(angle)*r,tt*dd*0.5f,paint);
			Matrix.reset();
			Matrix.postScale(ee/(float)bmp[i].getWidth()*tt,ee/(float)bmp[i].getHeight()*tt);
			Matrix.postTranslate(kx+(float)Math.cos(angle)*r-ee/2*tt,ky+(float)Math.sin(angle)*r-ee/2*tt);
			paint.setShadowLayer(0,0,0,0);
			canvas.drawBitmap(bmp[i],Matrix,paint);
		}
		paint.setShadowLayer(margin,0,margin/3,0x50000000);
		paint.setColor(uidata.MAIN);
		canvas.drawCircle(kx,ky,myAnim.getNLinearValueByTime(progress,0,200)*dd*0.75f,paint);
		paint.setColor(uidata.TEXTBACK);
		if(SEL<0||SEL>=tip.length)
		{
			paint.setShadowLayer(0,0,0,0);
			Matrix.reset();
			float tt=myAnim.getNLinearValueByTime(progress,0,200);
			Matrix.postScale(ee*1.5f/(float)bmp[8].getWidth()*tt,ee*1.5f/(float)bmp[8].getHeight()*tt);
			Matrix.postTranslate(kx-ee*1.5f/2*tt,ky-ee*1.5f/2*tt);
			canvas.drawBitmap(bmp[8],Matrix,paint);
		}
		else if(!isAnim())
		{
			canvas.drawText(tip[SEL],kx,ky+paint.getTextSize()/2.5f,paint);
		}
		/*canvas.drawArc(rect,-180,Math.min(progress,225),true,paint);
		 paint.setColor(uidata.MAIN);
		 if(SEL>=0&&SEL<=4&&!isAnim&&progress>=360)canvas.drawArc(rect,-180+SEL*45,45,true,paint);
		 if(progress>225)
		 {
		 canvas.drawCircle(kx,ky,dd*((float)progress-225f)/180f,paint);
		 }
		 float R2=4f/3f*dd;
		 paint.setShadowLayer(0,0,0,0);
		 for(int i=0;i<5;i++)
		 if(progress>=45*(i+1))
		 {
		 double d=arc*(22.5+45*i);
		 }
		 if(progress>=360)
		 {
		 }
		 }*/
		if(isAnim()&&open)
		{
			progress+=System.currentTimeMillis()-curtime;
			curtime=System.currentTimeMillis();
			invalidate();
		}
		else if(isAnim()&&!open)
		{
			progress-=System.currentTimeMillis()-curtime;
			curtime=System.currentTimeMillis();
			invalidate();
		}
		else if(progress<0&&!open)
		{
			if(listener!=null)listener.onAnimEnd();
		}

	}
	private boolean isAnim()
	{
		return progress>=0&&progress<=500;
	}
    public interface Listener
    {
        public abstract void onItemClick(int which);
        public abstract void onAnimEnd();
        public abstract void onAnimStart();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
        if(isAnim())return true;
        float xx=event.getX(),yy=event.getY();
        float rr=(float)Math.sqrt(Math.pow(kx-xx,2)+Math.pow(ky-yy,2));
		if(rr<dd*0.75f)SEL=8;
		else if(rr<dd*2)
        {
			double de=Math.asin((yy-ky)/rr);
			if(xx-kx<0)de=(Math.PI-de);
			de=(int)((de*180/Math.PI)-22.5);
			if(de<0)de+=360;
            SEL=(int)(de/45);
			//System.out.println(de);
			//System.out.println(SEL);
        }
		else SEL=-1;
		int act=event.getAction();
		if(act==MotionEvent.ACTION_DOWN)
		{
			longcli=false;
			lSel=SEL;
			selmoved=false;
			longclick=System.currentTimeMillis();
		}
		else if(act==MotionEvent.ACTION_MOVE)
		{
			if(lSel!=SEL)selmoved=true;
		}
		else if(act==MotionEvent.ACTION_UP&&!longcli)
		{
			if(listener!=null&&rr<dd*2)listener.onItemClick(SEL);
			close();
		}
		if(!longcli&&System.currentTimeMillis()-longclick>1000&&SEL>=0&&SEL<=6&&!selmoved)
		{
			longcli=true;
			pkg[SEL]=null;
			tip[SEL]="添加程序";
			try
			{
				bmp[SEL]=VECfile.createBitmap(util.ctx,"add",(int)ee,(int)ee);
				invalidate();
			}
			catch (Exception e)
			{}
			util.getSPWrite("pluginpicker")
			.putString("pkg"+SEL,null)
			.putString("cls"+SEL,null)
			.putString("tip"+SEL,null)
			.putString("ico"+SEL,null)
			.commit();
		}
		invalidate();
        return true;
    }

}
