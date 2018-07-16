package com.mooc.house.common.constants;

/**
 * 房产用户类型-枚举 1-售卖，2-收藏
 */
public enum HouseUserType {

	SALE(1),BOOKMARK(2);
	
	public final Integer value;
	
	private HouseUserType(Integer value){
		this.value = value;
	}
}
