package com.example.weatherapp_ramide

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp_ramide.ui.components.DataField
import com.example.weatherapp_ramide.ui.components.PasswordField
import com.example.weatherapp_ramide.ui.theme.WeatherAPPRamideTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAPPRamideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val activity = LocalActivity.current as Activity
    val fieldModifier = Modifier.fillMaxWidth(0.9f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(12.dp))

        DataField(
            value = email,
            label = "Digite seu e-mail",
            modifier = fieldModifier,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.size(12.dp))

        PasswordField(
            value = password,
            label = "Digite sua senha",
            modifier = fieldModifier,
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = fieldModifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    Toast.makeText(activity, "Login OK!", Toast.LENGTH_LONG).show()
                    activity.startActivity(
                        Intent(activity, MainActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                        )
                    )
                },
                enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login")
            }

            Button(
                onClick = {
                    email = ""
                    password = ""
                }
            ) {
                Text("Limpar")
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = {
                activity.startActivity(Intent(activity, RegisterActivity::class.java))
            }
        ) {
            Text("Criar conta")
        }
    }
}
