package com.a1337.kt.musiq

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a1337.kt.musiq.models.File
import com.a1337.kt.musiq.models.Files
import com.google.gson.Gson


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class SearchResultFragment : Fragment() {
    // TODO: Customize parameters
    private var queryResult: String? = null
    private var mListener: OnListFragmentInteractionListener? = null
    private var layoutManager: LinearLayoutManager? = null

    // for scroll loading
    private val isLoading: Boolean = false
    private val visibleThreshold = 5
    private val lastVisibleItem: Int = 0
    private val totalItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            queryResult = arguments.getString(Constants.QUERY_RESULT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_searchresult_list, container, false)
        val gson = Gson()
        val files = gson.fromJson(queryResult, Files::class.java)

        //         Set the adapter
        if (view is RecyclerView && files != null) {
            val fileList = files.files
            val context = view.getContext()
            val recyclerView = view
            //            if (mColumnCount <= 1) {
            layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            //            } else {
            //                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            //            }

            recyclerView.adapter = SearchResultRecyclerViewAdapter(fileList, mListener)
            val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                    layoutManager!!.orientation)
            recyclerView.addItemDecoration(dividerItemDecoration)

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
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context as OnListFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: File)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(queryResult: String): SearchResultFragment {
            val fragment = SearchResultFragment()
            val args = Bundle()
            args.putString(Constants.QUERY_RESULT, queryResult)
            fragment.arguments = args
            return fragment
        }
    }
}
