/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gxtc.huchuan.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListBottomDialog;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.qrcode.utils.QRCodeDecoder;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.LodingCircleView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 通用photoview
 */
public class CommonPhotoViewActivity extends BaseTitleActivity implements View.OnClickListener, CommonImageContract.View {

    @BindView(R.id.headTitle)
    TextView headTitle;
    @BindView(R.id.headBackButton)
    ImageButton headBackButton;
    @BindView(R.id.HeadRightImageButton)
    ImageButton HeadRightImageButton;
    @BindView(R.id.view_pager)
    HackyViewPager viewPager;

    private List<Uri> datas;
    private int mPosition;
    private String qrcodeResult;
    private String editPicturePath;
    private SamplePagerAdapter mAdapter;
    private QrcodeHandler mQrcodeHandler;
    private AlertDialog mAlertDialog;

    private CommonImageContract.Presenter mPresenter;

    @Override
    public void showShareResult(Uri source) {
        if (source != null) {
            ToastUtil.showShort(this, "分享成功");
        } else {
            ToastUtil.showShort(this, "分享失败");
        }
    }

    @Override
    public void showCollectResult(String errorMsg) {
        if (TextUtils.isEmpty(errorMsg)) {
            ToastUtil.showShort(this, "收藏成功");
        } else {
            ToastUtil.showShort(this, errorMsg);
        }
    }

    @Override
    public void showSaveResult(Uri file) {
        if (file == null) {
            ToastUtil.showShort(this, "保存图片失败");
        } else {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_common_photo_view);
    }

    @Override
    public void initData() {
        datas = getIntent().getParcelableArrayListExtra("photo");
        mPosition = getIntent().getIntExtra("position", 0);
        mAdapter = new SamplePagerAdapter(this, datas);
        viewPager.setAdapter(mAdapter);
        headTitle.setText((mPosition + 1) + "/" + datas.size());
        viewPager.setCurrentItem(mPosition, false);
        mQrcodeHandler = new QrcodeHandler(this);
        new CommonImagePresenter(this);
    }

    @Override
    public void initListener() {
        headBackButton.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPosition = position;
                headTitle.setText((mPosition + 1) + "/" + datas.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.headBackButton, R.id.HeadRightImageButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                this.finish();
                break;
            case R.id.HeadRightImageButton:
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                UMShareUtils utils = new UMShareUtils(CommonPhotoViewActivity.this);
                                utils.shareImg(String.valueOf(datas.get(mPosition)));
                                utils.setOnItemClickListener(
                                        new UMShareUtils.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int flag) {
                                                if (0 == flag) {//保存图片
                                                    //createByComprs(datas.get(mPosition).toString(), 0);
                                                    mPresenter.saveImage(datas.get(mPosition));
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(CommonPhotoViewActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(CommonPhotoViewActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });


                            }
                        });

                break;
        }
    }

    private void requestPermissions(View v, Bitmap image) {
        final View tempView = v;
        final Bitmap tempBitmap = image;
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        performRequestPermissions("此应用需要存储权限", permissions, 110, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                showMenuDialog(tempView, tempBitmap);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(CommonPhotoViewActivity.this, false, null,
                        getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(CommonPhotoViewActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });

    }

    private ListBottomDialog mBottomDialog;
    private List<String> dialogItems;

    private void showMenuDialog(View v, Bitmap image) {
        RxTaskHelper.getInstance().cancelAllTask();
        final String[] items = new String[]{"发送好友", "收藏", "保存图片", "编辑图片"};
        dialogItems = new ArrayList<>();
        Collections.addAll(dialogItems, items);

        mBottomDialog = new ListBottomDialog();
        mBottomDialog.setDatas(items)
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mBottomDialog != null) {
                            mBottomDialog.dismiss();
                        }
                        switch (dialogItems.get(position)) {
                            case "发送好友":
                                mPresenter.shareImageToConversation(CommonPhotoViewActivity.this, 0, datas.get(mPosition));
                                break;

                            case "收藏":
                                if (UserManager.getInstance().isLogin(CommonPhotoViewActivity.this)) {
                                    mPresenter.collectImage(datas.get(mPosition));
                                }
                                break;

                            case "保存图片":
                                mPresenter.saveImage(datas.get(mPosition));
                                break;

                            case "编辑图片":
                                mPresenter.editImage(CommonPhotoViewActivity.this, 0, datas.get(mPosition));
                                break;

                            case "识别图中二维码":
                                mQrcodeHandler.resolvingCode(qrcodeResult, "");
                                break;
                        }
                    }
                });
        mBottomDialog.show(getSupportFragmentManager(), ListBottomDialog.class.getName());

        //识别图片是否包含二维码
        if (image != null && mQrcodeHandler != null) {
            Subscription sub =
                    Observable.just(datas.get(mPosition))
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<Uri, String>() {
                                @Override
                                public String call(Uri uri) {
                                    try {
                                        Bitmap bitmap =
                                                Glide.with(CommonPhotoViewActivity.this)
                                                        .asBitmap()
                                                        .load(uri)
//                                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                                        .submit()
                                                        .get();
                                        return QRCodeDecoder.syncDecodeQRCode(bitmap);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return "";
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    if (!TextUtils.isEmpty(s) && mBottomDialog != null) {
                                        qrcodeResult = s;
                                        dialogItems.add("识别图中二维码");
                                        mBottomDialog.notifyChangeData(dialogItems);
                                    }
                                }
                            });

            RxTaskHelper.getInstance().addTask(CommonPhotoViewActivity.this, sub);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlertDialog = null;
        mQrcodeHandler.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        mPresenter.destroy();
    }

    public static void startActivity(Context context, ArrayList<Uri> datas, int position) {
        Intent intent = new Intent(context, CommonPhotoViewActivity.class);
        intent.putExtra("photo", datas);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }


    private static class SamplePagerAdapter extends PagerAdapter {

        List<Uri> datas;
        private OnPhotoListener listener;

        WeakReference<CommonPhotoViewActivity> mWeakReference;
        CommonPhotoViewActivity mPhotoViewActivity;


        public SamplePagerAdapter(CommonPhotoViewActivity photoViewActivity, List<Uri> datas) {
            this.datas = datas;
            mWeakReference = new WeakReference<>(photoViewActivity);
        }

        @Override
        public int getCount() {
            return datas != null ? datas.size() : 0;
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            mPhotoViewActivity = mWeakReference.get();
            if (mPhotoViewActivity == null) return null;
            View view = View.inflate(container.getContext(), R.layout.layout_commonphotoview, null);
            PhotoView photoView = view.findViewById(R.id.pv_common);
            LodingCircleView lvProgress = view.findViewById(R.id.lv_common);
//            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setMaximumScale(10);//设置最大缩放倍数
            if (datas != null) {
                if (datas.size() == 1) {
                    //单图有时会因图片过长导致图片被拉伸，需要重新设置宽高（后台有返回）
//                    mPhotoViewActivity.setImageSize(String.valueOf(datas.get(position)), photoView, container);
                    mPhotoViewActivity.loadImage(lvProgress, String.valueOf(datas.get(position)), photoView);
                } else {
                    //多图没有返回图片的宽高，只能这样分开显示
//                    ImageHelper.loadImage(container.getContext(), photoView, String.valueOf(datas.get(position)));
                    mPhotoViewActivity.loadImage(lvProgress, String.valueOf(datas.get(position)), photoView);
                }
            }
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoViewActivity.finish();
                }
            });

            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final ImageView view = (ImageView) v;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
                    if(bitmapDrawable != null){
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        mPhotoViewActivity.requestPermissions(view, bitmap);
                    }
                    return false;
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        public void setOnPhotoClickListener(OnPhotoListener listener) {
            this.listener = listener;
        }
    }

    //设置图片的尺寸
    private void setImageSize(String picUrl, PhotoView mPhotoView, ViewGroup container) {
        if (TextUtils.isEmpty(picUrl)) {
            return;
        } else {
            try {
                String sub = picUrl.split("\\?")[1];
                String[] var = sub.split("\\*");
                float tempWidth = Float.valueOf(var[0]);
                float tempheight = Float.valueOf(var[1]);
                if (mPhotoView != null) {
//                    container.addView(mPhotoView);
//                    RequestOptions options = new RequestOptions().centerCrop().override((int) tempWidth, (int) tempheight).placeholder(R.drawable.plugin_image);
//                    Glide.with(this)
//                            .load(picUrl)
//                            .apply(options)
//                            .into(mPhotoView);
//                    loadImage(picUrl, mPhotoView);

                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i(e.getMessage());
                if (mPhotoView != null) {
//                    container.addView(mPhotoView);
                    RequestOptions options = new RequestOptions().centerInside();
                    Glide.with(this)
                            .load(picUrl)
                            .apply(options)
                            .into(mPhotoView);

                }
            }
        }

    }

    interface OnPhotoListener {
        void showPhotoClick(Bitmap bitmap);
    }

    private void loadImage(final LodingCircleView lvProgress, String uri, final PhotoView photoView) {
        ImageLoader.getInstance().displayImage(uri, photoView, this.createDisplayImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                lvProgress.setVisibility(View.VISIBLE);
                lvProgress.setProgerss(0, true);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                lvProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                lvProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                lvProgress.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int current, int total) {
                lvProgress.setProgerss(current * 100 / total, true);
                if (current == total) {
                    lvProgress.setVisibility(View.GONE);
                } else {
                    lvProgress.setVisibility(View.VISIBLE);
                    lvProgress.stopAnimAutomatic();
                }
            }
        });
    }

    private DisplayImageOptions createDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        return builder.resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.photoview_loading).handler(new Handler()).build();
    }
}
