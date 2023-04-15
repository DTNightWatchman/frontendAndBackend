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
import com.yt.searchbackend.model.dto.picture.PictureQueryRequest;
import com.yt.searchbackend.model.dto.post.PostAddRequest;
import com.yt.searchbackend.model.dto.post.PostEditRequest;
import com.yt.searchbackend.model.dto.post.PostQueryRequest;
import com.yt.searchbackend.model.dto.post.PostUpdateRequest;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.model.entity.Post;
import com.yt.searchbackend.model.entity.User;
import com.yt.searchbackend.model.vo.PostVO;
import com.yt.searchbackend.service.PictureService;
import com.yt.searchbackend.service.PostService;
import com.yt.searchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/DTNightWatchman">YT摆渡人</a>
 * @from
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                           HttpServletRequest request) {

        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }



}
