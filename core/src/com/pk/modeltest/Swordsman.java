package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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

class Swordsman extends Enemy{

    public PerspectiveCamera camera;

    private Texture enemyTexture;
    public Decal enemyDecal;
    private Texture armTexture;
    public List<Decal> arms = new ArrayList<>();

    private Object p1 = new Object();
    private Object p2 = new Object();
    private Object p3 = new Object();
    private Object p4 = new Object();
    private Object p5 = new Object();
    public Object sword = new Object();
    public Object leftShoulder = new Object();
    public Object rightShoulder = new Object();
    public Object leftHand = new Object();
    public Object rightHand = new Object();
    public Object head = new Object();
    public Object front = new Object();
    public Object leftHandDead = new Object();
    public Object rightHandDead = new Object();
    public Object upSwingPoint = new Object();
    public Object downHitPoint = new Object();
    public Object guardPoint = new Object();

    private float timer = 0;
    private float deltaT = 0;

    private Vector3 origin = new Vector3(0,-4,0);
    private Vector3 currentOrigin = new Vector3();
    private Vector3 startPosition = new Vector3();
    private Vector3 enteringVector = new Vector3();
    private Vector3 flyVector = new Vector3();
    private Vector3 swordFlyVector = new Vector3();
    private float swordFlyAngle = 0f;
    private Vector3 swordRotationAxis = new Vector3();

    private Sound stepSound;
    private Timer stepTimer = new Timer();
    private long stepSoundId;
    private float phase = 0;

    private float timeOfPhaseChange;
    private float speed = 6f;
    private float jogTime = 0.3f;
    private float standJogTime = 0.8f;
    private float periodicMotionTime = jogTime;
    private float jogLength = speed * jogTime;

    float enteringPosition = 10;
    float enteringDistance = 10;
    float enteringStop;

    boolean leftStage = false;
    boolean simulateSwordSwing = false;

    float flyPosition = 0;
    float flySpeed = 6f;

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

    private String globalState = "entering";
    private boolean newState = true;

    private Timer attackTimer = new Timer();

    private float rotationDistance = 1.0f;
    private boolean movingToPoint = false;

    private float jumpPosition = 0f;
    private float jumpLength = 0f;
    private Vector3 jumpEndPosition = new Vector3(0,-4, -5);
    private Vector3 jumpStartPosition = new Vector3(0,0,0);
    private Vector3 jumpVector = new Vector3(0,0,0);

    private float hp = 0;

    private boolean movingAfterHit = false;
    private Timer hitMovementTimer = new Timer();
    private Vector3 afterHitMovementPosition = new Vector3(0,0,0);
    private float hitDirection = 1;

    private Timer playerSwordHitTimer = new Timer();

    private Sound blockSound;

    private Texture swingTraceTexture;
    public Decal swingTraceDecal;
    private Timer swingTimer = new Timer();
    private Vector3 swingDecalOrigin = new Vector3();
    private Vector3 swingDecalFace = new Vector3();
    private Vector3 swingDecalApex = new Vector3();


    public Swordsman(PerspectiveCamera camera, ParallaxController parallaxController, PlayerSword playerSword, Vector3 startPosition){
        this.camera = camera;
        this.playerSword = playerSword;
        this.startPosition.set(startPosition);

        ModelBuilder modelBuilder = new ModelBuilder();
        sword.initWithModel(modelBuilder.createBox(0.1f, 6f, 0.4f,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        p4.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        p1.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        p2.initWithModel(new Model());
        p3.initWithModel(new Model());
        p5.initWithModel(new Model());
        leftShoulder.initWithModel(new Model());
        rightShoulder.initWithModel(new Model());
        leftHand.initWithModel(new Model());
        rightHand.initWithModel(new Model());
        head.initWithModel(new Model());
        front.initWithModel(new Model());
        rightHandDead.initWithModel(new Model());
        leftHandDead.initWithModel(new Model());
        upSwingPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        downHitPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        guardPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        p4.addChild(sword);
        p1.addChild(p2);
        p2.addChild(p3);
        p3.addChild(p4);

        p1.addChild(head);
        p1.addChild(front);
        p1.addChild(leftShoulder);
        p1.addChild(rightShoulder);
        p1.addChild(leftHandDead);
        p1.addChild(rightHandDead);
        leftShoulder.addChild(leftHand);
        rightShoulder.addChild(rightHand);

        //Pose points
        p4.addChild(upSwingPoint);
        p4.addChild(downHitPoint);
        p4.addChild(guardPoint);

        enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
        enemyDecal = Decal.newDecal(new TextureRegion(enemyTexture), true);
        enemyDecal.setPosition(new Vector3(0,0,0));
        enemyDecal.setScale(0.01f,0.014f);

        armTexture = new Texture(Gdx.files.internal("arm.png"));
        for (int i=0; i<2; i++){
            arms.add(Decal.newDecal(new TextureRegion(armTexture), true));
            arms.get(i).setScale(0.002f,1);
        }

        //Initial poses memorization

        //Pre-upper swing
        p4.translate(new Vector3(0,0,-2.2f));
        p1.translate(new Vector3(0,1,0));
        sword.translate(new Vector3(0,3,0));
        p3.setRotation(new Quaternion(new Vector3(1,0,0),60));
        p2.setRotation(new Quaternion(new Vector3(0,0,1),-30));
        p4.setRotation(new Quaternion(new Vector3(0,0,1),30));

        p4.removeChild(upSwingPoint);
        upSwingPoint.clearParentInfo();
        p1.addChildClean(upSwingPoint);

        p1.clearTransform();
        p2.clearTransform();
        p3.clearTransform();
        p4.clearTransform();
        sword.clearTransform();

        //Up hit
        p4.translate(new Vector3(0,0,-2.2f));
        p1.translate(new Vector3(0,1,0));
        sword.translate(new Vector3(0,3,0));
        p2.setRotation(new Quaternion(new Vector3(0,0,1),-30));
        p3.setRotation(new Quaternion(new Vector3(1, 0, 0), -80));
        p4.setRotation(new Quaternion(new Vector3(1, 0, 0), -20));

        p4.removeChild(downHitPoint);
        downHitPoint.clearParentInfo();
        p1.addChildClean(downHitPoint);


        p1.clearTransform();
        p2.clearTransform();
        p3.clearTransform();
        p4.clearTransform();
        sword.clearTransform();

        //Guard
        p4.translate(new Vector3(0,0,-2.2f));
        p1.translate(new Vector3(0,1,0));
        sword.translate(new Vector3(0,3,0));
        p3.setRotation(new Quaternion(new Vector3(1,0,0),-20));
        p2.setRotation(new Quaternion(new Vector3(0,0,1),-60));
        p4.setRotation(new Quaternion(new Vector3(0,0,1),+40));

        p4.removeChild(guardPoint);
        guardPoint.clearParentInfo();
        p1.addChildClean(guardPoint);

        ////////////////

        front.setTranslation(new Vector3(0,0,-1));
        head.setTranslation(new Vector3(0,1,0));
        leftShoulder.setTranslation(new Vector3(1f,1,-0.3f));
        rightShoulder.setTranslation(new Vector3(-1f,1,-0.3f));
        rightHand.setCleanTranslation(p4.getWorldTranslation());
        leftHand.setCleanTranslation(p4.getWorldTranslation());
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
        blockSound = Gdx.audio.newSound(Gdx.files.internal("block.wav"));

        attackTimer.setStopTime(3);
        hitMovementTimer.setStopTime(0.2f);
        hitMovementTimer.deactivate();
        playerSwordHitTimer.setStopTime(0.7f);

        swingTraceTexture = new Texture(Gdx.files.internal("swing_trace.png"));
        swingTraceDecal = Decal.newDecal(new TextureRegion(swingTraceTexture), true);
        swingDecalOrigin = new Vector3(-1f,0,-7);
        swingTraceDecal.setPosition(swingDecalOrigin);
        swingTraceDecal.setScale(0.025f,0.005f);
        swingTraceDecal.setColor(1,1,1,0f);
        swingDecalFace.set(swingDecalOrigin);
        swingDecalApex.set(swingDecalOrigin);
        swingDecalFace.add(new Vector3(1,-0.3f,1));
        swingDecalApex.add(new Vector3(0,0,-10));
        Utils.rotateDecal(swingTraceDecal,swingDecalOrigin, swingDecalApex,swingDecalFace);
    }

    @Override
    public void update(){
        deltaT = Gdx.graphics.getDeltaTime();
        timer += deltaT;

        //Perform modifications

        currentOrigin.set(origin);

        performJogging();
        performState();

        //Set Pivot origin and sprite
        p1.setTranslation(currentOrigin);
        Utils.rotateDecal(enemyDecal,currentOrigin,head.getWorldTranslation(),front.getWorldTranslation());

        //Hands
        if (globalState != "killed"){
            rightHand.setCleanTranslation(p4.getWorldTranslation());
            leftHand.setCleanTranslation(p4.getWorldTranslation());
        }


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
        if (enteringPosition > 0) {
            enteringPosition -= deltaT * speed;
        } else {
            timeOfPhaseChange = timer;
            periodicMotionTime = standJogTime;
            globalState = Math.random() > 0.5 ? "stay" : "attackPrep";
            //globalState = "stay";
            attackTimer.reset();
            attackTimer.clearTimes();
            newState = true;
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
        flySpeed = 10;
        flyPosition += deltaT * flySpeed;

        float apexHeight = 7f;
        float apexDistance = 5f;
        float a = -apexHeight/(apexDistance*apexDistance);
        float b = 2*(apexHeight/apexDistance);

        Vector3 fly = new Vector3();
        flyVector.nor();
        fly.set(flyVector);
        fly.scl(flyPosition);
        fly.add(new Vector3(0,(float)(a*flyPosition*flyPosition+b*flyPosition),0));
        currentOrigin.add(fly);
        //Sword fly

        apexHeight = 7f;
        apexDistance = 5f;
        a = -apexHeight/(apexDistance*apexDistance);
        b = 2*(apexHeight/apexDistance);

        swordFlyVector.nor();
        fly.set(swordFlyVector);
        fly.scl(flyPosition);
        fly.add(new Vector3(0,(float)(a*flyPosition*flyPosition+b*flyPosition),0));
        //Body rotation
        Quaternion rotation = new Quaternion(new Vector3(1,1,0),flyPosition*90);
        p1.setRotation(rotation);
        //Sword rotation
        swordFlyAngle = deltaT*1000;
        sword.rotate(new Quaternion(swordRotationAxis,swordFlyAngle));
        //Gdx.app.error("Sword rot", sword.getWorldTranslation().toString());
        sword.setWorldTranslation(fly);
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

    private void moveAfterHit(){
        float afterHitSpeed = 20f;
        float maxDeviation = 2;
        hitMovementTimer.update();
        if (hitMovementTimer.isActive() && Math.abs(afterHitMovementPosition.x) < maxDeviation){
            afterHitMovementPosition.x += hitDirection * afterHitSpeed * deltaT;
            if (Math.abs(afterHitMovementPosition.x) > maxDeviation){
                afterHitMovementPosition.x = afterHitMovementPosition.x > 0 ? maxDeviation : -maxDeviation;
            }
        }
        if (hitMovementTimer.checkIfStop()){
            hitMovementTimer.deactivate();
        }
        currentOrigin.add(afterHitMovementPosition);
    }

    private void returnToCenter(){
        float returningSpeed = 1f;
        Vector3 returnVector = new Vector3();
        returnVector.set(afterHitMovementPosition);
        returnVector.x = -returnVector.x;
        float tmpX = (float)Math.abs(returnVector.x);
        if (returningSpeed*deltaT <= tmpX){
            returnVector.nor();
            returnVector.scl(returningSpeed*deltaT);
            afterHitMovementPosition.add(returnVector);
        }
    }

    public Sound getPainSound(){
        Sound sound = painSounds.get(0);
        painSounds.remove(0);
        Collections.shuffle(painSounds);
        painSounds.add(sound);
        return sound;
    }

    private void performState(){
        if (globalState == "entering") {
            enterStage();
        }
        if (globalState == "stay") {
            if (newState) {
                attackTimer.reset();
                attackTimer.clearTimes();
                attackTimer.setStopTime(999f);
                newState = false;
            }

            if ((playerSword.checkForSwing() || simulateSwordSwing)){
                    simulateSwordSwing = false;
                    globalState = "killed";
                    newState = true;
                    return;
            }
            //moveAfterHit();
            //returnToCenter();
            //attackTimer.update();
            if (attackTimer.checkIfStop()){
                attackTimer.reset();
                attackTimer.clearTimes();
                globalState = "attackPrep";
                newState = true;
                return;
            }
        }
        if (globalState == "attackPrep"){
            if (newState){
                newState = false;
                p3.removeChild(p4);
                p4.clearParentInfo();
                movingToPoint = true;
                attackTimer.setStopTime(0.2f);
                playerSword.allowBlock();
            }
            moveAfterHit();
            returnToCenter();
            performMovementTo(upSwingPoint,0.15f,30f);
            attackTimer.update();
            if (attackTimer.checkIfStop()){
                attackTimer.reset();
                attackTimer.clearTimes();
                globalState = "attack";
                newState = true;

                jumpStartPosition.set(currentOrigin);
                jumpVector.set(jumpEndPosition);
                jumpVector.sub(jumpStartPosition);
                jumpVector.set(jumpVector.x,0,jumpVector.z);
                jumpLength = jumpVector.len();
                jumpPosition = 0;

                return;
            }
        }
        if (globalState == "attack") {
            if (newState){
                newState = false;
                movingToPoint = true;
                attackTimer.setStopTime(0.2f);
                swingTimer.reset();
                swingTimer.setStopTime(0.1f);
            }
            currentOrigin.add(afterHitMovementPosition);
            performJump();
            attackTimer.update();
            if (attackTimer.checkIfStop()) {
                if (playerSword.checkForBlock()) {
                    blockSound.play();
                    playerSword.disallowBlock();
                }
            }
            if (attackTimer.checkIfStop()) {
                performMovementTo(downHitPoint, 0.15f, 30f);
                swingTimer.update();
                if (swingTimer.checkIfStop()) {
                    float alpha = 1 - 0.5f * swingTimer.getTime();
                    if (alpha < 0) {
                        alpha = 0;
                    }
                    swingTraceDecal.setColor(1, 1, 1, alpha);
                }
            }
            if (jumpPosition >= jumpLength){
                globalState = "coolDown";
                newState = true;
                playerSword.disallowBlock();
                return;
            }
        }
        if (globalState == "coolDown") {
            if (newState){
                newState = false;
                attackTimer.reset();
                attackTimer.clearTimes();
                attackTimer.setStopTime(1f);
            }
            currentOrigin.add(jumpEndPosition);
            currentOrigin.sub(origin);
            attackTimer.update();

            swingTimer.update();
            float alpha = 1 - 0.5f * swingTimer.getTime();
            if (alpha < 0) {
                alpha = 0;
            }
            swingTraceDecal.setColor(1,1,1,alpha);

            if (attackTimer.checkIfStop()){
                globalState = "retreat";
                newState = true;

                jumpStartPosition.set(currentOrigin);
                jumpVector.set(origin);
                jumpVector.sub(jumpStartPosition);
                jumpVector.set(jumpVector.x,0,jumpVector.z);
                jumpLength = jumpVector.len();
                jumpPosition = 0;

                return;
            }
        }
        if (globalState == "retreat") {
            if (newState){
                newState = false;
                attackTimer.reset();
                attackTimer.clearTimes();
                attackTimer.setStopTime(0.2f);
                movingToPoint = true;
            }

            swingTimer.update();
            float alpha = 1 - 0.5f * swingTimer.getTime();
            if (alpha < 0) {
                alpha = 0;
            }

            swingTraceDecal.setColor(1,1,1,alpha);
            currentOrigin.add(jumpEndPosition);
            currentOrigin.sub(origin);
            performJump();
            attackTimer.update();
            if (attackTimer.checkIfStop()) {
                performMovementTo(guardPoint, 0.15f, 30f);
            }

            if (jumpPosition >= jumpLength){
                globalState = "stay";
                newState = true;
                return;
            }

        }
        if (globalState == "killed") {
            if (newState){
                hitTimers.add(new Timer(Arrays.asList(0.1f,0.4f,0.4f)));
                killTimers.add(new Timer(Arrays.asList(0.1f,0.3f/*,0.35f*/)));
                addDebrisParticles(new Vector3((float)(Math.random()*2 - 1),4,1));
                flyVector.set((float)(Math.random()*2 - 1),0,1f);
                swordFlyVector.set((float)(Math.random()*2 - 1),0,1f);
                p4.removeChild(sword);
                sparkTimer.addRandomTimes(30, 0,0.07f,0.14f);
                swordRotationAxis.set((float)(Math.random()*2 - 1),(float)(Math.random()*2 - 1),(float)(Math.random()*2 - 1));
                parallaxController.layers.add(new ParallaxLayer(null,sword,4,false));

                newState = false;
            }
            performDeath();

            float handSpeed = 20f;
            if (leftHand.distanceTo(leftHandDead) > 0.3){
                leftHand.moveTo(leftHandDead.getWorldTranslation(),handSpeed*deltaT);
            }
            if (rightHand.distanceTo(rightHandDead) > 0.3){
                rightHand.moveTo(rightHandDead.getWorldTranslation(),handSpeed*deltaT);
            }
        }
    }

    public void performJump(){
        float jumpSpeed = 8;

        if (jumpPosition < jumpLength){
            if (jumpPosition + deltaT * jumpSpeed > jumpLength){
                jumpPosition = jumpLength;
            } else {
                jumpPosition += deltaT * jumpSpeed;
            }
        }

        float apexHeight = 4f;
        float apexDistance = 2.5f;
        float a = -apexHeight/(apexDistance*apexDistance);
        float b = 2*(apexHeight/apexDistance);

        Vector3 jump = new Vector3();
        jump.set(jumpVector);
        jump.nor();
        jump.scl(jumpPosition);
        jump.add(new Vector3(0,(float)(a*jumpPosition*jumpPosition+b*jumpPosition),0));
        currentOrigin.add(jump);
    }

    public void performMovementTo(Object object, float rotSpeed, float transSpeed){
        if (!movingToPoint){
            return;
        }
        float fraction = 1.0f;
        if (rotationDistance > 0) {
            fraction = rotSpeed / rotationDistance;
        }
        rotationDistance -= rotSpeed;
        if (rotationDistance < 0){rotationDistance = 0;}
        p4.rotateTo(object.getWorldRotation(),fraction);
        if (p4.distanceTo(object) > transSpeed *deltaT) {
            p4.moveTo2(object.getWorldTranslation(), transSpeed *deltaT);
        } else {
            p4.setTranslation(object.getWorldTranslation());
            p1.addChildClean(p4);
            rotationDistance = 0.0f;
            movingToPoint = false;
        }
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
        if (swingTraceDecal.getColor().a != 0) {
            decalBatch.add(swingTraceDecal);
        }
        for (int i = 0; i< arms.size(); i++){
            decalBatch.add(arms.get(i));
        }
        for (int i=0; i<particleSpawners.size(); i++){
            particleSpawners.get(i).fillDecalBatch(decalBatch);
        }
    }

    @Override
    public void fillModelBatch(ModelBatch modelBatch){
        modelBatch.render(sword.objectInstance);
        //Test
        //modelBatch.render(downHitPoint.objectInstance);
        //modelBatch.render(p4.objectInstance);
        //modelBatch.render(rightHandDead.objectInstance);
        //modelBatch.render(rightHand.objectInstance);
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
        p2.objectModel.dispose();
        p3.objectModel.dispose();
        p4.objectModel.dispose();
        p5.objectModel.dispose();
        sword.objectModel.dispose();
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
