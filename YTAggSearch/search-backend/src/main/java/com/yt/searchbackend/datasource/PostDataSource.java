package com.yt.searchbackend.datasource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.yt.searchbackend.common.ErrorCode;
import com.yt.searchbackend.constant.CommonConstant;
import com.yt.searchbackend.exception.BusinessException;
import com.yt.searchbackend.exception.ThrowUtils;
import com.yt.searchbackend.mapper.PostFavourMapper;
import com.yt.searchbackend.mapper.PostMapper;
import com.yt.searchbackend.mapper.PostThumbMapper;
import com.yt.searchbackend.model.dto.post.PostEsDTO;
import com.yt.searchbackend.model.dto.post.PostQueryRequest;
import com.yt.searchbackend.model.entity.Post;
import com.yt.searchbackend.model.entity.PostFavour;
import com.yt.searchbackend.model.entity.PostThumb;
import com.yt.searchbackend.model.entity.User;
import com.yt.searchbackend.model.vo.PostVO;
import com.yt.searchbackend.model.vo.UserVO;
import com.yt.searchbackend.service.PostService;
import com.yt.searchbackend.service.UserService;
import com.yt.searchbackend.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 * @author <a href="https://github.com/DTNightWatchman">YT摆渡人</a>
 * @from
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    private final static Gson GSON = new Gson();

    @Resource
    private PostService postService;


    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
//        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        System.out.println(postPage.getRecords().size());
        return postService.getPostVOPage(postPage, request);
    }
}
