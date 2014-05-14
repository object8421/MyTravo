package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.models.Spot;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class SpotDetailActivity extends Activity {
	private static final String TAG = "SpotDetailActivity";
	private static final String SPOT_TAG = "spot";
	private Spot spot;
	private ImageView imageView;
	private TextView information,price,advice;
	private BitmapDrawable mDefaultBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spot_detail);
		
		mDefaultBitmap = (BitmapDrawable)getResources().getDrawable(R.drawable.default_avatar);
		spot = (Spot) getIntent().getSerializableExtra(SPOT_TAG);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(),spot.getSpot_name());
		imageView = (ImageView) findViewById(R.id.imageView1);
		information = (TextView) findViewById(R.id.textView2);
		price = (TextView) findViewById(R.id.textView4);
		advice = (TextView) findViewById(R.id.textView6);
		information.setText(spot.getBrief_information());
		price.setText(spot.getTicket());
		advice.setText(spot.getAttention());
		
		RequestManager.loadImage(spot.getCover_url(),
                RequestManager.getImageListener(imageView, mDefaultBitmap,
                		mDefaultBitmap));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.spot_detail, menu);
		return true;
	}

}
