package com.haanhgs.livedrawing;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class LiveDrawingActivity extends Activity {
    private LiveDrawingView liveDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        liveDrawingView = new LiveDrawingView(this, size.x, size.y);
        setContentView(liveDrawingView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        liveDrawingView.resume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        liveDrawingView.pause();

    }
}
