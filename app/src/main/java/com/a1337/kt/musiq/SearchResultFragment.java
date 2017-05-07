package com.a1337.kt.musiq;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a1337.kt.musiq.models.File;
import com.a1337.kt.musiq.models.Files;
import com.google.gson.Gson;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchResultFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String queryResult;
    private OnListFragmentInteractionListener mListener;
    private LinearLayoutManager layoutManager;

    // for scroll loading
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchResultFragment newInstance(String queryResult) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(Constants.QUERY_RESULT, queryResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            queryResult = getArguments().getString(Constants.QUERY_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresult_list, container, false);
        Gson gson = new Gson();
        Files files = gson.fromJson(queryResult, Files.class);

//         Set the adapter
        if (view instanceof RecyclerView && files != null) {
            List<File> fileList = files.getFiles();
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }

            recyclerView.setAdapter(new SearchResultRecyclerViewAdapter(fileList, mListener));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            //TODO:implement endless loading
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
//                    lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                        if (onLoadMoreListener != null) {
//                            onLoadMoreListener.onLoadMore();
//                        }
//                        isLoading = true;
//                    }
//                }
//            });

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(File item);
    }
}
