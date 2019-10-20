package com.example.mobilelab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout main;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        registerNetworkMonitoring();
        connectAndGetApiData();
        setOnRefresh();
    }

    public void connectAndGetApiData() {
        final GoodsService service = ((App) getApplication()).getApiService();
        Call<List<Good>> call = service.listGoods();
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Good>>() {
            @Override
            public void onResponse(Call<List<Good>> call, Response<List<Good>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    List<Good> dataArrayList = response.body();
                    recyclerView.setAdapter(new GoodsAdapter(dataArrayList, R.layout.list_item_good, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<List<Good>> call, Throwable throwable) {
                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(main, R.string.load_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void initFields() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        main = findViewById(R.id.mainLayout);
        progressBar = findViewById(R.id.loading_spinner);
    }

    public void registerNetworkMonitoring() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReceiver receiver = new NetworkChangeReceiver(main);
        this.registerReceiver(receiver, filter);
    }

    public void setOnRefresh() {
        pullToRefresh.setOnRefreshListener(() -> {
            connectAndGetApiData();
            pullToRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
