package com.lishensong.apidemo.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.lishensong.apidemo.R;

import java.util.ArrayList;

/**
 * Created by li.shensong on 2016/10/11.
 */
public class BouncingBalls extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouncing_balls);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(new MyAnimationView(this));
    }

    public class MyAnimationView extends View {
        private static final int RED = 0xffFF8080;
        private static final int BLUE = 0xff8080FF;
        private static final int CYAN = 0xff80ffff;
        private static final int GREEN = 0xff80ff80;

        public final ArrayList<ShapeHolder> balls = new ArrayList<>();
        AnimationSet animation = null;

        public MyAnimationView(Context context){
            super(context);

            ValueAnimator colorAnim = ObjectAnimator.ofInt(this,"backgroundColor",RED,BLUE);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() != MotionEvent.ACTION_DOWN &&
                    event.getAction() != MotionEvent.ACTION_MOVE){
                return false;
            }
            ShapeHolder newBall = addBall(event.getX(),event.getY());

            float startY = newBall.getY();
            float endY = getHeight() - 50f;
            float h = (float)getHeight();
            float eventY = event.getY();
            int duration = (int)(500 * ((h - eventY) /h));

            ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall,"y",startY,endY);
            bounceAnim.setDuration(duration);
            bounceAnim.setInterpolator(new AccelerateInterpolator());

            ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall,"x",newBall.getX(),
                    newBall.getX() - 25f);
            squashAnim1.setDuration(duration/4);
            squashAnim1.setRepeatCount(1);
            squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim1.setInterpolator(new DecelerateInterpolator());

            ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall,"width",
                    newBall.getWidth(),newBall.getWidth() + 50);
            squashAnim2.setDuration(duration /4);
            squashAnim2.setRepeatCount(1);
            squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim2.setInterpolator(new DecelerateInterpolator());

            ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall,"y",endY,endY + 25f);
            stretchAnim1.setDuration(duration/4);
            stretchAnim1.setRepeatCount(1);
            stretchAnim1.setInterpolator(new DecelerateInterpolator());
            stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);

            ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall,"height",
                    newBall.getHeight(),newBall.getHeight() -25f);
            stretchAnim1.setDuration(duration/4);
            stretchAnim1.setRepeatCount(1);
            stretchAnim1.setInterpolator(new DecelerateInterpolator());
            stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);

            ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall,"y",endY,startY);
            bounceBackAnim.setDuration(duration);
            bounceBackAnim.setInterpolator(new DecelerateInterpolator());

            AnimatorSet bouncer = new AnimatorSet();
            bouncer.play(bounceAnim).before(squashAnim1);
            bouncer.play(squashAnim1).with(squashAnim2);
            bouncer.play(squashAnim1).with(stretchAnim1);
            bouncer.play(squashAnim1).with(stretchAnim2);
            bouncer.play(bounceBackAnim).after(stretchAnim2);

            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall,"alpha",1f,0f);
            fadeAnim.setDuration(250);
            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    balls.remove(((ObjectAnimator)animation).getTarget());
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(bouncer).before(fadeAnim);

            animatorSet.start();

            return  true;
        }

        private ShapeHolder addBall(float x,float y){
            OvalShape circle = new OvalShape();
            circle.resize(50f,50f);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x - 25f);
            shapeHolder.setY(y - 25f);
            int red = (int)(Math.random() * 255);
            int green = (int)(Math.random() * 255);
            int blue = (int)(Math.random() * 255);
            int color = 0xFF000000 | red << 16 | green << 8 | blue;
            Paint paint = drawable.getPaint();
            int darkColor = 0xFF000000 | red/4 << 16 | green/4 << 8 | blue/4;
            RadialGradient gradient = new RadialGradient(37.5f,12.5f,50f,color,darkColor, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            shapeHolder.setPaint(paint);
            balls.add(shapeHolder);
            return shapeHolder;
        }

    }
}
