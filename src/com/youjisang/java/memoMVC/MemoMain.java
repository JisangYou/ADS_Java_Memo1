package com.youjisang.java.memoMVC;
/**
 * @author pc
 *
 */
public class MemoMain {

	public static void main(String[] args) {
		ModelWithDB model = new ModelWithDB();
		View view = new View();
		
		Control control = new Control(model, view);
		control.process();
	}
}