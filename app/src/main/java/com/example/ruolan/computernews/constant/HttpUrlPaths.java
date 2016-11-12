package com.example.ruolan.computernews.constant;

/**
 * Created by Administrator on 2016/11/12.
 */

public class HttpUrlPaths {

    public static final String COMPUTER_BASE_URL = "http://121.42.174.82/api/catalogs/";

    public static final String COMPUTER_BASE_DETAIL = "http://121.42.174.82/api/articles/";

    /**
     * 获取到新闻资讯列表
     *
     * @param type       资讯类型
     * @param startIndex 开始位置
     * @param endIndex   结束位置
     * @return url
     */
    public static String getNewsData(String type, int startIndex, int endIndex) {
        return COMPUTER_BASE_URL + type + "/articles?startIndex=" + startIndex + "&endIndex=" + endIndex;
    }


    /**
     * 获取到新闻轮播图列表
     *
     * @param type 资讯类型
     * @return url
     */
    public static String getNewBanner(String type) {
        return COMPUTER_BASE_URL + type +
                "/articles/headline?startIndex=0&endIndex=5";
    }

    public static String getNewsDetailUrl(String id) {
        return COMPUTER_BASE_DETAIL + id;
    }
}
