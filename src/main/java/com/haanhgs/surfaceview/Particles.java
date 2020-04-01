package com.haanhgs.surfaceview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Particles {

    private float duration;
    private List<Particle>particles;
    private boolean running = false;
    private Random random = new Random();

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void initParticles(int numOfParticles){
        particles = new ArrayList<>();
        for (int i = 0; i < numOfParticles; i++){
            double angle = random.nextFloat() * Math.PI * 2;
            float speed = random.nextFloat();
            PointF direction = new PointF(
                    (float)Math.cos(angle) * speed,
                    (float)Math.sin(angle) * speed
            );
            particles.add(new Particle(direction));
        }
    }

    public void startParticles(PointF startPosition){
        duration = Constants.DURATION;
        running = true;
        for (Particle particle:particles) particle.setPosition(startPosition);
    }

    public void updateParticles(float fps){
        duration -= 1/fps;
        for (Particle particle:particles) particle.update();
        if (duration < 0) duration = 0;
    }

    public void drawParticles(Canvas canvas, Paint paint){
        for (Particle particle:particles){
            paint.setARGB(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

            //Circle particle
//            canvas.drawCircle(particle.getPosition().x, particle.getPosition().y,
//                    Constants.RADIUS, paint);

//            Square particle
            canvas.drawRect(
                    particle.getPosition().x,
                    particle.getPosition().y,
                    particle.getPosition().x + Constants.SIZE_X,
                    particle.getPosition().y + Constants.SIZE_Y,
                    paint);
        }
    }













}
