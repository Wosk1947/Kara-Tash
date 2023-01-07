package com.pk.modeltest;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

import java.util.ArrayList;
import java.util.List;

public class Enemy {
    public void update(){}
    public void fillModelBatch(ModelBatch modelBatch){}
    public void fillDecalBatch(DecalBatch decalBatch){}
    public boolean checkIfLeftStage(){return false;}
    public void dispose(){}
    public float masterSoundVolume;
    //Empty class for Enemy
}
