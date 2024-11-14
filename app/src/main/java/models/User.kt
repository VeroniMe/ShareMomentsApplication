package models


data class User private constructor(
    val name: String,

    //val actors: List<String>,
    val surname: String,
    //val length: Int,
    val email: String,
    val phone: String,
    val startDate: String,
    val mentees: List<Mentee>,
    val profilePhotoUrl: String
    //val rating: Float,


) {
    constructor() : this("", "", "", "", "", emptyList(),"")


    class Builder(
        var name: String = "",
        //var genre: List<String> = emptyList(),
        //var actors: List<String> = emptyList(),
        var surname: String  = "",
        var email: String = "",
        var phone: String = "",
        var startDate: String = "",
        var mentees: List<Mentee> = emptyList(),
        var profilePhotoUrl: String = ""
        //var rating: Float = 0.0F,
        //var isFavorite: Boolean = false

    ) {
        fun name(name: String) = apply { this.name = name }
        fun surname(surname: String) = apply { this.surname = surname }
        fun email(email: String) = apply { this.email = email }
        fun phone(phone: String) = apply { this.phone = phone }
        fun startDate(startDate: String) = apply { this.startDate = startDate }
        fun mentees(mentees: List<Mentee>) = apply { this.mentees = mentees }
        fun profilePhotoUrl(profilePhotoUrl: String) = apply { this.profilePhotoUrl = profilePhotoUrl }

        fun build() = User(
            name,
            surname,
            email,
            phone,
            startDate,
            mentees,
            profilePhotoUrl

        )
    }

}