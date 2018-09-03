package com.gxtc.huchuan.utils;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.gxtc.huchuan.handler.QrcodeHandler;

import java.util.HashSet;
import java.util.Iterator;

import io.rong.imkit.emoticon.AndroidEmoji;

/**
 * Created by sjr on 2017/6/13.
 */

public class TextViewLinkUtils {
    private static TextViewLinkUtils mTextViewLinkUtils;
    private OnUrlLinkClickListener mUrlLinkClickListener;

    private TextViewLinkUtils() {
    }

    public static synchronized TextViewLinkUtils getInstance() {
        if (mTextViewLinkUtils == null) {
            mTextViewLinkUtils = new TextViewLinkUtils();
        }
        return mTextViewLinkUtils;
    }


    public SpannableStringBuilder getUrlClickableSpan(TextView textView, String content){
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setText(content);
        CharSequence text = textView.getText();

        SpannableStringBuilder style = new SpannableStringBuilder(text);
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            style.clearSpans();
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#8290AF")), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }

            AndroidEmoji.ensure(style);
        }
        return style;
    }


    /**
     * 自己处理链接点击
     */
    public class MyURLSpan extends ClickableSpan {
        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (mUrlLinkClickListener != null) {
                mUrlLinkClickListener.onLinkClick(widget, mUrl);
            }
            QrcodeHandler handler = new QrcodeHandler(widget.getContext());
            handler.resolvingCode(mUrl, "");
        }
    }

    public interface OnUrlLinkClickListener {
        void onLinkClick(View view, String url);
    }

    public void setUrlLinkClickListener(OnUrlLinkClickListener urlLinkClickListener) {
        mUrlLinkClickListener = urlLinkClickListener;
    }
}
