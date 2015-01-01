package stethoscope.com.blsassistant;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import java.util.ArrayList;
import stethoscope.com.blsassistant.blsmodel.BlsDataReader;
import stethoscope.com.blsassistant.blsmodel.BlsGuide;
import stethoscope.com.blsassistant.blsmodel.BlsMap;
import stethoscope.com.blsassistant.blsmodel.BlsSearch;
import stethoscope.com.blsassistant.blsmodel.BlsTemplate;
import stethoscope.com.blsassistant.blsmodel.BlsTemplateFactory;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{


    /*
     *  Constants
     */
    //DETAIL_* constants are used to specify the current template type in placeholder fragment
    public static final int DETAIL_FRAGMENT_TYPE_BLSGUIDE = 1;
    public static final int DETAIL_FRAGMENT_TYPE_BLSMAP = 2;
    public static final int DETAIL_FRAGMENT_TYPE_BLSSEARCH = 3;
    //public static final String FRAGMENT_TITLE_ARRAY_FLAG = "fragment_title_array";
    public static final String CURRENT_FRAGMENT = "current_fragment";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //menu instance for collapsing searchview
    private Menu menu;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //BlsTemplate data array, storing the data load from the json files
    private BlsTemplate[] templateDataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test", "onCreate()");
        //read json files
        loadData();

        setContentView(R.layout.activity_main);
        //setup nav drawer
        setUpDrawer();
        //update nav drawer titles

        updateDrawer();


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        /*  When an drawer item is selected,
         *  display the corresponding BlsTemplate at PlaceHolderFragment
         */
        collapseSearchView();
        displayTemplate(position);
    }

    private void loadData(){
        BlsDataReader tmpReader = new BlsDataReader(this);
        templateDataArr = tmpReader.getTemplates();
    }

    private void setUpDrawer(){
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void updateDrawer(){
        String[] titleList = new String[templateDataArr.length];
        int[] indexList = new int[templateDataArr.length];
        int[] stepCountList = new int[templateDataArr.length];
        for (int i=0;i<templateDataArr.length;i++){
            titleList[i] = templateDataArr[i].getTitle();
            indexList[i] = i;
            if (templateDataArr[i] instanceof BlsGuide){
                stepCountList[i] = ((BlsGuide) templateDataArr[i]).getDataCount();
            }
            else{
                stepCountList[i] = 0;
            }
        }
        mNavigationDrawerFragment.updateTitleList(templateDataArr, this);
    }

    public void onSectionAttached(String title) {
        //set PlaceholderFragment title
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void collapseSearchView(){
        //collapse the searchview
        if (menu != null){
            MenuItem menuSearch = menu.findItem(R.id.search);
            if (menuSearch != null)
                menuSearch.collapseActionView();
        }
    }

    public void displayTemplate(int position) {
        int templateType = -1;
        if (templateDataArr[position] instanceof BlsGuide)
            templateType = DETAIL_FRAGMENT_TYPE_BLSGUIDE;
        else if (templateDataArr[position] instanceof BlsMap)
            templateType = DETAIL_FRAGMENT_TYPE_BLSMAP;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        //create PlaceholderFragment instance
        PlaceholderFragment currentFragment = PlaceholderFragment.newInstance(
                templateType,
                templateDataArr[position]);
        //display the template
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                .commit();
    }

    public void hideKeyBoard(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            this.menu = menu;
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        //not using this
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //real-time search
        if (!newText.equals("")){
            //not empty
            BlsTemplateFactory tmpFactory = new BlsTemplateFactory();
            BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH", null, null);

            ArrayList<Integer> tmpIndexArr = new ArrayList<Integer>();
            ArrayList<BlsTemplate> matchTemplateArr = new ArrayList<BlsTemplate>();
            for (int i=0;i<templateDataArr.length;i++){
                if (templateDataArr[i].contains(newText)){
                    tmpIndexArr.add(i);
                    matchTemplateArr.add(templateDataArr[i]);
                }
            }
            int[] templateIndexArr = new int[tmpIndexArr.size()];
            BlsTemplate[] templateArr = new BlsTemplate[matchTemplateArr.size()];
            for (int i=0;i<tmpIndexArr.size();i++){
                templateIndexArr[i] = tmpIndexArr.get(i);
                templateArr[i] = matchTemplateArr.get(i);
            }

            if (matchTemplateArr.size() != 0){
                //found match
                searchResultList.setSearchResultArr(templateArr,templateIndexArr);
            }
            else{
                //no match found
                searchResultList.setSearchResultArr(null,null);
            }

            //display search result
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlaceholderFragment currentFragment = PlaceholderFragment.newInstance(
                    DETAIL_FRAGMENT_TYPE_BLSSEARCH,
                    searchResultList);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                    .commit();
        }
        else{
            BlsTemplateFactory tmpFactory = new BlsTemplateFactory();
            BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH", null, null);
            searchResultList.setSearchResultArr(null,null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlaceholderFragment currentFragment = PlaceholderFragment.newInstance(
                    DETAIL_FRAGMENT_TYPE_BLSSEARCH,
                    searchResultList);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                    .commit();
        }

        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        //searchview expanded (someone clicked on the search icon)
        if (item.getActionView() instanceof SearchView){
            //reset the query string and focus the searchview
            SearchView mSearchView = (SearchView) item.getActionView();
            mSearchView.setQuery("",false);
            mSearchView.requestFocus();
            //show keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                    toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        //setup search result view
        BlsTemplateFactory tmpFactory = new BlsTemplateFactory();
        BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH", null, null);
        searchResultList.setSearchResultArr(null,null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlaceholderFragment currentFragment = PlaceholderFragment.newInstance(
                DETAIL_FRAGMENT_TYPE_BLSSEARCH,
                searchResultList);

        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                .commit();

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        //searchview close
        Log.d("Search","CLOSE.");
        //hide keyboard
        if (item.getActionView() instanceof SearchView){
            hideKeyBoard(item.getActionView());
        }
        //set to the first BlsTemplate
        displayTemplate(0);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */


        //constants for swipe event
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        //message constants for fragHandler
        public static final int MESSAGE_NEXT_STEP = 1;
        public static final int MESSAGE_LAST_STEP = 2;

        //current type of the template displayed, see DETAIL_* constants
        private static int fragmentType;
        //displayed template
        private static BlsTemplate fragTemplate;
        //current step in the displayed template (only for blsguide atm)
        private static int currentIndex;
        //handler instant for events (onclick) in the displayed template
        private Handler fragHandler;
        //to detect swipe event (only for blsguide atm)
        private GestureDetector gestureDetector;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int type, BlsTemplate template) {
            fragmentType = type;
            fragTemplate = template;
            currentIndex = 0;
            return new PlaceholderFragment();
        }


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // return view from the appropriate layout file
            switch(fragmentType){
                case DETAIL_FRAGMENT_TYPE_BLSGUIDE:
                    return inflater.inflate(R.layout.layout_blsguide, container, false);
                case DETAIL_FRAGMENT_TYPE_BLSMAP:
                    return inflater.inflate(R.layout.layout_blsmap, container, false);
                case DETAIL_FRAGMENT_TYPE_BLSSEARCH:
                    return inflater.inflate(R.layout.layout_blssearch, container, false);
                default:
                    return inflater.inflate(R.layout.fragment_blsdetail, container, false);
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            //create the handler
            fragHandler = new fragMessageHandler();
            //set starting view
            fragTemplate.setView(getView(), 0, getActivity(), fragHandler);
            //set the template title
            String fragTitle = fragTemplate.getDataTitle(0);
            ((MainActivity) getActivity()).onSectionAttached(fragTitle);
            //create the gesture detector instance
            gestureDetector = new GestureDetector(getActivity(),new MyGestureDetector());
            getView().setOnTouchListener(new fragOnTouchListener());
        }

        private class fragOnTouchListener implements View.OnTouchListener{
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        }

        private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    int maxStepCount;
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // Left swipe in blsguide == next step
                        if (fragTemplate instanceof BlsGuide){
                            maxStepCount = ((BlsGuide) fragTemplate).getDataCount();
                            if (currentIndex < maxStepCount - 1 && getView() != null && getActivity() != null && currentIndex >= 0){
                                fragTemplate.setView(getView(), ++currentIndex, getActivity(), fragHandler);
                                String fragTitle = fragTemplate.getDataTitle(currentIndex);
                                ((MainActivity) getActivity()).onSectionAttached(fragTitle);
                            }
                        }
                    }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // Right swipe in blsguide == last step
                        if (fragTemplate instanceof BlsGuide){
                            maxStepCount = ((BlsGuide) fragTemplate).getDataCount();
                            if (currentIndex > 0 && currentIndex < maxStepCount && getView() != null && getActivity() != null){
                                fragTemplate.setView(getView(), --currentIndex, getActivity(), fragHandler);
                                String fragTitle = fragTemplate.getDataTitle(currentIndex);
                                ((MainActivity) getActivity()).onSectionAttached(fragTitle);
                            }
                        }
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        }

        private class fragMessageHandler extends Handler{
            int maxStepCount = 0;
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case MESSAGE_NEXT_STEP: //next step click event for BlsGuide
                        maxStepCount = ((BlsGuide) fragTemplate).getDataCount();

                        if (currentIndex < maxStepCount - 1 && getView() != null && getActivity() != null && currentIndex >= 0){
                            //call the setView() method on BlsTemplate to display the next stpe
                            fragTemplate.setView(getView(), ++currentIndex, getActivity(), fragHandler);
                            //set step title
                            String fragTitle = fragTemplate.getDataTitle(currentIndex);
                            ((MainActivity) getActivity()).onSectionAttached(fragTitle);
                        }
                        break;
                    case MESSAGE_LAST_STEP: //last step click event for BlsGuide
                        maxStepCount = ((BlsGuide) fragTemplate).getDataCount();
                        if (currentIndex > 0 && currentIndex < maxStepCount && getView() != null && getActivity() != null){
                            //call the setView() method on BlsTemplate to display the last stpe
                            fragTemplate.setView(getView(), --currentIndex, getActivity(), fragHandler);
                            //set step title
                            String fragTitle = fragTemplate.getDataTitle(currentIndex);
                            ((MainActivity) getActivity()).onSectionAttached(fragTitle);
                        }
                        break;
                    default:
                }
            }
        }

    }

}
