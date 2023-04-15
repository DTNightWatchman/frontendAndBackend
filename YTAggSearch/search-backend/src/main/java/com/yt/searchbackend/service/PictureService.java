package com.yt.searchbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yt.searchbackend.model.entity.Picture;

import java.util.List;

/**
 * 图片服务
 */
public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);

}
