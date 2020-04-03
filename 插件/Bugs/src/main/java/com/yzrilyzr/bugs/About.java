package com.yzrilyzr.bugs;

import com.yzrilyzr.engine2d.*;
import android.graphics.*;
import android.view.*;

public class About extends Scene
{
	Timer in,out;
	Button ok;
	Ui mask;
	@Override
	public void render(Canvas c, float dt)
	{
		if(in!=null)
			switch(in.render(dt))
			{
				case 0:
					anim(true,ok);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(in.getNLF()*128));
					c.drawPaint(Eg.p);
					Eg.drawVec(c,"vec/mainmenu/uiabout",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,90,-3,-21,null,null,
						0,0,in.getNLF(),Eg.Gravity.CENTER,
						in.getNLF(),100,100,
						0,0,0,
						in.getNLF()
					);
					Eg.drawVec(c,"vec/mainmenu/uiaboutok",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,10,-3,-21,null,null,
						0,35,in.getNLF(),Eg.Gravity.CENTER,
						in.getNLF(),100,100,
						0,0,0,
						in.getNLF()
					);
					break;
				case 500:
					anim(false,ok);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha(128);
					c.drawPaint(Eg.p);
					Eg.drawVec(c,"vec/mainmenu/uiabout",Eg.Gravity.CENTER,90,0,0);
					ok.draw(c);
					break;
			}
		else if(out!=null)
			switch(out.render(dt))
			{
				case 0:
					anim(true,ok);
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(128-out.getNLF()*128));
					c.drawPaint(Eg.p);
					Eg.drawVec(c,"vec/mainmenu/uiabout",Eg.Gravity.CENTER,90,0,0,null,null,
						-3,-21,out.getNLF(),Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,
						1-out.getNLF(),100,100,
						0,0,0,
						1-out.getNLF()
					);
					Eg.drawVec(c,"vec/mainmenu/uiaboutok",Eg.Gravity.CENTER,90,0,35,null,null,
						-3,-21,out.getNLF(),Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,
						1-out.getNLF(),100,100,
						0,0,0,
						1-out.getNLF()
					);
					break;
				case 500:
					removeSelf();
					break;
			}
	}

	@Override
	public void start()
	{
		in=new Timer(0,500);
		mask=new Ui(0,0,Eg.getAbsWidth(),Eg.getAbsHeight());
		ok=new Button("vec/mainmenu/uiaboutok",Eg.Gravity.CENTER,10,0,35){
			public void onClick(MotionEvent e)
			{
				//this.anim=true;
				in=null;
				out=new Timer(0,500);
			}
		};
		add(mask,ok);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}


}
