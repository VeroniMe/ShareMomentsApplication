package utilities

import models.Moment

object DataManager {

    fun generateMomentsList(): List<Moment> {
        val moments =  mutableListOf<Moment>()
        moments.add(
                Moment
                    .Builder()
                    .momentPhotoUrl("")
                    .volunteerName("Veronika Merkulova")
                    .description("המשימות תואמות את תוכנית הלימודים ומזמנות התייחסות לכישורי שפה והבנה בקריאה, בכתיבה ובידע לשוני . כל כלי מתמקד בהיבטים מסוימים של כישורי השפה וכולל הנחיות למורה לתיווך לתלמיד ולהמשך תכנון ההוראה.")
                    .creationDate("13/12/2023")
                    .likesCount(10)
                    .build()

               )
        moments.add(
            Moment
                .Builder()
                .momentPhotoUrl("")
                .volunteerName("Caterine Koren")
                .description("גכגכג גכג גכגכג. גכגכגכ גכגכגכ גכגכגכ. דכדכ'קגשגשדג. גשג. שכ''שכש' כ'כדכגכדגעדג דגעג")
                .creationDate("13/12/2023")
                .likesCount(11)
                .build()

        )
        moments.add(
            Moment
                .Builder()
                .momentPhotoUrl("")
                .volunteerName("Michael Levi")
                .description("גכגכג גכג גכגכג. גכגכגכ גכגכגכ גכגכגכ. דכדכ'קגשגשדג. גשג. שכ''שכש' כ'כדכגכדגעדג דגעג")
                /*.creationDate(
                    LocalDate.parse(
                        "13/12/2023",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                )*/
                .creationDate("13/12/2023")
                .likesCount(10)
                .build()

        )
        return moments



    }
}