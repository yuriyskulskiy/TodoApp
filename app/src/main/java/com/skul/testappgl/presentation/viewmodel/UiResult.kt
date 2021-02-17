package com.skul.testappgl.presentation.viewmodel

import com.skul.testappgl.data.UiItemTask

sealed class UiResult(val tasks: List<UiItemTask>?)
class SuccessResult(tasks: List<UiItemTask>?) : UiResult(tasks)
class InternetErrorResult(tasks: List<UiItemTask>?) : UiResult(tasks)
class LoadingResult(tasks: List<UiItemTask>?) : UiResult(tasks)