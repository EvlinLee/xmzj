package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by Steven on 17/4/26.
 */

public class CircleFileAdapter extends BaseRecyclerAdapter<CircleFileBean> {
    private final RecyclerView mRecyclerView;
    private              int    slidePostiion = -1;
    private static final String TAG = "CircleFileAdapter";
/*    id	文件ID
    userCode	用户编码
    userName	用户名
    fileName	文件名称
    fileUrl	文件链接
    fileSize	文件大小（单位：B）
    fileType	文件类型。0、word；1、excel；2、ppt；3、pdf；4、图片；5、视频；6、exe；7、压缩文件；8、其他
    createTime	上传时间*/


    public CircleFileAdapter(Context context, List<CircleFileBean> list, int itemLayoutId,RecyclerView recyclerView) {
        super(context, list, itemLayoutId);
        mRecyclerView=recyclerView;
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final CircleFileBean circleFileBean) {
        holder.setImage(R.id.img_bg, getImagRes(circleFileBean.getFileType()))
              .setText(R.id.tv_filename, circleFileBean.getFileName())
              .setText(R.id.tv_size, getFileSize(circleFileBean.getFileSize()))
              .setText(R.id.tv_time, getTime(circleFileBean.getCreateTime()))
              .setText(R.id.tv_username, "来自 " +circleFileBean.getUserName());
        LinearLayout itemView = (LinearLayout) holder.getItemView();
//        SwipeLayout itemView = (SwipeLayout) holder.getItemView();

//        if (slidePostiion != position) {
//            itemView.revert();
//        }
        holder.setOnClick(R.id.tv_move, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onMoveClick(v,circleFileBean);
                Log.d(TAG, "onClick: 移动");
            }
        });
        holder.setOnClick(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onDeleteClick(v,circleFileBean);
                Log.d(TAG, "onClick: 删除");
            }
        });
        holder.setOnClick(R.id.tv_rename, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onRenameClick(v,circleFileBean);
                Log.d(TAG, "onClick: 重命名");
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onOpenFileClick(circleFileBean);
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
                l.onOpenFileClick(circleFileBean);
            }
        });*/
    }
    private OnFileClickListener l;

    public void setOnFileClickListener(OnFileClickListener l) {
        this.l = l;
    }

    public static interface OnFileClickListener {
        public void onOpenFileClick(CircleFileBean bean);

        public void onMoveClick(View view,CircleFileBean bean);

        public void onDeleteClick(View view,CircleFileBean bean);
        public void onRenameClick(View view,CircleFileBean bean);
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
