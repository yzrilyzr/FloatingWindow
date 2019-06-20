package com.yzrilyzr.myclass;

import org.mozilla.javascript.*;

import android.content.Intent;
import com.yzrilyzr.myclass.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

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
								if (name.equals("getClass")||
								name.equals("delete")||
								name.equals("deleteOnExit")||
								name.equals("getRuntime")
								)
								{
									return NOT_FOUND;
								}
								return super.get(name, start);
							}
						};
					}
				});
				cx.setClassShutter(new ClassShutter(){
					@Override
					public boolean visibleToScripts(String p1)
					{
						if(p1.equals("java.lang.Runtime")||
						p1.equals("java.lang.reflect"))return false;
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
		{
			e.printStackTrace();
		}
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
			
			ScriptableObject.defineClass(scope,ModPE.class);
			scope.put("ModPE", scope, cx.newObject(scope, "ModPE",new Object[]{this}));
			
			
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
			print("js执行失败:\n"+util.getStackTrace(e));
		}
	}

	private void print(String e)
	{
		function("print",e);
	}
	public static class API extends com.yzrilyzr.floatingwindow.API{
		public static void startService(android.content.Context ctx,Intent intent,String targetClass)
		{
			intent.setAction("com.yzrilyzr.Service");
			intent.setPackage("com.yzrilyzr.floatingwindow");
			intent.putExtra("pkg","com.yzrilyzr.floatingwindow");
			intent.putExtra("class",targetClass);
			ctx.startService(intent);
		}
	}
	public static class ModPE extends ScriptableObject
	{
		private static final long serialVersionUID = 1L;
		JSEnv e;
		@Override
		public String getClassName()
		{
			// TODO: Implement this method
			return "ModPE";
		}
		public void dumpVtable(String p1,int p2){
			//e.print(p1+p2);
		}
		public void getBytesFromTexturePack(String p1){
			//e.print("");
		}
		public void getI18n(String p1){
			//e.print("");
		}
		public void getLanguage(){
			//e.print("");
		}
		public void getMinecraftVersion(){
			//e.print("");
		}
		public void langEdit(String p1, String p2){
			//e.print("");
		}
		public void leaveGame(){
			//e.print("");
		}
		public void log(String p1){
			//e.print("");
		}
		public void openInputStreamFromTexturePack(String p1){
			//e.print("");
		}
		public void overrideTexture(String p1, String p){
			//e.print("");
		}
		public void readData(String p){
			//e.print("");
		}
		public void removeData(String p){
			//e.print("");
		}
		public void resetFov(){
			//e.print("");
		}
		public void resetImages(){
			//e.print("");
		}
		public void saveData(String p1, String p){
			//e.print("");
		}
		public void selectLevel(String p){
			//e.print("");
		}
		public void setCamera(Object p){
			//e.print("");
		}
		public void setFoodItem(int p1, String p2, int p3, int p4, String p5, int p6){
			//e.print("");
		}
		public void setFov(double p){
			e.print("设置相机视野"+p);
		}
		public void setGameSpeed(double p){
			//e.print("");
		}
		public void setGuiBlocks(String p){
			//e.print("");
		}
		public void setItem(int p1, String p2, int p3, String p4, int p){
			//e.print("");
		}
		public void setItems(String p){
			//e.print("");
		}
		public void setTerrain(String p){
			//e.print("");
		}
		public void setUiRenderDebug(boolean p){
			//e.print("");
		}
		public void showTipMessage(String p){
			//e.print("");
		}
		public void takeScreenshot(String p){
			//e.print("");
		}
		
	}
}
