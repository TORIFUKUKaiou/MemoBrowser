package jp.torifuku.memobrowser;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * MySettingActivity
 * 
 * @author torifuku kaiou
 * 
 */
public class MySettingActivity extends PreferenceActivity {
	private EditTextPreference mEditTextPreference = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		TorifukuLog.methodIn();

		super.onCreate(savedInstanceState);
		super.addPreferencesFromResource(R.xml.setting);

		/** HOME URL */
		mEditTextPreference = (EditTextPreference) super.getPreferenceScreen()
				.findPreference("home_url");
		String url = mEditTextPreference.getText();
		mEditTextPreference.setSummary(url);
		mEditTextPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * android.preference.Preference.OnPreferenceChangeListener
					 * #onPreferenceChange(android.preference.Preference,
					 * java.lang.Object)
					 */
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						TorifukuLog.methodIn();
						MySettingActivity.this.mEditTextPreference
								.setSummary((CharSequence) newValue);
						TorifukuLog.methodOut();
						return true;
					}
				});

		/** Capture Quality */
		final ListPreference captureQualityListPreference = (ListPreference) super
				.getPreferenceManager().findPreference("capture_quality");
		captureQualityListPreference.setSummary(captureQualityListPreference.getEntry());
		captureQualityListPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * android.preference.Preference.OnPreferenceChangeListener
					 * #onPreferenceChange(android.preference.Preference,
					 * java.lang.Object)
					 */
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						TorifukuLog.methodIn();

						/** return true;するまで値は変更されていないので、配列から取り出す。 */
						/** ちなみに、return false;すると値は変更されない。*/
						if (captureQualityListPreference.equals(preference)) {
							preference.setSummary(captureQualityListPreference.getEntries()[Integer.parseInt(newValue.toString())]);
						}

						TorifukuLog.methodOut();
						return true;
					}
				});
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			super.getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		super.setResult(Activity.RESULT_OK);

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
