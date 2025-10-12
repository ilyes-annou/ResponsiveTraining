package com.example.testproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController

import com.example.testproject.ui.theme.TestProjectTheme
import com.example.testproject.view.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //Setting up navigation between different screens
            val navController = rememberNavController()
            TestProjectTheme {
                NavGraph(navController = navController)
            }
        }
    }
}
