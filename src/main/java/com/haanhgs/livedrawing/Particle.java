package com.haanhgs.livedrawing;

import android.graphics.PointF;

class Particle {

    final PointF velocity;
    final PointF position;

    Particle(PointF direction) {
        velocity = new PointF();
        position = new PointF();

        // Determine the direction
        velocity.x = direction.x;
        velocity.y = direction.y;
    }

    void update(float fps) {
        // Move the particle
        position.x += velocity.x;
        position.y += velocity.y;
    }

    void setPosition(PointF position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    PointF getPosition() {
        return position;
    }
}