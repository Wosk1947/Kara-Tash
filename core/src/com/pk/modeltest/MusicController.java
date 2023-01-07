package com.pk.modeltest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicController {
    private List<List<Integer>> soundIndecesForLayer = new ArrayList<>();
    private List<List<Float>> soundVolumesForLayer = new ArrayList<>();
    private List<Sound> sounds = new ArrayList<>();
    private Map<String,Sound> signToSoundMap = new HashMap<>();
    private Map<String, String> nameToSignMap = new HashMap<>();
    private List<Timer> timersForLayer = new ArrayList<>();

    private String mainTrack = "(1[1.0]1[0.8]1{0.4}[0.2]1[2.0])2,(1[1.0]1[1.0]1[1.0]1[1.0])0,(1[0.5]1{0.4}[0.25]1{0.4}[0.25]1{1.0}[0.5]1{0.4}[0.5]1[0.5]1[1.5])2;([0.5]2[0.5]2[1.0])20;([3.0]3[3.75]3{0.4}[0.25]3[1.0])10";
    private String mainTrackLegend = "1=drum_2_2.wav;2=stick_2.wav;3=stick_3.wav;4=drum_6_1.wav";
    private String track2 = "(1[1.0]1{0.7}[0.51]1{0.3}[0.49]1{0.8}[0.5]1[1.5]1[1.0]1{0.7}[0.51]1{0.3}[0.49]1{0.8}[0.5]1[1.5]1[1.0]1{0.7}[0.51]1{0.3}[0.49]1{0.8}[0.5]1[1.5]1[1.0]3{0.8}[1.0]3[2.0])10";
    private String track3 = "(1[1.0]1{0.3}[0.5]1{0.7}[0.5]1{0.8}[1.0]1{1.0}[1.0]1{0.7}[1.0]1{0.9}[1.0]1{1.0}[1.0]1{1.0}[1.0])1000"; //([0.05]1[1.0]1{0.3}[0.51]1{0.7}[0.49]1{0.8}[1.0]1{1.0}[1.0]1{0.7}[1.0]1{0.9}[1.0]1{1.0}[1.0]1{1.0}[0.95])10
    private String backgroundDrums = "(4{0.4}[0.5])1000";
    private String track3_smallDrum = "([0.04]4[1.0]4{0.3}[0.51]4{0.7}[0.49]4{0.8}[1.0]4{1.0}[1.0]4{0.7}[1.0]4{0.9}[1.0]4{1.0}[1.0]4{1.0}[0.96])10";
    private String track4 = "(1[0.5]1{0.7}[0.5]1{0.4}[0.5]1{0.4}[0.5]1[2.0]1[0.5]1{0.7}[0.5]1{0.4}[0.5]1{0.4}[0.5]1[2.0]1[0.5]1{0.7}[0.5]1{0.4}[0.5]1{0.4}[0.5]1[2.0]1[0.5]1{0.7}[0.5]1{0.4}[0.5]1{0.4}[0.5]1[1.0]1[1.0])3;([0.04]4[0.5]4{0.7}[0.5]4{0.4}[0.5]4{0.4}[0.5]4[2.0]4[0.5]4{0.7}[0.5]4{0.4}[0.5]4{0.4}[0.5]4[2.0]4[0.5]4{0.7}[0.5]4{0.4}[0.5]4{0.4}[0.5]4[2.0]4[0.5]4{0.7}[0.5]4{0.4}[0.5]4{0.4}[0.5]4[1.0]4[0.96])3;(4{0.4}[0.5])1000";

    private float speed = 4;

    public MusicController(){
        parseTrackLegend(mainTrackLegend);
        parseTrackString(track3);

      //  multiplyLayer(0);
      //  shiftTimesInLayer(1, 0.01f);
       // changeVolumesInLayer(1,0.8f);

       // multiplyLayer(0);
       // shiftTimesInLayer(2, 0.02f);
       // changeVolumesInLayer(2,0.8f);
        //changeInstrumentInLayer(1, "1", "4");

        randomizeTimes();

        Gdx.app.error("MusicController. times: ", timersForLayer.get(0).times.toString());
        Gdx.app.error("MusicController. sounds: ", soundIndecesForLayer.get(0).toString());
    }

    public void update(){
        for (int i=0; i<timersForLayer.size(); i++){
            if (!timersForLayer.get(i).isActive()){
                continue;
            }
            timersForLayer.get(i).update();
            if (timersForLayer.get(i).checkIfStop()){
                int tCount = timersForLayer.get(i).getCounter()-1;
                if (tCount < soundIndecesForLayer.get(i).size()) {
                    long id = sounds.get(soundIndecesForLayer.get(i).get(tCount)).play(soundVolumesForLayer.get(i).get(tCount));
                    //sounds.get(soundIndecesForLayer.get(i).get(tCount)).setPitch(id,1+Utils.random(0.01f));
                }
            }
            if (timersForLayer.get(i).isFinished()){
                timersForLayer.get(i).deactivate();
            }
        }
    }

    public void parseTrackLegend(String trackLegend){
        List<String> soundEntries = Arrays.asList(trackLegend.split(";"));
        for (String soundEntry:soundEntries){
            List<String> signAndSound = Arrays.asList(soundEntry.split("="));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal(signAndSound.get(1))));
            signToSoundMap.put(signAndSound.get(0),sounds.get(sounds.size()-1));
        }
    }

    public void parseTrackString(String track){
        int layerNum = 0;
        List<String> layers = Arrays.asList(track.split(";"));
        for (String layer: layers){
            soundIndecesForLayer.add(new ArrayList<Integer>());
            soundVolumesForLayer.add(new ArrayList<Float>());
            timersForLayer.add(new Timer());
            float time = 0f;
            //timersForLayer.get(layerNum).setStopTime(time);
            List<String> parts = Arrays.asList(layer.split(","));
            for (String part:parts){
                int repeat = 0;
                int repeatIndex = part.indexOf(")");
                if (repeatIndex != -1){
                    repeat = Integer.parseInt(part.substring(repeatIndex + 1));
                }
                Gdx.app.error("MusicController. repeats", repeat + "");
                String sample = "";
                if (repeatIndex != -1) {
                    sample = part.substring(1,repeatIndex);
                } else {
                    sample = part;
                }

                Gdx.app.error("MusicController. sample", sample);

                List<Integer> sampleInts = new ArrayList<>();
                List<Float> sampleTimes = new ArrayList<>();
                List<Float> sampleVolumes = new ArrayList<>();

                boolean parsingPause = false;
                int pauseStartIndex = 0;
                int pauseEndIndex = 0;

                boolean parsingVolume = false;
                int volumeStartIndex = 0;
                int volumeEndIndex = 0;

                for (int i=0; i<sample.length(); i++){
                    String character = Character.toString(sample.charAt(i));
                    if (i==0 && !character.equals("[")){
                        sampleTimes.add(0.0f);
                    }
                    if (i==sample.length()-1 && !character.equals("]")){
                        sampleTimes.add(0.0f);
                    }
                    if (!parsingPause && !parsingVolume) {
                        if (!character.equals("[") && !character.equals("]") && !character.equals("{") && !character.equals("}")) {
                            sampleInts.add(sounds.indexOf(signToSoundMap.get(character)));
                            if (!Character.toString(sample.charAt(i + 1)).equals("{")) {
                                sampleVolumes.add(1.0f);
                            }
                        }
                    }

                    if (character.equals("[")){
                        pauseStartIndex = i+1;
                        parsingPause = true;
                    }
                    if (character.equals("{")){
                        volumeStartIndex = i+1;
                        parsingVolume = true;
                    }
                    if (character.equals("]")){
                        pauseEndIndex = i;
                        parsingPause = false;
                        float pause = Float.parseFloat(sample.substring(pauseStartIndex,pauseEndIndex));
                        sampleTimes.add(pause);
                    }
                    if (character.equals("}")){
                        volumeEndIndex = i;
                        parsingVolume = false;
                        float volume = Float.parseFloat(sample.substring(volumeStartIndex,volumeEndIndex));
                        sampleVolumes.add(volume);
                    }
                }
                Gdx.app.error("MusicController. lists", sampleTimes.toString());
                Gdx.app.error("MusicController. lists", sampleInts.toString());

                for (int i=0; i<repeat; i++){
                    soundIndecesForLayer.get(layerNum).addAll(sampleInts);
                    soundVolumesForLayer.get(layerNum).addAll(sampleVolumes);
                    for (int j=0; j<sampleTimes.size(); j++){
                        if (timersForLayer.get(layerNum).times.size() == 0){
                            timersForLayer.get(layerNum).setStopTime(sampleTimes.get(j)/speed);
                        } else {
                            int lastTimesIndex = timersForLayer.get(layerNum).times.size() - 1;
                            if (j==0) {
                                timersForLayer.get(layerNum).times.set(lastTimesIndex, timersForLayer.get(layerNum).times.get(lastTimesIndex) + sampleTimes.get(j) / speed);
                            } else {
                                timersForLayer.get(layerNum).setStopTime(timersForLayer.get(layerNum).times.get(lastTimesIndex) + sampleTimes.get(j) / speed);
                            }
                        }
                        //timersForLayer.get(layerNum).setStopTime(time+sampleTimes.get(j)/speed);
                        //time+=sampleTimes.get(j)/speed;
                    }
                    //Gdx.app.error("MusicController. sampleMod", sampleTimes.toString());
                    //timersForLayer.get(layerNum).addTimes(sampleTimes);
                    //time += sampleTimes.get(sampleTimes.size()-1);
                }
            }
            layerNum++;
        }
    }

    private void multiplyLayer(int layerNum){
        List<Integer> tmp = new ArrayList<>(soundIndecesForLayer.get(layerNum));
        soundIndecesForLayer.add(tmp);
        timersForLayer.add(new Timer(timersForLayer.get(layerNum).times));
        List<Float> tmpF = new ArrayList<>(soundVolumesForLayer.get(layerNum));
        soundVolumesForLayer.add(tmpF);
    }

    private void randomizeVolumes(){
        for (int i=0; i<timersForLayer.size(); i++){
            for (int k=0; k<soundVolumesForLayer.get(i).size(); k++){
                float volume = soundVolumesForLayer.get(i).get(k);
                volume += Utils.random(0.05f);
                volume = Math.max(0,volume);
                soundVolumesForLayer.get(i).set(k,volume);
            }
        }
    }

    private void changeInstrumentInLayer(int layerNum, String i1, String i2){
        for (int k=0; k<soundIndecesForLayer.get(layerNum).size(); k++){
            int index = soundIndecesForLayer.get(layerNum).get(k);
            if (index == sounds.indexOf(signToSoundMap.get(i1))){
                index = sounds.indexOf(signToSoundMap.get(i2));
                soundIndecesForLayer.get(layerNum).set(k,index);
            }
        }
    }

    private void changeVolumesInLayer(int layerNum, float percent){
        for (int k=0; k<soundVolumesForLayer.get(layerNum).size(); k++){
            float volume = soundVolumesForLayer.get(layerNum).get(k);
            volume *= percent;
            soundVolumesForLayer.get(layerNum).set(k,volume);
        }
    }

    private void shiftTimesInLayer(int layerNum, float seconds){
        for (int k=0; k<timersForLayer.get(layerNum).times.size(); k++){
            float time = timersForLayer.get(layerNum).times.get(k);
            time += seconds;
            timersForLayer.get(layerNum).times.set(k,time);
        }
    }

    private void randomizeTimes(){
        for (int i=0; i<timersForLayer.size(); i++){
            float sumDelay = 0f;
            for (int k=0; k<timersForLayer.get(i).times.size(); k++){
                float time = timersForLayer.get(i).times.get(k);
                if (k!=timersForLayer.get(i).times.size()-1) {
                    float dt = Utils.random(0.01f);
                    time += dt;
                    if (time < 0) {
                        time = 0;
                    } else {
                        sumDelay += dt;
                    }
                } else {
                    time -= sumDelay;
                }
                timersForLayer.get(i).times.set(k,time);
            }
        }
    }
}
