package com.mooc.house.biz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mooc.house.biz.mapper.AgencyMapper;
import com.mooc.house.common.model.Agency;
import com.mooc.house.common.model.User;
import com.mooc.house.common.page.PageData;
import com.mooc.house.common.page.PageParams;

@Service
public class AgencyService {

  @Autowired
  private AgencyMapper agencyMapper;

  @Value("${file.prefix}")
  private String imgPrefix;

  /**
   * 访问user表获取详情 添加用户头像
   * 
   * @param userId
   * @return
   */
  public User getAgentDeail(Long userId) {
    User user = new User();
    user.setId(userId);
    //经纪人
    user.setType(2);
    List<User> list = agencyMapper.selectAgent(user, PageParams.build(1, 1));
    setImg(list);
    if (!list.isEmpty()) {
      User agent = list.get(0);
      //将经纪人关联的经纪机构也一并查询出来
      Agency agency = new Agency();
      agency.setId(agent.getAgencyId().intValue());
      List<Agency> agencies = agencyMapper.select(agency);
      if (!agencies.isEmpty()) {
          agent.setAgencyName(agencies.get(0).getName());
      }
      return agent;
    }
    return null;
  }

  /**
   * 设置用户头像
   * @param list
   */
  private void setImg(List<User> list) {
    list.forEach(i -> {
      i.setAvatar(imgPrefix + i.getAvatar());
    });

  }

  /**
   * 获取所有经纪人信息
   * @param pageParams
   * @return
   */
  public PageData<User> getAllAgent(PageParams pageParams) {
    List<User> agents = agencyMapper.selectAgent(new User(), pageParams);
    setImg(agents);
    Long count = agencyMapper.selectAgentCount(new User());
    return PageData.buildPage(agents, count, pageParams.getPageSize(), pageParams.getPageNum());
  }

  public Agency getAgency(Integer id) {
    Agency agency = new Agency();
    agency.setId(id);
    List<Agency> agencies = agencyMapper.select(agency);
    if (agencies.isEmpty()) {
      return null;
    }
    return agencies.get(0);
  }

  public List<Agency> getAllAgency() {
    return agencyMapper.select(new Agency());
  }
  
  public int add(Agency agency) {
    return  agencyMapper.insert(agency);
  }

  public void sendMsg(User agent, String msg, String name, String email) {
    // TODO Auto-generated method stub
    
  }

}
