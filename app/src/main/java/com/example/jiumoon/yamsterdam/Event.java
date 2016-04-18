package com.example.jiumoon.yamsterdam;

public class Event {

    //private variables
    int _id;
    String _name;
    String _date;
    String _description;

    // Empty constructor
    public Event(){

    }
    // constructor
    public Event(int id, String name, String _date, String _description){
        this._id = id;
        this._name = name;
        this._date = _date;
        this._description = _description;
    }

    // constructor
    public Event(String name, String _date, String _description){
        this._name = name;
        this._date = _date;
        this._description=_description;

    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting date
    public String getDate(){
        return this._date;
    }

    // setting date
    public void setDate(String date){
        this._date = date;
    }

    // getting description
    public String getDescription(){
        return this._description;
    }

    // setting description
    public void setDescription(String description){
        this._description = description;
    }

}
