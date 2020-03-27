package com.example.todolist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    private int lastAction;

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes

    private String taskName; // Nom de la tâche qu'on a touché
    private String groupName; // Nom du groupe de cette tâche

    public MainActivity mainActivity;
    public MultiTouchListener(MainActivity mainActivity1, ScaleGestureDetector scaleGestureDetector, String taskName, String groupName) {
        // Ouvrir la base de données
        dataBase = new DAOBase(mainActivity1);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();

        mainActivity = mainActivity1;
        this.taskName = taskName;
        this.groupName = groupName;
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
                lastAction = event.getAction();
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {

                currX = event.getRawX();
                currY = event.getRawY();

                // Mise à jour de la position ici
                MarginLayoutParams marginParams = new MarginLayoutParams(view.getLayoutParams());
                marginParams.setMargins((int)(currX - mPrevX), (int)(currY - mPrevY),0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                view.setLayoutParams(layoutParams);
                lastAction = event.getAction();

                break;
            }



            case MotionEvent.ACTION_CANCEL:
                lastAction = event.getAction();
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    // Action à effectuer au click

                    // Ouvrir l'activity pour voir la tâche
                    Intent seeTaskActivity = new Intent(mainActivity, SeeTaskActivity.class);
                    // Spécifier le nom de la tâche à afficher
                    Bundle b = new Bundle();
                    b.putString("taskName", taskName);
                    b.putString("groupName", groupName);
                    seeTaskActivity.putExtras(b);
                    mainActivity.startActivity(seeTaskActivity);
                    lastAction = event.getAction();
                }

                break;
        }



        return true;
    }




}