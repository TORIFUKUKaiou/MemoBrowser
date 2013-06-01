package jp.torifuku.memobrowser;

import com.example.android.actionbarcompat.ActionBarActivity;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * MainActivity
 * 
 * @author torifuku kaiou
 *
 */
public class MainActivity extends ActionBarActivity {
	static private final int BOOKMARK_REQUEST_CODE = 1;
	static private final int SETTING_REQUEST_CODE = 10;
	
	private TorifukuBrowserInterface mMyBrowser;
	private EditText mEditText = null;
	private LinearLayout mEditTextLinearLayout = null;
	private ImageView mSurfingImageView = null;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TorifukuLog.init(this);
		TorifukuLog.methodIn();
		
		super.onCreate(savedInstanceState);
		this.setContentView();
		
		TorifukuLog.methodOut();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		TorifukuLog.methodIn();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		TorifukuLog.methodOut();
		return super.onCreateOptionsMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		TorifukuLog.methodIn();
		
		long now = System.currentTimeMillis();
		int msgId = 0;
		if ((now % 2) == 0) {
			msgId = R.string.byebye_msg_1;
		} else {
			msgId = R.string.byebye_msg_2;
		}
		Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
		
		super.onDestroy();
		if (mMyBrowser != null) {
			mMyBrowser.stop();
		}
		
		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TorifukuLog.methodIn();
		switch (item.getItemId()) {
		case R.id.action_back:
			if (mMyBrowser != null) {
				mMyBrowser.back();
			}
			break;
		case R.id.action_bookmark:
			if (mMyBrowser != null) {
				Intent intent = mMyBrowser.bookmark();
				super.startActivityForResult(intent, MainActivity.BOOKMARK_REQUEST_CODE);
			}
			break;
		case R.id.action_memo:
			memo();
			break;
		case R.id.action_close:
			super.finish();
			break;
		case R.id.action_settings:
			if (mMyBrowser != null) {
				Intent intent = mMyBrowser.setting();
				super.startActivityForResult(intent, MainActivity.SETTING_REQUEST_CODE);
			}
			break;
		case R.id.action_reload:
			if (mMyBrowser != null) {
				mMyBrowser.reload();
			}
			break;
		case R.id.action_home:
			if (mMyBrowser != null) {
				mMyBrowser.home();
			}
			break;
		case R.id.action_capture:
			capture();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		TorifukuLog.methodOut();
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		TorifukuLog.methodIn();
		boolean keyHandled = false;
		if (mMyBrowser != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				keyHandled = mMyBrowser.back();
				if (!keyHandled) {
					finishConf();
					keyHandled = true;
				}
			}
		}
		TorifukuLog.d("keyHandled: " + keyHandled);
		if (keyHandled) {
			TorifukuLog.methodOut();
			return true;
		}	
		TorifukuLog.methodOut();
		return super.onKeyDown(keyCode, event);
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TorifukuLog.methodIn();
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == MainActivity.BOOKMARK_REQUEST_CODE) {
			String url = data.getStringExtra("url");
			if (mMyBrowser != null) {
				mMyBrowser.jump(url);
			}
		} else if (requestCode == MainActivity.SETTING_REQUEST_CODE) {
			if (mMyBrowser != null) {
				mMyBrowser.setJavaScriptEnabled();
			}
		}
		
		TorifukuLog.methodOut();
	}
	
	/**************************************************************/
	/**************************************************************/
	/**************************************************************/
	/**
	 * setContentView
	 */
	private void setContentView() {
		TorifukuLog.methodIn();
		
		//super.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.setContentView(R.layout.activity_main);
		
		/** UI */
		WebView webView = (WebView) super.findViewById(R.id.webView);
		mMyBrowser = new TorifukuBrowser(webView);
		Intent intent = super.getIntent();
		String url = null;
		if (intent == null) {
		} else {
			if (intent.getAction().equals(Intent.ACTION_SEND)) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					url = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
				}
			}
		}
		if (url == null) {
			mMyBrowser.start();
		} else {
			mMyBrowser.jump(url);
		}
		
		
		// EditText
		mEditText = (EditText) super.findViewById(R.id.editText);
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				TorifukuLog.methodIn();
				if (actionId != EditorInfo.IME_ACTION_DONE) {
					return false;
				}
				
				MainActivity.this.inputDone();
				
				TorifukuLog.methodOut();
				return true;
			}});
		
		// LinearLayout
		mEditTextLinearLayout = (LinearLayout) super.findViewById(R.id.editTextLinearLayout);
		
		// Done Button
		Button doneButton = (Button) super.findViewById(R.id.done_button);
		doneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TorifukuLog.methodIn();
				MainActivity.this.inputDone();
				TorifukuLog.methodOut();
			}});
		
		// Cancel Button
		Button cancelButton = (Button) super.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TorifukuLog.methodIn();
				MainActivity.this.inputCancel();
				TorifukuLog.methodOut();
			}});
		
		// ImageView
		mSurfingImageView = (ImageView) super.findViewById(R.id.surfing_imageView);
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * share
	 * @param msg
	 */
	private void share(String msg) {
		TorifukuLog.methodIn();
		
		if (msg == null) {
			return;
		}
		if (msg.length() <= 0) {
			return;
		}
		
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, msg);

		MainActivity.super.startActivity(Intent.createChooser(intent, super.getText(R.string.please_select)));

		TorifukuLog.methodOut();
	}
	
	/**
	 * memo
	 */
	private void memo() {
		TorifukuLog.methodIn();
		
		if (mEditTextLinearLayout == null) {
			return;
		}
		
		// show edit text
		int visibility = mEditTextLinearLayout.getVisibility();
		if (visibility == View.GONE) {
			mEditTextLinearLayout.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
			mEditTextLinearLayout.setAnimation(animation);
		}
		
		Animation surfingAnimation = AnimationUtils.loadAnimation(this, R.anim.surfing);
		surfingAnimation.setDuration(1800);
		mSurfingImageView.setVisibility(View.VISIBLE);
		mSurfingImageView.setAnimation(surfingAnimation);		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				TorifukuLog.methodIn();
				
				MainActivity.this.mSurfingImageView.setVisibility(View.GONE);
				
				TorifukuLog.methodOut();
			}}, 1001);
		
		/*
		AlertDialog.Builder builder = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			builder = new AlertDialog.Builder(this);
		}
		builder.setMessage(R.string.copy_question);
		builder.setNegativeButton(android.R.string.no, null);
		builder.setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				TorifukuLog.methodIn();
				if (arg1 != DialogInterface.BUTTON_POSITIVE) {
					return;
				}
				if (MainActivity.this.mEditText == null) {
					return;
				}
				if (MainActivity.this.mMyBrowser == null) {
					return;
				}
				
				MainActivity.this.mEditText.setText(MainActivity.this.mMyBrowser.getUrl());
				TorifukuLog.methodOut();
			}});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				TorifukuLog.methodIn();
				if (MainActivity.this.mEditText != null) {
					MainActivity.this.mEditText.setVisibility(View.VISIBLE);
				}
				TorifukuLog.methodOut();
			}});
		
		dialog.show();
		*/
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * inputDone
	 */
	private void inputDone() {
		TorifukuLog.methodIn();
		
		if (mEditText == null) {
			return;
		}
		
		String msg = mEditText.getEditableText().toString();
		if (msg == null) {
			return;
		}
		if (msg.length() <= 0) {
			return;
		}
		
		share(msg);
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * inputCancel
	 */
	private void inputCancel() {
		TorifukuLog.methodIn();
		
		if (mEditTextLinearLayout == null) {
			return;
		}
		if (mEditText == null) {
			return;
		}
		
		/** Close ime */
		InputMethodManager imm = (InputMethodManager) super.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
				
		/** hide EditText LinearLayout */
		Animation gone_animation = AnimationUtils.loadAnimation(this, R.anim.right2left);
		mEditTextLinearLayout.setAnimation(gone_animation);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				TorifukuLog.methodIn();
				mEditTextLinearLayout.setVisibility(View.GONE);
				TorifukuLog.methodOut();
			}}, 1001);
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * capture
	 */
	private void capture() {
		TorifukuLog.methodIn();
		
		if (mMyBrowser != null) {
			mSurfingImageView.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.surfing);
			mSurfingImageView.setAnimation(animation);
			
			final long now = System.currentTimeMillis();
			mMyBrowser.capture(new TorifukuBrowserInterface.CaptureListener() {
				/* (non-Javadoc)
				 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface.CaptureListener#complete()
				 */
				@Override
				public void complete() {
					TorifukuLog.methodIn();
					long diff = System.currentTimeMillis() - now;
					long delayMillis = 2000 - diff;
					
					if (delayMillis <= 0) {
						MainActivity.this.mSurfingImageView.setVisibility(View.GONE);
					} else {
						new Handler().postDelayed(new Runnable() {
							/* (non-Javadoc)
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								TorifukuLog.methodIn();
								MainActivity.this.mSurfingImageView.setVisibility(View.GONE);
								TorifukuLog.methodOut();
							}}, delayMillis);
					}

					TorifukuLog.methodOut();
				}});
		}

		TorifukuLog.methodOut();
	}

	/**
	 * finishConf
	 */
	private void finishConf() {
		TorifukuLog.methodIn();
		
		AlertDialog.Builder builder = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			builder = new AlertDialog.Builder(this);
		}
		builder.setMessage(R.string.finish_question);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setNeutralButton(R.string.show_ad, new android.content.DialogInterface.OnClickListener() {
			/* (non-Javadoc)
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				TorifukuLog.methodIn();
				Intent intent = new Intent(MainActivity.this, AdActivity.class);
				MainActivity.super.startActivity(intent);
				TorifukuLog.methodOut();
			}});
		builder.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
			/* (non-Javadoc)
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				TorifukuLog.methodIn();
				MainActivity.super.finish();
				TorifukuLog.methodOut();
			}});
		builder.show();

		TorifukuLog.methodOut();
	}
}
