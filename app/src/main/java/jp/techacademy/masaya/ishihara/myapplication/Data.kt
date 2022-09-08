package jp.techacademy.masaya.ishihara.myapplication

import android.graphics.Bitmap
import android.widget.ImageView

data class Data(
    var icon: ImageView? = null,
    var thumbnail : Bitmap? = null,
    var art: Int? = null,
    var title: String? = null,
    var artist: String? = null,
    var text: String? = null,
    var durationMax:Long? = null,
    var truckNum: Int? = null,
    var audioUri_s: String? =null,
    var year:String? = null
)