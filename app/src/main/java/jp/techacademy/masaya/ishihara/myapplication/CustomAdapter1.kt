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

class CustomAdapter1(context: Context, List: ArrayList<Data>) : View.OnClickListener,ArrayAdapter<Data>(context, 0, List) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view  = convertView
        if (view == null) {
            view  = LayoutInflater.from(context).inflate(R.layout.row_view, parent, false)
            view.setOnClickListener(this)
        }

        val data = getItem(position)
        val art = view?.findViewById<ImageView>(R.id.thumb)
        art?.setImageBitmap(data?.thumbnail)
        //data?.art?.let { art?.setImageResource(it) }
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = data?.title
        val year = view?.findViewById<TextView>(R.id.year)
        year?.text = data?.year
        val name = view?.findViewById<TextView>(R.id.text)
        name?.text = data?.text
        val artist = view?.findViewById<TextView>(R.id.artist)
        artist?.text = data?.artist

        return view!!
    }
    override fun onClick(v: View) {
        val itemTextView : TextView = v.findViewById(R.id.title)

        Log.i("debug2", itemTextView.text.toString())

        //syntax err
        val intent = Intent(context, SecondActivity::class.java)
        intent.putExtra("album", itemTextView.text.toString())
        context.startActivity(intent)
    }
}