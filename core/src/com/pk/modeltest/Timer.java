package com.pk.modeltest;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

class Timer {
    private float time = 0;
    private boolean isActive = true;
    public List<Float> times = new ArrayList<>();
    private int timesCounter = 0;
    public Timer(){

    }
    public Timer(List<Float> times){
        this.times.addAll(times);
    }
    public void setTime(float time){this.time = time;}
    public void setStopTime(float time){
        times.add(time);
    }
    public void addTimes(List<Float> times){
        this.times.addAll(times);
    }
    public void addRandomTimes(int number, float startTime, float minInterval, float maxInterval){
        float prevTime = startTime;
        for (int i=0; i<number; i++) {
            prevTime += minInterval + Math.random() * (maxInterval - minInterval);
            this.times.add(prevTime);
        }
    }
    public void update(){
        if (isActive) {
            time += Gdx.graphics.getDeltaTime();
        }
    }

    public void clearTimes(){
        times.clear();
    }

    public void reset(){
        time = 0f;
        timesCounter = 0;
        isActive = true;
    }
    public boolean checkIfStop() {
        if (isActive) {
            if (timesCounter > times.size() - 1) {
                return true;
            }
            if (time > times.get(timesCounter)) {
                timesCounter++;
                return true;
            }
        }
        return false;
    }
    public float getTime(){return this.time;}
    public void deactivate(){this.isActive = false;}
    public void activate(){this.isActive = true;}
    public boolean isActive(){return this.isActive;}
    public int getCounter(){return timesCounter;}
    public boolean isFinished(){return timesCounter > times.size() - 1;}
}