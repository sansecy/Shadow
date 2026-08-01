package com.tencent.shadow.core.utils;

import java.io.File;

public class FileUtils {

    public static void deleteDirectories(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectories(f);
                }
            }
        }
        file.delete();
    }

}
