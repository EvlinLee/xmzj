package com.gxtc.huchuan.ui.circle.file.folder;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 */

public interface FolderListContract {
    public interface View extends BaseUiView<Presenter> {
        void showFolderData(List<FolderBean> datas);

        void showLoMore(List<FolderBean> datas);

        void loadFinish();


        void createFolder(int grouid, String folderName, String fileid);

        void showFileData(List<CircleFileBean> datas);

        void createFolder(FolderBean bean);

        void deleteFolder(int grouid, int folderId);

        void saveFolder(int fileId, String name, int fileName);
    }

    public interface Presenter extends BasePresenter {


        void getData(boolean isloadmroe,int grouid, int start,Integer pagesize);

        void createFolder(String token, int grouid, String folderName, String fileid);

        void listFileByFolder(int folderId, int start);

        void deleteFolder(String token, int grouid, int folderId);

        void saveFile(String token, final int fileId, final String fileName, Integer folderId);
    }

    public interface Source extends BaseSource {
        void getData(String token,int grouid, int start,Integer pageise, ApiCallBack<List<FolderBean>> callBack);

        void createFolder(String token, int grouid, String folderName, String fileid,
                ApiCallBack<FolderBean> callBack);

        void listFileByFolder(int groupId,String token ,int folderId, int start, ApiCallBack<List<CircleFileBean>> callBack);

        void deleteFolder(String token, int grouid, int folderId, ApiCallBack<Void> callBack);

        void saveFile(String token, int fileId, String fileName, Integer folderId,
                ApiCallBack<Void> callBack);
    }
}
