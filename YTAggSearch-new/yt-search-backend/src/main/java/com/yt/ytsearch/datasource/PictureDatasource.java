package com.yt.ytsearch.datasource;

import com.yt.ytsearch.model.entity.Picture;
import com.yt.ytsearch.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class PictureDatasource implements Datasource<Picture> {

    @Resource
    private PictureService pictureService;

    @Override
    public List<Picture> doSearch(String searchTest, long pageNum, long pageSize) {
        List<Picture> pictures = pictureService.searchPicture(searchTest, pageNum);
        return pictures;
    }

}
