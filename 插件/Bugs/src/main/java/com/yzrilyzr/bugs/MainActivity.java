package com.yzrilyzr.bugs;
import android.os.Bundle;
import com.yzrilyzr.engine2d.GameActivity;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;

public class MainActivity extends GameActivity
{
	@Override
	public void render(Canvas c, float dt)
	{
	}
	@Override
	public void start()
	{
		Eg.setBackground(0xff666666);
		Eg.startScene(new Splash());
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
}
