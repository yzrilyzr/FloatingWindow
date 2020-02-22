package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.viewholder.HolderList;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myFab;
import com.yzrilyzr.ui.myListView;
import com.yzrilyzr.ui.myViewPager;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class VVVF
{
	Window w;
	BaseAdapter adap;
	File[] flist;
	myFab fab;
	AudioTrack track;
	public VVVF(final Context ctx,Intent e){
		w=new Window(ctx,util.px(230),util.px(325))
		.setTitle("VVVF音")
		.setIcon("vvvf")
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.window_vvvf);
		fab=(myFab) vg.getChildAt(1);
		fab.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				util.write(util.mainDir+"VVVF/例子","#VVVF\nFMAX:90\n0-10:100,100\n10-20:200,500\n20-30:300,450\n30-40:250,400\n40-50:s8\n50-60:s6\n60-70:s3\n70-80:s2\n80-90:s1");
				initFileList();
			}
		});
		File f=new File(util.mainDir+"VVVF");
		if(!f.exists())f.mkdirs();
		flist=f.listFiles();
		adap=new BaseAdapter(){

			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return flist.length;
			}

			@Override
			public Object getItem(int p1)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public long getItemId(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup convetView)
			{
				HolderList holder;
				if(convertView==null)
				{
					holder=new HolderList(ctx);
					convertView=holder.vg;
					convertView.setTag(holder);
				}
				else holder=(HolderList) convertView.getTag();
				if(position<flist.length)
				{
					File w=flist[position];
					holder.v[0].setVisibility(8);
					holder.text.setText(w.getName());
					holder.v[1].setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							// TODO: Implement this method
						}
					});
					holder.v[1].setImageVec("edit");
					holder.v[2].setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							// TODO: Implement this method
						}
					});
					holder.v[2].setImageVec("closewin");
					holder.v[3].setVisibility(8);
				}
				return convertView;

			}
		};
		((myListView)((myViewPager)vg.getChildAt(0)).getPageAt(0)).setAdapter(adap);
		initFileList();
		initaudio();
	}

	private void initaudio()
	{
		int TEST_SR =48000;
		int BufferSize=2400;
		int TEST_CONF =AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int TEST_FORMAT= AudioFormat.ENCODING_PCM_16BIT;
		int TEST_MODE =AudioTrack.MODE_STREAM;
		int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
		int minBuffSize =AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
		track=new AudioTrack(TEST_STREAM_TYPE,TEST_SR,TEST_CONF,TEST_FORMAT,minBuffSize*2,TEST_MODE);
		track.play();
		
	}
	static float pwm(float x,float period,float width,float rise,float fall,float delay)
	{
		float r=period*rise;
		float w=width*period;
		float f=fall*period;
		x=(x+period*delay+r/2f)%period;
		if(width==-1)
			if(x>=r&&x<r+f)return (2f*(float)Math.random()-1f);
			else return 0;
		if(x<r)return x*2f/r-1f;
		else if(x>=r&&x<r+w)return 1;
		else if(x>=r+w&&x<r+w+f)return 1f-(x-r-w)*2f/f;
		else return -1;
	}
	static float sin(float x,float p,float fai)
	{
		return (float)Math.sin(2.0*Math.PI*x/p+fai);
	}
	
	private void initFileList()
	{
		File f=new File(util.mainDir+"VVVF");
		if(!f.exists())f.mkdirs();
		flist=f.listFiles();
		Arrays.sort(flist,new Comparator<File>(){
			@Override
			public int compare(File p1, File p2)
			{
				// TODO: Implement this method
				return p1.getName().compareToIgnoreCase(p2.getName());
			}
		});
		adap.notifyDataSetChanged();
	}
	
}
