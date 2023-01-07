package com.pk.modeltest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.graalvm.compiler.lir.amd64.AMD64ControlFlow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import jdk.internal.org.jline.utils.Log;

public class ModelTest implements ApplicationListener {
	private Environment environment;
	private PerspectiveCamera camera;
	private Viewport viewport;
	private CameraInputController cameraController;
	private ModelBatch modelBatch;
	private DecalBatch decalBatch;

	GroupStrategy strategy;
	private ParallaxController parallaxController = new ParallaxController();
	private AccelerometerController accelerometerController = new AccelerometerController();

	List<Enemy> enemies = new ArrayList<>();
	private PlayerSword playerSword;

	//Sounds
	private Music wind;
	private Music mainTrack;
	private float masterSoundVolume = 1.0f;
	private float masterMusicVolume = 0.0f;

	boolean firstframe = true;
	private float timer = 0;
	private int enemyCounter = 0;

	private Timer spawnTimer = new Timer();

	@Override
	public void create() {
		//Sounds
		wind = Gdx.audio.newMusic(Gdx.files.internal("wind.wav"));
		mainTrack = Gdx.audio.newMusic(Gdx.files.internal("mainTrack.mp3"));

		// Create an environment so we have some lighting
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		// Create a perspective camera with some sensible defaults
		camera = new PerspectiveCamera(67,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		viewport.apply();
		camera.position.set(0,0, -10);
		camera.lookAt(0, 0, 1000);
		camera.near = 1f;
		camera.far = 10000f;
		camera.update();
		strategy = new CameraGroupStrategy(camera, new ZStrategyComparator());
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);
		//decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		decalBatch = new DecalBatch(strategy);

		Utils.lightSource = new Vector3(0,0,0);

		playerSword = new PlayerSword(accelerometerController);

		BackGround.createBackGround(Arrays.asList("farMountains.png","rocks3.png","rocks2.png","rocks1_2.png","ground.png"),
									Arrays.asList(new Vector3(0,0,100.3f),new Vector3(0,0,100.2f),new Vector3(0,0,100.1f),new Vector3(0,0,100.0f),new Vector3(0,-12,6f)),
									Arrays.asList(0.08f,0.07f,0.07f,0.07f,0.01f),
									Arrays.asList(1000f,1f,0.8f,0.6f,4f),
									parallaxController
		);

		spawnTimer.setStopTime(4f);
		spawnTimer.setTime(5f);
	}

	@Override
	public void render() {
		float deltaT = Gdx.graphics.getDeltaTime();
		timer += deltaT;
		cameraController.update();
		accelerometerController.update();

		spawnTimer.update();
		if (spawnTimer.checkIfStop()){
			Vector3 startPosition = new Vector3((float)(Math.random()*14 - 7),-4,-10);
			//if (enemyCounter % 2 == 0){
			//	enemies.add(new Puncher(camera,parallaxController,playerSword,startPosition));
			//} else {
				enemies.add(new Swordsman(camera, parallaxController, playerSword, startPosition));
			//}
			enemyCounter++;
			spawnTimer.deactivate();
			spawnTimer.reset();
		}

		Gdx.gl.glClearColor( 0, 0, 0,1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//Update objects
		playerSword.update();

		//Gdx.app.error("enemyList", "===============");
		int enemiesListSize = enemies.size();
		//Gdx.app.error("enemyList", enemiesListSize + "");
		for (int i = 0; i < enemiesListSize; i++){
			//Gdx.app.error("enemyList inside loop", enemiesListSize + "");
			//Gdx.app.error("enemyList inside loop", i + "");
			Enemy enemy = enemies.get(i);
			enemy.update();
			if (enemy.checkIfLeftStage()){
				enemy.dispose();
				enemies.remove(i);
				enemiesListSize--;
				i--;
			}
		}
		parallaxController.render(-accelerometerController.getLastSmoothedAcceleration().x,-accelerometerController.getLastSmoothedAcceleration().y);

		// Render models and decals
		modelBatch.begin(camera);
		playerSword.fillModelBatch(modelBatch);
		for (Enemy enemy : enemies){
			enemy.fillModelBatch(modelBatch);
		}
		modelBatch.end();
		// Render decals
		parallaxController.fillDecalBatch(decalBatch);
		for (Enemy enemy : enemies){
			enemy.fillDecalBatch(decalBatch);
		}
		playerSword.fillDecalBatch(decalBatch);

		decalBatch.flush();
		Gdx.gl20.glDepthMask(true);
		//Update camera
		camera.update();
		//End of frame routines
		if (firstframe) {
			wind.setLooping(true);
			mainTrack.setLooping(true);
			wind.setVolume(1.0f*masterSoundVolume);
			mainTrack.setVolume(1.0f*masterMusicVolume);
			accelerometerController.calibrate();
			firstframe = false;
			mainTrack.play();
			wind.play();
		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		//sword.dispose();
		//swordHandle.dispose();
		int enemiesListSize = enemies.size();
		for (int i = 0; i < enemiesListSize; i++){
			Enemy enemy = enemies.get(i);
			if (enemy.checkIfLeftStage()){
				enemy.dispose();
				enemies.remove(i);
				enemiesListSize--;
				i--;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.update();
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	private class ZStrategyComparator implements Comparator<Decal> {

		@Override
		public int compare (Decal o1, Decal o2) {
			float dist1 = camera.position.dst(0, 0, o1.getPosition().z);
			float dist2 = camera.position.dst(0, 0, o2.getPosition().z);
			return (int)Math.signum( - dist1 + dist2);
		}
	}
}

















