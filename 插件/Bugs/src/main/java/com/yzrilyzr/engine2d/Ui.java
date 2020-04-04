package com.yzrilyzr.engine2d;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;

public class Ui
{
	//public int gravity;
	//public float dx,dy,si;
	//public boolean anim;
	public CopyOnWriteArrayList<Timer> anim=new CopyOnWriteArrayList<Timer>();
	public Bitmap bmp;
	public float r=0;
	public RectF rect=new RectF();
	public Ui parent;
	public float absX,absY,absW,absH;
	public Matrix matrix=new Matrix();
	public Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
	public Canvas canvas;
	public boolean show=true;
	public void draw(Canvas c,float dt)
	{
		for(Timer t:anim)t.render(dt);
		if(show)
		{
			matrix.postTranslate(absX,absY);
			paint.setStyle(Paint.Style.FILL);
			c.drawBitmap(bmp,matrix,paint);
			rect.set(0,0,absW,absH);
			matrix.mapRect(rect);
			if(Eg.showgrid)
			{
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setColor(0xff0000ff);
				if(r==0)c.drawRect(rect,paint);
				else c.drawCircle(rect.centerX(),rect.centerY(),r,paint);
			}
			matrix.reset();
		}
		//Eg.drawVec(c,vec,gravity,si,dx,dy,rect,parent==null?null:parent.rect,0,0,0,0,1,0,0,0,0,0,1);
	}
	public static float[] getPosition(int gravity,float dx,float dy,float w,float h,Ui ref)
	{
		float x=0,y=0;
		RectF r=null;
		if(ref!=null)r=ref.rect;
		else r=new RectF(0,0,getAbsWidth(),getAbsHeight());
		if(hasFlag(gravity,G.C))
		{
			x=r.centerX()-w/2;
			y=r.centerY()-h/2;
		}
		if(hasFlag(gravity,G.L))x=r.left;
		if(hasFlag(gravity,G.R))x=r.right-w;
		if(hasFlag(gravity,G.T))y=r.top;
		if(hasFlag(gravity,G.B))y=r.bottom-h;
		if(hasFlag(gravity,G.OL))x=r.left-w;
		if(hasFlag(gravity,G.OR))x=r.right;
		if(hasFlag(gravity,G.OT))y=r.top-h;
		if(hasFlag(gravity,G.OB))y=r.bottom;
		x+=r.width()*dx/100f;
		y+=r.height()*dy/100f;
		return new float[]{x,y};
	}
	public static boolean hasFlag(long flag,long f)
	{
		return (flag&f)==f;
	}
	public void onClick(MotionEvent e)
	{}
	public void onMove(MotionEvent e)
	{}
	public void onDown(MotionEvent e)
	{}

	public static boolean down(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_DOWN;
	}
	/*public boolean isOutOfScr(){
	 return x<0||y<0||x>p(1600)||y>p(900);
	 }*/
	public static boolean move(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_MASK;
	}
	public static boolean up(MotionEvent e)
	{
		return e.getAction()==MotionEvent.ACTION_UP;
	}
	public boolean contains(float x,float y)
	{
		return r==0?
			x>rect.left&&x<rect.right&&y>rect.top&&y<rect.bottom:
			Math.pow(x-rect.centerX(),2)+Math.pow(y-rect.centerY(),2)<r*r;
	}
	/*public boolean contains(Ui o){
	 return r==0?x>rect.left&&x<rect.left+w&&y>rect.top&&y<rect.top+h:(x-o.x)*(x-o.x)+(y-o.y)*(y-o.y)<(r+o.r)*(r+o.r);
	 }*/
	//100:255
	public Ui alpha(final int delay,final int duration,final float from,final float to,final Interpolator in)
	{
		anim.add(new Timer(0,delay,delay+duration){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						paint.setAlpha((int)((from+(to-from)*(in==null?getNLF():in.get(getFuncX())))*2.55f));
					}
					else if(ii==delay+duration)
					{
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	//100:1
	public Ui scale(final int delay,final int duration,final float fromx,final float fromy,final float tox,final float toy,final float cx,final float cy,final Interpolator inX,final Interpolator inY)
	{
		anim.add(new Timer(0,delay,delay+duration){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						matrix.postScale(
						(fromx+(tox-fromx)*(inX==null?getNLF():inX.get(getFuncX())))/100f,
						(fromy+(toy-fromy)*(inY==null?getNLF():inY.get(getFuncX())))/100f,
						cx*absW/100f,cy*absH/100f);
					}
					else if(ii==delay+duration)
					{
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	//360:360
	public Ui rotate(final int delay,final int duration,final float from,final float to,final float cx,final float cy,final Interpolator in)
	{
		anim.add(new Timer(0,delay,delay+duration){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						matrix.postRotate(from+(to-from)*(in==null?getNLF():in.get(getFuncX())),cx*absW/100f,cy*absH/100f);
					}
					else if(ii==delay+duration)
					{
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	//100:1
	public Ui translate(final int delay,final int duration,final float fromX,final float fromY,final float toX,final float toY,final Interpolator inX,final Interpolator inY)
	{
		anim.add(new Timer(0,delay,delay+duration){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						matrix.postTranslate(absW*(fromX+(toX-fromX)*(inX==null?getNLF():inX.get(getFuncX())))/100f,absH*(fromY+(toY-fromY)*(inY==null?getNLF():inY.get(getFuncX())))/100f);
					}
					else if(ii==delay+duration)
					{
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	public Ui translate(final int delay,final int duration,
	final int fromGra,final float fromX,final float fromY,final float fromW,final float fromH,final Ui fromUi,
	final int toGra,final float toX,final float toY,final float toW,final float toH,final Ui toUi,
	final Interpolator inX,final Interpolator inY)
	{
		anim.add(new Timer(0,delay,delay+duration){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						float[] fp=getPosition(fromGra,fromX,fromY,fromW*absW/100f,fromH*absH/100f,fromUi);
						float[] tp=getPosition(toGra,toX,toY,toW*absW/100f,toH*absH/100f,toUi);
						matrix.postTranslate(
						-absX+fp[0]+(tp[0]-fp[0])*(inX==null?getNLF():inX.get(getFuncX())),
						-absY+fp[1]+(tp[1]-fp[1])*(inY==null?getNLF():inY.get(getFuncX())));
					}
					else if(ii==delay+duration)
					{
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	public Ui alpha(int delay,int duration,float from,float to)
	{
		return alpha(delay,duration,from,to,null);
	}
	public Ui scale(int delay,int duration,float from,float to)
	{
		return scale(delay,duration,from,from,to,to,50,50,null,null);
	}
	public Ui rotate(int delay,int duration,float from,float to)
	{
		return rotate(delay,duration,from,to,50,50,null);
	}
	public Ui translate(int delay,int duration,float fromX,float fromY,float toX,float toY)
	{
		return translate(delay,duration,fromX,fromY,toX,toY,null,null);
	}
	public Ui translate(final int delay,final int duration,
		final int fromGra,final float fromX,final float fromY,final float fromW,final float fromH,final Ui fromUi,
		final int toGra,final float toX,final float toY,final float toW,final float toH,final Ui toUi){
		return translate(delay,duration,fromGra,fromX,fromY,fromW,fromH,fromUi,toGra,toX,toY,toW,toH,toUi,null,null);
	}
	public Ui delayShow(final int delay)
	{
		anim.add(new Timer(0,delay){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						show=true;
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	public Ui delayDismiss(final int delay)
	{
		anim.add(new Timer(0,delay){
				public int render(float dt)
				{
					int ii=super.render(dt);
					if(ii==delay)
					{
						show=false;
						anim.remove(this);
					}
					return ii;
				}
			});
		return this;
	}
	public Ui(String vec,float size,int g,float dx,float dy,Ui parent)
	{
		Bitmap b=null;
		try
		{
			VECfile vf=VECfile.readFileFromIs(Eg.gameact.getAssets().open(vec+".vec"));
			int w=1,h=1;
			if(getAbsHeight()>getAbsWidth())
			{
				w=(int)(getAbsWidth()*size/100f);
				h=(int)(getAbsWidth()*size/100f*vf.getHeight()/vf.getWidth());
			}
			else
			{
				h=(int)(getAbsHeight()*size/100f);
				w=(int)(getAbsHeight()*size/100f*vf.getWidth()/vf.getHeight());
			}
			b=VECfile.createBitmap(vf,w,h);
		}
		catch(Throwable e)
		{
			Eg.gameact.toast(e);
			return;
		}
		bmp=b;
		absW=b.getWidth();
		absH=b.getHeight();
		float[] p=getPosition(g,dx,dy,absW,absH,parent);
		absX=p[0];
		absY=p[1];
		paint.setColor(0xffffffff);
		rect.set(absX,absY,absX+absW,absY+absH);
	}
	public Ui(float x,float y,float w,float h)
	{
		bmp=Bitmap.createBitmap((int)(w*getAbsWidth()/100f),(int)(h*getAbsHeight()/100f),Bitmap.Config.ARGB_8888);
		canvas=new Canvas(bmp);
		absX=x*getAbsWidth()/100f;
		absY=y*getAbsHeight()/100f;
		absW=w*getAbsWidth()/100f;
		absH=h*getAbsHeight()/100f;
		paint.setColor(0xffffffff);
		rect.set(absX,absY,absX+absW,absY+absH);
	}
	public static float getAbsWidth()
	{
		return Eg.getAbsWidth();
	}
	public static float getAbsHeight()
	{
		return Eg.getAbsHeight();
	}
	public static class G
	{
		public static int C=1,L=2,R=4,T=8,B=16,OL=32,OT=64,OR=128,OB=256;
	}
	public static abstract class Interpolator
	{
		public abstract float get(float x);
	}
}
