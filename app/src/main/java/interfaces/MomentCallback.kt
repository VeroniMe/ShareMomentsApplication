package interfaces

import models.Moment

interface MomentCallback {
    fun likeClickedCallback(moment: Moment, position:Int)
}