package com.example.pokemongame

import android.location.Location

class NewLocations {

    var name:String?=null
    var desc:String?=null
    var speciality:String?=null
    var lati:Double?=null
    var longi:Double?=null
    var isVisited:Boolean?=false
    var placeLocation:Location?=null

    constructor(name:String,desc:String,speciality:String,lati:Double,longi:Double){
        this.name=name
        this.desc=desc
        this.speciality=speciality
        this.placeLocation=Location(name)
        placeLocation!!.latitude=lati
        placeLocation!!.longitude=longi
        this.isVisited=false
    }
}