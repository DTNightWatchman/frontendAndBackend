package com.yt.ytsearch.service.impl;


import cn.hutool.json.JSONUtil;
import com.yt.ytsearch.common.ErrorCode;
import com.yt.ytsearch.exception.BusinessException;
import com.yt.ytsearch.model.entity.Picture;
import com.yt.ytsearch.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServiceImpl implements PictureService {

    private final Integer pageSize = 10;

    @Override
    public List<Picture> searchPicture(String searchText, Long current) {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.bing.com/images/search?q=" + searchText + "&qs=ds&form=QBIR&first=" + current).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取失败");
        }
        //System.out.println(doc);
        Elements elements = doc.select(".iuscp.isv");
        //System.out.println(elements.size());
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            final Object o = map.get("murl");
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl((String) o);
            pictures.add(picture);
            if (pictures.size() >= pageSize) {
                break;
            }
        }
        return pictures;
    }
}
