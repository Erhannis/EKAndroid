package com.erhannis.android.ekandroid.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.erhannis.android.ekandroid.R;

import java.util.List;
import java.util.function.Consumer;

/**
 * A fragment representing a list of Items.
 * Use:
 * Add a `androidx.fragment.app.FragmentContainerView` to your layout.  In your activity `onCreate`:<br/>
 * ListFragment&lt;String&gt; listFragment = new ListFragment&lt;&gt;(Arrays.asList("do what you want", "'cause a pirate is free", "you are a pirate"), x -> {}, x -> {});<br/>
 * <br/>
 * FragmentTransaction ft = getSupportFragmentManager().beginTransaction();<br/>
 * ft.replace(R.id.fragmentContainerView, listFragment);<br/>
 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);<br/>
 * ft.commit();<br/>
 */
public class ListFragment<T> extends Fragment {
    List<T> mRows;
    ArrayAdapter<T> adapter;
    Consumer<T> onClickHandler;
    Consumer<T> onLongClickHandler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    public ListFragment(List<T> rows, Consumer<T> onClickHandler, Consumer<T> onLongClickHandler) {
        this.mRows = rows;
        this.onClickHandler = onClickHandler;
        this.onLongClickHandler = onLongClickHandler;
        this.setRetainInstance(true);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static <T> ListFragment<T> newInstance(List<T> rows, Consumer<T> onClickHandler, Consumer<T> onLongClickHandler) {
        return new ListFragment<>(rows, onClickHandler, onLongClickHandler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options_list, container, false);

        // Set the adapter
        if (view instanceof ListView) {
            Context context = view.getContext();
            ListView listView = (ListView) view;
            adapter = new ArrayAdapter<T>(context, android.R.layout.simple_list_item_1, mRows);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((adapterView, view1, idx, id) -> {
                onClickHandler.accept(mRows.get(idx)); //TODO COULD encounter race condition if list changed
            });
            listView.setOnItemLongClickListener((adapterView, view1, idx, id) -> {
                if (onLongClickHandler != null) {
                    onLongClickHandler.accept(mRows.get(idx)); //TODO COULD encounter race condition if list changed
                    return true;
                } else {
                    return false;
                }
            });
        }
        return view;
    }

    public void setList(List<T> rows) {
        this.mRows = rows;
        try {
            adapter.clear();
            adapter.addAll(mRows);
            adapter.notifyDataSetChanged();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int getCount() {
        return mRows.size();
    }

    public T getItem(int i) {
        return mRows.get(i);
    }
}