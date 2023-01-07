package com.pk.modeltest;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

class ParallaxController{
    public List<ParallaxLayer> layers = new ArrayList<>();
    public void render(float dx, float dy){
        for (int i = 0; i < layers.size(); i++){
            if (layers.get(i).decal != null) {
                float depth = layers.get(i).depth;
                float shiftX = dx / depth;
                float shiftY = dy / depth;
                Vector3 shift = new Vector3(shiftX, shiftY, 0);
                Vector3 oldPosition = new Vector3();
                if (!layers.get(i).useCurrentOrigin) {
                    oldPosition.set(layers.get(i).decalOrigin);
                } else {
                    oldPosition.set(layers.get(i).decal.getPosition());
                }
                //Gdx.app.log("Debug", layers.get(i).origin.toString());
                oldPosition.add(shift);
                layers.get(i).decal.setPosition(oldPosition);
            }
            if (layers.get(i).object != null){
                float depth = layers.get(i).depth;
                float shiftX = dx / depth;
                float shiftY = dy / depth;
                Vector3 shift = new Vector3(shiftX, shiftY, 0);
                Vector3 oldPosition = new Vector3();
                if (!layers.get(i).useCurrentOrigin) {
                    oldPosition.set(layers.get(i).objectOrgin);
                } else {
                    oldPosition.set(layers.get(i).object.getWorldTranslation());
                }
                oldPosition.add(shift);
                //This is for flying sword parallax. Fix later.
                if (layers.get(i).object.getWorldRotation().getAngle() == 0.) {
                    layers.get(i).object.setTranslation(oldPosition);
                } else {
                    layers.get(i).object.setWorldTranslation(shift);
                }
            }
        }
    }
    public void fillDecalBatch(DecalBatch decalBatch){
        for (int i=0; i<layers.size(); i++){
            if (layers.get(i).decal != null) {
                decalBatch.add(layers.get(i).decal);
            }
        }
    }
}