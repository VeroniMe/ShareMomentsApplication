package models

data class Report private constructor(

    val mentorName: String,
    val menteeName: String,
    val reportDate: String,
    val description: String


) {
    class Builder(
        var mentorName: String = "",
        var menteeName: String  = "",
        var reportDate: String = "",
        var description: String = ""

    ) {
        fun mentorName(mentorName: String) = apply { this.mentorName = mentorName }
        fun menteeName(menteeName: String) = apply { this.menteeName = menteeName }
        fun reportDate(reportDate: String) = apply { this.reportDate = reportDate }
        fun description(description: String) = apply { this.description = description }

        fun build() = Report(
            mentorName,
            menteeName,
            reportDate,
            description
        )
    }

}
