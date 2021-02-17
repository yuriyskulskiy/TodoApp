package com.skul.testappgl.data

data class UiItemTask(
    var timestamp: Long = -1L,
    val id: Long = -1L,
    val title: String,
    var isCompleted: Boolean
) {

    // still could be collision
    val uniqueIdentifier: Long
        get() {
            return if (this.isRemote()) {
                this.id
            } else {
                this.timestamp
            }
        }
}

fun UiItemTask.isRemote(): Boolean {
    return id != -1L
}