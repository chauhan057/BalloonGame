package com.example.baloon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BalloonGameActivity extends Activity {

    private BalloonView balloonView;
    private Handler handler = new Handler();
    MediaPlayer pop;
    MediaPlayer wrong;

    private Paint textPaint = new Paint(); //for Display Text
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        balloonView = new BalloonView(this);
        setContentView(balloonView);
        pop=MediaPlayer.create(this,R.raw.balloon);
        wrong=MediaPlayer.create(this,R.raw.funny);

    }

    private class Balloon {
        float x, y;      // Position
        int color;       // Balloon color
        boolean isPopped;
        float speedY;    // Vertical speed
        float size;      // Balloon size

        Balloon(float x, float y, int color, float speedY, float size) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.isPopped = false;
            this.speedY = speedY+8;
            this.size = size;
        }

        void move() {
            // Update the balloon's position
            y -= speedY;

            // Check if the balloon has gone off the screen
            if (y < -size) {
                // Reset the balloon's position to the bottom
                y = balloonView.getHeight();
                x = new Random().nextInt(balloonView.getWidth());
                isPopped = false;
            }
        }
    }

    private class BalloonView extends View {
        private List<Balloon> balloons = new ArrayList<>();
        private Paint paint = new Paint();
        private Paint targetPaint = new Paint();
        private boolean targetShown = false;

        public BalloonView(Context context) {
            super(context);
            paint.setAntiAlias(true);
            targetPaint.setAntiAlias(true);
            targetPaint.setColor(Color.BLUE); // Set the target balloon color to blue
            targetPaint.setStyle(Paint.Style.STROKE);
            targetPaint.setStrokeWidth(5);
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.BLUE);
            textPaint.setTextSize(60); // Adjust text size as needed
        }
        private void createBalloons(int width, int height) {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                float x = random.nextInt(width);
                float y = random.nextInt(height);
                int color = random.nextBoolean() ? Color.BLUE : Color.RED;
                float speedY = random.nextFloat() * 3 + 2; // Adjust the speed as needed
                float size = random.nextFloat() * 50 + 50;
                if(i==8){
                    color=Color.BLUE;
                }
                balloons.add(new Balloon(x, y, color, speedY, size));
            }
        }
        private void startGameLoop() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Update balloon positions
                    for (Balloon balloon : balloons) {
                        balloon.move();
                    }
                    // Redraw the view
                    invalidate();

                    // Repeat the loop
                    handler.postDelayed(this, 16); // Adjust the delay as needed
                }
            }, 0); // Start immediately
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            createBalloons(w, h);
            startGameLoop();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Balloon balloon : balloons) {
                if (!balloon.isPopped) {
                    paint.setColor(balloon.color);
                    canvas.drawCircle(balloon.x, balloon.y, balloon.size, paint);
                }
            }
            String text =" Your Target is Blue Balloon";
            float textX = canvas.getWidth() / 2 - textPaint.measureText(text) / 2;
            float textY = (float) (canvas.getHeight() / 1.1);
            canvas.drawText(text, textX, textY, textPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float touchX = event.getX();
            float touchY = event.getY();

            Iterator<Balloon> iterator = balloons.iterator();
            while (iterator.hasNext()) {
                Balloon balloon = iterator.next();
                if(!balloon.isPopped&&!(balloon.color==Color.BLUE)){
                        float dx = balloon.x - touchX;
                        float dy = balloon.y - touchY;
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);
                        if(distance<balloon.size){
                            if (wrong != null) {
                                wrong.seekTo(0); // Rewind to the beginning
                                wrong.start();
                            }
                        }
                }
                if (!balloon.isPopped && balloon.color==Color.BLUE) {
                    float dx = balloon.x - touchX;
                    float dy = balloon.y - touchY;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    if (distance < balloon.size) {
                        balloon.isPopped = true;
                        if (pop != null) {
                            pop.seekTo(0); // Rewind to the beginning
                            pop.start();
                        }
                        invalidate(); // Redraw the view to remove the popped balloon
                    }
                }
            }
            return super.onTouchEvent(event);
        }
    }
}