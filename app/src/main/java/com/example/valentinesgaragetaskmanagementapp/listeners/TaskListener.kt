package com.example.valentinesgaragetaskmanagementapp.listeners

import com.example.valentinesgaragetaskmanagementapp.models.Task

interface TaskListener {
    fun onTaskClicked(task: Task)
}