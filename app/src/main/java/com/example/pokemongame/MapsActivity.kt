package com.example.pokemongame


import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pokemongame.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadLocation()
    }

    var ACCESSLOCATION = 123 //request code. Value is random
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=22 ){
            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return;
            }
        }
        getUserLocation()

    }
    fun getUserLocation(){
        Toast.makeText(this,"User Location Access On",Toast.LENGTH_LONG).show()

        var myLocation = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var threadAct = myThread()
        threadAct.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                     getUserLocation()
                }
                else{
                    Toast.makeText(this,"We cannot access your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    // Get user location

    var location:Location? = null //location of Player
    var oldLocation:Location? = null

    inner class MyLocationListener: LocationListener{


        constructor(){
            location= Location("Start")
            location!!.latitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location) {
            location=p0
        }

    }



    inner class myThread:Thread{

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.latitude=0.0
            oldLocation!!.longitude=0.0

        }

        override fun run(){

            while(true){

                try{
                    if(oldLocation!!.distanceTo(location)==0f){ // this is done to avoid putting markers on every iteration and fixing the screen at one place
                        continue
                    }

                    oldLocation=location

                    runOnUiThread() {

                        mMap!!.clear()

                        // show me
                        val myPlayer = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(myPlayer)
                                .title("Me")
                                .snippet("Here is my location")
                                .flat(true)
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlayer, 13f))

                        // show locations
                        for(i in 0..listOfLocations.size-1){
                            var newPlace = listOfLocations[i]

                            if(newPlace.isVisited==false){
                                val placeMarker = LatLng(newPlace.placeLocation!!.latitude,newPlace.placeLocation!!.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(placeMarker)
                                        .title(newPlace.name!!)
                                        .snippet(newPlace.desc!!+"\nSpeciality:"+newPlace.speciality)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                )

                                if(location!!.distanceTo(newPlace.placeLocation)<10){
                                    newPlace.isVisited=true
                                    listOfLocations[i]=newPlace
                                    Toast.makeText(applicationContext,"You visited this place",Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                    }
                    Thread.sleep(1000)

                }
                catch (ex:java.lang.Exception){

                }
            }
        }
    }

    var listOfLocations = ArrayList<NewLocations>()

    fun loadLocation(){
        listOfLocations.add(NewLocations("AD Mall","A mall near Vijay Chowk","Has a Hotel above it",26.7563,83.3666))
        listOfLocations.add(NewLocations("City Mall","A mall near Civil Lines","Has a BBQ Nation",26.7536, 83.3752))
        listOfLocations.add(NewLocations("Orion Mall","A mall in Mohaddipur","Has Subway Store",26.75698,83.39915))
    }



}