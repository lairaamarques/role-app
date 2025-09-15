package com.example.projetorole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.projetorole.ui.theme.AppTheme
import com.example.projetorole.ui.theme.GradientBackground
import com.example.projetorole.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                GradientBackground {
                    NavGraph()
                }
            }
        }
    }
}