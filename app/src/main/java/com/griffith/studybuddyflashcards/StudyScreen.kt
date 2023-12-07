// StudyScreen.kt
package com.griffith.studybuddyflashcards

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File


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
        viewModel.getFlashcardsBySubjectId(subjectId)
    }

    val context = LocalContext.current

    val state by viewModel.stateFlashcard.collectAsState()
    val subjectDetails = viewModel.getSubjectDetails(subjectId)

    // Fetch the flashcards whenever the state changes
    val flashcardList = remember(subjectId, state.flashcards) {
        viewModel.getFlashcardsBySubjectId(subjectId)
    }

    Log.d("StudyScreen", "Number of flashcards: ${flashcardList.size}")

    Scaffold(

        topBar = {
            TopAppBar(
                title = { },
                actions = {

                    Spacer(modifier = Modifier.weight(1f))

                    // IconButton for navigating back to the main screen
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close Study Screen"
                        )
                    }

                    // Your custom content for the app bar
                    Image(
                        painter = painterResource(id = R.drawable.header),  // Replace with your image resource
                        contentDescription = "Header Image",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                },
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

        modifier = Modifier.background(Color.Transparent),

        ) { _ ->

        // Use a Box to layer the content and the background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Set a default background color

            // Content of the Box
            contentAlignment = Alignment.Center,
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.background2), // Replace with your image resource
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .statusBarsPadding()
            )

            if (state.isAddingFlashcard) {
                // Pass subjectId to AddFlashcardDialog
                AddFlashcardDialog(
                    subjectId = subjectId ?: -1,
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    if (subjectDetails != null) {
                        Text(
                            "${subjectDetails.subjectName} Flashcards",
                            fontSize = 25.sp
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (state.flashcards.isNotEmpty()) {
                            val currentFlashcardIndex = state.currentFlashcardIndex
                            val audioFile =
                                flashcardList.getOrNull(currentFlashcardIndex)?.audioFilePath
                            Flashcard(
                                flashcards = flashcardList,
                                currentFlashcardIndex = currentFlashcardIndex,
                                state = state,
                                onNavigateToPrevious = { onEvent(AppEvent.NavigateToPreviousFlashcard) },
                                onNavigateToNext = { onEvent(AppEvent.NavigateToNextFlashcard) },
                                audioFilePath = audioFile,
                                onPlayAudio = { file ->
                                    val playAudioEvent = AppEvent.PlayAudio(file = file)
                                    viewModel.onEvent(playAudioEvent)
                                },
                                onStopAudio = { viewModel.onEvent(AppEvent.StopAudio) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                onEvent = viewModel::onEvent
                            )
                        } else {
                            Text(
                                text = "No flashcards found for this Subject. Click on the '+' sign and create one now!",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Test your knowledge!",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight(0.6f)
                            .height(IntrinsicSize.Max),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.DarkGray
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {

//                                showToast(context, "Clicked on Quiz")

                                    if (flashcardList.size < 2) {
                                        showToast(context, "Not enough flashcards to start quiz")
                                        return@clickable
                                    }

                                    navController.navigate("quiz_screen/${subjectId}")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Quiz",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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
    audioFilePath: String?,
    onPlayAudio: (File) -> Unit,
    onStopAudio: () -> Unit,
    modifier: Modifier = Modifier,
    onEvent: (AppEvent) -> Unit
) {
    var isFrontVisible by remember { mutableStateOf(true) }

    Column {

        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFrontVisible) Color.DarkGray else Color.Gray,
                contentColor = if (isFrontVisible) Color.White else Color.Black,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isFrontVisible = !isFrontVisible },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Adjusting currentFlashcardIndex when it exceeds the bounds of the list
                    val adjustedIndex = if (flashcards.isNotEmpty()) {
                        currentFlashcardIndex.coerceIn(0, flashcards.size - 1)
                    } else {
                        0
                    }

                    val flashcard = flashcards.getOrNull(adjustedIndex)

                    IconButton(onClick = {
                        onNavigateToPrevious()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate to Previous Flashcard"
                        )
                    }

                    Text(
                        text = if (isFrontVisible) flashcard?.front.orEmpty() else flashcard?.back.orEmpty(),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )

                    IconButton(onClick = {
                        onNavigateToNext()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Navigate to Next Flashcard"
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isFrontVisible && audioFilePath != null) {

                        //Button for playing/stopping audio
                        IconButton(onClick = {
                            if (state.isPlayingAudio) onStopAudio() else onPlayAudio(File(audioFilePath ?: ""))
                        }) {
                            Icon(
                                imageVector = if (state.isPlayingAudio) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = if (state.isPlayingAudio) "Stop Audio" else "Play Audio"
                            )
                        }
                    }
                }
            }
        }

//        // Display this Text block when there are no flashcards
//        if (flashcards.isEmpty()) {
//            Text(
//                text = "No flashcards found for this Subject. Click on the '+' sign and create one now!",
//                modifier = Modifier
//                    .fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                onEvent(AppEvent.DeleteFlashcard(state.flashcards[currentFlashcardIndex]))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete flashcard"
                )
            }
        }
    }
}
