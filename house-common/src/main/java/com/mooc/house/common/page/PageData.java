package com.mooc.house.common.page;

import java.util.List;

/**
 * 分页数据
 * @param <T>
 */
public class PageData<T> {

	private List<T> list;
	private Pagination pagination;//pageNum,pageSize,page list
	
	public PageData(Pagination pagination,List<T> list){
		this.pagination = pagination;
		this.list = list;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	/**
	 * 构建分页
	 * @param list
	 * @param count
	 * @param pageSize
	 * @param pageNum
	 * @param <T>
	 * @return
	 */
	public static  <T> PageData<T> buildPage(List<T> list,Long count,Integer pageSize,Integer pageNum){
		Pagination _pagination = new Pagination(pageSize, pageNum, count);
		return new PageData<>(_pagination, list);
	}
	
}
