package com.haanhgs.livedrawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class LiveDrawingView extends SurfaceView implements Runnable {

    private final SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private final Paint paint;
    private long fps;
    private final int fontsize;
    private final int fontMargin;
    private final ArrayList<ParticleSystem> particleSystems = new ArrayList<>();
    private int nextSystem = 0;
    private final int MAX_SYSTEMS = 1000;
    private final int PARTICLES_PER_SYSTEM = 100;
    private Thread thread = null;
    private volatile boolean drawing;
    private boolean pause = false;
    private final RectF buttonReset;
    private final RectF buttonTogglePause;

    public LiveDrawingView(Context context, int x, int y) {
        super(context);
        fontsize = x /20;
        fontMargin = x /75;
        surfaceHolder = getHolder();
        paint = new Paint();
        buttonReset = new RectF(300,300,400,400);
        buttonTogglePause = new RectF(300,450,400,550);
        for (int i = 0; i < MAX_SYSTEMS; i++) {
            particleSystems.add(new ParticleSystem());
            particleSystems.get(i).init(PARTICLES_PER_SYSTEM);
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,0,0,0));
            paint.setColor(Color.argb(255,255,255,255));
            paint.setTextSize(fontsize);
            for (int i = 0; i < nextSystem; i++) {
                particleSystems.get(i).draw(canvas, paint);
            }
            canvas.drawRect(buttonReset, paint);
            canvas.drawRect(buttonTogglePause, paint);
            printDebuggingText();
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void printDebuggingText() {
        int debugSize = fontsize /2;
        int debugStart = 150;
        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + fps, 10, debugStart + debugSize, paint);
        canvas.drawText("Systems: " + nextSystem,
                10, fontMargin + debugStart + debugSize * 2, paint);

        canvas.drawText("Particles: " + nextSystem * PARTICLES_PER_SYSTEM,
                10, fontMargin + debugStart + debugSize * 3, paint);
    }

    @Override
    public void run() {
        while (drawing) {
            long frameStartTime = System.currentTimeMillis();
            if (!pause) {
                update();
            }
            draw();
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;

            if (timeThisFrame > 0) {
                int MILLIS_IN_SECOND = 1000;
                fps = MILLIS_IN_SECOND /timeThisFrame;
            }
        }
    }

    private void update() {
        for (int i = 0; i < particleSystems.size(); i++) {
            if (particleSystems.get(i).isRunning) {
                particleSystems.get(i).update(fps);
            }
        }
    }

    public void pause() {
        drawing = false;
        try {
            thread.join();
        } catch (InterruptedException e) { Log.e("Error: ", "joining thread");}
    }

    public void resume() {
        drawing = true;
        thread = new Thread(this);
        thread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // User moved a finger while touching screen
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

            particleSystems.get(nextSystem).emitParticles(
                    new PointF(motionEvent.getX(),
                            motionEvent.getY()));

            nextSystem++;
            if (nextSystem == MAX_SYSTEMS) {
                nextSystem = 0;
            }
        }

        // Did the user touch the screen
        if ((motionEvent.getAction() &
                MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_DOWN) {

            // User pressed the screen see if it was in a button
            if (buttonReset.contains(motionEvent.getX(),
                    motionEvent.getY())) {
                // Clear the screen of all particles
                nextSystem = 0;
            }

            // User pressed the screen see if it was in a button
            if (buttonTogglePause.contains(motionEvent.getX(),
                    motionEvent.getY())) {
                pause = !pause;
            }
        }

        return true;
    }


}
