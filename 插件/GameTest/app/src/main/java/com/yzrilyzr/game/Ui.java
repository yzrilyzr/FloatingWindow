package com.yzrilyzr.game;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;

public class Ui
{
	public String id;
	public VECfile vec;
	public float x=0,y=0,width=1000,height=1000,mx,my;
	public Bitmap bmp;
	public Matrix matrix=new Matrix();
	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	public int backcolor=0;
	public RectF rectf;
	public int gravity;
	public CopyOnWriteArrayList<BaseAnim> anim=new CopyOnWriteArrayList<BaseAnim>();

	public boolean bound=false;
	public void onTouch(MotionEvent p2)
	{
		// TODO: Implement this method
	}
	public void measure(){
		if((int)height==-2)height=width*(float)vec.getHeight()/(float)vec.getWidth();
	}
	public void init()
	{
		//if(height==-2)height=100;
		if(vec!=null)
		{
			bmp=VECfile.createBitmap(vec,(int)width,(int)height);
			//Utils.alert(width+"h"+height);
			p.setColor(0xff000000);
		}
		//if(bmp==null)bmp=Bitmap.createBitmap((int)width,(int)height,Bitmap.Config.ARGB_8888);
		mx=0;my=0;
		if(gravity==2||gravity==5||gravity==8)mx=Utils.getWidth()/2-width/2;
		if(gravity==4||gravity==5||gravity==6)my=Utils.getHeight()/2-height/2;
		
		if(gravity==7||gravity==9)my=Utils.getHeight()-height;
		if(gravity==3||gravity==9)mx=Utils.getWidth()-width;
		rectf=new RectF(x+mx,y+my,x+mx+width,y+my+height);
	}
	public void onDraw(Canvas c)
	{
		matrix.reset();
		matrix.setTranslate(x+mx,y+my);
		for(BaseAnim an:anim)an.doAnim();
		if(bound||backcolor!=0)c.saveLayer(rectf,p);
		if(backcolor!=0)c.drawColor(backcolor);
		if(bmp!=null)c.drawBitmap(bmp,matrix,p);
		if(bound||backcolor!=0)c.restore();
	}
}
