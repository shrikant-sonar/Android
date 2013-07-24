package com.pack.Lebanonnews;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pack.AppConstants.AppConstants;
import com.pack.AppConstants.CompareDates;
import com.pack.ColourCode.OperaColor;
import com.pack.Internet.ImageDownloader;
import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarShareEventListener;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.SharePanelView;

@SuppressLint("SimpleDateFormat")
public class DetailNews extends Activity {

	WebView w;
	SQLiteDatabase db;
	String body[], title[], date[], source[], image, thumbnail_img[],
			new_date[], new_body[], neeew_date[], caption[], content_item[],
			dislikes[], duration[], enclosure[], file_collection[],
			last_updated[], likecnt[], numrater[], published_date[], rss[],
			source_name[], thumbnail[], url[], uploader[], viewcount[],
			rating[], file_path[], file_type_name[], small_thumb[], entityKey,
			finalFont, secondFont, direction, mUrl, fontSize = "12";
	int value, r, g, b, id, val;
	RelativeLayout header;
	TextView title_text, date_text;
	Button back, fwd, back_btn;
	ImageView logo, news_img;
	Context context;
	// ProgressDialog dialog;
	ProgressBar bar;
	float device_height, device_width, image_width, image_height, aspect_ratio;
	double new_img_height;

	public ImageLoader imageLoader;

	Dialog share_dialog;

	Entity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.detail_news);

		if (AppConstants.ldpi || AppConstants.mdpi) {
			fontSize = "12";
		} else if (AppConstants.hdpi) {
			fontSize = "12";
		} else if (AppConstants.xhdpi) {
			fontSize = "13";
		} else if (AppConstants.xxhdpi) {
			fontSize = "14";
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			value = extras.getInt("Position");
			id = extras.getInt("ID");
			image = extras.getString("Image");
			entityKey = extras.getString("Socialize");

			if (id == 5 && !AppConstants.istablet) {
				val = extras.getInt("Value");
				System.out.println("VVVVVAAAAAALLLLLLLLL is MOBILE " + val);
			}

			if (id == 8 && AppConstants.istablet) {
				val = extras.getInt("Value");

				System.out.println("VVVVVAAAAAALLLLLLLLL is TABLET " + val);
			}

			// value = value - 1; // as value starts with 1 we need 0

			System.out.println("******" + value);
			System.out.println("---" + image);
			System.out.println("KEY " + entityKey);

		}

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getBaseContext().getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("YOURHASH KEY:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}

		getarticle_data_from_db();

		// Initialize socialize
		Socialize.initAsync(DetailNews.this, new SocializeInitListener() {

			@Override
			public void onError(SocializeException error) {
				System.out.println(error.getMessage());
			}

			@Override
			public void onInit(Context context, IOCContainer container) {
				// If you want to access Socialize directly, do it here
			}

		});

		Socialize.onCreate(this, savedInstanceState);

		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityName = getResources().getString(R.string.app_name)
				+ " app";

		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole
		// object in the Intent
		entity = Entity.newInstance(title[value], entityName);

		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.

		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(false);

		View actionBarWrapped = ActionBarUtils.showActionBar(DetailNews.this,
				R.layout.detail_news, entity, options, new ActionBarListener() {

					@Override
					public void onCreate(ActionBarView view) {

						view.setOnActionBarEventListener(new OnActionBarShareEventListener() {

							/******************************************************************
							 * Standard event listener callbacks (Optional)
							 ******************************************************************/

							@Override
							public void onUpdate(ActionBarView actionBar) {
								// Called when the action bar has its data
								// updated
							}

							@Override
							public void onPostUnlike(ActionBarView actionBar) {
								// Called AFTER a user has removed a like
							}

							@Override
							public void onPostShare(ActionBarView actionBar,
									Share share) {
								// Called AFTER a user has posted a share
							}

							@Override
							public void onPostLike(ActionBarView actionBar,
									Like like) {
								// Called AFTER a user has posted a like
							}

							@Override
							public void onLoad(ActionBarView actionBar) {
								// Called when the action bar is loaded
							}

							@Override
							public void onLoadFail(Exception error) {
								// Called when the action bar load failed
							}

							@Override
							public void onGetLike(ActionBarView actionBar,
									Like like) {
								// Called when the action bar retrieves the like
								// for the
								// current user
							}

							@Override
							public void onGetEntity(ActionBarView actionBar,
									Entity entity) {
								// Called when the action bar retrieves the
								// entity data
							}

							@Override
							public boolean onClick(ActionBarView actionBar,
									ActionBarEvent evt) {
								// Called when the user clicks on the action bar
								// Return true to indicate you do NOT want the
								// action to continue
								return false;
							}

							/******************************************************************
							 * Share dialog callbacks (Optional)
							 ******************************************************************/

							@Override
							public void onShow(Dialog dialog,
									SharePanelView dialogView) {
								// The dialog was shown.
							}

							@Override
							public void onCancel(Dialog dialog) {
								// User cancelled.
							}

							@Override
							public void onSimpleShare(ShareType type) {
								// User performed a simple share operation (e.g.
								// Email or SMS)
							}

							@Override
							public void onFlowInterrupted(
									DialogFlowController controller) {
								// This will only be called if onContinue
								// returns true

								// Obtain share text (e.g. from the user via a
								// dialog)

								// Call continue when you want flow to resume
								controller.onContinue(title[value] + "\t"
										+ url[value]);
							}

							@Override
							public boolean onContinue(Dialog dialog,
									boolean remember, SocialNetwork... networks) {
								// Return true if you want to interrupt the flow
								return true;
							}

							/******************************************************************
							 * Social Network Callbacks (Optional)
							 ******************************************************************/

							@Override
							public void onNetworkError(Activity context,
									SocialNetwork network, Exception error) {
								// Handle error
							}

							@Override
							public void onCancel() {
								// The user cancelled the operation.
							}

							@Override
							public void onAfterPost(Activity parent,
									SocialNetwork socialNetwork,
									JSONObject responseObject) {
								// Called after the post returned from the
								// social network.
								// responseObject contains the raw JSON response
								// from the social network.
							}

							@Override
							public boolean onBeforePost(Activity parent,
									SocialNetwork socialNetwork,
									PostData postData) {

								return false;
							}

						});
					}

				});

		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);

		context = DetailNews.this;

		// root = (RelativeLayout) findViewById(R.id.tabs_parent);
		w = (WebView) findViewById(R.id.detail_news);

		w.getSettings().setJavaScriptEnabled(true);
		w.getSettings().setPluginState(PluginState.ON);
		w.getSettings().setPluginState(PluginState.ON_DEMAND);
		w.getSettings().setLoadsImagesAutomatically(true);
		w.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		w.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		
		header = (RelativeLayout) findViewById(R.id.detailheader);

		title_text = (TextView) findViewById(R.id.title);
		date_text = (TextView) findViewById(R.id.date);
		logo = (ImageView) findViewById(R.id.logo);
		news_img = (ImageView) findViewById(R.id.detail_image);

		back = (Button) findViewById(R.id.detail_back);
		fwd = (Button) findViewById(R.id.detail_forward);
		back_btn = (Button) findViewById(R.id.detail_back_btn);

		bar = (ProgressBar) findViewById(R.id.detail_news_progress);

		// r = Integer.parseInt(AppConstants.PCR);
		// g = Integer.parseInt(AppConstants.PCG);
		// b = Integer.parseInt(AppConstants.PCB);

		getConfiguaration();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		if (AppConstants.finalLangId.equals("1")) {

			params.gravity = Gravity.RIGHT;

			title_text.setGravity(Gravity.RIGHT);
			date_text.setGravity(Gravity.RIGHT);
			date_text.setLayoutParams(params);
			title_text.setLayoutParams(params);

			// date_text.setGravity(android.view.Gravity.RIGHT);
		} else if (AppConstants.finalLangId.equals("2")) {

			params.gravity = Gravity.LEFT;
			title_text.setGravity(Gravity.LEFT);
			date_text.setGravity(Gravity.LEFT);
			date_text.setLayoutParams(params);
			title_text.setLayoutParams(params);

			// date_text.setGravity(Gravity.LEFT);
		}

		header.setBackgroundColor(Color.parseColor((OperaColor.toHex(r, g, b))));

		// ImageDownloader im = new ImageDownloader();
		// im.download("http://portal.mobilepasse.com/CPImages/"
		// + AppConstants.HeaderLogo, logo);

		String file_path;
		if (AppConstants.sdcard) {
			// file_path = Environment.getExternalStorageDirectory()
			// .getAbsolutePath() + "/LebanonNews/Images/.Icons/Logo.png";

			file_path = getExternalCacheDir().getAbsolutePath() + "/"
					+ AppConstants.cacheName + "/Images/Icons/Logo.png";
		} else {
			file_path = DetailNews.this.getFilesDir().getAbsolutePath() + "/"
					+ AppConstants.cacheName + "/Images/Icons/Logo.png";
		}

		File path = new File(file_path);
		if (path.exists()) {

			System.out.println("DETAIL LOGO IMAGE EXISTS");

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			float density = metrics.density;

			Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());

			RelativeLayout.LayoutParams p = null;

			if (AppConstants.ldpi || AppConstants.mdpi || AppConstants.hdpi) {
				p = new RelativeLayout.LayoutParams(((int) density * 120),
						bitmap.getWidth());
			} else {
				p = new RelativeLayout.LayoutParams(((int) density * 100),
						bitmap.getWidth());
			}

			p.addRule(RelativeLayout.CENTER_VERTICAL);
			p.addRule(RelativeLayout.CENTER_HORIZONTAL);

			p.addRule(RelativeLayout.CENTER_VERTICAL);

			p.addRule(RelativeLayout.CENTER_HORIZONTAL);

			logo.setLayoutParams(p);
			logo.setImageBitmap(bitmap);
		} else {
			System.out.println("DETAIL NO LOGO IMAGE");
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		device_width = dm.widthPixels;
		device_height = dm.heightPixels;

		System.out.println("Device width " + device_width);
		System.out.println("Device height " + device_height);

		try {
			if (!image.equals(null)) {
				news_img.setVisibility(View.VISIBLE);

				imageLoader = ImageLoader.getInstance();

				File cachedImage = imageLoader.getDiscCache().get(image);

				if (cachedImage.exists()) {

					System.out.println("Image exists *******************");

					BitmapFactory.Options o = new BitmapFactory.Options();
					o.inJustDecodeBounds = true;

					Bitmap bitmap = BitmapFactory.decodeFile(cachedImage
							.getAbsolutePath());

					image_height = bitmap.getHeight();
					image_width = bitmap.getWidth();

					System.out.println("Height is " + image_height);
					System.out.println("Width is " + image_width);

					aspect_ratio = (image_width) / (device_width);

					System.out.println("Aspect Ratio : " + aspect_ratio);

					new_img_height = image_height / aspect_ratio;

					// news_img.getLayoutParams().height = (int) new_img_height;

					// news_img.getLayoutParams().width = (int) image_width;

					Bitmap b = Bitmap.createScaledBitmap(bitmap,
							(int) image_width, (int) image_height, true);

					// LinearLayout.LayoutParams parms = new
					// LinearLayout.LayoutParams(
					// image_width, (int) new_img_height);
					// news_img.setLayoutParams(parms);

					news_img.setImageBitmap(b);

					System.out.println("New Height is " + new_img_height);
					System.out.println("New Width is " + image_width);

					// news_img.setImageBitmap(imageLoader.getMemoryCache().get(
					// image));

					// news_img.setImageBitmap(new BitmapDrawable(cachedImage));

				} else {
					System.out.println("Image not exists ############");
					ImageDownloader im1 = new ImageDownloader();
					im1.download(image, news_img);
				}
			} else {
				System.out.println("IMAGE GONE");
				news_img.setVisibility(View.GONE);
			}
		} catch (NullPointerException w) {
			w.printStackTrace();
		}

		/*
		 * w.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
		 * 
		 * try { Log.i("TAG", "getExtra = " + hr.getExtra() + "\t\t Type=" +
		 * hr.getType());
		 * 
		 * Intent i = new Intent(DetailNews.this, ShowPhotos.class);
		 * i.putExtra("Identifier", "Article"); i.putExtra("Url",
		 * hr.getExtra()); startActivity(i);
		 * 
		 * } catch (Exception e) { e.printStackTrace(); } return false; } });
		 */
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				w.stopLoading();
				w.loadUrl("");
				w.reload();
				w = null;

				Tabs.start = true;

				if (id == 1) {
					AppConstants.isTabOneRefresh = false;
				}
				if (id == 2) {
					AppConstants.isTabTwoRefresh = false;
				}
				if (id == 3) {
					AppConstants.isTabThreeRefresh = false;
				}
				if (id == 4) {
					AppConstants.isTabFourRefresh = false;
				}
				if (id == 5 && AppConstants.istablet) {
					AppConstants.isTabFiveRefresh = false;
				}
				if (id == 6 && AppConstants.istablet) {
					AppConstants.isTabSixRefresh = false;
				}
				if (id == 7 && AppConstants.istablet) {
					AppConstants.isTabSevenRefresh = false;
				}

				// if (AppConstants.searchClicked) {
				//
				// System.out.println("BACK BUTTON");
				//
				// MoreList.settings_list.setVisibility(View.VISIBLE);
				// MoreList.list.setVisibility(View.GONE);
				// MoreList.detail_list.setVisibility(View.GONE);
				//
				// finish();
				//
				// } else {
				finish();
				// }
			}
		});

		// fwd and back buttton ///////////
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				System.out.println("back btn value = " + value);

				setdate();

				if (value > 0) {
					value = value - 1;

					// dialog = ProgressDialog.show(DetailNews.this, null,
					// null);
					// dialog.setContentView(R.layout.loader);

					bar.setVisibility(View.VISIBLE);

					try {

						new_body[value] = body[value]
								.replace("##MP_Direction##",
										AppConstants.direction)
								.replace("##MP_Weight##", "normal")
								.replace("##MP_Color##", "black")
								.replace("##MP_Size##", fontSize)
								.replace("##MP_FontType##", finalFont)
								.replace("##MP_AColor##", "blue")
								.replace("##MP_ASize##", fontSize)
								.replace("##MP_AFontType##", "Arial");

						w.setWebChromeClient(new WebChromeClient());

						w.setWebViewClient(new MyWebViewClient());

						w.loadDataWithBaseURL("same://ur/l/tat/does/not/work",
								new_body[value], "text/html", "utf-8", null);

						if (AppConstants.finalLangId.equals("1")) {

							if (!AppConstants.isHoneyComb) {

								title_text.setText(ArabicReshape
										.reshape(title[value]));
								date_text.setText(ArabicReshape
										.reshape(neeew_date[value] + " - "
												+ source_name[value]));

							} else {
								title_text.setText(title[value]);
								date_text.setText(neeew_date[value] + " - "
										+ source_name[value]);
							}
						} else if (AppConstants.finalLangId.equals("2")) {
							title_text.setText(title[value]);
							date_text.setText(neeew_date[value] + " - "
									+ source_name[value]);
						}

						System.out.println("Image value back button "
								+ thumbnail_img[value]);
					} catch (Exception e) {
						e.printStackTrace();
						// dialog.dismiss();
						bar.setVisibility(View.GONE);
					}

					try {
						news_img.setImageBitmap(null);
						news_img.setVisibility(View.GONE);

						if (!thumbnail_img[value].equals(null)) {

							news_img.setVisibility(View.VISIBLE);

							imageLoader = ImageLoader.getInstance();

							File cachedImage = imageLoader.getDiscCache().get(
									thumbnail_img[value]);

							if (cachedImage.exists()) {

								System.out
										.println("Image exists back *******************");

								BitmapFactory.Options o = new BitmapFactory.Options();
								o.inJustDecodeBounds = true;

								Bitmap bitmap = BitmapFactory
										.decodeFile(cachedImage
												.getAbsolutePath());

								image_height = bitmap.getHeight();
								image_width = bitmap.getWidth();

								System.out.println("Height is " + image_height);
								System.out.println("Width is " + image_width);

								aspect_ratio = (image_width) / (device_width);

								System.out.println("Aspect Ratio : "
										+ aspect_ratio);

								new_img_height = image_height / aspect_ratio;

								Bitmap b = Bitmap.createScaledBitmap(bitmap,
										(int) image_width, (int) image_height,
										true);

								news_img.setImageBitmap(b);

								System.out.println("New Height is "
										+ new_img_height);
								System.out.println("New Width is "
										+ image_width);

							} else {
								System.out
										.println("Image not exists ############");
								ImageDownloader im1 = new ImageDownloader();
								im1.download(thumbnail_img[value], news_img);
							}
						} else {
							System.out.println("IMAGE GONE");
							news_img.setVisibility(View.GONE);
						}
					} catch (Exception w) {
						w.printStackTrace();
					}
				}

			}
		});

		fwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				System.out.println("Body array length " + body.length);
				System.out.println("forward value  " + value);

				setdate();

				if (value < body.length - 1) {

					value = value + 1;

					System.out.println("ELSE " + value);

					// dialog = ProgressDialog.show(DetailNews.this, null,
					// null);
					// dialog.setContentView(R.layout.loader);

					bar.setVisibility(View.VISIBLE);

					try {

						new_body[value] = body[value]
								.replace("##MP_Direction##",
										AppConstants.direction)
								.replace("##MP_Weight##", "normal")
								.replace("##MP_Color##", "black")
								.replace("##MP_Size##", fontSize)
								.replace("##MP_FontType##", finalFont)
								.replace("##MP_AColor##", "blue")
								.replace("##MP_ASize##", fontSize)
								.replace("##MP_AFontType##", "Arial");

						w.setWebChromeClient(new WebChromeClient());

						w.setWebViewClient(new MyWebViewClient());

						w.loadDataWithBaseURL("same://ur/l/tat/does/not/work",
								new_body[value], "text/html", "utf-8", null);

						if (AppConstants.finalLangId.equals("1")) {

							if (!AppConstants.isHoneyComb) {
								title_text.setText(ArabicReshape
										.reshape(title[value]));
								date_text.setText(ArabicReshape
										.reshape(neeew_date[value] + " - "
												+ source_name[value]));
							} else {
								title_text.setText(title[value]);
								date_text.setText(neeew_date[value] + " - "
										+ source_name[value]);
							}
						} else if (AppConstants.finalLangId.equals("2")) {
							title_text.setText(title[value]);
							date_text.setText(neeew_date[value] + " - "
									+ source_name[value]);
						}

						System.out.println("Image value forward button "
								+ thumbnail_img[value]);
					} catch (Exception e) {
						// dialog.dismiss();

						bar.setVisibility(View.GONE);
						e.printStackTrace();
					}

					try {

						news_img.setImageBitmap(null);
						news_img.setVisibility(View.GONE);

						if (!thumbnail_img[value].equals(null)
								|| thumbnail_img[value] != null) {

							System.out.println("SHOW IMAGE ");

							news_img.setVisibility(View.VISIBLE);

							imageLoader = ImageLoader.getInstance();

							File cachedImage = imageLoader.getDiscCache().get(
									thumbnail_img[value]);

							if (cachedImage.exists()) {

								System.out
										.println("Image exists fwd *******************");

								BitmapFactory.Options o = new BitmapFactory.Options();
								o.inJustDecodeBounds = true;

								Bitmap bitmap = BitmapFactory
										.decodeFile(cachedImage
												.getAbsolutePath());

								image_height = bitmap.getHeight();
								image_width = bitmap.getWidth();

								System.out.println("Height is " + image_height);
								System.out.println("Width is " + image_width);

								aspect_ratio = (image_width) / (device_width);

								System.out.println("Aspect Ratio : "
										+ aspect_ratio);

								new_img_height = image_height / aspect_ratio;

								Bitmap b = Bitmap.createScaledBitmap(bitmap,
										(int) image_width, (int) image_height,
										true);

								news_img.setImageBitmap(b);

								System.out.println("New Height is "
										+ new_img_height);
								System.out.println("New Width is "
										+ image_width);

							} else {
								System.out
										.println("Image not exists ############");
								ImageDownloader im1 = new ImageDownloader();
								im1.download(thumbnail_img[value], news_img);
							}
						} else {
							System.out.println("IMAGE GONE");
							news_img.setVisibility(View.GONE);
						}
					} catch (Exception w) {
						w.printStackTrace();
					}
				}

			}
		});

		news_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(DetailNews.this, ShowPhotos.class);
				i.putExtra("Identifier", "Article");
				i.putExtra("Url", image);
				startActivity(i);

			}
		});
	}

	@SuppressWarnings("deprecation")
	public void setdate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm:ss a");

		SimpleDateFormat dateFormat1 = new SimpleDateFormat(
				"MM/dd/yyyy kk:mm:ss a");

		Date date;

		try {
			for (int i = 0; i < new_date.length; i++) {
				date = dateFormat.parse(new_date[i]);
				if (AppConstants.is24hour) {
					if (new_date[i].contains("PM")) {
						if (date.getHours() >= 12) {
							neeew_date[i] = dateFormat1.format(date);
						}
					} else {
						neeew_date[i] = dateFormat.format(date);
					}
				} else {
					neeew_date[i] = new_date[i];
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private void getConfiguaration() {

		String query = "SELECT * FROM Configuration";

		db = openOrCreateDatabase(AppConstants.dbName, 0, null);
		Cursor cursor = db.rawQuery(query, null);

		int numrow = cursor.getCount();

		System.out.println("Query passed for get config " + query + numrow);

		cursor.moveToFirst();

		for (int i = 0; i < numrow; i++) {
			try {
				r = Integer.parseInt(cursor.getString(17));

				g = Integer.parseInt(cursor.getString(18));

				b = Integer.parseInt(cursor.getString(19));
				cursor.moveToNext();

			} catch (Exception e) {
				System.out.println("ERROR in tab one " + e.getMessage());
				e.printStackTrace();
			}
		}

		cursor.close();
		db.close();

	}

	// //////////////////////
	public void getarticle_data_from_db() {

		db = openOrCreateDatabase(AppConstants.dbName, 0, null);
		String query = "";

		if (AppConstants.savedClicked) {

			System.out.println("INside save clicked from get article");

			if (AppConstants.finalLangId.equals("1")) {
				query = "Select * FROM Save_Urdu";
			} else {
				query = "Select * FROM Save_English";
			}

		}

		else {

			System.out.println("INside else from get article");

			if (id == 1) {

				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_One_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_One_English ORDER BY Content DESC";
				}
			} else if (id == 2) {

				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Two_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Two_English ORDER BY Content DESC";
				}

			} else if (id == 3) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Three_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Three_English ORDER BY Content DESC";
				}
			} else if (id == 4) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Four_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Four_English ORDER BY Content DESC";
				}
			} else if (id == 5 && AppConstants.istablet) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Five_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Five_English ORDER BY Content DESC";
				}
			} else if (id == 6 && AppConstants.istablet) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Six_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Six_English ORDER BY Content DESC";
				}
			} else if (id == 7 && AppConstants.istablet) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_Seven_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_Seven_English ORDER BY Content DESC";
				}
			}

			else if (id == 5 && !AppConstants.istablet) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_" + val
							+ "_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_" + val
							+ "_English ORDER BY Content DESC";
				}
			}

			else if (id == 8 && AppConstants.istablet) {
				if (AppConstants.finalLangId.equals("1")) {
					query = "SELECT * FROM Get_article_data_Tab_" + val
							+ "_Urdu ORDER BY Content DESC";
				} else if (AppConstants.finalLangId.equals("2")) {
					query = "SELECT * FROM Get_article_data_Tab_" + val
							+ "_English ORDER BY Content DESC";
				}
			}

		}
		Cursor cursor = db.rawQuery(query, null);

		int numrow = cursor.getCount();

		System.out.println("More tab " + numrow + query);

		cursor.moveToFirst();

		new_body = new String[numrow];
		body = new String[numrow];
		title = new String[numrow];
		date = new String[numrow];
		source = new String[numrow];
		thumbnail_img = new String[numrow];
		new_date = new String[numrow];
		neeew_date = new String[numrow];
		caption = new String[numrow];
		content_item = new String[numrow];
		dislikes = new String[numrow];
		duration = new String[numrow];
		enclosure = new String[numrow];
		file_path = new String[numrow];
		file_type_name = new String[numrow];
		last_updated = new String[numrow];
		likecnt = new String[numrow];
		numrater = new String[numrow];
		published_date = new String[numrow];
		rss = new String[numrow];
		small_thumb = new String[numrow];
		source_name = new String[numrow];
		thumbnail = new String[numrow];
		url = new String[numrow];
		uploader = new String[numrow];
		viewcount = new String[numrow];
		rating = new String[numrow];

		for (int i = 0; i < numrow; i++) {
			try {

				body[i] = cursor.getString(0);
				caption[i] = cursor.getString(1);
				content_item[i] = cursor.getString(2);
				date[i] = cursor.getString(3);
				dislikes[i] = cursor.getString(4);
				duration[i] = cursor.getString(5);
				enclosure[i] = cursor.getString(6);
				file_path[i] = cursor.getString(7);
				thumbnail_img[i] = cursor.getString(7);
				file_type_name[i] = cursor.getString(8);
				last_updated[i] = cursor.getString(9);
				likecnt[i] = cursor.getString(10);
				numrater[i] = cursor.getString(11);
				published_date[i] = cursor.getString(12);
				rss[i] = cursor.getString(13);
				small_thumb[i] = cursor.getString(14);
				source_name[i] = cursor.getString(15);
				thumbnail[i] = cursor.getString(16);
				title[i] = cursor.getString(17);
				url[i] = cursor.getString(18);
				uploader[i] = cursor.getString(19);
				viewcount[i] = cursor.getString(20);
				rating[i] = cursor.getString(20);

				cursor.moveToNext();

			} catch (Exception e) {
				System.out.println("ERROR" + e.getMessage());
				e.printStackTrace();
			}
		}

		for (int i = 0; i < date.length; i++) {
			new_date[i] = CompareDates.gettime(date[i]);
		}

		cursor.close();
		db.close();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		try {
			w.stopLoading();
			w.loadUrl("");
			w.reload();
			w = null;
		} catch (Exception e) {

		}

		finish();
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			System.out.println("-------------- " + url);

			if (url.startsWith("http://")) {
				view.stopLoading();
				Intent intent = new Intent(DetailNews.this, Web.class);
				intent.putExtra("Web", url);
				startActivity(intent);
			}

			// view.loadUrl(url);

			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			System.out.println("PAGE STARTED ");

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			System.out.println("PAGE FINISH ");

			// dialog.dismiss();

			bar.setVisibility(View.GONE);

		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		Socialize.onPause(DetailNews.this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		com.facebook.Settings.publishInstallAsync(DetailNews.this,
				getResources().getString(R.string.FacebookId));

		if (AppConstants.finalLangId.equals("1")) {
			finalFont = "DejaVuSans;";
			secondFont = "DejaVuSans;";
		} else if (AppConstants.finalLangId.equals("2")) {
			finalFont = "Helvetica;";
			secondFont = "Arial";
		}

		if (AppConstants.finalLangId.equals("1")) {
			direction = "right;";
		} else if (AppConstants.finalLangId.equals("2")) {
			direction = "left;";
		}

		Socialize.onResume(DetailNews.this);

		getarticle_data_from_db();

		if (AppConstants.finalLangId.equals("1")) {
			AppConstants.direction = "RTL";
		} else if (AppConstants.finalLangId.equals("2")) {
			AppConstants.direction = "LTR";
		}

		for (int i = 0; i < body.length; i++) {

			new_body[i] = body[i]
					.replace("##MP_Direction##", AppConstants.direction)
					.replace("##MP_Weight##", "normal")
					.replace("##MP_Color##", "black")
					.replace("##MP_Size##", fontSize)
					.replace("justify", direction)
					/*
					 * .replace( "##MP_FontType##",
					 * "'Roboto-Regular'; src: url('file:///android_asset/fonts/Roboto-Regular.ttf') format('truetype')"
					 * )
					 */

					.replace("##MP_FontType##", finalFont)
					.replace("##MP_AColor##", "blue")
					.replace("##MP_ASize##", fontSize)
					.replace("##MP_AFontType##", secondFont);

		}

		// System.out.println("text \n" + new_body[value]);

		setdate();

		bar.setVisibility(View.VISIBLE);

		w.setWebChromeClient(new WebChromeClient());

		w.setWebViewClient(new MyWebViewClient());

		w.loadDataWithBaseURL("same://ur/l/tat/does/not/work", new_body[value],
				"text/html", "utf-8", null);

		// w.setWebViewClient(new MyWebViewClient());

		if (AppConstants.finalLangId.equals("1")) {
			if (!AppConstants.isHoneyComb) {
				title_text.setText(ArabicReshape.reshape(title[value]));
				date_text.setText(ArabicReshape.reshape(neeew_date[value]
						+ " - " + source_name[value]));
			} else {
				title_text.setText(title[value]);
				date_text.setText(neeew_date[value] + " - "
						+ source_name[value]);
			}
		} else if (AppConstants.finalLangId.equals("2")) {
			title_text.setText(title[value]);
			date_text.setText(neeew_date[value] + " - " + source_name[value]);
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.onDestroy(DetailNews.this);
		super.onDestroy();
	}

	public boolean saveArticle() {

		boolean a = false;
		Cursor cursor2 = null;

		try {
			if (!db.isOpen()) {
				System.out.println("Db in not open if");
				db = getApplicationContext().openOrCreateDatabase(
						AppConstants.dbName, 0, null);
			}
			String query = "", create_articledata_table = "";

			int numr;

			String table_name = "";

			if (AppConstants.finalLangId.equals("1")) {
				table_name = "Save_Urdu";
			} else if (AppConstants.finalLangId.equals("2")) {
				table_name = "Save_English";
			}

			create_articledata_table = "CREATE TABLE IF NOT EXISTS "
					+ table_name + " (" + "Body TEXT," + "Caption TEXT,"
					+ "Content LONG," + "Date TEXT," + "Dislike TEXT,"
					+ "Duration TEXT," + "Enclosure TEXT," + "FilePath TEXT,"
					+ "FileName TEXt," + "Last_Update TEXT,"
					+ "Like_Count TEXT," + "Numrater TEXT,"
					+ "PublishedDate TEXT," + "Rss TEXT," + "SmallThumb TEXT,"
					+ "SourceName TEXT," + "Thumbnail TEXT," + "Title TEXT,"
					+ "Url TEXT," + "Uploader TEXT," + "ViewCount TEXT,"
					+ "Rating TEXT" + ");";

			db.execSQL(create_articledata_table);

			for (int i = 0; i < body.length; i++) {

				System.out.println("Contetnt " + content_item[value]);

				query = "Select * from " + table_name + " where Content = "
						+ content_item[value];
				cursor2 = db.rawQuery(query, null);

				numr = cursor2.getCount();

				System.out.println("TABLE CREATED SAVE " + query + "=== "
						+ numr);

				if (numr < 1) {

					ContentValues values = new ContentValues();

					values.put("Body", body[value]);
					values.put("Caption", caption[value]);
					values.put("Content", Long.parseLong(content_item[value]));
					values.put("Date", date[value]);
					values.put("Dislike", dislikes[value]);
					values.put("Duration", duration[value]);
					values.put("Enclosure", enclosure[value]);
					values.put("FilePath", file_path[value]);
					values.put("FileName", file_type_name[value]);
					values.put("Last_Update", last_updated[value]);
					values.put("Like_Count", likecnt[value]);
					values.put("Numrater", numrater[value]);
					values.put("PublishedDate", published_date[value]);
					values.put("Rss", rss[value]);
					values.put("SmallThumb", small_thumb[value]);
					values.put("SourceName", source_name[value]);
					values.put("Thumbnail", thumbnail[value]);
					values.put("Title", title[value]);
					values.put("Url", url[value]);
					values.put("Uploader", uploader[value]);
					values.put("ViewCount", viewcount[value]);
					values.put("Rating", rating[value]);

					db.insert(table_name, null, values);

					System.out.println("TRUE");
					a = true;
				}

				cursor2.close();
				db.close();

			}
		} catch (Exception e) {
			e.printStackTrace();

			cursor2.close();
			db.close();

			System.out.println("FALSE");
			a = false;
		}
		return a;

	}

}

/*
 * new_body[value] = "<html><head><style>@font-face" +
 * " {font-family: dejavusans;" + "\n" + "src: url('dejavusans.TTF');}body " +
 * "{font-family: 'dejavusans';}</style></head>" + "<body>" + new_body[value] +
 * "</body></html>";
 */

/*
 * String resultText = "<html><head>"; resultText +=
 * "<link href=\"css/result.css\"" + " rel=\"stylesheet\" type=\"text/css\" />";
 * resultText += "<style>" + "body{ text-align:" + direction + "}" +
 * "@font-face { font-family: \"DejaVuSans\"; " + "" +
 * "src: url('file:///android_asset/fonts/tradbdo.ttf'); " + "}</style>";
 * resultText += "</head>"; resultText += "<body><p>" + new_body[value] + "</p>"
 * + "</body></html>";
 * 
 * // Log.e("TAG", "" + resultText);
 */