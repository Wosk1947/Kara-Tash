package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class BackGround {
    public static void createBackGround(List<String> layerTextures,
                                        List<Vector3> positions,
                                        List<Float> scales,
                                        List<Float> virtualDepths,
                                        ParallaxController parallaxController){
        for (int i=0; i<layerTextures.size(); i++){
            Texture tmpTexture = new Texture(Gdx.files.internal(layerTextures.get(i)));
            Decal tmpDecal = Decal.newDecal(new TextureRegion(tmpTexture), true);
            tmpDecal.setPosition(positions.get(i));
            tmpDecal.setScale(scales.get(i));
            parallaxController.layers.add(new ParallaxLayer(tmpDecal,null,virtualDepths.get(i),false));
        }
    }
}
