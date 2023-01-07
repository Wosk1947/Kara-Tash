package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Puncher extends Enemy{

    public PerspectiveCamera camera;

    private Texture enemyTexture;
    public Decal enemyDecal;
    private Texture armTexture;
    public List<Decal> arms = new ArrayList<>();

    private Object p1 = new Object();
    public Object leftShoulder = new Object();
    public Object rightShoulder = new Object();
    public Object leftHand = new Object();
    public Object rightHand = new Object();
    public Object head = new Object();
    public Object front = new Object();
    public Object leftHandDead = new Object();
    public Object rightHandDead = new Object();

    private float constraint = 0;
    public String animationState = "null";

    private float timer = 0;
    private float deltaT = 0;

    private Vector3 origin = new Vector3(0,-4,0);
    private Vector3 currentOrigin = new Vector3();
    private Vector3 startPosition = new Vector3();
    private Vector3 enteringVector = new Vector3();
    private Vector3 flyVector = new Vector3();

    private Sound stepSound;
    private Timer stepTimer = new Timer();
    private long stepSoundId;
    private float phase = 0;

    private float timeOfPhaseChange;
    private float speed = 4f;
    private float jogTime = 0.5f;
    private float standJogTime = 0.8f;
    private float periodicMotionTime = jogTime;
    private float jogLength = speed * jogTime;

    float enteringPosition = 10;
    float enteringDistance = 10;
    float enteringStop;

    boolean jogging = true;
    boolean entering = true;
    boolean killed = false;
    boolean leftStage = false;
    boolean simulateSwordSwing = false;

    float flyPosition = 0;
    float flySpeed = 6f;

    boolean deathDebris = false;

    private ParallaxController parallaxController;

    List<ParticleSpawner> particleSpawners = new ArrayList<>();

    PlayerSword playerSword;

    private Timer hitTimer = new Timer();
    private Timer killTimer = new Timer();
    private List<Timer> killTimers = new ArrayList<>();
    private List<Timer> hitTimers = new ArrayList<>();
    private List<Sound> hitSounds = new ArrayList<>();
    private List<Sound> killSounds = new ArrayList<>();
    private List<Sound> painSounds = new ArrayList<>();
    private Sound sparks;
    private Timer sparkTimer = new Timer();

    public float masterSoundVolume = 0.5f;

    public Puncher(PerspectiveCamera camera, ParallaxController parallaxController, PlayerSword playerSword, Vector3 startPosition){
        this.camera = camera;
        this.playerSword = playerSword;
        this.startPosition.set(startPosition);

        ModelBuilder modelBuilder = new ModelBuilder();
        p1.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        leftShoulder.initWithModel(new Model());
        rightShoulder.initWithModel(new Model());
        leftHand.initWithModel(new Model());
        rightHand.initWithModel(new Model());
        head.initWithModel(new Model());
        front.initWithModel(new Model());
        rightHandDead.initWithModel(new Model());
        leftHandDead.initWithModel(new Model());

        p1.addChild(head);
        p1.addChild(front);
        p1.addChild(leftShoulder);
        p1.addChild(rightShoulder);
        p1.addChild(leftHandDead);
        p1.addChild(rightHandDead);
        leftShoulder.addChild(leftHand);
        rightShoulder.addChild(rightHand);

        enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
        enemyDecal = Decal.newDecal(new TextureRegion(enemyTexture), true);
        enemyDecal.setPosition(new Vector3(0,0,0));
        enemyDecal.setScale(0.01f,0.014f);

        armTexture = new Texture(Gdx.files.internal("arm.png"));
        for (int i=0; i<2; i++){
            arms.add(Decal.newDecal(new TextureRegion(armTexture), true));
            arms.get(i).setScale(0.002f,1);
        }

        //Initial pose
        p1.translate(new Vector3(0,1,0));

        front.setTranslation(new Vector3(0,0,-1));
        head.setTranslation(new Vector3(0,1,0));
        leftShoulder.setTranslation(new Vector3(1.0f,0.5f,-0.3f));
        rightShoulder.setTranslation(new Vector3(-1.0f,0.5f,-0.3f));
        rightHand.setCleanTranslation(new Vector3(0.4f,3f,-0.3f));
        leftHand.setCleanTranslation(new Vector3(-0.4f,3f,-0.3f));
        leftHandDead.setTranslation(new Vector3(-2f,-0.5f, 0.5f));
        rightHandDead.setTranslation(new Vector3(2f,-0.5f, 0.5f));

        stepSound = Gdx.audio.newSound(Gdx.files.internal("step.wav"));
        stepTimer.reset();
        stepTimer.setStopTime(jogTime);

        //Derive entering start. Point of integer jogs greater then entering distance
        enteringVector.set(origin);
        enteringVector.sub(startPosition);
        enteringDistance = enteringVector.len();
        enteringVector.nor();
        int periods = (int)(enteringDistance/jogLength) + 1;
        enteringDistance = (float)(periods * jogLength);
        enteringPosition = enteringDistance;

        this.parallaxController = parallaxController;
        addParallax(parallaxController,4);

        hitSounds.add(Gdx.audio.newSound(Gdx.files.internal("swordHit_2.wav")));
        hitSounds.add(Gdx.audio.newSound(Gdx.files.internal("electricity.wav")));
        killSounds.add(Gdx.audio.newSound(Gdx.files.internal("swordHit_2.wav")));
        killSounds.add(Gdx.audio.newSound(Gdx.files.internal("enemyDeath.wav")));
        //killSounds.add(Gdx.audio.newSound(Gdx.files.internal("electricityLong.wav")));
        painSounds.add(Gdx.audio.newSound(Gdx.files.internal("enemyPain.wav")));
        painSounds.add(Gdx.audio.newSound(Gdx.files.internal("enemyPain2.wav")));
        painSounds.add(Gdx.audio.newSound(Gdx.files.internal("enemyPain3.wav")));
        killTimer.addTimes(Arrays.asList(0.1f,0.3f/*,0.35f*/));
        hitTimer.addTimes(Arrays.asList(0.1f,0.4f,0.4f));
        sparks = Gdx.audio.newSound(Gdx.files.internal("sparks.wav"));
    }

    @Override
    public void update(){
        deltaT = Gdx.graphics.getDeltaTime();
        timer += deltaT;

        //Perform modifications

        currentOrigin.set(origin);
        if (jogging) {
            performJogging();
        }
        if (entering) {
            enterStage();
        } else {
            simulateSwordSwing = true;
        }
        if ((playerSword.checkForSwing() || simulateSwordSwing) && !killed && !entering){
            simulateSwordSwing = false;
            killed = true;
            hitTimers.add(new Timer(Arrays.asList(0.1f,0.4f,0.4f)));
            killTimers.add(new Timer(Arrays.asList(0.1f,0.3f/*,0.35f*/)));
        }
        if (killed) {
            if (!deathDebris) {
                deathDebris = true;
                addDebrisParticles(new Vector3((float)(Math.random()*2 - 1),4,1));
                flyVector.set((float)(Math.random()*2 - 1),0,1f);
                leftShoulder.setTranslation(new Vector3(1f,1,-0.3f));
                rightShoulder.setTranslation(new Vector3(-1f,1,-0.3f));
                sparkTimer.addRandomTimes(30, 0,0.07f,0.14f);
            }
            performDeath();
        }

        //Set Pivot origin and sprite
        p1.setTranslation(currentOrigin);
        Utils.rotateDecal(enemyDecal,currentOrigin,head.getWorldTranslation(),front.getWorldTranslation());


        if (killed){
            float handSpeed = 20f;
            if (leftHand.distanceTo(leftHandDead) > 0.3){
                leftHand.moveTo(leftHandDead.getWorldTranslation(),handSpeed*deltaT);
            }
            if (rightHand.distanceTo(rightHandDead) > 0.3){
                rightHand.moveTo(rightHandDead.getWorldTranslation(),handSpeed*deltaT);
            }
        }

        //Hands
        Utils.strectchDecal(arms.get(0),leftShoulder.getWorldTranslation(),rightHand.getWorldTranslation(),camera);
        Utils.strectchDecal(arms.get(1),rightShoulder.getWorldTranslation(),leftHand.getWorldTranslation(),camera);

        Utils.shadowDecal(enemyDecal);
        Utils.shadowDecal(arms.get(0));
        Utils.shadowDecal(arms.get(1));

        int particleSpawnerNumber = particleSpawners.size();
        for (int i=0; i<particleSpawnerNumber; i++){
            particleSpawners.get(i).update();
            if (particleSpawners.get(i).checkIfSpawnerStop()){
                particleSpawners.get(i).dispose();
                particleSpawners.remove(i);
                i--;
                particleSpawnerNumber--;
            }
        }
    }

    private void performJogging(){
        phase = (((timer-timeOfPhaseChange)%periodicMotionTime)/periodicMotionTime)*(float)Math.PI;
        Vector3 jog = new Vector3(0,(float)Math.abs(Math.sin(phase)),0);
        currentOrigin.add(jog);
        stepTimer.update();
        if (stepTimer.checkIfStop() && enteringPosition > enteringStop){
            stepTimer.reset();
            stepSoundId = stepSound.play((1 - enteringPosition/enteringDistance)*1.0f*masterSoundVolume);
        }
    }

    private void enterStage(){
        if (enteringPosition > enteringStop) {
            enteringPosition -= deltaT * speed;
        } else {
            timeOfPhaseChange = timer;
            periodicMotionTime = standJogTime;
            entering = false;
        }
        float apex = 2f;
        Vector3 verticaltranslation = new Vector3(0,(float)(-Math.pow(enteringPosition-Math.sqrt(apex),2)+apex),0);
        Vector3 walk = new Vector3();
        walk.set(enteringVector);
        walk.y = 0;
        walk.nor();
        walk.scl(enteringPosition);
        walk.add(verticaltranslation);
        currentOrigin.add(walk);
    }

    private void performDeath(){
        //Death animation
        //Body fly
        flySpeed = 6;
        flyPosition += deltaT * flySpeed;
        float apexHeight = 7f;
        float apexDistance = 1f;
        Vector3 fly = new Vector3();
        flyVector.nor();
        fly.set(flyVector);
        fly.scl(flyPosition);
        fly.add(new Vector3(0,(float)(-Math.pow(flyPosition/apexDistance-Math.sqrt(apexHeight),2)+apexHeight),0));
        currentOrigin.add(fly);

        //Body rotation
        Quaternion rotation = new Quaternion(new Vector3(1,1,0),flyPosition*90);
        p1.setRotation(rotation);

        //Death sounds
        int timersListSize = killTimers.size();
        for (int i = 0; i < timersListSize; i++ ) {
            Timer timer = killTimers.get(i);
            timer.update();
            if (timer.checkIfStop()) {
                if (timer.getCounter() - 1 == 0){
                    Gdx.input.vibrate(500);
                }
                killSounds.get(timer.getCounter() - 1).play(2.0f*masterSoundVolume);
            }
            if (timer.isFinished()) {
                killTimers.remove(i);
                timersListSize--;
                i--;
            }
        }
        sparkTimer.update();
        if (sparkTimer.checkIfStop()) {
            //Gdx.app.error("Sparks", "time");
            if (currentOrigin.y > -10) {
                addSparkParticles(currentOrigin, new Vector3((float) Math.random() - 0.5f, (float) Math.random() - 0.5f, (float) Math.random() - 0.5f));
            }
            float volume = 1 - (currentOrigin.y > 0? 0:currentOrigin.y/(-20));
            if (volume < 0){
                volume = 0;
            }
            sparks.play(volume*masterSoundVolume);
        }
        if (sparkTimer.isFinished()){
            //Gdx.app.error("Sparks", "finished");
            sparkTimer.deactivate();
        }
        if(flyPosition > 50){
            leftStage = true;
        }
    }

    private void recieveHit(){
        for (Timer timer : hitTimers) {
            timer.update();
            if (timer.checkIfStop()) {
                if (timer.getCounter() - 1 == 0){
                    Gdx.input.vibrate(200);
                }
                if (timer.getCounter() - 1 == 2) {
                    getPainSound().play(2.0f*masterSoundVolume);
                } else {
                    hitSounds.get(timer.getCounter() - 1).play(2.0f*masterSoundVolume);
                }
            }
            if (timer.isFinished()) {
                timer.deactivate();
            }
        }
    }

    public Sound getPainSound(){
        Sound sound = painSounds.get(0);
        painSounds.remove(0);
        Collections.shuffle(painSounds);
        painSounds.add(sound);
        return sound;
    }

    public void addParallax(ParallaxController parallaxController, float depth){
        for (int i=0; i<arms.size(); i++){
            parallaxController.layers.add(new ParallaxLayer(arms.get(i),null,depth,true));
        }
        parallaxController.layers.add(new ParallaxLayer(enemyDecal,null,depth,true));
        parallaxController.layers.add(new ParallaxLayer(null,p1,depth,true));
    }

    public void addDebrisParticles(Vector3 direction){
        particleSpawners.add(new ParticleSpawner(8,
                100f,
                10f,
                0.002f,
                new Vector3(0f, 0f, -2f),
                direction,
                30,
                -100,
                "debris_2.png",
                0.007f,
                0.003f,
                true,
                10f,
                1f,
                parallaxController));
    }

    public void addSparkParticles(Vector3 origin, Vector3 direction){
        particleSpawners.add(new ParticleSpawner(10,
                200f,
                20f,
                0.003f,
                origin,
                direction,
                20,
                -100,
                "spark.png",
                0.006f,
                0,
                false,
                0f,
                0f,
                parallaxController));
    }

    @Override
    public void fillDecalBatch(DecalBatch decalBatch){
        decalBatch.add(enemyDecal);
        for (int i = 0; i< arms.size(); i++){
            decalBatch.add(arms.get(i));
        }
        for (int i=0; i<particleSpawners.size(); i++){
            particleSpawners.get(i).fillDecalBatch(decalBatch);
        }
    }

    @Override
    public void fillModelBatch(ModelBatch modelBatch){
    }

    @Override
    public boolean checkIfLeftStage(){
        return leftStage;
    }

    @Override
    public void dispose(){
        //Clear parallax controller
        int layersNumber = parallaxController.layers.size();
        for (int i=0; i<layersNumber; i++){
            ParallaxLayer layer = parallaxController.layers.get(i);
            if (layer.decal != null){
                if (layer.decal == enemyDecal ||
                        arms.contains(layer.decal)){
                    parallaxController.layers.remove(i);
                    i--;
                    layersNumber--;
                    continue;
                }
            }
            if (layer.object != null){
                if (layer.object == p1){
                    parallaxController.layers.remove(i);
                    i--;
                    layersNumber--;
                }
            }
        }

        for (int i=0; i<particleSpawners.size(); i++){
            particleSpawners.get(i).dispose();
        }

        enemyTexture.dispose();
        armTexture.dispose();

        p1.objectModel.dispose();
        leftShoulder.objectModel.dispose();
        rightShoulder.objectModel.dispose();
        leftHand.objectModel.dispose();
        rightHand.objectModel.dispose();
        head.objectModel.dispose();
        front.objectModel.dispose();

        for (Sound sound: painSounds){
            sound.dispose();
        }
        for (Sound sound: killSounds){
            sound.dispose();
        }
        for (Sound sound: hitSounds){
            sound.dispose();
        }
        painSounds.clear();
        killSounds.clear();
        hitSounds.clear();
    }
}
