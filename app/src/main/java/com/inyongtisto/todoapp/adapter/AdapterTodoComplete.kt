package com.inyongtisto.todoapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.model.Todo
import kotlin.collections.ArrayList

class AdapterTodoComplete(private var context: Activity, private var data: ArrayList<Todo>, val listener: Listener) :
        RecyclerView.Adapter<AdapterTodoComplete.HolderNotif>() {
    internal var b: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HolderNotif {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.item_todo_complete, viewGroup, false)
        return HolderNotif(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderNotif, i: Int) {
        val a = data[i]

        holder.tvTodo.text = a.todo
        holder.tvTodo.paintFlags = holder.tvTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.img.setOnClickListener {
            holder.img.setImageResource(R.drawable.ic_panorama_fish_eye_black_24dp)
            Handler().postDelayed({
                Handler().postDelayed({
                    data.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    Handler().postDelayed({
                        holder.img.setImageResource(R.drawable.ic_check_black_24dp)
                        listener.onChanged(a, holder.adapterPosition)
                    }, 100)
                }, 100)
            }, 100)
        }

        holder.layout.setOnClickListener {
            listener.onClick(a, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listener {
        fun onChanged(data: Todo, i: Int)
        fun onClick(data: Todo, i: Int)
    }

    inner class HolderNotif(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTodo: TextView = itemView.findViewById(R.id.tv_todo)
        var img: ImageView = itemView.findViewById(R.id.img)
        var layout: LinearLayout = itemView.findViewById(R.id.layout)
    }
}