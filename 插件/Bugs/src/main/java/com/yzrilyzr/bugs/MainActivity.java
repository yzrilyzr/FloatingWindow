package com.yzrilyzr.bugs;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.yzrilyzr.engine2d.MainActivity;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this,MainActivity.class));
		finish();
	}
	
}
