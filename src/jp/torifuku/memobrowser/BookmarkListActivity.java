package jp.torifuku.memobrowser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.torifuku.util.torifukuutility.log.TorifukuLog;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * BookmarkListActivity
 * @author torifuku kaiou
 *
 */
public class BookmarkListActivity extends ListActivity {
	private Map<Long, Pair<String, String>> mBookmarkMap = null;
	
	interface TaskCompleteListener {
		void complete(Map<Long, Pair<String, String>> result);
	}
	
	/**
	 * Task
	 * @author torifuku kaiou
	 *
	 */
	private class Task extends AsyncTask<Void, Void, Map<Long, Pair<String, String>>> {
		private Context mContext = null;
		private ProgressDialog mProgressDialog = null;
		private TaskCompleteListener mTaskCompleteListener;
		
		/**
		 * Task
		 * @param context
		 */
		Task(Context context, TaskCompleteListener l) {
			mContext = context;
			mTaskCompleteListener = l;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			TorifukuLog.methodIn();
			
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.show();
			
			TorifukuLog.methodOut();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Map<Long, Pair<String, String>> doInBackground(Void... params) {
			TorifukuLog.methodIn();
			
			//Cursor c = Browser.getAllBookmarks(mContext.getContentResolver());
			Cursor c = mContext.getContentResolver().query(Browser.BOOKMARKS_URI, null, null, null, null);
			if (c == null) {
				return null;
			}
			if (c.getCount() <= 0) {
				return null;
			}
			
			String[] columnNames = c.getColumnNames();
			for (String columnName : columnNames) {
				TorifukuLog.d(columnName);
			}
			Map<Long, Pair<String, String>> out = new LinkedHashMap<Long, Pair<String, String>>();
			final int id_index = c.getColumnIndex(Browser.BookmarkColumns._ID);
			final int title_index = c.getColumnIndex(Browser.BookmarkColumns.TITLE);
			final int url_index = c.getColumnIndex(Browser.BookmarkColumns.URL);
			final int bookmark_index = c.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
			c.moveToLast();
			do {
				int bookmark = c.getInt(bookmark_index);
				if (bookmark != 1) {
					continue;
				}
				long _id = c.getLong(id_index);
				String title = c.getString(title_index);
				String url = c.getString(url_index);
				if (title == null) {
					title = url;
				}
				if (title.length() <= 0) {
					title = url;
				}
				Pair<String, String> pair = Pair.create(title, url);
				out.put(_id, pair);
			} while(c.moveToPrevious());
			
			c.close();
			
			TorifukuLog.methodOut();
			return out;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Map<Long, Pair<String, String>> result) {
			TorifukuLog.methodIn();
			
			mProgressDialog.dismiss();
			if (mTaskCompleteListener != null) {
				mTaskCompleteListener.complete(result);
			}
			
			TorifukuLog.methodOut();
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TorifukuLog.methodIn();
		
		super.onCreate(savedInstanceState);
		setContentView();
		
		super.setResult(Activity.RESULT_CANCELED);
		
		TorifukuLog.methodOut();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TorifukuLog.methodIn();
		
		@SuppressWarnings("unchecked")
		Map.Entry<Long, Pair<String, String>> item = (Entry<Long, Pair<String, String>>) l.getAdapter().getItem(position);
		String url = item.getValue().second;
		
		/** set result */
		Intent intent = new Intent();
		intent.putExtra("url", url);
		super.setResult(Activity.RESULT_OK, intent);
		
		super.finish();
		
		TorifukuLog.methodOut();
	}
	
	/************************************************************************/
	/************************************************************************/
	/************************************************************************/
	/**
	 * setContentView
	 */
	private void setContentView() {
		TorifukuLog.methodIn();
		
		super.setContentView(R.layout.bookmarklist);
		
		Task task = new Task(this, new TaskCompleteListener() {
			/* (non-Javadoc)
			 * @see jp.torifuku.memobrowser.BookmarkListActivity.TaskCompleteListener#complete(java.util.Map)
			 */
			@Override
			public void complete(Map<Long, Pair<String, String>> result) {
				TorifukuLog.methodIn();
				
				BookmarkListActivity.this.mBookmarkMap = result;
				if (BookmarkListActivity.this.mBookmarkMap == null) {
					return;
				}
				
				BookmarkListActivity.this.showBookmark();
				
				TorifukuLog.methodOut();
			}});
		
		task.execute();
		
		// add bookmark button
		Button button = (Button) super.findViewById(R.id.add_bookmark_button);
		button.setOnClickListener(new OnClickListener() {
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				TorifukuLog.methodIn();
				v.setEnabled(false);
				BookmarkListActivity.this.addBookmark();
				TorifukuLog.methodOut();
			}});
		
		TorifukuLog.methodOut();
	}
	
	/**
	 * showBookmark
	 */
	private void showBookmark() {
		TorifukuLog.methodIn();
		if (mBookmarkMap == null) {
			return;
		}
		
		ArrayAdapter<Map.Entry<Long, Pair<String, String>>> adapter = new ArrayAdapter<Map.Entry<Long, Pair<String, String>>>(this, android.R.layout.simple_list_item_1) {
			private LayoutInflater mInflater = null;
			
			/**
			 * ViewHolder
			 * @author torifuku kaiou
			 *
			 */
			class ViewHolder {
				TextView mTextView;
			}
			
			/* (non-Javadoc)
			 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
			 */
			@Override
			public View getView(int position, View convertView, android.view.ViewGroup parent) {
				if (mInflater == null) {
					mInflater = (LayoutInflater) BookmarkListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				}
				ViewHolder holder;
				if (convertView == null) {
					convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
					holder = new ViewHolder();
					holder.mTextView = (TextView) convertView;
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				
				Map.Entry<Long, Pair<String, String>> data = this.getItem(position);
				holder.mTextView.setText(data.getValue().first);
				
				return convertView;
			}
		};
		/*
		Set<Long> keySet = mBookmarkMap.keySet();
		Iterator<Long> it = keySet.iterator();
		while (it.hasNext()) {
			Long _id = it.next();
			Pair<String, String> value = mBookmarkMap.get(_id);
			Pair<Long, String> item = Pair.create(_id, value.first);
			adapter.add(item);
		}*/
		Set<Map.Entry<Long, Pair<String, String>>> items = mBookmarkMap.entrySet();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			adapter.addAll(items);
		} else {
			Iterator<Map.Entry<Long, Pair<String, String>>> it = items.iterator();
			while (it.hasNext()) {
				adapter.add(it.next());
			}
		}
		
		super.setListAdapter(adapter);
		TorifukuLog.methodOut();
	}
	
	/**
	 * addBookmark
	 */
	private void addBookmark() {
		TorifukuLog.methodIn();
		
		String url = super.getIntent().getStringExtra("current url");
		String title = super.getIntent().getStringExtra("title");
		long now = System.currentTimeMillis();
		
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.URL, url);
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		values.put(Browser.BookmarkColumns.CREATED, now);
		values.put(Browser.BookmarkColumns.DATE, now);
		
		super.getContentResolver().insert(Browser.BOOKMARKS_URI, values);
		Toast.makeText(this, R.string.added_bookmark, Toast.LENGTH_LONG).show();
		super.finish();
		
		TorifukuLog.methodOut();
	}
}
