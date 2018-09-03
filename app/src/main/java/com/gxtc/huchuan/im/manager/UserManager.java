package com.gxtc.huchuan.im.manager;



import com.gxtc.huchuan.bean.RongUserBean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gubr on 2017/2/22.
 * 融云的测试用的用户类
 */

public class UserManager {

    private static UserManager instance;
    private List<RongUserBean> mUsers;

    private UserManager(){
        mUsers=new ArrayList<RongUserBean>();
        mUsers.add(new RongUserBean("binrong1","1001","e8ZHfFW+xgGk7LwHtZgacPUXKVkQMJi1p0XCc2ptCKTeUdixjKQbkZzosRqOeHcGS60PiUsCvPV69adt1Yl5XA=="));
        mUsers.add(new RongUserBean("binrong2","1002","cdnxjedl2IUMcPukfhaf2/UXKVkQMJi1p0XCc2ptCKTeUdixjKQbkY5WcGvJ4v+hhnqn/K8uPbJX/w72YL4IDA=="));
        mUsers.add(new RongUserBean("binrong3","1003","axXCx3WTYiD/h+OVePo5Jv8MKrtMomAzOVaZtkWW98Aw/fa+9tcwhAw5Fh1ZIYPaJGAyXpty+HE+alf8/Dk2Vw=="));
    }

    public static UserManager getInstance(){
        if (instance==null){
            synchronized (UserManager.class){
                if (instance==null){
                    instance=new UserManager();
                }
            }
        }

        return instance;
    }

    public List<RongUserBean> getUsers() {
        return mUsers;
    }
}
