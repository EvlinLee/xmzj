package com.gxtc.huchuan.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by 宋家任 on 2016/12/2.
 * 友盟分享工具类
 * 2017/1/3
 * 一期无微博分享
 * 2017/5/2
 * 应该把运行时权限的方法写在这个类里，后续要优化,因为太多地方用到分享了，，所以暂时不改有空再改，，
 */

public class UMShareUtils {

    private FragmentActivity mActivity;
    private OnItemClickListener onclick;

    public WeakReference<FragmentActivity> mActivityWeakReferencel;

    public UMShareUtils(FragmentActivity mActivity) {
        mActivityWeakReferencel = new WeakReference<>(mActivity);
        if (mActivityWeakReferencel.get() != null) {
            this.mActivity = mActivityWeakReferencel.get();
        }
    }

    /**
     * 以后用到分享的地方都统一用这个方法
     */
    public void shareCustom(String imgUrl, final String title, final String text, final String url, ShareDialog.Action[] actions, final ShareDialog.OnShareLisntener lisntener) {
        ShareDialog shareDialog ;
        UMImage image ;
        if (!"".equals(imgUrl)) {
            image = new UMImage(mActivity, imgUrl);//网络图片

        } else {
            image = new UMImage(mActivity, R.mipmap.person_icon_head_share);
        }
        //通过类名判断是否需要去掉微信搜索的（目前只有交易这里需要去掉）
        if ("GoodsDetailedActivity".equals(mActivity.getClass().getSimpleName())) {
            shareDialog = new ShareDialog().addButtons(actions).clearDefaultePlatforms();
        } else {
            shareDialog = new ShareDialog().addButtons(actions);
        }
        final UMImage finalImage = image;
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_WEIXIN)
                        || key.equals(ShareDialog.ACTION_WEIXIN_CIRCLE)
                        || key.equals(ShareDialog.ACTION_QQ)
                        || key.equals(ShareDialog.ACTION_QZONE)) {

                    UMWeb umWeb = new UMWeb(url, title, text, finalImage);

                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                    //复制链接
                } else if (key.equals(ShareDialog.ACTION_COPY)) {
                    ClipboardManager cmb = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(url);
                    ToastUtil.showShort(mActivity, "已复制");

                } else {
                    lisntener.onShare(key, media);
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 图文分享(图片从网络获取)
     *
     * @param imageUrl 图片url
     * @param title    标题
     * @param text     内容
     * @param url      点击跳转的url
     */
    public void shareNews(String imageUrl, final String title, final String text, final String url) {

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_REFRESH, R.drawable.share_icon_refresh, null));

        final UMImage image = new UMImage(mActivity, imageUrl);//网络图片
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                    onclick.onItemClick(2);

                } else if (key.equals(ShareDialog.ACTION_REFRESH)) {
                    onclick.onItemClick(3);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 分享个人主页
     */
    public void sharePersonalHome(String imageUrl, final String title, final String text, final String url) {

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        final UMImage image = new UMImage(mActivity, imageUrl);         //网络图片
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(0);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 圈子免费邀请
     */
    public void shareFreeCircle(String imageUrl, final String title, final String text, final String url) {
        List<SHARE_MEDIA> medias = new ArrayList<>();
        medias.add(SHARE_MEDIA.WEIXIN);
        ShareDialog shareDialog = new ShareDialog()
                .setPlatforms(medias)
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        final UMImage image = new UMImage(mActivity, imageUrl);         //网络图片
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(0);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    private static final String SHARE_INVITATION = "share_invitation";      //邀请
    private static final String SHARE_DYNAMIC = "share_in_dynamic";         //圈子动态
    private static final String SHARE_FRIENDS = "share_in_friends";         //好友
    private static final String SHARE_COLLECT = "collect";                  //收藏
    private static final String SHARE_QRCODE = "share_icon_erweima";        //二维码

    public void shareCircleIssueDynamic(String imageUrl, final String title, final String text, final String url) {

        final UMImage image = new UMImage(mActivity, imageUrl);     //网络图片

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                    onclick.onItemClick(2);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    public void shareCollect(String imageUrl, final String title, final String text, final String url) {

        final UMImage image = new UMImage(mActivity, imageUrl);     //网络图片

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(0);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    public void shareCollect(int imageUrl, final String title, final String text, final String url) {

        final UMImage image = new UMImage(mActivity, imageUrl);     //网络图片

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(0);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    public void shareCircleIssueDynamic(int imgRes, final String title, final String text, final String url) {
        final UMImage image = new UMImage(mActivity, imgRes);           //本地

        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                    onclick.onItemClick(2);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });

        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 分享动态
     *
     * @param imageUrl
     * @param title
     * @param text
     * @param url
     */
    public void shareDynamic(String imageUrl, final String title, final String text, final String url, boolean isMine) {
        final UMImage image = new UMImage(mActivity, imageUrl);//网络图片

        if (isMine) {
            ShareDialog shareDialog = new ShareDialog();
            shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
                @Override
                public void onShare(String key, SHARE_MEDIA media) {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            });
            shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());

        } else {
            ShareDialog shareDialog = new ShareDialog()
                    .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null));

            shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
                @Override
                public void onShare(String key, SHARE_MEDIA media) {
                    if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                        onclick.onItemClick(1);

                    } else {
                        UMWeb umWeb = new UMWeb(url, title, text, image);
                        new ShareAction(mActivity)
                                .withMedia(umWeb)
                                .setPlatform(media)
                                .setCallback(umShareListener)
                                .share();
                    }
                }
            });
            shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
        }
    }


    public void shareDynamic(int imageRsid, final String title, final String text, final String url, boolean isMine) {
        final UMImage image = new UMImage(mActivity, imageRsid);//本地图片
        if (isMine) {
            ShareDialog shareDialog = new ShareDialog();
            shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
                @Override
                public void onShare(String key, SHARE_MEDIA media) {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            });
            shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());

        } else {
            ShareDialog shareDialog = new ShareDialog()
                    .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null));

            shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
                @Override
                public void onShare(String key, SHARE_MEDIA media) {
                    if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                        onclick.onItemClick(1);

                    } else {
                        UMWeb umWeb = new UMWeb(url, title, text, image);
                        new ShareAction(mActivity)
                                .withMedia(umWeb)
                                .setPlatform(media)
                                .setCallback(umShareListener)
                                .share();

                    }
                }
            });
            shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
        }

    }

    public void shareMakeMoney(String imageUrl, final String title, final String text, final String url, SHARE_MEDIA[] platform) {
        List<SHARE_MEDIA> list = new ArrayList<>();
        Collections.addAll(list, platform);
        final UMImage image = new UMImage(mActivity, imageUrl);     //网络图片
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .setPlatforms(list)
                .setTitle("仅分享至微信、朋友圈才可以获得佣金收益，分享到新媒之家app内部不能获得佣金收益", mActivity.getResources().getColor(R.color.pay_failure));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(1);

                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    public void shareDefault(String imageUrl, final String title, final String text, final String url, SHARE_MEDIA[] platform) {
        final UMImage image = new UMImage(mActivity, imageUrl);     //网络图片
        ShareDialog shareDialog = new ShareDialog();

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                UMWeb umWeb = new UMWeb(url, title, text, image);
                new ShareAction(mActivity)
                        .withMedia(umWeb)
                        .setPlatform(media)
                        .setCallback(umShareListener)
                        .share();

            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    /**
     * 默认分享
     *
     * @param resid
     * @param title
     * @param text
     * @param url
     */
    public void shareDefault(int resid, final String title, final String text, final String url) {
        shareDefault(resid, title, text, url, new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE});
    }

    public void shareDefault(int resid, final String title, final String text, final String url, SHARE_MEDIA[] platform) {
        final UMImage image = new UMImage(mActivity, resid);//网络图片
        ShareDialog shareDialog = new ShareDialog().addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null));
        ;

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                //复制
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);
                }
                if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(1);
                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }


            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 直播图文分享(图片从网络获取)
     *
     * @param imageUrl 图片url
     * @param title    标题
     * @param text     内容
     * @param url      点击跳转的url
     */
    public void shareLive(String imageUrl, final String title, final String text, final String url) {
        final UMImage image = new UMImage(mActivity, imageUrl);//网络图片
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_INVITE, R.drawable.share_icon_invitation, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                //邀请
                if (key.equals(ShareDialog.ACTION_INVITE)) {
                    onclick.onItemClick(0);

                    //圈子动态
                } else if (key.equals(ShareDialog.ACTION_CIRCLE)) {
                    onclick.onItemClick(1);

                    //分享好友
                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(2);

                    //收藏
                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(3);

                } else if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(4);

                    //分享到其他应用
                } else {
                    UMWeb umWeb = new UMWeb(url, title, text, image);
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onclick = onItemClickListener;
    }

    //收藏监听回调
    public interface OnItemClickListener {
        void onItemClick(int flag);
    }

    /**
     * @param imageUrl 图片url
     * @param title    标题
     * @param text     文字
     * @param url      点击的链接
     * @param flag     标志 0微信 1朋友圈 2新浪
     */
    @JavascriptInterface
    public void shareSpecific(final String imageUrl, final String title, final String text, final String url, final String flag) {
        rx.Observable.create(new rx.Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                UMImage image = new UMImage(mActivity, imageUrl);//网络图片
                UMWeb umWeb = new UMWeb(url, title, text, image);
                if ("0".equals(flag)) {
                    new ShareAction(mActivity)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withMedia(umWeb)
                            .setCallback(umShareListener)
                            .share();
                } else if ("1".equals(flag)) {
                    new ShareAction(mActivity)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withMedia(umWeb)
                            .setCallback(umShareListener)
                            .share();
                } else if ("2".equals(flag)) {
                    new ShareAction(mActivity)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .withMedia(umWeb)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                    }
                });

    }


    /**
     * 分享特定平台
     * 最后在分享所在的Activity里复写onActivityResult方法,注意不可在fragment中实现，如果在fragment中调用分享，
     * 在fragment依赖的Activity中实现，如果不实现onActivityResult方法，会导致分享或回调无法正常进行
     * UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);复写这个方法
     */
    public void shareOne(SHARE_MEDIA shareMediaEnum, String imgUrl, String title, String content, String url) {
        UMImage image = new UMImage(mActivity, imgUrl);
        UMWeb umWeb = new UMWeb(url, title, content, image);
        new ShareAction(mActivity).setPlatform(shareMediaEnum)
                .withMedia(umWeb)
                .setCallback(umShareListener)
                .share();
    }


    public void shareOne(SHARE_MEDIA shareMediaEnum, int resid, String title, String content, String url) {
        UMImage image = new UMImage(mActivity, resid);
        UMWeb umWeb = new UMWeb(url, title, content, image);
        new ShareAction(mActivity).setPlatform(shareMediaEnum)
                .withMedia(umWeb)
                .setCallback(umShareListener)
                .share();
    }


    /**
     * 图文分享(图片从从资源文件获取)
     *
     * @param resid 图片id
     * @param title 标题
     * @param text  内容
     * @param url   点击跳转的url
     */
    public void shareNews(int resid, final String title, final String text, final String url) {
        UMImage image = new UMImage(mActivity, resid);    //本地图片
        final UMWeb umWeb = new UMWeb(url, title, text, image);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_REFRESH, R.drawable.share_icon_refresh, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                    onclick.onItemClick(2);

                } else if (key.equals(ShareDialog.ACTION_REFRESH)) {
                    onclick.onItemClick(3);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    /**
     * 分享课堂邀请制免费听课
     *
     * @param title 标题
     * @param text  内容
     * @param url   点击跳转的url
     */
    public void shareClassInviteFree(String img, final String title, final String text, final String url) {
        UMImage image = new UMImage(mActivity, img);    //本地图片
        List<SHARE_MEDIA> platforms = new ArrayList<>();
        platforms.add(SHARE_MEDIA.WEIXIN);
        platforms.add(SHARE_MEDIA.WEIXIN_CIRCLE);
        final UMWeb umWeb = new UMWeb(url, title, text, image);
        ShareDialog shareDialog = new ShareDialog()
                .setPlatforms(platforms);

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COMPLAINTS)) {
                    onclick.onItemClick(2);

                } else if (key.equals(ShareDialog.ACTION_REFRESH)) {
                    onclick.onItemClick(3);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();

                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    /**
     * 分享图片
     *
     * @param bitmap
     */
    public void shareImg(Bitmap bitmap) {
        final UMImage image = new UMImage(mActivity, bitmap);
        ShareDialog shareDialog = new ShareDialog();
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                new ShareAction(mActivity)
                        .withMedia(image)
                        .setPlatform(media)
                        .setCallback(umShareListener)
                        .share();
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 分享图片
     *
     * @param resid 资源id
     */
    public void shareImg(int resid) {
        final UMImage image = new UMImage(mActivity, resid);
        ShareDialog shareDialog = new ShareDialog();
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                new ShareAction(mActivity)
                        .withMedia(image)
                        .setPlatform(media)
                        .setCallback(umShareListener)
                        .share();
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    /**
     * 分享图片
     *
     * @param imgurl 图片链接
     */
    public void shareImg(String imgurl) {
        final UMImage image = new UMImage(mActivity, imgurl);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_SAVE, R.drawable.share_icon_save_image, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_SAVE)) {
                    onclick.onItemClick(0);
                } else {
                    new ShareAction(mActivity)
                            .withMedia(image)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    public void shareClassImg(String imgurl) {
        final UMImage image = new UMImage(mActivity, imgurl);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_SAVE, R.drawable.share_icon_save_image, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_SAVE)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(1);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(image)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    public void shareErweiCodeImg(String imgurl) {
        final UMImage image = new UMImage(mActivity, imgurl);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_SAVE, R.drawable.share_icon_save_image, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_COPY)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_SAVE)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(2);

                } else if (key.equals(ShareDialog.ACTION_CIRCLE)) {
                    onclick.onItemClick(3);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(image)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    //群聊好友邀请
    public void shareGroupCodeImg(String imgurl) {
        final UMImage image = new UMImage(mActivity, imgurl);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_CIRCLE)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(2);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(image)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 分享圈子 如果佣金小于0 不分享到qq 跟qq空间
     */
    public void shareCircle(String title, String content, String imgurl, double brokerage, final String shareUrl) {
        final UMImage image = new UMImage(mActivity, imgurl);
        final UMWeb umWeb = new UMWeb(shareUrl, title, content, image);
        final ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null));
        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                if (key.equals(ShareDialog.ACTION_CIRCLE)) {
                    onclick.onItemClick(0);

                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(1);

                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(2);

                } else if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(3);

                } else if (key.equals(ShareDialog.ACTION_COPY)) {
                    ClipboardManager cmb = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(shareUrl);
                    ToastUtil.showShort(mActivity, "已复制");

                } else {
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }

    /**
     * 自定义分享面板
     * 分享到动态、好友、二维码、收藏 4个自定义
     */
    public void shareCustom4(String imageUrl, final String title, final String text, final String url) {
        UMImage image = new UMImage(mActivity, imageUrl);//网络图片
        final UMWeb umWeb = new UMWeb(url, title, text, image);
        ShareDialog shareDialog = new ShareDialog()
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null))
                .addButton(new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null));

        shareDialog.setShareLisntener(new ShareDialog.OnShareLisntener() {
            @Override
            public void onShare(String key, SHARE_MEDIA media) {
                //圈子动态
                if (key.equals(ShareDialog.ACTION_CIRCLE)) {
                    onclick.onItemClick(0);

                    //分享好友
                } else if (key.equals(ShareDialog.ACTION_FRIENDS)) {
                    onclick.onItemClick(1);

                    //收藏
                } else if (key.equals(ShareDialog.ACTION_COLLECT)) {
                    onclick.onItemClick(2);

                    //二维码
                } else if (key.equals(ShareDialog.ACTION_QRCODE)) {
                    onclick.onItemClick(3);

                } else {
                    new ShareAction(mActivity)
                            .withMedia(umWeb)
                            .setPlatform(media)
                            .setCallback(umShareListener)
                            .share();
                }
            }
        });
        shareDialog.show(mActivity.getSupportFragmentManager(), ShareDialog.class.getSimpleName());
    }


    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                ToastUtil.showShort(mActivity, "收藏成功");
            } else {
                ToastUtil.showShort(mActivity, "分享成功");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (t != null) {
                ToastUtil.showShort(mActivity, "分享错误:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };


}
