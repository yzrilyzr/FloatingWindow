package com.yzrilyzr.bugs;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;

public class Splash extends Scene
{
	Timer t;
	Ui yzr,back;
	@Override
	public void render(Canvas c, float dt)
	{
		Eg.p.setColor(0xff55aaff);
		switch(t.render(dt))
		{
			case 0:
				Eg.p.setAlpha((int)(t.getNLF()*255));
				c.drawPaint(Eg.p);
				break;
			case 600:
				Eg.p.setAlpha(255);
				c.drawPaint(Eg.p);
				break;
			case 1400:
				Eg.p.setAlpha(255-(int)(t.getNLF()*255));
				c.drawPaint(Eg.p);
				break;
			case 2000:
				Eg.startScene(new StartAnim());
				removeSelf();
				break;
		}
		super.render(c,dt);
	}

	@Override
	public void start()
	{
		Eg.setBackground(0xff000000);
		t=new Timer(0,600,1400,2000);
		yzr=new Ui("yzrilyzr",40,Ui.G.C,0,-10,null)
			.alpha(0,600,0,100)
			.scale(0,600,50,100)
			.translate(0,600,0,0.05f,0,0,null,new Ui.Interpolator(){
				@Override
				public float get(float x)
				{
					// TODO: Implement this method
					return 500f*Eg.SinFunc(x);
				}
			})
			.alpha(1400,600,100,0)
			.delayDismiss(2000);
		add(yzr);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
}
