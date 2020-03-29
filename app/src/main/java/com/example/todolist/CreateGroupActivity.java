package com.example.todolist;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CreateGroupActivity extends AppCompatActivity {

    // Activité pour créer une groupe

    private DAOBase dataBase; // Pour manipuler la base
    private GroupsList groupsList;

    private EditText nameEditText; // Champ pour entrer le nom de la tâche
    private FloatingActionButton validateButton; // Bouton pour enregistrer
    private LinearLayout membersLayout;

    private String groupName = "New Group";    // Nom du groupe de la tâche
    private String memberName = "Moi"; // Nom du createur du groupe
    private Group groupToAdd = new Group(groupName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        dataBase = new DAOBase(this);
        dataBase.open();
        groupsList = dataBase.getGroupsList();

        setTitle("Créer un groupe");


        nameEditText = (EditText) findViewById(R.id.activity_create_group_name_input);
        validateButton = findViewById(R.id.activity_create_group_validate);
        membersLayout = findViewById(R.id.activity_create_group_member_layout);

        validateButton.setEnabled(false); // Initialement, on ne peut pas valider (attendre qu'un nom
        // de groupe soit entré)
        validateButton.setVisibility(View.GONE);

        updateContent();

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Quand un nom de tâche est entré, rendre le bouton enregistrer visible
                validateButton.setEnabled(true);
                validateButton.setVisibility(View.VISIBLE);
                groupName = nameEditText.getText().toString();
                groupToAdd.setName(groupName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Réaction au bouton valider :
        validateButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                // Afficher un toast
                Toast toast = Toast.makeText(CreateGroupActivity.this, "Groupe créée", duration);
                toast.show();
                // Ajouter le groupe à la base
                dataBase.addGroup(groupToAdd);

                // Revenir à l'activité principale
                CreateGroupActivity.this.finish();

            }
        });


    }

        // Fonction pour mettre à jour la liste des membres
    public void updateContent() {
        // Récupérer la liste des personnes
        List<Person> persons = groupsList.getPersons();
        // Vider l'ensemble des radio button
        membersLayout.removeAllViews();
        // Ajouter un radio Button pour chaque personne
        for (int i = 0; i < persons.size(); i++) {
            final Person person = persons.get(i);
            final CheckBox checkBoxPerson = new CheckBox((this));
            checkBoxPerson.setText(person.getName());
            checkBoxPerson.setId(i);
            int image = person.getImage();
            Drawable img = this.getResources().getDrawable(image);
            img.setBounds(0, 0, 150, 150);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                checkBoxPerson.setBackground(img);
            }
            checkBoxPerson.setGravity(Gravity.BOTTOM);

            // Réaction au check
            checkBoxPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (isChecked){
                        groupToAdd.addPerson(person);
                    }
                    else {
                        groupToAdd.removePerson(person);
                    }
                }

            });


            membersLayout.addView(checkBoxPerson);
        }


    }

}
