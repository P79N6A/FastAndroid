package com.apache.fastandroid.topic.support.bean;

import com.tesla.framework.network.biz.ResultBean;

import java.util.List;

/**
 * Created by jerryliu on 2017/4/11.
 */

public class ImageResultBeans extends ResultBean {
    public String col;
    public String tag;
    public String tag3;
    public String sort;
    public int totalNum;
    public int startIndex;
    public int returnNumber;
    public List<ImageBean> imgs;

    @Override
    public String toString() {
        return "ImageResultBeans{" + "col='" + col + '\'' + ", tag='" + tag + '\'' + ", tag3='" + tag3 + '\'' + ", " +
                "sort='" + sort + '\'' + ", totalNum=" + totalNum + ", startIndex=" + startIndex + ", returnNumber="
                + returnNumber + ", imgs=" + imgs + '}';
    }
}
