package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Intent;

import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/22.
 * 通用广告点击事件处理
 */

public class AdClickUtil {

    public static void performClick(Activity activity,NewsAdsBean bean){
        if(ClickUtil.isFastClick()) return;

        CircleShareHandler handler = new CircleShareHandler(activity);
        if(bean != null){
            int type = bean.getContentType();
            switch (type){
                //广告
                case 0:
                    Intent adIntent = new Intent(activity, CommonWebViewActivity.class);
                    adIntent.putExtra("web_url", bean.getUrl());
                    adIntent.putExtra("web_title", bean.getTitle());
                    adIntent.putExtra("web_cover", bean.getCover());
                    adIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(adIntent);
                    break;

                //新闻
                case 1:
                    int id = bean.getAdvContentId();
                    handler.getNewsData(String.valueOf(id));

                    /*Intent newsIntent = new Intent(activity, NewsWebActivity.class);
                    NewsAdsBean.NewsRespVoBean newsRespVo = bean.getNewsRespVo();
                    if(newsRespVo != null){
                        NewsBean tempBean = new NewsBean();
                        tempBean.setId(newsRespVo.getId());
                        tempBean.setSource(newsRespVo.getSource());
                        tempBean.setIsVideo(newsRespVo.getIsVideo());
                        tempBean.setVideoUrl(newsRespVo.getVideoUrl());
                        tempBean.setSourceicon(newsRespVo.getSourceicon());
                        tempBean.setCommentCount(newsRespVo.getCommentCount());
                        tempBean.setThumbsupCount(newsRespVo.getThumbsupCount());
                        tempBean.setIsThumbsup(newsRespVo.getIsThumbsup());
                        tempBean.setIsCollect(newsRespVo.getIsCollect());
                        tempBean.setCover(newsRespVo.getCover());
                        tempBean.setTitle(newsRespVo.getTitle());
                        tempBean.setDigest(newsRespVo.getDigest());
                        tempBean.setRedirectUrl(newsRespVo.getRedirectUrl());
                        tempBean.setUserCode(newsRespVo.getUserCode());
                        newsIntent.putExtra("data", tempBean);
                        newsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(newsIntent);
                    }*/
                    break;

                //交易
                case 4:
                    int dealId = bean.getAdvContentId();
                    if(dealId != 0){
                        Intent intent = new Intent(activity,GoodsDetailedActivity.class);
                        intent.putExtra(Constant.INTENT_DATA, String.valueOf(dealId));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                    break;

                //课堂
                case 5:
                    //课程
                    if("1".equals(bean.getChatType())){
                        int classId = bean.getAdvContentId();
                        if(classId != 0){
                            LiveIntroActivity.startActivity(activity,String.valueOf(classId),Intent.FLAG_ACTIVITY_NEW_TASK);
                           // handler.getLiveInfo(String.valueOf(classId),null);
                        }
                    }
                    //系列课
                    if("2".equals(bean.getChatType())){
                        SeriesActivity.startActivity(activity,String.valueOf(bean.getAdvContentId()), true,Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                    break;
            }
        }

    }

}
