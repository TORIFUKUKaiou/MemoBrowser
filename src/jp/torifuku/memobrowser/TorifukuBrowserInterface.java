package jp.torifuku.memobrowser;

import android.content.Intent;

/**
 * TorifukuBrowserInterface
 * 
 * @author torifuku kaiou
 * 
 */
public interface TorifukuBrowserInterface {

	/**
	 * CaptureListener
	 * 
	 * @author torifuku kaiou
	 * 
	 */
	public interface CaptureListener {
		void complete();
	}

	/**
	 * start
	 */
	void start();

	/**
	 * stop
	 */
	void stop();

	/**
	 * back
	 * 
	 * @return
	 */
	boolean back();

	/**
	 * bookmark
	 * 
	 * @return
	 */
	Intent bookmark();

	/**
	 * home
	 */
	void home();

	/**
	 * setting
	 * 
	 * @return
	 */
	Intent setting();

	/**
	 * reload
	 */
	void reload();

	/**
	 * jump
	 * 
	 * @param url
	 */
	void jump(String url);

	/**
	 * getUrl
	 * 
	 * @return
	 */
	String getUrl();

	/**
	 * capture
	 * 
	 * @param l
	 */
	void capture(CaptureListener l);

	/**
	 * setJavaScriptEnabled
	 */
	void setJavaScriptEnabled();
}
