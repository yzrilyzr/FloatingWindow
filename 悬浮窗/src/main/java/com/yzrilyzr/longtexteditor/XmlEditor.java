package com.yzrilyzr.longtexteditor;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.floatingwindow.*;
import com.yzrilyzr.myclass.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.xmlpull.v1.*;

import com.yzrilyzr.floatingwindow.Window;

public class XmlEditor
{
	String xml;
	Window w;
	LinearLayout addin;
	ListView list;
	Context ctx;
	public XmlEditor(Context c,Intent e)
	{
		ctx=c;
		xml=e.getStringExtra("xml");
		w=new Window(c,util.px(270),util.px(480))
			.setTitle("XML可视化编辑器")
			.setIcon("image")
			.setParent(e)
			.show();
		ViewGroup v=(ViewGroup) w.addView(R.layout.window_longtexteditorxmleditor);
		addin=(LinearLayout) v.findViewById(R.id.windowlongtexteditorxmleditorLinearLayout1);
		list=(ListView)v.findViewById(R.id.windowlongtexteditorxmleditorListView1);
		parseXML2();
	}
	public void parseXML2()
	{
		try
		{
			byte[] data=xml.getBytes();
			Class<?> clazz = Class.forName("android.content.res.XmlBlock");
			Constructor<?> constructor = clazz.getDeclaredConstructor(byte[].class);
			constructor.setAccessible(true);
			Object block = constructor.newInstance(data);
			Method method = clazz.getDeclaredMethod("newParser");
			method.setAccessible(true);
			XmlPullParser parser = (XmlPullParser) method.invoke(block);
			addin.removeAllViews();
			addin.addView(LayoutInflater.from(ctx).inflate(parser,null));
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	public void parseXML()
	{

		try
		{
			XmlPullParser parser=XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(new ByteArrayInputStream(xml.getBytes()),"utf-8");
			int event=parser.getEventType();
			View baseView=null;
			while(event!=XmlPullParser.END_DOCUMENT)
			{
				View nview=null;
				/*String tmptag=parser.getName();
				 String tmptxt=parser.getText();
				 System.out.println("name:"+parser.getName());
				 System.out.println("text:"+parser.getText());
				 int at=parser.getAttributeCount();
				 for(int i=0;i<at;i++){
				 System.out.println("attr:"+parser.getAttributeName(i));
				 System.out.println("v:"+parser.getAttributeValue(i));
				 System.out.println(parser.getAttributeNamespace(i));
				 System.out.println(parser.getAttributePrefix(i));
				 System.out.println(parser.getAttributeType(i));
				 }
				 System.out.println("event:"+parser.getEventType());
				 System.out.println("========");
				 //if(tmptag!=null)tag=tmptag;
				 //if(tmptxt!=null)txt=tmptxt;*/
				switch (event)
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						String name=parser.getName();
						Class cls=viewByName(name);
						nview=initView(cls,parser);
						if(baseView==null)baseView=nview;
						break;
					case XmlPullParser.TEXT:
						//test.append("text tag: "+tag+" ,text: "+txt+"\n");

						break;
					case XmlPullParser.END_TAG:

				}
				event=parser.next();
			}
			addin.removeAllViews();
			addin.addView(baseView);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			util.toast("解析XML失败");
		}
	}

	private View initView(Class cls, XmlPullParser parser)throws Exception
	{

		final int count=parser.getAttributeCount();
		final ArrayList<String> names=new ArrayList<String>();
		final ArrayList<String> values=new ArrayList<String>();
		for(int i=0;i<count;i++)
		{
			names.add(parser.getAttributeName(i));
			values.add(parser.getAttributeValue(i));
		}
		AttributeSet attr=new AttributeSet(){
			@Override
			public int getAttributeCount()
			{
				// TODO: Implement this method
				return count;
			}

			@Override
			public String getAttributeName(int p1)
			{
				// TODO: Implement this method
				return names.get(p1);
			}

			@Override
			public String getAttributeValue(int p1)
			{
				// TODO: Implement this method
				return values.get(p1);
			}

			@Override
			public String getAttributeValue(String p1, String p2)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public String getPositionDescription()
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public int getAttributeNameResource(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeListValue(String p1, String p2, String[] p3, int p4)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public boolean getAttributeBooleanValue(String p1, String p2, boolean p3)
			{
				// TODO: Implement this method
				return false;
			}

			@Override
			public int getAttributeResourceValue(String p1, String p2, int p3)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeIntValue(String p1, String p2, int p3)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeUnsignedIntValue(String p1, String p2, int p3)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public float getAttributeFloatValue(String p1, String p2, float p3)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeListValue(int p1, String[] p2, int p3)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public boolean getAttributeBooleanValue(int p1, boolean p2)
			{
				// TODO: Implement this method
				return false;
			}

			@Override
			public int getAttributeResourceValue(int p1, int p2)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeIntValue(int p1, int p2)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getAttributeUnsignedIntValue(int p1, int p2)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public float getAttributeFloatValue(int p1, float p2)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public String getIdAttribute()
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public String getClassAttribute()
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public int getIdAttributeResourceValue(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public int getStyleAttribute()
			{
				// TODO: Implement this method
				return 0;
			}
		};
		View v=(View) cls.getConstructor(Context.class,AttributeSet.class).newInstance(ctx,attr);
		return v;
	}
	public Class viewByName(String name) throws Exception
	{
		Class cls=null;
		try
		{
			cls=Class.forName("android.view."+name);
		}
		catch (ClassNotFoundException e)
		{}
		try
		{
			cls=Class.forName("android.widget."+name);
		}
		catch (ClassNotFoundException e)
		{}
		try
		{
			cls=Class.forName("com.yzrilyzr.ui."+name);
		}
		catch (ClassNotFoundException e)
		{}
		try
		{
			cls=Class.forName(name);
		}
		catch (ClassNotFoundException e)
		{}

		if(cls==null)throw new ClassNotFoundException("此视图未找到:"+name);
		return cls;
	}
}
