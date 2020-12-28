package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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

        openDB();

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

        registerForContextMenu(lvStudents);
        lvStudents.setLongClickable(true);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 101, 0, "Edit");
        menu.add(0, 102, 1, "Delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Student selectedStudent = studentsList.get(info.position);

        int id = item.getItemId();

        if (id == 101) {
            Dialog addStudentDialog = new Dialog(MainActivity.this);

            addStudentDialog.setContentView(R.layout.add_student_dialog);
            EditText edtMssv = addStudentDialog.findViewById(R.id.edt_mssv);
            EditText edtHoten = addStudentDialog.findViewById(R.id.edt_hoten);
            DatePicker dpNgaysinh = addStudentDialog.findViewById(R.id.edt_ngaysinh);
            EditText edtEmail = addStudentDialog.findViewById(R.id.edt_email);
            EditText edtDiachi = addStudentDialog.findViewById(R.id.edt_diachi);

            // set data
            edtMssv.setText(selectedStudent.getMssv());
            edtHoten.setText(selectedStudent.getHoten());
            String[] date = selectedStudent.getNgaysinh().split("-");
            dpNgaysinh.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
            edtEmail.setText(selectedStudent.getEmail());
            edtDiachi.setText(selectedStudent.getDiachi());

            ((Button) addStudentDialog.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStudentDialog.dismiss();
                }
            });

            ((Button) addStudentDialog.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedStudent.setMssv(edtMssv.getText().toString());
                    selectedStudent.setHoten(edtHoten.getText().toString());
                    selectedStudent.setNgaysinh(dpNgaysinh.getDayOfMonth() + "-" + dpNgaysinh.getMonth() + "-" + dpNgaysinh.getYear());
                    selectedStudent.setEmail(edtEmail.getText().toString());
                    selectedStudent.setDiachi(edtDiachi.getText().toString());

                    updateStudentToDB(selectedStudent);
                    getDataFromDB();
                    adapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, "Cap nhat thong tin sinh vien thanh cong!", Toast.LENGTH_SHORT).show();
                    addStudentDialog.dismiss();
                }
            });

            addStudentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            addStudentDialog.setCanceledOnTouchOutside(true);
            addStudentDialog.show();
        } else if (id == 102) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete student")
                    .setMessage("Cannot undo")
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteStudentFromDB(selectedStudent.getId());
                                    getDataFromDB();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "Xoa thong tin sinh vien thanh cong!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setNeutralButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .create()
                    .show();
        }
        return super.onContextItemSelected(item);
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
            db.execSQL("INSERT INTO students(mssv, hoten, ngaysinh, email, diachi) values (?, ?, ?, ?, ?);", args);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void updateStudentToDB(Student student) {
        if (!db.isOpen()) {
            openDB();
        }

        db.beginTransaction();

        try {
            String[] args = {student.getMssv(), student.getHoten(), student.getNgaysinh() , student.getEmail(), student.getDiachi(), String.valueOf(student.getId())};
            db.execSQL("UPDATE students SET mssv = ?, hoten = ?, ngaysinh = ?, email = ?, diachi = ? WHERE id = ?;", args);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void deleteStudentFromDB(int studentId) {
        if (!db.isOpen()) {
            openDB();
        }

        db.beginTransaction();

        try {
            db.execSQL("DELETE from students  WHERE id == " + studentId + ";");

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void openDB() {
        File storagePath = getApplication().getFilesDir();

        String myDBPath = storagePath + "/" + "student_management";
        db = SQLiteDatabase.openDatabase(myDBPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    private void getDataFromDB() {
        if (!db.isOpen()) {
            openDB();
        }

        String columns[] = {"id", "mssv", "hoten", "ngaysinh", "email", "diachi"};
        Cursor cs = db.query("students", columns, null, null, null, null, null);

        studentsList.removeAll(studentsList);
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