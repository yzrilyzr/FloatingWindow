package com.yzrilyzr.engine2d;
import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Eg
{
	public static boolean showfps=true;
	public static int fpslimit=120;
	public static int bgcolor=0xffaaaaaa;
	public interface GameCBK{
		public abstract void render();
		public abstract void start();
		public abstract void stop();
		public abstract void pause();
	}
}
