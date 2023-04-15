package com.yt.searchbackend;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.model.entity.Post;
import com.yt.searchbackend.service.PostService;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.json.Json;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class CrawlerTest {

    @Resource
    private PostService postService;

    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        Document doc = Jsoup.connect("https://www.bing.com/images/search?q=小黑子&qs=ds&form=QBIR&first=" + current).get();
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
        }
        System.out.println(pictures);
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
//            System.out.println(headline);
//        }
    }

    @Test
    void testFetchPassage() {
        String json = "{\n" +
                "  \"current\": 1,\n" +
                "  \"pageSize\": 8,\n" +
                "  \"sortField\": \"createTime\",\n" +
                "  \"sortOrder\": \"descend\",\n" +
                "  \"category\": \"文章\",\n" +
                "  \"reviewStatus\": 1\n" +
                "}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result2 = HttpRequest.post(url)
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result2, Map.class);
        JSONObject jsonObject = (JSONObject) map.get("data");
        JSONArray jsonArray = (JSONArray) jsonObject.get("records");
        List<Post> posts = new ArrayList<>();

        for (Object record : jsonArray) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            posts.add(post);
        }
        boolean b = postService.saveBatch(posts);
        Assertions.assertTrue(b);
    }
}
