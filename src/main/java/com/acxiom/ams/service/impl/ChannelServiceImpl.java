package com.acxiom.ams.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.acxiom.ams.api.ServiceAPI.UserCenterAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.TenantAndChannelPoMapper;
import com.acxiom.ams.model.dto.v2.TenantAndChannelDTO;
import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.ChannelVo;
import com.acxiom.ams.model.vo.TenantAndChannelVo;
import com.acxiom.ams.model.vo.TenantTypeAndDestinationsVO;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.TenantAndChannelPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.ChannelService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.UniverseService;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:43 1/3/2018
 */
@Service
public class ChannelServiceImpl implements ChannelService {

  private static Date lastRunDate;
  @Autowired
  TenantAndChannelPoJPA tenantAndChannelPoJPA;
  @Autowired
  TenantPoJPA tenantPoJPA;
  @Autowired
  AudiencePoJPA audiencePoJPA;
  @Autowired
  TenantAndChannelPoMapper tenantAndChannelPoMapper;
  @Autowired
  ErrorMessageSourceHandler errorMessageSourceHandler;
  @Autowired
  UserCenterAPI userCenterAPI;
  @Autowired
  TenantService tenantService;
  @Autowired
  UniverseService universeService;


  @Override
  public String transfer(String str) {
    int len = str.length();
    String a = str.substring(2, len - 2);
    String newstr = str.replaceAll(a, "********");
    return newstr;
  }

  @Override
  public void configChannelAndTenant(TenantAndChannelDTO tenantAndChannelDTO) throws AMSException {
    updateTenantBySsoTenant();
    if (!Optional.ofNullable(tenantAndChannelDTO.getSftpPassword()).isPresent()
        && !Optional.ofNullable(tenantAndChannelDTO.getSftpPath()).isPresent()) {
      throw new AMSInvalidInputException(Constant.ERROR_CODE_0238,
          errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0238));
    }
    String channelName = tenantAndChannelDTO.getChannelName();
    TenantPo tenantPo = tenantService.getTenantById(tenantAndChannelDTO.getTenantId());
    TenantAndChannelPo tenantAndChannelInDB =
        tenantAndChannelPoJPA.findByTenantPoAndChannelName(tenantPo, channelName);
    if (Optional.ofNullable(tenantAndChannelInDB).isPresent()) {
      throw new AMSInvalidInputException(Constant.ERROR_CODE_0218,
          errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0218));
    }
    TenantAndChannelPo tenantAndChannelPo = new TenantAndChannelPo();
    if (tenantAndChannelDTO.getSftpPort() == null) {
      tenantAndChannelPo.setPort(0);
    } else {
      tenantAndChannelPo.setPort(tenantAndChannelDTO.getSftpPort());
    }
    tenantAndChannelPo.setHost(tenantAndChannelDTO.getSftpHost());
    tenantAndChannelPo.setKeyFile(tenantAndChannelDTO.getSftpKeyFile());
    tenantAndChannelPo.setPassPhrase(tenantAndChannelDTO.getSftpPassphrase());
    System.out.println(tenantAndChannelDTO.getSftpPassword().indexOf("********"));
    if (tenantAndChannelDTO.getSftpPassword().indexOf("********") == -1) {
    tenantAndChannelPo.setPassword(tenantAndChannelDTO.getSftpPassword());
    }
    tenantAndChannelPo.setPath(tenantAndChannelDTO.getSftpPath());
    tenantAndChannelPo.setUsername(tenantAndChannelDTO.getSftpUsername());
    tenantAndChannelPo.setCreatedBy(tenantAndChannelDTO.getCreateBy());
    tenantAndChannelPo.setChannelName(channelName);
    if (tenantAndChannelDTO.getLrAudienceId() == null) {
      tenantAndChannelPo.setLrAudienceId("");
    } else {
      tenantAndChannelPo.setLrAudienceId(tenantAndChannelDTO.getLrAudienceId());
    }
    tenantAndChannelPo.setDataStoreDestinationId(tenantAndChannelDTO.getDataStoreDestinationId());
    tenantAndChannelPo.setDataStoreIntegrationId(tenantAndChannelDTO.getDataStoreIntegrationId());
    tenantAndChannelPo.setOnBoardDestinationId(tenantAndChannelDTO.getOnBoardDestinationId());
    tenantAndChannelPo.setOnBoardIntegrationId(tenantAndChannelDTO.getOnBoardIntegrationId());
    tenantAndChannelPo.setIcon(tenantAndChannelDTO.getIcon());
    tenantAndChannelPo.setTenantPo(tenantPo);
    tenantAndChannelPoJPA.save(tenantAndChannelPo);
  }

//  @Override
//  public List<TenantAndChannelVo> getChannelListByTenantId(long tenantId)
//      throws AMSInvalidInputException {
//    TenantPo tenantPo = tenantService.getTenantById(tenantId);
//    List<TenantAndChannelPo> tenantAndChannelPoList =
//        tenantAndChannelPoJPA.findByTenantPo(tenantPo);
//    for (int i = 0; i < tenantAndChannelPoList.size(); i++) {
//      String newpwd = transfer(tenantAndChannelPoList.get(i).getPassword());
//      tenantAndChannelPoList.get(i).setPassword(newpwd);
//    }

//     return tenantAndChannelPoMapper.map(tenantAndChannelPoList);
//    return null;
//  }

  @Override
  public List<TenantAndChannelVo> getChannelListByTenantId(long tenantId)
      throws AMSInvalidInputException {
    TenantPo tenantPo = tenantService.getTenantById(tenantId);
    List<TenantAndChannelPo> tenantAndChannelPoList =
        tenantAndChannelPoJPA.findByTenantPo(tenantPo);
    return tenantAndChannelPoMapper.map(tenantAndChannelPoList);
  }

  @Override
  public void deleteTenantChannelById(Long id) throws AMSInvalidInputException {
    getTenantChannelById(id);
    tenantAndChannelPoJPA.delete(id);
  }

  @Override
  public TenantTypeAndDestinationsVO getTenantTypeAndDestinationsByTenantId(Long tenantId)
      throws AMSException {
    TenantPo tenantPo = tenantService.getTenantById(tenantId);
    TenantTypeAndDestinationsVO tenantTypeAndDestinationsVO = new TenantTypeAndDestinationsVO();
    TenantVo tenantVo = Optional.ofNullable(userCenterAPI.getTenantById(tenantPo.getTenantId()))
        .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
    List<ChannelVo> channelVoList = new ArrayList<>();
    tenantTypeAndDestinationsVO.setPlatformType(tenantVo.getPlatformType());
    if (StringUtils.equals("MVPD", tenantVo.getPlatformType())) {
      List<UniversePo> universePoList = universeService.listUniverseByTenantId(tenantId, "");
      for (UniversePo universePo : universePoList) {
        ChannelVo channelVo = new ChannelVo();
        channelVo.setChannelName(universePo.getUniverseName());
        channelVo.setId(universePo.getId());
        channelVoList.add(channelVo);
      }
    }
    tenantTypeAndDestinationsVO.setChannelVoList(channelVoList);
    return tenantTypeAndDestinationsVO;
  }

  @Override
  public void updateTenantChannelById(long id, TenantAndChannelDTO tenantAndChannelDTO)
      throws AMSException {
    updateTenantBySsoTenant();
    TenantAndChannelPo tenantAndChannelPo = tenantAndChannelPoJPA.findOne(id);
    String channelName = tenantAndChannelPo.getChannelName();
    long tenantId = tenantAndChannelPo.getTenantPo().getId();
    if (!StringUtils.equalsIgnoreCase(channelName, tenantAndChannelDTO.getChannelName())
        || (tenantId != tenantAndChannelDTO.getTenantId())) {
      if (Optional.ofNullable(tenantAndChannelPoJPA.findByTenantPoAndChannelName(
          tenantService.getTenantById(tenantAndChannelDTO.getTenantId()),
          tenantAndChannelDTO.getChannelName())).isPresent()) {
        throw new AMSInvalidInputException(Constant.ERROR_CODE_0219,
            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0219));
      }
    }
    if (!StringUtils.equals(tenantAndChannelPo.getCreatedBy(), tenantAndChannelDTO.getCreateBy())) {
      throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
          errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
    }
    tenantAndChannelPo.setPort(tenantAndChannelDTO.getSftpPort());
    tenantAndChannelPo.setHost(tenantAndChannelDTO.getSftpHost());
    tenantAndChannelPo.setKeyFile(tenantAndChannelDTO.getSftpKeyFile());
    tenantAndChannelPo.setPassPhrase(tenantAndChannelDTO.getSftpPassphrase());



    // String test = tenantAndChannelPoJPA.findOne(id).getPassword();
    System.out.println(tenantAndChannelPoJPA.findOne(id).getPassword().indexOf("********"));
    System.out.println(tenantAndChannelDTO.getSftpPassword().indexOf("********"));
     if (tenantAndChannelDTO.getSftpPassword().indexOf("********") == -1) {
       System.out.println("-------------------------------");
     tenantAndChannelPo.setPassword(tenantAndChannelDTO.getSftpPassword());
     }
//    tenantAndChannelPo.setPassword(tenantAndChannelDTO.getSftpPassword());



    tenantAndChannelPo.setPath(tenantAndChannelDTO.getSftpPath());
    tenantAndChannelPo.setUsername(tenantAndChannelDTO.getSftpUsername());
    tenantAndChannelPo.setCreatedBy(tenantAndChannelDTO.getCreateBy());
    tenantAndChannelPo.setChannelName(tenantAndChannelDTO.getChannelName());
    tenantAndChannelPo.setIcon(tenantAndChannelDTO.getIcon());
    tenantAndChannelPo.setLrAudienceId(tenantAndChannelDTO.getLrAudienceId());
    tenantAndChannelPo.setDataStoreDestinationId(tenantAndChannelDTO.getDataStoreDestinationId());
    tenantAndChannelPo.setDataStoreIntegrationId(tenantAndChannelDTO.getDataStoreIntegrationId());
    tenantAndChannelPo.setOnBoardDestinationId(tenantAndChannelDTO.getOnBoardDestinationId());
    tenantAndChannelPo.setOnBoardIntegrationId(tenantAndChannelDTO.getOnBoardIntegrationId());
    tenantAndChannelPo.setTenantPo(tenantService.getTenantById(tenantAndChannelDTO.getTenantId()));
    tenantAndChannelPoJPA.save(tenantAndChannelPo);
  }
  
  @Override
  public TenantAndChannelPo getTenantChannelById(Long id) throws AMSInvalidInputException {
    TenantAndChannelPo newpo = tenantAndChannelPoJPA.findOne(id);
    newpo.setPassword(transfer(newpo.getPassword()));
    System.out.println(tenantAndChannelPoJPA.findOne(id).getPassword());
    return Optional.ofNullable(newpo)
        .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0227,
            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0227)));
  }
  
//  @Override
//  public TenantAndChannelPo getTenantChannelById(Long id) throws AMSInvalidInputException {
//    TenantAndChannelPo newpo = tenantAndChannelPoJPA.findOne(id);
//    newpo.setPassword(transfer(newpo.getPassword()));
//    return Optional.ofNullable(newpo)
//        .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0227,
//            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0227)));
//  }

  private void updateTenantBySsoTenant()
      throws AMSRMIException, AMSInvalidInputException, JSONException {
    Date currentDate = new Date();
    if (!Optional.ofNullable(lastRunDate).isPresent()
        || (new Date().getTime() - lastRunDate.getTime()) / (1000 * 3600) > 1) {
      lastRunDate = currentDate;
      String resp = Optional.ofNullable(userCenterAPI.getAllTenant())
          .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0232,
              errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0232)));
      try {
        JSONArray array = JSON.parseArray(resp);
        List<TenantPo> tenantPoList = tenantPoJPA.findAll();
        List<TenantPo> tenantPoForDeleteList = tenantPoList.stream().filter(tenantPo -> {
          for (int i = 0; i < array.size(); i++) {
            if (StringUtils.equals(tenantPo.getTenantId(),
                array.getJSONObject(i).getString("tenantId"))) {
              return false;
            }
          }
          return true;
        }).collect(Collectors.toList());
        tenantPoJPA.delete(tenantPoForDeleteList);
      } catch (JSONException e) {
        throw new AMSInvalidInputException(Constant.ERROR_CODE_0232,
            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0232));
      }
    }
  }
}
