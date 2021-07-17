package com.ok.golight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ok.golight.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private WindowManager windowManager;
    private View overlayPowerView;
    private boolean overlayEnabled = false;
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dimension
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        // Tab
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getApplicationContext())){
                    CharSequence text = "Please grant the access to the application.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", getPackageName(), null)));
                } else {
                    if (!overlayEnabled) {
                        startPowerOverlay();
                        overlayEnabled = true;
                    }
                    else {
                        stopPowerOverlay();
                        overlayEnabled = false;
                    }
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void startPowerOverlay() {
        // Starts the button overlay.
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // Get light layout
        final LayoutInflater factory = getLayoutInflater();
        final View overlayEntryView = factory.inflate(R.layout.screen_light_layout, null);
        overlayPowerView = overlayEntryView.findViewById(R.id.overlay_layout);
        ImageButton closeBtn = overlayPowerView.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(overlayPowerView);
                overlayEnabled = false;
            }
        });

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // APPLICATION_OVERLAY FOR ANDROID 26+ AS THE PREVIOUS VERSION RAISES ERRORS
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // FOR PREVIOUS VERSIONS USE TYPE_PHONE AS THE NEW VERSION IS NOT SUPPORTED
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 400;
        params.height = 500;
//        params.screenBrightness = 1;
        params.width = width;

        windowManager.addView(overlayPowerView, params);

        ImageButton resizeBtn =  overlayPowerView.findViewById(R.id.resize_btn);
        resizeBtn.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
//             int exactlyTouchX = (int) (event.getRawX() - params.x - params.width);
//             int exactlyTouchY = (int) (event.getRawY() - initialY - params.height);
//             if (Math.abs(exactlyTouchY) < 50 && params.width <= width && params.height <= height) {
//    //                            params.width = (int) event.getRawX() - initialX;
//    //                            params.height = (int) event.getRawY() - initialY;
//                 params.height += event.getX() / Math.abs(event.getX());
//             }
             switch (event.getAction()){
                 case MotionEvent.ACTION_MOVE:
                     params.width = (int) event.getRawX() - params.x;
                     params.height = (int) event.getRawY() - params.y;
//                     params.width += 8*event.getX()/Math.abs(event.getX());
//                     params.height += 8*event.getY()/Math.abs(event.getY());
                     windowManager.updateViewLayout(overlayPowerView, params);
                     if (params.width > width) {
                         params.width = width;
                     }
                     if (params.width < 100) {
                         params.width = 100;
                     }
                     if (params.height > height) {
                         params.height = height;
                     }
                     if (params.height < 100) {
                         params.height = 100;
                     }
                     break;
             }
             windowManager.updateViewLayout(overlayPowerView, params);
             return true;
             }
         });

        overlayPowerView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long latestPressTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save current x/y
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        // Check for double clicks.
                        if (latestPressTime == 0 || latestPressTime + 500 < System.currentTimeMillis()) {
                            latestPressTime = System.currentTimeMillis();
                        } else {
                            // Doubleclicked. Do any action you'd like
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(overlayPowerView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void stopPowerOverlay() {
        windowManager.removeView(overlayPowerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (overlayPowerView != null)
//            windowManager.removeView(overlayPowerView);
    }
}