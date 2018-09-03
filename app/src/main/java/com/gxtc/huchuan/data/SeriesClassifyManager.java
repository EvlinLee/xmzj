package com.gxtc.huchuan.data;

import com.gxtc.huchuan.bean.dao.AddSeriesClassify;
import com.gxtc.huchuan.bean.dao.AddSeriesClassifyDao;
import com.gxtc.huchuan.helper.GreenDaoHelper;

/**
 * Describe:
 * Created by ALing on 2017/3/20 .
 */

public class SeriesClassifyManager {
    private static SeriesClassifyManager manager;


    private SeriesClassifyManager() {
    }

    public static SeriesClassifyManager getInstance() {
        if (manager == null) {
            manager = new SeriesClassifyManager();
        }
        return manager;
    }

    private AddSeriesClassifyDao getDao(){
        return GreenDaoHelper.getInstance().getSeeion().getAddSeriesClassifyDao();
    }
    public void updateSeriesClassify(AddSeriesClassify addSeriesClassify){
        if(addSeriesClassify == null)    return ;

        AddSeriesClassifyDao dao = getDao();
        dao.update(addSeriesClassify);
    }
}
