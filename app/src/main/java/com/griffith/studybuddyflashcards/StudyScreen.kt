package com.griffith.studybuddyflashcards

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.griffith.studybuddyflashcards.SubjectViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    subjectId: Int,
    viewModel: SubjectViewModel,
    navController: NavController,
    onEvent: (AppEvent) -> Unit
) {

    // Call updateFlashcardState when StudyScreen is created
    LaunchedEffect(Unit) {
        viewModel.updateFlashcardState(subjectId)
    }

    val state by viewModel.stateFlashcard.collectAsState()
   val subjectDetails = viewModel.getSubjectDetails(subjectId)

    val flashcardList = viewModel.getFlashcardsBySubjectId(subjectId)

//    Log.d("StudyScreen", "Number of flashcards: ${flashcards.size}")


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    subjectDetails?.subjectName ?: "Unknown",
                ) },
                actions = {
                    // IconButton for navigating back to the main screen
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close Study Screen"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(AppEvent.ShowFlashcardDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Flashcard"
                )
            }
        },
        modifier = Modifier.padding(bottom = 16.dp)
    ) { _ ->

        if (state.isAddingFlashcard) {
            // Pass subjectId to AddFlashcardDialog
            AddFlashcardDialog(subjectId = subjectId ?: -1, state = state, onEvent = viewModel::onEvent)
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .navigationBarsPadding(),  // Use this line instead
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display subject details
//            Text(
//                "Subject ID: $subjectId",
//                fontSize = 20.sp)
            Text(
                "Subject Name: ${subjectDetails?.subjectName ?: "Unknown"}",
                fontSize = 20.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (state.flashcards.isNotEmpty()) {
                    Flashcard(
                        flashcards = flashcardList,
                        currentFlashcardIndex = state.currentFlashcardIndex,
                        state = state,
                        onNavigateToPrevious = { onEvent(AppEvent.NavigateToPreviousFlashcard) },
                        onNavigateToNext = { onEvent(AppEvent.NavigateToNextFlashcard) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                } else {
                    Text("No flashcards available.")
                }
            }
        }
    }
}

@Composable
fun Flashcard(
    flashcards: List<Flashcard>,
    currentFlashcardIndex: Int,
    state: FlashcardState,
    onNavigateToPrevious: () -> Unit,
    onNavigateToNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFrontVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isFrontVisible = !isFrontVisible },
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val flashcard = flashcards.getOrNull(currentFlashcardIndex)

                if (flashcard != null) {
                    IconButton(onClick = onNavigateToPrevious) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate to Previous Flashcard"
                        )
                    }

                    Text(
                        text = if (isFrontVisible) flashcard.front else flashcard.back,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )

                    IconButton(onClick = onNavigateToNext) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Navigate to Next Flashcard"
                        )
                    }
                } else {
                    Text("No flashcard found for subjectId and index ${state.currentFlashcardIndex}")
                }
            }
        }
    }
}