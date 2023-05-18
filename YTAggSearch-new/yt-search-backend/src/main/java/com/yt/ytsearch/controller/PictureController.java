package com.yt.ytsearch.controller;

import com.yt.ytsearch.common.BaseResponse;
import com.yt.ytsearch.common.ResultUtils;
import com.yt.ytsearch.model.dto.picture.PictureQueryRequest;
import com.yt.ytsearch.model.entity.Picture;
import com.yt.ytsearch.service.PictureService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    @PostMapping("/search")
    public BaseResponse<List<Picture>> searchPicture(@RequestBody PictureQueryRequest pictureQueryRequest) {
        List<Picture> pictures = pictureService.searchPicture(pictureQueryRequest.getSearchText(), pictureQueryRequest.getCurrent());
        return ResultUtils.success(pictures);
    }
}
