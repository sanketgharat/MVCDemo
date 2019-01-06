package com.example.sanket.mvcdemo.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sanket.mvcdemo.R;
import com.example.sanket.mvcdemo.controller.Controller;
import com.example.sanket.mvcdemo.model.adapter.AdapterPhotos;
import com.example.sanket.mvcdemo.model.pojo.Photo;
import com.example.sanket.mvcdemo.receivers.ConnectionListener;
import com.example.sanket.mvcdemo.utils.Constants;
import com.example.sanket.mvcdemo.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Controller.PhotoFetchListener, AdapterPhotos.OnListItemClickListener {

    @BindView(R.id.recyclerViewAlbums)
    RecyclerView recyclerViewAlbums;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.spinnerSortBy)
    Spinner spinnerSortBy;
    @BindView(R.id.spinnerOrder)
    Spinner spinnerOrder;
    @BindView(R.id.textViewInfo)
    TextView textViewInfo;
    @BindView(R.id.textViewConnInfo)
    TextView textViewConnInfo;
    @BindView(R.id.mainView)
    ConstraintLayout mainView;

    AdapterPhotos adapterPhotos;
    List<Photo> mPhotoList = new ArrayList<>();
    Controller controller;
    public static final int REQUEST_CODE_ADD_PHOTO = 1;
    public static final int RESULT_CODE_ADD_PHOTO = 1;

    public static final String TAG = MainActivity.class.getSimpleName() + "Tag";
    private boolean enableSearch = false;

    @BindArray(R.array.sortBy_array)
    String[] sortByArray;
    @BindArray(R.array.orderBy_array)
    String[] orderByArray;
    String sortBy = "";
    String orderBy = "";

    int mCallCount = 0;
    public static boolean isAppRunning = false;
    ConnectionListener connectionListener = null;
    IntentFilter networkIntentFilter = null;
    private boolean isDataLoaded = false;

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCallCount = 0;
        sortBy = sortByArray[0];
        orderBy = orderByArray[0];
        configViews();
        configRegister();
        controller = new Controller(MainActivity.this);
        controller.getAlbumPhotos(null, null);
        if (connectionListener != null && networkIntentFilter != null)
            registerReceiver(connectionListener, networkIntentFilter);
    }

    private void configRegister() {
        connectionListener = new ConnectionListener();
        connectionListener.setMainActivityHandler(this);
        networkIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    }

    private void configViews() {
        snackbar = Snackbar.make(mainView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter sortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sortByArray);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerSortBy.setAdapter(sortAdapter);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter orderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, orderByArray);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerOrder.setAdapter(orderAdapter);

        swipeToRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimaryDark));
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetSpinners();
                controller.getAlbumPhotos(null, null);
            }
        });

        adapterPhotos = new AdapterPhotos(MainActivity.this, mPhotoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerViewAlbums.setLayoutManager(layoutManager);
        recyclerViewAlbums.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAlbums.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewAlbums.setNestedScrollingEnabled(false);
        recyclerViewAlbums.setAdapter(adapterPhotos);
        adapterPhotos.setOnListItemClickListener(this);

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = sortByArray[position];
                if (mCallCount++ > 1)
                    controller.getAlbumPhotos(sortBy, orderBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderBy = orderByArray[position];
                if (mCallCount++ > 1)
                    controller.getAlbumPhotos(sortBy, orderBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.fabAdd)
    public void addAlbum() {
        Intent intent = new Intent(MainActivity.this, AddPhotoActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_PHOTO);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);

                //controller.searchAlbumById(query);
                adapterPhotos.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);

                /*if(!enableSearch){
                    enableSearch=true;
                    return false;
                }

                if(newText.isEmpty()){
                    controller.getAlbumPhotos();
                }*/
                adapterPhotos.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreFetching() {
        isError(false);
        swipeToRefresh.setRefreshing(true);
    }

    @Override
    public void onFetchComplete(List<Photo> mPhotoList) {

        this.mPhotoList = mPhotoList;
        adapterPhotos.setPhotosList(mPhotoList);
        swipeToRefresh.setRefreshing(false);
        recyclerViewAlbums.setVisibility(View.VISIBLE);
        isDataLoaded = true;
    }

    @Override
    public void onFetchFailed(int code, String response) {
        Functions.showMessage(MainActivity.this, response);
        textViewInfo.setText(response);
        isError(true);
        isDataLoaded = false;
    }

    @Override
    public void onItemClick(int pos, Photo photo) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Constants.PHOTO_TAG, photo);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int pos, Photo photo, List<Photo> newList) {
        Functions.showMessage(MainActivity.this, "Item deleted");
        this.mPhotoList = newList;
        //adapterPhotos.setPhotosList(mPhotoList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PHOTO && resultCode == RESULT_CODE_ADD_PHOTO) {
            int albumId = data.getIntExtra(Constants.P_ALBUM_ID, 0);
            String title = data.getStringExtra(Constants.P_TITLE);
            String url = data.getStringExtra(Constants.P_URL);
            String thumbnail_url = data.getStringExtra(Constants.P_THUMBNAIL_URL);

            controller.addAlbum(mPhotoList, albumId, title, url, thumbnail_url);
        }
    }

    private void resetSpinners() {
        sortBy = sortByArray[0];
        orderBy = orderByArray[0];
        spinnerSortBy.setSelection(0);
        spinnerOrder.setSelection(0);
    }

    private void isError(boolean val) {
        if (val) {
            textViewInfo.setVisibility(View.VISIBLE);
            recyclerViewAlbums.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
            swipeToRefresh.setRefreshing(false);
        } else {
            textViewInfo.setVisibility(View.GONE);
            //recyclerViewAlbums.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //isAppRunning=true;
        /*if(connectionListener!=null && networkIntentFilter!=null)
            registerReceiver(connectionListener,networkIntentFilter);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppRunning = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppRunning = false;
        //unregisterReceiver(connectionListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
        //unregisterReceiver(connectionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
        unregisterReceiver(connectionListener);
    }

    public void onNetworkChange(boolean isBack) {
        if (isBack)
            //textViewConnInfo.setVisibility(View.GONE);
            snackbar.dismiss();
        else
            //textViewConnInfo.setVisibility(View.VISIBLE);
            snackbar.show();

        if (isBack && isAppRunning && !isDataLoaded) {
            controller.getAlbumPhotos(sortBy, orderBy);
        }
    }
}
