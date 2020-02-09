package com.haanhgs.livedrawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends SurfaceView implements Runnable {

    private List<Particles> particlesList = new ArrayList<>();
    private Paint paint;
    private Canvas canvas;
    private int fontsize;
    private int fontMargin;
    private RectF buttonReset;
    private RectF buttonTogglePause;
    private float fps;
    private int nextParticles;
    private boolean pause = false;
    private volatile  boolean drawing;
    private Thread thread = null;
    private SurfaceHolder surfaceHolder;

    private void init(int x, int y){
        fontsize = x/20;
        fontMargin = x/75;
        buttonReset = new RectF(300, 300, 400, 400);
        buttonTogglePause = new RectF(300, 450, 400, 550);

        surfaceHolder = getHolder();
        paint = new Paint();

        //create particles list
        for (int i = 0; i < Constants.MAX; i++){
            particlesList.add(new Particles());
            particlesList.get(i).initParticles(Constants.NUM_OF_PARTICLES);
        }
    }

    public DrawingView(Context context) {
        super(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        init(metrics.widthPixels, metrics.heightPixels);
    }

    private void printDebuggingText() {
        int debugSize = fontsize /2;
        int debugStart = 150;
        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + fps, 10, debugStart + debugSize, paint);
        canvas.drawText("Systems: " + nextParticles,
                10, fontMargin + debugStart + debugSize * 2, paint);

        canvas.drawText("Particles: " + nextParticles * Constants.NUM_OF_PARTICLES,
                10, fontMargin + debugStart + debugSize * 3, paint);
    }

    private void draw(){
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

            //draw particles
            for (int i = 0; i < nextParticles; i++ ){
                particlesList.get(i).drawParticles(canvas, paint);
            }

            //unlock and post draw.
            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    private void update(){
        for (Particles particles:particlesList){
            if (particles.isRunning())particles.updateParticles(fps);
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
                fps = 1000/elapse;
            }
        }
    }

    public void resume(){
        drawing = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        drawing = false;
        try{
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
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

        if (event.getAction() == MotionEvent.ACTION_MOVE
                && !buttonReset.contains(event.getX(), event.getY())
                && !buttonTogglePause.contains(event.getX(), event.getY())){
            particlesList.get(nextParticles).startParticles(new PointF(event.getX(), event.getY()));
            nextParticles++;
            if (nextParticles == Constants.MAX) nextParticles = 0;
        }
        return true;
    }


}
