package com.example.jiumoon.yamsterdam;

public class Event {

    //private variables
    int id;
    String name;
    String date;
    String description;
    String address;

    // Empty constructor
    public Event(){

    }
    // constructor
    public Event(int id, String name, String date, String description, String address){
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.address = address;
    }

    // constructor
    public Event(String name, String date, String description, String address){
        this.name = name;
        this.date = date;
        this.description=description;
        this.address = address;

    }
    // getting ID
    public int getID(){
        return this.id;
    }

    // setting id
    public void setID(int id){
        this.id = id;
    }

    // getting name
    public String getName(){
        return this.name;
    }

    // setting name
    public void setName(String name){
        this.name = name;
    }

    // getting date
    public String getDate(){
        return this.date;
    }

    // setting date
    public void setDate(String date){
        this.date = date;
    }

    // getting description
    public String getDescription(){
        return this.description;
    }

    // setting description
    public void setDescription(String description){
        this.description = description;
    }
    // getting address
    public String getAddress(){
        return this.address;
    }

    // setting address
    public void setAddress(String address){
        this.address = address;
    }
}
