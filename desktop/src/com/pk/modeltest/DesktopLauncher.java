package com.pk.modeltest;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pk.modeltest.ModelTest;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		float ratio = 1080f/1920f;
		config.setWindowedMode((int)(ratio*1000),1000);
		config.setTitle("ModelTest");
		new Lwjgl3Application(new ModelTest(), config);
	}
}
