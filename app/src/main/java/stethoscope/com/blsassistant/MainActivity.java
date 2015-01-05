package stethoscope.com.blsassistant;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.SearchView;
import java.io.IOException;
import java.util.ArrayList;
import stethoscope.com.blsassistant.blsmodel.BlsDataReader;
import stethoscope.com.blsassistant.blsmodel.BlsGuide;
import stethoscope.com.blsassistant.blsmodel.BlsHome;
import stethoscope.com.blsassistant.blsmodel.BlsMap;
import stethoscope.com.blsassistant.blsmodel.BlsSearch;
import stethoscope.com.blsassistant.blsmodel.BlsTemplate;
import stethoscope.com.blsassistant.blsmodel.BlsTemplateFactory;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener,
                    SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl{


    /*
     *  Constants
     */
    //DETAIL_* constants are used to specify the current template type in placeholder fragment
    public static final int DETAIL_FRAGMENT_TYPE_BLSHOME = 0;
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

    private MediaPlayer mPlayer = null;
    private VideoControllerView mController = null;
    private SurfaceHolder mHolder = null;
    private String mVideoName = null;

    private boolean onSearchMode = false;

    public static PlaceholderFragment currentFragment = null;

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
    protected void onPause() {
        if (mPlayer != null){
            Log.d("Surface","onPause release video player");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mController = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mVideoName != null){
            AssetFileDescriptor afd = getResources().openRawResourceFd(getResources().getIdentifier(mVideoName, "raw", getPackageName()));
            Log.d("Surface","onResume set video player");
            try
            {
                SurfaceView videoSurface = (SurfaceView) findViewById(R.id.guide_video_surface);
                mPlayer = new MediaPlayer();
                mController = new VideoControllerView(this);
                videoSurface.setOnTouchListener(new VideoOnTouchListener(mController));
                mHolder = videoSurface.getHolder();
                mPlayer.reset();
                //player.setDisplay(videoHolder);
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                mPlayer.prepare();
                mController.setMediaPlayer(this);
                mController.setAnchorView((FrameLayout) findViewById(R.id.guide_video));
                mController.show(2000);
                mPlayer.seekTo(1);
                //mPlayer.start();
                afd.close();
                if (mHolder != null)
                    mPlayer.setDisplay(mHolder);
            }
            catch (IllegalArgumentException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
            catch (IllegalStateException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
            catch (IOException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
        }

        super.onResume();
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

    public void setSelectedItem(int index){
        mNavigationDrawerFragment.setSelectedItem(index);
    }

    public void displayTemplate(int index) {
        //index: the displayed item's index in templateDataArr
        int templateType = -1;
        if (templateDataArr[index] instanceof BlsGuide)
            templateType = DETAIL_FRAGMENT_TYPE_BLSGUIDE;
        else if (templateDataArr[index] instanceof BlsMap)
            templateType = DETAIL_FRAGMENT_TYPE_BLSMAP;
        else if (templateDataArr[index] instanceof BlsHome)
            templateType = DETAIL_FRAGMENT_TYPE_BLSHOME;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        //create PlaceholderFragment instance
        currentFragment = PlaceholderFragment.newInstance(
                templateType,
                templateDataArr[index]);
        //display the template
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                .commit();
        setSelectedItem(index);

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
            BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH",null, null, null);

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
            currentFragment = PlaceholderFragment.newInstance(
                    DETAIL_FRAGMENT_TYPE_BLSSEARCH,
                    searchResultList);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                    .commit();
        }
        else{
            BlsTemplateFactory tmpFactory = new BlsTemplateFactory();
            BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH",null, null, null);
            searchResultList.setSearchResultArr(null,null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            currentFragment = PlaceholderFragment.newInstance(
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
            onSearchMode = true;
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
        BlsSearch searchResultList = (BlsSearch) tmpFactory.getTemplate("SEARCH",null, null, null);
        searchResultList.setSearchResultArr(null,null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        currentFragment = PlaceholderFragment.newInstance(
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
        if (onSearchMode)
            displayTemplate(0);
        return true;
    }

    public void onlyCollapse(){
        onSearchMode = false;
        collapseSearchView();
    }

    //video functions starts
    public void setVideoPlayer(MediaPlayer player, VideoControllerView controller, SurfaceHolder holder, String name){
        mVideoName = name;
        if (mController == null)
            mController = controller;
        if (mHolder == null)
            mHolder = holder;
        if (mPlayer == null){
            SurfaceView mVideo = (SurfaceView) findViewById(R.id.guide_video_surface);
            mVideo.setVisibility(View.VISIBLE);
            mPlayer = player;
        }
        else{
            AssetFileDescriptor afd = getResources().openRawResourceFd(getResources().getIdentifier(mVideoName, "raw", getPackageName()));
            Log.d("MEDIA","set video player");
            try
            {
                mPlayer.reset();
                mPlayer.setDisplay(mHolder);
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                mPlayer.prepare();
                mController.setMediaPlayer(this);
                mController.setAnchorView((FrameLayout) findViewById(R.id.guide_video));
                mController.show(2000);
                mPlayer.seekTo(1);
                //mPlayer.start();
                afd.close();
            }
            catch (IllegalArgumentException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
            catch (IllegalStateException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
            catch (IOException e)
            {
                //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
            }
        }


    }

    public MediaPlayer getMPlayer(){
        return mPlayer;
    }

    private class VideoOnTouchListener implements View.OnTouchListener{
        VideoControllerView controller;

        public VideoOnTouchListener(VideoControllerView controller){
            this.controller = controller;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            controller.show();
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mPlayer == null){
            SurfaceView videoSurface = (SurfaceView) findViewById(getResources().getIdentifier("guide_video_surface", "id", getPackageName()));
            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            mPlayer = new MediaPlayer();
            mController = new VideoControllerView(this);
            videoSurface.setOnTouchListener(new VideoOnTouchListener(mController));
        }
        mPlayer.setDisplay(holder);
        AssetFileDescriptor afd = getResources().openRawResourceFd(getResources().getIdentifier(mVideoName, "raw", getPackageName()));
        Log.d("MEDIA","SURFACE_CREATED");
        try
        {
            mPlayer.reset();
            //player.setDisplay(videoHolder);
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mPlayer.prepare();
            mController.setMediaPlayer(this);
            mController.setAnchorView((FrameLayout) findViewById(R.id.guide_video));
            mController.show(2000);
            mPlayer.seekTo(1);
            //mPlayer.start();
            afd.close();
        }
        catch (IllegalArgumentException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
        catch (IllegalStateException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Surface","changed");
        mHolder = holder;
        if (mPlayer != null){
            mPlayer.setDisplay(mHolder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Surface","destroyed");
        mPlayer = null;
        mController = null;
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public int getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null)
            return mPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
        //start videoactivity here
        mPlayer.stop();
        mPlayer.reset();
        Intent videoIntent = new Intent(this, VideoPlayerActivity.class);
        videoIntent.putExtra("VIDEO_RESOURCE_ID",getResources().getIdentifier(mVideoName, "raw", getPackageName()));
        startActivity(videoIntent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
    //video functions ends
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final int ANIMATION_CHANGE_STEP_DURATION = 50;

        //constants for swipe event
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        //message constants for fragHandler
        public static final int MESSAGE_NEXT_STEP = 1;
        public static final int MESSAGE_LAST_STEP = 2;
        public static final int MESSAGE_VIDEO_FULLSCREEN = 3;

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
                case DETAIL_FRAGMENT_TYPE_BLSHOME:
                    return inflater.inflate(R.layout.layout_blshome, container, false);
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
            if (getView() != null)
                getView().setOnTouchListener(new fragOnTouchListener());
        }

        public BlsTemplate getFragTemplate(){
            return fragTemplate;
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
                                View indicatorView = getActivity().findViewById(R.id.guide_step_indicator);
                                ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(
                                        indicatorView, "translationX", indicatorView.getX(), indicatorView.getX() + indicatorView.getWidth());
                                objectAnimator.setDuration(ANIMATION_CHANGE_STEP_DURATION);
                                objectAnimator.start();
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
                                View indicatorView = getActivity().findViewById(R.id.guide_step_indicator);
                                ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(
                                        indicatorView, "translationX", indicatorView.getX(), indicatorView.getX() - indicatorView.getWidth());
                                objectAnimator.setDuration(ANIMATION_CHANGE_STEP_DURATION);
                                objectAnimator.start();
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
                            View indicatorView = getActivity().findViewById(R.id.guide_step_indicator);
                            ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(
                                    indicatorView, "translationX", indicatorView.getX(), indicatorView.getX() + indicatorView.getWidth());
                            objectAnimator.setDuration(ANIMATION_CHANGE_STEP_DURATION);
                            objectAnimator.start();
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
                            View indicatorView = getActivity().findViewById(R.id.guide_step_indicator);
                            ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(
                                    indicatorView, "translationX", indicatorView.getX(), indicatorView.getX() - indicatorView.getWidth());
                            objectAnimator.setDuration(ANIMATION_CHANGE_STEP_DURATION);
                            objectAnimator.start();
                        }
                        break;
                    case MESSAGE_VIDEO_FULLSCREEN:
                        /*
                        DisplayMetrics metrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        RelativeLayout.LayoutParams params =
                                (RelativeLayout.LayoutParams) getActivity().findViewById(R.id.guide_video).getLayoutParams();
                        params.width =  metrics.widthPixels;
                        params.height = metrics.heightPixels;
                        Log.d("Params","Width: " + metrics.widthPixels + ", Height: " + metrics.heightPixels);
                        params.setMargins(0, 0, 0, 0);
                        getActivity().findViewById(R.id.guide_video).setLayoutParams(params);*/
                        break;
                    default:
                }
            }
        }

    }

}
