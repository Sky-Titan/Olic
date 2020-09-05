package com.jun.vacancyclassroom.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.LectureroomlistItemBinding;
import com.example.vacancyclassroom.databinding.LecturesearchItemBinding;
import com.jun.vacancyclassroom.database.MyViewHolder;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.SearchLecture;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;

public class SearchLectureAdapter extends ListAdapter<Lecture, MyViewHolder<LecturesearchItemBinding>> {

    private MainViewModel viewModel;
    private Context context;

    public SearchLectureAdapter(Context context, MainViewModel viewModel)
    {
        super(Lecture.DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder<LecturesearchItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder<>(inflater.inflate(R.layout.lecturesearch_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<LecturesearchItemBinding> holder, int position) {
        holder.binding().setLecture(getItem(position));

        if(Integer.parseInt(holder.binding().getLecture().quota) > Integer.parseInt(holder.binding().getLecture().req_cnt))
            holder.binding().lectureButton.setBackgroundColor(Color.GREEN);
        else
            holder.binding().lectureButton.setBackgroundColor(Color.RED);

        holder.itemView.setOnClickListener(view -> {
            //삭제 여부 확인
            Lecture lecture = holder.binding().getLecture();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(lecture.lecture_code + " " + lecture.lecture_name);
            builder.setMessage("해당 강의를 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", (dialog, which)->{

                viewModel.removeSearchLecture(new SearchLecture(lecture.lecture_code));
                Toast.makeText(context,"삭제하였습니다.",Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("취소", null);
            builder.show();
        });
    }
}
