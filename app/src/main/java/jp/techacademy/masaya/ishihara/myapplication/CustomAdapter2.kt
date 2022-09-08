package jp.techacademy.masaya.ishihara.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class CustomAdapter2(context: Context, List: ArrayList<Data>) : View.OnClickListener,ArrayAdapter<Data>(context, 0, List) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view  = convertView
        if (view == null) {
            view  = LayoutInflater.from(context).inflate(R.layout.row_view2, parent, false)
            view.setOnClickListener(this)
        }

        val data = getItem(position)

        val art = view?.findViewById<ImageView>(R.id.thumb)
        art?.setImageBitmap(data?.thumbnail)
     //   data?.art?.let { art?.setImageResource(it) }
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = data?.title
        val name = view?.findViewById<TextView>(R.id.text)
        name?.text = data?.text
        val hide = view?.findViewById<TextView>(R.id.hide)
        hide?.text = data?.audioUri_s
        val artist = view?.findViewById<TextView>(R.id.artist)
        artist?.text = data?.truckNum.toString()

        return view!!
    }

    override fun onClick(v: View) {

        val itemTextView : TextView = v.findViewById(R.id.title)
        val itemHideView : TextView = v.findViewById(R.id.hide)
        Application.instance
        val intent = Intent(context, ThirdActivity::class.java)
        intent.putExtra("album", itemTextView.text.toString())
        intent.putExtra("music_uri",itemHideView.text.toString())
        Log.i("debug3", itemHideView.text.toString())

        context.startActivity(intent)

    }
}