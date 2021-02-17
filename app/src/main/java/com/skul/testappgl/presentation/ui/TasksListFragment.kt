package com.skul.testappgl.presentation.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.skul.testappgl.R
import com.skul.testappgl.data.UiItemTask
import com.skul.testappgl.presentation.viewmodel.InternetErrorResult
import com.skul.testappgl.presentation.viewmodel.LoadingResult
import com.skul.testappgl.presentation.viewmodel.SuccessResult
import com.skul.testappgl.presentation.viewmodel.TasksViewModel
import com.skul.testappgl.repository.TasksRepository

class TasksListFragment : Fragment() {

    private lateinit var taskET: EditText
    private lateinit var taskTIL: TextInputLayout
    private lateinit var addTaskBtn: Button
    private lateinit var loadingPB: ProgressBar
    private var errorBar: Snackbar? = null

    private val viewModel: TasksViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, TasksViewModel.Factory(TasksRepository()))
            .get(TasksViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingPB = view.findViewById(R.id.loadingPB)
        loadingPB.visibility = View.INVISIBLE

        taskET = view.findViewById(R.id.taskET)
        taskTIL = view.findViewById(R.id.textInputLayout)

        addTaskBtn = view.findViewById(R.id.addBtn)
        addTaskBtn.setOnClickListener {
            createNewTask()
            refreshInput()

        }
        taskET.run {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (text.isNotEmpty()) {
                        taskTIL.error = null
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //nothing to do
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // nothing to do
                }

            })
        }


        val adapter = TasksRecyclerViewAdapter { task -> adapterOnClick(task) }
        val recyclerView = view.findViewById(R.id.tasksListRV) as RecyclerView
        recyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (isItemInsertedToTheTop(positionStart, itemCount)) {
                    (recyclerView.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(positionStart, 0)
                }
            }
        })

        viewModel.combinedTasksMediatorLiveData.observe(viewLifecycleOwner, { uiResult ->
            when (uiResult) {
                is InternetErrorResult -> {
                    loadingFinished()
                    showNetworkError()
                    adapter.submitList(uiResult.tasks)
                }
                is LoadingResult -> loadingStarted()
                is SuccessResult -> {
                    hideNetworkError()
                    loadingFinished()
                    adapter.submitList(uiResult.tasks)
                }

            }

        })
    }

    private fun isItemInsertedToTheTop(positionStart: Int, itemCount: Int): Boolean {
        return positionStart == 0 && itemCount == 1
    }

    private fun createNewTask() {
        val taskTitle = taskET.text.trim()
        if (TextUtils.isEmpty(taskTitle)) {
            taskTIL.error = getString(R.string.warning_not_empty)
        } else {
            viewModel.addNewTask(taskTitle)
        }
    }

    private fun adapterOnClick(task: UiItemTask) {
        viewModel.updateTask(task)
    }

    private fun loadingStarted() {
        hideNetworkError()
        loadingPB.visibility = View.VISIBLE
    }

    private fun loadingFinished() {
        loadingPB.visibility = View.INVISIBLE
    }

    private fun showNetworkError() {
        if (errorBar == null) {
            view?.let {
                errorBar =
                    Snackbar.make(
                        it,
                        getString(R.string.faile_to_load_from_internet),
                        Snackbar.LENGTH_LONG
                    )
            }
            errorBar?.let {
                if (it.isShownOrQueued) {
                    return
                } else {
                    it.show()
                }
            }
        }
    }

    private fun hideNetworkError() {
        errorBar?.let {
            it.dismiss()
        }
    }

    private fun refreshInput() {
        taskET.setText("")
    }

}