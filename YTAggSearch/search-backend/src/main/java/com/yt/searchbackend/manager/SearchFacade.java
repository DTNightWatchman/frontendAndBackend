package com.yt.searchbackend.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yt.searchbackend.common.BaseResponse;
import com.yt.searchbackend.common.ErrorCode;
import com.yt.searchbackend.common.ResultUtils;
import com.yt.searchbackend.datasource.*;
import com.yt.searchbackend.exception.BusinessException;
import com.yt.searchbackend.exception.ThrowUtils;
import com.yt.searchbackend.model.dto.post.PostQueryRequest;
import com.yt.searchbackend.model.dto.search.SearchRequest;
import com.yt.searchbackend.model.dto.user.UserQueryRequest;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.model.enums.SearchTypeEnum;
import com.yt.searchbackend.model.vo.PostVO;
import com.yt.searchbackend.model.vo.SearchVO;
import com.yt.searchbackend.model.vo.UserVO;
import com.yt.searchbackend.service.PictureService;
import com.yt.searchbackend.service.PostService;
import com.yt.searchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegister dataSourceRegister;


    @Resource
    private Executor asyncTaskExecutor;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        System.out.println(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchRequest.getSearchText();
        if (searchTypeEnum == null) {
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            }, asyncTaskExecutor);

            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, 1, 10);
                return userVOPage;
            }, asyncTaskExecutor);
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, 1, 10);
                return postVOPage;
            }, asyncTaskExecutor);
            CompletableFuture.allOf(pictureTask, userTask, postTask).join();
            Page<UserVO> userVOPage = null;
            Page<Picture> picturePage = null;
            Page<PostVO> postVOPage = null;
            try {
                userVOPage = userTask.get();
                picturePage = pictureTask.get();
                postVOPage = postTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                return searchVO;
            } catch (InterruptedException | ExecutionException e) {
                log.error("搜索失败" ,e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "搜索失败");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegister.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, searchRequest.getCurrent(), searchRequest.getPageSize());
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }

    }
}
