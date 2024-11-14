package models




data class Moment private constructor(
    val momentName: String,
    val momentPhotoUrl: String,
    val volunteerName: String,
    val description: String,
    val creationDate: String,
    var likesCount: Int,
    var isCollapsed: Boolean = true

) {

    fun toggleCollapse() = apply { this.isCollapsed = !isCollapsed }

    constructor() : this("","", "", "", "", 0, true)

    class Builder(
        var momentName: String = "",
        var momentPhotoUrl: String = "",
        var volunteerName: String  = "",
        var description: String = "",
        var creationDate: String = "",
        var likesCount: Int = 0

    ) {
        fun momentName(momentName: String) = apply { this.momentName = momentName }
        fun momentPhotoUrl(momentPhotoUrl: String) = apply { this.momentPhotoUrl = momentPhotoUrl }
        fun volunteerName(volunteerName: String) = apply { this.volunteerName = volunteerName }
        fun description(description: String) = apply { this.description = description }
        fun creationDate(creationDate: String) = apply { this.creationDate = creationDate }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }

        fun build() = Moment(
            momentName,
            momentPhotoUrl,
            volunteerName,
            description,
            creationDate,
            likesCount


        )
    }

    companion object {
        private var counter = 1

        fun incrementCounter() {
            counter++
        }

        fun getMomentId(): String {


            return buildString {
                append("moment")
                append(counter.toString())
            }
        }

        fun getCounter(): Int {
            return counter
        }

        fun setCounter(count:Int) {
            counter = count
        }
    }

}