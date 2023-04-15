package com.yt.searchbackend.datasource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yt.searchbackend.common.ErrorCode;
import com.yt.searchbackend.constant.CommonConstant;
import com.yt.searchbackend.exception.BusinessException;
import com.yt.searchbackend.mapper.UserMapper;
import com.yt.searchbackend.model.dto.user.UserQueryRequest;
import com.yt.searchbackend.model.entity.User;
import com.yt.searchbackend.model.enums.UserRoleEnum;
import com.yt.searchbackend.model.vo.LoginUserVO;
import com.yt.searchbackend.model.vo.UserVO;
import com.yt.searchbackend.service.UserService;
import com.yt.searchbackend.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yt.searchbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/DTNightWatchman">YT摆渡人</a>
 * @from
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
}
