package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.yzrilyzr.icondesigner.Shape;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myImageButton extends ImageButton implements myTouchProcessor.Event
{
	private myTouchProcessor pr=new myTouchProcessor(this);
    private myRippleDrawable mrd;
	private boolean useRound=false;
    public myImageButton(Context c,AttributeSet a)
    {
        super(c,a);
        setScaleType(ImageView.ScaleType.FIT_CENTER);
        WidgetUtils.setIcon(this,a);
		float radius=util.px(uidata.UI_RADIUS);
		if(a!=null)
		{
			radius=a.getAttributeFloatValue(null,"radius",util.px(uidata.UI_RADIUS));
			useRound=a.getAttributeBooleanValue(null,"round",false);
        }
		mrd=new myRippleDrawable(isEnabled()?uidata.BUTTON:uidata.getBFColor(),radius);
        mrd.setLayer(isEnabled()?this:null);
        setBackgroundDrawable(mrd);
    }
	public myImageButton(Context c){
		this(c,null);
	}
	public void setColor(int i){
		mrd.setColor(i);
		if(i==0)mrd.setColor(uidata.BUTTON);
	}
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // TODO: Implement this method
        super.onSizeChanged(w, h, oldw, oldh);
        if(useRound)mrd.setRadius(h/2f);
    }
	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO: Implement this method
		super.setEnabled(enabled);
		mrd.setLayer(enabled?this:null);
		mrd.setColor(enabled?uidata.BUTTON:uidata.getBFColor());
	}
	public void setImageVec(String asset){
		try
		{
			VECfile vec=VECfile.readFileFromIs(getContext().getAssets().open(asset+".vec"));
			for(Shape s:vec.shapes){
				//s.setColor(uidata.TEXTMAIN);
				s.setStrokeColor(uidata.TEXTMAIN);
			}
			setImageBitmap(VECfile.createBitmap(vec,getWidth(),getHeight()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    @Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(pr.process(this,event))return true;
		return super.onTouchEvent(event);
	}
	@Override
	public void onDown(View v, MotionEvent m)
	{
		if(isEnabled())mrd.shortRipple(m.getX(),m.getY());
	}
	@Override
	public void onUp(View v, MotionEvent m)
	{
	}
	@Override
	public boolean onView(View v, MotionEvent m)
	{
		return false;
	}
	@Override
	public void onClick(View v)
	{
	}
	@Override
	public boolean onLongClick(View v, MotionEvent m)
	{
		if(isEnabled())mrd.longRipple(m.getX(),m.getY());
		return false;
	}
}
