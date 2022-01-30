package io.github.JustINCodingUK.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.JustINCoding.R


class AboutActivity: ComponentActivity() {
    private val customText: TextStyle = TextStyle(
            color = Color.Black,
            fontSize = 28.sp,
            textAlign = TextAlign.Left,
            fontFamily = FontFamily.SansSerif
    )

    private val padding: Modifier = Modifier.padding(10.dp)

    private val githubRepo: AnnotatedString = buildAnnotatedString {
        append("To view the source code, visit ")
        pushStringAnnotation(tag = "github", annotation = "https://github.com/JustINCodingUK/TensorFlowExample")
        withStyle(style = SpanStyle(color = Color.Blue)){
            append("github repository")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            createAboutPage()
        }
    }

    @Composable
    fun createAboutPage(){
        Box(modifier = Modifier
                .background(Color.White)
                .fillMaxSize())

        Column {
            Text(text = "Fruits Classifier", color = Color.Black, fontSize = 45.sp,
                    fontStyle = FontStyle.Italic, textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center, modifier = Modifier.width(400.dp)
            )
            Text(text = "Author: JustINCodingUK", fontSize = 33.sp, modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterHorizontally))
            Image(painter = painterResource(id = R.drawable.pixel),
                  contentDescription = "",
                  modifier = Modifier
                          .clip(CircleShape)
                          .height(100.dp)
                          .align(Alignment.CenterHorizontally)
            )

            Text(text = "Instructions to use:", style = customText, modifier = padding)
            Text(text = "   1. After clicking on \'See in Action\', click a photo of the fruit", style = customText, modifier = padding)
            Text(text = "   2. You'll see the results screen. To get a detailed report, click on \'More Info\'", style = customText, modifier = padding)
            Text(text = "\n\n"+githubRepo, style = customText, modifier = padding)
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun previewAboutPage(){
        createAboutPage()
    }


}


