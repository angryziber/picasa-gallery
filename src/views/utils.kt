package views

val newline = "\r?\n".toRegex()

operator fun Boolean.div(s: String) = if (this) s else ""

fun String?.escapeHTML() = this?.replace("<", "&lt;") ?: ""
fun String?.escapeJS() = this?.replace("'", "\'") ?: ""

operator fun String?.unaryPlus() = escapeHTML()
