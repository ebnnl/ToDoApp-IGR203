package com.example.todolist;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.net.SecureCacheResponse;

// Classe à base de choses trouvées sur stackOverflow
// Implémente un Listener pour détecter quand l'utilisateur touche l'objet et se déplace

public class MultiTouchListener
        implements OnTouchListener
{

    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector scaleGestureDetector;



    public MainActivity mainActivity;
    public MultiTouchListener(MainActivity mainActivity1, ScaleGestureDetector scaleGestureDetector) {
        mainActivity = mainActivity1;
        this.scaleGestureDetector = scaleGestureDetector;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        float currX,currY;
        int action = event.getAction();
        switch (action ) {
            case MotionEvent.ACTION_DOWN: {

                mPrevX = event.getX();
                mPrevY = event.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {

                currX = event.getRawX();
                currY = event.getRawY();


                MarginLayoutParams marginParams = new MarginLayoutParams(view.getLayoutParams());
                marginParams.setMargins((int)(currX - mPrevX), (int)(currY - mPrevY),0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                view.setLayoutParams(layoutParams);


                break;
            }



            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:

                break;
        }


        return true;
    }


}