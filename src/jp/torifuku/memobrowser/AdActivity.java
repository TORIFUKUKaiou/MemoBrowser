package jp.torifuku.memobrowser;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * AdActivity
 * 
 * @author torifuku kaiou
 * 
 */
public class AdActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TorifukuLog.methodIn();

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_ad);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			super.getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		Button button = (Button) super.findViewById(R.id.setting_button);
		button.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				TorifukuLog.methodIn();

				Intent intent = new Intent(AdActivity.this,
						MySettingActivity.class);
				AdActivity.super.startActivity(intent);

				TorifukuLog.methodOut();
			}
		});

		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TorifukuLog.methodIn();
		
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(Intent.ACTION_SEND);
            if (MainActivity.sMyBrowser != null) {
            	intent.putExtra(Intent.EXTRA_TEXT, MainActivity.sMyBrowser.getUrl());
            }
            super.startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		TorifukuLog.methodOut();
		return true;
	}
}
