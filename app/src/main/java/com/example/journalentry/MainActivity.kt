package com.example.journalentry

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.journalentry.database.VisualizeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission()
        }

        val homeFrag = MenuActivity()
        val logsFrag = LogActivity()
        val visualFrag = VisualizeActivity()

        makeCurrentFragment(homeFrag)

        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_bar)
        bottomNav.setOnNavigationItemSelectedListener {
             when(it.itemId) {
                 R.id.nav_home -> makeCurrentFragment(homeFrag)
                 R.id.nav_logs -> makeCurrentFragment(logsFrag)
                 R.id.nav_visualize -> makeCurrentFragment(visualFrag)
             }
            true
        }

    }


    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_fragment, fragment)
            commit()
        }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), 0);
    }

}


