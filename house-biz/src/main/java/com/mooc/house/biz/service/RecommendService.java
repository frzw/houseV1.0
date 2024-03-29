package com.mooc.house.biz.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mooc.house.common.model.House;
import com.mooc.house.common.page.PageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 推荐房产
 * 热榜服务层，使用Jedis缓存进行处理，使用数据结构为：Zset
 */
@Service
public class RecommendService {

  private static final String HOT_HOUSE_KEY = "hot_house";

  private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

  @Autowired
  private HouseService houseService;

  /**
   * 计时器
   * @param id 即为houseId
   */
  public void increase(Long id) {
    try {
      //连接Redis服务
      Jedis jedis = new Jedis("127.0.0.1",6379);
      //ZSet结构，类似于Hash MaP<List<Map<Interger,Interger>>>
      jedis.zincrby(HOT_HOUSE_KEY, 1.0D, id + "");
      //0代表第一个元素,-1代表最后一个元素
      //jedis.zremrangeByRank(HOT_HOUSE_KEY, 10, -1);// 移除从11个元素到最后一个元素，默认得分是从低到高排序的
      jedis.zremrangeByRank(HOT_HOUSE_KEY, 0, -11);// 0代表第一个元素,-1代表最后一个元素，保留热度由低到高末尾10个房产
      jedis.close();
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
   
  }

  /**
   * 获取热榜ids
   * @return
   */
  public List<Long> getHot() {
    try {
      Jedis jedis = new Jedis("127.0.0.1",6379);
      Set<String> idSet = jedis.zrevrange(HOT_HOUSE_KEY, 0, -1);//默认得分是从低到高排序的，这里从从高到低排序的
      jedis.close();
      List<Long> ids = idSet.stream().map(Long::parseLong).collect(Collectors.toList());
      return ids;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);//有同学反应在未安装redis时会报500,在这里做下兼容,
      return Lists.newArrayList();
    }

  }

  /**
   * 获取热榜房产
   * @param size 页面展示的房产个数
   * @return
   */
  public List<House> getHotHouse(Integer size) {
    House query = new House();
    List<Long> list = getHot();
    list = list.subList(0, Math.min(list.size(), size));
    if (list.isEmpty()) {
      return Lists.newArrayList();
    }
    query.setIds(list);//存储的是房产Ids
    final List<Long> order = list;
    List<House> houses = houseService.queryAndSetImg(query, PageParams.build(size, 1));
    //定义规则
    Ordering<House> houseSort = Ordering.natural().onResultOf(hs -> {
      return order.indexOf(hs.getId());
    });
    //排序后，重新复制
    return houseSort.sortedCopy(houses);
  }

  /**
   * 获取最新房源
   * @return
   */
  public List<House> getLastest() {
    House query = new House();
    query.setSort("create_time");
    List<House> houses = houseService.queryAndSetImg(query, new PageParams(3, 1));
    return houses;
  }
}
