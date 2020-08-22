package com.inyongtisto.todoapp.model

import java.io.Serializable
import java.util.ArrayList

class ResponModel : Serializable {
    var success = 0
    var message = ""
    var user = User()

    val task = Task()
    val tasks = ArrayList<Task>()
    var todos = ArrayList<Todo>()

}


