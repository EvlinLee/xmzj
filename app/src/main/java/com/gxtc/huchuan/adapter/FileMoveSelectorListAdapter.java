package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.SwipeLayout;

import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 */

public class FileMoveSelectorListAdapter extends BaseRecyclerAdapter<FolderBean> {
    private final RecyclerView mRecyclerView;
    private            FolderBean mFolderBean;
    private static final String TAG              = "FolderListAdapter";

    public FileMoveSelectorListAdapter(Context context, List<FolderBean> list, int itemLayoutId,
            RecyclerView recyclerView) {
        super(context, list, itemLayoutId);
        mRecyclerView = recyclerView;
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final FolderBean folderBean) {
        holder.setText(R.id.tv_folder_name, folderBean.getFolderName()).setText(R.id.tv_file_num,
                folderBean.getFileNum() + "个").setText(R.id.tv_time,
                getTime(folderBean.getCreateTime()));
        LinearLayout itemView = (LinearLayout) holder.getItemView();
        holder.getView(R.id.iv_selected_label).setVisibility(mFolderBean==folderBean?View.VISIBLE:View.GONE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFolderBean =folderBean;
                mRecyclerView.notifyChangeData();
                l.onFolderClick(folderBean);
            }
        });

    }

    public FolderBean getSelectedFolderBean(){
        return mFolderBean;
    }

    private OnFolderClickListener l;

    public void setFolderClickListener(OnFolderClickListener l) {
        this.l = l;
    }

    public static interface OnFolderClickListener {
        public void onFolderClick(FolderBean bean);
    }

    private int getImagRes(int fileType) {
        int resId = -1;
        switch (fileType) {
            case 0:
                resId = R.drawable.circle_details_icon_docx;
                break;
            case 1:
                resId = R.drawable.circle_details_icon_excel;
                break;
            case 2:
                resId = R.drawable.circle_details_icon_ppt;
                break;
            case 3:
                resId = R.drawable.circle_details_icon_pdf;
                break;
            case 4:
                resId = R.drawable.circle_details_icon_jpg;
                break;
            case 5:
                resId = R.drawable.circle_details_icon_mp4;
                break;
            case 6:
                resId = R.drawable.circle_details_icon_unknown;
                break;
            case 7:
                resId = R.drawable.circle_details_icon_compress;
                break;
            default:
                resId = R.drawable.circle_details_icon_unknown;
                break;
        }
        return resId;
    }

    private String getTime(long createTime) {
        return DateUtil.formatTime(createTime, "MM-dd");

    }

    private String getFileSize(long fileSize) {
        return FileUtil.getTrafficStr(fileSize);
    }


}
