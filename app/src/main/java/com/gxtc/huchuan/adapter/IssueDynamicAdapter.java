package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.IssueDynamicBean;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/5/15.
 * 发布动态图片适配器
 */

public class IssueDynamicAdapter extends BaseRecyclerAdapter<IssueDynamicBean> {
    private List<IssueDynamicBean> mDatas;

    public IssueDynamicAdapter(Context context, List<IssueDynamicBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mDatas = list;

    }
    public void  setData(List<IssueDynamicBean>  datas){
        if(datas != null){
            this. mDatas = datas;
            notifyChangeData(this. mDatas);
        }
    }
    @Override
    public void bindData(ViewHolder holder, int position, IssueDynamicBean bean) {
        ImageView ivAdd = (ImageView) holder.getView(R.id.tv_issue_dynamic_add);
        final ImageView ivImg = (ImageView) holder.getView(R.id.iv_item_issue_dynamic);

        if ("img".equals(bean.getType())) {
            if (mDatas.size() == 1) {
                ivAdd.setVisibility(View.VISIBLE);
            }

            if (position == mDatas.size() - 1 && mDatas.get(position).getFile() == null) {
                ivAdd.setVisibility(View.VISIBLE);
            } else {
                ivAdd.setVisibility(View.GONE);
            }

            if (bean.getFile() != null){
                int width = WindowUtil.dip2px(getContext(), 120);
                Glide.with(getContext())
                     .load(Uri.fromFile(bean.getFile()))
                     .apply(new RequestOptions().override(width, width))
                     .into(ivImg);
                ivImg.setVisibility(View.VISIBLE);
            } else {
                ivImg.setVisibility(View.GONE);
            }

        } else {
            ivAdd.setVisibility(View.GONE);
            Observable.just(bean.getFile().getPath())
                      .map(new Func1<String, Bitmap>() {
                        @Override
                        public Bitmap call(String s) {
                            return ThumbnailUtils.createVideoThumbnail(s, MediaStore.Video.Thumbnails.MICRO_KIND);
                        }
                    })
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Bitmap bitmap) {
                                ivImg.setImageBitmap(bitmap);
                            }
                        });
        }
    }
}
