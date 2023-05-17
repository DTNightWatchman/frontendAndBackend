package com.yt.ytsearch.service;

import com.yt.ytsearch.model.entity.Picture;

import java.util.List;

public interface PictureService {

    List<Picture> searchPicture(String searchText, Long current);
}
