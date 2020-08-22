package com.inyongtisto.todoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.model.Task
import java.util.*

class AdapterTask(private var data: ArrayList<Task>, val listener: Listener) :
    RecyclerView.Adapter<AdapterTask.HolderNotif>() {
    internal var b: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HolderNotif {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.item_task, viewGroup, false)
        return HolderNotif(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderNotif, i: Int) {
        val a = data[i]

        holder.tvJudul.text = a.task
        holder.layout.setOnClickListener {
            a.position = holder.adapterPosition
            listener.onClick(a)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listener{
        fun onClick(data: Task)
    }

    inner class HolderNotif(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvJudul: TextView = itemView.findViewById(R.id.tv_name)
//        var tvDes: TextView = itemView.findViewById(R.id.tv_des)
//        var image: ImageView = itemView.findViewById(R.id.image)
        var layout: CardView = itemView.findViewById(R.id.layout)
    }
}