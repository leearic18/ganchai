package org.ganchai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jayfeng.lesscode.core.AdapterLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.ganchai.R;
import org.ganchai.activity.BaseActivity;
import org.ganchai.activity.WebViewActivity;
import org.ganchai.model.Digest;
import org.ganchai.webservices.json.DigestJson;
import org.ganchai.webservices.request.DigestListRequest;
import org.ganchai.widget.RecycleItemDecoration;

import java.util.List;

public class HomeDigestListFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<AdapterLess.RecycleViewHolder> adapter;

    public HomeDigestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_digest_list, container, false);
        recyclerView = ViewLess.$(rootView, R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestData();
    }

    private void requestData() {
        DigestListRequest request = new DigestListRequest();
        ((BaseActivity) getActivity()).getSpiceManager().execute(request, new RequestListener<DigestJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(DigestJson digestJson) {
                List<Digest> data = digestJson.getData().getResult();
                initListView(data);
            }
        });
    }

    private void initListView(List<Digest> data) {
        adapter = AdapterLess.$recycle(getActivity(),
                data,
                R.layout.fragment_home_digest_list_item,
                new AdapterLess.RecycleCallBack<Digest>() {
                    @Override
                    public void onBindViewHolder(int i, AdapterLess.RecycleViewHolder recycleViewHolder, Digest digest) {
                        // set content
                        TextView titleView = recycleViewHolder.$view(R.id.title);
                        TextView summaryView = recycleViewHolder.$view(R.id.summary);

                        titleView.setText(digest.getTitle());
                        summaryView.setText(digest.getSummary());

                        titleView.getPaint().setFakeBoldText(true);

                        // set listener
                        recycleViewHolder.itemView.setTag(digest);
                        recycleViewHolder.itemView.setOnClickListener(HomeDigestListFragment.this);
                    }
                });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST, getResources().getDrawable(R.drawable.recycleview_item_decoration)));
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(WebViewActivity.KEY_URL, ((Digest) tag).getSource());
            startActivity(intent);
        }
    }
}