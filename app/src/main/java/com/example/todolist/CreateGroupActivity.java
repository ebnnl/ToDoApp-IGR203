package com.example.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class CreateGroupActivity extends AppCompatActivity {

    // Activité pour créer une groupe

    private DAOBase dataBase; // Pour manipuler la base
    private GroupsList groupsList;

    private EditText nameEditText; // Champ pour entrer le nom de la tâche
    private FloatingActionButton validateButton; // Bouton pour enregistrer
    private LinearLayout membersLayout; // Layout où afficher les membres
    private Button newMemberButton; // Bouton pour créer un membre

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
        newMemberButton = findViewById(R.id.activity_create_group_new_member_button);

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

        // Réaction au bouton créer un membre
        newMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir un dialog pour créer un membre
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
                builder.setTitle("Créer un nouveau membre");
                builder.setMessage("Nom du membre");

                // Create edit text
                Context context = builder.getContext();
                final LinearLayout linearLayout = new LinearLayout(CreateGroupActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText nameEditText = new EditText(context);
                TextView textViewColor = new TextView(context);
                textViewColor.setText("Couleur du membre :");
                final Spinner colorSpinner = new Spinner(context);
                final Person personToCreate = new Person("name", "red");

                // Add the buttons
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String memberName = nameEditText.getText().toString();
                        if (!memberName.equals("")){
                            personToCreate.setName(memberName);
                            dataBase.addPerson(personToCreate, new Group(""));
                            updateContent(); // Mettre à jour la liste des membres
                        }
                    }
                });

                // Configurer le spinner pour choisir la couleur du memebre
                List colorsList = new ArrayList<String>();
                colorsList.add("red");
                colorsList.add("yellow");
                colorsList.add("orange");
                colorsList.add("purple");
                colorsList.add("green");
                colorsList.add("blue");
                ArrayAdapter adapterColor = new ArrayAdapter(context, android.R.layout.simple_spinner_item, colorsList);
                adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                colorSpinner.setAdapter(adapterColor);

                colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String color = (String) colorSpinner.getItemAtPosition(position);
                        personToCreate.setColor(color);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                linearLayout.addView(nameEditText);
                linearLayout.addView(textViewColor);
                linearLayout.addView(colorSpinner);
                dialog.setView(linearLayout, 30, 0, 30, 0);
                dialog.show();
            }
        });


    }

        // Fonction pour mettre à jour la liste des membres
    public void updateContent() {
        dataBase = new DAOBase(this);
        dataBase.open();
        groupsList = dataBase.getGroupsList();
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
