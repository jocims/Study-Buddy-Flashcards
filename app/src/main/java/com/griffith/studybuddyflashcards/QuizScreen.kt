package com.griffith.studybuddyflashcards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    subjectId: Int,
    viewModel: SubjectViewModel,
    navController: NavController,
    onEvent: (AppEvent) -> Unit
) {
    // Fetch flashcards for the given subject
    val flashcards = viewModel.getFlashcardsBySubjectId(subjectId)
    val totalFlashcards = flashcards.size

    // MutableState variables to track the current state of the quiz
    var currentFlashcardIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var allFlashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }

    // Shuffle the flashcards and separate into matching and random sets
    val flashcardShuffled = flashcards.shuffled()
    val flashcardsToMatch = flashcardShuffled.take(totalFlashcards / 2).shuffled()
    val flashcardsRandom = flashcardShuffled.drop(totalFlashcards / 2).shuffled()

    allFlashcards = (flashcardsToMatch + flashcardsRandom).shuffled()

    // Scaffold provides a basic layout structure for the screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Title placeholder */ },
                actions = {
                    Spacer(modifier = Modifier.weight(1f))

                    // Close button to navigate back to the main screen
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close Study Screen"
                        )
                    }

                    // Custom content for the app bar (Header Image)
                    Image(
                        painter = painterResource(id = R.drawable.header),
                        contentDescription = "Header Image",
                        modifier = Modifier.fillMaxSize()
                    )
                },
            )
        },
        content = { _ ->

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
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .statusBarsPadding()
                )

                // Main Column for the quiz content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentFlashcardIndex < totalFlashcards) {

                        // Get the front and back flashcards for the current index
                        val frontFlashcard = allFlashcards[currentFlashcardIndex]
                        var backFlashcard = Flashcard(0, "", "", 0)

                        // Check if the front flashcard exists in flashcardsToMatch
                        if (frontFlashcard in flashcardsToMatch) {
                            // Display front flashcard
                            backFlashcard = allFlashcards[(currentFlashcardIndex) % totalFlashcards]
                        } else {
                            // Display back flashcard
                            backFlashcard =
                                allFlashcards[(currentFlashcardIndex + 1) % totalFlashcards]
                        }

                        // Front and Back Flashcards Side by Side
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Front Flashcard
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Front",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                )

                                // Card displaying the front flashcard
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                        .height(200.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(android.graphics.Color.parseColor("#FCEFCA")),
                                        contentColor = Color.Black,
                                    )
                                ) {
                                    Text(
                                        text = frontFlashcard.front,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            // Back Flashcard
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Back",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                )
                                // Card displaying the back flashcard
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                        .height(200.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(android.graphics.Color.parseColor("#FBDFE4")),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = backFlashcard.back,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }

                        // Question: Do these match?
                        Text(
                            text = "Do these match?",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )

                        // Yes and No Cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Yes Card
                            Card(
                                modifier = Modifier
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#52BF48")),
                                    contentColor = Color.White
                                ),
                            ) {
                                IconButton(onClick = {
                                    // Handle Yes click
                                    if (frontFlashcard.id == backFlashcard.id) {
                                        correctCount++
                                    }
                                    currentFlashcardIndex += 1 // Move to the next set of flashcards
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Yes",
                                        modifier = Modifier
                                            .size(40.dp), // Adjust the size as needed
                                    )
                                }
                            }

                            // No Card
                            Card(
                                modifier = Modifier
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#EF2E2B")),
                                    contentColor = Color.White
                                )
                            ) {
                                IconButton(onClick = {
                                    // Handle No click
                                    if (frontFlashcard.id != backFlashcard.id) {
                                        correctCount++
                                    }
                                    currentFlashcardIndex += 1 // Move to the next set of flashcards
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "No",
                                        modifier = Modifier
                                            .size(40.dp) // Adjust the size as needed
                                    )
                                }
                            }
                        }
                    } else {
                        // Display total correct percentage
                        val totalQuestions = totalFlashcards // Each flashcard set has 2 questions
                        val percentage = if (totalQuestions > 0) {
                            (correctCount.toFloat() / totalQuestions.toFloat() * 100).coerceAtMost(
                                100f
                            )
                        } else {
                            0f
                        }

                        Text(
                            text = "Quiz completed! Total Correct: ${percentage.toInt()}%",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    )
}