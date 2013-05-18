package jp.torifuku.memobrowser;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.app.Activity;
import android.os.Bundle;

/**
 * AdActivity
 * @author torifuku kaiou
 *
 */
public class AdActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TorifukuLog.methodIn();
		
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.ad_layout);
		
		TorifukuLog.methodOut();
	}
}
