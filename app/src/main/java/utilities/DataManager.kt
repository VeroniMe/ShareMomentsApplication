package utilities

import models.Moment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DataManager {

    fun generateMomentsList(): List<Moment> {
        val moments =  mutableListOf<Moment>()
        moments.add(
                Moment
                    .Builder()
                    .momentPhoto("")
                    .volunteerName("Veronika Merkulova")
                    .description("גכגכג גכג גכגכג. גכגכגכ גכגכגכ גכגכגכ. דכדכ'קגשגשדג. גשג. שכ''שכש' כ'כדכגכדגעדג דגעג")
                    .creationDate(
                        LocalDate.parse(
                            "13/12/2023",
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    )
                    .likesCount(10)
                    .build()

               )
        moments.add(
            Moment
                .Builder()
                .momentPhoto("")
                .volunteerName("Caterine Koren")
                .description("גכגכג גכג גכגכג. גכגכגכ גכגכגכ גכגכגכ. דכדכ'קגשגשדג. גשג. שכ''שכש' כ'כדכגכדגעדג דגעג")
                .creationDate(
                    LocalDate.parse(
                        "14/10/2023",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                )
                .likesCount(11)
                .build()

        )
        moments.add(
            Moment
                .Builder()
                .momentPhoto("")
                .volunteerName("Michael Levi")
                .description("גכגכג גכג גכגכג. גכגכגכ גכגכגכ גכגכגכ. דכדכ'קגשגשדג. גשג. שכ''שכש' כ'כדכגכדגעדג דגעג")
                .creationDate(
                    LocalDate.parse(
                        "13/12/2023",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                )
                .likesCount(10)
                .build()

        )
        return moments



    }
}