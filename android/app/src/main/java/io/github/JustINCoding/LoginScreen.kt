package io.github.JustINCoding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class LoginScreen: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           ActivitySettings()
        }
    }

    @Composable
    fun ActivitySettings() {
        Image(
            painter = painterResource(id = R.drawable.green_grid),
            contentDescription = "",
            modifier = Modifier
                       .fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )
        Column(modifier = Modifier.padding(top = 150.dp)) {
            Text(text = "TensorFlow", color = Color.Blue, fontSize = 50.sp,fontWeight = FontWeight.Bold
                    , fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center ,modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White)
                    .padding(16.dp))
            Button(onClick = { onStartClick() }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue
            ),modifier = Modifier
                    .padding(top = 50.dp)
                    .width(240.dp)
                    .height(80.dp)
                    .align(Alignment.CenterHorizontally)){
                  Text("See in Action!", fontSize = 30.sp, color = Color.White)
            }
            Button(onClick = { onAboutClick() }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue
            ), modifier = Modifier
                    .padding(top = 30.dp)
                    .width(240.dp)
                    .height(80.dp)
                    .align(Alignment.CenterHorizontally)){
                Text("About", fontSize = 30.sp, color = Color.White)
            }
            Image(
                    painter = painterResource(id = R.drawable.tf_logo),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                            .width(250.dp)
                            .height(200.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 80.dp)
            )
            
        }

    }
    

    
    private fun onStartClick(){}

    private fun onAboutClick(){}

    @Composable
    @Preview(showBackground = true)
    fun ComposeMethod(){
        ActivitySettings()
    }
}