package jp.torifuku.util.torifukuutility.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * TorifukuLog
 * 
 * output log.
 * 
 * <How to use><br>
 * 1. Please call
 * jp.torifuku.util.torifukuutility.log.TorifukuLog.init(Context) or<br>
 * init(Context, boolean) or<br>
 * init(Context, boolean, String) first at android.app.Activity.onCreate(Bundle).<br>
 * This is only once.<br>
 * 2. Please call at method Entrance.<br>
 * jp.torifuku.util.torifukuutility.log.TorifukuLog.methodIn()<br>
 * 3. Please call at method Exit.
 * jp.torifuku.util.torifukuutility.log.TorifukuLog.methodOut()<br>
 * You do not need to write Tag & msg!!!
 * 
 * @author torifuku.kaiou@gmail.com
 * 
 */
public class TorifukuLog {
	static private boolean sDebugable = false;
	static private String sTag = "XXXX";

	/**
	 * init
	 * 
	 * Set manifest debuggable
	 */
	static public void init(Context context) {
		if (context == null) {
			return;
		}
		ApplicationInfo appInfo = context.getApplicationInfo();
		if (appInfo == null) {
			return;
		}
		boolean debuggable = false;
		int flagDebugable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE);
		if (flagDebugable == ApplicationInfo.FLAG_DEBUGGABLE) {
			debuggable = true;
		} else {
			debuggable = false;
		}
		TorifukuLog.init(context, debuggable);
	}

	/**
	 * init
	 * 
	 * @param context
	 * @param debuggable
	 *            if true output log
	 */
	static public void init(Context context, boolean debuggable) {
		TorifukuLog.sDebugable = debuggable;
		if (context == null) {
			return;
		}

		ApplicationInfo appInfo = context.getApplicationInfo();
		if (appInfo == null) {
			return;
		}
		PackageManager pm = context.getPackageManager();
		TorifukuLog.sTag = pm.getApplicationLabel(appInfo).toString();
	}
	
	/**
	 * init
	 * 
	 * @param context
	 * @param on
	 * @param tag
	 */
	static public void init(Context context, boolean debuggable, String tag) {
		TorifukuLog.init(context, debuggable);
		if (tag != null) {
			TorifukuLog.sTag = tag;
		}
	}

	/**
	 * methodIn
	 */
	static public void methodIn() {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.d(sTag, info[0] + " In: " + info[1]);
	}

	/**
	 * methodOut
	 */
	static public void methodOut() {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.d(sTag, info[0] + " Out: " + info[1]);
	}

	/**
	 * d
	 * @param msg
	 */
	static public void d(String msg) {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = TorifukuLog.createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.d(TorifukuLog.sTag, msg + "[" + info[1] + "]");
	}

	/**
	 * e
	 * @param msg
	 */
	static public void e(String msg) {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = TorifukuLog.createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.e(TorifukuLog.sTag, msg + "[" + info[1] + "]");
	}

	/**
	 * i
	 * @param msg
	 */
	static public void i(String msg) {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = TorifukuLog.createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.i(TorifukuLog.sTag, msg + "[" + info[1] + "]");
	}

	/**
	 * w
	 * @param msg
	 */
	static public void w(String msg) {
		if (!TorifukuLog.sDebugable) {
			return;
		}
		String[] info = TorifukuLog.createMethodInfo();
		if (info == null) {
			return;
		}
		if (info.length < 2) {
			return;
		}
		Log.w(TorifukuLog.sTag, msg + "[" + info[1] + "]");
	}

	static private String[] createMethodInfo() {
		StackTraceElement ste = getStackTraceElement();
		if (ste == null) {
			return null;
		}

		String fileName = ste.getFileName();
		String className = ste.getClassName();
		String methodName = ste.getMethodName();
		int lineNumber = ste.getLineNumber();
		String[] splits = className.split("\\.");

		Thread thread = Thread.currentThread();
		String threadName = thread.getName();
		int priority = thread.getPriority();

		String methodInfo = splits[splits.length - 1] + "#" + methodName;
		String fileInfo = fileName + "(" + lineNumber + ") {" + threadName
				+ "(priority=" + priority + ")}";
		String[] ret = { methodInfo, fileInfo };
		return ret;
	}

	static private StackTraceElement getStackTraceElement() {
		StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		if (stes == null) {
			return null;
		}

		int index = -1;
		int cnt = 0;
		for (StackTraceElement s : stes) {
			if (s != null) {
				String fileName = s.getFileName();
				String className = s.getClassName();
				if (fileName.equals("TorifukuLog.java")
						&& (className
								.equals("jp.torifuku.util.torifukuutility.log.TorifukuLog"))) {
					String methodName = s.getMethodName();
					if (methodName.equals("createMethodInfo")) {
						index = cnt;
						break;
					}
				}
			}
			cnt++;
		}

		if (index >= 0) {
			index += 2;
			if (index < stes.length) {
				return stes[index];
			}
		}

		return null;
	}
}
