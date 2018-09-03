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

public class FolderListAdapter extends BaseRecyclerAdapter<FolderBean> {
    private final RecyclerView mRecyclerView;
    private              int    slidePostiion = -1;
    private static final String TAG           = "FolderListAdapter";

    public FolderListAdapter(Context context, List<FolderBean> list, int itemLayoutId,
            RecyclerView recyclerView) {
        super(context, list, itemLayoutId);
        mRecyclerView = recyclerView;
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final FolderBean folderBean) {
        holder.setText(R.id.tv_folder_name, folderBean.getFolderName()).setText(R.id.tv_file_num, folderBean.getFileNum() + "个").setText(R.id.tv_time, getTime(folderBean.getCreateTime()));
//        SwipeLayout itemView = (SwipeLayout) holder.getItemView();
        LinearLayout itemView = (LinearLayout) holder.getItemView();

        holder.setOnClick(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onDeleteClick(v,folderBean);
            }
        });
        holder.setOnClick(R.id.tv_rename, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onRenameClick(v,folderBean);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onOpenFolderClick(folderBean);
            }
        });
       /* itemView.setOnSlide(new SwipeLayout.onSlideListener() {
            @Override
            public void onSlided(boolean isSlide) {
                if (isSlide) {
                    slidePostiion = position;
//                    mRecyclerView.notifyChangeData();
                    Log.d(TAG, "slidePostiion:" + slidePostiion);
                } else {
//                    slidePostiion=-1;
                }
            }

            @Override
            public void onClick() {
                l.onOpenFolderClick(folderBean);
            }
        });*/
    }

    private OnFolderClickListener l;

    public void setFolderClickNameClickListener(OnFolderClickListener l) {
        this.l = l;
    }

    public interface OnFolderClickListener {
        void onOpenFolderClick(FolderBean bean);

        void onRenameClick(View view,FolderBean bean);

        void onDeleteClick(View view,FolderBean bean);
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
