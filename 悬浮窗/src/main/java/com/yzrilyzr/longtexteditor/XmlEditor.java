package com.yzrilyzr.longtexteditor;
import android.content.Context;
import android.content.Intent;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;

public class XmlEditor
{
	String xml;
	Window w;
	public XmlEditor(Context c,Intent e){
		xml=e.getStringExtra("xml");
		w=new Window(c,util.px(270),util.px(480))
		.setTitle("XML可视化编辑器")
		.setIcon("image")
		.setParent(e)
		.show();
	}
}
