package com.inyongtisto.todoapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.model.Todo

class AdapterTodo(private var context: Activity, private var data: ArrayList<Todo>, val listener: Listener) :
        RecyclerView.Adapter<AdapterTodo.HolderNotif>() {
    internal var b: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HolderNotif {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.item_todo, viewGroup, false)
        return HolderNotif(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderNotif, i: Int) {
        val a = data[i]
        holder.tvTodo.text = a.todo
        val format = "yyyy-MM-dd kk:mm"
        val mDate: String
        var mTime = ""
        if (!a.date.contains("0000")) {
            holder.tvTgl.visibility = View.VISIBLE
            val time = Helper.convertTanggal(a.date, format, "kk:mm")
            Log.d("Time", "$time " + a.date)
            mDate = Helper.convertTanggal(a.date, format)
            mTime = if (time != "24:00") {
                "$mDate, $time"
            } else {
                mDate
            }


            holder.tvTgl.text = mTime
        }
        holder.btnDone.setOnClickListener {
            holder.img.setImageResource(R.drawable.ic_check_black_24dp)
            Handler().postDelayed({
                holder.imgKlip.visibility = View.VISIBLE
                Handler().postDelayed({
                    holder.imgKlip.visibility = View.GONE
                    Handler().postDelayed({
                        data.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        Handler().postDelayed({
                            holder.img.setImageResource(R.drawable.ic_panorama_fish_eye_black_24dp)
                            listener.onChanged(a, holder.adapterPosition)
                        }, 100)
                    }, 90)
                }, 90)
            }, 90)
        }

        holder.layout.setOnClickListener {
            listener.onClick(a, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listener {
        fun onChanged(data: Todo, position: Int)
        fun onClick(data: Todo, i: Int)
    }

    inner class HolderNotif(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTodo: TextView = itemView.findViewById(R.id.tv_todo)
        var tvTgl: TextView = itemView.findViewById(R.id.tv_tgl)
        var img: ImageView = itemView.findViewById(R.id.img)
        var imgKlip: ImageView = itemView.findViewById(R.id.img_klip)
        var layout: LinearLayout = itemView.findViewById(R.id.layout)
        var btnDone: RelativeLayout = itemView.findViewById(R.id.btn_done)
    }
}