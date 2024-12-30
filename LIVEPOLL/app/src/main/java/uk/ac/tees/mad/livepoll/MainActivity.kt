package uk.ac.tees.mad.livepoll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.livepoll.domain.workmanager.schedulePollStatusUpdate
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.ui.theme.LivePollTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule the WorkManager task to check and update poll statuses
        schedulePollStatusUpdate(this)

        setContent {
            LivePollTheme {
                ApplicationNavigation()
            }
        }
    }
}

