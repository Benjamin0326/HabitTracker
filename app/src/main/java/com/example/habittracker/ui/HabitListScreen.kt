package com.example.habittracker.ui

import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.model.Habit
import com.example.habittracker.viewmodel.HabitViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items




@Composable
fun HabitListScreen(
    viewModel: HabitViewModel = viewModel()
) {
    val habitsState = viewModel.allHabits.collectAsState(initial = emptyList())
    val habits = habitsState.value
    var showDialog by remember { mutableStateOf(false)}
    var selectedHabit by remember { mutableStateOf<Habit?>(null) }

    var newHabitText by remember { mutableStateOf("")}
    var modifiedHabitText by remember { mutableStateOf("")}

    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text(
            text = "\uD83C\uDF3F 나의 습관 목록",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newHabitText,
            onValueChange = { newHabitText = it },
            label = { Text("새 습관 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    if (newHabitText.isNotBlank()) {
                        viewModel.insert(Habit(name = newHabitText))
                        newHabitText = ""
                    }
                },
            ) {
                Text("추가하기")
            }

            Button(
                onClick = {
                    showDialog = true
                },
            ) {
                Text("삭제하기")
            }
        }

        DeleteConfirmationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = {
                val completedHabits = habits.filter { it.isCompleted }
                for (completedHabit in completedHabits) {
                    viewModel.delete(completedHabit)
                }
                showDialog = false
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(habits, key = { it.id }) { habit ->
                HabitItem(
                    habit = habit,
                    onToggle = {
                        viewModel.update(habit.copy(isCompleted = !habit.isCompleted))
                    },
                    onTextClick = {
                        selectedHabit = habit
                    }
                )
            }
        }

        selectedHabit?.let { habit ->
            AlertDialog(
                onDismissRequest = {
                    selectedHabit = null
                    modifiedHabitText = ""},
                title = { Text("수정하기") },
                text = {
                    OutlinedTextField(
                        value = modifiedHabitText,
                        onValueChange = { modifiedHabitText = it },
                        label = { Text("수정할 습관 입력") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (modifiedHabitText.isNotBlank()) {
                            viewModel.update(Habit(
                                id = selectedHabit!!.id,
                                name = modifiedHabitText,
                                isCompleted = selectedHabit!!.isCompleted
                            ))
                            modifiedHabitText = ""
                        }
                        selectedHabit = null
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedHabit = null
                        modifiedHabitText = ""
                    }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit,
    onTextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = habit.name,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onTextClick()
                }
        )
        Checkbox(
            checked = habit.isCompleted,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("정말 삭제하시겠어요?") },
            text = { Text("선택된 습관이 모두 삭제됩니다.")},
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        )
    }
}