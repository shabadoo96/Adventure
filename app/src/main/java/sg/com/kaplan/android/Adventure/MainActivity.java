package sg.com.kaplan.android.Adventure;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;

    private FloatingActionButton addPostBtn;
    private DrawerLayout mainDrawerNav;
    private ActionBarDrawerToggle mainDrawerToggle;
    private NavigationView navigationView;

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private FriendsFragment friendsFragment;
    private FindFriendsFragment findFriendsFragment;
    private FollowersFragment followersFragment;
    private MessagesAllStreamsFragment messagesAllStreamsFragment;
    private FindAdventureFragment findAdventureFragment;
    private SavedAdventuresFragment savedAdventuresFragment;
    private CreateAdventureFragment createAdventureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Init Toolbar & Navbar
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Adventure");



        if (mAuth.getCurrentUser() != null) {

            // Init Fragments
            homeFragment = new HomeFragment();
            accountFragment = new AccountFragment();
            friendsFragment = new FriendsFragment();
            findFriendsFragment = new FindFriendsFragment();
            followersFragment = new FollowersFragment();
            messagesAllStreamsFragment = new MessagesAllStreamsFragment();
            findAdventureFragment = new FindAdventureFragment();
            savedAdventuresFragment = new SavedAdventuresFragment();
            createAdventureFragment = new CreateAdventureFragment();


            mainDrawerNav = findViewById(R.id.main_drawer_layout);
            mainDrawerToggle = new ActionBarDrawerToggle(this, mainDrawerNav, mainToolbar, R.string.drawer_open, R.string.drawer_close);
            mainDrawerNav.addDrawerListener(mainDrawerToggle);
            mainDrawerToggle.syncState();

            navigationView = findViewById(R.id.mainNavView);
            navigationView.setNavigationItemSelectedListener(this);

            navigationView.setCheckedItem(R.id.nav_home);

            initializeFragment();

            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(addPostIntent);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
        } else {
            currentUserId = mAuth.getCurrentUser().getUid();
            db.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        if (!task.getResult().exists()) {
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }

                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mainDrawerNav.isDrawerOpen(GravityCompat.START)) {
            mainDrawerNav.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

        switch (item.getItemId()) {
            case R.id.nav_home:
                addPostBtn.setEnabled(true);
                addPostBtn.setVisibility(View.VISIBLE);
                replaceFragment(homeFragment, currentFragment);
                break;

            case R.id.nav_account:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(accountFragment, currentFragment);
                break;

            case R.id.nav_friends:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(friendsFragment, currentFragment);
                break;

            case R.id.nav_find_friends:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(findFriendsFragment, currentFragment);
                break;

            case R.id.nav_followers:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(followersFragment, currentFragment);
                break;

            case R.id.nav_find_adventure:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(findAdventureFragment, currentFragment);
                break;

            case R.id.nav_saved_adventure:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(savedAdventuresFragment, currentFragment);
                break;

            case R.id.nav_create_adventure:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(createAdventureFragment, currentFragment);
                break;

            case R.id.nav_messages:
                addPostBtn.setEnabled(false);
                addPostBtn.setVisibility(View.INVISIBLE);
                replaceFragment(messagesAllStreamsFragment, currentFragment);
                break;

        }

        mainDrawerNav.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_btn);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Logout handler
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_settings_btn:
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;
        }
    }

    private void logout() {
        mAuth.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);
        fragmentTransaction.add(R.id.main_container, friendsFragment);
        fragmentTransaction.add(R.id.main_container, findFriendsFragment);
        fragmentTransaction.add(R.id.main_container, followersFragment);
        fragmentTransaction.add(R.id.main_container, messagesAllStreamsFragment);
        fragmentTransaction.add(R.id.main_container, findAdventureFragment);
        fragmentTransaction.add(R.id.main_container, savedAdventuresFragment);
        fragmentTransaction.add(R.id.main_container, createAdventureFragment);

        fragmentTransaction.hide(accountFragment);
        fragmentTransaction.hide(friendsFragment);
        fragmentTransaction.hide(findFriendsFragment);
        fragmentTransaction.hide(followersFragment);
        fragmentTransaction.hide(messagesAllStreamsFragment);
        fragmentTransaction.hide(findAdventureFragment);
        fragmentTransaction.hide(savedAdventuresFragment);
        fragmentTransaction.hide(createAdventureFragment);


        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //home button pressed
        if(fragment == homeFragment){
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //account button pressed
        if(fragment == accountFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }
        //friends button pressed
        if(fragment == friendsFragment){
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //find friends button pressed
        if(fragment == findFriendsFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //followers button pressed
        if(fragment == followersFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //messages button pressed
        if(fragment == messagesAllStreamsFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //find adventure button pressed
        if(fragment == findAdventureFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //saved adventures button pressed
        if(fragment == savedAdventuresFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(friendsFragment);
            fragmentTransaction.hide(createAdventureFragment);
        }

        //create adventure button pressed
        if(fragment == createAdventureFragment) {
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(findFriendsFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(followersFragment);
            fragmentTransaction.hide(messagesAllStreamsFragment);
            fragmentTransaction.hide(findAdventureFragment);
            fragmentTransaction.hide(savedAdventuresFragment);
            fragmentTransaction.hide(friendsFragment);
        }

        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
}
