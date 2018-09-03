package com.gxtc.huchuan.ui.mine.editinfo.vertifance;

import android.graphics.Bitmap;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.pay.AccountBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class VertifanceContract {
    public interface View extends BaseUserView<VertifanceContract.Presenter> {
        void showVertifanceResule(Object object);
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showWatermarkImage(Bitmap watermarkBitmap);
        void showUploadingSuccess(String url);
        void showVertifanceCardSuccess(Object object);
        void showVertifanceEpaySuccess(Object object);
    }

    public interface Presenter extends BasePresenter {
        void Vertifance(HashMap<String,String> map);
        //压缩图片
        void compressImg(String s);

        //上传图片
        void uploadingFile(File file);

        void showVertifanceCard(HashMap<String,String> map);

        void showVertifanceEpay(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {

        void Vertifance( ApiCallBack<Object> callBack,HashMap<String,String> map );
        void VertifanceAccoun(ApiCallBack<List<AccountBean>> callBack, HashMap<String,String> map );

    }



}
