package io.github.JustINCoding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class LoginScreen: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Android Compose")
        }
    }

    @Composable
    @Preview()
    fun ComposeMethod(){
        Text("This is compose")
    }
}