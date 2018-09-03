package com.gxtc.huchuan.widget;

import android.content.Context;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 * Created by 宋家任 on 2017/6/11.
 * 外链app内部打开
 * blog.csdn.net/sahadev_/article/details/53639168
 */
public class AutoLinkTextView extends android.support.v7.widget.AppCompatTextView {

    public AutoLinkTextView(Context context) {
        super(context);
        init();
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setAutoLinkMask(Linkify.WEB_URLS);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        replace();
    }

    @Override
    public void append(CharSequence text, int start, int end) {
        super.append(text, start, end);
        replace();
    }

    private void replace() {

        CharSequence text = getText();

        if (text instanceof SpannableString) {

            SpannableString spannableString = (SpannableString) text;

            Class<? extends SpannableString> aClass = spannableString.getClass();

            try {

                Class<?> aClassSuperclass = aClass.getSuperclass();

                Field mSpans = aClassSuperclass.getDeclaredField("mSpans");

                mSpans.setAccessible(true);
                Object o = mSpans.get(spannableString);

                if (o.getClass().isArray()) {
                    Object objs[] = (Object[]) o;

                    if (objs.length > 1) {
                        Object obj = objs[0];
                        if (obj.getClass().equals(URLSpan.class)) {
                            Field oldUrlField = obj.getClass().getDeclaredField("mURL");
                            oldUrlField.setAccessible(true);
                            Object o1 = oldUrlField.get(obj);

                            Constructor<?> constructor = ExtendUrlSpan.class.getConstructor(String.class);
                            constructor.setAccessible(true);
                            Object newUrlField = constructor.newInstance(o1.toString());
                            objs[0] = newUrlField;
                        }
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
