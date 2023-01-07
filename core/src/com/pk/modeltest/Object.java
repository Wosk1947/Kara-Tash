package com.pk.modeltest;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

class Object{
    public Model objectModel = new Model();
    public ModelInstance objectInstance = new ModelInstance(objectModel);
    public Matrix4 localTransform = new Matrix4();
    public Matrix4 externalTransform = new Matrix4();
    public void initWithModel(Model model){
        objectModel = model;
        objectInstance = new ModelInstance(objectModel);
        objectInstance.transform.set(new Matrix4());
    }
    public List<Object> children = new ArrayList<>();
    public void addChild(Object object){
        children.add(object);
    }

    public void addChildClean(Object object) {
        Matrix4 tmpTransform = new Matrix4();
        tmpTransform.set(externalTransform);
        tmpTransform.mul(localTransform);
        Matrix4 invTransform = new Matrix4();
        invTransform.set(tmpTransform);
        invTransform.inv();
        invTransform.mul(object.localTransform);
        object.localTransform.set(invTransform);
        addChild(object);
    }

    public void clearTransform(){
        externalTransform = new Matrix4();
        localTransform = new Matrix4();
        objectInstance.transform = new Matrix4();
        updateChildren();
    }

    public void removeChild(Object object){
        int childNum = children.size();
        for (int i = 0; i < childNum; i++){
            if (children.get(i) == object){
                children.remove(i);
                break;
            }
        }
    }

    public void rotate(Quaternion quat){
        localTransform.rotate(quat);
        Matrix4 tempTransfrom = new Matrix4();
        tempTransfrom.set(externalTransform);
        tempTransfrom.mul(localTransform);
        objectInstance.transform.set(tempTransfrom);
        updateChildren();
    }

    public void setRotation(Quaternion quat){
        Vector3 localTrans = new Vector3();
        localTransform.getTranslation(localTrans);
        localTransform.set(new Matrix4());
        localTransform.translate(localTrans);
        localTransform.rotate(quat);
        Matrix4 tempTransfrom = new Matrix4();
        tempTransfrom.set(externalTransform);
        tempTransfrom.mul(localTransform);
        objectInstance.transform.set(tempTransfrom);
        updateChildren();
    }

    public void setTranslation(Vector3 vec){
        Vector3 localTrans = new Vector3();
        localTransform.setTranslation(vec);
        Matrix4 tempTransfrom = new Matrix4();
        tempTransfrom.set(externalTransform);
        tempTransfrom.mul(localTransform);
        objectInstance.transform.set(tempTransfrom);
        updateChildren();
    }

    public void setWorldTranslation(Vector3 vec){
        Matrix4 world = new Matrix4();
        world.setTranslation(vec);
        objectInstance.transform.set(world.mul(objectInstance.transform));
    }

    public void translate(Vector3 vec){
        localTransform.translate(vec);
        Matrix4 tempTransfrom = new Matrix4();
        tempTransfrom.set(externalTransform);
        tempTransfrom.mul(localTransform);
        objectInstance.transform.set(tempTransfrom);
        updateChildren();
    }

    public void setCleanTranslation(Vector3 vec){
        Matrix4 world = new Matrix4();
        world.setTranslation(vec);
        objectInstance.transform.set(world);
        Matrix4 externalInv = new Matrix4();
        externalInv.set(externalTransform);
        externalInv.inv();
        localTransform.set(world);
        localTransform.set(externalInv.mul(localTransform));
        updateChildren();
    }

    public void moveRotateTo(Object object, float fraction){
        Vector3 destPoint = object.getWorldTranslation();
        Quaternion destRotation = object.getWorldRotation();
        moveTo(destPoint, distanceTo(object)*fraction);
        rotateTo(destRotation, fraction);
    }

    public void moveTo(Vector3 vec, float distance){
        Vector3 direction = new Vector3();
        direction.set(vec);
        direction.sub(getWorldTranslation());
        direction.nor();
        direction.scl(distance);
        direction.add(getWorldTranslation());
        setCleanTranslation(direction);
    }

    public void moveTo2(Vector3 vec, float distance){
        Vector3 direction = new Vector3();
        direction.set(vec);
        direction.sub(getWorldTranslation());
        direction.nor();
        direction.scl(distance);
        Matrix4 world = new Matrix4();
        world.setTranslation(direction);
        world.mul(objectInstance.transform);
        Matrix4 externalInv = new Matrix4();
        externalInv.set(externalTransform);
        externalInv.inv();
        externalInv.mul(world);
        localTransform.set(externalInv);
        objectInstance.transform.set(externalInv);
        updateChildren();
    }

    public void rotateTo(Quaternion quat, float fraction){
        Quaternion rotation = getWorldRotation();
        rotation.slerp(quat, fraction);
        setRotation(rotation);
    }

    public void clearParentInfo(){
        combineTransform();
        clearExternalTransform();
    }

    public void clearExternalTransform(){
        externalTransform = new Matrix4();
    }

    public void combineTransform(){
        Matrix4 tempTransfrom = new Matrix4();
        tempTransfrom.set(externalTransform);
        tempTransfrom.mul(localTransform);
        localTransform.set(tempTransfrom);
        objectInstance.transform.set(tempTransfrom);
    }

    public float distanceTo(Object object){
        Vector3 p1 = new Vector3(getWorldTranslation());
        Vector3 p2 = new Vector3(object.getWorldTranslation());
        return p2.sub(p1).len();
    }

    public void updateChildren(){
        for (int i=0; i<children.size(); i++){
            Matrix4 childTransform = children.get(i).localTransform;
            Matrix4 tempTransform = new Matrix4();
            tempTransform.set(externalTransform);
            tempTransform.mul(localTransform);
            children.get(i).externalTransform.set(tempTransform);
            tempTransform.mul(childTransform);
            children.get(i).objectInstance.transform.set(tempTransform);
            children.get(i).updateChildren();
        }
    }

    public Vector3 getWorldTranslation(){
        Vector3 translation = new Vector3();
        this.objectInstance.transform.getTranslation(translation);
        return translation;
    }

    public Quaternion getWorldRotation(){
        Quaternion rotation = new Quaternion();
        this.objectInstance.transform.getRotation(rotation);
        return rotation;
    }
}
