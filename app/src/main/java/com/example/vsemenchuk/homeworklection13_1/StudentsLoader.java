package com.example.vsemenchuk.homeworklection13_1;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

public class StudentsLoader extends AsyncTaskLoader<ArrayList<Student>> {

    public StudentsLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public ArrayList<Student> loadInBackground() {
        ArrayList<Student> students = new ArrayList<>();
        Uri uri = Uri.parse("content://com.example.vsemenchuk.homeworklection13/students");
        Cursor cursor = null;

        try {
            cursor = getContext().getContentResolver().query(uri, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Student student = new Student();
                    student.setId(cursor.getInt(cursor.getColumnIndex(Student.KEY_ID)));
                    student.setFirstName(cursor.getString(cursor.getColumnIndex(Student.KEY_FIRST_NAME)));
                    student.setLastName(cursor.getString(cursor.getColumnIndex(Student.KEY_LAST_NAME)));
                    student.setAge(cursor.getInt(cursor.getColumnIndex(Student.KEY_AGE)));

                    students.add(student);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return students;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
