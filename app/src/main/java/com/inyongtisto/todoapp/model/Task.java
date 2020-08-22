package com.inyongtisto.todoapp.model;

import java.util.ArrayList;

public class Task {
    public int id;
    public int user_id;
    public int position;
    public String task;
    public String updated_at;
    public ArrayList<Todo> todo = new ArrayList<>();
}
