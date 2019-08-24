package com.yzrilyzr.bugs;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.yzrilyzr.engine2d.MainActivity;
import com.yzrilyzr.icondesigner.VECfile;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		VECfile.VTypeface.DEFAULT=Typeface.createFromAsset(getAssets(),"font.ttf");
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this,MainActivity.class));
		finish();
	}
	
}
