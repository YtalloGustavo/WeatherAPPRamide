package com.example.weatherapp_ramide

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp_ramide.ui.components.DataField
import com.example.weatherapp_ramide.ui.components.PasswordField
import com.example.weatherapp_ramide.ui.theme.WeatherAPPRamideTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAPPRamideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val activity = LocalActivity.current as? Activity
    val context = LocalContext.current
    val fieldModifier = Modifier.fillMaxWidth(0.9f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Criar conta",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(12.dp))

        DataField(
            value = name,
            label = "Digite seu nome",
            modifier = fieldModifier,
            onValueChange = { name = it }
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

        Spacer(modifier = Modifier.size(12.dp))

        PasswordField(
            value = confirmPassword,
            label = "Repita sua senha",
            modifier = fieldModifier,
            onValueChange = { confirmPassword = it }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = fieldModifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (FirebaseApp.getApps(context).isEmpty()) {
                        Toast.makeText(
                            context,
                            "Configure o Firebase antes de registrar.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    activity?.let {
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(it) { task ->
                                val message = if (task.isSuccessful) {
                                    "Registro OK!"
                                } else {
                                    "Registro FALHOU!"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                    }
                },
                enabled = activity != null &&
                    name.isNotEmpty() &&
                    email.isNotEmpty() &&
                    password.isNotEmpty() &&
                    confirmPassword.isNotEmpty() &&
                    password == confirmPassword
            ) {
                Text("Registrar")
            }

            Button(
                onClick = {
                    name = ""
                    email = ""
                    password = ""
                    confirmPassword = ""
                }
            ) {
                Text("Limpar")
            }
        }
    }
}
