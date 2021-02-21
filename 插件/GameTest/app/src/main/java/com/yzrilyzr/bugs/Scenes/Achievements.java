package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;
import java.util.concurrent.*;

public class Achievements extends Scene
{
	public Achievements(String id){
		super(id);
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/achievements.txt"));
		Ui stat=findUi("stat");
		CopyOnWriteArrayList<Shape> s=stat.getVec().getShapes();
		s.get(5).txt=Long.toString(Data.tmoney);
		s.get(6).txt=Long.toString(Data.tbugs);
		s.get(7).txt=Long.toString(Data.tscore);
		s.get(8).txt=Long.toString(Data.tlives);
		s.get(11).txt=String.format("等级\n%d",Data.level);
		ArrayList<Point> pts=s.get(10).pts;
		Point lt=pts.get(0),rb=pts.get(1),ed=pts.get(3);
		int nexp=Data.getNextlevelExp();
		int cx=(rb.x-lt.x)/2;
		ed.set(
			lt.x+cx+(int)(cx*Math.sin(2f*(float)Data.exp/(float)nexp*Math.PI)),
			lt.y+cx+(int)(-cx*Math.cos(2f*(float)Data.exp/(float)nexp*Math.PI)));
		stat.reDrawVecBmp();
	}
	public void back(Ui s){
		Utils.loadScene(new LevelSelect("levelselect"));
		exitAnim();
	}
}
