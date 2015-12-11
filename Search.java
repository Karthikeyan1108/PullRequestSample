package com.example.mioa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mioa.db.DBHelper;

public class Search extends ListActivity {
	// ----------------------------Initialization-Start------------------------------------//
	private DBHelper dbHelper;
	List<String> listcontent = new ArrayList<String>();
	List<String> listsubchap = new ArrayList<String>();
	List<String> searchids = new ArrayList<String>();
	ArrayList<HashMap<String, String>> detailslist;
	ListView list;
	ChuckApplication application = new ChuckApplication();
	Button btFilter;
	ImageView home;
	RelativeLayout menu;
	String neededsearchvalue;
	static String CONTENT = "content";
	static String SUBCHAPTER = "subchapter";
	static String CHAPTER = "chap";
	ProgressDialog progress;

	// ----------------------------Initialization-End------------------------------------//
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
		Editable searchvalue = application.getsearch();

		neededsearchvalue = ChuckApplication.getsearch().toString().trim()
				.toLowerCase();

		btFilter = (Button) findViewById(R.id.menu);
		home = (ImageView) findViewById(R.id.btnhome);
		menu = (RelativeLayout) findViewById(R.id.advancebacks);
		// OnClick of btFilter
		btFilter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// OnClick of menu
		menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent in = new Intent(Search.this, IndexChapter.class);
				startActivity(in);*/
				finish();
			}
		});
		// OnClick of home
		home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(Search.this, Books.class);
				startActivity(in);
			}
		});

		new loadscreen().execute();

	}

	public class ListViewAdapter extends BaseAdapter {

		// Declare Variables
		Context context;
		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> data;
		HashMap<String, String> resultp = new HashMap<String, String>();

		public ListViewAdapter(Context context,
				ArrayList<HashMap<String, String>> arraylist) {
			this.context = context;
			data = arraylist;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// Declare Variables
			TextView grade, copy, chap;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View itemView = inflater.inflate(R.layout.list_row, parent, false);
			// Get the position
			resultp = data.get(position);

			// Locate the TextViews in listview_item.xml
			grade = (TextView) itemView.findViewById(R.id.row_grade);
			copy = (TextView) itemView.findViewById(R.id.row_copy);
			chap = (TextView) itemView.findViewById(R.id.row_chap);

			// Capture position and set results to the TextViews
			grade.setText(resultp.get(Search.CONTENT));
			copy.setText(resultp.get(Search.SUBCHAPTER));
			chap.setText(resultp.get(Search.CHAPTER));

			String tvt = grade.getText().toString().toLowerCase();

			int ofe = tvt.indexOf(neededsearchvalue, 0);
			Spannable WordtoSpan = new SpannableString(grade.getText());

			for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe +1) {

				ofe = tvt.indexOf(neededsearchvalue, ofs);
				if (ofe == -1)
					break;
				else {
					WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00),
							ofe, ofe + neededsearchvalue.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					grade.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
				}
			}

			return itemView;
		}
	}

	// Service call---------------newly Added-------------------
	private class loadscreen extends AsyncTask<Void, String, String> {

		@Override
		protected void onPreExecute() {

			progress = ProgressDialog.show(Search.this, "", "Please Wait.. ",
					true);

		}

		@Override
		protected String doInBackground(Void... params) {
			Editable searchvalue = ChuckApplication.getsearch();

			dbHelper = new DBHelper(Search.this);
			dbHelper.openDataBase();
			String lang = dbHelper.getlang();
			if (lang.equalsIgnoreCase("English")) {
				lang = "eng_content";
			} else if (lang.equalsIgnoreCase("Français")) {
				lang = "eng_content";
			} else if (lang.equalsIgnoreCase("Deutsch")) {
				lang = "eng_content";
			} else if (lang.equalsIgnoreCase("Español")) {
				lang = "eng_content";
			} else if (lang.equalsIgnoreCase("Indonesia")) {
				lang = "indo_content";
			} else {
				lang = "eng_content";
			}
			listcontent = dbHelper.searchcontent(searchvalue, lang);// calls
																	// method in
																	// dbHelper
			listsubchap = dbHelper.searchcontent1(searchvalue, lang);
			searchids = dbHelper.searchids(searchvalue, lang);
			dbHelper.close();

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			detailslist = new ArrayList<HashMap<String, String>>();
			if (listcontent.size() == 0) {
				// creates alert box with message "No match found" and OK button
				AlertDialog.Builder alertbox = new AlertDialog.Builder(
						Search.this);

				alertbox.setMessage(R.string.matchfound);
				alertbox.setCancelable(false);
				alertbox.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
				alertbox.show();
			} else {
				for (int i = 0; i < listcontent.size(); i++) {

					HashMap<String, String> details1 = new HashMap<String, String>();
					String contents = listcontent.get(i);
					String subchaps = listsubchap.get(i);
					String searchid = searchids.get(i);
					details1.put("content", contents);
					details1.put("subchap", subchaps);
					details1.put("chap", searchid);
					detailslist.add(details1);
				}
			}

			list = (ListView) findViewById(android.R.id.list);

			// Newly added List Adapter - Lakshmi
			ListViewAdapter adapter = new ListViewAdapter(Search.this,
					detailslist);
			list.setAdapter(adapter);

			if (progress.isShowing()) {
				progress.dismiss();
			}

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					String text2 = ((TextView) arg1.findViewById(R.id.row_chap))
							.getText().toString();
					int seposi = Integer.parseInt(text2);

					/*
					 * application.setsearchposition(seposi);
					 * application.setkey("searchentry");
					 */
					application.setbookmark(seposi);
					application.setbook_entry("searchentry");
					Intent in = new Intent(Search.this, ViewContent.class);
					startActivity(in);

				}

			});

		}
	}

	@Override
	public void onBackPressed() {

		/*
		 * Intent intent = new Intent(Search.this, IndexChapter.class);
		 * startActivity(intent);
		 */
		finish();
		super.onBackPressed();
	}

}
