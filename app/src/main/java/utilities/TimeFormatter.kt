package utilities


object TimeFormatter {
    fun formatTime(lengthInMinutes: Int): String {
        var hours = lengthInMinutes / 60
        var minutes = lengthInMinutes % 60
        return buildString {
            append(String.format(locale = null, format = "%02dh", hours))
            append(" ")
            append(String.format(locale = null, format = "%02dm", minutes))
        }
    }
}