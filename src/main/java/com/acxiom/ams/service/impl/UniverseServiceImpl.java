package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.TenantVoMapper;
import com.acxiom.ams.model.dto.v2.UniverseDTO;
import com.acxiom.ams.model.dto.v2.UniverseForUpdateDTO;
import com.acxiom.ams.model.dto.v2.UniverseIntegrationDTO;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.em.UniverseType;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.Icon;
import com.acxiom.ams.model.vo.SourceItem;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.model.vo.WhiteTenant;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.*;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UniverseServiceImpl implements UniverseService {
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    TenantVoMapper tenantVoMapper;
    @Autowired
    TenantService tenantService;
    @Autowired
    UniversePoJPA universePoJPA;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;
    @Autowired
    VersionPoService versionPoService;
    @Autowired
    ServiceAPI.TaxonomyAPI taxonomyAPI;
    @Autowired
    VersionPoJPA versionPoJPA;
    @Autowired
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Autowired
    TenantAndUniversePoJPA tenantAndUniversePoJPA;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    UniverseIntegrationPoJPA universeIntegrationPoJPA;
    @Autowired
    AudiencePoService audiencePoService;
    @Autowired
    ServiceAPI.DataSourceAPI dataSourceAPI;

    private static final String UNIVERSE_PREFIX = "universe_";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUniverse(Long tenantId, UniverseDTO universeDTO) throws
            AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        String universeSystemName = UNIVERSE_PREFIX.concat(universeDTO.getUniverseName()
                .replaceAll(" ", "").toLowerCase());
        List<UniversePo> universePoList =
                universePoJPA.findByUniverseNameOrUniverseSystemName(universeDTO.getUniverseName(), universeSystemName);
        for (UniversePo universePo : universePoList) {
            if (StringUtils.equals(universePo.getTenantPath(), tenantPo.getPath())
                    && StringUtils.equals(universePo.getOwnerTenantPath(), tenantPo.getPath())) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0258,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0258));
            }
        }
        UniversePo universe = new UniversePo();
        universe.setTenantId(tenantPo.getId());
        universe.setTenantPath(tenantPo.getPath());
        universe.setUniverseCount(universeDTO.getUniverseCount());
        universe.setUniverseRuleJson(universeDTO.getUniverseRuleJson());
        universe.setCreatedBy(universeDTO.getCreatedBy());
        universe.setUniverseName(universeDTO.getUniverseName());
        universe.setUniverseSystemName(universeSystemName);
        universe.setUniverseType(UniverseType.OTHER);
        universe.setUniverseThreshold(universeDTO.getUniverseThreshold());
        universe.setSegmentThreshold(universeDTO.getSegmentThreshold());
        universe.setUniverseStatus(SegmentStatusType.UNIVERSE_PROCESSING);
        universe.setOwnerTenantPath(tenantPo.getPath());
        universePoJPA.save(universe);
        Long universeId = universe.getId();
        HashMap paramMap = new HashMap<String, Object>();
        paramMap.put("tenantPath", tenantPo.getPath());
        paramMap.put("universeSysName", universeSystemName);
        paramMap.put("universeName", universeDTO.getUniverseName());
        paramMap.put("universeId", universeId);
        paramMap.put("campaign", JSONObject.parseObject(universeDTO.getUniverseRuleJson()));
        paramMap.put("tenantId", tenantPo.getId());
        paramMap.put("ownerUniverseId", universeId);
        paramMap.put("ownerTenantPath", tenantPo.getPath());
        bitmapAPI.createUniverse(JSONObject.toJSONString(paramMap));
        return universe.getId();
    }

    @Override
    public void callbackUniverseStatus(String tenantPath, String universeSystemName, String universeJobId,
                                       Boolean status)
            throws AMSInvalidInputException {
        UniversePo universePo = getUniverseBySystemNameAndTenantPath(universeSystemName, tenantPath);
        if (!Optional.ofNullable(universePo.getUniverseJobId()).isPresent()
                && universeJobId.compareTo(universePo.getUniverseJobId()) < 0) {
            return;
        }
        if (status) {
            universePo.setUniverseStatus(SegmentStatusType.UNIVERSE_SUCCESS);
        } else {
            universePo.setUniverseStatus(SegmentStatusType.UNIVERSE_FAILED);
        }
        universePo.setUpdateTime(new Date());
        universePoJPA.save(universePo);
    }

    @Override
    public String calculateUniverse(Long tenantId, String rules) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        HashMap paramMap = new HashMap<String, Object>();
        paramMap.put("tenantPath", tenantPo.getPath());
        paramMap.put("campaign", JSONObject.parseObject(rules));
        return bitmapAPI.calculateUniverse(JSONObject.toJSONString(paramMap));
    }

    private UniversePo getUniverseBySystemNameAndTenantPath(String universeSystemName, String tenantPath) throws
            AMSInvalidInputException {
        return Optional.ofNullable(universePoJPA.findByUniverseSystemNameAndTenantPath(universeSystemName,
                tenantPath)).orElseThrow(()
                -> new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257)));
    }

    @Override
    public UniversePo getUniverseByIdAndTenantId(Long universeId, Long tenantId) throws
            AMSInvalidInputException {
        return Optional.ofNullable(universePoJPA.findByIdAndTenantId(universeId, tenantId)).orElseThrow(()
                -> new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257)));
    }

    @Override
    public List<SourceItem> getMyDataByTenantId(Long tenantId) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionPoService.getActiveVersionByTenant(tenantPo);
        List<SourceItem> sourceItemList = new ArrayList<>();
        if (!Optional.ofNullable(versionPo.getTreeId()).isPresent() || versionPo.getTreeId().isEmpty()) {
            return sourceItemList;
        }
        List<Taxonomy> taxonomyList = taxonomyAPI
                .getTaxonomyList(String.valueOf(versionPo.getId()), versionPo.getTreeId());
        for (Taxonomy taxonomy : taxonomyList) {
            if (!StringUtils.equals(taxonomy.getTaxonomyId(), "MY_DATA")) {
                continue;
            }
            SourceItem sourceItem = new SourceItem();
            sourceItem.setName(taxonomy.getName());
            sourceItem.setId(taxonomy.getObjectId());
            sourceItem.setSourceType(SourceType.TAXONOMY);
            sourceItem.setTenantId(versionPo.getTenantPo().getId());
            sourceItemList.add(sourceItem);
        }
        return sourceItemList;
    }

    @Override
    public UniversePo getUniverseById(Long universeId) throws AMSInvalidInputException {
        return Optional.ofNullable(universePoJPA.findOne(universeId)).orElseThrow(()
                -> new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UniversePo> listUniverseByTenantId(Long tenantId, String username) throws AMSException {
        List<UniversePo> universePoList = universePoJPA.findAllByTenantId(tenantId);
        if (universePoList.isEmpty()) {
            TenantPo tenantPo = tenantService.getTenantById(tenantId);
            String universeSystemName = "universe_".concat(tenantPo.getPath());
            UniversePo defaultUniverse = new UniversePo();
            defaultUniverse.setUniverseType(UniverseType.DEFAULT);
            defaultUniverse.setTenantId(tenantId);
            defaultUniverse.setTenantPath(tenantPo.getPath());
            defaultUniverse.setUniverseName(tenantPo.getName());
            defaultUniverse.setUniverseSystemName(universeSystemName);
            defaultUniverse.setUniverseThreshold(-1f);
            defaultUniverse.setUniverseCount(countUniverseBaseCountByUniverseSysName(tenantId, universeSystemName));
            defaultUniverse.setUniverseStatus(SegmentStatusType.UNIVERSE_SUCCESS);
            defaultUniverse.setCreatedBy("");
            defaultUniverse.setOwnerTenantPath(tenantPo.getPath());
            universePoJPA.save(defaultUniverse);
            HashMap paramMap = new HashMap<String, Object>();
            paramMap.put("tenantPath", tenantPo.getPath());
            paramMap.put("universeSysName", universeSystemName);
            paramMap.put("universeName", tenantPo.getName());
            paramMap.put("universeId", defaultUniverse.getId());
            paramMap.put("tenantId", tenantPo.getId());
            paramMap.put("ownerUniverseId", defaultUniverse.getId());
            paramMap.put("ownerTenantPath", tenantPo.getPath());
            bitmapAPI.createUniverse(JSONObject.toJSONString(paramMap));
            return universePoJPA.findAllByTenantId(tenantId);
        }
        List<String> universeIdList = getWorkingUniverseByTenantIdAndUsername(tenantId, username);
        UniversePo universePo = universePoJPA.findByTenantIdAndUniverseType(tenantId, UniverseType.DEFAULT);
        if (Optional.ofNullable(universePo).isPresent()) {
            if (null == universePo.getUniverseCount() || universePo.getUniverseCount() == 0) {
                universePo.setUniverseCount(countUniverseBaseCountByUniverseSysName(tenantId,
                        universePo.getUniverseSystemName()));
                universePo.setUpdateTime(new Date());
                universePoJPA.save(universePo);
            }
        }
        List<TenantVo> tenantVoList = tenantService.getAllTenantList();
        Map<String, Long> tenantMap = new HashMap<>();
        tenantVoList.forEach(tenantVo -> tenantMap.put(tenantVo.getPath(), tenantVo.getId()));
        List<TenantVo> ucTenantList = userCenterAPI.listUcTenant();
        Map<String, String> tenantNameMap = new HashMap<>();
        ucTenantList.forEach(tenantVo -> tenantNameMap.put(tenantVo.getTenantSysName(), tenantVo.getDisplayName()));
        for (UniversePo universePo1 : universePoList) {
            if (universeIdList.contains(String.valueOf(universePo1.getId()))) {
                universePo1.setLastUsedFlag(true);
            }
            universePo1.setIcon(getIconByTenantId(tenantMap.get(universePo1.getOwnerTenantPath())));
            universePo1.setOwnerTenantName(tenantNameMap.get(universePo1.getOwnerTenantPath()));
        }
        return universePoList;
    }

    private List<String> getWorkingUniverseByTenantIdAndUsername(Long tenantId, String username) {
        TenantAndUniverseKey tenantAndUniverseKey = new TenantAndUniverseKey();
        tenantAndUniverseKey.setTenantId(tenantId);
        tenantAndUniverseKey.setUsername(username);
        TenantAndUniversePo tenantAndUniversePo = tenantAndUniversePoJPA.findOne(tenantAndUniverseKey);
        if (Optional.ofNullable(tenantAndUniversePo).isPresent()) {
            String universeIds = tenantAndUniversePo.getUniverseIds();
            if (Optional.ofNullable(universeIds).isPresent()) {
                return Arrays.asList(universeIds.split(","));
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Taxonomy> listAllUniverseAttributeByRootIdAndKey(Long tenantId, String key, String rootId) throws
            AMSException {
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionPoService.getActiveVersionByTenant(treeTenantPo);
        List<Taxonomy> taxonomyList = taxonomyAPI.listAllUniverseAttributeByRootIdAndKey(String.valueOf(versionPo
                .getId()), rootId);
        return taxonomyList.stream().filter(taxonomy -> taxonomy.getName().toLowerCase().startsWith(key.toLowerCase()
        )).collect(Collectors.toList());
    }

    @Override
    public String getIconByTenantId(Long tenantId) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        TenantVo tenantVo = Optional.ofNullable(userCenterAPI.getTenantById(tenantPo.getTenantId()))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
        if (null == tenantVo.getIconId()) {
            return null;
        }
        Icon icon = userCenterAPI.getIconById(tenantVo.getIconId());
        if (null == icon) {
            return null;
        }
        return icon.getIconCode();
    }

    @Override
    public void updateUniverse(Long tenantId, Long universeId, UniverseForUpdateDTO universeForUpdateDTO) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<UniversePo> universePoList = new ArrayList<>();
        UniversePo universePo = getUniverseById(universeId);
        String universeName = universeForUpdateDTO.getUniverseName();
        if (!StringUtils.equalsIgnoreCase(universePo.getUniverseName(), universeName)) {
            if (Optional.ofNullable(universePoJPA.findByUniverseNameAndTenantPath(universeName, tenantPo.getPath())).isPresent()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0258,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0258));
            }
        }
        universePo.setUniverseCount(universeForUpdateDTO.getUniverseCount());
        universePo.setUniverseRuleJson(universeForUpdateDTO.getUniverseRuleJson());
        universePo.setUniverseName(universeForUpdateDTO.getUniverseName());
        universePo.setUniverseThreshold(universeForUpdateDTO.getUniverseThreshold());
        universePo.setSegmentThreshold(universeForUpdateDTO.getSegmentThreshold());
        universePoList.add(universePo);
        // get shared universe
        String sharedUniverseSysName =
                "shared_".concat(universePo.getTenantPath()).concat("_").concat(universePo.getUniverseSystemName());
        List<UniversePo> sharedUniversePoList =
                universePoJPA.findByUniverseSystemNameAndOwnerTenantPath(sharedUniverseSysName,
                        universePo.getTenantPath());
        if (!sharedUniversePoList.isEmpty()) {
            for (UniversePo sharedUniversePo : sharedUniversePoList) {
                sharedUniversePo.setUniverseCount(universeForUpdateDTO.getUniverseCount());
                sharedUniversePo.setUniverseRuleJson(universeForUpdateDTO.getUniverseRuleJson());
                sharedUniversePo.setUniverseName(universeForUpdateDTO.getUniverseName());
                sharedUniversePo.setUniverseThreshold(universeForUpdateDTO.getUniverseThreshold());
                sharedUniversePo.setSegmentThreshold(universeForUpdateDTO.getSegmentThreshold());
                universePoList.add(sharedUniversePo);
            }
        }
        HashMap paramMap = new HashMap<String, Object>();
        paramMap.put("tenantPath", universePo.getTenantPath());
        paramMap.put("universeSysName", universePo.getUniverseSystemName());
        paramMap.put("universeName", universeForUpdateDTO.getUniverseName());
        paramMap.put("universeId", universePo.getId());
        paramMap.put("campaign", JSONObject.parseObject(universeForUpdateDTO.getUniverseRuleJson()));
        paramMap.put("tenantId", universePo.getTenantId());
        paramMap.put("ownerUniverseId", universeId);
        paramMap.put("ownerTenantPath", universePo.getTenantPath());
        String resp = bitmapAPI.createUniverse(JSONObject.toJSONString(paramMap));
        JSONObject respObj = JSONObject.parseObject(resp);
        Boolean finishFlag;
        try {
            finishFlag = respObj.getJSONObject("data").getBoolean("finishFlag");
        } catch (NullPointerException e) {
            throw new AMSInvalidInputException();
        }
        for (UniversePo universe : universePoList) {
            if (!finishFlag && !SegmentStatusType.UNIVERSE_PROCESSING.equals(universe.getUniverseStatus())) {
                universe.setUniverseStatus(SegmentStatusType.UNIVERSE_UPDATING);
            }
        }
        // refresh universe status
        if (!finishFlag) {
            try {
                dataSourceAPI.updateUniverseStatusByUniverseSysName(tenantPo.getTenantId(),
                        universePo.getUniverseSystemName());
            } catch (Exception e) {
                LogUtils.error("Failed to refresh file track's universe status");
            }
        }
        universePoJPA.save(universePoList);
        for (UniversePo universe : universePoList) {
            Long audienceThreshold =
                    Float.valueOf(universe.getUniverseCount() * universe.getUniverseThreshold()).longValue();
            Long segmentThreshold = universe.getSegmentThreshold();
            List<AudiencePo> audiencePoList = audiencePoJPA.findAllByUniverseIdsLike("%" + universe.getId() + "%");
            for (AudiencePo audiencePo : audiencePoList) {
                if (audiencePo.getUniverseSegmentCountJson() == null) {
                    continue;
                }
                JSONArray list = JSON.parseArray(audiencePo.getUniverseSegmentCountJson());
                audiencePo.setLegalFlag(true);
                for (Object obj : list) {
                    if (((JSONObject) obj).getLong("universeId") != universe.getId()) {
                        continue;
                    }
                    if (((JSONObject) obj).getLong("audienceCountByUniverse") < audienceThreshold) {
                        audiencePo.setLegalFlag(false);
                        break;
                    }
                    JSONArray segmentCounts = ((JSONObject) obj).getJSONArray("segmentCounts");
                    for (Object count : segmentCounts) {
                        if (Long.valueOf((Integer) count) < segmentThreshold) {
                            audiencePo.setLegalFlag(false);
                            break;
                        }
                    }
                }
            }
            audiencePoJPA.save(audiencePoList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUniverseIntegration(UniverseIntegrationDTO universeIntegrationDTO) throws AMSInvalidInputException {
        UniverseIntegrationPo universeIntegrationPo =
                universeIntegrationPoJPA.findByUniverseId(universeIntegrationDTO.getUniverseId());
        Boolean updateFlag = true;
        if (!Optional.ofNullable(universeIntegrationPo).isPresent()) {
            universeIntegrationPo = new UniverseIntegrationPo();
            updateFlag = false;
        }
        universeIntegrationPo.setLrAudienceId(universeIntegrationDTO.getLrAudienceId());
        universeIntegrationPo.setDropOffPoint(universeIntegrationDTO.getDropOffPoint());
        universeIntegrationPo.setUniverseId(universeIntegrationDTO.getUniverseId());
        universeIntegrationPo.setOnboardDestinationId(universeIntegrationDTO.getOnboardDestinationId());
        universeIntegrationPo.setOnboardIntegrationId(universeIntegrationDTO.getOnboardIntegrationId());
        universeIntegrationPo.setDataStoreIntegrationId(universeIntegrationDTO.getDataStoreIntegrationId());
        universeIntegrationPo.setDataStoreDestinationId(universeIntegrationDTO.getDataStoreDestinationId());
        universeIntegrationPoJPA.save(universeIntegrationPo);
        if (updateFlag) {
            UniversePo sourceUniversePo = getUniverseById(universeIntegrationDTO.getUniverseId());
            String sharedUniverseSysName =
                    "shared_".concat(sourceUniversePo.getTenantPath()).concat("_").concat(sourceUniversePo.getUniverseSystemName());
            List<UniversePo> sharedUniversePoList =
                    universePoJPA.findByUniverseSystemNameAndOwnerTenantPath(sharedUniverseSysName,
                            sourceUniversePo.getTenantPath());
            List<UniverseIntegrationPo> universeIntegrationPoList = new ArrayList<>();
            for (UniversePo shareUniversePo : sharedUniversePoList) {
                UniverseIntegrationPo sharedUniverseIntegrationPo = getByUniverseId(shareUniversePo.getId());
                sharedUniverseIntegrationPo.setLrAudienceId(universeIntegrationDTO.getLrAudienceId());
                sharedUniverseIntegrationPo.setDropOffPoint(universeIntegrationDTO.getDropOffPoint());
                sharedUniverseIntegrationPo.setOnboardDestinationId(universeIntegrationDTO.getOnboardDestinationId());
                sharedUniverseIntegrationPo.setOnboardIntegrationId(universeIntegrationDTO.getOnboardIntegrationId());
                sharedUniverseIntegrationPo.setDataStoreIntegrationId(universeIntegrationDTO.getDataStoreIntegrationId());
                sharedUniverseIntegrationPo.setDataStoreDestinationId(universeIntegrationDTO.getDataStoreDestinationId());
                universeIntegrationPoList.add(sharedUniverseIntegrationPo);
            }
            universeIntegrationPoJPA.save(universeIntegrationPoList);
        }
    }

    @Override
    public UniverseIntegrationPo getUniverseIntegrationByUniverseId(Long universeId) {
        return universeIntegrationPoJPA.findByUniverseId(universeId);
    }

    @Override
    public List<WhiteTenant> getWhiteListByTenantId(Long tenantId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        try {
            return userCenterAPI.getWhiteListByTenantId(tenantPo.getTenantId());
        } catch (AMSRMIException e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSharedUniverse(Long universeId, String targetTenantId) throws AMSInvalidInputException,
            AMSRMIException {
        TenantVo tenantVo = tenantService.getTenantByTenantId(targetTenantId);
        UniversePo universePo = getUniverseById(universeId);
        String sharedUniverseSysName =
                "shared_".concat(universePo.getTenantPath()).concat("_").concat(universePo.getUniverseSystemName());
        UniversePo comparedUniverse =
                universePoJPA.findByUniverseSystemNameAndTenantPathAndOwnerTenantPath(sharedUniverseSysName,
                        tenantVo.getPath(), universePo.getTenantPath());
        if (Optional.ofNullable(comparedUniverse).isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0258,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0258));
        }
        UniversePo sharedUniverse = new UniversePo();
        sharedUniverse.setTenantId(tenantVo.getId());
        sharedUniverse.setOwnerTenantPath(universePo.getTenantPath());
        sharedUniverse.setTenantPath(tenantVo.getPath());
        sharedUniverse.setUniverseCount(universePo.getUniverseCount());
        sharedUniverse.setUniverseRuleJson(universePo.getUniverseRuleJson());
        sharedUniverse.setCreatedBy(universePo.getCreatedBy());
        sharedUniverse.setUniverseName(universePo.getUniverseName());
        sharedUniverse.setUniverseSystemName("shared_".concat(universePo.getTenantPath()).concat("_").concat(universePo.getUniverseSystemName()));
        sharedUniverse.setUniverseType(UniverseType.SHARED);
        sharedUniverse.setUniverseThreshold(universePo.getUniverseThreshold());
        sharedUniverse.setUniverseStatus(SegmentStatusType.UNIVERSE_PROCESSING);
        universePoJPA.save(sharedUniverse);
        // create target universe integration by source universe integration
        UniverseIntegrationPo universeIntegrationPo = universeIntegrationPoJPA.findByUniverseId(universeId);
        if (!Optional.ofNullable(universeIntegrationPo).isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0259,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0259));
        }
        UniverseIntegrationPo sharedUniverseIntegrationPo = new UniverseIntegrationPo();
        sharedUniverseIntegrationPo.setUniverseId(sharedUniverse.getId());
        sharedUniverseIntegrationPo.setDropOffPoint(universeIntegrationPo.getDropOffPoint());
        sharedUniverseIntegrationPo.setLrAudienceId(universeIntegrationPo.getLrAudienceId());
        sharedUniverseIntegrationPo.setOnboardDestinationId(universeIntegrationPo.getOnboardDestinationId());
        sharedUniverseIntegrationPo.setOnboardIntegrationId(universeIntegrationPo.getOnboardIntegrationId());
        sharedUniverseIntegrationPo.setDataStoreDestinationId(universeIntegrationPo.getDataStoreDestinationId());
        sharedUniverseIntegrationPo.setDataStoreIntegrationId(universeIntegrationPo.getDataStoreIntegrationId());
        sharedUniverseIntegrationPo.setCreatedBy(universeIntegrationPo.getCreatedBy());
        universeIntegrationPoJPA.save(sharedUniverseIntegrationPo);
        // call bitmap-api to create shared universe
        HashMap paramMap = new HashMap<String, Object>();
        paramMap.put("tenantPath", tenantVo.getPath());
        paramMap.put("universeSysName", sharedUniverse.getUniverseSystemName());
        paramMap.put("universeName", sharedUniverse.getUniverseName());
        paramMap.put("universeId", sharedUniverse.getId());
        paramMap.put("campaign", JSONObject.parseObject(sharedUniverse.getUniverseRuleJson()));
        paramMap.put("tenantId", tenantVo.getId());
        paramMap.put("ownerUniverseId", universeId);
        paramMap.put("ownerTenantPath", universePo.getTenantPath());
        bitmapAPI.createUniverse(JSONObject.toJSONString(paramMap));
        return sharedUniverse.getId();
    }

    @Override
    public void callbackSharedUniverseStatus(String tenantPath, String universeSystemName, String universeJobId) throws AMSInvalidInputException {
        UniversePo universePo = getUniverseBySystemNameAndTenantPath(universeSystemName, tenantPath);
        if (!Optional.ofNullable(universePo).isPresent()) {
            return;
        }
        if (!Optional.ofNullable(universePo.getUniverseJobId()).isPresent()
                && universeJobId.compareTo(universePo.getUniverseJobId()) < 0) {
            return;
        }
        universePo.setUniverseStatus(SegmentStatusType.SHARE_COMPLETED);
        universePoJPA.save(universePo);
    }

    private Long countUniverseBaseCountByUniverseSysName(Long tenantId, String UniverseSysName) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        String path = tenantPo.getPath();
        String resp = bitmapAPI.countUniverseBaseCountByUniverseSysName(path, UniverseSysName);
        JSONObject respObject = JSON.parseObject(resp);
        return respObject.getLong("data");
    }

    private UniverseIntegrationPo getByUniverseId(Long universeId) throws AMSInvalidInputException {
        UniverseIntegrationPo sharedUniverseIntegrationPo = universeIntegrationPoJPA.findByUniverseId(universeId);
        if (!Optional.ofNullable(sharedUniverseIntegrationPo).isPresent()) {
            throw new AMSInvalidInputException("020260", errorMessageSourceHandler.getMessage("020260"));
        }
        return sharedUniverseIntegrationPo;
    }

    @Override
    public List<TenantVo> getShareUniverseTenantInfoByTenantId(Long tenantId) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<UniversePo> universePoList = universePoJPA.findAllByAndOwnerTenantPath(tenantPo.getPath());
        Map<String, Long> map = new HashMap<>(16);
        for (UniversePo universePo : universePoList) {
            if (map.get(universePo.getTenantPath()) == null && !tenantId.equals(universePo.getTenantId())) {
                map.put(universePo.getTenantPath(), universePo.getTenantId());
            }
        }
        List list = tenantPoJPA.findAll(map.values());
        return tenantVoMapper.map(list);
    }

    @Override
    public UniversePo getIconByUniverseSysNameAndTenantId(String universeSysName, String tenantId) throws AMSException {
        TenantVo tenantVo = tenantService.getTenantByTenantId(tenantId);
        UniversePo universePo = universePoJPA.findByUniverseSystemNameAndTenantPath(universeSysName,
                tenantVo.getPath());
        TenantPo tenantPo = tenantPoJPA.findFirstByPath(universePo.getOwnerTenantPath());
        universePo.setIcon(getIconByTenantId(tenantPo.getId()));
        return universePo;
    }
}
