package com.mooc.house.web.controller;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;
import com.mooc.house.common.model.User;
import com.mooc.house.common.result.ResultMsg;

/**
 * 用户表单信息验证
 */
public class UserHelper {
	
	public static ResultMsg validate(User accout) {
		if (StringUtils.isBlank(accout.getEmail())) {
			return ResultMsg.errorMsg("Email 有误");
		}
		if (StringUtils.isBlank(accout.getName())) {
			return ResultMsg.errorMsg("姓名 有误");
		}
		if (StringUtils.isBlank(accout.getConfirmPasswd()) || StringUtils.isBlank(accout.getPasswd())
				|| !accout.getPasswd().equals(accout.getConfirmPasswd())) {
			return ResultMsg.errorMsg("密码 有误");
		}
		if (accout.getPasswd().length() < 6) {
			return ResultMsg.errorMsg("密码大于6位");
		}
		return ResultMsg.successMsg("");
	}

	/**
	 * 重置密码确认
	 * @param key
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	public static ResultMsg validateResetPassword(String key, String password, String confirmPassword) {
	    if (StringUtils.isBlank(key) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
	      return ResultMsg.errorMsg("参数有误");
	    }
	    if (!Objects.equal(password, confirmPassword)) {
	      return ResultMsg.errorMsg("密码必须与确认密码一致");
	    }
	    return ResultMsg.successMsg("重置密码成功");
	  }

}
