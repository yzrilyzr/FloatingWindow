package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.mImageView;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.io.IOException;
import com.yzrilyzr.ui.uidata;
import android.os.Handler;

public class ImageViewer implements Window.OnButtonDown
{
	Bitmap b;
	boolean lock=false;
	private Window w;
	Context ctx;
	File f=null;

	private int type;

	private mImageView iv;
	public ImageViewer(Context c,Intent e) throws IOException
	{
		ctx=c;
		type=e.getIntExtra("type",0);
		f=null;
		String ll=e.getStringExtra("path");
		if(ll!=null)f=new File(ll);
		iv=new mImageView(c);
		if(type==1)
		{
			b=BitmapFactory.decodeFile(f.getAbsolutePath());
		}
		else if(type==2)
		{
			VECfile v=VECfile.readFile(f.getAbsolutePath());
			b=VECfile.createBitmap(v,v.width,v.height);
		}
		else if(type==3)
		{
			byte[] h=e.getByteArrayExtra("data");
			b=BitmapFactory.decodeByteArray(h,0,h.length);
		}
		if(b==null)
		{
			util.toast("无法打开图像");
			return;
		}
		w=new Window(c,util.px(300),util.px(300))
		.setTitle(f==null?"图片预览":f.getName())
		.setBar(0,0,0)
		.setAddButton("lock")
		.setCanFocus(false)
		.setOnButtonDown(this)
		.addView(iv)
		.setIcon(type==1||type==3?"image":(type==2?"class":"floatingwindow"))
		.show();
		iv.setImage(b);
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)b.recycle();
		else if(code==Window.ButtonCode.ADD||code==Window.ButtonCode.ADD_LONG)
		{
			lock=!lock;
			if(lock)
			{
				if(code==Window.ButtonCode.ADD_LONG)iv.setAlpha(0.5f);
				w
				.setColor(0)
				.setBColor(0)
				.setTitle("")
				.setBar(8,8,8)
				.setAddButton("")
				.setPosition(w.getPositionX(),w.getPositionY()-util.getStatusBarHeight())
				.setIcon("");
				w.getLayoutParams().flags=WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
				w.update();
				final Window fp=new Window(ctx,util.px(30),util.px(30));
				fp.setTitle("图片预览-解除锁定按钮")
				.show()
				.setIcon("unlock")
				.setCanFocus(false)
				.setCanResize(false)
				.setMinWin(true)
				.setOnButtonDown(new Window.OnButtonDown(){
					@Override
					public void onButtonDown(int code)
					{
						if(code==Window.ButtonCode.MIN)
						{
							lock=!lock;
							fp.dismiss();
							iv.setAlpha(1);
							w
							.setColor(uidata.MAIN)
							.setBColor(uidata.BACK)
							.setTitle(f==null?"图片预览":f.getName())
							.setBar(0,0,0)
							.setPosition(w.getPositionX(),w.getPositionY()+util.getStatusBarHeight())
							.setAddButton("lock")
							.setIcon(type==1||type==3?"image":(type==2?"class":"floatingwindow"))
							.setMaxWin(w.getMax());
						}
					}
				});

			}
			
		}
	}
}
