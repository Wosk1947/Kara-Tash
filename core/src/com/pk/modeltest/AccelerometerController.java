package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

class AccelerometerController{
    public List<Vector3> accelerationsRaw = new ArrayList<>();
    public List<Vector3> accelerations = new ArrayList<>();
    public List<Vector3> accelerationsSmoothed = new ArrayList<>();
    public int historyLength = 60;
    public float alpha = 0.9f;
    private Vector3 origin = new Vector3();
    public AccelerometerController(){}
    public void update(){
        Vector3 currentAcceleration = new Vector3(
                Gdx.input.getAccelerometerX(),
                Gdx.input.getAccelerometerY(),
                Gdx.input.getAccelerometerZ());
        accelerationsRaw.add(new Vector3(currentAcceleration));
        currentAcceleration.sub(origin);
        accelerations.add(currentAcceleration);
        if (accelerations.size() > historyLength){
            for (int i=0; i< accelerations.size()-historyLength; i++){
                accelerations.remove(0);
                accelerationsRaw.remove(0);
            }
        }
        smooth();
        if (accelerationsSmoothed.size() > historyLength){
            for (int i=0; i< accelerationsSmoothed.size()-historyLength; i++){
                accelerationsSmoothed.remove(0);
            }
        }
    }
    private void smooth(){
        if (accelerations.size() >= 2){
            Vector3 prevSmAcc = new Vector3();
            Vector3 curAcc = new Vector3();
            Vector3 curSmAcc = new Vector3();
            prevSmAcc.set(accelerationsSmoothed.get(accelerationsSmoothed.size()-1));
            curAcc.set(accelerations.get(accelerations.size()-1));
            prevSmAcc.scl(alpha);
            curAcc.scl(1-alpha);
            curSmAcc.set(prevSmAcc);
            curSmAcc.add(curAcc);
            accelerationsSmoothed.add(curSmAcc);
        } else {
            accelerationsSmoothed.add(accelerations.get(accelerations.size()-1));
        }
    }
    public Vector3 getLastAcceleration(){
        return accelerations.get(accelerations.size()-1);
    }

    public Vector3 getLastSmoothedAcceleration(){
        return accelerationsSmoothed.get(accelerationsSmoothed.size()-1);
    }

    public boolean checkIfSwing(){
        if (getLastAcceleration().len2() > 3000){
            return true;
        }
        return false;
    }

    public boolean checkIfBlock(){
        float maxAbsAcceleration = 14f*14f;
        float minAbsAcceleration = 7f*7f;
        float maxYvsXZRatio = 0.5f;
        int lengthOfSearch = 10;
        int blockFramesCounter = 0;
        int minBlockFrames = 2;
        //Gdx.app.error("A:", "X: "+accelerationsRaw.get(accelerationsRaw.size()-1).x + " Y: "+accelerationsRaw.get(accelerationsRaw.size()-1).y+" Z: "+ accelerationsRaw.get(accelerationsRaw.size()-1).z);
        for (int i = Math.max(accelerationsRaw.size() - lengthOfSearch, 0); i < accelerationsRaw.size(); i++){
            float ratio = (float)(Math.abs(accelerationsRaw.get(i).y) / Math.pow((accelerationsRaw.get(i).x * accelerationsRaw.get(i).x + accelerationsRaw.get(i).z * accelerationsRaw.get(i).z),0.5f));
            if (accelerationsRaw.get(i).len2() < maxAbsAcceleration && accelerationsRaw.get(i).len2() > minAbsAcceleration && ratio <= maxYvsXZRatio) {
                blockFramesCounter ++;
                if (blockFramesCounter >= minBlockFrames){
                    return true;
                }
            } else {
                blockFramesCounter = 0;
            }
        }
        return false;
    }

    public void calibrate(){
        origin.set(getLastAcceleration());
    }
}
