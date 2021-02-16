package com.yzrilyzr.game;
import android.graphics.*;
import java.util.concurrent.*;
import android.view.*;
import java.io.*;
import com.yzrilyzr.icondesigner.*;
import java.util.regex.*;
import java.util.*;
import java.text.*;

public class Scene
{
	public CopyOnWriteArrayList<Ui> uis=new CopyOnWriteArrayList<Ui>();
	//int backcolor=0;
	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	public String id=null;
	public float time=0;
	float exittime=-1000;
	public Scene(String id)
	{
		this.id=id;
	}
	public String getId()
	{
		return id;
	}
	public boolean onTouch(MotionEvent p2)
	{
		for(int i=uis.size()-1;i>=0;i--)
		{
			Ui u=uis.get(i);
			if(u!=null&&u.onTouch(this,p2))return true;
		}
		return false;
	}
	public void clearGUI()
	{
		uis.clear();
	}
	public void setBackgroundColor(int c)
	{
		Utils.backcolor=c;
	}
	public void onDraw(Canvas c)
	{
		time+=Utils.getDtMs();
		if(exittime!=-1000)
			if(exittime>0)
			{
				exittime-=Utils.getDtMs();
				//for(Ui u:uis)u.exit=true;
			}
			else
			{
				clearGUI();
				Utils.unloadScene(id);
			}
		for(Ui u:uis)
		{
			u.onDraw(c);
		}
	}
	public Ui findUi(String s)
	{
		for(Ui ps:uis)
			if(ps.id.equals(s))return ps;
		return null;
	}
	public void removeGUI(String s)
	{
		uis.remove(findUi(s));
	}
	public void exitAnim()
	{
		exittime=0;
		for(Ui u:uis)
		{
			u.exit=true;
			for(BaseAnim b:u.eanim)exittime=Math.max(exittime,b.delay+b.duration);
		}
	}
	public void loadGUI(String s,String... replacement)
	{
		try
		{
			Pattern pat=Pattern.compile("(prandom|replacement)\\d+");
			Matcher match=pat.matcher(s);
			while(match.find())
			{
				String p=match.group();
				if(p.contains("prandom"))s=s.replaceFirst(p,Integer.toString(new Random().nextInt(Integer.parseInt(p.substring(7)))));
				else if(p.contains("replacement"))s=s.replaceAll(p,replacement[Integer.parseInt(p.substring(11))]);
			}
			String l=null;
			Ui buf=null;
			String[] ppp=s.split("\n");
			for(int fg=0;fg<ppp.length;fg++)
			{
				l=ppp[fg];
				if(l.startsWith("#")||l.replace(" ","").length()==0)continue;
				if(l.startsWith("@"))
				{
					buf=new Ui();
					buf.id=l.substring(1);
					if(findUi(buf.id)!=null)throw new IllegalArgumentException("GUI \""+buf.id+"\" 的ID重复");
					continue;
				}
				else if(l.equals("*"))
				{
					if(buf==null)continue;
					buf.init();
					uis.add(buf);
					buf=null;
				}
				else
				{
					int c=l.indexOf(":");
					if(c==-1)throw new IndexOutOfBoundsException("语法错误 未知的 \""+l+"\"\n @行"+(fg+1));
					String t=l.substring(0,c),p=l.substring(c+1);
					switch(t)
					{
						case "vec":
							if(p.contains("file:"))buf.vec=VECfile.readFile(Utils.mainDir+"/vec/"+p.substring(5)+".vec");
							else buf.vec=VECfile.readFileFromIs(Utils.ctx.getAssets().open(p+".vec"));
							break;
						case "gravity":
							buf.gravity=Integer.parseInt(p);
							break;
						case "parent":
							if(p.contains("/"))
							{
								String[] tt=p.split("/");
								Scene sc=Utils.findScene(tt[0]);
								if(sc==null)throw new NullPointerException("Scene \""+tt[0]+"\" 未找到");
								Ui parent=sc.findUi(tt[1]);
								if(parent==null)throw new NullPointerException("Parent \""+tt[1]+"\" 未找到");
								parent.child.add(buf);
								buf.parent=parent;
							}
							else
							{
								Ui parent=findUi(p);
								if(parent==null)throw new NullPointerException("Parent \""+p+"\" 未找到");
								parent.child.add(buf);
								buf.parent=parent;
							}
							break;
						case "animclickable":
							buf.animclickable=Boolean.parseBoolean(p);
							break;
						case "event":
							buf.event=p;
							break;
						case "size"://%  p  d  s
							String[] d=p.split(",");
							buf.width=Utils.parseUiNumExp(buf,0,d[0]);
							buf.height=Utils.parseUiNumExp(buf,1,d[1]);
							buf.measure();
							break;
						case "pos"://%  p  d  s
							String[] d2=p.split(",");
							buf.x=Utils.parseUiNumExp(buf,0,d2[0]);
							buf.y=Utils.parseUiNumExp(buf,1,d2[1]);

							break;
						case "color":
							buf.backcolor=(int)Long.parseLong(p,16);
							break;
						case "bound":
							buf.bound=Boolean.parseBoolean(p);
							break;
						case "show":
							buf.show=Boolean.parseBoolean(p);
							break;
						case "anim":
						case "eanim":
							BaseAnim an=null;
							String[] h=p.split("\\|");
							for(String j:h)
							{
								String[] k=j.split(":");
								String q=k[1];
								switch(k[0])
								{
									case "type":
										if(q.equals("alpha"))an=new AlphaAnim(buf.p);
										if(q.equals("translate"))an=new TransAnim(buf.matrix);
										if(q.equals("scale"))an=new ScaleAnim(buf.matrix);
										if(q.equals("rotate"))an=new RotateAnim(buf.matrix);
										break;
									case "duration":
										an.duration=Integer.parseInt(q);
										break;
									case "delay":
										an.delay=Integer.parseInt(q);
										break;
									case "fromto":
										String[] jk=q.split(",");
										float[] pp=new float[jk.length];
										for(int i=0;i<jk.length;i++)
										{
											/*if(an instanceof TransAnim||an instanceof RotateAnim||an instanceof)pp[i]=(int)Utils.parseUiNumExp(buf,0,jk[i]);
											 else if(an instanceof AlphaAnim)
											 pp[i]=Integer.parseInt(jk[i]);*/
											pp[i]=Utils.parseUiNumExp(buf,i%2==0?0:1,jk[i]);
										}
										an.fromto=pp;
										break;
									case "pos":
										String[] d3=q.split(",");
										an.cx=Utils.parseUiNumExp(buf,0,d3[0]);
										an.cy=Utils.parseUiNumExp(buf,1,d3[1]);
										break;
								}

							}
							if(t.equals("anim"))buf.anim.add(an);
							else if(t.equals("eanim"))buf.eanim.add(an);
							break;
						default:
							throw new IndexOutOfBoundsException("语法错误 未知的 \""+l+"\"\n @行"+(fg+1));
					}
				}
			}
		}
		catch(IOException e)
		{
			Utils.alert(e);
		}
	}
}
