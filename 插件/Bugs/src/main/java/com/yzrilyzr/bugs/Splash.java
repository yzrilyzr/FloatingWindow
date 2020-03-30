package com.yzrilyzr.bugs;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;

public class Splash extends Scene
{
	Timer t;
	@Override
	public void render(Canvas c, float dt)
	{
		switch(t.render(dt))
		{
			case 0:
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha((int)(t.getNLF()*255));
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,0,-10f*t.getSF(),(t.getNLF()+1)*0.5f,50,50,0,0,0,t.getNLF());
				break;
			case 600:
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha(255);
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,0,0,1,0,0,0,0,0,1);
				break;
			case 1400:
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha(255-(int)(t.getNLF()*255));
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,0,0,1,0,0,0,0,0,1-t.getNLF());
				break;
			case 2000:
				Eg.startScene(new StartAnim());
				removeSelf();
				break;
		}
	}

	@Override
	public void start()
	{
		Eg.setBackground(0xff000000);
		t=new Timer(0,600,1400,2000);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
}
