package interfaces

import models.Mentee

interface MenteeCallback {
    fun addReportClicked(mentee: Mentee, position:Int)
}