package com.yzrilyzr.bugs;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;

public class MainMenu extends Scene
{
	Timer in,out;
	@Override
	public void render(Canvas c, float dt)
	{
		switch(in.render(dt))
		{
			case 0:
				float tin=Eg.getNLinearValueByTime(in.timer,0,500);
				Eg.drawVec(c,"vec/mainmenu/mainmenutitle",Eg.Gravity.CENTER|Eg.Gravity.TOP,35,0,0,0,0,1.5f-tin*0.5f,50,50,0,0,0,tin);
				break;
			case 500:
				Eg.drawVec(c,"vec/mainmenu/mainmenutitle",Eg.Gravity.CENTER|Eg.Gravity.TOP,35);
				
				break;
		}
		/*mainmenubuttback=new Ui("mainmenubuttback",975,350,400,500)
		 .tScFrom(975,400,400,500,250)
		 .alphaFrom(0,500);
		 mainmenutitle=new Ui("mainmenutitle",450,0,700,300)
		 .tScFrom(400,0,800,300,250)
		 .alphaFrom(0,500);
		 mainmenustart=new Ui(null,1010,380,330,100){
		 @Override
		 public void onClick(MotionEvent e)
		 {
		 uiSelLevel();
		 }
		 };
		 mainmenusettings=new Ui(null,1025,700,175,100){
		 @Override
		 public void onClick(MotionEvent e)
		 {
		 uisettings();
		 }
		 };
		 mainmenuabout=new Ui(null,1175,700,175,100){
		 @Override
		 public void onClick(MotionEvent e)
		 {
		 uiabout();
		 }
		 };
		 mainmenuyzr=new Ui("mainmenuyzr",100,250,600,650){
		 String[] uh=null;
		 Ui tip=null;
		 public Ui init()
		 {
		 StringBuilder sb=new StringBuilder();
		 try
		 {
		 String g=null;
		 BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("tips.txt")));
		 while((g=br.readLine())!=null)sb.append(g).append("\n");
		 br.close();
		 }
		 catch(Throwable pe)
		 {
		 toast(pe);
		 }
		 uh=sb.toString().split("\n");	
		 return this;
		 }
		 @Override
		 public void onClick(MotionEvent e)
		 {
		 final String[] d=uh[new Random().nextInt(uh.length)].split("\\\\n");
		 if(tip==null)
		 {
		 tip=new Ui("mainmenutip",100,50,350,300){
		 @Override public void onDraw(Canvas c)
		 {
		 super.onDraw(c);
		 if(!isAnim()&&visible)
		 {
		 p.setColor(0xff000000);
		 p.setTextSize(p(28));
		 for(int i=0;i<d.length;i++)
		 c.drawText(d[i],x+p(25),y+(i+1)*p(50),p);
		 }
		 }
		 };
		 ui.remove(tip);
		 ui.add(ui.indexOf(mainmenuyzr)+1,tip);
		 tip.tScFrom(350,300,50,50,200)
		 .alphaFrom(0,200);
		 new Thread(new Runnable(){
		 @Override
		 public void run()
		 {
		 try
		 {
		 Thread.sleep(2000);
		 if(tip.visible)tip.tScTo(350,300,50,50,200);
		 Thread.sleep(200);
		 ui.remove(tip);
		 tip=null;
		 }
		 catch (InterruptedException e)
		 {}
		 }
		 }).start();
		 }
		 }
		 }.init();
		 }
		 else
		 {
		 mainmenubuttback.tScFrom(975,400,400,500,250).alphaFrom(0,500);
		 mainmenutitle.tScFrom(400,0,800,300,250).alphaFrom(0,500);
		 mainmenuyzr.alphaFrom(0,250);
		 mainmenustart.tScFrom(975,400,400,500,250).alphaFrom(0,500);
		 mainmenuabout.tScFrom(975,400,400,500,250).alphaFrom(0,500);
		 mainmenusettings.tScFrom(975,400,400,500,250).alphaFrom(0,500);
		 }
		 new Thread(new Runnable(){
		 @Override
		 public void run()
		 {
		 int lres=resolution;
		 while(curui==1)
		 {
		 try
		 {
		 Ui t=new Ui("mainmenuw",625,350,100,100).tScFrom(600,350,0,0,1000).alphaTo(0,1000);
		 ui.remove(t);
		 ui.add(ui.indexOf(mainmenuyzr)+1,t);
		 int i=0;
		 while(i++<2000&&resolution==lres&&curui==1)Thread.sleep(1);
		 if(resolution!=lres)break;
		 ui.remove(t);
		 }
		 catch (Exception e)
		 {
		 break;
		 }
		 }
		 }
		 }).start();*/


	}

	@Override
	public void start()
	{
		new Button("vec/mainmenu/mainmenuyzr",Eg.Gravity.BOTTOM|Eg.Gravity.LEFT,70,6,-2)
		{}.add();
		in=new Timer(0,500);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
}
