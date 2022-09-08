package jp.techacademy.masaya.ishihara.myapplication




import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_third.*
import java.io.FileDescriptor
import java.util.*


import android.os.Handler
import android.widget.SeekBar
import androidx.constraintlayout.motion.utils.StopLogic
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


private val MediaPlayer.isInitialized: Boolean
    get() {
        TODO()
    }
var album_intent = ""
var nn:Int = 0
var maxnn:Int = 0
var paused:Boolean = false
var durMaxstr =""
var loopmode:String = "all"
val albumList = arrayListOf<Data>()
var timerStart:Boolean = false
class SecondActivity: AppCompatActivity() {

    private lateinit var customAdapter: CustomAdapter2
    private lateinit var player: MediaPlayer
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    private var mHandler = Handler()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        album_intent = intent.getStringExtra("album").toString()
        nn = 0
        albumList.clear()
        loopmode = "all"
        button_loop.setBackgroundResource(R.drawable.rpt_all)
        try {
            if (player.isPlaying) {
                player.stop()
                player.release()
            }
        }catch(e: Exception){

        }
            getContentsInfo()
        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        // イベントリスナーの追加
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // 値が変更された時に呼ばれる
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val seconds: String = java.lang.String.valueOf(progress % 60000 / 1000)
                val minutes: String = java.lang.String.valueOf(progress / 60000)
                var duration: String = ""
                if (seconds.length == 1) {
                    duration = "0$minutes:0$seconds"
                } else {
                    duration = "0$minutes:$seconds"
                }
                //     time00.text = dur.toString() + " / " + duration + " / " + durationR
                time00.text = duration + " / " + durMaxstr
           //     text01.text = progress.toString()
            }

            // つまみがタッチされた時に呼ばれる
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
              //  text01.text = "タッチされました。"
            }

            // つまみが離された時に呼ばれる
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                var prog = seekBar?.progress
            //    if(player.isPlaying){
                    if (prog != null) {
                        player.seekTo(prog)
            //        }
                }
            }
        })
     //   button_play.setOnClickListener { onClick() }
    }
/*
    fun getArtWork(filePath: String?): Bitmap? {
        var bm: Bitmap? = null
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(filePath)
        val data = mmr.embeddedPicture

        if (null != data) {
            bm = BitmapFactory.decodeByteArray(data,0 , data.size )
        }else{
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_audiotrack_24)
        }
        return bm
    }
*/
    //  val contentResolver = applicationContext.contentResolver

    //  @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }


    fun onClick(){
        Log.d("ANDROID", "Click!!")
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            //   MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        //val albamList = mutableListOf("")

        val titleList = mutableListOf<String>()
        if (cursor!!.moveToFirst()) {

            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val musicPath =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            val truckColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER)
            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val AudioUri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val title_s = cursor.getString(titleColumn)
                val duration_l = cursor.getLong(durationColumn)
                val path = cursor.getString(musicPath)
                val truck = cursor.getInt(truckColumn)
                val album = cursor.getString(albumColumn)

                val seconds: String = java.lang.String.valueOf(duration_l % 60000 / 1000)
                val minutes: String = java.lang.String.valueOf(duration_l / 60000)
                var duration_s: String = ""
                if (seconds.length == 1) {
                    duration_s = "0$minutes:0$seconds"
                } else {
                    duration_s = "0$minutes:$seconds"
                }

                if (album == album_intent) {
                    Log.d("ANDROID", title_s + "::" + AudioUri)

                    //       val album_art = R.drawable.ic_baseline_audiotrack_24

                    //    val resources: Resources = this.resources
                    val bitmap00 = BitmapFactory.decodeResource(resources, R.drawable.noimage)

                    var thumb: Bitmap? = null
                    try {
                        thumb = applicationContext.contentResolver.loadThumbnail(
                            AudioUri, Size(640, 480), null
                        )
                    } catch (e: Exception) {
                        thumb = bitmap00
                    }
                    var truck_s: String = ""
                    if (truck < 10) {
                        truck_s = "0" + truck.toString()
                    } else {
                        truck_s = truck.toString()
                    }
                    titleList.add(truck_s + " " + title_s)
                    albumList.add(Data().apply {
                        //    art = album_art
                        thumbnail = thumb
                        title = title_s
                        text = duration_s
                        durationMax = duration_l
                        truckNum = truck
                        audioUri_s = AudioUri.toString()
                    })
                    imageView.setImageBitmap(thumb)
                }

            } while (cursor.moveToNext())

            cursor.close()
            titleList.sort()
            maxnn = titleList.count() - 1
            Log.i("debug", maxnn.toString())
            albumList.sortBy { list -> list.truckNum }

            val lists = findViewById<ListView>(R.id.titlelist)
            lists.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, titleList
            )
            //dummystart
            val dummy_lst = listOf(albumList[0].audioUri_s)
            val dummy_bin = dummy_lst.toString()
            val dummy_str1 = dummy_bin.replace("[", "")
            val dummy_str2 = dummy_str1.replace("]", "")
            val dummyUri: Uri = Uri.parse(dummy_str2)
            val title_lst = listOf(albumList[0].title)
            val title_bin = title_lst.toString()
            val title_str1 = title_bin.replace("[", "")
            val title_str2 = title_str1.replace("]", "")
            val duration_lst = listOf(albumList[0].text)
            val duration_bin = duration_lst.toString()
            val duration_str1 = duration_bin.replace("[", "")
            durMaxstr = duration_str1.replace("]", "")
            val durationMax_lst = listOf(albumList[0].durationMax)
            val durationMax_bin = durationMax_lst.toString()
            val durationMax_str1 = durationMax_bin.replace("[", "")
            val durationMax_str2 = durationMax_str1.replace("]", "")
            val durationMax_int:Int = durationMax_str2.toInt()
            seekBar.min = 0
            seekBar.max = durationMax_int
            var nnInt:Int = nn + 1
            var nnStr = nnInt.toString()
            if(nn < 9){
                nnStr = "0" + nnStr
            }
            text01.text = nnStr + " " + title_str2

            player = MediaPlayer.create(this@SecondActivity, dummyUri)
            playerStart()
            timerStart = true
            mTimer = Timer()

            // タイマーの始動


            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    if(timerStart) {
                        mTimerSec += 0.1
                    }
                    var mTimerInt = mTimerSec.toInt() * 1000
                    mHandler.post {
                        /*
                        val secondsR: String = java.lang.String.valueOf(mTimerInt % 60000 / 1000)
                        val minutesR: String = java.lang.String.valueOf(mTimerInt / 60000)
                        var durationR: String = ""
                        if (secondsR.length == 1) {
                            durationR = "0$minutesR:0$secondsR"
                        } else {
                            durationR = "0$minutesR:$secondsR"
                        }

                         */


                        if(player.isPlaying){
                            var dur = player.getCurrentPosition()
                            seekBar.progress = dur

                            val seconds: String = java.lang.String.valueOf(dur % 60000 / 1000)
                            val minutes: String = java.lang.String.valueOf(dur / 60000)
                            var duration: String = ""
                            if (seconds.length == 1) {
                                duration = "0$minutes:0$seconds"
                            } else {
                                duration = "0$minutes:$seconds"
                            }
                       //     time00.text = dur.toString() + " / " + duration + " / " + durationR
                            time00.text = duration + " / " + durMaxstr
                        }
                    }
                }
            }, 100, 100) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定


            lists.setOnItemClickListener { adapterView, view, position, id ->
                Log.i("debug", "Click!!" + id)
                nn = id.toInt()
                val dummy1_lst = listOf(albumList[id.toInt()].audioUri_s)
                val dummy1_bin = dummy1_lst.toString()
                val dummy1_str1 = dummy1_bin.replace("[", "")
                val dummy1_str2 = dummy1_str1.replace("]", "")
                val audioUri: Uri = Uri.parse(dummy1_str2)
                val title1_lst = listOf(albumList[id.toInt()].title)
                val title1_bin = title1_lst.toString()
                val title1_str1 = title1_bin.replace("[", "")
                val title1_str2 = title1_str1.replace("]", "")
                val duration1_lst = listOf(albumList[id.toInt()].text)
                val duration1_bin = duration1_lst.toString()
                val duration1_str1 = duration1_bin.replace("[", "")
                val durationMax1_lst = listOf(albumList[id.toInt()].durationMax)
                val durationMax1_bin = durationMax1_lst.toString()
                val durationMax1_str1 = durationMax1_bin.replace("[", "")
                val durationMax1_str2 = durationMax1_str1.replace("]", "")
                val durationMax1_int:Int = durationMax1_str2.toInt()
                seekBar.min = 0
                seekBar.max = durationMax1_int
                durMaxstr = duration1_str1.replace("]", "")
                var nnInt:Int = nn + 1
                var nnStr = nnInt.toString()
                if(nn < 9){
                    nnStr = "0" + nnStr
                }
                text01.text = nnStr + " " + title1_str2
             //   time00.text = "00:00" + "/" + duration1_str2
                if (player.isPlaying) {
                    player.stop()
                    player.release()
                }
                player = MediaPlayer.create(this@SecondActivity, audioUri)

                playerStart()
                mTimerSec = 0.0
            }
            /*





            player.setOnCompletionListener {
                if (nn < maxnn) {
                    nn = nn + 1
                } else {
                    nn = 0
                }
                Log.i("debug", "End" + ":" + nn.toString() + ":" + maxnn.toString())

                val dummy2_lst = listOf(albumList[nn].audioUri_s)
                val dummy2_bin = dummy2_lst.toString()
                val dummy2_str1 = dummy2_bin.replace("[", "")
                val dummy2_str2 = dummy2_str1.replace("]", "")
                val audioUri: Uri = Uri.parse(dummy2_str2)
                val title2_lst = listOf(albumList[nn].title)
                val title2_bin = title2_lst.toString()
                val title2_str1 = title2_bin.replace("[", "")
                val title2_str2 = title2_str1.replace("]", "")
                val duration2_lst = listOf(albumList[0].text)
                val duration2_bin = duration2_lst.toString()
                val duration2_str1 = duration2_bin.replace("[", "")
                val duration2_str2 = duration2_str1.replace("]", "")
                text01.text = title2_str2
                time00.text = "00:00" + "/" + duration2_str2
             //   if (player.isPlaying) {
                    player.stop()
                    player.reset()
                    player.release()


             //   }
                player = MediaPlayer.create(this@SecondActivity, audioUri)
                player.setOnPreparedListener(){
                    playerStart()
                 }
                mTimerSec = 0.0

            }

             */
            button_play.setOnClickListener {
                Log.i("debug", "CLICK_PLAY")
                if (player.isPlaying) {
                    player.pause()
                    button_play.setBackgroundResource(R.drawable.ic_baseline_stop_24)
                    timerStart = false
                    paused = true
                } else {
                    player.start()
                    button_play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                    timerStart = true
                    paused = false
                }
                //    val adapter = CustomAdapter2(this, titleList)
                //    custom_list_view2.adapter = adapter
            }
            button_loop.setOnClickListener(){
                if(loopmode == "all"){
                    loopmode = "onece"
                    button_loop.setBackgroundResource(R.drawable.rpt_onece)
                }else if(loopmode == "onece"){
                    loopmode = "all"
                    button_loop.setBackgroundResource(R.drawable.rpt_all)
                }
                Log.i("debug", loopmode)
            }

            button_rr.setOnClickListener {
                var dur = player.getCurrentPosition()
                if(dur > 3000){
                    player.seekTo(0)
                    seekBar.progress = 0
                }else {
                    if (nn == 0) {
                        nn = maxnn
                    } else {
                        nn -= 1
                    }
                    val dummy2_lst = listOf(albumList[nn].audioUri_s)
                    val dummy2_bin = dummy2_lst.toString()
                    val dummy2_str1 = dummy2_bin.replace("[", "")
                    val dummy2_str2 = dummy2_str1.replace("]", "")
                    val audioUri: Uri = Uri.parse(dummy2_str2)
                    val title2_lst = listOf(albumList[nn].title)
                    val title2_bin = title2_lst.toString()
                    val title2_str1 = title2_bin.replace("[", "")
                    val title2_str2 = title2_str1.replace("]", "")
                    val duration2_lst = listOf(albumList[nn].text)
                    val duration2_bin = duration2_lst.toString()
                    val duration2_str1 = duration2_bin.replace("[", "")
                    val durationMax2_lst = listOf(albumList[nn].durationMax)
                    val durationMax2_bin = durationMax2_lst.toString()
                    val durationMax2_str1 = durationMax2_bin.replace("[", "")
                    val durationMax2_str2 = durationMax2_str1.replace("]", "")
                    val durationMax2_int:Int = durationMax2_str2.toInt()
                    seekBar.min = 0
                    seekBar.max = durationMax2_int
                    durMaxstr = duration2_str1.replace("]", "")
                    var nnInt3:Int = nn
                    nnInt3 +=1
                    var nnStr3 = nnInt3.toString()
                    if(nnInt3 < 10){
                        nnStr3 = "0" + nnStr3
                    }
                    text01.text = nnStr3 + " " + title2_str2
                    //     time00.text = "00:00/$duration2_str2"
                    //   if (player.isPlaying) {
                    player.stop()
                    player.reset()
                    player.release()


                    //   }
                    player = MediaPlayer.create(this@SecondActivity, audioUri)
                    player.setOnPreparedListener(){
                        playerStart()
                    }
                    mTimerSec = 0.0
                }
            }
            button_ff.setOnClickListener(){
                if (nn < maxnn) {
                    nn += 1
                } else {
                    nn = 0
                }
                val dummy2_lst = listOf(albumList[nn].audioUri_s)
                val dummy2_bin = dummy2_lst.toString()
                val dummy2_str1 = dummy2_bin.replace("[", "")
                val dummy2_str2 = dummy2_str1.replace("]", "")
                val audioUri: Uri = Uri.parse(dummy2_str2)
                val title2_lst = listOf(albumList[nn].title)
                val title2_bin = title2_lst.toString()
                val title2_str1 = title2_bin.replace("[", "")
                val title2_str2 = title2_str1.replace("]", "")
                val duration2_lst = listOf(albumList[nn].text)
                val duration2_bin = duration2_lst.toString()
                val duration2_str1 = duration2_bin.replace("[", "")
                val durationMax2_lst = listOf(albumList[nn].durationMax)
                val durationMax2_bin = durationMax2_lst.toString()
                val durationMax2_str1 = durationMax2_bin.replace("[", "")
                val durationMax2_str2 = durationMax2_str1.replace("]", "")
                val durationMax2_int:Int = durationMax2_str2.toInt()
                seekBar.min = 0
                seekBar.max = durationMax2_int
                durMaxstr = duration2_str1.replace("]", "")
                var nnInt3:Int = nn
                nnInt3 +=1
                var nnStr3 = nnInt3.toString()
                if(nnInt3 < 10){
                    nnStr3 = "0" + nnStr3
                }
                text01.text = nnStr3 + " " + title2_str2
                //     time00.text = "00:00/$duration2_str2"
                //   if (player.isPlaying) {
                player.stop()
                player.reset()
                player.release()


                //   }
                player = MediaPlayer.create(this@SecondActivity, audioUri)
                player.setOnPreparedListener(){
                    playerStart()
                }
                mTimerSec = 0.0
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun playerStart(){
        seekBar.progress = 0
        if(paused){
            player.start()
            player.pause()
        }else {
            player.start()
        }
 //       player.setOnPreparedListener(){
 //           player.start()
 //       }
        player.setOnCompletionListener {
            if(loopmode == "all") {
                if (nn < maxnn) {
                    nn += 1
                } else {
                    nn = 0
                }
            }


            Log.i("debug", "End" + ":" + nn.toString() + ":" + maxnn.toString())

            val dummy2_lst = listOf(albumList[nn].audioUri_s)
            val dummy2_bin = dummy2_lst.toString()
            val dummy2_str1 = dummy2_bin.replace("[", "")
            val dummy2_str2 = dummy2_str1.replace("]", "")
            val audioUri: Uri = Uri.parse(dummy2_str2)
            val title2_lst = listOf(albumList[nn].title)
            val title2_bin = title2_lst.toString()
            val title2_str1 = title2_bin.replace("[", "")
            val title2_str2 = title2_str1.replace("]", "")
            val duration2_lst = listOf(albumList[nn].text)
            val duration2_bin = duration2_lst.toString()
            val duration2_str1 = duration2_bin.replace("[", "")
            val durationMax2_lst = listOf(albumList[nn].durationMax)
            val durationMax2_bin = durationMax2_lst.toString()
            val durationMax2_str1 = durationMax2_bin.replace("[", "")
            val durationMax2_str2 = durationMax2_str1.replace("]", "")
            val durationMax2_int:Int = durationMax2_str2.toInt()
            seekBar.min = 0
            seekBar.max = durationMax2_int
            durMaxstr = duration2_str1.replace("]", "")
            val nnInt:Int = nn + 1
            var nnStr = nnInt.toString()
            if(nn < 9){
                nnStr = "0" + nnStr
            }
            text01.text = nnStr + " " + title2_str2
       //     time00.text = "00:00/$duration2_str2"
            //   if (player.isPlaying) {
            player.stop()
            player.reset()
            player.release()


            //   }
            player = MediaPlayer.create(this@SecondActivity, audioUri)
            player.setOnPreparedListener(){
                playerStart()
            }
            mTimerSec = 0.0
         }
    }


    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
 //       player.stop()
  //      player.release()
        finish()
  //      player.pause()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }
}


