package placesharing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import placesharing.R;

/**
 * Created by sabussy on 07/03/17.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MAIN";

    private Toolbar myToolbar;
    private boolean viewIsHomePage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public MarkerOptions currentMarkerOptions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(getApplicationContext(),ConnexionActivity.class);
                    startActivity(intent);
                }
            }
        };

        myToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(myToolbar);
        ActionBar myActionBar = getSupportActionBar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset){
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                super.onDrawerSlide(drawerView,slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState != null){
            viewIsHomePage = savedInstanceState.getBoolean("viewIsHomePage");
            getSupportActionBar().setTitle(savedInstanceState.getString("title"));
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,getSupportFragmentManager().getFragment(savedInstanceState,"mainFragment")).commit();
            currentMarkerOptions = savedInstanceState.getParcelable("currentMarkerOptions");
        }
        else{
            displayView(R.id.homePage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public boolean displayView(int ViewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (ViewId){
            case R.id.homePage:
                if(viewIsHomePage){
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
                fragment = new HomePageFragment();
                title = getString(R.string.carte);
                viewIsHomePage = true;
                break;
            case R.id.ajout_lieu:
                fragment = new AddPlaceFragment();
                title = getString(R.string.ajout_lieu);
                viewIsHomePage = false;
                break;
            case R.id.nav_user_profile:
                fragment = new UserInfoFragment();
                title = getString(R.string.nav_profile);
                viewIsHomePage = false;
                break;
            case R.id.place_info:
                fragment = new PlaceInfoFragment();
                title = currentMarkerOptions.getTitle();
                viewIsHomePage = false;
                break;
            case R.id.deconnexion:
                viewIsHomePage = false;
                FirebaseAuth.getInstance().signOut();
                break;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.replace(R.id.mainFragment, fragment).commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        getCurrentFocus().clearFocus();
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            drawer.findFocus().clearFocus();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!viewIsHomePage) {
                displayView(R.id.homePage);
            } else {
                moveTaskToBack(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("viewIsHomePage",viewIsHomePage);
        outState.putString("title",getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState,"mainFragment",getSupportFragmentManager().findFragmentById(R.id.mainFragment));
        outState.putParcelable("currentMarkerOptions",currentMarkerOptions);
    }

}
