package jp.torifuku.memobrowser;

import android.content.Intent;

public interface TorifukuBrowserInterface {

	public interface CaptureListener {
		void complete();
	}
	
	void start();
	void stop();
	boolean back();
	Intent bookmark();
	void home();
	Intent setting();
	void reload();
	void jump(String url);
	String getUrl();
	void capture(CaptureListener l);
	void setJavaScriptEnabled();
}
