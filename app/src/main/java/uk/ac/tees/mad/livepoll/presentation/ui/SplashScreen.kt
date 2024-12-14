package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.livepoll.R
import uk.ac.tees.mad.livepoll.navigateWithoutBackStack
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2500)
        navigateWithoutBackStack(navController, ApplicationNavigation.Login.route)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.designer), contentDescription = "Logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape))
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "LivePoll", fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
    }
}