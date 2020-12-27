package com.example.studentmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowStudentActivity extends AppCompatActivity {
    TextView txtHoten, txtNgaysinh, txtMssv, txtEmail, txtDiachi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student);

        txtHoten = findViewById(R.id.txt_hoten);
        txtMssv = findViewById(R.id.txt_mssv);
        txtNgaysinh = findViewById(R.id.txt_ngaysinh);
        txtEmail = findViewById(R.id.txt_email);
        txtDiachi = findViewById(R.id.txt_diachi);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        txtHoten.setText(bundle.getString("hoten"));
        txtMssv.setText(bundle.getString("mssv"));
        txtNgaysinh.setText(bundle.getString("ngaysinh"));
        txtEmail.setText(bundle.getString("email"));
        txtDiachi.setText(bundle.getString("diachi"));
    }
}