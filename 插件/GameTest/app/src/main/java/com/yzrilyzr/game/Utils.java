package com.yzrilyzr.game;
import com.yzrilyzr.bugs.*;
import android.content.*;
import java.io.*;
import android.app.*;
import java.util.regex.*;
import android.content.res.*;
import java.util.*;
import java.lang.reflect.*;

public class Utils
{
	public static String mainDir="";
	public static com.yzrilyzr.game.MainActivity ctx;
	protected static float dt;
	public static int fpslimit=30;
	public static boolean showfps=false;
	protected static int draws=0;
	protected static int backcolor=0;

	public static float getDtMs()
	{
		// TODO: Implement this method
		return dt/1000000f;
	}
	public static void unloadAll()
	{
		ctx.scenes.clear();
	}
	public static float px(float dipValue)
    {
        return  (dipValue *Resources.getSystem().getDisplayMetrics().density)*(ctx.sv.getHeight()>ctx.sv.getWidth()?(float)ctx.sv.getWidth()/(float)ctx.sv.getHeight():1f);//*(getScreenHeight()>getScreenWidth()?1:0.5f));
	}
	public static float parseUiNumExp(Ui ui,int widthOrHeight,String p)
	{
		Pattern pat=Pattern.compile("(\\+|\\-)?(random)?\\d+(\\.\\d+)?(%|p|d|s)?");
		Matcher m=pat.matcher(p);
		float v=0;
		while(m.find())
		{
			String f=m.group();
			float num=parseUiNum(ui,widthOrHeight,f);
			v+=num;
		}
		return v;
	}
	public static float parseUiNum(Ui ui,int widthOrHeight,String p)
	{
		//%占比，d相对像素，p绝对像素，省略为绝对像素
		//s参照自身大小，只用于%
		//50%s 自身的50%
		//20p  20个像素
		//34ds 34个相对像素(s被忽略)
		float f=0;
		if(p.startsWith("random"))f=new Random().nextFloat()*Float.parseFloat(p.substring(6));
		else if(p.endsWith("%"))f=(widthOrHeight==1?(ui.parent==null?getHeight():ui.parent.height):(ui.parent==null?getWidth():ui.parent.width))*Float.parseFloat(p.substring(0,p.length()-1))/100f;
		else if(p.endsWith("s"))f=(widthOrHeight==1?ui.height:ui.width)*Float.parseFloat(p.substring(0,p.length()-1))/100f;
		else if(p.endsWith("d"))f=px(Float.parseFloat(p.substring(0,p.length()-1)));
		else if(p.endsWith("p"))f=Float.parseFloat(p.substring(0,p.length()-1));
		else f=Float.parseFloat(p);
		return f;
	}
	public static float getWidth()
	{
		// TODO: Implement this method
		return ctx.sv.getWidth();
	}
	public static float getHeight()
	{
		float width=getWidth(),height=ctx.sv.getHeight();
		if(height>width)height=width*width/height;
		return height;
	}
	public static void print(Object a){
		ctx.sendBroadcast(new Intent().setAction("com.yzrilyzr.sysprinter").putExtra("out",a+""));
	}
	public static void alert(final Object ep)
	{
		ctx.runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					String a=ep+"";
					Object e=ep;
					if(e instanceof Throwable)
					{
						if(e instanceof InvocationTargetException){
							e=((InvocationTargetException)e).getTargetException();
						}
						ByteArrayOutputStream b=new ByteArrayOutputStream();
						PrintWriter p=new PrintWriter(b);
						((Throwable)e).printStackTrace(p);
						p.flush();
						p.close();
						a=b.toString();
						ctx.sendBroadcast(new Intent().setAction("com.yzrilyzr.sysprinter").putExtra("err",a));
					}
					new AlertDialog.Builder(ctx)
						.setMessage(a)
						.show();
				}
			});
	}
	public static String readTxt(String path)
	{
		try
		{
			File f=new File(path);
			BufferedReader fr=null;
			if(!f.exists())fr=new BufferedReader(new InputStreamReader(ctx.getAssets().open(path.replace(mainDir,""))));
			else fr=new BufferedReader(new FileReader(f));
			StringBuilder sb=new StringBuilder();
			String bf=null;
			while((bf=fr.readLine())!=null)sb.append(bf).append("\n");
			return sb.substring(0,sb.length()-1);
		}
		catch(Throwable e)
		{
			alert(e);
		}
		return null;
	}
	public static void setContext(com.yzrilyzr.game.MainActivity p0)
	{
		ctx=p0;
	}
	public static void setMainDir(String path)
	{
		mainDir=path;
	}
	public static void loadScene(Scene id)
	{
		if(findScene(id.id)!=null)throw new IllegalArgumentException("Scene \""+id.id+"\" 的ID重复");
		ctx.scenes.add(id);
	}
	public static Scene findScene(String id)
	{
		for(Scene s:ctx.scenes)
			if(s.id.equals(id))return s;
		return null;
	}
	public static Scene unloadScene(String id)
	{
		for(Scene s:ctx.scenes)
		{
			if(id.equals(s.id))
			{
				ctx.scenes.remove(s);
				return s;
			}
		}
		return null;
	}
	public static float getFuncXByTime(float time,float starttime,float endtime)
	{
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		return limit((time-starttime)/sec,0f,1f);
	}
	public static float getNLinearValueByTime(float time,float starttime,float endtime)
	{
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=limit((time-starttime)/sec,0f,1f);
		return NonLinearFunc(x);
	}
	public static float NonLinearFunc(float x)
	{
		x=limit(x,0,1);
		float y=x<0.5?(float)Math.pow(x,2)*2f:-(float)Math.pow(x-1f,2)*2f+1f;
		return limit(y,0,1);
	}
	public static float getSinValueByTime(float time,float starttime,float endtime)
	{
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=limit((time-starttime)/sec,0f,1f);
		return SinFunc(x);
	}
	public static float SinFunc(float x)
	{
		x=limit(x,0,1);
		float y=(float)Math.sin(x*Math.PI);
		return limit(y,0,1);
	}

	public static float limit(float x,float min,float max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static long limit(long x,long min,long max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int limit(int x,int min,int max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int random(int min,int max)
    {
        return (int)Math.floor(Math.random()*(max-min))+min;
    }
	public static double getArc(double x,double y,double r)
	{
		double a=Math.asin(y/r);
		if(x<0)a=Math.PI-a;
		if(x>0&&y<0)a+=2*Math.PI;
		return a;
	}

}
