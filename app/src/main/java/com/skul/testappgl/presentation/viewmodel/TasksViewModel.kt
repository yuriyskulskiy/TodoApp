package  com.skul.testappgl.presentation.viewmodel

import androidx.lifecycle.*
import com.skul.testappgl.data.Task
import com.skul.testappgl.data.UiItemTask
import com.skul.testappgl.data.isRemote
import com.skul.testappgl.network.Loading
import com.skul.testappgl.network.NetworkError
import com.skul.testappgl.network.NetworkResult
import com.skul.testappgl.network.SuccessfulResult
import com.skul.testappgl.repository.TasksRepository
import kotlinx.coroutines.launch
import java.util.*


class TasksViewModel internal constructor(
    private val repository: TasksRepository
) : ViewModel() {


    val combinedTasksMediatorLiveData: LiveData<UiResult>


    init {
        refreshTasksFormInternet()
        combinedTasksMediatorLiveData = combineToUiResult(
            localData = repository.localTasks,
            remoteResultData = repository.remoteResult
        )

    }

    private fun refreshTasksFormInternet() {
        viewModelScope.launch {
            repository.refreshRemoteTasks()
        }
    }

    fun updateTask(task: UiItemTask) {
        task.isCompleted = task.isCompleted
        if (task.isRemote()) {
            repository.updateRemoteTasks(task)
        } else {
            repository.updateLocal(task)
        }
    }


    fun addNewTask(taskTitle: CharSequence) {
        repository.addNewLocalTask(taskTitle)
    }

    class Factory(private val repository: TasksRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TasksViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}

fun combineToUiResult(
    localData: LiveData<List<Task>>,
    remoteResultData: LiveData<NetworkResult<List<Task>>>
): LiveData<UiResult> =
    MediatorLiveData<UiResult>().also { mediator ->
        mediator.value = null

        mediator
            .addSource(localData) { localTasks ->
                when (val remoteResult = remoteResultData.value) {
                    Loading -> mediator.value = LoadingResult(toUiDataTask(localTasks))
                    is NetworkError -> mediator.value =
                        InternetErrorResult(toUiDataTask(localTasks))
                    is SuccessfulResult -> {
                        val mergedList = mergeLists(localTasks, remoteResult.data)
                        mediator.value = SuccessResult(toUiDataTask(mergedList))
                    }
                    null -> mediator.value = SuccessResult(toUiDataTask(localTasks))
                }
            }
        mediator.addSource(remoteResultData) { networkResult ->
            when (networkResult) {
                Loading -> mediator.value = LoadingResult(toUiDataTask(localData.value))
                is NetworkError -> mediator.value =
                    InternetErrorResult(toUiDataTask(localData.value))
                is SuccessfulResult -> {
                    val mergedList = mergeLists(localData.value, networkResult.data)
                    mediator.value = SuccessResult(toUiDataTask(mergedList))
                }
            }
        }

    }

fun toUiDataTask(list: List<Task>?): List<UiItemTask>? {
    return list?.map {
        UiItemTask(
            timestamp = it.timestamp,
            id = it.id,
            title = it.title,
            isCompleted = it.isCompleted
        )
    }

}

fun mergeLists(first: List<Task>?, second: List<Task>?): MutableList<Task> {
    val mergedListOfTasks = mutableListOf<Task>()
    if (first != null) {
        mergedListOfTasks.addAll(first)
    }
    if (second != null) {
        mergedListOfTasks.addAll(second)
    }
    return Collections.unmodifiableList(mergedListOfTasks)
}





