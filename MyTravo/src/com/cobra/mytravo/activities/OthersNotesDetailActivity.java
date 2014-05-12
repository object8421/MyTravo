package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.id;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ScreenUtil;
import com.cobra.mytravo.models.Note;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.ImageView;

public class OthersNotesDetailActivity extends Activity {
	private String TAG = "OthersNotesDetailActivity";
	private ImageView mImageView;
	private Bitmap mBitmap;
	private Note note;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	private int maxWidth, maxHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_others_notes_detail);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		maxWidth = ScreenUtil.getScreenWidth(this);
		maxHeight = ScreenUtil.getScreenHeight(this);
		note = (Note) getIntent().getSerializableExtra("note");
		if(note != null){
			RequestManager.loadImage("http://travo-note-pic.oss-cn-hangzhou.aliyuncs.com/"+note.getImage_path(), 
					RequestManager.getImageListener(mImageView, mDefaultImageDrawable, mDefaultImageDrawable), maxWidth, (int)(maxHeight*0.8));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.others_notes_detail, menu);
		return true;
	}

}
