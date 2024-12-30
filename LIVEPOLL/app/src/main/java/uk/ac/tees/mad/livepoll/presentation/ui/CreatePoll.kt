import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import uk.ac.tees.mad.livepoll.navigateWithoutBackStack
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePoll(viewModel: PollViewModel, navController : NavController) {
    val isLoading = viewModel.isLoading
    var question by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<TimePickerState?>(null) }
    var selectedDate by remember { mutableStateOf<DatePickerState?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }  // For validation error messages

    val context = LocalContext.current
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    if (showTimePicker) {
        TimePicker(onConfirm = { time ->
            selectedTime = time
            showTimePicker = false
        }, onDismiss = { showTimePicker = false })
    }

    if (showDatePicker) {
        DatePicker(onConfirm = { date ->
            selectedDate = date
            showDatePicker = false
        }, onDismiss = { showDatePicker = false })
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(text = "Create Poll", fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(20.dp))

            PollInputField(
                label = "Poll Question:",
                value = question,
                onValueChange = { question = it })
            Spacer(modifier = Modifier.height(20.dp))
            PollInputField(label = "Option 1", value = option1, onValueChange = { option1 = it })
            Spacer(modifier = Modifier.height(20.dp))
            PollInputField(label = "Option 2", value = option2, onValueChange = { option2 = it })
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateTimePicker(
                    label = "Pick Time",
                    selectedValue = selectedTime?.let {
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, it.hour)
                            set(Calendar.MINUTE, it.minute)
                        }.let { cal -> timeFormatter.format(cal.time) }
                    } ?: "No time selected.",
                    onClick = { showTimePicker = true }
                )

                DateTimePicker(
                    label = "Pick Date",
                    selectedValue = selectedDate?.selectedDateMillis?.let { millis ->
                        dateFormatter.format(Date(millis))
                    } ?: "No date selected.",
                    onClick = { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red)
                Spacer(modifier = Modifier.height(10.dp))
            }

            ActionButton(text = "CREATE POLL", onClick = {
                viewModel.createPoll(
                    question = question,
                    option1 = option1,
                    option2 = option2,
                    selectedDateMillis = selectedDate?.selectedDateMillis,
                    selectedTime = selectedTime,
                    onValidationError = { error ->
                        errorMessage = error
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    },
                    onSuccess = {
                        errorMessage = null
                        navigateWithoutBackStack(navController, ApplicationNavigation.Poll.route)
                    }
                )
            })
            Spacer(modifier = Modifier.height(10.dp))
            ActionButton(text = "HOME", onClick = { /*TODO: Navigate home*/ })
        }
    }
    if(isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.CircularProgressIndicator()
        }
    }
}

@Composable
fun PollInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Write your $label") }
        )
    }
}

@Composable
fun DateTimePicker(label: String, selectedValue: String, onClick: () -> Unit) {
    Column {
        Text(text = selectedValue)
        Button(onClick = onClick, shape = RoundedCornerShape(5.dp)) {
            Text(text = label)
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(Color.Black),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(timePickerState) }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onConfirm: (DatePickerState) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = Calendar.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime.timeInMillis
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    DatePicker(state = datePickerState)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { onConfirm(datePickerState) }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}