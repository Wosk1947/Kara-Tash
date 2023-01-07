package com.pk.modeltest;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class Utils {
    public static Vector3 lightSource;

    public static void rotateDecal(Decal decal, Vector3 p1, Vector3 p2, Vector3 p3){
        Vector3 direction = new Vector3();
        direction.set(p2);
        direction.sub(p1);
        decal.setPosition(p1);
        decal.lookAt(p3,direction);
    }

    public static void strectchDecal(Decal decal, Vector3 p1, Vector3 p2, PerspectiveCamera camera){
        Vector3 direction = new Vector3();
        direction.set(p2);
        direction.sub(p1);

        float length = direction.len();

        Vector3 center = new Vector3();
        center.set(direction);
        center.scl(0.5f);
        center.add(p1);

        Vector3 look = new Vector3();
        look.set(decal.getPosition());
        look.sub(camera.position);

        Vector3 face = new Vector3();
        face.set(direction);
        face.crs(look);
        face.crs(direction);

        decal.setScaleY(length*0.005f);
        decal.setPosition(center);
        decal.lookAt(face,direction);
    }

    public static void shadowDecal(Decal decal){
        float decalZ = decal.getPosition().z;
        float lightSourceZ = lightSource.z;
        if (decalZ - lightSourceZ > 0){
            float m = (float)Math.exp(-0.05*(decalZ-lightSourceZ));
            if (m<0){
                m=0;
            }
            decal.setColor(m,m,m,1);
        }
    }

    public static float random(float halfInteval){
        return (float)((2*Math.random()-1)*halfInteval);
    }
}
