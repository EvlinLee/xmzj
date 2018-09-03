package com.gxtc.huchuan.customemoji.utils;

import android.support.v4.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.gxtc.huchuan.R;

import java.util.LinkedHashMap;


/**
 * @description :表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
 */
public class EmotionUtils {

    /**
     * 表情类型标志符
     */
    public static final int EMOTION_CLASSIC_TYPE = 0x0001;//经典表情

    /**
     * key-表情文字;
     * value-表情图片资源
     */
    private static ArrayMap<String, Integer>       EMPTY_MAP;
    private static LinkedHashMap<Integer, Integer> EMOTION_CLASSIC_MAP;

    private static SparseArray<String> EMOJI_STRING;

    static {
        EMPTY_MAP = new ArrayMap<>();
        EMOTION_CLASSIC_MAP = new LinkedHashMap<>();
        EMOJI_STRING = new SparseArray<>();

		/*EMOTION_CLASSIC_MAP.put("[呲牙]", R.drawable.a_chiya);
        EMOTION_CLASSIC_MAP.put("[调皮]", R.drawable.a_tiaobi);
		EMOTION_CLASSIC_MAP.put("[汗]", R.drawable.a_han);
		EMOTION_CLASSIC_MAP.put("[偷笑]", R.drawable.a_douxiao);
		EMOTION_CLASSIC_MAP.put("[拜拜]", R.drawable.a_baibai);
		EMOTION_CLASSIC_MAP.put("[打你]", R.drawable.a_dani);
		EMOTION_CLASSIC_MAP.put("[擦汗]", R.drawable.a_chahan);
		EMOTION_CLASSIC_MAP.put("[猪头]", R.drawable.a_zhutou);
		EMOTION_CLASSIC_MAP.put("[玫瑰]", R.drawable.a_meigui);
		EMOTION_CLASSIC_MAP.put("[流泪]", R.drawable.a_liulei);
		EMOTION_CLASSIC_MAP.put("[快哭了]", R.drawable.a_kuaiku);
		EMOTION_CLASSIC_MAP.put("[嘘]", R.drawable.a_xu);
		EMOTION_CLASSIC_MAP.put("[酷]", R.drawable.a_ku);
		EMOTION_CLASSIC_MAP.put("[抓狂]", R.drawable.a_zhuakuang);
		EMOTION_CLASSIC_MAP.put("[委屈]", R.drawable.a_weiqu);
		EMOTION_CLASSIC_MAP.put("[屎]", R.drawable.a_shi);
		EMOTION_CLASSIC_MAP.put("[炸弹]", R.drawable.a_zhadan);
		EMOTION_CLASSIC_MAP.put("[菜刀]", R.drawable.a_dao);
		EMOTION_CLASSIC_MAP.put("[可爱]", R.drawable.a_keai);
		EMOTION_CLASSIC_MAP.put("[色]", R.drawable.a_se);
		EMOTION_CLASSIC_MAP.put("[害羞]", R.drawable.a_haixiu);
		EMOTION_CLASSIC_MAP.put("[得意]", R.drawable.a_deyi);
		EMOTION_CLASSIC_MAP.put("[吐]", R.drawable.a_tu);
		EMOTION_CLASSIC_MAP.put("[微笑]", R.drawable.a_weixiao);
		EMOTION_CLASSIC_MAP.put("[发火]", R.drawable.a_fahuo);
		EMOTION_CLASSIC_MAP.put("[尴尬]", R.drawable.a_ganga);
		EMOTION_CLASSIC_MAP.put("[惊恐]", R.drawable.a_jingkong);
		EMOTION_CLASSIC_MAP.put("[冷汗]", R.drawable.a_lenghan);
		EMOTION_CLASSIC_MAP.put("[心]", R.drawable.a_xin);
		EMOTION_CLASSIC_MAP.put("[嘴唇]", R.drawable.a_zuichun);
		EMOTION_CLASSIC_MAP.put("[白眼]", R.drawable.a_baiyan);
		EMOTION_CLASSIC_MAP.put("[傲慢]", R.drawable.a_aoman);
		EMOTION_CLASSIC_MAP.put("[难过]", R.drawable.a_nanguo);
		EMOTION_CLASSIC_MAP.put("[惊讶]", R.drawable.a_qingya);
		EMOTION_CLASSIC_MAP.put("[疑问]", R.drawable.a_yiwen);
		EMOTION_CLASSIC_MAP.put("[睡]", R.drawable.a_shui);
		EMOTION_CLASSIC_MAP.put("[亲亲]", R.drawable.a_qinqin);
		EMOTION_CLASSIC_MAP.put("[憨笑]", R.drawable.a_hanxiao);
		EMOTION_CLASSIC_MAP.put("[爱情]", R.drawable.a_aiqing);
		EMOTION_CLASSIC_MAP.put("[衰]", R.drawable.a_shuai);
		EMOTION_CLASSIC_MAP.put("[撇嘴]", R.drawable.a_biezui);
		EMOTION_CLASSIC_MAP.put("[奸笑]", R.drawable.a_jianxiao);
		EMOTION_CLASSIC_MAP.put("[奋斗]", R.drawable.a_fendou);
		EMOTION_CLASSIC_MAP.put("[发呆]", R.drawable.a_fadai);
		EMOTION_CLASSIC_MAP.put("[右哼哼]", R.drawable.d_yiwen);
		EMOTION_CLASSIC_MAP.put("[抱抱]", R.drawable.a_baobao);
		EMOTION_CLASSIC_MAP.put("[坏笑]", R.drawable.a_hanxiao);
		EMOTION_CLASSIC_MAP.put("[企鹅亲]", R.drawable.a_qi_e_qin);
		EMOTION_CLASSIC_MAP.put("[鄙视]", R.drawable.a_bishi);
		EMOTION_CLASSIC_MAP.put("[晕]", R.drawable.a_yun);
		EMOTION_CLASSIC_MAP.put("[大兵]", R.drawable.a_daping);
		EMOTION_CLASSIC_MAP.put("[拜托]", R.drawable.a_baiduo);
		EMOTION_CLASSIC_MAP.put("[强]", R.drawable.a_qiang);
		EMOTION_CLASSIC_MAP.put("[垃圾]", R.drawable.a_laji);
		EMOTION_CLASSIC_MAP.put("[握手]", R.drawable.a_woshou);
		EMOTION_CLASSIC_MAP.put("[胜利]", R.drawable.a_shengli);
		EMOTION_CLASSIC_MAP.put("[抱拳]", R.drawable.a_baoquan);
		EMOTION_CLASSIC_MAP.put("[枯萎]", R.drawable.a_kuwei);
		EMOTION_CLASSIC_MAP.put("[米饭]", R.drawable.a_mifan);
		EMOTION_CLASSIC_MAP.put("[蛋糕]", R.drawable.a_dangao);
		EMOTION_CLASSIC_MAP.put("[西瓜]", R.drawable.a_xigua);
		EMOTION_CLASSIC_MAP.put("[啤酒]", R.drawable.a_pijiu);
		EMOTION_CLASSIC_MAP.put("[瓢虫]", R.drawable.a_gunchong);
		EMOTION_CLASSIC_MAP.put("[勾引]", R.drawable.a_gouyin);
		EMOTION_CLASSIC_MAP.put("[哦了]", R.drawable.a_ole);
		EMOTION_CLASSIC_MAP.put("[手势]", R.drawable.a_shoushi);
		EMOTION_CLASSIC_MAP.put("[咖啡]", R.drawable.a_gafei);
		EMOTION_CLASSIC_MAP.put("[月亮]", R.drawable.a_yueliang);
		EMOTION_CLASSIC_MAP.put("[匕首]", R.drawable.a_bishou);
		EMOTION_CLASSIC_MAP.put("[发抖]", R.drawable.a_fatou);
		EMOTION_CLASSIC_MAP.put("[菜]", R.drawable.a_cai);
		EMOTION_CLASSIC_MAP.put("[拳头]", R.drawable.a_quantou);*/

        EMOTION_CLASSIC_MAP.put(0x1f60a, R.drawable.u1f60a);
        EMOTION_CLASSIC_MAP.put(0x1f601, R.drawable.u1f601);
        EMOTION_CLASSIC_MAP.put(0x1f628, R.drawable.u1f628);
        EMOTION_CLASSIC_MAP.put(0x1f60d, R.drawable.u1f60d);
        EMOTION_CLASSIC_MAP.put(0x1f633, R.drawable.u1f633);
        EMOTION_CLASSIC_MAP.put(0x1f60e, R.drawable.u1f60e);
        EMOTION_CLASSIC_MAP.put(0x1f62d, R.drawable.u1f62d);
        EMOTION_CLASSIC_MAP.put(0x1f60c, R.drawable.u1f60c);
        EMOTION_CLASSIC_MAP.put(0x1f635, R.drawable.u1f635);
        EMOTION_CLASSIC_MAP.put(0x1f634, R.drawable.u1f634);
        EMOTION_CLASSIC_MAP.put(0x1f622, R.drawable.u1f622);
        EMOTION_CLASSIC_MAP.put(0x1f605, R.drawable.u1f605);
        EMOTION_CLASSIC_MAP.put(0x1f621, R.drawable.u1f621);
        EMOTION_CLASSIC_MAP.put(0x1f61c, R.drawable.u1f61c);
        EMOTION_CLASSIC_MAP.put(0x1f600, R.drawable.u1f600);
        EMOTION_CLASSIC_MAP.put(0x1f632, R.drawable.u1f632);
        EMOTION_CLASSIC_MAP.put(0x1f61f, R.drawable.u1f61f);
        EMOTION_CLASSIC_MAP.put(0x1f624, R.drawable.u1f624);
        EMOTION_CLASSIC_MAP.put(0x1f61e, R.drawable.u1f61e);
        EMOTION_CLASSIC_MAP.put(0x1f62b, R.drawable.u1f62b);
        EMOTION_CLASSIC_MAP.put(0x1f623, R.drawable.u1f623);
        EMOTION_CLASSIC_MAP.put(0x1f608, R.drawable.u1f608);
        EMOTION_CLASSIC_MAP.put(0x1f609, R.drawable.u1f609);
        EMOTION_CLASSIC_MAP.put(0x1f62f, R.drawable.u1f62f);
        EMOTION_CLASSIC_MAP.put(0x1f615, R.drawable.u1f615);
        EMOTION_CLASSIC_MAP.put(0x1f630, R.drawable.u1f630);
        EMOTION_CLASSIC_MAP.put(0x1f60b, R.drawable.u1f60b);
        EMOTION_CLASSIC_MAP.put(0x1f61d, R.drawable.u1f61d);
        EMOTION_CLASSIC_MAP.put(0x1f613, R.drawable.u1f613);
        EMOTION_CLASSIC_MAP.put(0x1f603, R.drawable.u1f603);
        EMOTION_CLASSIC_MAP.put(0x1f602, R.drawable.u1f602);
        EMOTION_CLASSIC_MAP.put(0x1f618, R.drawable.u1f618);
        EMOTION_CLASSIC_MAP.put(0x1f612, R.drawable.u1f612);
        EMOTION_CLASSIC_MAP.put(0x1f60f, R.drawable.u1f60f);
        EMOTION_CLASSIC_MAP.put(0x1f636, R.drawable.u1f636);
        EMOTION_CLASSIC_MAP.put(0x1f631, R.drawable.u1f631);
        EMOTION_CLASSIC_MAP.put(0x1f616, R.drawable.u1f616);
        EMOTION_CLASSIC_MAP.put(0x1f629, R.drawable.u1f629);
        EMOTION_CLASSIC_MAP.put(0x1f614, R.drawable.u1f614);
        EMOTION_CLASSIC_MAP.put(0x1f611, R.drawable.u1f611);
        EMOTION_CLASSIC_MAP.put(0x1f61a, R.drawable.u1f61a);
        EMOTION_CLASSIC_MAP.put(0x1f62a, R.drawable.u1f62a);
        EMOTION_CLASSIC_MAP.put(0x1f607, R.drawable.u1f607);
        EMOTION_CLASSIC_MAP.put(0x1f44a, R.drawable.u1f44a);
        EMOTION_CLASSIC_MAP.put(0x1f44e, R.drawable.u1f44e);
        EMOTION_CLASSIC_MAP.put(0x261d, R.drawable.u261d);
        EMOTION_CLASSIC_MAP.put(0x270c, R.drawable.u270c);
        EMOTION_CLASSIC_MAP.put(0x1f62c, R.drawable.u1f62c);
        EMOTION_CLASSIC_MAP.put(0x1f637, R.drawable.u1f637);
        EMOTION_CLASSIC_MAP.put(0x1f648, R.drawable.u1f648);
        EMOTION_CLASSIC_MAP.put(0x1f64a, R.drawable.u1f64a);
        EMOTION_CLASSIC_MAP.put(0x1f649, R.drawable.u1f649);
        EMOTION_CLASSIC_MAP.put(0x1f44c, R.drawable.u1f44c);
        EMOTION_CLASSIC_MAP.put(0x1f44f, R.drawable.u1f44f);
        EMOTION_CLASSIC_MAP.put(0x270a, R.drawable.u270a);
        EMOTION_CLASSIC_MAP.put(0x1f4aa, R.drawable.u1f4aa);
        EMOTION_CLASSIC_MAP.put(0x1f606, R.drawable.u1f606);
        EMOTION_CLASSIC_MAP.put(0x263a, R.drawable.u263a);
        EMOTION_CLASSIC_MAP.put(0x1f44d, R.drawable.u1f44d);
        EMOTION_CLASSIC_MAP.put(0x1f64f, R.drawable.u1f64f);
        EMOTION_CLASSIC_MAP.put(0x270b, R.drawable.u270b);
        EMOTION_CLASSIC_MAP.put(0x2600, R.drawable.u2600);
        EMOTION_CLASSIC_MAP.put(0x2615, R.drawable.u2615);
        EMOTION_CLASSIC_MAP.put(0x26c4, R.drawable.u26c4);
        EMOTION_CLASSIC_MAP.put(0x1f4da, R.drawable.u1f4da);
        EMOTION_CLASSIC_MAP.put(0x1f381, R.drawable.u1f381);
        EMOTION_CLASSIC_MAP.put(0x1f389, R.drawable.u1f389);
        EMOTION_CLASSIC_MAP.put(0x1f366, R.drawable.u1f366);
        EMOTION_CLASSIC_MAP.put(0x2601, R.drawable.u2601);
        EMOTION_CLASSIC_MAP.put(0x2744, R.drawable.u2744);
        EMOTION_CLASSIC_MAP.put(0x26a1, R.drawable.u26a1);
        EMOTION_CLASSIC_MAP.put(0x1f4b0, R.drawable.u1f4b0);
        EMOTION_CLASSIC_MAP.put(0x1f382, R.drawable.u1f382);
        EMOTION_CLASSIC_MAP.put(0x1f356, R.drawable.u1f356);
        EMOTION_CLASSIC_MAP.put(0x1f393, R.drawable.u1f393);
        EMOTION_CLASSIC_MAP.put(0x2614, R.drawable.u2614);
        EMOTION_CLASSIC_MAP.put(0x26c5, R.drawable.u26c5);
        EMOTION_CLASSIC_MAP.put(0x270f, R.drawable.u270f);
        EMOTION_CLASSIC_MAP.put(0x1f4a9, R.drawable.u1f4a9);
        EMOTION_CLASSIC_MAP.put(0x1f384, R.drawable.u1f384);
        EMOTION_CLASSIC_MAP.put(0x1f377, R.drawable.u1f377);
        EMOTION_CLASSIC_MAP.put(0x1f3a4, R.drawable.u1f3a4);
        EMOTION_CLASSIC_MAP.put(0x1f3c0, R.drawable.u1f3c0);
        EMOTION_CLASSIC_MAP.put(0x1f004, R.drawable.u1f004);
        EMOTION_CLASSIC_MAP.put(0x1f4a3, R.drawable.u1f4a3);
        EMOTION_CLASSIC_MAP.put(0x1f4e2, R.drawable.u1f4e2);
        EMOTION_CLASSIC_MAP.put(0x1f30f, R.drawable.u1f30f);
        EMOTION_CLASSIC_MAP.put(0x1f36b, R.drawable.u1f36b);
        EMOTION_CLASSIC_MAP.put(0x1f3b2, R.drawable.u1f3b2);
        EMOTION_CLASSIC_MAP.put(0x1f3c2, R.drawable.u1f3c2);
        EMOTION_CLASSIC_MAP.put(0x1f4a1, R.drawable.u1f4a1);
        EMOTION_CLASSIC_MAP.put(0x1f4a4, R.drawable.u1f4a4);
        EMOTION_CLASSIC_MAP.put(0x1f6ab, R.drawable.u1f6ab);
        EMOTION_CLASSIC_MAP.put(0x1f33b, R.drawable.u1f33b);
        EMOTION_CLASSIC_MAP.put(0x1f37b, R.drawable.u1f37b);
        EMOTION_CLASSIC_MAP.put(0x1f3b5, R.drawable.u1f3b5);
        EMOTION_CLASSIC_MAP.put(0x1f3e1, R.drawable.u1f3e1);
        EMOTION_CLASSIC_MAP.put(0x1f4a2, R.drawable.u1f4a2);
        EMOTION_CLASSIC_MAP.put(0x1f4de, R.drawable.u1f4de);
        EMOTION_CLASSIC_MAP.put(0x1f6bf, R.drawable.u1f6bf);
        EMOTION_CLASSIC_MAP.put(0x1f35a, R.drawable.u1f35a);
        EMOTION_CLASSIC_MAP.put(0x1f46a, R.drawable.u1f46a);
        EMOTION_CLASSIC_MAP.put(0x1f47c, R.drawable.u1f47c);
        EMOTION_CLASSIC_MAP.put(0x1f48a, R.drawable.u1f48a);
        EMOTION_CLASSIC_MAP.put(0x1f52b, R.drawable.u1f52b);
        EMOTION_CLASSIC_MAP.put(0x1f339, R.drawable.u1f339);
        EMOTION_CLASSIC_MAP.put(0x1f436, R.drawable.u1f436);
        EMOTION_CLASSIC_MAP.put(0x1f484, R.drawable.u1f484);
        EMOTION_CLASSIC_MAP.put(0x1f46b, R.drawable.u1f46b);
        EMOTION_CLASSIC_MAP.put(0x1f47d, R.drawable.u1f47d);
        EMOTION_CLASSIC_MAP.put(0x1f48b, R.drawable.u1f48b);
        EMOTION_CLASSIC_MAP.put(0x1f319, R.drawable.u1f319);
        EMOTION_CLASSIC_MAP.put(0x1f349, R.drawable.u1f349);
        EMOTION_CLASSIC_MAP.put(0x1f437, R.drawable.u1f437);
        EMOTION_CLASSIC_MAP.put(0x1f494, R.drawable.u1f494);
        EMOTION_CLASSIC_MAP.put(0x1f47b, R.drawable.u1f47b);
        EMOTION_CLASSIC_MAP.put(0x1f47f, R.drawable.u1f47f);
        EMOTION_CLASSIC_MAP.put(0x1f48d, R.drawable.u1f48d);
        EMOTION_CLASSIC_MAP.put(0x1f332, R.drawable.u1f332);
        EMOTION_CLASSIC_MAP.put(0x1f434, R.drawable.u1f434);
        EMOTION_CLASSIC_MAP.put(0x1f451, R.drawable.u1f451);
        EMOTION_CLASSIC_MAP.put(0x1f525, R.drawable.u1f525);
        EMOTION_CLASSIC_MAP.put(0x2b50, R.drawable.u2b50);
        EMOTION_CLASSIC_MAP.put(0x26bd, R.drawable.u26bd);
        EMOTION_CLASSIC_MAP.put(0x1f556, R.drawable.u1f556);
        EMOTION_CLASSIC_MAP.put(0x23f0, R.drawable.u23f0);
        EMOTION_CLASSIC_MAP.put(0x1f680, R.drawable.u1f680);
        EMOTION_CLASSIC_MAP.put(0x23f3, R.drawable.u23f3);

        EMOJI_STRING.put(0x1f60a, newString(0x1f60a));
        EMOJI_STRING.put(0x1f601, newString(0x1f601));
        EMOJI_STRING.put(0x1f628, newString(0x1f628));
        EMOJI_STRING.put(0x1f60d, newString(0x1f60d));
        EMOJI_STRING.put(0x1f633, newString(0x1f633));
        EMOJI_STRING.put(0x1f60e, newString(0x1f60e));
        EMOJI_STRING.put(0x1f62d, newString(0x1f62d));
        EMOJI_STRING.put(0x1f60c, newString(0x1f60c));
        EMOJI_STRING.put(0x1f635, newString(0x1f635));
        EMOJI_STRING.put(0x1f634, newString(0x1f634));
        EMOJI_STRING.put(0x1f622, newString(0x1f622));
        EMOJI_STRING.put(0x1f605, newString(0x1f605));
        EMOJI_STRING.put(0x1f621, newString(0x1f621));
        EMOJI_STRING.put(0x1f61c, newString(0x1f61c));
        EMOJI_STRING.put(0x1f600, newString(0x1f600));
        EMOJI_STRING.put(0x1f632, newString(0x1f632));
        EMOJI_STRING.put(0x1f61f, newString(0x1f61f));
        EMOJI_STRING.put(0x1f624, newString(0x1f624));
        EMOJI_STRING.put(0x1f61e, newString(0x1f61e));
        EMOJI_STRING.put(0x1f62b, newString(0x1f62b));
        EMOJI_STRING.put(0x1f623, newString(0x1f623));
        EMOJI_STRING.put(0x1f608, newString(0x1f608));
        EMOJI_STRING.put(0x1f609, newString(0x1f609));
        EMOJI_STRING.put(0x1f62f, newString(0x1f62f));
        EMOJI_STRING.put(0x1f615, newString(0x1f615));
        EMOJI_STRING.put(0x1f630, newString(0x1f630));
        EMOJI_STRING.put(0x1f60b, newString(0x1f60b));
        EMOJI_STRING.put(0x1f61d, newString(0x1f61d));
        EMOJI_STRING.put(0x1f613, newString(0x1f613));
        EMOJI_STRING.put(0x1f603, newString(0x1f603));
        EMOJI_STRING.put(0x1f602, newString(0x1f602));
        EMOJI_STRING.put(0x1f618, newString(0x1f618));
        EMOJI_STRING.put(0x1f612, newString(0x1f612));
        EMOJI_STRING.put(0x1f60f, newString(0x1f60f));
        EMOJI_STRING.put(0x1f636, newString(0x1f636));
        EMOJI_STRING.put(0x1f631, newString(0x1f631));
        EMOJI_STRING.put(0x1f616, newString(0x1f616));
        EMOJI_STRING.put(0x1f629, newString(0x1f629));
        EMOJI_STRING.put(0x1f614, newString(0x1f614));
        EMOJI_STRING.put(0x1f611, newString(0x1f611));
        EMOJI_STRING.put(0x1f61a, newString(0x1f61a));
        EMOJI_STRING.put(0x1f62a, newString(0x1f62a));
        EMOJI_STRING.put(0x1f607, newString(0x1f607));
        EMOJI_STRING.put(0x1f64a, newString(0x1f64a));
        EMOJI_STRING.put(0x1f44a, newString(0x1f44a));
        EMOJI_STRING.put(0x1f44e, newString(0x1f44e));
        EMOJI_STRING.put(0x1f62c, newString(0x1f62c));
        EMOJI_STRING.put(0x1f637, newString(0x1f637));
        EMOJI_STRING.put(0x1f648, newString(0x1f648));
        EMOJI_STRING.put(0x1f44c, newString(0x1f44c));
        EMOJI_STRING.put(0x1f44f, newString(0x1f44f));
        EMOJI_STRING.put(0x1f4aa, newString(0x1f4aa));
        EMOJI_STRING.put(0x1f606, newString(0x1f606));
        EMOJI_STRING.put(0x1f649, newString(0x1f649));
        EMOJI_STRING.put(0x1f44d, newString(0x1f44d));
        EMOJI_STRING.put(0x1f64f, newString(0x1f64f));
        EMOJI_STRING.put(0x1f4da, newString(0x1f4da));
        EMOJI_STRING.put(0x1f381, newString(0x1f381));
        EMOJI_STRING.put(0x1f389, newString(0x1f389));
        EMOJI_STRING.put(0x1f366, newString(0x1f366));
        EMOJI_STRING.put(0x1f4b0, newString(0x1f4b0));
        EMOJI_STRING.put(0x1f382, newString(0x1f382));
        EMOJI_STRING.put(0x1f356, newString(0x1f356));
        EMOJI_STRING.put(0x1f393, newString(0x1f393));
        EMOJI_STRING.put(0x1f4a9, newString(0x1f4a9));
        EMOJI_STRING.put(0x1f384, newString(0x1f384));
        EMOJI_STRING.put(0x1f377, newString(0x1f377));
        EMOJI_STRING.put(0x1f3a4, newString(0x1f3a4));
        EMOJI_STRING.put(0x1f3c0, newString(0x1f3c0));
        EMOJI_STRING.put(0x1f004, newString(0x1f004));
        EMOJI_STRING.put(0x1f4a3, newString(0x1f4a3));
        EMOJI_STRING.put(0x1f4e2, newString(0x1f4e2));
        EMOJI_STRING.put(0x1f30f, newString(0x1f30f));
        EMOJI_STRING.put(0x1f36b, newString(0x1f36b));
        EMOJI_STRING.put(0x1f3b2, newString(0x1f3b2));
        EMOJI_STRING.put(0x1f3c2, newString(0x1f3c2));
        EMOJI_STRING.put(0x1f4a1, newString(0x1f4a1));
        EMOJI_STRING.put(0x1f4a4, newString(0x1f4a4));
        EMOJI_STRING.put(0x1f6ab, newString(0x1f6ab));
        EMOJI_STRING.put(0x1f33b, newString(0x1f33b));
        EMOJI_STRING.put(0x1f37b, newString(0x1f37b));
        EMOJI_STRING.put(0x1f3b5, newString(0x1f3b5));
        EMOJI_STRING.put(0x1f3e1, newString(0x1f3e1));
        EMOJI_STRING.put(0x1f4a2, newString(0x1f4a2));
        EMOJI_STRING.put(0x1f4de, newString(0x1f4de));
        EMOJI_STRING.put(0x1f6bf, newString(0x1f6bf));
        EMOJI_STRING.put(0x1f35a, newString(0x1f35a));
        EMOJI_STRING.put(0x1f46a, newString(0x1f46a));
        EMOJI_STRING.put(0x1f47c, newString(0x1f47c));
        EMOJI_STRING.put(0x1f48a, newString(0x1f48a));
        EMOJI_STRING.put(0x1f52b, newString(0x1f52b));
        EMOJI_STRING.put(0x1f339, newString(0x1f339));
        EMOJI_STRING.put(0x1f436, newString(0x1f436));
        EMOJI_STRING.put(0x1f484, newString(0x1f484));
        EMOJI_STRING.put(0x1f46b, newString(0x1f46b));
        EMOJI_STRING.put(0x1f47d, newString(0x1f47d));
        EMOJI_STRING.put(0x1f48b, newString(0x1f48b));
        EMOJI_STRING.put(0x1f319, newString(0x1f319));
        EMOJI_STRING.put(0x1f437, newString(0x1f437));
        EMOJI_STRING.put(0x1f349, newString(0x1f349));
        EMOJI_STRING.put(0x1f494, newString(0x1f494));
        EMOJI_STRING.put(0x1f47b, newString(0x1f47b));
        EMOJI_STRING.put(0x1f47f, newString(0x1f47f));
        EMOJI_STRING.put(0x1f48d, newString(0x1f48d));
        EMOJI_STRING.put(0x1f332, newString(0x1f332));
        EMOJI_STRING.put(0x1f434, newString(0x1f434));
        EMOJI_STRING.put(0x1f451, newString(0x1f451));
        EMOJI_STRING.put(0x1f525, newString(0x1f525));
        EMOJI_STRING.put(0x1f556, newString(0x1f556));
        EMOJI_STRING.put(0x1f680, newString(0x1f680));
        EMOJI_STRING.put(0x23f3, fromChar((char) 0x23f3));
        EMOJI_STRING.put(0x23f0, fromChar((char) 0x23f0));
        EMOJI_STRING.put(0x26bd, fromChar((char) 0x26bd));
        EMOJI_STRING.put(0x2b50, fromChar((char) 0x2b50));
        EMOJI_STRING.put(0x270f, fromChar((char) 0x270f));
        EMOJI_STRING.put(0x26c5, fromChar((char) 0x26c5));
        EMOJI_STRING.put(0x2614, fromChar((char) 0x2614));
        EMOJI_STRING.put(0x26a1, fromChar((char) 0x26a1));
        EMOJI_STRING.put(0x2744, fromChar((char) 0x2744));
        EMOJI_STRING.put(0x2601, fromChar((char) 0x2601));
        EMOJI_STRING.put(0x26c4, fromChar((char) 0x26c4));
        EMOJI_STRING.put(0x2615, fromChar((char) 0x2615));
        EMOJI_STRING.put(0x261d, fromChar((char) 0x261d));
        EMOJI_STRING.put(0x2600, fromChar((char) 0x2600));
        EMOJI_STRING.put(0x270c, fromChar((char) 0x270c));
        EMOJI_STRING.put(0x270a, fromChar((char) 0x270a));
        EMOJI_STRING.put(0x263a, fromChar((char) 0x263a));
        EMOJI_STRING.put(0x270b, fromChar((char) 0x270b));

    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName     名称
     * @return
     */
    public static int getImgByName(int EmotionType, Integer imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                integer = EMOTION_CLASSIC_MAP.get(imgName);
                break;
            default:
                LogUtils.e("the emojiMap is null!! Handle Yourself ");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     *
     * @param EmotionType
     * @return
     */
    public static LinkedHashMap getEmojiMap(int EmotionType) {
        LinkedHashMap<Integer,Integer> EmojiMap = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                EmojiMap = EMOTION_CLASSIC_MAP;
                break;
            default:
                //EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }

    public static String getEmojiString(int emojiCode) {
        return EMOJI_STRING.get(emojiCode);
    }

    public static String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    public static String fromChar(char ch) {
        return Character.toString(ch);
    }

}
