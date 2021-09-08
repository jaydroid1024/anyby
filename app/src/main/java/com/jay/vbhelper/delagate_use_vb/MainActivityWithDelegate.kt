package com.jay.vbhelper.delagate_use_vb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.jay.vbhelper.R
import com.jay.vbhelper.databinding.ActivityMainBinding
import com.jay.vbhelper.delegate.vb

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class MainActivityWithDelegate : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val binding: ActivityMainBinding by vb()

    private val binding2: ActivityMainBinding by vb(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding2.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}