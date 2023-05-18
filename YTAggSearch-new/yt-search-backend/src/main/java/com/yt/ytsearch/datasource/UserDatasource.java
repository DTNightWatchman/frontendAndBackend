package com.yt.ytsearch.datasource;

import com.yt.ytsearch.model.entity.User;
import com.yt.ytsearch.model.vo.UserVO;
import com.yt.ytsearch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserDatasource implements Datasource<UserVO> {

    @Resource
    private UserService userService;


    @Override
    public List<UserVO> doSearch(String searchTest, long pageNum, long pageSize) {
        List<UserVO> userVOS = userService.searchUser(searchTest, pageNum);
        return userVOS;
    }
}
