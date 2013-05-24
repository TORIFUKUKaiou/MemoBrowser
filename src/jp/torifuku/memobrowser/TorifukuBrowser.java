package jp.torifuku.memobrowser;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * TorifukuBrowser
 * @author torifuku kaiou
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class TorifukuBrowser implements TorifukuBrowserInterface {
	private WebView mWebView = null;
	private ProgressDialog mProgressDialog = null;
	private Context mContext = null;

	TorifukuBrowser(WebView webView) {
		super();
		mWebView = webView;
		mContext = mWebView.getContext();
		
		init();
	}

	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#start()
	 */
	@Override
	public void start() {
		TorifukuLog.methodIn();
		
		String url = this.getHomeUrl();
		mWebView.loadUrl(url);
		/** ProgressDialog */
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
		
		TorifukuLog.methodOut();
	}

	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#stop()
	 */
	@Override
	public void stop() {
		TorifukuLog.methodIn();
		
		mWebView.stopLoading();
		mWebView.destroy();
		//Intent intent = new Intent(mContext, FinishActivity.class);
		//mContext.startActivity(intent);
		
		TorifukuLog.methodOut();
	}

	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#back()
	 */
	@Override
	public boolean back() {
		TorifukuLog.methodIn();
		boolean ret = mWebView.canGoBack();
		if (ret) {
			mWebView.goBack();
		}
		TorifukuLog.methodOut();
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#bookmark()
	 */
	@Override
	public Intent bookmark() {
		TorifukuLog.methodIn();
		
		Intent intent = new Intent(this.mContext, BookmarkListActivity.class);
		intent.putExtra("current url", mWebView.getUrl());
		intent.putExtra("title", mWebView.getTitle());
		
		TorifukuLog.methodOut();
		return intent;
	}

	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#home()
	 */
	@Override
	public void home() {
		TorifukuLog.methodIn();
		
		String url = this.getHomeUrl();
		mWebView.loadUrl(url);
		
		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#setting()
	 */
	public Intent setting() {
		TorifukuLog.methodIn();
		
		Intent intent = new Intent(mContext, MySettingActivity.class);
		//mContext.startActivity(intent);
		
		TorifukuLog.methodOut();
		return intent;
	}

	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#update()
	 */
	@Override
	public void reload() {
		TorifukuLog.methodIn();
		
		mWebView.reload();
		
		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#jump(java.lang.String)
	 */
	public void jump(String url) {
		TorifukuLog.methodIn();
		
		TorifukuLog.i("url: " + url);
		mWebView.loadUrl(url);
		
		TorifukuLog.methodOut();
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#getUrl()
	 */
	@Override
	public String getUrl() {
		return mWebView.getUrl();
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#capture()
	 */
	@Override
	public void capture(CaptureListener l) {
		TorifukuLog.methodIn();
		
		new CaptureTask(l).execute();
		
		TorifukuLog.methodIn();
	}
	
	/* (non-Javadoc)
	 * @see jp.torifuku.memobrowser.TorifukuBrowserInterface#setJavaScriptEnabled()
	 */
	@Override
	public void setJavaScriptEnabled() {
		TorifukuLog.methodIn();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean enable = sharedPref.getBoolean("enable_java_script", true);
		
		TorifukuLog.i("enable: " + enable);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(enable);
		
		TorifukuLog.methodOut();
	}
	
	/********************************************************************/
	/********************************************************************/
	/********************************************************************/
	/**
	 * init
	 */
	private void init() {
		TorifukuLog.methodIn();
		
		setJavaScriptEnabled();
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				TorifukuLog.methodIn();
				if (progress >= 100) {
					if (TorifukuBrowser.this.mProgressDialog != null) {
						TorifukuBrowser.this.mProgressDialog.setProgress(progress);
						TorifukuBrowser.this.mProgressDialog.dismiss();
						TorifukuBrowser.this.mProgressDialog = null;
					}
					return;
				}
				if (TorifukuBrowser.this.mProgressDialog == null) {
					if (progress > 0) {
						TorifukuBrowser.this.mProgressDialog = new ProgressDialog(TorifukuBrowser.this.mContext);
						//TorifukuBrowser.this.mProgressDialog.setCancelable(false);
						TorifukuBrowser.this.mProgressDialog.setCanceledOnTouchOutside(false);
						TorifukuBrowser.this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						TorifukuBrowser.this.mProgressDialog.setProgress(progress);
						TorifukuBrowser.this.mProgressDialog.show();
					}
				} else {
					TorifukuBrowser.this.mProgressDialog.setMessage(TorifukuBrowser.this.mWebView.getTitle());
					TorifukuBrowser.this.mProgressDialog.setProgress(progress);
				}
				TorifukuLog.methodOut();
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
				TorifukuLog.methodIn();
				TorifukuLog.methodOut();
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * getHomeUrl
	 * @return
	 */
	private String getHomeUrl() {
		TorifukuLog.methodIn();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		//SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
		String defaultUrl = mContext.getString(R.string.home_url_default);
		String url = sharedPref.getString("home_url", defaultUrl);
		
		TorifukuLog.methodOut();
		return url;
	}
	
	/**
	 * viewImage
	 * @param uri
	 */
	private void viewImage(String uri) {
		TorifukuLog.methodIn();
		if (uri == null) {
			Toast.makeText(mContext, R.string.cannot_view, Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(uri));
		PackageManager pm = mContext.getPackageManager();
		if (pm.queryIntentActivities(intent, 0).isEmpty()) {
			Toast.makeText(mContext, R.string.cannot_view, Toast.LENGTH_LONG).show();
			return;
		}
		
		mContext.startActivity(intent);
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * CaptureTaskResult
	 * @author torifuku kaiou
	 *
	 */
	private enum CaptureTaskResult {
		Success,
		Failed,
		NoSdcard
	}
	
	/**
	 * CaptureTask
	 * @author torifuku kaiou
	 *
	 */
	private class CaptureTask extends AsyncTask<Void, Void, CaptureTaskResult> {
		private ProgressDialog mProgressDialog = null;
		private String mFilePath = null;
		private String mUri = null;
		private CaptureListener mCaptureListener = null;
		
		private CaptureTask(CaptureListener l) {
			mCaptureListener = l;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			TorifukuLog.methodIn();
			
			this.mProgressDialog = new ProgressDialog(TorifukuBrowser.this.mContext);
			this.mProgressDialog.setTitle(TorifukuBrowser.this.mWebView.getTitle());
			this.mProgressDialog.setCancelable(false);
			this.mProgressDialog.setCanceledOnTouchOutside(false);
			this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.mProgressDialog.show();
			
			TorifukuLog.methodOut();
		}
		
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected CaptureTaskResult doInBackground(Void... arg0) {
			TorifukuLog.methodIn();
			
			/** check sd card state */
			if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				return CaptureTaskResult.NoSdcard;
			}
			
			Bitmap.Config config = Bitmap.Config.RGB_565;
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TorifukuBrowser.this.mContext);
			int quality = Integer.parseInt(sharedPref.getString("capture_quality", "1"));
			switch (quality) {
			case 0:
				config = Bitmap.Config.ARGB_8888;
				break;
			case 2:
				config = Bitmap.Config.ALPHA_8;
				break;
			case 1:
			default:
				break;
			}
			Picture picture = TorifukuBrowser.this.mWebView.capturePicture();
			Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), config);
			if (bitmap == null) {
				return CaptureTaskResult.Failed;
			}
				
			Canvas canvas = new Canvas(bitmap);
			picture.draw(canvas);

			
			ContentResolver cr = TorifukuBrowser.this.mContext.getContentResolver();
			mUri = MediaStore.Images.Media.insertImage(
					cr,
					bitmap,
					TorifukuBrowser.this.mWebView.getTitle() + "_" + System.currentTimeMillis(),
					"");
			bitmap.recycle();
			if (mUri == null) {
				return CaptureTaskResult.Failed;
			}
			
			
			Cursor c = MediaStore.Images.Media.query(cr, Uri.parse(mUri), null);
			if (c != null) {
				TorifukuLog.d("count: " + c.getCount());
				c.moveToFirst();
				int index = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				mFilePath = c.getString(index);
				c.close();
			} else {
				mFilePath = mUri;
			}
			
			TorifukuLog.methodOut();
			return CaptureTaskResult.Success;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(CaptureTaskResult result) {
			TorifukuLog.methodIn();
			
			this.mProgressDialog.dismiss();
			
			/** show result */
			String message = null;
			switch (result) {
			case Success:
				message = TorifukuBrowser.this.mContext.getString(R.string.capture_success, mFilePath);
				break;
			case NoSdcard:
				message = TorifukuBrowser.this.mContext.getString(R.string.capture_failed_nosdcard);
				break;
			case Failed:
			default:
				message = TorifukuBrowser.this.mContext.getString(R.string.capture_failed);
				break;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(TorifukuBrowser.this.mContext);
			builder.setMessage(message);
			builder.setCancelable(false);
			builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (CaptureTask.this.mCaptureListener != null) {
						CaptureTask.this.mCaptureListener.complete();
					}
				}});
			if (result == CaptureTaskResult.Success) {
				builder.setNeutralButton(R.string.view, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						TorifukuLog.methodIn();
						if (CaptureTask.this.mCaptureListener != null) {
							CaptureTask.this.mCaptureListener.complete();
						}
						TorifukuBrowser.this.viewImage(CaptureTask.this.mUri);
						
						TorifukuLog.methodOut();
				}});
			}
			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			
			
			
			TorifukuLog.methodOut();
		}
	}
}
