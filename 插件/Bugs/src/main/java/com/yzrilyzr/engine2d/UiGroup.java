package com.yzrilyzr.engine2d;
import java.util.ArrayList;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class UiGroup extends Ui2
{
	ArrayList<Ui2> uis=new ArrayList<Ui2>();
	public UiGroup(Ui2... uiss){
		super(null,0,0,0,0);
		this.x=p(x);
		this.y=p(y);
		this.w=p(w);
		this.h=p(h);
		visible=false;
		if(uiss!=null)
		for(Ui2 ui:uiss){
			if(ui==null)continue;
			uis.add(ui);
			ui.parent=this;
			ui.setVisable(false);
			MainActivity.ui.add(ui);
		}
}
	/*@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		for(Ui u:uis)u.onDraw(c);
	}

	@Override
	public void onClick(MotionEvent e)
	{
		// TODO: Implement this method
		for(Ui u:uis)u.onClick(e);
	}

	@Override
	public void onMove(MotionEvent e)
	{
		// TODO: Implement this method
		for(Ui u:uis)u.onMove(e);
	}

	@Override
	public void onDown(MotionEvent e)
	{
		// TODO: Implement this method
		for(Ui u:uis)u.onDown(e);
	}
*/
	void upui()
	{
		MainActivity.ui.remove(this);
		MainActivity.ui.add(this);
		for(Ui2 u:uis)
		{
			MainActivity.ui.remove(u);
			MainActivity.ui.add(u);
		}
	}
	
	@Override
	public UiGroup setVisable(boolean b)
	{
		visible=b;
		for(Ui2 u:uis)u.setVisable(b);
		return this;
	}

	@Override
	public UiGroup alphaFrom(float x, float m)
	{
		for(Ui2 u:uis)u.alphaFrom(x,m);
		return this;
	}

	@Override
	public UiGroup alphaTo(float x, float m)
	{
		for(Ui2 u:uis)u.alphaTo(x,m);
		return this;
	}

	@Override
	public UiGroup tScFrom(float x, float y, float w, float h, float millis)
	{
		for(Ui2 u:uis)u.tScFrom(x, y, w, h, millis);
		return this;
	}

	@Override
	public UiGroup tScTo(float x, float y, float w, float h, float millis)
	{
		for(Ui2 u:uis)u.tScTo(x, y, w, h, millis);
		return this;
	}
	public UiGroup tScTo(float... p){
		int i=0;
		for(Ui2 u:uis)u.tScTo(p[i++],p[i++],p[i++],p[i++],p[i++]);
		return this;
	}
	public UiGroup tScFrom(float... p){
		int i=0;
		for(Ui2 u:uis)u.tScFrom(p[i++],p[i++],p[i++],p[i++],p[i++]);
		return this;
	}
}
