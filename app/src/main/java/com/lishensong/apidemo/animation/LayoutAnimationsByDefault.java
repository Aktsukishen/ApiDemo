package com.lishensong.apidemo.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/12.
 */
public class LayoutAnimationsByDefault extends Activity {

    private int numButtons = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_animations_by_default);
        final GridLayout gridContainer = (GridLayout) findViewById(R.id.gridContainer);
        Button button = (Button)findViewById(R.id.addNewButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button newButton = new Button(LayoutAnimationsByDefault.this);
                newButton.setText(String.valueOf(numButtons++));
                newButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        gridContainer.removeView(v);
                    }
                });
                gridContainer.addView(newButton,Math.min(1,gridContainer.getChildCount()));
            }
        });
    }
}
