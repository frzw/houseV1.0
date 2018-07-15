package com.mooc.house.common.page;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 分页插件
 */
public class Pagination {
	private int pageNum;//当前页
	private int pageSize;//每页显示数
	private long totalCount;//总数
	private List<Integer> pages = Lists.newArrayList();//显示页面

	public Pagination(Integer pageSize,Integer pageNum,Long totalCount) {
	   this.totalCount = totalCount;
	   this.pageNum = pageNum;
	   this.pageSize = pageSize;
	   for(int i=1;i<=pageNum;i++){
		   pages.add(i);
	   }
	   Long pageCount = totalCount/pageSize + ((totalCount % pageSize == 0 ) ? 0: 1);//总页数
	   if (pageCount > pageNum) {
		  for(int i= pageNum + 1; i<= pageCount ;i ++){
			  pages.add(i);//显示页面
		  }
	   }
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<Integer> getPages() {
		return pages;
	}

	public void setPages(List<Integer> pages) {
		this.pages = pages;
	}
}
