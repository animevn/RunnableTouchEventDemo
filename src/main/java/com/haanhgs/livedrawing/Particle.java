package com.haanhgs.livedrawing;

import android.graphics.PointF;

public class Particle {

    private PointF position;
    private PointF velocity;

    public Particle(PointF direction) {
        position = new PointF();
        velocity = new PointF();
        velocity.set(direction.x, direction.y);
    }

    public void update(){
        position.x += velocity.x;
        position.y += velocity.y;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position.set(position.x, position.y);
    }
}
