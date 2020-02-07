package com.haanhgs.livedrawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Particles {

    private float duration;
    private List<Particle>particleList;
    private Random random = new Random();
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void initParticles(int numOfParticles){
        particleList = new ArrayList<>();
        for (int i = 0; i < numOfParticles; i++){
            //convert angle to radiant     - recheck
            float angle = random.nextInt(360) * (float)Math.PI/180f;
            float speed = random.nextFloat()/3;

            PointF direction = new PointF(
                    (float)Math.cos(angle)*speed,
                    (float)Math.sin(angle)*speed
            );
            particleList.add(new Particle(direction));
        }
    }

    public void update(float movePerSec){
        duration -= 1/ movePerSec;
        for (Particle particle:particleList) particle.update();
        if (duration < 0) running = false;
    }

    public void emitParticles(PointF startPosition){
        running = true;
        duration = 30f;
        for (Particle particle:particleList) particle.setPosition(startPosition);
    }

    public void drawParticles(Canvas canvas, Paint paint){
        for (Particle particle:particleList){
            paint.setARGB(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

//            //Square particle
//            float sizeX = 3f;
//            float sizeY = 3f;
//
//            canvas.drawRect(
//                    particle.getPosition().x,
//                    particle.getPosition().y,
//                    particle.getPosition().x + sizeX,
//                    particle.getPosition().y + sizeY,
//                    paint);
            //Circle particle
            float radius = 3f;
            canvas.drawCircle(particle.getPosition().x, particle.getPosition().y, radius, paint);
        }
    }
}




























