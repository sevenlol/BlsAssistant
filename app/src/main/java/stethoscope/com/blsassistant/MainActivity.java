package stethoscope.com.blsassistant;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import stethoscope.com.blsassistant.blsmodel.BlsDataReader;
import stethoscope.com.blsassistant.blsmodel.BlsGuide;
import stethoscope.com.blsassistant.blsmodel.BlsMap;
import stethoscope.com.blsassistant.blsmodel.BlsTemplate;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    /*
     *  Constants
     */
    public static final int DETAIL_FRAGMENT_TYPE_BLSGUIDE = 1;
    public static final int DETAIL_FRAGMENT_TYPE_BLSMAP = 2;
    //public static final String FRAGMENT_TITLE_ARRAY_FLAG = "fragment_title_array";
    public static final String CURRENT_FRAGMENT = "current_fragment";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //BlsTemplate data array
    private BlsTemplate[] templateDataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //read json files
        BlsDataReader tmpReader = new BlsDataReader(this);
        templateDataArr = tmpReader.getTemplates();

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //update nav drawer titles
        String[] titleList = new String[templateDataArr.length];
        for (int i=0;i<templateDataArr.length;i++)
            titleList[i] = templateDataArr[i].getTitle();
        mNavigationDrawerFragment.updateTitleList(titleList);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        int templateType = -1;
        if (templateDataArr[position] instanceof BlsGuide)
            templateType = DETAIL_FRAGMENT_TYPE_BLSGUIDE;
        else if (templateDataArr[position] instanceof BlsMap)
            templateType = DETAIL_FRAGMENT_TYPE_BLSMAP;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlaceholderFragment currentFragment = PlaceholderFragment.newInstance(
                position + 1,
                templateType,
                templateDataArr[position]);
        //new BlsTemplateFactory().getTemplate("TEST_GUIDE", null, null)
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment, CURRENT_FRAGMENT)
                .commit();
    }

    public void onSectionAttached(String title) {
        //set detail fragment title
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_FRAGMENT_TYPE = "fragment_type";

        //message constants
        public static final int MESSAGE_NEXT_STEP = 1;
        public static final int MESSAGE_LAST_STEP = 2;

        private static int fragmentType;
        private static BlsTemplate fragTemplate;
        private static int currentIndex;

        private Handler fragHandler;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, int type, BlsTemplate template) {
            fragmentType = type;
            fragTemplate = template;
            currentIndex = 0;


            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_FRAGMENT_TYPE, type);
            fragment.setArguments(args);
            return fragment;
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
                default:
                    return inflater.inflate(R.layout.fragment_blsdetail, container, false);
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((MainActivity) activity).onSectionAttached(
            //        getArguments().getInt(ARG_SECTION_NUMBER),getArguments().getInt(ARG_FRAGMENT_TYPE));
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);


            fragHandler = new fragMessageHandler();
            //set starting view
            fragTemplate.setView(getView(), 0, getActivity(), fragHandler);
            String fragTitle = fragTemplate.getDataTitle(0);
            ((MainActivity) getActivity()).onSectionAttached(fragTitle);
        }

        private class fragMessageHandler extends Handler{
            int maxStepCount = 0;
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case MESSAGE_NEXT_STEP: //next step click event for BlsGuide
                        maxStepCount = ((BlsGuide) fragTemplate).getDataCount();
                        if (currentIndex < maxStepCount - 1 && getView() != null && getActivity() != null && currentIndex >= 0){
                            fragTemplate.setView(getView(), ++currentIndex, getActivity(), fragHandler);
                            String fragTitle = fragTemplate.getDataTitle(currentIndex);
                            ((MainActivity) getActivity()).onSectionAttached(fragTitle);
                        }


                        break;
                    case MESSAGE_LAST_STEP: //last step click event for BlsGuide
                        maxStepCount = ((BlsGuide) fragTemplate).getDataCount();
                        if (currentIndex > 0 && currentIndex < maxStepCount && getView() != null && getActivity() != null){
                            fragTemplate.setView(getView(), --currentIndex, getActivity(), fragHandler);
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
