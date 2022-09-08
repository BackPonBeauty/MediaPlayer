package jp.techacademy.masaya.ishihara.myapplication

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileDescriptor


class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    private lateinit var customAdapter: CustomAdapter1

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo("album")
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            custom_list_view.setOnItemClickListener { parent, view, position, id ->
                // Questionのインスタンスを渡して質問詳細画面を起動する
                val itemTextView : TextView = view.findViewById(R.id.title)

                // 項目のラベルテキストをログに表示
                Log.i("debug0", itemTextView.text.toString())
                val intent = Intent(applicationContext, SecondActivity::class.java)
                intent.putExtra("value", 20)
                startActivity(intent)
            }
            getContentsInfo("artist")

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val message = when (item?.itemId) {
            R.id.title -> {
                getContentsInfo("title")
            }
            R.id.artist -> {
                getContentsInfo("artist")
            }
            R.id.year -> {
                getContentsInfo("year")
            }






            else -> "Please select menu"
        }



        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo("artist")
                }
        }
    }


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

  //  val contentResolver = applicationContext.contentResolver

  //  @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getContentsInfo(sortby:String) {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
         //   MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val titleList =  arrayListOf<Data>()
        titleList.clear()
        if (cursor!!.moveToFirst()) {
            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val albumNumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val yearColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
           // val thumb: Bitmap =

            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
                val id = cursor.getLong(fieldIndex)
                val AudioUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id)
                val album = cursor.getString(albumColumn)
                val artistName = cursor.getString(artistColumn)
                val albumNum = cursor.getString(albumNumColumn)
                val albumYear = cursor.getString(yearColumn)

                Log.d("ANDROID", album + "::" +  artistName)

                val bitmap00 = BitmapFactory.decodeResource(resources, R.drawable.noimage)

                var thumb: Bitmap? = null
                try{
                    thumb = applicationContext.contentResolver.loadThumbnail(
                        AudioUri, Size(640, 480), null)
                }catch(e: Exception){
                    thumb = bitmap00
                }


                var album_art = R.drawable.ic_baseline_audiotrack_24
                    titleList.add(Data().apply {
                        thumbnail = thumb
                        art = album_art
                        title = album
                        text = albumNum + "曲"
                        artist = artistName
                        year = albumYear
                    })
            } while (cursor.moveToNext())


            cursor.close()
            titleList.sortByDescending { list -> list.year }
            if(sortby == "artist"){
                titleList.sortBy { list -> list.artist }
            }else if(sortby == "title"){
                titleList.sortBy { list -> list.title }
            }else if(sortby == "year"){
                titleList.sortByDescending { list -> list.year }
            }

            val adapter = CustomAdapter1(this, titleList)
            custom_list_view.adapter = adapter
        }
    }

    override fun onClick(p0: View?) {
        val itemTextView : TextView = custom_list_view.findViewById(R.id.title)
        Log.i("debug1", itemTextView.text.toString())
    }
}