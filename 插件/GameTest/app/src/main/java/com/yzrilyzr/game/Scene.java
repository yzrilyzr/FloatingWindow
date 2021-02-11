package com.yzrilyzr.game;
import android.graphics.*;
import java.util.concurrent.*;
import android.view.*;
import java.io.*;
import com.yzrilyzr.icondesigner.*;
import java.util.regex.*;
import java.util.*;

public class Scene
{
	public CopyOnWriteArrayList<Ui> uis=new CopyOnWriteArrayList<Ui>();
	int backcolor=0;
	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	String id=null;
	public float time=0;
	public Scene(String id){
		this.id=id;
	}
	public void onTouch(MotionEvent p2)
	{
		for(Ui u:uis)
		{
			u.onTouch(p2);
		}
	}
	public void clearGUI(){
		uis.clear();
	}
	public void setBackgroundColor(int c)
	{
		backcolor=c;
	}
	public void onDraw(Canvas c)
	{
		if(backcolor!=0)c.drawColor(backcolor);
		for(Ui u:uis)
		{
			u.onDraw(c);
		}
		time+=Utils.dt;
	}
	public void loadGUI(String s)
	{
		try
		{
			BufferedReader r=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
			String l=null;
			Ui buf=null;
			Pattern pat=Pattern.compile("prandom\\d+");
			while((l=r.readLine())!=null)
			{
				Matcher match=pat.matcher(l);
				while(match.find()){
					String p=match.group();
					l=l.replaceFirst(p,Integer.toString(new Random().nextInt(Integer.parseInt(p.substring(7)))));
				}
				if(l.startsWith("#")||l.replace(" ","").length()==0)continue;
				if(l.startsWith("@"))
				{
					buf=new Ui();
					buf.id=l.substring(1);
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
					String t=l.substring(0,c),p=l.substring(c+1);
					switch(t)
					{
						case "vec":
							buf.vec=VECfile.readFileFromIs(Utils.ctx.getAssets().open(p+".vec"));
							break;
						case "gravity":
							buf.gravity=Integer.parseInt(p);
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
						case "anim":
							BaseAnim an=null;
							String[] h=p.split("\\|");
								for(String j:h){
									String[] k=j.split(":");
									String q=k[1];
									switch(k[0]){
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
											int[] pp=new int[jk.length];
											for(int i=0;i<jk.length;i++){
												/*if(an instanceof TransAnim||an instanceof RotateAnim||an instanceof)pp[i]=(int)Utils.parseUiNumExp(buf,0,jk[i]);
												else if(an instanceof AlphaAnim)
													pp[i]=Integer.parseInt(jk[i]);*/
												pp[i]=(int)Utils.parseUiNumExp(buf,i%2==0?0:1,jk[i]);
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
							buf.anim.add(an);
							break;
					}
				}
			}
		}
		catch(Throwable e)
		{
			Utils.alert(e);
		}
	}
}
