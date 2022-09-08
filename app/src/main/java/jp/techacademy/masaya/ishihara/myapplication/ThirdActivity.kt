package jp.techacademy.masaya.ishihara.myapplication


import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch

import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*
var music_intent : Uri? = null
class ThirdActivity : AppCompatActivity() {

    private lateinit var player: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
       music_intent = Uri.parse(intent.getStringExtra("music_uri"))
        Log.d("ANDROID", music_intent.toString())
       player = MediaPlayer.create(this@ThirdActivity, music_intent)
        if(player.isPlaying){
            player.stop()
            player.release()
        }


    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    //    player.pause()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }
}