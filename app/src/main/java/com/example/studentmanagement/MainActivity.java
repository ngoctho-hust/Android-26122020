package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.studentmanagement.adapter.StudentListBaseAdapter;
import com.example.studentmanagement.model.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    List<Student> studentsList;
    ListView lvStudents;
    StudentListBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File storagePath = getApplication().getFilesDir();

        String myDBPath = storagePath + "/" + "student_management";
        db = SQLiteDatabase.openDatabase(myDBPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

//        db.beginTransaction();
//        try {
//            db.execSQL("CREATE TABLE students (" +
//                    "id integer primary key autoincrement," +
//                    "mssv text," +
//                    "hoten text," +
//                    "ngaysinh text," +
//                    "email text," +
//                    "diachi text" +
//                    ");");
//
//            Faker faker = new Faker();
//
//            for (int i = 0; i < 10; i++) {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
//
//                String[] args = {String.valueOf(20173300 + i), faker.name.name(), simpleDateFormat.format(faker.date.birthday()) , faker.internet.email(), faker.address.city()};
//
//                db.execSQL("INSERT INTO students(mssv, hoten, ngaysinh, email, diachi) values (?, ?, ?, ?, ?)", args);
//            }
//
//            db.execSQL("drop table students");
//
//
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();
//        }


        studentsList = new ArrayList<>();

        getDataFromDB();

        adapter = new StudentListBaseAdapter(this, studentsList);

        lvStudents = findViewById(R.id.lv_students);
        lvStudents.setAdapter(adapter);

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowStudentActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("hoten", studentsList.get(position).getHoten());
                bundle.putString("mssv", studentsList.get(position).getMssv());
                bundle.putString("ngaysinh", studentsList.get(position).getNgaysinh());
                bundle.putString("email", studentsList.get(position).getEmail());
                bundle.putString("diachi", studentsList.get(position).getDiachi());

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Dialog addStudentDialog = new Dialog(MainActivity.this);

            addStudentDialog.setContentView(R.layout.add_student_dialog);

            ((Button) addStudentDialog.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStudentDialog.dismiss();
                }
            });

            ((Button) addStudentDialog.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mssv = ((EditText) addStudentDialog.findViewById(R.id.edt_mssv)).getText().toString();
                    String hoten = ((EditText) addStudentDialog.findViewById(R.id.edt_hoten)).getText().toString();
                    DatePicker datePicker = addStudentDialog.findViewById(R.id.edt_ngaysinh);
                    String ngaysinh = datePicker.getDayOfMonth() + "-" + datePicker.getMonth() + "-" + datePicker.getYear();
                    String email = ((EditText) addStudentDialog.findViewById(R.id.edt_email)).getText().toString();
                    String diachi = ((EditText) addStudentDialog.findViewById(R.id.edt_diachi)).getText().toString();

                    Student newStudent = new Student(1, mssv, hoten, ngaysinh, email, diachi);
                    insertStudentToDB(newStudent);
                    getDataFromDB();
                    adapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, "Da them sinh vien!", Toast.LENGTH_SHORT).show();
                    addStudentDialog.dismiss();
                }
            });

            addStudentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            addStudentDialog.setCanceledOnTouchOutside(true);
            addStudentDialog.show();

            return true;
        }

        return false;
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    private void insertStudentToDB(Student student) {
        db.beginTransaction();

        try {
            String[] args = {student.getMssv(), student.getHoten(), student.getNgaysinh() , student.getEmail(), student.getDiachi()};
            db.execSQL("INSERT INTO students(mssv, hoten, ngaysinh, email, diachi) values (?, ?, ?, ?, ?)", args);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void getDataFromDB() {
        String columns[] = {"id", "mssv", "hoten", "ngaysinh", "email", "diachi"};
        Cursor cs = db.query("students", columns, null, null, null, null, null);

        cs.moveToFirst();
        do {
            int id = cs.getInt(0);
            String mssv = cs.getString(1);
            String hoten = cs.getString(2);
            String ngaysinh = cs.getString(3);
            String email = cs.getString(4);
            String diachi = cs.getString(5);

            studentsList.add(new Student(id, mssv, hoten, ngaysinh, email, diachi));
        } while (cs.moveToNext());
    }
}