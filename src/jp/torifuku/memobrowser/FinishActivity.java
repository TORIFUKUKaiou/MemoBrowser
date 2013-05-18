package jp.torifuku.memobrowser;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * FinishActivity
 * @author torifuku kaiou
 *
 */
public class FinishActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TorifukuLog.methodIn();
		
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.finish);
		super.setTitle(R.string.finish_question);
		
		final View yesButton = super.findViewById(R.id.yes_button);
		final View noButton = super.findViewById(R.id.no_button);
		OnClickListener l = new OnClickListener() {
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				TorifukuLog.methodIn();
				
				if (v.equals(yesButton)) {
					FinishActivity.super.finish();
				} else if (v.equals(noButton)) {
					Intent intent = new Intent(FinishActivity.this, MainActivity.class);
					FinishActivity.super.startActivity(intent);
				} else {
					// nop
				}
				
				TorifukuLog.methodOut();
			}};
		yesButton.setOnClickListener(l);
		noButton.setOnClickListener(l);
		
		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		TorifukuLog.methodIn();
		
		super.onPause();
		super.finish();
		
		TorifukuLog.methodOut();
	}
}
