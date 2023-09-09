package com.erendogan.havadis_v2.view

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.databinding.ActivityNavigasyonBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Navigasyon : AppCompatActivity() {
    private lateinit var binding: ActivityNavigasyonBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigasyonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth= Firebase.auth
        if (auth.currentUser==null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navigasyon)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_zamanTuneli, R.id.navigation_paylasim, R.id.navigation_profil))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}