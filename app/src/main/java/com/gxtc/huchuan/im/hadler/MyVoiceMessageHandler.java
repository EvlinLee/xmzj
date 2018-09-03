//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gxtc.huchuan.im.hadler;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.im.Extra;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyVoiceMessageHandler {
    private static final String TAG        = "MyVoiceMessageHandler";
    private static final String VOICE_PATH = "/voice/";


    public static void decodeMessage(VoiceMessage model) {
        Uri    uri   = obtainVoiceUri(MyApplication.getInstance(), model);
        Extra  extra = new Extra(model.getExtra());
        String name  = extra.getMsgId() + ".amr";
        if (TextUtils.isEmpty(extra.getMsgId()) || "0".equals(extra.getMsgId())) {
            name = System.currentTimeMillis() + ".amr";
        }

        File file = new File(uri.toString() + "/voice/" + name);
        if (!TextUtils.isEmpty(model.getBase64()) && !file.exists()) {
            try {
                byte[] e = Base64.decode(model.getBase64(), 2);
                file = saveFile(e, uri.toString() + "/voice/", name);
            } catch (IllegalArgumentException var7) {
                RLog.e("MyVoiceMessageHandler", "afterDecodeMessage Not Base64 Content!");
                var7.printStackTrace();
            } catch (IOException var8) {
                var8.printStackTrace();
            }
        }
        model.setUri(Uri.fromFile(file));
    }

    public void encodeMessage(Message message) {
        VoiceMessage model     = (VoiceMessage) message.getContent();
        Uri          uri       = obtainVoiceUri(MyApplication.getInstance(), model);
        byte[]       voiceData = FileUtils.getByteFromUri(model.getUri());
        File         file      = null;

        try {
            String e = Base64.encodeToString(voiceData, 2);
            model.setBase64(e);
            String name = message.getMessageId() + ".amr";
            file = saveFile(voiceData, uri.toString() + "/voice/", name);
        } catch (IllegalArgumentException var8) {
            RLog.e("MyVoiceMessageHandler", "beforeEncodeMessage Not Base64 Content!");
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        if (file != null && file.exists()) {
            model.setUri(Uri.fromFile(file));
        }

    }

    private static File saveFile(byte[] data, String path, String fileName) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(data);
        bos.flush();
        bos.close();
        return file;
    }

    private static Uri obtainVoiceUri(Context context, VoiceMessage model) {
        File   file   = context.getFilesDir();
        String path   = file.getAbsolutePath();
        Extra  extra  = new Extra(model.getExtra());
        String userId = model.getUserInfo().getUserId();

        return Uri.parse(path + File.separator + userId);
    }
}
