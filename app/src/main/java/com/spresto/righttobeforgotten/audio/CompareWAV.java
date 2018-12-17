package com.spresto.righttobeforgotten.audio;

import java.util.ArrayList;

public class CompareWAV {
    private int [] ReferenceWAV;
    private int [] CompareWAV;

    public CompareWAV(int[] ReferenceWAV, int[] CompareWAV) {
        this.ReferenceWAV = ReferenceWAV.clone();
        this.CompareWAV = CompareWAV.clone();
    }

    private int[] frameBundling(int[] bundlingFile, int bundleSize) {
        int[] res = new int[(int) Math.ceil(bundlingFile.length/bundleSize)];

        int gain = -1;
        for (int f = 0; f < bundlingFile.length; f++) {
            if (bundlingFile[f] > gain) {
                gain = bundlingFile[f];
            }

            if ((f % bundleSize) == bundleSize-1) {
                res[f/bundleSize] = gain;
                gain = -1;
            }
        }

        if (gain > -1)
            res[res.length-1] = gain;

        return res;
    }

    private int getBundleSize() {
        int BUNDLE_DIVIDER = 10000000;
        return (CompareWAV.length * ReferenceWAV.length)/BUNDLE_DIVIDER + 1;
    }

    private boolean mainWavCompare (int[] tmpReferenceWav, int[] tmpCompareWav, int startFrameAtReference, int threshold, int threshold2) {
        double X_OUT = 0.4, X_WARN = 0.2;

        int f;
        boolean warnStatus = false;
        double diffRef = (double)tmpReferenceWav[startFrameAtReference]/(double)tmpCompareWav[0];

        int thresholdCnt = 0, thresholdCnt2 = 0;
        double diffValue, deltaDiff;

        for (f = 1; f < tmpCompareWav.length; f++) {
            diffValue = (double)tmpReferenceWav[f+startFrameAtReference]/(double)tmpCompareWav[f];
            deltaDiff = Math.abs(diffRef - diffValue);
            if (warnStatus) {
                if (deltaDiff >= X_WARN) {
                    return false;
                }
                else {
                    thresholdCnt++;
                    if (thresholdCnt > threshold)
                        warnStatus = false;
                }
            }
            else {
                if (deltaDiff >= X_OUT) {
                    return false;
                }
                else if (deltaDiff >= X_WARN) {
                    thresholdCnt = thresholdCnt2 = 0;
                    diffRef = diffValue;

                    warnStatus = true;
                }
                else {
                    thresholdCnt2++;
                    if (thresholdCnt2 >= threshold2)
                        return true;
                }
            }
        }
        return true;
    }

    public ArrayList<Integer> CompareExecute() {
        ArrayList<Integer> res = new ArrayList<>();

        int bundleSize = getBundleSize();

        int[] bundledReferenceWav = frameBundling(ReferenceWAV, bundleSize);
        int[] bundledCompareWav = frameBundling(CompareWAV, bundleSize);

        int warningThreshold = bundledCompareWav.length/10;
        int successThreshold = warningThreshold * 2;

        // Phase1. Find the frames that the amplitude pattern are similar
        int f;
        for (f = 0; f <=  bundledReferenceWav.length - bundledCompareWav.length; f++ ) {
            if (mainWavCompare(bundledReferenceWav, bundledCompareWav, f, warningThreshold, successThreshold)) {
                res.add(f);
            }
        }

        // Phase2. Routine to consider the conversion of audio speed

        // Phase3. Routine to increase the accuracy of start frame
        //         Lower bundling size until the bundle size is 1

        return res;
    }
}