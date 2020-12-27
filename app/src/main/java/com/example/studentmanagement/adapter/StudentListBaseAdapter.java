package com.example.studentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentListBaseAdapter extends BaseAdapter implements Filterable {
    Context context;
    List<Student> items;
    List<Student> itemsFiltered;

    public StudentListBaseAdapter(Context context, List<Student> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
    }

    @Override
    public int getCount() {
        return itemsFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtMssv = convertView.findViewById(R.id.txt_mssv);
            viewHolder.txtHoten = convertView.findViewById(R.id.txt_hoten);
            viewHolder.txtEmail = convertView.findViewById(R.id.txt_email);

            Student item = itemsFiltered.get(position);
            viewHolder.txtMssv.setText(item.getMssv());
            viewHolder.txtHoten.setText(item.getHoten());
            viewHolder.txtEmail.setText(item.getEmail());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

            Student item = itemsFiltered.get(position);
            viewHolder.txtMssv.setText(item.getMssv());
            viewHolder.txtHoten.setText(item.getHoten());
            viewHolder.txtEmail.setText(item.getEmail());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter searchFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = items.size();
                    filterResults.values = items;
                } else {
                    List<Student> resultsModel = new ArrayList<>();

                    for (Student student : items) {
                        if (student.getHoten().toLowerCase().contains(constraint.toString().toLowerCase())
                                || student.getMssv().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultsModel.add(student);
                        }
                    }

                    filterResults.count = resultsModel.size();
                    filterResults.values = resultsModel;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemsFiltered = (List<Student>) results.values;
                StudentListBaseAdapter.this.notifyDataSetChanged();
            }
        };

        return searchFilter;
    }

    private class ViewHolder {
        TextView txtMssv, txtHoten, txtEmail;
    }
}
