package models

import java.time.LocalDate


data class Moment private constructor(
    val momentPhoto: String,
    //val genre: List<String>,
    //val actors: List<String>,
    val volunteerName: String,
    //val length: Int,
    val description: String,
    val creationDate: LocalDate,
    //val rating: Float,
    var likesCount: Int,
    var isCollapsed: Boolean = true
) {
    fun toggleCollapse() = apply { this.isCollapsed = !isCollapsed }

    class Builder(
        var momentPhoto: String = "",
        //var genre: List<String> = emptyList(),
        //var actors: List<String> = emptyList(),
        var volunteerName: String  = "",
        var likesCount: Int =0,
        var description: String = "",
        var creationDate: LocalDate = LocalDate.now(),
        //var rating: Float = 0.0F,
        //var isFavorite: Boolean = false
    ) {
        fun momentPhoto(poster: String) = apply { this.momentPhoto = momentPhoto }
        //fun genre(genre: List<String>) = apply { this.genre = genre }
        //fun actors(actors: List<String>) = apply { this.actors = actors }
        fun volunteerName(name: String) = apply { this.volunteerName = volunteerName }
        fun likesCount(length: Int) = apply { this.likesCount = likesCount }
        fun description(overview: String) = apply { this.description = description }
        fun creationDate(releaseDate: LocalDate) = apply { this.creationDate = creationDate }
        //fun rating(rating: Float) = apply { this.rating = rating }
        //fun isFavorite(isFavorite: Boolean) = apply { this.isFavorite = isFavorite }
        fun build() = Moment(
            momentPhoto,
            volunteerName,
            description,
            creationDate,
            likesCount


        )
    }

}