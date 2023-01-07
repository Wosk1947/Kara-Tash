# Kara-Tash
Motion controlled First Person Slasher for Android

<img src="https://user-images.githubusercontent.com/66104180/211170117-894de4cd-dffd-4959-b3a9-920b228577ce.gif" width="700" height="400" />

## Introduction
Kara-Tash is a prototype of motion controlled first person slasher game. The idea of this project is to simulate the feeling of a VR game using only mobile phone. Kara-Tash utilizes the phone both as the medium for observing game world and as a motion controller. The main goal is to provide seemless transition between the two states so that when users perform motion actions they don't lose the sense of immersion into the game world. This is achieved through sound design and delayed graphical effects which allow users to observe the consequences of their actions when they return their focus to the screen. 

## Technical details
The project utilizes LibGDX framework. During the development several major features were created from scratch:
- 3d scene graph (class Object.java)
- depth parallax (class ParallaxController.java)
- basic state machine for enemies (performState() method of class Swordsman.java)
- procedural animations for sprites and models (class Swordsman.java)
- particle system (class ParticleSpawner.java)

