package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.livepoll.navigateWithoutBackStack
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@Composable
fun SignUp(vm : PollViewModel, navController: NavController) {
    val context = LocalContext.current
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = vm.isLoading.value
    val isLoggedIn = vm.isLoggedIn.value
    if(isLoggedIn){
        navigateWithoutBackStack(navController, ApplicationNavigation.Poll.route)
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
        }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
           trailingIcon =   {
               val image = if (passwordVisible)
                   Icons.Filled.Visibility
               else
                   Icons.Filled.VisibilityOff

               IconButton(onClick = {
                   passwordVisible = !passwordVisible
               }) {
                   Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
               }
           },  modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { vm.signUp(context, email.value, password.value) }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),colors = ButtonDefaults.buttonColors(
                Color.Black)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "LOG IN")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Enter your email and password to begin using the app")
    }
}