/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.kervala.comicsreader;

import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoView;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class PhotoViewActivity extends Activity {

	private ViewPager mViewPager;
	private Album mAlbum;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("ComicsReader", "PhotoViewActivity.onCreate");
		super.onCreate(savedInstanceState);

		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);

		Intent intent = getIntent();
                if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                        Uri uri = intent.getData();
			Log.d("ComicsReader", "PhotoViewActivity.onCreate: uri=" + uri.toString());
                        String filename = Album.getFilenameFromUri(uri);
			Log.d("ComicsReader", "PhotoViewActivity.onCreate: fname=" + filename);
			mAlbum = Album.createInstance(filename);
			boolean res = mAlbum.open(filename, true);
			Log.d("ComicsReader", "open res: " + Boolean.toString(res));
			mViewPager.setAdapter(new SimplePagerAdapter());
			mViewPager.setCurrentItem(mAlbum.getCurrentPage());
                }
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_TAB:
		case KeyEvent.KEYCODE_SEARCH:
			showDialog(CommonActivity.DIALOG_PAGES);
			return true;
		case KeyEvent.KEYCODE_BACK:
			if (mAlbum != null) {
				setResult(CommonActivity.RESULT_FILE);
//				String mimeType = mAlbum.getMimeType();
//				Intent intent = getIntent();
//				intent.setDataAndType(mAlbum.getAlbumUri(), mimeType);
//				setResult(RESULT_FILE, intent);
			}
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	class SimplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
//			Log.d("ComicsReader", "getCount: " + Integer.toString(mAlbum.getNumPages()));
			return mAlbum.getNumPages();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());

//			Log.d("ComicsReader", "instantiateItem: " + Integer.toString(position) + ", " + mAlbum.getFilename(position));
			Log.d("ComicsReader", "instantiateItem: " + Integer.toString(position));

			photoView.setImageBitmap(mAlbum.getPage(position, 2048, 2048, false));

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
