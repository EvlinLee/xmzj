package com.gxtc.huchuan.im.extension;


import com.gxtc.huchuan.ui.im.call.CallPlugin;
import com.gxtc.huchuan.ui.im.video.VideoPlugin;

import java.util.ArrayList;
import java.util.List;

import im.collect.CollectPlugin;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 自定义输入区域的扩展览，增加红包按钮
 */

public class MyRongExtensionModule extends DefaultExtensionModule {

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> list = super.getPluginModules(conversationType);
        list.clear();     //融云已经把发送图片和文件集成进去，这里发送文件不用融云的，要把它删去，换成我们自定义的发送文件
        list.add(new ImagePlugin());
        list.add(new CameraPlugin());
        list.add(new FilePlugin());
        //list.add(new CustomFilePlugin());发送文件改回用融云的
        list.add(new RedPacketPlugin());
        list.add(new PostcardPlugin());
        list.add(new CollectPlugin());
        list.add(new VideoPlugin());
        if(conversationType == Conversation.ConversationType.PRIVATE){
            //list.add(new CallPlugin());
            list.add(new io.rong.callkit.VideoPlugin());
        }
        return list;
    }
}
