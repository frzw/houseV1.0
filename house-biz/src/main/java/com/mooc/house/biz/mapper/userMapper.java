package com.mooc.house.biz.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mooc.house.common.model.User;

@Mapper
public interface userMapper {

	public List<User>  selectUsers();
	
	public int insert(User account);

	public int delete(String email);

	public int update(User updateUser);

	public List<User> selectUsersByQuery(User user);
}
