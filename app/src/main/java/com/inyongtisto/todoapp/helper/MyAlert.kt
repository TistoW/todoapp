package com.inyongtisto.todoapp.helper

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.inyongtisto.todoapp.R
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

object MyAlert {
    private var alertDialog: AlertDialog? = null
    fun loading(context: Activity, pesan: String) {
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

    fun error(context: Activity, title: String, pesan: String) {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(pesan)
            .show()
    }

    fun success(context: Activity, title: String, pesan: String) {
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(title)
            .setContentText(pesan)
            .show()
    }

    fun info(context: Activity, title: String, pesan: String) {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(title)
            .setContentText(pesan)
            .show()
    }

    fun info(context: Activity, title: String, pesan: String, confirmText: String, cancelText: String, callback: Callbacks) {
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText(title)
            .setContentText(pesan)
            .setConfirmText(confirmText)
            .setCancelText(cancelText)
            .setConfirmClickListener {
                callback.onConfirmCliked()
                pDialog.dismiss()
            }
            .setCancelClickListener {
                callback.onCancelCliked()
                pDialog.dismiss()
            }
            .show()
    }

    interface Callbacks {

        fun onConfirmCliked()

        fun onCancelCliked()

        fun onFilterClicked(status: String)
    }

    open class DefaultCallback : Callbacks {
        override fun onConfirmCliked() {}

        override fun onCancelCliked() {}

        override fun onFilterClicked(status: String) {}
    }

    fun alertDismis() {
        if (alertDialog != null) {
            if (alertDialog!!.isShowing) {
                alertDialog?.dismiss()
            }
        }
    }
}