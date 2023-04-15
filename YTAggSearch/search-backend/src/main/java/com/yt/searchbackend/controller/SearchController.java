package com.yt.searchbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yt.searchbackend.annotation.AuthCheck;
import com.yt.searchbackend.common.BaseResponse;
import com.yt.searchbackend.common.DeleteRequest;
import com.yt.searchbackend.common.ErrorCode;
import com.yt.searchbackend.common.ResultUtils;
import com.yt.searchbackend.constant.UserConstant;
import com.yt.searchbackend.exception.BusinessException;
import com.yt.searchbackend.exception.ThrowUtils;
import com.yt.searchbackend.manager.SearchFacade;
import com.yt.searchbackend.model.dto.post.PostAddRequest;
import com.yt.searchbackend.model.dto.post.PostEditRequest;
import com.yt.searchbackend.model.dto.post.PostQueryRequest;
import com.yt.searchbackend.model.dto.post.PostUpdateRequest;
import com.yt.searchbackend.model.dto.search.SearchRequest;
import com.yt.searchbackend.model.dto.user.UserQueryRequest;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.model.entity.Post;
import com.yt.searchbackend.model.entity.User;
import com.yt.searchbackend.model.enums.SearchTypeEnum;
import com.yt.searchbackend.model.vo.PostVO;
import com.yt.searchbackend.model.vo.SearchVO;
import com.yt.searchbackend.model.vo.UserVO;
import com.yt.searchbackend.service.PictureService;
import com.yt.searchbackend.service.PostService;
import com.yt.searchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.Computable;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * 统一搜索接口
 *
 * @author <a href="https://github.com/DTNightWatchman">YT摆渡人</a>
 * @from
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private Executor asyncTaskExecutor;


    private final static Gson GSON = new Gson();

    @Resource
    private SearchFacade searchFacade;


    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));

    }

}
