package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignUp(navController: NavController) {
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.height(200.dp))
        Text(text = "Register", fontSize = 50.sp)
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(value = email.value, onValueChange = {email.value = it}, label = { Text(text = "Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(value = password.value, onValueChange = {password.value = it}, label = { Text(
            text = "Password"
        )
        },  modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),colors = ButtonDefaults.buttonColors(
                Color.Black)
        ) {
            Text(text = "LOG IN")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Enter your email and password to begin using the app")
    }
}