package com.example.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.*;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// Classe à base de choses trouvées sur stackOverflow
// Implémente un Listener pour détecter quand un mouvement de zoom (pincé) est fait sur l'objet

/* This listener is used to listen pinch zoom gesture. ß*/
public class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    Button button;
    float factor;

    public OnPinchListener(Button b) {
        super();
        button = b;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        factor = 1.0f;
        return true;
        //return super.onScaleBegin(detector);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        // Mise à jour de la taille ici
        float scaleFactor = detector.getScaleFactor() - 1;
        factor += scaleFactor;
        button.setScaleX(factor);
        button.setScaleY(factor);
        return true;
        //return super.onScale(detector);
    }

}