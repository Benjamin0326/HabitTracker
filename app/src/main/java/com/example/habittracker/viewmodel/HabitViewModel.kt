package com.example.habittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val habitDao = HabitDatabase.getDatabase(application).habitDao()

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    fun insert(habit: Habit) = viewModelScope.launch {
        habitDao.insert(habit)
    }

    fun update(habit: Habit) = viewModelScope.launch {
        habitDao.update(habit)
    }

    fun delete(habit: Habit) = viewModelScope.launch {
        habitDao.delete(habit)
    }
}
