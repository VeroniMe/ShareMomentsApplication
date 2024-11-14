package models


data class Mentee private constructor(

    val name: String,
    val surname: String,
    val phone: String,
    val serviceStart: String,
    val serviceEnd: String,
    val birthday: String,
    val city: String,
    val homeland: String,
    val phoneNumberOfMentor: String,
    val profilePhotoUrl : String


) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "")

    class Builder(
        var name: String = "",
        var surname: String  = "",
        var phone: String = "",
        var serviceStart: String = "",
        var serviceEnd: String = "",
        var birthday: String = "",
        var city: String = "",
        var homeland: String  = "",
        var phoneNumberOfMentor: String = "",
        var profilePhotoUrl: String = ""


    ) {
        fun name(name: String) = apply { this.name = name }
        fun surname(surname: String) = apply { this.surname = surname }
        fun phone(phone: String) = apply { this.phone = phone }
        fun serviceStart(serviceStart: String) = apply { this.serviceStart = serviceStart }
        fun serviceEnd(serviceEnd: String) = apply { this.serviceEnd = serviceEnd }
        fun birthday(birthday: String) = apply { this.birthday = birthday }
        fun city(city: String) = apply { this.city = city }
        fun homeland(homeland: String) = apply { this.homeland = homeland }
        fun phoneNumberOfMentor(phoneNumberOfMentor: String) = apply { this.phoneNumberOfMentor = phoneNumberOfMentor }
        fun profilePhotoUrl(profilePhotoUrl: String) = apply { this.profilePhotoUrl = profilePhotoUrl }

        fun build() = Mentee(
            name,
            surname,
            phone,
            serviceStart,
            serviceEnd,
            birthday,
            city,
            homeland,
            phoneNumberOfMentor,
            profilePhotoUrl

        )
    }

}