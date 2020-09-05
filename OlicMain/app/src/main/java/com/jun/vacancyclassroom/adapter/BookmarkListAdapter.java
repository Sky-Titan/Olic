package com.jun.vacancyclassroom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.BookmarklistItemBinding;
import com.jun.vacancyclassroom.interfaces.MyItemClickListener;
import com.jun.vacancyclassroom.item.BookMarkedRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

public class BookmarkListAdapter extends ListAdapter<BookMarkedRoom, MyViewHolder<BookmarklistItemBinding>> {

    private MainViewModel viewModel;

    private MyItemClickListener itemClickListener;


    public BookmarkListAdapter(MainViewModel viewModel)
    {
        super(BookMarkedRoom.DIFF_CALLBACK);
        this.viewModel = viewModel;
    }

    public void setOnItemClickListener(MyItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder<BookmarklistItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder<>(inflater.inflate(R.layout.bookmarklist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<BookmarklistItemBinding> holder, int position) {
        holder.binding().setItem(getItem(position));
        holder.itemView.setOnClickListener( view -> {
            itemClickListener.OnItemClick(view, position);
        });
    }

    @Override
    public int getItemCount() {
        return viewModel.getBookMarkedRoomsData().getValue().size();
    }
}
