package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.huchuan.R;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/29.
 * 使用自己的图片选择库
 */
public class ImagePlugin implements IPluginModule, PictureConfig.OnSelectResultCallback {

    private Conversation.ConversationType conversationType;
    private String                        targetId;

    private boolean isSource = false;   //是否发送原图

    public ImagePlugin() {
    }

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.plugin_image_selector);
    }

    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_image);
    }

    public void onClick(Fragment currentFragment, RongExtension extension) {
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        if(PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
            this.conversationType = extension.getConversationType();
            this.targetId = extension.getTargetId();

            FunctionOptions options =
                    new FunctionOptions.Builder()
                            .setType(FunctionConfig.TYPE_IMAGE)
                            .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                            .setMaxSelectNum(9)
                            .setImageSpanCount(3)
                            .setEnableQualityCompress(false) //是否启质量压缩
                            .setEnablePixelCompress(false)   //是否启用像素压缩
                            .setEnablePreview(true)          //是否打开预览选项
                            .setEnableSource(true)           //显示勾选原图
                            .setShowCamera(true)
                            .setPreviewVideo(true)
                            .create();
            PictureConfig.getInstance().init(options).openPhoto(currentFragment.getActivity(), this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {
        List<Uri> uris = new ArrayList<>();
        for (LocalMedia media: resultList){
            isSource = media.isSource();
            Uri uri = Uri.parse("file://" + media.getPath());
            uris.add(uri);
        }
        sendImageMessage(uris);
    }


    @Override
    public void onSelectSuccess(LocalMedia media) {}

    private void sendImageMessage(List<Uri> uris){
        SendImageManager.getInstance().sendImages(this.conversationType, this.targetId, uris, isSource);
    }

}
