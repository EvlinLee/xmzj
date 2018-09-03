package com.gxtc.huchuan.utils;

import com.gxtc.commlibrary.utils.LogUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/24.
 */
public class StringUtilTest {

    @Test
    public void fromat10000() throws Exception {
        String s  = StringUtil.Fromat10000("12020");
        LogUtil.i(s);
    }

}