package com.mooc.house.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Objects;
import com.mooc.house.biz.service.AgencyService;
import com.mooc.house.biz.service.HouseService;
import com.mooc.house.biz.service.MailService;
import com.mooc.house.biz.service.RecommendService;
import com.mooc.house.common.constants.CommonConstants;
import com.mooc.house.common.model.Agency;
import com.mooc.house.common.model.House;
import com.mooc.house.common.model.User;
import com.mooc.house.common.page.PageData;
import com.mooc.house.common.page.PageParams;
import com.mooc.house.common.result.ResultMsg;
import com.mooc.house.web.interceptor.UserContext;

@Controller
public class AgencyController {
  
  @Autowired
  private AgencyService agencyService;
  
  @Autowired
  private RecommendService recommendService;
  
  @Autowired
  private HouseService houseService;
  
  @Autowired
  private MailService mailService;

  @RequestMapping("agency/create")
  public String agencyCreate(){
    User user =  UserContext.getUser();
    if (user == null || !Objects.equal(user.getEmail(), "spring_boot@163.com")) {
      return "redirect:/accounts/signin?" + ResultMsg.successMsg("请先登录").asUrlParams();
    }
    return "/user/agency/create";
  }

    /**
     * 获取经纪人列表
     * @param pageSize
     * @param pageNum
     * @param modelMap
     * @return
     */
  @RequestMapping("/agency/agentList")
  public String agentList(Integer pageSize,Integer pageNum,ModelMap modelMap){
    if (pageSize == null) {
      pageSize = 6;
    }
    PageData<User> ps = agencyService.getAllAgent(PageParams.build(pageSize, pageNum));
    List<House> houses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
    modelMap.put("recomHouses", houses);
    modelMap.put("ps", ps);
    return "/user/agent/agentList";
  }

    /**
     * 经纪人详情
     * @param id
     * @param modelMap
     * @return
     */
  @RequestMapping("/agency/agentDetail")
  public String agentDetail(Long id,ModelMap modelMap){
      User user =  agencyService.getAgentDeail(id);
      List<House> houses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
      House query = new House();
      //对应经纪人userId
      query.setUserId(id);
      query.setBookmarked(false);
      PageData<House> bindHouse = houseService.queryHouse(query, new PageParams(3,1));
      if (bindHouse != null) {
        modelMap.put("bindHouses", bindHouse.getList()) ;
      }
      modelMap.put("recomHouses", houses);
      modelMap.put("agent", user);
      modelMap.put("agencyName", user.getAgencyName());
      return "/user/agent/agentDetail";
  }
  
  @RequestMapping("/agency/agentMsg")
  public String agentMsg(Long id,String msg,String name,String email, ModelMap modelMap){
      User user =  agencyService.getAgentDeail(id);
      mailService.sendMail("咨询", "email:"+email+",msg:"+msg, user.getEmail());
      return "redirect:/agency/agentDetail?id="+id +"&" + ResultMsg.successMsg("留言成功").asUrlParams();
  }
  
  @RequestMapping("/agency/agencyDetail")
  public String agencyDetail(Integer id,ModelMap modelMap){
      Agency agency =  agencyService.getAgency(id);
      List<House> houses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
      modelMap.put("recomHouses", houses);
      modelMap.put("agency", agency);
      return "/user/agency/agencyDetail";
  }
  
  
  
  @RequestMapping("agency/list")
  public String agencyList(ModelMap modelMap){
    List<Agency> agencies = agencyService.getAllAgency();
    List<House> houses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
    modelMap.put("recomHouses", houses);
    modelMap.put("agencyList", agencies);
    return "/user/agency/agencyList";
  }
  
  @RequestMapping("agency/submit")
  public String agencySubmit(Agency agency){
    User user =  UserContext.getUser();
    if (user == null || !Objects.equal(user.getEmail(), "spring_boot@163.com")) {//只有超级管理员可以添加,拟定spring_boot@163.com为超管
      return "redirect:/accounts/signin?" + ResultMsg.successMsg("请先登录").asUrlParams();
    }
    agencyService.add(agency);
    return "redirect:/index?" + ResultMsg.successMsg("创建成功").asUrlParams();
  }

  

}
