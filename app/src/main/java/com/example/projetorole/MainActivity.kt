package com.example.projetorole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.data.repository.CheckinsSalvosRepository
import com.example.projetorole.navigation.NavGraph
import com.example.projetorole.ui.theme.AppTheme
import com.example.projetorole.ui.theme.GradientBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthRepository.init(applicationContext)
        CheckinsSalvosRepository.init(applicationContext)

        setContent {
            AppTheme {
                GradientBackground {
                    NavGraph()
                }
            }
        }
    }
}