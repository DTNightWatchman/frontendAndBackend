package com.yt.searchbackend.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yt.searchbackend.model.entity.Picture;
import com.yt.searchbackend.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *
 * @author <a href="https://github.com/DTNightWatchman">YT摆渡人</a>
 * @from
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<Picture> pictureList;

    private List<PostVO> postList;

    private List<?> dataList;


}
