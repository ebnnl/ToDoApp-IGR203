package com.example.todolist;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private RadioGroup memberRadioGroup; // Ensemble de radio button pour choisir le responsable

    private String groupName = "New Group";    // Nom du groupe de la tâche
    private String memberName = "Moi"; // Nom du createur du groupe
    private Group group = new Group(groupName);

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
        memberRadioGroup = findViewById(R.id.activity_create_group_member_group);

        validateButton.setEnabled(false); // Initialement, on ne peut pas valider (attendre qu'un nom
        // de groupe soit entré)
        validateButton.setVisibility(View.GONE);


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
                dataBase.addGroup(group);

                // Revenir à l'activité principale
                CreateGroupActivity.this.finish();

            }
        });

        // Réaction au choix du responsable :
        memberRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                List<Group> groups = groupsList.getList();
                for (int i = 0; i < groups.size(); i++) {
                    Group groupaux = groups.get(i);
                    List<Person> persons = groupaux.getPersons();
                    memberName = persons.get(checkedId).getName();
                }
            }

        });
    }

        // Fonction pour mettre à jour la liste des membres
    public void updateContent() {
        // Récupérer la liste des personnes
        List<Group> groups = groupsList.getList();
        List<Person> persons = groupsList.getGroup(groups.get(0).getName()).getPersons();
        for (int i = 1; i < groups.size(); i++) {
            Group groupaux = groups.get(i);
            persons.addAll(groupaux.getPersons());
            }
        // Vider l'ensemble des radio button
        memberRadioGroup.removeAllViews();
        // Ajouter un radio Button pour chaque personne
        for (int i = 0; i < persons.size(); i++) {
            Person person = persons.get(i);
            RadioButton radioButtonPerson = new RadioButton(this);
            radioButtonPerson.setText(person.getName());
            radioButtonPerson.setId(i);
            int image = person.getImage();
            Drawable img = this.getResources().getDrawable(image);
            img.setBounds(0, 0, 150, 150);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                radioButtonPerson.setBackground(img);
            }
            radioButtonPerson.setGravity(Gravity.BOTTOM);
            memberRadioGroup.addView(radioButtonPerson);
        }


    }

}
