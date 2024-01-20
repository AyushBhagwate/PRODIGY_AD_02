package com.example.to_dolistapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText taskInput;
    private ListView taskList;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> tasks;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.taskInput);
        taskList = findViewById(R.id.taskList);

        tasks = new ArrayList<>();
        dbHelper = new DBHelper(this);

        taskAdapter = new TaskAdapter(this, tasks);
        taskList.setAdapter(taskAdapter);

        updateTaskList();

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTask(position);
            }
        });

        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteTask(position);
                return true;
            }
        });
    }

    public void addTask(View view) {
        String description = taskInput.getText().toString().trim();

        if (!description.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_DESCRIPTION, description);

            long id = db.insert(DBHelper.TABLE_TASKS, null, values);

            if (id != -1) {
                Task newTask = new Task((int) id, description);
                tasks.add(newTask);
                taskAdapter.notifyDataSetChanged();
                taskInput.getText().clear();
            } else {
                Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show();
            }

            db.close();
        }
    }

    private void updateTaskList() {
        tasks.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_TASKS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
               @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
               @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION));
                Task task = new Task(id, description);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        taskAdapter.notifyDataSetChanged();
    }

    private void editTask(final int position) {
        final EditText editTaskInput = new EditText(this);
        editTaskInput.setText(tasks.get(position).getDescription());

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(editTaskInput)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDescription = editTaskInput.getText().toString().trim();
                        if (!newDescription.isEmpty()) {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put(DBHelper.COLUMN_DESCRIPTION, newDescription);

                            int rowsAffected = db.update(
                                    DBHelper.TABLE_TASKS,
                                    values,
                                    DBHelper.COLUMN_ID + " = ?",
                                    new String[]{String.valueOf(tasks.get(position).getId())});

                            if (rowsAffected > 0) {
                                tasks.get(position).setDescription(newDescription);
                                taskAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "Error updating task", Toast.LENGTH_SHORT).show();
                            }

                            db.close();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        int rowsAffected = db.delete(
                                DBHelper.TABLE_TASKS,
                                DBHelper.COLUMN_ID + " = ?",
                                new String[]{String.valueOf(tasks.get(position).getId())});

                        if (rowsAffected > 0) {
                            tasks.remove(position);
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show();
                        }

                        db.close();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}