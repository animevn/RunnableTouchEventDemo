package com.haanhgs.livedrawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends SurfaceView implements Runnable {

    private static final int MAX = 1000;
    private static final int NUM_OF_PARTICLES = 100;
    private final List<Particles> particlesList = new ArrayList<>();
    private Paint paint;
    private Canvas canvas;
    private int fontsize;
    private int fontMargin;
    private RectF buttonReset;
    private RectF buttonTogglePause;
    private float movesPerSec;
    private int nextParticles;
    private boolean pause = false;
    private volatile  boolean drawing;
    private Thread thread = null;
    private SurfaceHolder surfaceHolder;

    private void init(int x, int y){
        fontsize = x/20;
        fontMargin = x/75;
        surfaceHolder = getHolder();
        paint = new Paint();
        buttonReset = new RectF(300, 300, 400, 400);
        buttonTogglePause = new RectF(300, 450, 400, 550);

        //init list of particles
        for (int i = 0; i < MAX; i++){
            particlesList.add(new Particles());
            particlesList.get(i).initParticles(NUM_OF_PARTICLES);
        }
    }

    public DrawingView(Context context) {
        super(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int x = metrics.widthPixels;
        int y = metrics.heightPixels;
        init(x, y);
    }

    private void printDebuggingText() {
        int debugSize = fontsize /2;
        int debugStart = 150;
        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + movesPerSec, 10, debugStart + debugSize, paint);
        canvas.drawText("Systems: " + nextParticles,
                10, fontMargin + debugStart + debugSize * 2, paint);

        canvas.drawText("Particles: " + nextParticles * NUM_OF_PARTICLES,
                10, fontMargin + debugStart + debugSize * 3, paint);
    }

    public void draw(){
        if (surfaceHolder.getSurface().isValid()){
            //get canvas
            canvas = surfaceHolder.lockCanvas();
            //create black screen
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            //create text color
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(fontsize);
            //draw text
            printDebuggingText();
            //draw 2 buttons
            canvas.drawRect(buttonReset, paint);
            canvas.drawRect(buttonTogglePause, paint);

            //draw particles list
            for (int i = 0; i < nextParticles; i++){
                particlesList.get(i).drawParticles(canvas, paint);
            }

            //draw now
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void update(){
        for (Particles particles:particlesList){
            if (particles.isRunning()) particles.update(movesPerSec);
        }
    }

    @Override
    public void run() {
        while (drawing){
            long start = System.currentTimeMillis();
            if (!pause) update();
            draw();
            long elapse = System.currentTimeMillis() - start;
            if (elapse > 0){
                movesPerSec = 1000/elapse;
            }
        }
    }

    public void pause(){
        drawing = false;
        try{
            thread.join();
        }catch (InterruptedException e){
            Log.e("D.DrawingView", "error joining thread");
        }
    }

    public void resume(){
        drawing = true;
        thread = new Thread(this);
        thread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (buttonReset.contains(event.getX(), event.getY())){
            nextParticles = 0;
        }

        if (buttonTogglePause.contains(event.getX(), event.getY())){
            pause = !pause;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE){
            particlesList.get(nextParticles).emitParticles(new PointF(event.getX(), event.getY()));
            nextParticles++;
            if (nextParticles == MAX) nextParticles = 0;
        }

        return true;
    }
}
