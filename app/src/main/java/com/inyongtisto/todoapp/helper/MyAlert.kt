package com.inyongtisto.todoapp.helper

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.inyongtisto.todoapp.R
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

object MyAlert {
    private var alertDialog: AlertDialog? = null
    fun alartLoading(context: Activity, pesan: String) {
        val inflater = context.layoutInflater
        val layout = inflater.inflate(R.layout.view_loading, null)
        val tvPesan: TextView = layout.findViewById(R.id.tv_pesan)
        tvPesan.text = pesan

        if (alertDialog != null) {
            if (alertDialog!!.isShowing) {
                tvPesan.text = pesan
                alertDialog!!.setView(layout)
            }
        }

        if (alertDialog == null) {
            alertDialog = AlertDialog.Builder(context).create()
            alertDialog?.setView(layout)
            alertDialog?.setCancelable(false)
            alertDialog?.show()
            alertDialog?.window!!.setLayout(650, 400)
        }
    }

    fun error(context: Activity, title: String, pesan: String){
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(pesan)
            .show()
    }


    fun alertDismis() {
        if (alertDialog != null) {
            if (alertDialog!!.isShowing) {
                alertDialog?.dismiss()
            }
        }
    }
}