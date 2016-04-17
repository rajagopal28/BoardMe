package com.dextrous.hack.boardme.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.dextrous.hack.boardme.R;
import com.dextrous.hack.boardme.callback.AllRoutesForDialogCallback;
import com.dextrous.hack.boardme.callback.BoardMeLocationCallback;
import com.dextrous.hack.boardme.callback.BoardWaitLocationCallback;
import com.dextrous.hack.boardme.callback.LoginResponseCallback;
import com.dextrous.hack.boardme.callback.RouteListCallback;
import com.dextrous.hack.boardme.callback.UserHistoryListCallback;
import com.dextrous.hack.boardme.callback.UserListCallback;
import com.dextrous.hack.boardme.constant.BoardMeConstants;
import com.dextrous.hack.boardme.fragment.RoutesDialogFragment;
import com.dextrous.hack.boardme.model.Route;
import com.dextrous.hack.boardme.model.TravelHistory;
import com.dextrous.hack.boardme.model.User;
import com.dextrous.hack.boardme.response.GenericBeanResponse;
import com.dextrous.hack.boardme.response.GenericListResponse;
import com.dextrous.hack.boardme.service.BoardMeAPIService;
import com.dextrous.hack.boardme.util.AndroidUtil;
import com.dextrous.hack.boardme.wrapper.RetrofitWrapper;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static com.dextrous.hack.boardme.constant.BoardMeConstants.USER_AUTH_KEY_PREFERENCE_KEY;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RoutesDialogFragment.RouteSelectionFragmentListener {

    Activity self = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_home);
        User userAuth = AndroidUtil.getPreferenceAsObject(getApplicationContext(), BoardMeConstants.USER_AUTH_KEY_PREFERENCE_KEY, User.class);
        if(userAuth != null) {
            TextView usernameText = (TextView) findViewById(R.id.usernameValueLabel);
            TextView useremailText = (TextView) findViewById(R.id.useremailValueLabel);
            if(usernameText!= null
                    && useremailText != null) {
                usernameText.setText(userAuth.getFullName());
                useremailText.setText(userAuth.getEmail());
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initLoginView();
    }
    public void initLoginView() {
        final ViewFlipper flipper = (ViewFlipper)findViewById(R.id.navigation_flip_view);
        String BASE_URL = getResources().getString(R.string.base_api_url);
        RetrofitWrapper.start(BASE_URL);
        final BoardMeAPIService apiService = RetrofitWrapper.build();
        Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView usernameText = (TextView) findViewById(R.id.usernameText);
        final TextView passwordText = (TextView) findViewById(R.id.passwordText);
        System.out.println("Setting on click listener");
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Login and set preference
                    Map<String, Object> form = new HashMap<String, Object>();
                    form.put("username", usernameText.getText().toString());
                    form.put("password", passwordText.getText().toString());
                    Call<GenericBeanResponse<User>> loginCall = apiService.loginUser(form);
                    System.out.println(form);
                    loginCall.enqueue(new LoginResponseCallback(getApplicationContext(), flipper, 2));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final ViewFlipper flipper = (ViewFlipper)findViewById(R.id.navigation_flip_view);
        String BASE_URL = getResources().getString(R.string.base_api_url);
        final BoardMeAPIService apiService = RetrofitWrapper.build();
        RetrofitWrapper.start(BASE_URL);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String userAuthKey = AndroidUtil.getStringPreferenceValue(getApplicationContext(), USER_AUTH_KEY_PREFERENCE_KEY);
        if("".equals(userAuthKey)) {
            switch (id) {
                case R.id.nav_history:
                case R.id.nav_board_me:
                case R.id.nav_board_wait:
                    // Redirect to login page
                    flipper.setDisplayedChild(6);
                    initLoginView();
                    break;
                case R.id.nav_about:
                case R.id.nav_users:
                case R.id.nav_routes:
                    // ignore the non auth screens
                    break;
            }
        } else {
             if (id == R.id.nav_about) {
                flipper.setDisplayedChild(0);
            } else if (id == R.id.nav_users) {
                 flipper.setDisplayedChild(1);
                 // API call
                 Call<GenericListResponse<User>> userCall = apiService.getAllUsers();
                 userCall.enqueue(new UserListCallback(getApplicationContext(), (ListView) findViewById(R.id.userListView)));
             }  else if (id == R.id.nav_routes) {
                flipper.setDisplayedChild(2);
                 // API call
                 Call<GenericListResponse<Route>> routesCall = apiService.getAllRoutes();
                 routesCall.enqueue(new RouteListCallback(getApplicationContext(), (ListView) findViewById(R.id.routeListView)));
            } else if (id == R.id.nav_board_me) {
                flipper.setDisplayedChild(3);
                Button boardmeButton = (Button) findViewById(R.id.boardmeButton);
                if (boardmeButton != null) {
                    boardmeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AndroidUtil.checkGPSEnabled(getApplicationContext(), true)) {
                                // Listener call
                                AndroidUtil.getLocationsHandler(self, new BoardMeLocationCallback(getApplicationContext()));
                            }
                        }
                    });
                }
            } else if (id == R.id.nav_board_wait) {
                flipper.setDisplayedChild(4);
                Button boardwaitButton = (Button) findViewById(R.id.boardwaitButton);
                if (boardwaitButton != null) {
                    boardwaitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = getFragmentManager();
                            Call<GenericListResponse<Route>> allRoutesCall = apiService.getAllRoutes();
                            allRoutesCall.enqueue(new AllRoutesForDialogCallback(fragmentManager));
                        }
                    });
                }
            } else if (id == R.id.nav_history) {
                 // Handle the camera action
                 flipper.setDisplayedChild(5);
                 User userAuthObject = AndroidUtil.getPreferenceAsObject(getApplicationContext(), BoardMeConstants.USER_AUTH_KEY_PREFERENCE_KEY, User.class);
                 // API call
                 Call<GenericListResponse<TravelHistory>> userHistoryCall = apiService.getUserTravelHistory(String.valueOf(userAuthObject.getId()));
                 userHistoryCall.enqueue(new UserHistoryListCallback(getApplicationContext(), (ListView) findViewById(R.id.travelListView)));
             }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRouteSelectConfirmation(Route selectedRoute) {
        if (AndroidUtil.checkGPSEnabled(getApplicationContext(), true)) {
            AndroidUtil.getLocationsHandler(self, new BoardWaitLocationCallback(getApplicationContext(), selectedRoute));
        }
    }
}
