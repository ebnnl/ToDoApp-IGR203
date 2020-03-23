package com.example.todolist;

public class Person {
    // Classe représentant une personne

    private String name; // Nom de la personne
    private String color; // Couleur de la personne

    public Person(String name, String color){
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return this.name;
    }

    public int getImage(){
        if (color.equals("red")){
            return R.mipmap.red_man_foreground;
        }
        else if(color.equals("orange")){
            return R.mipmap.orange_man_foreground;
        }
        else if(color.equals("blue")){
            return R.mipmap.blue_man_foreground;
        }
        else if(color.equals("green")){
            return R.mipmap.green_man_foreground;
        }
        else if(color.equals("yellow")){
            return R.mipmap.yellow_man_foreground;
        }
        else if(color.equals("violet")){
            return R.mipmap.violet_man_foreground;
        }
        else return 0;
    }

    public String getColor(){
        return color;
    }
}
