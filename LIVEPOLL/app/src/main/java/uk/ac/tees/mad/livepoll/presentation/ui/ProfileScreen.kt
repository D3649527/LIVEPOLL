import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.tees.mad.livepoll.navigateWithBackStack
import uk.ac.tees.mad.livepoll.navigateWithoutBackStack
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel
import java.io.File

@Composable
fun ProfileScreen(viewModel: PollViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val isLoading = viewModel.isLoading

    var editableName by remember { mutableStateOf(viewModel.user.value?.name ?: "") }
    var editableEmail by remember { mutableStateOf(viewModel.user.value?.email ?: "") }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.uploadProfile(context, imageUri.value!!)
            Toast.makeText(context, "Image captured successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.getExternalFilesDir(null), "UserPictures/profile_image.jpg")
            file.parentFile?.mkdirs()
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            imageUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    viewModel.fetchUserData()
    val user by viewModel.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile", style = MaterialTheme.typography.h6) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardDoubleArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }, modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (user!!.profileImage.isNotEmpty()){
                AsyncImage(model = user!!.profileImage, contentDescription = null, contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp))
            }
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user?.profileImage?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user!!.profileImage,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(75.dp))
                            .clickable {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Profile Placeholder",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(75.dp))
                            .clickable {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable fields for Name, Email, and Phone
                OutlinedTextField(
                    value = editableName,
                    onValueChange = { editableName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editableEmail,
                    onValueChange = { editableEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateUserData(context,editableName, editableEmail)
                        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navigateWithBackStack(navController, ApplicationNavigation.Offline.route)
                              },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary)
                ) {
                    Text("User created polls")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.logOut()
                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                        navigateWithoutBackStack(navController, ApplicationNavigation.Login.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                ) {
                    Text("Log Out")
                }
            }
            if (isLoading.value){
                Box(modifier = Modifier.fillMaxSize()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
