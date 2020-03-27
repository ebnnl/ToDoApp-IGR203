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
            return R.mipmap.yellow_man;
        }
        else if(color.equals("violet")){
            return R.mipmap.violet_man_foreground;
        }
        else return 0;
    }

    public int getImage2(){
        if (color.equals("red")){
            return R.mipmap.red_man;
        }
        else if(color.equals("orange")){
            return R.mipmap.orange_man;
        }
        else if(color.equals("blue")){
            return R.mipmap.blue_man;
        }
        else if(color.equals("green")){
            return R.mipmap.green_man;
        }
        else if(color.equals("yellow")){
            return R.mipmap.yellow_man;
        }
        else if(color.equals("violet")){
            return R.mipmap.violet_man;
        }
        else return 0;
    }

    public int getColorInt(){
        if (color.equals("red")){
            return R.color.colorRed;
        }
        else if(color.equals("orange")){
            return R.color.colorOrange;
        }
        else if(color.equals("blue")){
            return R.color.colorBlue;
        }
        else if(color.equals("green")){
            return R.color.colorGreen;
        }
        else if(color.equals("yellow")){
            return R.color.colorYellow;
        }
        else if(color.equals("violet")){
            return R.color.colorViolet;
        }
        else return 0;
    }

    public String getColor(){
        return color;
    }
}
