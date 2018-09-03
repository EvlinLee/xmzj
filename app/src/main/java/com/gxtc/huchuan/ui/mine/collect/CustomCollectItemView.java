package com.gxtc.huchuan.ui.mine.collect;

import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.CustomCollectBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Gubr on 2017/6/5.
 *
 */
class CustomCollectItemView implements ItemViewDelegate<CollectionBean> {
    public CustomCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_custom;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "5".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        CustomCollectBean bean = collectionBean.getData();
        if(bean == null)    return;

        if (!bean.isFlag()) {
            try{
                Document document = Jsoup.parse(bean.getContent());
                Element  body     = document.body();
                Elements imgs     = body.getElementsByTag("img");
                if (imgs != null && imgs.size() > 0) {
                    Element element = imgs.get(0);
                    if (element.hasAttr("src")) {
                        String src = element.attr("src");
                        bean.setImgUrl(src);
                    }
                }
                List<TextNode> textNodes    = body.textNodes();
                StringBuffer   stringBuffer = new StringBuffer();
                for (int i = 0; i < textNodes.size(); i++) {
                    if (i == 0) {
                        bean.setTitle(textNodes.get(i).getWholeText());
                    } else {
                        stringBuffer.append(textNodes.get(i).getWholeText());
                    }
                }
                bean.setStrContent(stringBuffer.toString());

            }catch (Exception e){

            }
            bean.setFlag(true);
        }
        if (UserManager.getInstance().isLogin()) {
            User user = UserManager.getInstance().getUser();
            holder.setText(R.id.tv_username,user.getName());
            ImageHelper.loadCircle(holder.getConvertView().getContext(),holder.getImageView(R.id.iv_head),user.getHeadPic(), R.drawable.person_icon_head);
        }

        if (!TextUtils.isEmpty(bean.getTitle())){
            holder.getView(R.id.tv_custom_title).setVisibility(View.VISIBLE);

            holder.setText(R.id.tv_custom_title, bean.getTitle());
        }else{
            holder.getView(R.id.tv_custom_title).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bean.getStrContent())){
            holder.getView(R.id.tv_custom_content).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_custom_content, bean.getStrContent());

        }else{
            holder.getView(R.id.tv_custom_content).setVisibility(View.GONE);
        }  if (!TextUtils.isEmpty(bean.getImgUrl())){
            holder.getView(R.id.iv_custom_img).setVisibility(View.VISIBLE);
            ImageHelper.loadImage(holder.getConvertView().getContext(),holder.getImageView(R.id.iv_custom_img),bean.getImgUrl());

        }else{
            holder.getView(R.id.iv_custom_img).setVisibility(View.GONE);
        }


    }
}
