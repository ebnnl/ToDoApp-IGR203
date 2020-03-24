package com.example.todolist;

public class Date {

    // Classe représentant une date
    int day;
    int month;
    int year;

    public Date(int ddmmaaaa){
        this.day = ddmmaaaa/1000000;
        int mmaaaa = ddmmaaaa - 1000000*this.day;
        this.month = mmaaaa/10000;
        this.year = mmaaaa - 10000*month;
    }

    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getInt(){
        return day*1000000+month*10000+year;
    }
}
