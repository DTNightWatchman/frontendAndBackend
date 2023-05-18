package com.yt.ytsearch.datasource;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yt.ytsearch.model.dto.post.PostQueryRequest;
import com.yt.ytsearch.model.entity.Post;
import com.yt.ytsearch.model.vo.PostVO;
import com.yt.ytsearch.service.PostService;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostDatasource implements Datasource<PostVO> {

    private Gson gson = new Gson();

    @Resource
    private PostService postService;


    @Override
    public List<PostVO> doSearch(String searchTest, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchTest);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postPage.getRecords().stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtils.copyProperties(post, postVO);
            return postVO;
        }).collect(Collectors.toList());
    }
}
