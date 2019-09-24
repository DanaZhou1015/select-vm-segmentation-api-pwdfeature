package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.api.ServiceAPI.TaxonomyAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.dto.VersionDTO;
import com.acxiom.ams.model.dto.v2.VersionDTOCreate;
import com.acxiom.ams.model.dto.v2.VersionDTOUpdateTaxonomy;
import com.acxiom.ams.model.dto.v2.VersionDatasourceDTO;
import com.acxiom.ams.model.dto.v2.VersionDtoDelete;
import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.NodeCountAndDepthVO;
import com.acxiom.ams.model.vo.ShareVO;
import com.acxiom.ams.model.vo.VersionPageVo;
import com.acxiom.ams.model.vo.VersionVo;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.VersionPoService;

import java.util.*;

import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.acxiom.ams.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
@Service
@Transactional
public class VersionPoServiceImpl implements VersionPoService {

    @Autowired
    VersionPoJPA versionPoJPA;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    VersionPoMapper versionPoMapper;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    TaxonomyAPI taxonomyAPI;
    @Autowired
    TenantService tenantService;
    @Autowired
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Autowired
    ServiceAPI.DataSourceAPI dataSourceAPI;

    @Override
    public long saveVersion(Long tenantId, VersionDTO versionDTO) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionPoJPA
                .findFirstByTenantPoAndNameAndCreatedBy(tenantPo, versionDTO.getName(),
                        versionDTO.getUserId());
        if (null != versionPo) {
            return -1;
        }
        versionPo = new VersionPo();
        versionPo.setName(versionDTO.getName());
        versionPo.setTreeId(versionDTO.getTreeId());
        versionPo.setCreatedBy(versionDTO.getUserName());
        versionPo.setCreatedTime(new Date());
        versionPo.setTenantPo(tenantPo);
        versionPoJPA.save(versionPo);
        return versionPo.getId();
    }

    @Override
    public VersionPo getVersionById(Long versionId) throws AMSInvalidInputException {
        return Optional
                .ofNullable(versionPoJPA.findById(versionId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0239,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0239)));
    }

    public VersionPageVo findVersionByTenantIdAndPage(Long tenantId, Integer page, Integer pageSize)
            throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        Pageable pageable = new PageRequest(page, pageSize,
                new Sort(Sort.Direction.DESC, "updateTime"));
        Specification<VersionPo> spec = new Specification<VersionPo>() {
            @Override
            public Predicate toPredicate(Root<VersionPo> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate tenantPoPredicate = criteriaBuilder
                        .equal(root.get("tenantPo").as(TenantPo.class), tenantPo);
                return criteriaQuery.where(tenantPoPredicate).getRestriction();
            }
        };
        Page<VersionPo> array = versionPoJPA.findAll(spec, pageable);
        List<VersionVo> versionVoList = versionPoMapper.map(array.getContent());
        VersionPageVo versionPageVo = new VersionPageVo();
        versionPageVo.setPageList(versionVoList);
        versionPageVo.setTotal(array.getTotalElements());
        return versionPageVo;
    }


    public VersionPo findVersionById(Long tenantId, Long versionId)
            throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionPoJPA.findById(versionId);
        return versionPo;
    }

    @Override
    public void setVersionFlagActive(Long tenantId, Long versionId)
            throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        versionPoJPA.updateOperationFlagByTenantId(TemplateStatusType.READY, tenantId);
        versionPoJPA.updateOperationFlagById(TemplateStatusType.ACTIVE, versionId);
    }

    public void updateVersionTreeId(Long tenantId, Long versionId, String treeId)
            throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        versionPoJPA.updateTreeIdById(treeId, versionId);
    }

    public void deleteVersionById(Long tenantId, Long versionId) throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        VersionPo versionPo = getVersionById(versionId);
        if (versionPo.getTenantPo().getId() != tenantId) {
            throw new AMSInvalidInputException();
        }
        versionPoJPA.delete(versionId);
    }


    //add for new version table
    @Override
    public List<VersionVo> findByTenant(Long tenantId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoOrderByUpdateTimeDesc(tenantPo);
        return versionPoMapper.map(versionPoList);
    }

    @Override
    public VersionVo findByTenantAndId(Long tenantId, Long versionId)
            throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionPoJPA.findById(versionId);
        return versionPoMapper.map(versionPo);
    }

    @Override
    public Long createVersion(VersionDTOCreate versionDTOCreate, Long tenantId)
            throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = new VersionPo();
        List<VersionPo> versionPoName = versionPoJPA
                .findByTenantPoAndName(tenantPo, versionDTOCreate.getName());
        if (versionPoName.size() != 0) {
            if (!Optional.ofNullable(versionPoName.get(0).getTreeId()).isPresent()
                    || StringUtils.equals(versionPoName.get(0).getTreeId(), "")) {
                return versionPoName.get(0).getId();
            }
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0243,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0243));
        }
        versionPo.setName(versionDTOCreate.getName());
        versionPo.setTenantPo(tenantPo);
        versionPo.setCreatedBy(versionDTOCreate.getUserName());
        versionPo.setCreatedTime(new Date());
        versionPoJPA.save(versionPo);
        return versionPo.getId();
    }

    @Override
    public void updateVersion(VersionDTOUpdateTaxonomy versionDTOUpdateTaxonomy, Long tenantId)
            throws AMSException {
        VersionPo versionPo = getVersionById(versionDTOUpdateTaxonomy.getVersionId());
        if (versionPo.getTenantPo().getId() != tenantId) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0242,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0242));
        }
        if (!StringUtils.equals(versionPo.getName(), versionDTOUpdateTaxonomy.getName()) && Optional
                .ofNullable(versionDTOUpdateTaxonomy.getName()).isPresent()) {
            List<VersionPo> versionPoList = versionPoJPA
                    .findByTenantPoAndName(versionPo.getTenantPo(), versionDTOUpdateTaxonomy.getName());
            if (Optional.ofNullable(versionPoList).isPresent() && !versionPoList.isEmpty()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0243,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0243));
            }
        }
        NodeCountAndDepthVO nodeCountAndDepthVO = taxonomyAPI.countMaxDepthAndNodeCount(
                String.valueOf(versionPo.getId()));
        Optional.ofNullable(versionDTOUpdateTaxonomy.getTreeId())
                .ifPresent(value -> versionPo.setTreeId(value));
        Optional.ofNullable(versionDTOUpdateTaxonomy.getName())
                .ifPresent(value -> versionPo.setName(value));
        Optional.ofNullable(versionDTOUpdateTaxonomy.getActiveFlag())
                .ifPresent(value -> {
                    switch (value) {
                        case 0:
                            versionPo.setOperationFlag(TemplateStatusType.READY);
                            break;
                        case 1:
                            versionPo.setOperationFlag(TemplateStatusType.ACTIVE);
                            break;
                        case 2:
                            versionPo.setOperationFlag(TemplateStatusType.DRAFT);
                            break;
                    }
                });
        Optional.ofNullable(versionDTOUpdateTaxonomy.getDatasourceId())
                .ifPresent(value -> versionPo.setDatasourceId(value));
        versionPo.setSyncFlag(0);
        versionPo.setUpdateTime(new Date());
        versionPo.setMaxDepth(nodeCountAndDepthVO.getMaxDepth());
        versionPo.setNodeNumber(nodeCountAndDepthVO.getNodeCount());
        versionPoJPA.save(versionPo);
        // refresh shared taxonomy
        try {
            List<ShareVO> shareVOList = userCenterAPI.listShareBySourceTenantId(versionPo.getTenantPo().getTenantId());
            if (Optional.ofNullable(shareVOList).isPresent() && !shareVOList.isEmpty()) {
                Set<String> targetTenantList = new HashSet<>();
                shareVOList.forEach(shareVO -> targetTenantList.add(shareVO.getRecipientTenantId()));
                try {
                    dataSourceAPI.refreshSharedDataSourceMappingByTaxonomyName(String.valueOf(versionPo.getId()),
                            new ArrayList<>(targetTenantList));
                }catch (Exception e){
                    LogUtils.error(e);
                    LogUtils.error("Failed to refresh shared dataSource mapping file, source tenant id is : " + versionPo.getTenantPo().getTenantId());
                }
            }
        } catch (Exception e){
            LogUtils.error(e);
        }
    }

    @Override
    public void updateVersionFlag(Long tenantId, Long versionId, Integer activeFlag)
            throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = getVersionById(versionId);
        switch (activeFlag) {
            case 0: {
                versionPo.setOperationFlag(TemplateStatusType.READY);
                break;
            }
            case 1: {
                Optional.ofNullable(versionPo.getTreeId()).orElseThrow(
                        () -> new AMSInvalidInputException(Constant.ERROR_CODE_0248,
                                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0248)));
                List<VersionPo> versionPoList = versionPoJPA
                        .findByTenantPoAndOperationFlagIn(tenantPo,
                                new TemplateStatusType[]{TemplateStatusType.ACTIVE,
                                        TemplateStatusType.READY});
                versionPoList
                        .forEach(version -> version.setOperationFlag(TemplateStatusType.READY));
                versionPoJPA.save(versionPoList);
                versionPo.setOperationFlag(TemplateStatusType.ACTIVE);
                break;
            }
            case 2: {
                versionPo.setOperationFlag(TemplateStatusType.DRAFT);
                break;
            }
        }
        versionPoJPA.save(versionPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersionByIdList(Long tenantId, VersionDtoDelete versionDtoDelete)
            throws AMSInvalidInputException, AMSRMIException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<VersionPo> deleteVersionList = versionPoJPA
                .findAllByTenantPoAndAndIdIn(tenantPo, versionDtoDelete.getIdList());
        Integer deleteCount = versionDtoDelete.getIdList().size();
        if (deleteVersionList.size() < deleteCount) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0246,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0246));
        }
        Integer operationFlagCount = (int) deleteVersionList.stream().filter(versionPo ->
                versionPo.getOperationFlag() == TemplateStatusType.ACTIVE
        ).count();
        if (operationFlagCount != 0) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0247,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0247));
        }
        Integer createdByCount = (int) deleteVersionList.stream().filter(versionPo ->
                !StringUtils.equals(versionPo.getCreatedBy(), versionDtoDelete.getCreatedBy())
        ).count();
        if (createdByCount != 0) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0249,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0249));
        }
        List<Long> versionIdList = new ArrayList<>();
        deleteVersionList.forEach(versionPo -> versionIdList.add(versionPo.getId()));
        versionPoJPA.delete(deleteVersionList);
        taxonomyAPI.deleteVersionByIdList(versionIdList);
    }

    @Override
    public void migrationTaxonomyTree() throws AMSRMIException {
        List<VersionPo> versionPoList = versionPoJPA.findAll();
        List<String> versionIdList = new ArrayList<>();
        versionPoList.stream().filter(versionPo -> {
            if (StringUtils.isNotBlank(versionPo.getTreeId())) {
                versionIdList.add(String.valueOf(versionPo.getId()));
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        taxonomyAPI.migrationTaxonomyTree(versionIdList);
    }

    @Override
    public void updateSyncFlagById(Integer flag, Long versionId) {
        versionPoJPA.updateSyncFlagById(flag, versionId);
    }

    @Override
    public Long duplicateVersionById(Long versionId, String username) throws AMSInvalidInputException {
        VersionPo versionPo = getVersionById(versionId);
        VersionPo duplicateVersionPo = new VersionPo();
        String treeName = versionPo.getName().concat(Constant.UNDER_LINE).concat(String.valueOf(System
                .currentTimeMillis())).concat(Constant.COPY_FIX);
        duplicateVersionPo.setName(treeName);
        if (TemplateStatusType.DRAFT.equals(versionPo.getOperationFlag())) {
            duplicateVersionPo.setOperationFlag(TemplateStatusType.DRAFT);
        } else {
            duplicateVersionPo.setOperationFlag(TemplateStatusType.READY);
        }
        duplicateVersionPo.setTreeId(versionPo.getTreeId());
        duplicateVersionPo.setMaxDepth(versionPo.getMaxDepth());
        duplicateVersionPo.setTenantPo(versionPo.getTenantPo());
        duplicateVersionPo.setSyncFlag(versionPo.getSyncFlag());
        duplicateVersionPo.setNodeNumber(versionPo.getNodeNumber());
        duplicateVersionPo.setCreatedBy(username);
        duplicateVersionPo.setCreatedTime(new Date());
        duplicateVersionPo.setUpdateTime(new Date());
        duplicateVersionPo.setDatasourceId(versionPo.getDatasourceId());
        versionPoJPA.save(duplicateVersionPo);
        return duplicateVersionPo.getId();
    }

    @Override
    public List<VersionVo> listVersionByDatasourceId(Integer datasourceId) {
        List<VersionPo> versionPoList = versionPoJPA.findByDatasourceIdNotNull();
        versionPoList = versionPoList.stream().filter(versionPo -> {
            List<String> datasourceIdList = Arrays.asList(versionPo.getDatasourceId().split(Constant.COMMA));
            if (datasourceIdList.contains(String.valueOf(datasourceId))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return versionPoMapper.map(versionPoList);
    }

    @Override
    public String listTenantPathByIds(Long versionId) throws AMSInvalidInputException {
        VersionPo versionPo = getVersionById(versionId);
        return versionPo.getTenantPo().getPath();
    }

    @Override
    public VersionPo getActiveVersionByTenant(TenantPo tenantPo) throws AMSInvalidInputException {
        return Optional.ofNullable(versionPoJPA
                .findFirstByTenantPoAndOperationFlag(tenantPo, TemplateStatusType.ACTIVE))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0202,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0202)));
    }

    @Override
    public List<VersionDatasourceDTO> findVersionIdsByDatasourceIds(List<String> dataSourceIds) {
        List<VersionDatasourceDTO> ret = new ArrayList<>();
        List<VersionPo> versionVoList = versionPoJPA.findAll();
        for (VersionPo versionPo : versionVoList) {
            String versionDSIds = versionPo.getDatasourceId();
            if (StringUtils.isNotBlank(versionDSIds)) {
                Set<String> dsSet = new HashSet<>(Arrays.asList(versionDSIds.split(",")));
                dsSet.retainAll(new HashSet<>(dataSourceIds));
                if (dsSet.isEmpty()) {
                    continue;
                }
                ret.add(new VersionDatasourceDTO(
                        String.valueOf(versionPo.getId()),
                        new ArrayList<>(dsSet)));
                // update version table
                Set<String> newDsSet = new HashSet<>(Arrays.asList(versionDSIds.split(",")));
                newDsSet.removeAll(dsSet);
                String newDatasourceId = newDsSet.isEmpty() ? null : String.join(",", newDsSet);
                versionPoJPA.updateDatasourceIdById(newDatasourceId, versionPo.getId());
            }
        }

        return ret;
    }
}
