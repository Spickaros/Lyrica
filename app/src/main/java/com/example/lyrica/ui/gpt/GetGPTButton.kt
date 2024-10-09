package com.example.lyrica.ui.gpt

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun GetGPTButtonPreview() {
    GetGPTButton()
}

annotation class Preview

@Composable
fun GetGPTButton(getGPTResponse: (() -> Unit)? = null) {
    Button(
        onClick = {
            getGPTResponse?.invoke()
        }
    ) {
        Text(text = "Get GPT Response")
    }
}