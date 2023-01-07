package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class ParticleSpawner {
    private List<Particle> particles = new ArrayList<>();
    private int particleNumber;
    private float particleVelocity;
    private float particleVelocityDispersion;
    private float particleVelocityDegradation;
    private Vector3 particleDirection = new Vector3();
    private float particleDispersion;
    private float gravity = -9.81f;
    private Vector3 origin = new Vector3();
    private Texture particleTexture;
    private String particleTextureName;
    private float particleScale;
    private float particleScaleDispersion;
    private boolean particleRotating;
    private float rotationSpeed;
    private float rotationSpeedDispersion;
    private ParallaxController parallaxController;
    private boolean spawnerStop = false;
    public ParticleSpawner(int particleNumber,
                           float particleVelocity,
                           float particleVelocityDispersion,
                           float particleVelocityDegradation,
                           Vector3 origin,
                           Vector3 particleDirection,
                           float particleDispersion,
                           float gravity,
                           String particleTextureName,
                           float particleScale,
                           float particleScaleDispersion,
                           boolean particleRotating,
                           float rotationSpeed,
                           float rotationSpeedDispersion,
                           ParallaxController parallaxController){
        this.particleScaleDispersion = particleScaleDispersion;
        this.particleRotating = particleRotating;
        this.rotationSpeed = rotationSpeed;
        this.rotationSpeedDispersion = rotationSpeedDispersion;
        this.parallaxController = parallaxController;
        this.particleNumber = particleNumber;
        this.particleVelocity = particleVelocity;
        this.particleVelocityDegradation = particleVelocityDegradation;
        this.particleDirection.set(particleDirection);
        this.particleDispersion = particleDispersion;
        this.gravity = gravity;
        this.particleTexture = new Texture(Gdx.files.internal(particleTextureName));
        this.particleTextureName = particleTextureName;
        this.particleScale = particleScale;
        this.origin.set(origin);
        this.particleVelocityDispersion = particleVelocityDispersion;
        startSpawner();
    }

    private void startSpawner(){
        for (int i = 0; i < particleNumber; i++){
            //Create particle
            float speed = (float)(particleVelocity + 2*(Math.random() - 0.5f)*particleVelocityDispersion);
            Vector3 up = new Vector3(0,1,0);
            Vector3 direction = new Vector3();
            direction.set(particleDirection);
            float xAngle = (float)(2*(Math.random() - 0.5f)*particleDispersion);
            float yAngle = (float)(2*(Math.random() - 0.5f)*particleDispersion);
            direction.rotate(up,xAngle);
            Vector3 horizontal = new Vector3();
            horizontal.set(direction);
            horizontal.crs(up);
            direction.rotate(horizontal,yAngle);
            direction.nor();
            direction.scl(speed);
            //Gdx.app.error("ParticleSpawner", direction.toString());
            particles.add(new Particle(particleTexture, particleScale, particleScaleDispersion, origin, direction, gravity, particleVelocityDegradation, particleRotating, rotationSpeed, rotationSpeedDispersion));
            addParallax(parallaxController, particles.get(particles.size()-1).decal,4);
        }
    }

    private void addParallax(ParallaxController parallaxController, Decal decal, float depth){
        parallaxController.layers.add(new ParallaxLayer(decal,null,depth,true));
    }

    public void update(){
        float deltaT = Gdx.graphics.getDeltaTime();
        for (int i = 0; i < particles.size(); i++){
            particles.get(i).update(deltaT);
            if (particles.get(i).checkIfLeftStage()){
                for (int j=0;j<parallaxController.layers.size();j++){
                    if (particles.get(i).decal == parallaxController.layers.get(j).decal){
                        parallaxController.layers.remove(j);
                        break;
                    }
                }
                particles.remove(i);
                i--;
            }
        }
        if (particles.size() == 0){
            spawnerStop = true;
        }
        //Gdx.app.error("ParticleSpawner", "========================");
    }

    public boolean checkIfSpawnerStop(){
        return spawnerStop;
    }

    public void dispose(){
        for (int i = 0; i < particles.size(); i++){
            for (int j=0;j<parallaxController.layers.size();j++){
                if (particles.get(i).decal == parallaxController.layers.get(j).decal){
                    parallaxController.layers.remove(j);
                    break;
                }
            }
            particles.remove(i);
            i--;
        }
        particleTexture.dispose();
    }

    public void fillDecalBatch(DecalBatch decalBatch){
        for (Particle particle : particles){
            //Gdx.app.error("ParticleSpawner decal", particle.decal.getPosition().toString());
            decalBatch.add(particle.decal);
        }
    }
}

class Particle {
    private boolean leftStage = false;
    public Decal decal;
    private Vector3 position = new Vector3();
    private Vector3 velocity = new Vector3();
    private float gravity = -9.81f;
    private float scale;
    private float scaleDispersion;
    private float degradation;
    private boolean rotating;
    private float rotationSpeed;
    private float rotationSpeedDispersion;
    private float angle;
    private Vector3 axis = new Vector3();
    Object center = new Object();
    Object up = new Object();
    Object face = new Object();

    public Particle(Texture texture, float scale, float scaleDispersion, Vector3 position, Vector3 velocity, float gravity, float degradation, boolean rotating, float rotationSpeed, float rotationSpeedDispersion){
        this.position.set(position);
        this.velocity.set(velocity);
        this.gravity = gravity;
        this.decal = Decal.newDecal(new TextureRegion(texture), true);
        this.scale = scale;
        this.scaleDispersion = scaleDispersion;
        this.degradation = degradation;
        this.rotating = rotating;
        this.rotationSpeed = rotationSpeed + (float)(2*(Math.random()-1)*rotationSpeedDispersion);
        this.rotationSpeedDispersion = rotationSpeedDispersion;
        this.decal.setScaleX(scale+2*((float)Math.random()-1)*scaleDispersion);
        this.decal.setScaleY(scale+2*((float)Math.random()-1)*scaleDispersion);
        center.initWithModel(new Model());
        up.initWithModel(new Model());
        center.initWithModel(new Model());
        center.addChild(up);
        center.addChild(face);
        up.translate(new Vector3(0,1,0));
        face.translate(new Vector3(0,0,-1));
        center.translate(position);
        if (rotating) {
            axis.set((float)(2*(Math.random()-1)),(float)(2*(Math.random()-1)),(float)(2*(Math.random()-1)));
            angle = (float)(Math.random()*360);
            center.setRotation(new Quaternion(axis,angle));
            Utils.rotateDecal(decal,center.getWorldTranslation(),up.getWorldTranslation(),face.getWorldTranslation());
        }
        //update();
    }

    public void update(float deltaT){
        Vector3 newVelocity = new Vector3();
       // Gdx.app.error("ParticleSpawner", velocity.toString());
        newVelocity.set(this.velocity);
        //newVelocity.set(newVelocity.x * degradation, newVelocity.y > 0 ? newVelocity.y * degradation : newVelocity.y, newVelocity.z * degradation);

        Vector3 dV = new Vector3();
        dV.set(0, gravity, 0);
        dV.scl(deltaT);
        newVelocity.add(dV);

        Vector3 deceleration = new Vector3();
        deceleration.set(newVelocity);
        deceleration.nor();
        deceleration.scl(newVelocity.len2());
        deceleration.scl(degradation);

        newVelocity.sub(deceleration);

        this.velocity.set(newVelocity);
        Vector3 translation = new Vector3();
        translation.set(newVelocity);
        translation.scl(deltaT);
        this.position.add(translation);

        this.decal.setPosition(position);

        center.setTranslation(position);
        if (rotating) {
            angle += rotationSpeed;
            center.setRotation(new Quaternion(axis,angle));
            Utils.rotateDecal(decal,center.getWorldTranslation(),up.getWorldTranslation(),face.getWorldTranslation());
        }

        if (position.y < -20){
            leftStage = true;
        }

        Utils.shadowDecal(decal);
    }

    public boolean checkIfLeftStage(){
        return leftStage;
    }
}
