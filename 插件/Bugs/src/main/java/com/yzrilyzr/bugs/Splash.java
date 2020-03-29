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
				float t1=Eg.getNLinearValueByTime(t.timer,0,600);
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha((int)(t1*255));
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,
					0,0,
					t1*0.5f+0.5f,50,50,
					0,0,0,
					t1);
				break;
			case 600:
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha(255);
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,0,0,1,0,0,0,0,0,1);
				break;
			case 1400:
				float t11=Eg.getNLinearValueByTime(t.timer,1400,2000);
				Eg.p.setColor(0xff55aaff);
				Eg.p.setAlpha(255-(int)(t11*255));
				c.drawPaint(Eg.p);
				Eg.drawVec(c,"yzrilyzr",Eg.Gravity.CENTER,40,0,-10,
					0,0,
					1,0,0,
					0,0,0,
					1-t11);
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
