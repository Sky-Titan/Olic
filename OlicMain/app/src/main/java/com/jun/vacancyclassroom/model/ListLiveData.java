package com.jun.vacancyclassroom.model;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ListLiveData<T> extends MutableLiveData<ArrayList<T>> {

    public ListLiveData()
    {
        setValue(new ArrayList<>());
    }

    public void add(T item)
    {
        ArrayList<T> itemList = getValue();
        itemList.add(item);
        setValue(itemList);
    }

    public T get(int index)
    {
        return getValue().get(index);
    }
    public int size()
    {
        return getValue().size();
    }

    public void addAll(ArrayList<T> list) {
        ArrayList<T> itemList = getValue();
        itemList.addAll(list);
        setValue(itemList);
    }

    public void clear()
    {
        ArrayList<T> itemList = getValue();
        itemList.clear();
        setValue(itemList);
    }

    public void remove(T item)
    {
        ArrayList<T> itemList = getValue();
        itemList.remove(item);
        setValue(itemList);
    }

    public void remove(int index)
    {
        ArrayList<T> itemList = getValue();
        itemList.remove(index);
        setValue(itemList);
    }

}
