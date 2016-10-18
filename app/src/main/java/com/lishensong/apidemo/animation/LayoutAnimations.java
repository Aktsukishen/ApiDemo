package com.lishensong.apidemo.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/12.
 */
public class LayoutAnimations extends Activity {
    private int numButtons = 1;
    ViewGroup container = null;
    Animator defaultAppearingAnim,defaultDisappearingAnim;
    Animator defaultChangingAppearingAnim,defaultChangingDisappearingAnim;
    Animator customAppearingAnim,customDisappearingAnim;
    Animator customChangingAppearingAnim,customChangingDisappearingAnim;
    Animator currentApperingAnim,currentDisappearingAnim;
    Animator currentChangingAppearingAnim,currentChangingDisappearingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_animations);

        container = new FixedGridLayout(this);
        container.setClipChildren(false);
        ((FixedGridLayout) container).setmCellHeight(90);
        ((FixedGridLayout) container).setmCellWidth(100);

        final LayoutTransition transitioner = new LayoutTransition();
        container.setLayoutTransition(transitioner);
        defaultAppearingAnim = transitioner.getAnimator(LayoutTransition.APPEARING);
        defaultDisappearingAnim = transitioner.getAnimator(LayoutTransition.DISAPPEARING);
        defaultChangingAppearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_APPEARING);
        defaultChangingDisappearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_DISAPPEARING);

        createCustomAnimations(transitioner);
        currentApperingAnim = defaultAppearingAnim;
        currentDisappearingAnim = defaultDisappearingAnim;
        currentChangingAppearingAnim = defaultChangingAppearingAnim;
        currentChangingDisappearingAnim = defaultChangingDisappearingAnim;

        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        parent.addView(container);
        parent.setClipChildren(true);

        Button button = (Button) findViewById(R.id.addNewButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button newButton = new Button(LayoutAnimations.this);
                newButton.setText(String.valueOf(numButtons ++));
                newButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        container.removeView(v);
                    }
                });
                container.addView(newButton,Math.min(1,container.getChildCount()));
            }
        });

        CheckBox customAnimCB = (CheckBox) findViewById(R.id.customAnimCB);
        customAnimCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupTransition(transitioner);
            }
        });

        CheckBox disappearingCB = (CheckBox) findViewById(R.id.disappearingCB);
        disappearingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupTransition(transitioner);
            }
        });

        CheckBox changingAppearingCB = (CheckBox) findViewById(R.id.changingAppearingCB);
        changingAppearingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupTransition(transitioner);
            }
        });

        CheckBox changingDisappearingCB = (CheckBox) findViewById(R.id.changingDisappearingCB);
        changingDisappearingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupTransition(transitioner);
            }
        });
    }

    private void createCustomAnimations(LayoutTransition transition){
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left",0,1);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top",0,1);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right",0,1);
        PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom",0,1);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX",1f,0f,1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY",1f,0f,1f);

        customChangingAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                                pvhLeft,pvhTop,pvhRight,pvhBottom,pvhScaleX,pvhScaleY)
                                .setDuration(transition.getDuration(LayoutTransition.CHANGE_APPEARING));
        customChangingAppearingAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        // Changing while Removing
        Keyframe kf0 = Keyframe.ofFloat(0f,0f);
        Keyframe kf1 = Keyframe.ofFloat(0.9999f,360f);
        Keyframe kf2 = Keyframe.ofFloat(1f,0f);
        PropertyValuesHolder pvhRotation =
                PropertyValuesHolder.ofKeyframe("rotation",kf0,kf1,kf2);
        customChangingDisappearingAnim = ObjectAnimator.ofPropertyValuesHolder(
                                            this,pvhLeft,pvhTop,pvhRight,pvhBottom,pvhRotation)
                                            .setDuration(transition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
        customChangingDisappearingAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator)anim).getTarget();
                view.setRotationY(0f);
            }
        });

        customAppearingAnim = ObjectAnimator.ofFloat(null,"rotationY",90f,0f)
                                .setDuration(transition.getDuration(LayoutTransition.APPEARING));
        customAppearingAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setRotationY(0f);
            }
        });

        customDisappearingAnim = ObjectAnimator.ofFloat(null,"rotationX",0f,90f)
                .setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
        customDisappearingAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setRotationY(0f);
            }
        });
    }

    private void setupTransition(LayoutTransition transition){
        CheckBox customAnimCB = (CheckBox) findViewById(R.id.customAnimCB);
        CheckBox appearingCB = (CheckBox) findViewById(R.id.appearingCB);
        CheckBox disappearingCB = (CheckBox) findViewById(R.id.disappearingCB);
        CheckBox changingAppearingCB = (CheckBox) findViewById(R.id.changingAppearingCB);
        CheckBox changingDisappearingCB = (CheckBox) findViewById(R.id.changingDisappearingCB);
        transition.setAnimator(LayoutTransition.APPEARING,appearingCB.isChecked() ?
                (customAnimCB.isChecked() ? currentApperingAnim : defaultAppearingAnim) : null);
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappearingCB.isChecked() ?
                (customAnimCB.isChecked() ? customDisappearingAnim : defaultDisappearingAnim) : null);
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, changingAppearingCB.isChecked() ?
                (customAnimCB.isChecked() ? customChangingAppearingAnim :
                        defaultChangingAppearingAnim) : null);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
                changingDisappearingCB.isChecked() ?
                        (customAnimCB.isChecked() ? customChangingDisappearingAnim :
                                defaultChangingDisappearingAnim) : null);
    }
}
