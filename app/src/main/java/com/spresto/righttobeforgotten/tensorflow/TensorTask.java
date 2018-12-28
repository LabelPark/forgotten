package com.spresto.righttobeforgotten.tensorflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.tensorflow.Tensor;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Created by spresto on 2018-12-23.
 */

public class TensorTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = TensorTask.class.getSimpleName();

    private static final float THRESHOLD = 0.1f;
    private static final int MAX_RESULTS = 3;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private Vector<String> labels = new Vector<String>();   // Recognition Title
    private int[] intValues;
    private float[] floatValues;
    private float[] outputs;                                // Recognition Confidence
    private String[] outputNames;

    private boolean logStats = false;
    private Context context;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private TensorFlowInferenceInterface inferenceInterface;


    public TensorTask(Context context, Bitmap uploadBitmap, Bitmap pornBitmap) {
        this.context = context;

        this.bitmapArrayList.add(uploadBitmap);
        this.bitmapArrayList.add(pornBitmap);
        this.outputNames = new String[]{OUTPUT_NAME};
        this.intValues = new int[INPUT_SIZE * INPUT_SIZE];
        this.floatValues = new float[INPUT_SIZE * INPUT_SIZE * 3];
        this.outputs = new float[1008];

        this.inferenceInterface = new TensorFlowInferenceInterface(this.context.getAssets(), MODEL_FILE);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String actualFilename = LABEL_FILE.split("file:///android_asset/")[1];
        Log.i(TAG, "Reading labels from: " + actualFilename);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(actualFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                this.labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        for (int i = 0; i < 2; i++) {
            Log.e("recognizeImage", "intValue length: " + intValues.length);
            Log.e("recognizeImage", "recognizeImage bitmap.getWidth(): " + bitmapArrayList.get(i).getWidth());
            Log.e("recognizeImage", "recognizeImage bitmap.getHeight(): " + bitmapArrayList.get(i).getHeight());

            Log.e("recognizeImage", "Before int []: " + Arrays.toString(intValues));

            bitmapArrayList.get(i).getPixels(intValues, 0, 224, 0, 0, 224, 224);

            Log.e("recognizeImage", "After int []: " + Arrays.toString(intValues));

            for (int j = 0; j < intValues.length; ++j) {
                final int val = intValues[j];
                /**
                 * ImageMean : 117 , imageStd : 1
                 */
                floatValues[j * 3 + 0] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                floatValues[j * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                floatValues[j * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            }
            Log.e("recognizeImage", "After float []: " + Arrays.toString(floatValues));

            inferenceInterface.feed(INPUT_NAME, floatValues, 1, INPUT_SIZE, INPUT_SIZE, 3);

            Log.e("recognizeImage", "1. outputs []: " + Arrays.toString(outputs) + ", " + outputs.length);

            inferenceInterface.run(outputNames, logStats);

            Log.e("recognizeImage", "2. outputs []: " + Arrays.toString(outputs) + ", " + outputs.length);

            inferenceInterface.fetch(OUTPUT_NAME, outputs);

            Log.e("recognizeImage", "3. outputs []: " + Arrays.toString(outputs) + ", " + outputs.length);

            PriorityQueue<Classifier.Recognition> pq =
                    new PriorityQueue<>(
                            3,
                            (Classifier.Recognition lhs, Classifier.Recognition rhs) -> {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            });

            Log.e("output len", "outputs.length: " + outputs.length);
            for (int k = 0; k < outputs.length; ++k) {
                if (outputs[k] > THRESHOLD) {
                    pq.add(
                            new Classifier.Recognition(
                                    "" + k,
                                    labels.size() > k ? labels.get(k) : "unknown",
                                    outputs[k],
                                    null));
                }
            }

            final ArrayList<Classifier.Recognition> recognitions = new ArrayList<>();
            int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);

            for (int l = 0; l < recognitionsSize; ++l) {
                recognitions.add(pq.poll());
            }
            Log.e("recognizeImage","recognitions.size(): "+recognitions.size());
            for (int m = 0; m < recognitionsSize; ++m) {
                Log.e("results", "result: " + recognitions.get(m).toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
