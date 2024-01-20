package com.example.to_dolistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final LayoutInflater inflater;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Task task = getItem(position);

        TextView textView = itemView.findViewById(android.R.id.text1);
        textView.setText(task.getDescription());

        return itemView;
    }
}