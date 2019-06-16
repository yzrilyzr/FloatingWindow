package com.yzrilyzr.myclass;

import org.mozilla.javascript.*;

import com.yzrilyzr.myclass.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.floatingwindow.API;

public class JSEnv implements Runnable
{
	private Scriptable scope;
	private String js;
	private Object cbk;
	private String name;
	static{
		ContextFactory.initGlobal(new ContextFactory(){
			@Override
			protected Context makeContext()
			{
				Context cx = super.makeContext();
				cx.setWrapFactory(new WrapFactory(){
					@Override
					public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType)
					{
						return new NativeJavaObject(scope, javaObject, staticType){
							@Override
							public Object get(String name, Scriptable start)
							{
								System.out.println(name);
								/*if (name.equals("getClass"))
								 {
								 return NOT_FOUND;
								 }*/
								return super.get(name, start);
							}
						};
					}
				});
				cx.setClassShutter(new ClassShutter(){
					@Override
					public boolean visibleToScripts(String p1)
					{
						System.out.println(p1);
						return true;
					}
				});
				cx.setOptimizationLevel(-1);
				cx.setLanguageVersion(cx.VERSION_1_8);
				cx.setLocale(Locale.CHINA);
				return cx;
			}
		});
	}
	public JSEnv(String js,Object cbk)
	{
		this.js=js;
		this.cbk=cbk;
		try
		{
			util.runInTime(this,10000);
		}
		catch (Exception e)
		{}
		//new Thread(Thread.currentThread().getThreadGroup(),this,"js解析线程",256*1024).start();
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	public void eval(String func)
	{
		Context.enter().evaluateString(scope,func,"JS",1, null);
	}
	public void function(String name,Object... o)
	{
		Context cx=Context.enter();
		((Function)scope.get(name,scope)).call(cx, scope, scope,o);
	}
	@Override
	public void run()
	{
		try
		{
			Context cx = Context.enter();
			scope = cx.initStandardObjects();
			ScriptableObject.putProperty(scope, "javaContext", cx.javaToJS(util.ctx, scope));
			ScriptableObject.putProperty(scope, "javaLoader", cx.javaToJS(JSEnv.class.getClassLoader(), scope));
			BufferedReader i=new BufferedReader(new InputStreamReader(util.ctx.getAssets().open("JS/BaseApi.js")));
			final StringBuilder b=new StringBuilder();
			String x=null;
			while((x=i.readLine())!=null)b.append(x).append("\n");
			i.close();
			cx.evaluateString(scope,b.toString(),"NativeCode",1,null);
			((Function)scope.get("_setcbk",scope)).call(cx, scope, scope,new Object[]{cbk});
			cx.evaluateString(scope,js,"JS",1, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			function("print","js执行失败:\n"+util.getStackTrace(e));
		}
	}
}
