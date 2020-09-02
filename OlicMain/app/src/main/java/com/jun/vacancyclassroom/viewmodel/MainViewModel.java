package com.jun.vacancyclassroom.viewmodel;

import androidx.lifecycle.ViewModel;

import com.jun.vacancyclassroom.item.BookMarkItem;
import com.jun.vacancyclassroom.item.ListLiveData;

public class MainViewModel extends ViewModel {

    private ListLiveData<BookMarkItem> bookMarkListData = new ListLiveData<>();;




    public ListLiveData<BookMarkItem> getBookMarkListData()
    {
        return bookMarkListData;
    }

    public void addBookMarkItem(BookMarkItem bookMarkItem)
    {
        bookMarkListData.add(bookMarkItem);
    }

    public void removeBookMarkItem(BookMarkItem bookMarkItem)
    {
        bookMarkListData.remove(bookMarkItem);
    }

    public void removeBookMarkItem(int index)
    {
        bookMarkListData.remove(index);
    }



}
