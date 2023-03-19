package com.example.demo.mapper;

import com.example.demo.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.example.demo.model.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




