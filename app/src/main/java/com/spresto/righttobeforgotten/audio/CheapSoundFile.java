package com.spresto.righttobeforgotten.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CheapSoundFile {
    public interface ProgressListener {
        boolean reportProgress(double fractionComplete);
    }

    public interface Factory {
        public CheapSoundFile create();
        public String[] getSupportedExtensions();
    }

    static Factory[] sSubclassFactories = new Factory[] {
            CheapAAC.getFactory(),
            CheapAMR.getFactory(),
            CheapMP3.getFactory(),
            CheapWAV.getFactory(),
    };

    static ArrayList<String> sSupportedExtensions = new ArrayList<String>();
    static HashMap<String, Factory> sExtensionMap = new HashMap<String, Factory>();

    static {
        for (Factory f : sSubclassFactories) {
            for (String extension : f.getSupportedExtensions()) {
                sSupportedExtensions.add(extension);
                sExtensionMap.put(extension, f);
            }
        }
    }

    public static CheapSoundFile create(String fileName, ProgressListener progressListener) throws java.io.FileNotFoundException, java.io.IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new java.io.FileNotFoundException(fileName);
        }
        String name = f.getName().toLowerCase();
        String[] components = name.split("\\.");
        if (components.length < 2) {
            return null;
        }
        Factory factory = sExtensionMap.get(components[components.length - 1]);
        if (factory == null) {
            return null;
        }
        CheapSoundFile soundFile = factory.create();
        soundFile.setProgressListener(progressListener);
        soundFile.ReadFile(f);
        return soundFile;
    }

    public static boolean isFilenameSupported(String filename) {
        String[] components = filename.toLowerCase().split("\\.");
        if (components.length < 2) {
            return false;
        }
        return sExtensionMap.containsKey(components[components.length - 1]);
    }

    public static String[] getSupportedExtensions() {
        return sSupportedExtensions.toArray(
                new String[sSupportedExtensions.size()]);
    }

    protected ProgressListener mProgressListener = null;
    protected File mInputFile = null;

    protected CheapSoundFile() {
    }

    public void ReadFile(File inputFile)
            throws java.io.FileNotFoundException,
            java.io.IOException {
        mInputFile = inputFile;
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public int getNumFrames() {
        return 0;
    }

    public int getSamplesPerFrame() {
        return 0;
    }

    public int[] getFrameGains() {
        return null;
    }

    public int getFileSizeBytes() {
        return 0;
    }

    public int getAvgBitrateKbps() {
        return 0;
    }

    public int getSampleRate() {
        return 0;
    }

    public int getChannels() {
        return 0;
    }

    public String getFiletype() {
        return "Unknown";
    }

    public int getSeekableFrameOffset(int frame) {
        return -1;
    }

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static String bytesToHex (byte hash[]) {
        char buf[] = new char[hash.length * 2];
        for (int i = 0, x = 0; i < hash.length; i++) {
            buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
            buf[x++] = HEX_CHARS[hash[i] & 0xf];
        }
        return new String(buf);
    }

    public void WriteFile(File outputFile, int startFrame, int numFrames)
            throws java.io.IOException {
    }
}