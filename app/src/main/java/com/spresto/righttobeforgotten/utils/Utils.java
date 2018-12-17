package com.spresto.righttobeforgotten.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.spresto.righttobeforgotten.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by spresto on 2018-09-04.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static void copyFileToExternalStorage(Context context, String folderName, int resourceId, String resourceName){
        String pathSDCard = folderName + File.separator + resourceName;
        try{
            InputStream in = context.getResources().openRawResource(resourceId);
            FileOutputStream out;
            out = new FileOutputStream(pathSDCard);
            byte[] buff = new byte[1024];
            int read;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.status_bar_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            //window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }


    public static boolean checkPermissionsArray(Context mContext, String TAG, String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(mContext, TAG, check)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissions(Context mContext ,String TAG, String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
    public static String getRealPathFromUri(Context context,Uri tempUri) throws IOException {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA};
            cursor = context.getContentResolver().query(tempUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if(src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if(width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if(width > w)
            x = (width - w)/2;

        if(height > h)
            y = (height - h)/2;

        int cw = w; // crop width
        int ch = h; // crop height

        if(w > width)
            cw = width;

        if(h > height)
            ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);
    }

//    public static void ASIFT(Bitmap video, Bitmap thumb, int index){
//        Log.e(TAG, "index: "+index);
//        // Read the images from two streams
////        final String input_1Str = "/org/openimaj/examples/image/input_0.png";
////        final String input_2Str = "/org/openimaj/examples/image/input_1.png";
////        final FImage input_1 = ImageUtilities.readF(ASIFTMatchingExample.class.getResourceAsStream(input_1Str));
////        final FImage input_2 = ImageUtilities.readF(ASIFTMatchingExample.class.getResourceAsStream(input_2Str));
//        FImage input_1 = null;
//        FImage input_2 = null;
//        try{
//            ByteArrayOutputStream video_output_stream = new ByteArrayOutputStream();
//            ByteArrayOutputStream thumb_output_stream = new ByteArrayOutputStream();
//
//            video.compress(Bitmap.CompressFormat.PNG, 0, video_output_stream);
//            thumb.compress(Bitmap.CompressFormat.PNG, 0, thumb_output_stream);
//
//            byte[] video_data = video_output_stream.toByteArray();
//            byte[] thumb_data = thumb_output_stream.toByteArray();
//
//            ByteArrayInputStream video_output = new ByteArrayInputStream(video_data);
//            ByteArrayInputStream thumb_output = new ByteArrayInputStream(thumb_data);
//
//            input_1 = ImageUtilities.readF(video_output);
//            input_2 = ImageUtilities.readF(thumb_output);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        // Prepare the engine to the parameters in the IPOL demo
//        final ASIFTEngine engine = new ASIFTEngine(false, 7);
//
//        // Extract the keypoints from both images
//        final LocalFeatureList<Keypoint> input1Feats = engine.findKeypoints(input_1);
//        System.out.println("Extracted input1: " + input1Feats.size());
//        final LocalFeatureList<Keypoint> input2Feats = engine.findKeypoints(input_2);
//        System.out.println("Extracted input2: " + input2Feats.size());
//
//        // Prepare the matcher, uncomment this line to use a basic matcher as
//        // opposed to one that enforces homographic consistency
//        // LocalFeatureMatcher<Keypoint> matcher = createFastBasicMatcher();
//        final LocalFeatureMatcher<Keypoint> matcher = createConsistentRANSACHomographyMatcher();
//
//        // Find features in image 1
//        matcher.setModelFeatures(input1Feats);
//        // ... against image 2
//        matcher.findMatches(input2Feats);
//
//        // Get the matches
//        final List<Pair<Keypoint>> matches = matcher.getMatches();
//        Log.e(TAG, "NMatches: "+matches.size());
//
//        // Display the results
//        //final MBFImage inp1MBF = input_1.toRGB();
//        //final MBFImage inp2MBF = input_2.toRGB();
//        //DisplayUtilities.display(MatchingUtilities.drawMatches(inp1MBF, inp2MBF, matches, RGBColour.RED));
//    }
//
//    /**
//     * @return a matcher with a homographic constraint
//     */
//    private static LocalFeatureMatcher<Keypoint> createConsistentRANSACHomographyMatcher() {
//        final ConsistentLocalFeatureMatcher2d<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
//                createFastBasicMatcher());
//        matcher.setFittingModel(new RobustHomographyEstimator(10.0, 1000, new RANSAC.BestFitStoppingCondition(),
//                HomographyRefinement.NONE));
//
//        return matcher;
//    }
//
//    /**
//     * @return a basic matcher
//     */
//    private static LocalFeatureMatcher<Keypoint> createFastBasicMatcher() {
//        return new FastBasicKeypointMatcher<Keypoint>(8);
//    }
//    public static void CompareImages(String filePath){
//        int retVal = 0;
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat image1 = Imgcodecs.imread(filePath, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//        Mat image2 = Imgcodecs.imread(filePath, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//
//        MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
//        MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
//
//        Mat description1 = new Mat();
//        Mat description2 = new Mat();
//        // Definition of ORB key point detector and descriptor extractors
//        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
//        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//
//        // Detect key points
//        detector.detect(image1, keyPoint1);
//        detector.detect(image2, keyPoint2);
//
//        // Extract descriptors
//        extractor.compute(image1, keyPoint1, description1);
//        extractor.compute(image2, keyPoint2, description2);
//
//        // Definition of descriptor matcher
//        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//
//        // Match points of two images
//        MatOfDMatch matches = new MatOfDMatch();
////  System.out.println("Type of Image1= " + descriptors1.type() + ", Type of Image2= " + descriptors2.type());
////  System.out.println("Cols of Image1= " + descriptors1.cols() + ", Cols of Image2= " + descriptors2.cols());
//
//        // Avoid to assertion failed
//        // Assertion failed (type == src2.type() && src1.cols == src2.cols && (type == CV_32F || type == CV_8U)
//        if (description2.cols() == description1.cols()) {
//            matcher.match(description1, description2 ,matches);
//
//            // Check matches of key points
//            DMatch[] match = matches.toArray();
//            double max_dist = 0; double min_dist = 100;
//
//            for (int i = 0; i < description1.rows(); i++) {
//                double dist = match[i].distance;
//                if( dist < min_dist ) min_dist = dist;
//                if( dist > max_dist ) max_dist = dist;
//            }
//            System.out.println("max_dist=" + max_dist + ", min_dist=" + min_dist);
//
//            // Extract good images (distances are under 10)
//            for (int i = 0; i < description1.rows(); i++) {
//                if (match[i].distance <= 10) {
//                    retVal++;
//                }
//            }
//        }
//
//        Log.e("compare","retVal: "+retVal);
//
//        return;
//    }

}
