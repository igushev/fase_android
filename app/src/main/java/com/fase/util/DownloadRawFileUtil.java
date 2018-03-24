package com.fase.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class DownloadRawFileUtil {

    private static final int BUF_SIZE = 1024;

    public static Single<File> saveRawToFile(Single<ResponseBody> rawRequest, File file) {
        return rawRequest
                .flatMap(responseBody -> {
                    try {
                        file.delete();
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        copyStream(responseBody.byteStream(), fos);
                        return Single.just(file);
                    } catch (IOException e) {
                        file.delete();
                        Timber.e(e, "Error saving file");
                        throw new RuntimeException(e);
                    } finally {
                        responseBody.close();
                    }
                });
    }

    private static long copyStream(InputStream sourceStream, OutputStream destinationStream) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        long total = 0;
        while (true) {
            int r = sourceStream.read(buf);
            if (r == -1) {
                break;
            }
            destinationStream.write(buf, 0, r);
            total += r;
        }

        return total;
    }
}
