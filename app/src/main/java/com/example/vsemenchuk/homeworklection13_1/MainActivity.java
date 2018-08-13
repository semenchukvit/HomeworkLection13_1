package com.example.vsemenchuk.homeworklection13_1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Student>> {

    public static final String EXTRA_STUDENT = "com.example.vsemenchuk.homeworklection13_1.extra.STUDENT";
    public static final int REQUEST_CODE_ADD_STUDENT = 1;
    public static final int REQUEST_CODE_EDIT_STUDENT = 2;

    public static final String EXTRA_ACTION_EDIT_ACTIVITY = "com.example.vsemenchuk.homeworklection12.extra.ACTION_EDIT_ACTIVITY";
    public static final int ACTION_EDIT = 1;
    public static final int ACTION_DELETE = 2;

    public static final String STUDENTS_URI = "content://com.example.vsemenchuk.homeworklection13/students";

    ListView listView;
    FloatingActionButton btnAdd;
    ArrayAdapter<Student> adapter;

    SaveStudentTask saveStudentTask;
    EditStudentTask editStudentTask;
    DeleteStudentTask deleteStudentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.addStudent);

        initStudents();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddStudentActivity.class), REQUEST_CODE_ADD_STUDENT);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = adapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, EditStudentActivity.class);
                intent.putExtra(EXTRA_STUDENT, student);
                startActivityForResult(intent, REQUEST_CODE_EDIT_STUDENT);
            }
        });
    }

    @NonNull
    @Override
    public Loader<ArrayList<Student>> onCreateLoader(int id, @Nullable Bundle args) {
        return new StudentsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Student>> loader, ArrayList<Student> data) {
        setAdapter(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Student>> loader) {

    }

    private void setAdapter(ArrayList<Student> students) {
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                students
        );
        listView.setAdapter(adapter);
    }

    private void initStudents() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void reloadStudents() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_STUDENT) {
                Student student = data.getParcelableExtra(EXTRA_STUDENT);

                saveStudentTask = new SaveStudentTask();
                saveStudentTask.execute(student);
            } else if (requestCode == REQUEST_CODE_EDIT_STUDENT) {
                Student student = data.getParcelableExtra(EXTRA_STUDENT);
                int action = data.getIntExtra(EXTRA_ACTION_EDIT_ACTIVITY, 0);

                switch (action) {
                    case ACTION_EDIT:
                        editStudentTask = new EditStudentTask();
                        editStudentTask.execute(student);
                        break;
                    case ACTION_DELETE:
                        deleteStudentTask = new DeleteStudentTask();
                        deleteStudentTask.execute(student);
                        break;
                }
            }
        }
    }

    private class SaveStudentTask extends AsyncTask<Student, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Student... students) {
            Boolean result = false;

            Uri uri = Uri.parse(STUDENTS_URI);
            ContentValues cv = new ContentValues();

            Student student = students[0];
            cv.put(Student.KEY_FIRST_NAME, student.getFirstName());
            cv.put(Student.KEY_LAST_NAME, student.getLastName());
            cv.put(Student.KEY_AGE, student.getAge());

            try {
                Uri resultUri = getContentResolver().insert(uri, cv);
                if (result != null) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            reloadStudents();
        }
    }

    private class EditStudentTask extends AsyncTask <Student, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Student... students) {
            Boolean result = false;
            Student student = students[0];

            Uri uri = ContentUris.withAppendedId(Uri.parse(STUDENTS_URI), student.getId());
            ContentValues cv = new ContentValues();

            cv.put(Student.KEY_ID, student.getId());
            cv.put(Student.KEY_FIRST_NAME, student.getFirstName());
            cv.put(Student.KEY_LAST_NAME, student.getLastName());
            cv.put(Student.KEY_AGE, student.getAge());

            try {
                int count = getContentResolver().update(uri, cv, null, null);
                if (count != 0) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            reloadStudents();
        }
    }

    private class DeleteStudentTask extends AsyncTask<Student, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Student... students) {
            Boolean result = false;
            Student student = students[0];

            Uri uri = ContentUris.withAppendedId(Uri.parse(STUDENTS_URI), student.getId());

            try {
                int count = getContentResolver().delete(uri, null, null);
                if (count != 0) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            reloadStudents();
        }
    }
}
