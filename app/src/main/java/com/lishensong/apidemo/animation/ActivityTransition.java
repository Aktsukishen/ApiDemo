package com.lishensong.apidemo.animation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lishensong.apidemo.R;

import java.util.List;
import java.util.Map;

/**
 * Created by li.shensong on 2016/10/11.
 */
public class ActivityTransition  extends Activity {
    private static final String TAG = "ActivityTransition";

    private static final String KEY_ID = "ViewTransitionValues:id";

    private ImageView mHero;

    public static final int[] DRAWABLES = {
            R.drawable.ball,
            R.drawable.block,
            R.drawable.ducky,
            R.drawable.jellies,
            R.drawable.mug,
            R.drawable.pencil,
            R.drawable.scissors,
            R.drawable.woot
    };

    public static final int[] IDS = {
            R.id.ball,
            R.id.block,
            R.id.ducky,

    };

    public static final String[] NAMES = {
            "ball",
            "block",
            "ducky",
            "jellies",
            "mug",
            "pencil",
            "scissors",
            "woot",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(randomColor()));
        setContentView(R.layout.image_block);
        setupHero();
    }

    private void setupHero(){
        String name = getIntent().getStringExtra(KEY_ID);
        mHero = null;
        if(name != null){
            mHero = (ImageView) findViewById(getIdForKey(name));
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    sharedElements.put("hero",mHero);
                }
            });
        }
    }

    public void clicked(View view){
        mHero = (ImageView)view;
        Intent intent = new Intent(this,ActivityTransitionDetails.class);
        intent.putExtra(KEY_ID,view.getTransitionName());
        ActivityOptions activityOptions
                = ActivityOptions.makeSceneTransitionAnimation(this,mHero,"hero");
        startActivity(intent,activityOptions.toBundle());
    }

    public static int randomColor(){
        int red = (int)(Math.random() * 128);
        int green = (int)(Math.random() * 128);
        int blue = (int)(Math.random() * 128);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    public static int getDrawableIdForKey(String id){
        return DRAWABLES[getIndexForKey(id)];
    }

    public static int getIndexForKey(String id){
        for(int i = 0; i < NAMES.length ; i++){
            String name = NAMES[i];
            if(name.equals(id)){
                return i;
            }
        }
        return 2;
    }

    public static int getIdForKey(String id){
        return IDS[getIndexForKey(id)];
    }
}
