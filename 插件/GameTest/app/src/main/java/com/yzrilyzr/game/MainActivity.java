package com.yzrilyzr.game;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import java.util.concurrent.*;
import android.view.View.*;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener
{
	SurfaceView sv;
	public CopyOnWriteArrayList<Scene> scenes=new CopyOnWriteArrayList<Scene>();
	Render renderRunn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        sv=new SurfaceView(this);
		sv.getHolder().addCallback(this);
		renderRunn=new Render();
		sv.setOnTouchListener(this);
		setContentView(sv);
    }
	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		new Thread(renderRunn).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		renderRunn.render=false;
	}

	class Render implements Runnable{
		public boolean render=true;
		@Override
		public void run()
		{
			try{
				long t=System.nanoTime();
			while(render){
				SurfaceHolder h=sv.getHolder();
				Canvas p=h.lockCanvas();
				if(p==null){
					Thread.sleep(20);
					continue;
				}
				for(Scene s:scenes){
					s.onDraw(p);
				}
				h.unlockCanvasAndPost(p);
				long dt=System.nanoTime()-t;
				t=System.nanoTime();
				Utils.dt=dt/1000000f;
			}
			}catch(Throwable e){
				Utils.alert(e);
			}
			render=true;
		}
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		for(Scene s:scenes){
			s.onTouch(p2);
		}
		return true;
	}
}
