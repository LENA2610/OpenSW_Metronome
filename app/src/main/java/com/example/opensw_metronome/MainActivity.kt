package com.example.opensw_metronome

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opensw_metronome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var NavControl_Class: NavControl_Class
    lateinit var SetControl_Class: SetControl_Class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 하단 네비바 컨트롤러
        val navView : BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val inflater = navController.navInflater
        val navGraph = inflater.inflate(R.navigation.mobile_navigation)


        // 환경설정 버튼
        val showSet : Boolean = false
        val setView : ImageButton = findViewById(R.id.btnSettings)
        SetControl_Class = SetControl_Class(setView)
        if (showSet) setView.visibility = View.VISIBLE
        else setView.visibility = View.GONE

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            findNavController(R.id.nav_host_fragment_activity_main)
                .navigate(R.id.navigation_Settings)
            SetControl_Class.hide()
        }


        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isItFirst = prefs.getBoolean("isItFirst", true)

        if (isItFirst) {
            navGraph.setStartDestination(R.id.navigation_Tutorial)
        } else {
            navGraph.setStartDestination(R.id.navigation_Metronome)
        }
        navController.graph = navGraph

        NavControl_Class = NavControl_Class(navView)
        if (isItFirst) {
            NavControl_Class.hide()
            SetControl_Class.hide()
        } else {
            NavControl_Class.show()
            SetControl_Class.show()
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_Tutorial, R.id.navigation_Lesson,
                R.id.navigation_Metronome, R.id.navigation_PracticeLog,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnItemReselectedListener { menuItem ->
            if (menuItem.itemId == R.id.navigation_Lesson) {
                navController.popBackStack(R.id.navigation_Lesson, false)
            }
        }
    }
}

// 하단 네비게이션 바 show 여부 컨트롤
class NavControl_Class(private val navView: BottomNavigationView) {
    fun show() {
        navView.visibility = View.VISIBLE
    }
    fun hide() {
        navView.visibility = View.GONE
    }
}
// 설정버튼
class SetControl_Class(private val setView : ImageButton) {
    fun show() {
        setView.visibility = View.VISIBLE
    }
    fun hide() {
        setView.visibility = View.GONE
    }
}