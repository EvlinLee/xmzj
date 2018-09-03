package com.gxtc.huchuan.ui.mine.editorarticle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.ArticleResolveRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.utils.ImageUtils;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

public class ArticleResolvePresenter implements ArticleResolveContract.Presenter {

    private ArticleResolveContract.View mView;
    private ArticleResolveContract.Source mData;

    private int timeOut = 1000 * 10;

    public ArticleResolvePresenter(ArticleResolveContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ArticleResolveRepository();
    }

    private Thread t;

    @Override
    public void getArticleType() {
        if(mView == null) return;
        mView.showLoad();
        mData.getArticleType(new ApiCallBack<ChannelBean>() {

            @Override
            public void onSuccess(ChannelBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                data.getDefaultX().remove(0);
                String[] items = new String[data.getDefaultX().size() + data.getNormal().size()];
                List<ChannelBean.NormalBean> beans = new ArrayList<ChannelBean.NormalBean>();

                int k = 0;
                for (int i = 0; i < data.getDefaultX().size(); i++) {
                    ChannelBean.DefaultBean bean = data.getDefaultX().get(i);
                    items[i] = bean.getNewstypeName();
                    beans.add(new ChannelBean.NormalBean(bean.getNewstypeId(),
                            bean.getNewstypeName()));
                    k++;
                }
                for (int i = 0; i < data.getNormal().size(); i++) {
                    ChannelBean.NormalBean bean = data.getNormal().get(i);
                    items[k] = bean.getNewstypeName();
                    beans.add(new ChannelBean.NormalBean(bean.getNewstypeId(),
                            bean.getNewstypeName()));
                    k++;
                }
                mView.showArticleType(items, beans);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });


    }

    @Override
    public void getHtmlContent(final String url) {
        if(mView == null) return;
        mView.showLoad();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imgUrl = "";
                    String title = "";
                    String des = "";
                    Document doc = Jsoup.connect(url).timeout(timeOut).get();
                    Element content = doc.body().getElementById("js_content");

                    //让里面的图片自适应屏幕
                    Elements imgs = content.getElementsByTag("img");
                    for (Element el : imgs) {
                        String attImg = "";
                        Attributes attributes = el.attributes();

                        //调整img标签宽高
                        if(attributes.hasKey("style")){
                            String style = attributes.get("style");
                            if(!style.contains("width") && !style.contains("height")){
                                style += ";width:100%;height:auto";
                                el.attr("style", style);
                            }

                        }else{
                            el.attr("style", "width:100%;height:auto;");
                        }

                        //获取img 的 url 路径
                        for(Attribute att : attributes){
                            if(att.getKey().equals("data-src")){
                                //String temp = "&wxfrom=5&wx_lazy=1";
                                //att.setValue(att.getValue().replace("data-src", "src"));
                                attImg = att.getValue();
                                break;
                            }
                        }
                        el.attr("src", attImg);
                    }

                    /*取得script下面的JS变量*/
                    Elements els = doc.body().getElementsByTag("script").eq(7);
                    /*循环遍历script下面的JS变量*/
                    for (Element e : els) {
                        String[] data = e.data().toString().split("var");

                        /*取得单个JS变量*/
                        for (String var : data) {
                            if (var.contains("msg_cdn_url")) {
                                String[] kvp = var.split("=");
                                imgUrl = kvp[1];
                            }

                            if (var.contains("msg_title")) {
                                String[] kvp = var.split("=");
                                title = kvp[1];
                            }

                            if (var.contains("msg_desc")) {
                                String[] kvp = var.split("=");
                                des = kvp[1];
                            }

                        }
                    }
                    imgUrl = imgUrl.replace("\"", "").replace(";", "").trim();
                    title = title.replace("\"", "").replace(";", "").trim();
                    des = des.replace("\"", "").replace(";", "").trim();
                    //mView.showHtmlContent(url, title, des, imgUrl, content.html().replace("data-src", "src"));
                    mView.showHtmlContent(url, title, des, imgUrl, content.html());

                } catch (Exception e) {
                    if(mView == null) return;
                    e.printStackTrace();
                    mView.showHtmlError();
                }
            }
        });
        t.start();
    }

    @Override
    public void saveArticle(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.saveArticle(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.showLoadFinish();
                mView.showSaveSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    private Subscription uploadSub;
    private File srcFile;

    @Override
    public void uploadingFile(final String id, String path) {
        LogUtil.i("原图路径： " + path);
        LogUtil.i("img id ： " + id);
        final Context context = MyApplication.getInstance();
        srcFile = new File(path);

        //将图片进行压缩
        uploadSub =

        Observable.just(path)
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<String, File>() {
                      @Override
                      public File call(String oldFile) {
                          File file;
                          //这里要判断一下是否是插入图片到文章，如果是的话要打个水印，如果不是就直接传文件
                          if(id.startsWith("a")){
                              Bitmap waterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_shuiyin);
                              try {
                                  Bitmap srcBitmap =
                                          Glide.with(context)
                                               .asBitmap()
                                               .load(oldFile)
                                               .submit()
                                               .get();

                                  //按比例缩放水印
                                  float scale = waterBitmap.getWidth() / waterBitmap.getHeight();
                                  int width = (int) (srcBitmap.getWidth() * 0.2);
                                  int height = (int) (width / scale);
                                  Bitmap scaleWatermark = com.gxtc.huchuan.utils.ImageUtils.zoomBitmap(waterBitmap, width, height);
                                  int paddingLeft = srcBitmap.getWidth() - scaleWatermark.getWidth() - WindowUtil.dip2px(context, 10);
                                  int paddingTop = srcBitmap.getHeight() - scaleWatermark.getHeight() - WindowUtil.dip2px(context, 10);
                                  Bitmap targetBitmap = ImageUtils.createWaterMaskBitmap(srcBitmap, scaleWatermark, paddingLeft, paddingTop);

                                  String newPath = FileStorage.getImgCacheFile().getAbsolutePath() + "/" + FileStorage.getImageTempName();
                                  ImageUtils.saveImageToSD(MyApplication.getInstance(), newPath, targetBitmap, 100);
                                  srcFile = new File(newPath);
                                  file = srcFile;

                              } catch (Exception e) {
                                  e.printStackTrace();
                                  file = null;
                              }

                          }else{
                              file = new File(oldFile);
                          }
                          return file;
                      }
                  })
                  .filter(new Func1<File, Boolean>() {
                      @Override
                      public Boolean call(File file) {
                          return file != null;
                      }
                  })
                  .flatMap(new Func1<File, Observable<File>>() {
                      @Override
                      public Observable<File> call(File file) {
                          return Luban.get(MyApplication.getInstance())
                                      .load(file)                        //传人要压缩的图片
                                      .putGear(Luban.THIRD_GEAR)         //设定压缩档次，默认三挡
                                      .asObservable();
                      }
                  })
                  .map(new Func1<File,File>() {
                      @Override
                      public File call(File compressFile) {
                          if (FileUtil.getSize(srcFile) > maxLen_500k ){
                              return compressFile;
                          }else {
                              return srcFile;
                          }
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<File>() {
                      @Override
                      public void onCompleted() {}

                      @Override
                      public void onError(Throwable e) {
                          if(mView == null) return;
                          mView.showError("上传图片失败");
                      }

                      @Override
                      public void onNext(File file) {
                          LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                              @Override
                              public void onUploadSuccess(UploadResult result) {
                                  if(mView == null) return;
                                  mView.showUploadingSuccess(id, result.getUrl());
                              }

                              @Override
                              public void onUploadFailed(String errorCode, String msg) {
                                  if(mView == null) return;
                                  ErrorCodeUtil.handleErr(mView, errorCode, msg);
                              }
                          }, null, file);
                      }
                  });
    }

    @Override
    public void uploadingVideo(final String id, String path) {
        final File file = new File(path);

        LoadHelper.uploadFile(LoadHelper.UP_TYPE_VIDEO, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(mView == null) return;
                mView.showUploadVideoSuccess(id, result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                if(mView != null){
                    mView.showUploadVideoFailure(msg);
                }
            }
        },new UIProgressListener() {
            @Override
            public void onUIProgress(long currentBytes, long contentLength, boolean done) {
                LogUtil.i("bytesWrite :  " + currentBytes + "        contentLength : " + contentLength);
            }
        }, file);
    }


    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        if (t != null) t.interrupt();
        if (uploadSub != null) uploadSub.unsubscribe();
        mView = null;
    }

}
