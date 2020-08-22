package com.inyongtisto.todoapp.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.inyongtisto.todoapp.R
import java.text.ParseException
import java.text.SimpleDateFormat

object Helper {

    fun setToolbar(context: Activity, toolbar: Toolbar, title: String) {
        (context as AppCompatActivity).setSupportActionBar(toolbar)
        context.supportActionBar!!.title = title
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    fun blackStatusBar(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = context.window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun pullRefrash(view: SwipeRefreshLayout, listener: OnRefrashListeners) {
        view.setOnRefreshListener {
            listener.onRefrash()
        }

        view.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTanggal(date: String, frmtlama: String, ygDimau: String): String {
        var hasil = ""
        //        final String formatBaru = "dd MMMM yyyy, hh:mm";
        //        final String formatLama = "yyyy-MM-dd hh:mm:s";

        val dateFormat = SimpleDateFormat(frmtlama)
        if (date != "null" || date != null) {
            try {
                val dd = dateFormat.parse(date)
                dateFormat.applyPattern(ygDimau)
                hasil = dateFormat.format(dd)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return hasil
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTanggal(date: String, format: String): String {
        var tanggal = ""
        var hari = ""

        val formatTgl = "dd MMM"
        val formatHari = "EEEE"
        //        final String formatLama = "yyyy-MM-dd hh:mm:s";

        val dateFormat = SimpleDateFormat(format)
        val dateFormat2 = SimpleDateFormat(format)
        try {
            val dd = dateFormat.parse(date)
            dateFormat.applyPattern(formatTgl)
            tanggal = dateFormat.format(dd)

            val mHari = dateFormat2.parse(date)
            dateFormat2.applyPattern(formatHari)
            hari = dateFormat2.format(mHari)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        when (hari) {
            "Sunday" -> hari = "Minggu"
            "Monday" -> hari = "Senin"
            "Tuesday" -> hari = "Selasa"
            "Wednesday" -> hari = "Rabo"
            "Thursday" -> hari = "Kamis"
            "Friday" -> hari = "Jumat"
            "Saturday" -> hari = "Sabtu"
        }

        return "$hari $tanggal"
    }

    fun toast(activity: Activity, string: String) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }

    fun snackBar(view: View, pesan: String) {
        val snackbar = Snackbar.make(view, pesan,Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.WHITE)
        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.BLACK)
        snackbar.show()
    }

    interface OnRefrashListeners {
        fun onRefrash()
    }
}