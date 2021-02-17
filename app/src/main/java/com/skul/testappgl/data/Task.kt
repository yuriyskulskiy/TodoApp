package com.skul.testappgl.data

data class Task(
    var timestamp: Long = -1,
    val userId: Long,
    var id: Long = -1L,
    val title: String,
    var isCompleted: Boolean
)

fun Task.isRemote(): Boolean {
    return id!=-1L
}