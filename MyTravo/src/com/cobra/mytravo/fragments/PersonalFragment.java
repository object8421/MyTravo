package com.cobra.mytravo.fragments;

import java.util.Dictionary;
import java.util.Hashtable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.FollowersActivity;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.activities.PersonalTravelDetailActivity;
import com.cobra.mytravo.adapters.MeTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.ui.LoadingFooter;
import com.cobra.mytravo.util.ComposeBtnUtil;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PersonalFragment extends BaseFragment implements LoaderCallbacks<Cursor>{
	private static final int DISTANCE_SENSOR = 10;
	private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
	private static final String TRAVEL_STRING = "travel";
	private static final String TAG = "PersonalFragment";
	private ListView mListView;
	private MeTravelAdapter mAdapter;
	private TravelsDataHelper mDataHelper;
	private User user;
	private View headerView;
	private TextView nickname;
	private TextView gender;
	private TextView signature;
	private Button focusButton;
	private ImageView avatar;
	private TextView travelCounTextView;
	private TextView followingCountTextView;
	private TextView favoriteCountTextView;
	private View followingView;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, null);
		mListView = (ListView) view.findViewById(R.id.me_listview);
		mDataHelper = new TravelsDataHelper(getActivity(), AppData.getUserId());
		headerView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_head_personal, null);
		avatar = (ImageView) headerView.findViewById(R.id.avatar);
		if(AppData.getSex().equals("女"))
			avatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_listitem_shot_red));
		else {
			avatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_listitem_shot_blue));
		}
		nickname = (TextView) headerView.findViewById(R.id.tv_nickname);
		gender = (TextView) headerView.findViewById(R.id.tv_gender);
		signature = (TextView) headerView.findViewById(R.id.tv_signature);
		
		travelCounTextView = (TextView) headerView.findViewById(R.id.tv_travel_count);
		followingCountTextView = (TextView) headerView.findViewById(R.id.tv_folower_count);
		favoriteCountTextView = (TextView) headerView.findViewById(R.id.tv_favorite_count);
		followingView = headerView.findViewById(R.id.ll_followings);
		gender.setText(AppData.getSex());
		signature.setText(AppData.getSignature());
		followingView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent followersIntent = new Intent(getActivity(), FollowersActivity.class);
				getActivity().startActivity(followersIntent);
			}
		});
		
		mListView.addHeaderView(headerView);
		mAdapter = new MeTravelAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position > 0){
					Intent intent = new Intent();
					intent.setClass(getActivity(), PersonalTravelDetailActivity.class);
					Travel travel = mAdapter.getItem(position - mListView.getHeaderViewsCount());
					Bundle bundle = new Bundle();
					bundle.putSerializable(TRAVEL_STRING, travel);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				
			}
		});
		getUser();
		return view;
	}
	private void getUser(){
		executeRequest(new GsonRequest<User.UserLoginRequestData>(String.format(AppData.HOST_IP+"user/login?user_type=travo&email=%1$s&password=%2$s",
        		AppData.getEmail(),AppData.getPassword()), User.UserLoginRequestData.class, null,
                new Response.Listener<User.UserLoginRequestData>() {
                    @Override
                    public void onResponse(final User.UserLoginRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                               if(rsp_code == MyServerMessage.SUCCESS){
                            	   user = requestData.getUser();
                            	   if(user != null){
                            		   travelCounTextView.setText(String.valueOf(user.getTravel_qty()));
                                       followingCountTextView.setText(String.valueOf(user.getFollower_qty()));
                               			favoriteCountTextView.setText(String.valueOf(user.getFavorite_travel_qty()));
                               			nickname.setText(user.getNickname());
                               			signature.setText(user.getSignature());
                               			
                               			RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+user.getFace_path(), RequestManager
                                               .getImageListener(avatar, mDefaultImageDrawable, mDefaultImageDrawable));
                            	   AppData.setFacePath(user.getFace_path());
                            	   AppData.setIdToken(user.getToken());
                            	   AppData.setSignature(user.getSignature());
                            	   AppData.setNickname(user.getNickname());
                            	   }
                            	   
                                  
                               }
                               else{
                            	   Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                           Toast.LENGTH_SHORT).show();
                               }
                                
                               
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                 Toast.LENGTH_SHORT).show();
                        
                    }
                }));
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((MainActivity)getActivity()).getComposeButton().setVisibility(View.VISIBLE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}
}
