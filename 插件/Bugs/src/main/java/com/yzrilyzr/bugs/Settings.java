package com.yzrilyzr.bugs;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.engine2d.*;

public class Settings extends Scene
{
	Timer in,out;
	Button close;
	Ui mask,setting;
	@Override
	public void render(Canvas c, float dt)
	{
		/*if(in!=null)
			switch(in.render(dt))
			{
				case 0:
					anim(true,close);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(in.getNLF()*128));
					c.drawPaint(Eg.p);
					setting.draw(c,
						0,-100+100*in.getNLF()+30*in.getSF(),
						1,0,0,
						0,0,0,
						in.getNLF()
					);
					close.draw(c,
						0,-100+100*in.getNLF()+30*in.getSF(),
						1,0,0,
						0,0,0,
						in.getNLF()
					);
					/*Eg.drawVec(c,"vec/settings/close",Eg.Gravity.CENTER,10,22,-35,
						0,-100+100*in.getNLF()+30*in.getSF(),
						1,0,0,
						0,0,0,
						in.getNLF()
					);*/
					/*break;
				case 500:
					anim(false,close);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha(128);
					c.drawPaint(Eg.p);
					setting.draw(c);
					close.draw(c);
					break;
			}
		else if(out!=null)
			switch(out.render(dt))
			{
				case 0:
					anim(true,close);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(128-out.getNLF()*128));
					c.drawPaint(Eg.p);
					Eg.drawVec(c,"vec/settings/setting",Eg.Gravity.CENTER,90,0,0,
						0,100*out.getNLF(),
						1-out.getNLF(),50,50,
						0,0,0,
						1-out.getNLF()
					);
					Eg.drawVec(c,"vec/settings/close",Eg.Gravity.CENTER,10,22,-35,
						0,100*out.getNLF(),
						1-out.getNLF(),-400,400,
						0,0,0,
						1-out.getNLF()
					);
					break;
				case 500:
					removeSelf();
					break;
			}*/
	}

	@Override
	public void start()
	{
		in=new Timer(0,500);
		mask=new Ui(0,0,Eg.getAbsWidth(),Eg.getAbsHeight());
		close=new Button("vec/settings/close",10,Eg.Gravity.RIGHT|Eg.Gravity.TOP,-5,5,null){
			public void onClick(MotionEvent e)
			{
				//this.anim=true;
				in=null;
				out=new Timer(0,500);
			}
		};
		//setting=new Ui("vec/settings/setting",Eg.Gravity.CENTER,90,0,0);
		close.parent=setting;
		add(mask,setting,close);
	}
	
	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
	
}
