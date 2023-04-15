package com.yt.searchbackend.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yt.searchbackend.common.ErrorCode;
import com.yt.searchbackend.exception.BusinessException;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureDataSource implements DataSource<Picture> {
    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        Document doc = null;
        try {
            String url = "https://www.bing.com/images/search?q=" + searchText + "&qs=ds&form=QBIR&first=" + current;
            //System.out.println(url);
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取失败");        }
        //System.out.println(doc);
        Elements elements = doc.select(".iuscp.isv");
        System.out.println(elements.size());
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
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictures);

        return picturePage;
    }
}
