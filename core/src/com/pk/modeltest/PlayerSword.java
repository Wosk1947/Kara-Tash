package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

class PlayerSword{
    private Texture swordTexture;
    public Decal swordDecal;
    private Texture swingTraceTexture;
    public Decal swingTraceDecal;
    public Object swordCenter = new Object();
    public Object handleTrans = new Object();
    public Object handleYaw = new Object();
    public Object handleRoll = new Object();
    public Object tipPoint = new Object();
    public Object sidePoint = new Object();
    public Object handlePitch = new Object();
    public Object normalPoint = new Object();
    public Object blockPoint = new Object();
    private float armLength = 2f;
    public AccelerometerController accelerometerController;

    private Timer swingTimer = new Timer();
    private Vector3 swingDecalOrigin = new Vector3();
    private Vector3 swingDecalFace = new Vector3();
    private Vector3 swingDecalApex = new Vector3();
    boolean wasSwing = false;

    private Quaternion cleanHandleRoll = new Quaternion();
    private Quaternion cleanHandlePitch = new Quaternion();
    private Quaternion cleanHandleYaw = new Quaternion();

    private float deltaT;
    private float timer;
    private float rotationDistance = 1.0f;
    private boolean movingToPoint = false;

    boolean canBlock = false;
    boolean wasBlock = false;
    boolean drawingBlock = false;
    Timer blockTimer = new Timer();

    public PlayerSword(AccelerometerController accelerometerController){
        swingTimer.setTime(10f);
        swingTimer.setStopTime(0.25f);

        this.accelerometerController = accelerometerController;
        ModelBuilder modelBuilder = new ModelBuilder();
        handleTrans.initWithModel(new Model());
        handleYaw.initWithModel(new Model());
        handleRoll.initWithModel(new Model());
        handlePitch.initWithModel(new Model());
        tipPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        sidePoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        swordCenter.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        normalPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        blockPoint.initWithModel(modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        handleTrans.addChild(handleYaw);
        handleYaw.addChild(handlePitch);
        handlePitch.addChild(handleRoll);
        handleRoll.addChild(tipPoint);
        handleRoll.addChild(sidePoint);
        handleRoll.addChild(swordCenter);

        //Poses
        handleRoll.addChild(normalPoint);
        handleRoll.addChild(blockPoint);

        swordTexture = new Texture(Gdx.files.internal("blade.png"));
        swordDecal = Decal.newDecal(new TextureRegion(swordTexture), true);
        swordDecal.setPosition(new Vector3(0,0,0));
        swordDecal.setScale(0.0025f,0.0025f);

        swingTraceTexture = new Texture(Gdx.files.internal("swing_trace.png"));
        swingTraceDecal = Decal.newDecal(new TextureRegion(swingTraceTexture), true);

        swingDecalOrigin = new Vector3(0.5f,-0.8f,-10f);
        swingTraceDecal.setPosition(swingDecalOrigin);
        swingTraceDecal.setScale(0.01f,0.01f);
        swingTraceDecal.setColor(1,1,1,0f);

        //Poses
        //Block point
        handleTrans.translate(new Vector3(-1.1f,0.7f,-8f));
        handleYaw.setRotation(new Quaternion(new Vector3(0,1,0),45));
        handlePitch.setRotation(new Quaternion(new Vector3(1,0,0),-10));
        tipPoint.translate(new Vector3(0,0,4f));
        sidePoint.translate(new Vector3(4f,0,armLength));
        swordCenter.translate(new Vector3(0,0,armLength));

        handleRoll.removeChild(blockPoint);
        blockPoint.clearParentInfo();

        handleTrans.clearTransform();
        handleYaw.clearTransform();
        handlePitch.clearTransform();
        handleRoll.clearTransform();
        tipPoint.clearTransform();
        sidePoint.clearTransform();
        swordCenter.clearTransform();

        //Initial pose
        handleTrans.translate(new Vector3(-1.1f,-3.5f,-8f));
        handleYaw.setRotation(new Quaternion(new Vector3(0,1,0),10));
        handlePitch.setRotation(new Quaternion(new Vector3(1,0,0),-45));
        handleRoll.setRotation(new Quaternion(new Vector3(0,0,1),10));
        tipPoint.translate(new Vector3(0,0,4f));
        sidePoint.translate(new Vector3(4f,0,armLength));
        swordCenter.translate(new Vector3(0,0,armLength));

        handleRoll.removeChild(normalPoint);
        normalPoint.clearParentInfo();

        handlePitch.objectInstance.transform.getRotation(cleanHandlePitch);
        handleYaw.objectInstance.transform.getRotation(cleanHandleYaw);

        swingDecalFace.set(swingDecalOrigin);
        swingDecalApex.set(swingDecalOrigin);
        swingDecalFace.add(new Vector3(1,4,0));
        swingDecalApex.add(new Vector3(0,0,1));
        Utils.rotateDecal(swingTraceDecal,swingDecalOrigin, swingDecalApex,swingDecalFace);
    }

    public void update(){
        deltaT = Gdx.graphics.getDeltaTime();
        timer += deltaT;

        swingTimer.update();
        Utils.rotateDecal(swordDecal,swordCenter.getWorldTranslation(),tipPoint.getWorldTranslation(),sidePoint.getWorldTranslation());
        handlePitch.setRotation(cleanHandlePitch);
        handleYaw.setRotation(cleanHandleYaw);
        //Do sth with handle
        cleanHandleRoll.set(handlePitch.getWorldRotation());
        cleanHandleYaw.set(handleYaw.getWorldRotation());
        rotateWithAccelerometer();

        //Check for block
        wasBlock = false;
        Gdx.app.error("Block", accelerometerController.checkIfBlock() + " ");
        if (canBlock && accelerometerController.checkIfBlock()) {
            wasBlock = true;
            drawingBlock = true;
        }

        //Check for swing
        wasSwing = false;
        if (swingTimer.checkIfStop() && accelerometerController.checkIfSwing() && !wasBlock && !drawingBlock){
            swingTimer.reset();
            swingTraceDecal.setColor(1,1,1,1);
            wasSwing = true;

            swingDecalFace.set(swingDecalOrigin);
            swingDecalFace.add(new Vector3((float)(Math.random()*2 - 1),4,0));

            Utils.rotateDecal(swingTraceDecal,swingDecalOrigin, swingDecalApex,swingDecalFace);
        }

        if (drawingBlock){
            if (wasBlock){
                handlePitch.removeChild(handleRoll);
                handleRoll.clearParentInfo();
                movingToPoint = true;
                blockTimer.reset();
                blockTimer.clearTimes();
                blockTimer.setStopTime(0.8f);
            }
            blockTimer.update();
            if (!blockTimer.checkIfStop()) {
                performMovementTo(blockPoint, 0.15f, 15f);
            }
            if (blockTimer.checkIfStop()){
                movingToPoint = true;
                performMovementTo(normalPoint,0.015f,15f);
                if (!movingToPoint) {
                    handlePitch.addChild(handleRoll);

                    handleTrans.clearTransform();
                    handleYaw.clearTransform();
                    handlePitch.clearTransform();
                    handleRoll.clearTransform();
                    tipPoint.clearTransform();
                    sidePoint.clearTransform();
                    swordCenter.clearTransform();

                    handleTrans.translate(new Vector3(-1.1f,-3.5f,-8f));
                    handleYaw.setRotation(new Quaternion(new Vector3(0,1,0),10));
                    handlePitch.setRotation(new Quaternion(new Vector3(1,0,0),-45));
                    handleRoll.setRotation(new Quaternion(new Vector3(0,0,1),10));
                    tipPoint.translate(new Vector3(0,0,4f));
                    sidePoint.translate(new Vector3(4f,0,armLength));
                    swordCenter.translate(new Vector3(0,0,armLength));

                    handlePitch.objectInstance.transform.getRotation(cleanHandlePitch);
                    handleYaw.objectInstance.transform.getRotation(cleanHandleYaw);

                    drawingBlock = false;
                }
            }
        }

        float alpha = 1 - 0.5f * swingTimer.getTime();
        if (alpha < 0){
            alpha = 0;
        }
        swingTraceDecal.setColor(1,1,1, alpha);
    }

    public void rotateWithAccelerometer(){
        Vector3 acceleration = accelerometerController.getLastSmoothedAcceleration();
        float turnX = acceleration.x * 4;
        float turnY = acceleration.y * -4;
        handlePitch.rotate(new Quaternion(new Vector3(1,0,0),turnY));
        handleYaw.rotate(new Quaternion(new Vector3(0,1,0),turnX));
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
        handleRoll.rotateTo(object.getWorldRotation(),fraction);
        if (handleRoll.distanceTo(object) > transSpeed *deltaT) {
            handleRoll.moveTo2(object.getWorldTranslation(), transSpeed *deltaT);
        } else {
            handleRoll.setTranslation(object.getWorldTranslation());
            rotationDistance = 0.0f;
            movingToPoint = false;
        }
    }

    public boolean checkForSwing(){
        return wasSwing;
    }

    public boolean checkForBlock(){
        return wasBlock;
    }

    public void allowBlock(){
        canBlock = true;
    }

    public void disallowBlock(){
        canBlock = false;
    }

    public void fillDecalBatch(DecalBatch decalBatch){
        decalBatch.add(swordDecal);
        decalBatch.add(swingTraceDecal);
    }

    public void fillModelBatch(ModelBatch modelBatch){
        //modelBatch.render(normalPoint.objectInstance);
        //modelBatch.render(blockPoint.objectInstance);
        //modelBatch.render(swordCenter.objectInstance);
        //modelBatch.render(tipPoint.objectInstance);
        //modelBatch.render(sidePoint.objectInstance);
    }
}
