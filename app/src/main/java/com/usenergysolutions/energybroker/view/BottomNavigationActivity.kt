package com.usenergysolutions.energybroker.view

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.usenergysolutions.energybroker.R

class BottomNavigationActivity : AppCompatActivity() {
    private val TAG: String = "BottomNavigationAct"

    // UI Components
    lateinit var toolbar: ActionBar

    // Helpers
    private lateinit var contex: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        Log.d(TAG, "onCreate")

        contex = this

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

        bottomNavigation.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navCRM -> {
                        Toast.makeText(contex, "CRM Pressed", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.navProfile -> {
                        Toast.makeText(contex, "Profile Pressed", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.navNews -> {
                        Toast.makeText(contex, "News Pressed", Toast.LENGTH_SHORT).show()
                        return true
                    }
                }
                return false
            }
        })
    }
}