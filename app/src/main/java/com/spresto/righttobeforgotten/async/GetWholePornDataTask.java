package com.spresto.righttobeforgotten.async;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.interfaces.WholePornDataTaskListener;
import com.spresto.righttobeforgotten.model.GetWholeSiteData;
import com.spresto.righttobeforgotten.utils.SexualSiteLists;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by spresto on 2018-10-04.
 */

public class GetWholePornDataTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = GetWholePornDataTask.class.getSimpleName();
    private int validCount = 1000;
    private Context context;
    private WholePornDataTaskListener wholepornDataTaskListener;
    private ArrayList<GetWholeSiteData> data = new ArrayList<>();
    private HashMap<String, String> siteMap = null;
    int middle_index = 0;
    private RequestOptions options;
    private String default_url, detail_url;
    private RequestManager requestManager;
    private boolean isNotFound = false;

    public GetWholePornDataTask(Context context, WholePornDataTaskListener wholePornDataTaskListener, String default_url, String detail_url) {
        this.context = context;
        this.wholepornDataTaskListener = wholePornDataTaskListener;
        this.default_url = default_url;
        this.detail_url = detail_url;
        siteMap = SexualSiteLists.siteMap;
        options = new RequestOptions()
                .error(context.getResources().getDrawable(R.drawable.no_thumbnail))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        requestManager = Glide.with(context);

        Log.e(TAG, "default_url: "+default_url);
        Log.e(TAG, "detail_url: "+detail_url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        Document document;
        Elements elements;
        Elements extraElements;
        ArrayList<String> site_url = new ArrayList<>();
        ArrayList<String> img_url = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();


        if (default_url.contains("mingkyaa")) {
            // mingkyaa start statement
            try {
                int startCount;
                for (int i = 0; i <= validCount; i++) {
                    document = Jsoup.connect(detail_url + "&keyword=&keyword2=&cate2=&page=" + i).get();
                    Log.e(TAG, "page URL: " + detail_url + "&keyword=&keyword2=&cate2=&page=" + i);
                    if (!document.html().toString().contains("<td class=\"no\">")) {
                        startCount = middle_index;
                        elements = document.select("td.thum > a");
                        extraElements = document.select("td.subject > a");

                        for (Element link : elements) {
                            Log.e(TAG, "site url: " + default_url + link.attr("href"));
                            Log.e(TAG, "image url: " + link.select("img").attr("src"));
                            site_url.add(default_url + link.attr("href"));
                            img_url.add(link.select("img").attr("src"));
                        }

                        for (Element link : extraElements) {
                            Log.e(TAG, "title: " + link.text());
                            title.add(link.text());
                        }

                        for (; startCount < title.size(); startCount++) {
                            GetWholeSiteData model = new GetWholeSiteData(
                                    site_url.get(startCount),
                                    title.get(startCount),
                                    img_url.get(startCount),
                                    (startCount + 1) + ".jpg"
                            );

                            data.add(model);

                            try {
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                                saveImage(String.valueOf(startCount + 1),
                                        Bitmap.createScaledBitmap(
                                                requestManager.asBitmap().load(model.getUrl()).apply(options).submit().get(),
                                                262,
                                                262,
                                                false
                                        )
                                );
                            } catch (Exception e) {
                                Log.e(TAG, "Middle exception");
                                e.printStackTrace();
                            }
                        }

                    } else {
                        break;
                    }
                }
            } catch (HttpStatusException e) {
                Log.e(TAG, "404 Exception");
                if (e.getStatusCode() == 404) {
                    Log.e(TAG, "404");
                }
            } catch (Exception e) {
                Log.e(TAG, "Wide Exception");
                e.printStackTrace();
            } finally {
                if (data.size() == 0) {
                    return "false";
                } else {
                    return "true";
                }
            }
            // mingkyaa end statement
        } else if (default_url.contains("yazaral")) {
            // yazaral start statement
            try {
                int startCount;
                for (int i = 1; i <= validCount; i++) {
                    document = Jsoup.connect(detail_url + "&page=" + i).get();
                    Log.e(TAG, "page URL: " + detail_url + "&page=" + i);
                    if (!document.html().toString().contains("게시물이 없습니다.")) {
                        startCount = middle_index;
                        elements = document.select("div.img-item > a");
                        extraElements = document.select("h2.media-heading > a");
                        for (Element link : elements) {
                            middle_index++;
                            Log.e(TAG, "image url: " + link.attr("href"));
                            img_url.add(link.attr("href"));
                        }
                        for (Element link : extraElements) {
                            Log.e(TAG, "site url: " + link.attr("href"));
                            Log.e(TAG, "title: " + link.text());
                            site_url.add(link.attr("href"));
                            title.add(link.text());
                        }
                        Log.e(TAG, "middle_index: "+middle_index);
                        Log.e(TAG, "startCount: "+startCount);

                        for (; startCount < title.size(); startCount++) {
                            GetWholeSiteData model = new GetWholeSiteData(
                                    site_url.get(startCount),
                                    title.get(startCount),
                                    img_url.get(startCount),
                                    (startCount + 1) + ".jpg"
                            );
                            Log.e(TAG, "s; "+startCount);
                            Log.e(TAG, "title.size; "+title.size());
                            data.add(model);

                            try {
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                                saveImage(String.valueOf(startCount + 1),
                                        Bitmap.createScaledBitmap(
                                                requestManager.asBitmap().load(model.getUrl()).apply(options).submit().get(),
                                                262,
                                                262,
                                                false
                                        )
                                );
                            } catch (Exception e) {
                                Log.e(TAG, "Middle exception");
                                e.printStackTrace();
                            }
                        }

                    } else {
                        break;
                    }

                }
            } catch (HttpStatusException e) {
                Log.e(TAG, "404 Exception");
                if (e.getStatusCode() == 404) {
                    Log.e(TAG, "404");
                }
            } catch (Exception e) {
                Log.e(TAG, "Wide Exception");
                e.printStackTrace();
            } finally {
                if (data.size() == 0) {
                    return "false";
                } else {
                    return "true";
                }
            }
            // yazaral end statement

        } else {
            // pornhub start statement
            try {
                for (int i = 1; i <= validCount; i++) {
                    document = Jsoup.connect(detail_url + "&page=" + i).get();
                    Log.e(TAG, "page URL: " + detail_url + "&page=" + i);
                    if (!document.html().toString().contains("No Videos found.")) {
                        elements = document.select("div.img.fade.fadeUp.videoPreviewBg > a");
                        for (Element link : elements) {
                            middle_index++;
                            Log.e(TAG, "Parse!!");
                            GetWholeSiteData model = new GetWholeSiteData(
                                    default_url + link.attr("href"),
                                    link.attr("title"),
                                    link.select("img").attr("data-thumb_url"),
                                    middle_index + ".jpg"
                            );
                            data.add(model);

                            try {
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                saveImage(String.valueOf(middle_index),
                                        Bitmap.createScaledBitmap(
                                                Glide.with(context).asBitmap().load(model.getUrl()).apply(options).submit().get(),
                                                262,
                                                262,
                                                false
                                        )
                                );
                            } catch (Exception e) {
                                Log.e(TAG, "Middle exception");
                                e.printStackTrace();
                            }

                        }
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            } catch (HttpStatusException e) {
                Log.e(TAG, "404 Exception");
                if (e.getStatusCode() == 404) {
                    Log.e(TAG, "404");
                }
            } catch (Exception e) {
                Log.e(TAG, "Wide Exception");
                e.printStackTrace();
            } finally {
                if (data.size() == 0) {
                    return "false";
                } else {
                    return "true";
                }
            }
            // pornhub end statement
        }



    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("true")) {
            wholepornDataTaskListener.onComplete(data);
        } else {
            // comments...
        }
    }

    private String saveImage(String index, Bitmap image) {
        String savedImagePath = null;

        String imageFileName = index + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/ForgottenTemp");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
