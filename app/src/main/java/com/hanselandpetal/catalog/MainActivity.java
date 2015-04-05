package com.hanselandpetal.catalog;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanselandpetal.catalog.model.Flower;
import com.hanselandpetal.catalog.parsers.FlowerJSONParser;

public class MainActivity extends ListActivity {

    private static final  String PHOTOS_BASE_URL = "http://services.hanselandpetal.com/photos/";

	TextView output;
	ProgressBar pb;
	List<MyTask> tasks;
	
	List<Flower> flowerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);
		
		tasks = new ArrayList<>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_get_data) {
			if (isOnline()) {
				requestData("http://services.hanselandpetal.com/secure/flowers.json");
			} else {
				Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	private void requestData(String uri) {
		MyTask task = new MyTask();
		task.execute(uri);
	}

	protected void updateDisplay() {
		//Use FlowerAdapter to display data
        FlowerAdapter adapter = new FlowerAdapter(this, R.layout.item_flower, flowerList);
        setListAdapter(adapter);
	}
	
	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}
	
	private class MyTask extends AsyncTask<String, String, List<Flower>> {

		@Override
		protected void onPreExecute() {
			if (tasks.size() == 0) {
				pb.setVisibility(View.VISIBLE);
			}
			tasks.add(this);
		}
		
		@Override
		protected List<Flower> doInBackground(String... params) {

			String content = HttpManager.getData(params[0], "feeduser", "feedpassword");
            flowerList = FlowerJSONParser.parseFeed(content);

            for(Flower flower: flowerList){
                try{
                    String imageUrl = PHOTOS_BASE_URL + flower.getPhoto();
                    InputStream in = (InputStream) new URL(imageUrl).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    flower.setBitmap(bitmap);
                    in.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
			return flowerList;
		}

        @Override
        protected void onPostExecute(List<Flower> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            flowerList = result;
            updateDisplay();

        }



    }

}