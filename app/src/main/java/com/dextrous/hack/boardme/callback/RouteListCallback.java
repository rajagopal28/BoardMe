package com.dextrous.hack.boardme.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dextrous.hack.boardme.activity.RouteLocationListActivity;
import com.dextrous.hack.boardme.adapter.RouteArrayAdapter;
import com.dextrous.hack.boardme.constant.BoardMeConstants;
import com.dextrous.hack.boardme.model.Route;
import com.dextrous.hack.boardme.response.GenericListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteListCallback implements Callback<GenericListResponse<Route>> {

    private Context context;
    private ListView listView;

    public RouteListCallback(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
    }
    @Override
    public void onResponse(Call<GenericListResponse<Route>> call, Response<GenericListResponse<Route>> response) {
        GenericListResponse<Route> apiResponse = response.body();
        if(apiResponse != null) {
            List<Route> routeList = apiResponse.getItems();
            if(routeList != null
                    && !routeList.isEmpty()){
                ListAdapter listAdapter = new RouteArrayAdapter(context, routeList);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final Route item = (Route) parent.getItemAtPosition(position);
                        // Open the route list activity with selected route item
                        Intent intent = new Intent(context, RouteLocationListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(BoardMeConstants.INTENT_PARAM_SELECTED_ROUTE_KEY, item);
                        context.startActivity(intent);
                        Log.d("Item Selected", item.toString());
                    }

                });
                Log.d("HTTP RESPONSE", apiResponse.toString());
            }
        }
    }

    @Override
    public void onFailure(Call<GenericListResponse<Route>> call, Throwable t) {

    }
}
