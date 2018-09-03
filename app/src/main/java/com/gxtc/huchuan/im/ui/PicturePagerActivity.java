package com.gxtc.huchuan.im.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListBottomDialog;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.qrcode.utils.QRCodeDecoder;
import com.gxtc.huchuan.ui.common.CommonImageContract;
import com.gxtc.huchuan.ui.common.CommonImagePresenter;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.luck.picture.lib.ui.PictureEditActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.forward.androids.utils.ImageUtils;
import io.rong.common.RLog;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.DisplayImageOptions.Builder;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.imageaware.ImageAware;
import io.rong.imageloader.core.imageaware.ImageViewAware;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper.ILoadImageCallback;
import io.rong.imkit.plugin.image.HackyViewPager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongCommonDefine.GetMessageDirection;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.photoview.PhotoView;
import io.rong.photoview.PhotoViewAttacher.OnPhotoTapListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PicturePagerActivity extends BaseTitleActivity implements OnLongClickListener,CommonImageContract.View {
    private static final String TAG = "PicturePagerActivity";
    private static final int IMAGE_MESSAGE_COUNT = 10;
    private HackyViewPager mViewPager;
    private Message currentMessage;
    private ImageMessage mCurrentImageMessage;
    private ConversationType mConversationType;
    private int mCurrentMessageId;
    private String mTargetId = null;
    private int mCurrentIndex = 0;
    private ImageAware mDownloadingImageAware;
    private PicturePagerActivity.ImageAdapter mImageAdapter;
    private boolean isFirstTime = false;
    private String qrcodeResult = "";   //二维码识别结果，如果有说明是二维码
    private QrcodeHandler                 mQrcodeHandler;
    private CommonImageContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;

    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            PicturePagerActivity.this.mCurrentIndex = position;
            View view = PicturePagerActivity.this.mViewPager.findViewById(position);
            if(view != null) {
                PicturePagerActivity.this.mImageAdapter.updatePhotoView(position, view);
            }

            if(position == PicturePagerActivity.this.mImageAdapter.getCount() - 1) {
                PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId(), GetMessageDirection.BEHIND);
            } else if(position == 0) {
                PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId(), GetMessageDirection.FRONT);
            }

        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    public PicturePagerActivity() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.rc_fr_photo);
        currentMessage = (Message)this.getIntent().getParcelableExtra("message");
        this.mCurrentImageMessage = (ImageMessage)currentMessage.getContent();
        this.mConversationType = currentMessage.getConversationType();
        this.mCurrentMessageId = currentMessage.getMessageId();
        this.mTargetId = currentMessage.getTargetId();
        this.mViewPager = (HackyViewPager)this.findViewById(id.viewpager);
        this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
        this.mImageAdapter = new PicturePagerActivity.ImageAdapter();
        this.isFirstTime = true;
        this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.FRONT);
        this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.BEHIND);
        this.mQrcodeHandler = new QrcodeHandler(this);
        new CommonImagePresenter(this);
    }

    private void getConversationImageUris(int mesageId, final GetMessageDirection direction) {
        if(this.mConversationType != null && !TextUtils.isEmpty(this.mTargetId)) {
            RongIMClient.getInstance().getHistoryMessages(this.mConversationType, this.mTargetId,
                    "RC:ImgMsg", mesageId, 10, direction, new ResultCallback<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            ArrayList lists = new ArrayList();
                            if(messages != null) {
                                if(direction.equals(GetMessageDirection.FRONT)) {
                                    Collections.reverse(messages);
                                }

                                for(int i = 0; i < messages.size(); ++i) {
                                    Message message = (Message)messages.get(i);
                                    if(message.getContent() instanceof ImageMessage) {
                                        ImageMessage imageMessage = (ImageMessage)message.getContent();
                                        Uri largeImageUri = imageMessage.getLocalUri() == null?imageMessage.getRemoteUri():imageMessage.getLocalUri();
                                        if(imageMessage.getThumUri() != null && largeImageUri != null) {
                                            lists.add(PicturePagerActivity.this.new ImageInfo(message.getMessageId(), imageMessage.getThumUri(), largeImageUri));
                                        }
                                    }
                                }
                            }

                            if(direction.equals(GetMessageDirection.FRONT) && PicturePagerActivity.this.isFirstTime) {
                                lists.add(PicturePagerActivity.this.new ImageInfo(PicturePagerActivity.this.mCurrentMessageId, PicturePagerActivity.this.mCurrentImageMessage.getThumUri(), PicturePagerActivity.this.mCurrentImageMessage.getLocalUri() == null?PicturePagerActivity.this.mCurrentImageMessage.getRemoteUri():PicturePagerActivity.this.mCurrentImageMessage.getLocalUri()));
                                PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                                PicturePagerActivity.this.mViewPager.setAdapter(PicturePagerActivity.this.mImageAdapter);
                                PicturePagerActivity.this.isFirstTime = false;
                                PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size() - 1);
                                PicturePagerActivity.this.mCurrentIndex = lists.size() - 1;
                            } else if(lists.size() > 0) {
                                PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                                PicturePagerActivity.this.mImageAdapter.notifyDataSetChanged();
                                if(direction.equals(GetMessageDirection.FRONT)) {
                                    PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size());
                                    PicturePagerActivity.this.mCurrentIndex = lists.size();
                                }
                            }
                        }

                        @Override
                        public void onError(ErrorCode errorCode) {

                        }
                    });
        }

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    public boolean onPictureLongClick(View v, Uri thumbUri, Uri largeImageUri) {
        return false;
    }

    public boolean onLongClick(View v) {
        requestPermissre(v);
        return true;
    }


    private ListBottomDialog mBottomDialog;
    private List<String> dialogItems;
    private void requestPermissre(View v){
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        performRequestPermissions("此应用需要存储权限", permissions, 110, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                PicturePagerActivity.ImageInfo imageInfo = PicturePagerActivity.this.mImageAdapter.getImageInfo(PicturePagerActivity.this.mCurrentIndex);
                if(imageInfo != null) {
                    Uri       thumbUri      = imageInfo.getThumbUri();
                    final Uri largeImageUri = imageInfo.getLargeImageUri();
                    if(largeImageUri == null) {
                        return ;
                    }

                    final File file;
                    if(!largeImageUri.getScheme().startsWith("http") && !largeImageUri.getScheme().startsWith("https")) {
                        file = new File(largeImageUri.getPath());
                    } else {
                        file = ImageLoader.getInstance().getDiskCache().get(largeImageUri.toString());
                    }
                    final String[] items = new String[]{"发送好友", "收藏", "保存图片", "编辑图片"};
                    dialogItems = new ArrayList<>();
                    Collections.addAll(dialogItems, items);

                    mBottomDialog = new ListBottomDialog();
                    mBottomDialog.setDatas(items)
                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    mBottomDialog.dismiss();
                                    switch (dialogItems.get(position)){
                                        case "发送好友":
                                            mPresenter.shareImageToConversation(PicturePagerActivity.this, 0, largeImageUri);
                                            break;

                                        case "收藏":
                                            if(UserManager.getInstance().isLogin(PicturePagerActivity.this)){
                                                mPresenter.collectImage(largeImageUri);
                                            }
                                            break;

                                        case "保存图片":
                                            mPresenter.saveImage(largeImageUri);
                                            break;

                                        case "编辑图片":
                                            mPresenter.editImage(PicturePagerActivity.this, 0, largeImageUri);
                                            break;

                                        case "识别图中二维码":
                                            if(currentMessage != null && currentMessage.getConversationType() == ConversationType.PRIVATE){
                                                mQrcodeHandler.resolvingCode(qrcodeResult,currentMessage.getSenderUserId());
                                            }else{
                                                mQrcodeHandler.resolvingCode(qrcodeResult,"");
                                            }
                                            break;
                                    }
                                }
                            });
                    mBottomDialog.show(getSupportFragmentManager(), ListBottomDialog.class.getName());


                    //识别图片是否包含二维码
                    if(file != null && mQrcodeHandler != null) {
                        Subscription sub =
                                Observable.just(file)
                                        .subscribeOn(Schedulers.io())
                                        .map(new Func1<File, String>() {
                                            @Override
                                            public String call(File file) {
                                                try {
                                                    Bitmap bitmap =
                                                            Glide.with(MyApplication.getInstance())
                                                                    .asBitmap()
                                                                    .load(file)
                                                                    .apply(new RequestOptions()
                                                                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                                                                    .submit(480, 800)
                                                                    .get();
                                                    LogUtil.i("bitmap.getWidth()   : " + bitmap.getWidth() + "  bitmap.getWidth() :  " + bitmap.getHeight());
                                                    return QRCodeDecoder.syncDecodeQRCode(bitmap);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<String>() {
                                            @Override
                                            public void call(String s) {
                                                if(!TextUtils.isEmpty(s) && mBottomDialog != null){
                                                    qrcodeResult = s;
                                                    dialogItems.add("识别图中二维码");
                                                    mBottomDialog.notifyChangeData(dialogItems);
                                                }
                                            }
                                        });

                        RxTaskHelper.getInstance().addTask(PicturePagerActivity.this, sub);
                    }
                }
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(PicturePagerActivity.this, false, null,
                        getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(PicturePagerActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });
    }



    @Override
    public void showShareResult(Uri source) {
        if(source != null){
            ToastUtil.showShort(this, "分享成功");
        }else{
            ToastUtil.showShort(this, "分享失败");
        }
    }

    @Override
    public void showCollectResult(String errorMsg) {
        if(TextUtils.isEmpty(errorMsg)){
            ToastUtil.showShort(this, "收藏成功");
        }else{
            ToastUtil.showShort(this, errorMsg);
        }
    }

    @Override
    public void showSaveResult(Uri file) {
        if(file == null){
            ToastUtil.showShort(this, "保存图片失败");
        }else{
            ToastUtil.showLong(this, "图片已保存至: " + file.getPath());
        }
    }

    @Override
    public void setPresenter(CommonImageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {

    }

    private class ImageInfo {
        private int messageId;
        private Uri thumbUri;
        private Uri largeImageUri;

        ImageInfo(int messageId, Uri thumbnail, Uri largeImageUri) {
            this.messageId = messageId;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public int getMessageId() {
            return this.messageId;
        }

        public Uri getLargeImageUri() {
            return this.largeImageUri;
        }

        public Uri getThumbUri() {
            return this.thumbUri;
        }
    }

    private class ImageAdapter extends PagerAdapter {
        private ArrayList<PicturePagerActivity.ImageInfo> mImageList;

        private ImageAdapter() {
            this.mImageList = new ArrayList();
        }

        private View newView(Context context, PicturePagerActivity.ImageInfo imageInfo) {
            View result = LayoutInflater.from(context).inflate(layout.rc_fr_image, (ViewGroup)null);
            PicturePagerActivity.ImageAdapter.ViewHolder holder = new PicturePagerActivity.ImageAdapter.ViewHolder();
            holder.progressBar = (ProgressBar)result.findViewById(id.rc_progress);
            holder.progressText = (TextView)result.findViewById(id.rc_txt);
            holder.photoView = (PhotoView)result.findViewById(id.rc_photoView);
            holder.photoView.setOnLongClickListener(PicturePagerActivity.this);
            holder.photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                public void onPhotoTap(View view, float x, float y) {
                    PicturePagerActivity.this.finish();
                }

                public void onOutsidePhotoTap() {
                }
            });
            result.setTag(holder);
            return result;
        }

        public void addData(ArrayList<PicturePagerActivity.ImageInfo> newImages, boolean direction) {
            if(newImages != null && newImages.size() != 0) {
                if(this.mImageList.size() == 0) {
                    this.mImageList.addAll(newImages);
                } else if(direction && !PicturePagerActivity.this.isFirstTime && !this.isDuplicate(((PicturePagerActivity.ImageInfo)newImages.get(0)).getMessageId())) {
                    ArrayList temp = new ArrayList();
                    temp.addAll(this.mImageList);
                    this.mImageList.clear();
                    this.mImageList.addAll(newImages);
                    this.mImageList.addAll(this.mImageList.size(), temp);
                } else if(!PicturePagerActivity.this.isFirstTime && !this.isDuplicate(((PicturePagerActivity.ImageInfo)newImages.get(0)).getMessageId())) {
                    this.mImageList.addAll(this.mImageList.size(), newImages);
                }

            }
        }

        private boolean isDuplicate(int messageId) {
            Iterator i$ = this.mImageList.iterator();

            PicturePagerActivity.ImageInfo info;
            do {
                if(!i$.hasNext()) {
                    return false;
                }

                info = (PicturePagerActivity.ImageInfo)i$.next();
            } while(info.getMessageId() != messageId);

            return true;
        }

        public PicturePagerActivity.ImageInfo getItem(int index) {
            return (PicturePagerActivity.ImageInfo)this.mImageList.get(index);
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.mImageList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            RLog.i("PicturePagerActivity", "instantiateItem.position:" + position);
            View imageView = this.newView(container.getContext(), (PicturePagerActivity.ImageInfo)this.mImageList.get(position));
            this.updatePhotoView(position, imageView);
            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            RLog.i("PicturePagerActivity", "destroyItem.position:" + position);
            PicturePagerActivity.ImageAdapter.ViewHolder holder = (PicturePagerActivity.ImageAdapter.ViewHolder)container.findViewById(position).getTag();
            holder.photoView.setImageURI((Uri)null);
            container.removeView((View)object);
        }

        private void updatePhotoView(int position, View view) {
            final PicturePagerActivity.ImageAdapter.ViewHolder holder = (PicturePagerActivity.ImageAdapter.ViewHolder)view.getTag();
            Uri originalUri = ((PicturePagerActivity.ImageInfo)this.mImageList.get(position)).getLargeImageUri();
            Uri thumbUri = ((PicturePagerActivity.ImageInfo)this.mImageList.get(position)).getThumbUri();
            if(originalUri != null && thumbUri != null) {
                File file;
                if(originalUri.getScheme() == null || !originalUri.getScheme().startsWith("http") && !originalUri.getScheme().startsWith("https")) {
                    file = new File(originalUri.getPath());
                } else {
                    file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
                }

                if(file != null && file.exists()) {
                    AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file.getAbsolutePath());
                    Bitmap imageAware2 = AlbumBitmapCacheHelper.getInstance().getBitmap(file.getAbsolutePath(), 0, 0, new ILoadImageCallback() {
                        public void onLoadImageCallBack(Bitmap bitmap, String p, Object... objects) {
                            if(bitmap != null) {
                                holder.photoView.setImageBitmap(bitmap);
                            }
                        }
                    }, new Object[]{Integer.valueOf(position)});
                    if(imageAware2 != null) {
                        holder.photoView.setImageBitmap(imageAware2);
                    } else {
                        Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
                        holder.photoView.setImageDrawable(drawable);
                    }
                } else if(position != PicturePagerActivity.this.mCurrentIndex) {
                    Drawable imageAware = Drawable.createFromPath(thumbUri.getPath());
                    holder.photoView.setImageDrawable(imageAware);
                } else {
                    ImageViewAware imageAware1 = new ImageViewAware(holder.photoView);
                    if(PicturePagerActivity.this.mDownloadingImageAware != null) {
                        ImageLoader.getInstance().cancelDisplayTask(PicturePagerActivity.this.mDownloadingImageAware);
                    }

                    //这里会造成内存泄露
                    ImageLoader.getInstance().displayImage(originalUri.toString(), imageAware1, this.createDisplayImageOptions(thumbUri), new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressText.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.progressText.setText("0%");
                        }

                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressText.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressText.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        public void onLoadingCancelled(String imageUri, View view) {
                            holder.progressText.setVisibility(View.GONE);
                            holder.progressText.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressText.setText(current * 100 / total + "%");
                            if(current == total) {
                                holder.progressText.setVisibility(View.GONE);
                                holder.progressBar.setVisibility(View.GONE);
                            } else {
                                holder.progressText.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    //ImageHelper.loadImage(PicturePagerActivity.this,holder.photoView,thumbUri.toString());
                    PicturePagerActivity.this.mDownloadingImageAware = imageAware1;
                }

            } else {
                RLog.e("PicturePagerActivity", "large uri and thumbnail uri of the image should not be null.");
            }
        }

        public PicturePagerActivity.ImageInfo getImageInfo(int position) {
            return (PicturePagerActivity.ImageInfo)this.mImageList.get(position);
        }

        private DisplayImageOptions createDisplayImageOptions(Uri uri) {
            Builder builder = new Builder();
            Drawable drawable = Drawable.createFromPath(uri.getPath());
            return builder.resetViewBeforeLoading(false).cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Config.RGB_565).showImageForEmptyUri(drawable).showImageOnFail(drawable).showImageOnLoading(drawable).handler(new Handler()).build();
        }

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            PhotoView photoView;

            public ViewHolder() {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PictureEditActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null){
            setResult(RESULT_OK, data);
            finish();
        }
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
