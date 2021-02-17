package com.skul.testappgl.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skul.testappgl.data.Task
import com.skul.testappgl.data.UiItemTask
import com.skul.testappgl.network.*

class TasksRepository {

    private val _remoteResult = MutableLiveData<NetworkResult<List<Task>>>()
    val remoteResult: LiveData<NetworkResult<List<Task>>>
        get() = _remoteResult

    private val _localTasks = MutableLiveData<List<Task>>()
    val localTasks: LiveData<List<Task>>
        get() = _localTasks


    init {
        _localTasks.value = listOf()
    }

    suspend fun refreshRemoteTasks() {
        _remoteResult.value = Loading
        try {
            val tasksList = TasksApi.retrofitService.getRemoteTasks()
            val successfulResult = SuccessfulResult(tasksList)
            _remoteResult.value = successfulResult
        } catch (e: Exception) {
            val failResult = NetworkError(e)
            _remoteResult.value = failResult
        }
    }

    fun updateLocal(task: UiItemTask) {
        val itemToChange = _localTasks.value?.find {
            it.timestamp == task.timestamp
        }
        itemToChange?.isCompleted = !task.isCompleted
        onLocalChanged()
    }

    fun updateRemoteTasks(task: UiItemTask) {
        val networkResult = _remoteResult.value
        if (networkResult is SuccessfulResult) {
            val itemToUpdate = networkResult
                .data.find {
                    it.id == task.id
                }
            itemToUpdate?.isCompleted = !task.isCompleted
            onRemoteChanged()
        }
    }

    private fun onLocalChanged() {
        _localTasks.value = _localTasks.value
    }


    private fun onRemoteChanged() {
        _remoteResult.value = _remoteResult.value
    }


    fun addNewLocalTask(taskTitle: CharSequence) {
        val newLocalTask = Task(
            userId = 22,
            id = -1L,
            title = taskTitle.toString(),
            timestamp = System.currentTimeMillis(),
            isCompleted = false
        )
        val oldTaskList: List<Task>? = _localTasks.value
        val newLocalTasks = mutableListOf(newLocalTask)
        if (oldTaskList != null && oldTaskList.isNotEmpty()) {
            val addAll: Boolean = newLocalTasks.addAll(oldTaskList)
        }
        _localTasks.value = newLocalTasks
    }


}