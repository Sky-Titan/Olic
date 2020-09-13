package com.jun.vacancyclassroom.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.BuildinglistItemBinding;
import com.jun.vacancyclassroom.activity.BuildingActivity;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.database.MyViewHolder;
import com.jun.vacancyclassroom.model.Building;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingListAdapter extends ListAdapter<Building, MyViewHolder<BuildinglistItemBinding>> {

    private MainViewModel viewModel;
    private MyDAO dao;
    private ExecutorService executorService;

    private Context context;
    private static final String TAG = "BuildingListAdapter";

    public BuildingListAdapter(Context context, MainViewModel viewModel)
    {
        super(Building.DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.context = context;

        dao = MyDatabase.getInstance(context).dao();

        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void submitList(@Nullable List<Building> list) {
        Collections.sort(list);
        super.submitList(list);
    }

    @NonNull
    @Override
    public MyViewHolder<BuildinglistItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder<>(inflater.inflate(R.layout.buildinglist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<BuildinglistItemBinding> holder, int position) {
        holder.binding().setBuilding(getItem(position));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, BuildingActivity.class);
            intent.putExtra("buildingName", holder.binding().getBuilding().buildingName);
            context.startActivity(intent);
        });
    }
}

