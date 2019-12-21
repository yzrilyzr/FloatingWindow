package com.yzrilyzr.floatingwindow.apps;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.floatingwindow.view.FloatPicker;
import com.yzrilyzr.floatingwindow.view.OscilloscopeView;
import com.yzrilyzr.myclass.Pcm;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.mySeekBar;
import com.yzrilyzr.ui.mySpinner;
import com.yzrilyzr.ui.mySpinnerAdapter;
import java.io.IOException;
import java.io.RandomAccessFile;
import android.widget.CompoundButton;
import com.yzrilyzr.ui.myImageButton;
import com.yzrilyzr.ui.uidata;

public class Oscilloscope implements FloatPicker.FloatPickerEvent,Runnable,Window.OnButtonDown,OnClickListener
{
	AudioRecord record;
	OscilloscopeView osc;
	FloatPicker pa,pb;
	boolean recing=false;
	Context ctx;
	protected Window w;
	View b1,b2,b3,b4;
	public Oscilloscope(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(260),util.px(400))
		.setTitle("示波器")
		.setIcon("signal")
		.setOnButtonDown(this)
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.window_oscilloscope);
		osc=(OscilloscopeView) vg.getChildAt(0);
		mySpinner sp=(mySpinner) vg.findViewById(R.id.windowoscilloscopemySpinner1);
		sp.setAdapter(new mySpinnerAdapter("内部指定,麦克风,wav文件".split(",")));
		pa=(FloatPicker) vg.findViewById(R.id.windowoscilloscopeFloatPicker1);
		pb=(FloatPicker) vg.findViewById(R.id.windowoscilloscopeFloatPicker2);
		pa.setListener(this);
		pb.setListener(this);
		ViewGroup gg=(ViewGroup) vg.getChildAt(3);
		b1=gg.getChildAt(1);
		b2=gg.getChildAt(3);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3=gg.getChildAt(5);
		b4=gg.getChildAt(7);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		sp.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onNothingSelected(AdapterView<?> p1)
			{
				// TODO: Implement this method
			}
			@Override
			public void onItemSelected(final AdapterView<?> pp1, View p2, int p3, long p4)
			{
				if(p3==0)recing=false;
				else if(p3==1)
				{
					util.toast("警告:此示波器不带输入衰减\n请勿输入高电压(>±0.1v)！！！");
					if(!recing)new Thread(Oscilloscope.this).start();
				}
				else if(p3==2)
				{
					recing=false;
					API.startServiceForResult(ctx,w,new BroadcastReceiver(){

						private RandomAccessFile raf;
						long pos=0;
						int ch=2,sr=48000,q=16;
						private void update()
						{
							try
							{
								byte[] by=new byte[q/8*ch*sr];
								pos=util.limit(pos,44,raf.length()-by.length);
								raf.seek(pos+44l);
								raf.read(by);
								int[] t=null;
								if(ch==1)
								{
									if(q==16)t=Pcm.mono_16Bit_PCM(by);
									else if(q==8)t=Pcm.mono_8Bit_PCM(by);
								}
								else if(ch==2)
								{
									if(q==16)t=Pcm.stereo_16Bit_PCM(by)[0];
									else if(q==8)t=Pcm.stereo_8Bit_PCM(by)[0];
								}

								append(t);
							}
							catch(Throwable e)
							{
								e.printStackTrace();
							}
						}
						@Override
						public void onReceive(Context p1, Intent p2)
						{
							try
							{
								String path=p2.getStringExtra("path");
								raf=new RandomAccessFile(path,"r");
								//int totalDataLen=buff.length+44-8,longSampleRate=44100,byteRate=44100*4,totalAudioLen=buff.length;
								byte[] header = new byte[44];
								raf.read(header);
								/*header[0] = 'R'; // RIFF/WAVE header
								 header[1] = 'I';
								 header[2] = 'F';
								 header[3] = 'F';*/
								/*header[4] = (byte) (totalDataLen & 0xff);
								 header[5] = (byte) ((totalDataLen >> 8) & 0xff);
								 header[6] = (byte) ((totalDataLen >> 16) & 0xff);
								 header[7] = (byte) ((totalDataLen >> 24) & 0xff);
								 header[8] = 'W';
								 header[9] = 'A';
								 header[10] = 'V';
								 header[11] = 'E';
								 header[12] = 'f'; // 'fmt ' chunk
								 header[13] = 'm';
								 header[14] = 't';
								 header[15] = ' ';
								 header[16] = 16; // 4 bytes: size of 'fmt ' chunk
								 header[17] = 0;
								 header[18] = 0;
								 header[19] = 0;
								 header[20] = 1; // format = 1
								 header[21] = 0;*/
								ch=header[22];// = (byte) 2;
								//header[23] = 0;
								sr=(header[24])+(header[25]<<8)+(header[26]<<16)+(header[27]<<24);
								//util.toast(sr);
								/*header[24] = (byte) (longSampleRate & 0xff);
								 header[25] = (byte) ((longSampleRate >> 8) & 0xff);
								 header[26] = (byte) ((longSampleRate >> 16) & 0xff);
								 header[27] = (byte) ((longSampleRate >> 24) & 0xff);
								 /*header[28] = (byte) (byteRate & 0xff);
								 header[29] = (byte) ((byteRate >> 8) & 0xff);
								 header[30] = (byte) ((byteRate >> 16) & 0xff);
								 header[31] = (byte) ((byteRate >> 24) & 0xff);
								 header[32] = (byte) (2 * 16 / 8); // block align
								 header[33] = 0;*/
								q=header[34] ;//= 16; // bits per sample
								/*header[35] = 0;
								 header[36] = 'd';
								 header[37] = 'a';
								 header[38] = 't';
								 header[39] = 'a';
								 header[40] = (byte) (totalAudioLen & 0xff);
								 header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
								 header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
								 header[43] = (byte) ((totalAudioLen >> 24) & 0xff);*/
								 q=16;
								 sr=48000;
								 ch=2;
								osc.setSr(sr);
								Window w=new Window(ctx,util.px(200),util.px(220))
								.setTitle("wav选择控制器")
								.setIcon("signal")
								.setBar(0,8,0)
								.setOnButtonDown(new Window.OnButtonDown(){
									@Override
									public void onButtonDown(int code)
									{
										if(code==Window.ButtonCode.CLOSE)
										{
											try
											{
												raf.close();
												pp1.setSelection(0);
											}
											catch (IOException e)
											{
												e.printStackTrace();
											}
										}
									}
								});
								mySeekBar see=new mySeekBar(ctx);
								//util.toast(raf.length());
								see.setMax((int)raf.length()-44);
								see.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
									@Override
									public void onProgressChanged(SeekBar p1, int p2, boolean p3)
									{
										pos=(int)(p2/q*8f)*q/8;
										update();
									}

									@Override
									public void onStartTrackingTouch(SeekBar p1)
									{
										// TODO: Implement this method
									}

									@Override
									public void onStopTrackingTouch(SeekBar p1)
									{
										// TODO: Implement this method
									}
								});
								w.addView(see);
								LinearLayout l=new LinearLayout(ctx);
								w.addView(l);
								LinearLayout l2=new LinearLayout(ctx);
								w.addView(l2);
								LinearLayout l3=new LinearLayout(ctx);
								w.addView(l3);
								final myButton b1=new myButton(ctx);
								b1.setText("上10");
								l.addView(b1);
								final myButton b2=new myButton(ctx);
								b2.setText("上1");
								l2.addView(b2);
								final myButton b3=new myButton(ctx);
								b3.setText("下1");
								l2.addView(b3);
								final myButton b4=new myButton(ctx);
								b4.setText("下10");
								l.addView(b4);
								final myButton b5=new myButton(ctx);
								b5.setText("上");
								l3.addView(b5);
								final myButton b6=new myButton(ctx);
								b6.setText("下");
								l3.addView(b6);
								((LinearLayout.LayoutParams)b1.getLayoutParams()).weight=1;
								((LinearLayout.LayoutParams)b2.getLayoutParams()).weight=1;
								((LinearLayout.LayoutParams)b3.getLayoutParams()).weight=1;
								((LinearLayout.LayoutParams)b4.getLayoutParams()).weight=1;
								((LinearLayout.LayoutParams)b5.getLayoutParams()).weight=1;
								((LinearLayout.LayoutParams)b6.getLayoutParams()).weight=1;
								
								OnClickListener oc=new OnClickListener(){
									@Override
									public void onClick(View p1)
									{
										if(p1==b1)pos-=q/8*ch*sr/2;
										else if(p1==b2)pos-=q/8*ch*sr/20;
										else if(p1==b3)pos+=q/8*ch*sr/20;
										else if(p1==b4)pos+=q/8*ch*sr/2;
										else if(p1==b5)pos-=q/8*ch;
										else if(p1==b6)pos+=q/8*ch;
											update();
		 							}
								};
								b1.setOnClickListener(oc);
								b2.setOnClickListener(oc);
								b3.setOnClickListener(oc);
								b4.setOnClickListener(oc);
								b5.setOnClickListener(oc);
								b6.setOnClickListener(oc);
								w.show();
							}
							catch(Exception ep)
							{
								util.toast("打开失败");
							}
						}
					},cls.EXPLORER);
				}
			}
		});
	}

	@Override
	public void onClick(View p1)
	{
		if(p1==b1){
			osc.setHold(!osc.isHold());
			((myImageButton)p1).setImageVec(osc.isHold()?"play":"pause");
		}
		else if(p1==b2){
			osc.setTdown(!osc.isTdown());
			((myImageButton)p1).setImageVec(osc.isTdown()?"falltrig":"risetrig");
		}
		else if(p1==b3){
			osc.setGrid(!osc.isGrid());
			((myImageButton)p1).setColor(osc.isGrid()?uidata.ACCENT:0);
		}
		else if(p1==b4){
			osc.setMore(!osc.isMore());
			((myImageButton)p1).setColor(osc.isMore()?uidata.ACCENT:0);
		}
		
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)recing=false;
	}
	@Override
	public void run()
	{
		try
		{
			util.toast("正在录音");
			recing=true;
			record=new AudioRecord(MediaRecorder.AudioSource.MIC,
			48000,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			AudioTrack.getMinBufferSize(48000,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT)
			);
			record.startRecording();
			byte[] data=new byte[2400*2];
			osc.setSr(48000);
			while(recing)
			{
				record.read(data,0,data.length);
				int[] d=Pcm.mono_16Bit_PCM(data);
				append(d);
			}
			record.stop();
			record.release();
		}
		catch(Throwable e)
		{
			util.toast("录音失败");
			recing=false;
			e.printStackTrace();
		}
	}


	public void append(int[] data)
	{
		osc.append(data,48000);
	}
	@Override
	public void onChange(FloatPicker p, float f)
	{
		if(f==0)f=0.01f;
		if(p==pa)osc.setScan(f);
		else if(p==pb)osc.setGain(f);
	}
}
