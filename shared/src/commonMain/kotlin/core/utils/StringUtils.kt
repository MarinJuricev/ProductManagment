package core.utils

fun String.getNameFromEmail() = this.split(".").firstOrNull().orEmpty()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
