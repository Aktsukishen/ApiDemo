package com.lishensong.apidemo.animation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/11.
 */
public class ActivityTransitionDetails extends Activity{
    private static final String TAG = "ActivityTransitionDetails";
    private static final String KEY_ID = "ViewTransitionValues:id";

    private int mImageResourceId = R.drawable.ducky;
    private String mName = "ducky";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(randomColor()));
        ImageView titleImage = (ImageView)findViewById(R.id.titleImage);
        titleImage.setImageDrawable(getHeroDrawable());
    }

    private Drawable getHeroDrawable(){
        String name = getIntent().getStringExtra(KEY_ID);
        if(name != null){
            mName = name;
            mImageResourceId = ActivityTransition.getDrawableIdForKey(name);
        }
        return getResources().getDrawable(mImageResourceId);
    }

    public void clicked(View view){
        Intent intent = new Intent(this,ActivityTransition.class);
        intent.putExtra(KEY_ID,mName);
        ActivityOptions activityOptions
                = ActivityOptions.makeSceneTransitionAnimation(this,view,"hero");
        startActivity(intent,activityOptions.toBundle());
    }

    public static int randomColor(){
        int red = (int)(Math.random() * 128);
        int green = (int)(Math.random() * 128);
        int blue = (int)(Math.random() * 128);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
}
