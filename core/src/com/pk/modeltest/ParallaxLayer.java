package com.pk.modeltest;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

class ParallaxLayer{
    public Decal decal;
    public Object object;
    public float depth;
    public boolean useCurrentOrigin = false;
    Vector3 decalOrigin = new Vector3();
    Vector3 objectOrgin = new Vector3();
    public ParallaxLayer(Decal decal, Object object, float depth, boolean useCurrentOrigin){
        this.decal = decal;
        this.object = object;
        this.depth = depth;
        this.useCurrentOrigin = useCurrentOrigin;
        if (decal != null) {
            decalOrigin.set(this.decal.getPosition());
        }
        if (object != null) {
            objectOrgin.set(this.object.getWorldTranslation());
        }
    }
}
