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

    private final SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private final Paint mPaint;
    private long mFPS;
    private final int mFontSize;
    private final int mFontMargin;
    private final ArrayList<ParticleSystem>
            mParticleSystems = new ArrayList<>();
    private int mNextSystem = 0;
    private final int MAX_SYSTEMS = 1000;
    private final int mParticlesPerSystem = 100;
    private Thread mThread = null;
    private volatile boolean mDrawing;
    private boolean mPaused = true;
    private final RectF mResetButton;
    private final RectF mTogglePauseButton;

    public LiveDrawingView(Context context, int x, int y) {
        super(context);
        mFontSize = x /20;
        mFontMargin = x /75;
        mOurHolder = getHolder();
        mPaint = new Paint();
        mResetButton = new RectF(300,300,400,400);
        mTogglePauseButton = new RectF(300,450,400,550);
        for (int i = 0; i < MAX_SYSTEMS; i++) {
            mParticleSystems.add(new ParticleSystem());
            mParticleSystems.get(i).init(mParticlesPerSystem);
        }
    }

    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255,0,0,0));
            mPaint.setColor(Color.argb(255,255,255,255));
            mPaint.setTextSize(mFontSize);
            for (int i = 0; i < mNextSystem; i++) {
                mParticleSystems.get(i).draw(mCanvas, mPaint);
            }
            mCanvas.drawRect(mResetButton, mPaint);
            mCanvas.drawRect(mTogglePauseButton, mPaint);
            printDebuggingText();
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void printDebuggingText() {
        int debugSize = mFontSize/2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS, 10, debugStart + debugSize, mPaint);
        mCanvas.drawText("Systems: " + mNextSystem,
                10, mFontMargin + debugStart + debugSize * 2, mPaint);

        mCanvas.drawText("Particles: " + mNextSystem * mParticlesPerSystem,
                10, mFontMargin + debugStart + debugSize * 3, mPaint);
    }

    @Override
    public void run() {
        while (mDrawing) {
            long frameStartTime = System.currentTimeMillis();
            if (!mPaused) {
                update();
            }
            draw();
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;

            if (timeThisFrame > 0) {
                int MILLIS_IN_SECOND = 1000;
                mFPS = MILLIS_IN_SECOND /timeThisFrame;
            }
        }
    }

    private void update() {
        for (int i = 0; i < mParticleSystems.size(); i++) {
            if (mParticleSystems.get(i).mIsRunning) {
                mParticleSystems.get(i).update(mFPS);
            }
        }
    }

    public void pause() {
        mDrawing = false;
        try {
            mThread.join();
        } catch (InterruptedException e) { Log.e("Error: ", "joining thread");}
    }

    public void resume() {
        mDrawing = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // User moved a finger while touching screen
        if ((motionEvent.getAction() &
                MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_MOVE) {

            mParticleSystems.get(mNextSystem).emitParticles(
                    new PointF(motionEvent.getX(),
                            motionEvent.getY()));

            mNextSystem++;
            if (mNextSystem == MAX_SYSTEMS) {
                mNextSystem = 0;
            }
        }

        // Did the user touch the screen
        if ((motionEvent.getAction() &
                MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_DOWN) {

            // User pressed the screen see if it was in a button
            if (mResetButton.contains(motionEvent.getX(),
                    motionEvent.getY())) {
                // Clear the screen of all particles
                mNextSystem = 0;
            }

            // User pressed the screen see if it was in a button
            if (mTogglePauseButton.contains(motionEvent.getX(),
                    motionEvent.getY())) {
                mPaused = !mPaused;
            }
        }

        return true;
    }


}
