package com.inyongtisto.todoapp.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.adapter.AdapterTodo
import com.inyongtisto.todoapp.adapter.AdapterTodoComplete
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Todo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class TodoFragment : Fragment(), TodoListener {

    private lateinit var tvTotal: TextView
    private lateinit var tvTitle: TextView
    private lateinit var pd: ProgressBar
    private lateinit var divKosong: LinearLayout
    private lateinit var divComplete: LinearLayout
    private lateinit var rvTodo: RecyclerView
    private lateinit var rvCompleteTodo: RecyclerView
    private lateinit var btnAdd: RelativeLayout
    private lateinit var btnOpen: RelativeLayout

    private lateinit var viewModel: TodoViewModel
    private var lstTodo: ArrayList<Todo> = ArrayList()
    private var lstTodoComplete: ArrayList<Todo> = ArrayList()
    private lateinit var s: SharePref

    private var taskId: Int = 0
    var task: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_todo, container, false)
        init(view)
        s = SharePref(activity!!)

        val id = arguments!!.getString("task")
        val title = arguments!!.getString("title")
        taskId = Integer.valueOf(id!!)
        task = title!!
        arguments!!.remove("task")
        arguments!!.remove("title")

        tvTitle.text = title.toString()

        viewModel = ViewModelProvider(activity!!).get(TodoViewModel::class.java)
        viewModel.listener = this
        viewModel.getTodo(id)
        viewModel.onProgress()
        viewModel.showProgress.observe(activity!!, Observer {
            if (it) {
                pd.visibility = View.VISIBLE
            } else {
                pd.visibility = View.GONE
            }
        })

        displayTodo()
        displayTodoComplete()
        mainButton()
        return view
    }

    private fun mainButton() {
        btnAdd.setOnClickListener {
            bottomSheet()
        }

        btnOpen.setOnClickListener {
            if (rvCompleteTodo.visibility == View.VISIBLE) {
                rvCompleteTodo.visibility = View.GONE
            } else {
                rvCompleteTodo.visibility = View.VISIBLE
            }
        }
    }

    var adapterTodoComplete: AdapterTodoComplete? = null
    var adapterTodo: AdapterTodo? = null
    private fun displayTodo() {
        if (lstTodo.size == 0 && lstTodoComplete.size == 0) {
            divKosong.visibility = View.VISIBLE
        } else {
            divKosong.visibility = View.GONE
        }

        adapterTodo = AdapterTodo(activity!!, lstTodo, object : AdapterTodo.Listener {
            @SuppressLint("SetTextI18n")
            override fun onChanged(data: Todo, position: Int) {
                data.status = "0"
                data.updated_at = null
                viewModel.updateTodo(data)
                lstTodoComplete.add(0, data)
                adapterTodoComplete!!.notifyItemInserted(0)
                updatePreffList()
                tvTotal.text = "Complete(" + lstTodoComplete.size + ")"

                if (lstTodoComplete.size > 0) {
                    divComplete.visibility = View.VISIBLE
                } else {
                    divComplete.visibility = View.GONE
                }

                val view: View = activity!!.findViewById(android.R.id.content)
                Helper.snackBar(view, "1 completed")
            }

            override fun onClick(data: Todo, i: Int) {
                bottomDetailTodo(data, i)
            }
        })

        val layoutManager2 = LinearLayoutManager(activity!!)
        layoutManager2.orientation = LinearLayoutManager.VERTICAL
        rvTodo.setHasFixedSize(false)
        rvTodo.layoutManager = layoutManager2
        rvTodo.adapter = adapterTodo
    }

    private fun displayTodoComplete() {
        if (lstTodoComplete.size > 0) {
            divComplete.visibility = View.VISIBLE
        } else {
            divComplete.visibility = View.GONE
        }

        adapterTodoComplete = AdapterTodoComplete(activity!!, lstTodoComplete, object : AdapterTodoComplete.Listener {
            @SuppressLint("SetTextI18n")
            override fun onChanged(data: Todo, i: Int) {
                data.status = "1"
                data.updated_at = null
                viewModel.updateTodo(data)
                lstTodo.add(0, data)
                adapterTodo!!.notifyItemInserted(0)
                updatePreffList()
                tvTotal.text = "Complete(" + lstTodoComplete.size + ")"
                if (lstTodoComplete.size > 0) {
                    divComplete.visibility = View.VISIBLE
                } else {
                    divComplete.visibility = View.GONE
                }

                val view: View = activity!!.findViewById(android.R.id.content)
                Helper.snackBar(view, "1 marked incomplete")
            }

            override fun onClick(data: Todo, i: Int) {
                bottomDetailTodo(data, i)
            }
        })

        val layoutManager2 = LinearLayoutManager(activity!!)
        layoutManager2.orientation = LinearLayoutManager.VERTICAL
        rvCompleteTodo.setHasFixedSize(false)
        rvCompleteTodo.layoutManager = layoutManager2
        rvCompleteTodo.adapter = adapterTodoComplete
    }

    lateinit var tvTanggal: TextView
    lateinit var tvTime: TextView
    lateinit var divTanggal: LinearLayout
    var dialog: BottomSheetDialog? = null
    private fun bottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_create_todo, null)
        dialog = BottomSheetDialog(activity!!)
        dialog!!.setContentView(view)
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog!!.show()
        dialog!!.setOnDismissListener {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        dialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val edtTitle = view.findViewById<EditText>(R.id.edt_title)
        val btnDatePic = view.findViewById<RelativeLayout>(R.id.btn_datePic)
        val btnTimePic = view.findViewById<RelativeLayout>(R.id.btn_timePic)
        val btnRemove = view.findViewById<ImageView>(R.id.btn_remove)
        val btnSave = view.findViewById<TextView>(R.id.btn_save)
        divTanggal = view.findViewById(R.id.div_tanggal)
        tvTanggal = view.findViewById(R.id.tv_tanggal)

        edtTitle.requestFocus()

        btnDatePic.setOnClickListener {
            setupTanggal(0)
        }

        btnTimePic.setOnClickListener {
            setupJam(0)
        }

        btnRemove.setOnClickListener {
            resetDateTime()
            divTanggal.visibility = View.GONE
        }

        btnSave.setOnClickListener {
            if (edtTitle.text.isEmpty()) {
                edtTitle.error = "Field Title can't be empty"
                edtTitle.requestFocus()
                return@setOnClickListener
            } else {
                createTodo(edtTitle.text.toString())
                dialog!!.dismiss()
            }
        }
    }

    lateinit var btnTimePic: TextView
    lateinit var btnDatePic: TextView
    lateinit var divTime: LinearLayout
    var dialogDetail: BottomSheetDialog? = null
    private fun bottomDetailTodo(data: Todo, i: Int) {
        val view: View = layoutInflater.inflate(R.layout.layout_deteil_todo, null)
        dialogDetail = BottomSheetDialog(activity!!)
        dialogDetail!!.setContentView(view)
        dialogDetail!!.show()
        dialogDetail!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val divSetting: LinearLayout = view.findViewById(R.id.div_settting)
        val btnBack: ImageView = view.findViewById(R.id.btn_back)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete)
        val btnSelesai: ImageView = view.findViewById(R.id.btn_selesai)
        val btnCloseTgl: ImageView = view.findViewById(R.id.btn_remove)
        val btnCloseTime: ImageView = view.findViewById(R.id.btn_removeTime)
        val tvTask: TextView = view.findViewById(R.id.tv_task)
        val edtTodo: TextView = view.findViewById(R.id.edt_todo)
        btnDatePic = view.findViewById(R.id.btn_datePic)
        btnTimePic = view.findViewById(R.id.btn_timePic)
        tvTanggal = view.findViewById(R.id.tv_tanggal)
        tvTime = view.findViewById(R.id.tv_time)
        divTanggal = view.findViewById(R.id.div_tanggal)
        divTime = view.findViewById(R.id.div_time)

        tvTask.text = task
        edtTodo.text = data.todo

        dialogDetail!!.setOnDismissListener {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            Log.d("cek", "$tanggal $time")
            if (data.todo != edtTodo.text.toString() || data.date != "$tanggal $time") {
                data.todo = edtTodo.text.toString()
                updateData(data, i)
            }
        }

        if (data.status == "0") {
            hideView(divSetting)
        }

        val mDate: String
        val format = "yyyy-MM-dd kk:mm"
        if (!data.date.contains("0000")) {
            displayView(divTanggal)
            hideView(btnDatePic)
            val time = Helper.convertTanggal(data.date, format, "kk:mm")
            mDate = Helper.convertTanggal(data.date, format)
            if (time != "24:00") {
                displayView(divTime)
                hideView(btnTimePic)
            }

            tvTanggal.text = mDate
            tvTime.text = time
        }

        btnBack.setOnClickListener {
            Log.d("cek", "$tanggal $time")
            dialogDetail!!.dismiss()
            if (data.todo != edtTodo.text.toString() || data.date != "$tanggal $time") {
                data.todo = edtTodo.text.toString()
                updateData(data, i)
            }
        }

        btnDelete.setOnClickListener {
            dialogDetail!!.dismiss()
            viewModel.deleteTodo(data)
            if (data.status == "0") {
                lstTodoComplete.removeAt(i)
                adapterTodoComplete!!.notifyItemRemoved(i)
            } else {
                lstTodo.removeAt(i)
                adapterTodo!!.notifyItemRemoved(i)
            }

        }

        btnSelesai.setOnClickListener {
            Log.d("cek", "$tanggal $time")
            dialogDetail!!.dismiss()
            if (data.todo != edtTodo.text.toString() || data.date != "$tanggal $time") {
                data.todo = edtTodo.text.toString()
                updateData(data, i)
            }
        }

        btnCloseTgl.setOnClickListener {
            hideView(divTanggal)
            displayView(btnDatePic)
            tanggal = "0000-00-00"
        }

        btnCloseTime.setOnClickListener {
            hideView(divTime)
            displayView(btnTimePic)
            time = "00:00"
        }

        btnDatePic.setOnClickListener {
            setupTanggal(1)
        }

        btnTimePic.setOnClickListener {
            setupJam(1)
        }
    }

    private fun updateData(data: Todo, i: Int) {
        data.date = "$tanggal $time"

        Log.d("cek", "" + data.date)
        viewModel.updateTodo(data)

        if (data.status == "0") {
            lstTodoComplete[i] = data
            adapterTodoComplete!!.notifyItemChanged(i)
        } else {
            lstTodo[i] = data
            adapterTodo!!.notifyItemChanged(i)
        }

        updatePreffList()
    }

    private fun createTodo(str: String) {
        val todo = Todo()
        todo.todo = str
        todo.task_id = taskId
        todo.date = "$tanggal $time"
        resetDateTime()
        viewModel.createTodo(todo)

        lstTodo.add(0, todo)
        adapterTodo!!.notifyItemInserted(0)

        updatePreffList()

        if (lstTodo.size > 0) {
            hideView(divKosong)
        } else {
            displayView(divKosong)
        }
    }

    private var tanggal: String = "0000-00-00"
    private var time: String = "00:00"
    private var setCalender: DatePickerDialog.OnDateSetListener? = null

    @SuppressLint("SetTextI18n")
    fun setupTanggal(i: Int) {
        setCalender = DatePickerDialog.OnDateSetListener { _, thun, month, day ->
            tanggal = thun.toString() + "-" + (month + 1) + "-" + day

            var mTgl = Helper.convertTanggal(tanggal, "yyyy-MM-dd")
            if (time != "00:00") {
                mTgl += ", $time"
            }

            displayView(divTanggal)
            tvTanggal.text = mTgl

            if (i == 1) {
                hideView(btnDatePic)
            }
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(activity!!, setCalender, year, month, day)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setupJam(i: Int) {
        val hour = 12
        val minute = 0
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(activity!!, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            val jam: String = if (selectedHour.toString().length == 1) {
                "0$selectedHour"
            } else {
                "$selectedHour"
            }

            val menit: String = if (selectedMinute.toString().length == 1) {
                "0$selectedMinute"
            } else {
                "$selectedMinute"
            }

            var mJam = "$jam:$menit"
            time = mJam
            if (tanggal == "0000-00-00") {
                tanggal = getToday()
            }

            if (i == 1) {
                displayView(divTime)
                hideView(btnTimePic)
                tvTime.text = mJam
            } else {
                mJam = Helper.convertTanggal(tanggal, "yyyy-MM-dd") + ", $mJam"
                tvTanggal.text = mJam
                displayView(divTanggal)
            }

        }, hour, minute, true)//Yes 24 hour time

        mTimePicker.setTitle("Atur jam")
        mTimePicker.show()
    }

    private fun getToday(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getTime(): String {
        return SimpleDateFormat("kk:mm", Locale.getDefault()).format(Date())
    }

    private fun init(view: View) {
        pd = view.findViewById(R.id.pd)
        divKosong = view.findViewById(R.id.div_kosong)
        divComplete = view.findViewById(R.id.div_complite)
        rvTodo = view.findViewById(R.id.rv_todo)
        rvCompleteTodo = view.findViewById(R.id.rv_completeTodo)
        tvTitle = view.findViewById(R.id.tv_title)
        btnAdd = view.findViewById(R.id.btn_addTodo)
        tvTotal = view.findViewById(R.id.tv_total)
        btnOpen = view.findViewById(R.id.btn_open)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess(data: ArrayList<Todo>) {
        val lstTodo = ArrayList<Todo>()
        val lstTodoComplete = ArrayList<Todo>()
        for (t: Todo in data) {
            if (t.status == "0")
                lstTodoComplete.add(t)
            else
                lstTodo.add(t)
        }
        this.lstTodo = lstTodo
        this.lstTodoComplete = lstTodoComplete
        displayTodo()
        displayTodoComplete()
        val rd = ResponModel()
        rd.todos = data
        SharePref(activity!!).setListTodo(rd)

        tvTotal.text = "Complete(" + lstTodoComplete.size + ")"
        if (dialog != null) dialog!!.dismiss()
    }

    override fun onFailure(message: String) {
        Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
        if (dialog != null) dialog!!.dismiss()
    }

    private fun updatePreffList() {
        val rd = ResponModel()
        for (t: Todo in lstTodo) rd.todos.add(t)
        for (t: Todo in lstTodoComplete) rd.todos.add(t)
        for (t: Todo in rd.todos) Log.d("Cek", "Name:" + t.todo + " sts:" + t.status)
        s.setListTodo(rd)
    }

    private fun hideView(view: View) {
        view.visibility = View.GONE
    }

    private fun displayView(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun resetDateTime() {
        tanggal = "0000-00-00"
        time = "00:00"
    }
}
