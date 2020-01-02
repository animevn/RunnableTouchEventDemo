package com.haanhgs.livedrawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.Random;

class ParticleSystem {

    private float duration;

    private ArrayList<Particle> particles;
    private final Random random = new Random();
    boolean isRunning = false;

    @SuppressWarnings("SameParameterValue")
    void init(int numParticles){
        particles = new ArrayList<>();

        for (int i = 0; i < numParticles; i++) {
            float angle = (random.nextInt(360)) ;
            angle = angle * 3.14f / 180.f;

            float speed = (random.nextFloat()/3);

            // Option 2 - Fast particles
            //float speed = (random.nextInt(10)+1);

            PointF direction;

            direction = new PointF((float)Math.cos(angle) * speed,
                    (float)Math.sin(angle) * speed);

            particles.add(new Particle(direction));
        }
    }

    void update(long fps){
        duration -= (1f/fps);

        for(Particle p : particles){p.update(fps);}

        if (duration < 0) {
            isRunning = false;}
    }

    void emitParticles(PointF startPosition){
        isRunning = true;

        // Option 1 - System lasts for half a minute
        duration = 30f;

        // Option 2 - System lasts for 2 seconds
        //duration = 3f;

        for(Particle p : particles) {p.setPosition(startPosition);}

    }

    void draw(Canvas canvas, Paint paint) {

        for (Particle p : particles) {

            // Option 1 - Coloured particles
            //paint.setARGB(255, random.nextInt(256),
            //random.nextInt(256),
            //random.nextInt(256));

            // Option 2 - White particles
            paint.setColor(Color.argb(255,255,255,255));


            // How big is each particle?
            float sizeX;
            float sizeY;

            // Option 1 - Big particles
            //sizeX = 25;
            //sizeY = 25;

            // Option 2 - Medium particles
            //sizeX = 10;
            //sizeY = 10;

            // Option 3 - Tiny particles
            sizeX = 6;
            sizeY = 6;

            // Draw the particle
            // Option 1 - Square particles
            canvas.drawRect(p.getPosition().x, p.getPosition().y,
                    p.getPosition().x + sizeX,
                    p.getPosition().y + sizeY,
                    paint);

            // Option 2 - Circle particles
            //canvas.drawCircle(p.getPosition().x, p.getPosition().y,
            //sizeX, paint);
        }
    }

}
