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
 * ListFragment listFragment = ListFragment.newInstance(Arrays.asList("do what you want", "'cause a pirate is free", "you are a pirate"), null, null);<br/>
 * <br/>
 * FragmentTransaction ft = getSupportFragmentManager().beginTransaction();<br/>
 * ft.replace(R.id.fragmentContainerView, listFragment);<br/>
 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);<br/>
 * ft.commit();<br/>
 */
public class ListFragment<T> extends Fragment {
    private List<T> mRows;
    private Consumer<T> onClickHandler;
    private Consumer<T> onLongClickHandler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static <T> ListFragment<T> newInstance(List<T> rows, Consumer<T> onClickHandler, Consumer<T> onLongClickHandler) {
        ListFragment fragment = new ListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
        fragment.mRows = rows;
        fragment.onClickHandler = onClickHandler;
        fragment.onLongClickHandler = onLongClickHandler;
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options_list, container, false);

        // Set the adapter
        if (view instanceof ListView) {
            Context context = view.getContext();
            ListView listView = (ListView) view;
            listView.setAdapter(new ArrayAdapter<T>(context, android.R.layout.simple_list_item_1, mRows));
        }
        return view;
    }
}