package com.fase.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Generates unique device id
 */
public class DeviceIdGenerator {

    private static final String EMULATOR_ANDROID_ID = "9774d56d682e549c";
    private static final String[] BAD_SERIAL_PATTERNS = {"1234567", "abcdef", "dead00beef"};

    public static String readDeviceId(Context context) {
        String deviceId;
        String androidSerialId = null;
        try {
            // try to get device serial number
            androidSerialId = Build.SERIAL;
        } catch (NoSuchFieldError ignored) {
        }

        if (!TextUtils.isEmpty(androidSerialId) && !Build.UNKNOWN.equals(androidSerialId) && !isBadSerial(androidSerialId)) {
            deviceId = androidSerialId;
        } else {
            // try to use Settings.Secure.ANDROID_ID
            // could be different after factory reset
            String androidSecureId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidSecureId) && !EMULATOR_ANDROID_ID.equals(androidSecureId) && !isBadDeviceId(androidSecureId)
                    && androidSecureId.length() == EMULATOR_ANDROID_ID.length()) {
                deviceId = androidSecureId;
            } else {
                deviceId = SoftInstallationId.id(context);
            }
        }
        return UUID.nameUUIDFromBytes(deviceId.getBytes()).toString();
    }

    private static boolean isBadDeviceId(String id) {
        // empty or contains only spaces or 0
        return TextUtils.isEmpty(id) || TextUtils.isEmpty(id.replace('0', ' ').replace('-', ' ').trim());
    }

    private static boolean isBadSerial(String id) {
        if (!TextUtils.isEmpty(id)) {
            id = id.toLowerCase();
            for (String pattern : BAD_SERIAL_PATTERNS) {
                if (id.contains(pattern)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * Class generates id for current application installation
     */
    private static class SoftInstallationId {
        private static String sID = null;
        private static final String INSTALLATION = "INSTALLATION";

        public synchronized static String id(Context context) {
            if (sID == null) {
                File installation = new File(context.getFilesDir(), INSTALLATION);
                try {
                    if (!installation.exists()) {
                        writeInstallationFile(installation);
                    }
                    sID = readInstallationFile(installation);
                } catch (Exception epicFail) {
                    throw new RuntimeException(epicFail);
                }
            }
            return sID;
        }

        private static String readInstallationFile(File installation) throws IOException {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            try {
                byte[] bytes = new byte[(int) f.length()];
                f.readFully(bytes);
                f.close();
                return new String(bytes);
            } finally {
                f.close();
            }
        }

        private static void writeInstallationFile(File installation) throws IOException {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(installation);
                String id = UUID.randomUUID().toString();
                out.write(id.getBytes());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }
}
