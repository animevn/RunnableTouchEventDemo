package com.haanhgs.livedrawing;

import android.graphics.PointF;

public class Particle {

    private PointF velocity;
    private PointF position;

    public Particle(PointF direction) {
        velocity = new PointF();
        position = new PointF();
        velocity.x = direction.x;
        velocity.y = direction.y;
    }

    //recheck
    public void update(){
        position.x += velocity.x;
        position.y += velocity.y;
    }

    public PointF getPosition() {
        return position;
    }

    //recheck
    public void setPosition(PointF position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }
}
