package com.yzrilyzr.engine2d;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import android.view.MotionEvent;
import android.graphics.Paint;
import android.graphics.Canvas;

public abstract class GameActivity extends Activity implements Runnable,View.OnTouchListener,SurfaceHolder.Callback
{

	public abstract void render();
	public abstract void start();
	public abstract void stop();
	public abstract void pause();
	Thread renderThread;
	boolean isPause,Running;
	SurfaceView sv;
	SurfaceHolder hd;
	Paint p;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		VECfile.VTypeface.DEFAULT=Typeface.createFromAsset(getAssets(),"font.ttf");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(sv=new SurfaceView(this));
		sv.setLongClickable(true);
		hd=sv.getHolder();
		sv.setOnTouchListener(this);
		hd.addCallback(this);
		start();
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		// TODO: Implement this method
	}
	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		isPause=true;
		pause();
	}

	@Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
		if(renderThread==null){
			renderThread=new Thread(this);
			renderThread.start();
		}
		else {
			toast("游戏正在运行");
		}
	}
	public void toast(final Throwable e)
	{
		new Handler(getMainLooper()).post(new Runnable(){
			@Override
			public void run()
			{
				ByteArrayOutputStream b=new ByteArrayOutputStream();
				PrintStream p=new PrintStream(b);
				e.printStackTrace(p);
				new AlertDialog.Builder(GameActivity.this)
				.setTitle("错误")
				.setMessage(b.toString())
				.show();
			}
		});
	}
	public void toast(final String s)
	{
		new Handler(getMainLooper()).post(new Runnable(){
			@Override
			public void run()
			{
				Toast.makeText(GameActivity.this,s,1).show();
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		isPause=false;
	}

	@Override
	public void run()
	{
		Running=true;
		long ns=System.nanoTime(),dt=0;
		//Matrix m=new Matrix();
		Runtime ru=Runtime.getRuntime();
		int lt=0,fps=0;
		float avgfps=0;
		int ram=0;
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(0xffff0000);
		p.setTypeface(VECfile.VTypeface.DEFAULT);
		Canvas c;
		while(Running)
		{
			try
			{
				c=hd.lockCanvas();
				if(isPause||c==null)
				{
					Thread.sleep(20);
					ns=System.nanoTime();
					continue;
				}
				c.drawColor(Eg.bgcolor);
				if(Eg.showfps)
				{
					if(dt==0)dt=1;
					p.setTextAlign(Paint.Align.LEFT);
					lt++;
					if(lt<=10)avgfps+=1000000000l/dt;
					else
					{
						fps=(int)(avgfps/10f);
						lt=0;
						avgfps=0;
					}
					p.setColor(0xffff0000);
					p.setTextSize(Shape.p(50));
					c.drawText(String.format("FPS:%d Shape:d RAM:d Size:dxd x:d y:d",fps/*,/*sh.size()+ui.size(),ram,bmpc[0].getWidth(),bmpc[0].getHeight(),ppx,ppy/*,deltax,deltay,scale*/),0,Shape.p(50),p);
				}
				dt=System.nanoTime()-ns;
				ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
				if(Eg.fpslimit!=120&&dt<1000000000/Eg.fpslimit)Thread.sleep((int)((float)(1000000000/Eg.fpslimit-(int)dt)/1000000f));
				dt=System.nanoTime()-ns;
				ns=System.nanoTime();
				//if(map!=null)map.lock=false;
				hd.unlockCanvasAndPost(c);
			}
			catch(Throwable e)
			{
				//System.exit(0);。
				toast(e);
				break;
			}
		}
		Running=false;
		renderThread=null;
	}


	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		Running=false;
		stop();
		super.onDestroy();
	}

	
}
