package com.example.mobilelab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class ListFragment extends Fragment {
    private RelativeLayout main;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        initOnRefresh(view);
        registerNetworkMonitoring();
        connectAndGetApiData();
    }

    private void initFields(final View view) {
        main = view.findViewById(R.id.mainLayout);
        progressBar = view.findViewById(R.id.loading_spinner);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(Objects.requireNonNull(getActivity()), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(new GoodsAdapter(main.getContext(), new ArrayList<>(), R.layout.list_item_good));
    }

    private void initOnRefresh(final View view) {
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            connectAndGetApiData();
            pullToRefresh.setRefreshing(false);
        });
    }

    private void registerNetworkMonitoring() {
        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        final NetworkChangeReceiver receiver = new NetworkChangeReceiver(main);
        Objects.requireNonNull(getActivity()).registerReceiver(receiver, filter);
    }

    public void connectAndGetApiData() {
        final GoodsService service = (getApplicationEx()).getApiService();
        Call<List<Good>> call = service.listGoods();
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Good>>() {
            @Override
            public void onResponse(Call<List<Good>> call, Response<List<Good>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                GoodsAdapter adapter = (GoodsAdapter) recyclerView.getAdapter();
                if (response.isSuccessful() && adapter != null) {
                    List<Good> dataArrayList = response.body();
                    adapter.updateGoods(dataArrayList);
                }
            }

            @Override
            public void onFailure(Call<List<Good>> call, Throwable throwable) {
                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(main, R.string.load_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private App getApplicationEx() {
        return ((App) Objects.requireNonNull(getActivity()).getApplication());
    }
}
