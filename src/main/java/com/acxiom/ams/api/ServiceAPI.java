package com.acxiom.ams.api;

import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.util.AWSV4Auth;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

/**
 * Created by cldong on 12/5/2017.
 */
@Service
public class ServiceAPI {

    private static final String BITMAP_API_RESPONSE = "Bitmap api(%s) response : %s";
    private static final String LOOKALIKE_API_RESPONSE = "Lookalike api(%s) response : %s";
    private static final String DATASOURCE_API_RESPONSE = "Datasource api(%s) response : %s";
    private static final String ERROR = "error";

    private ServiceAPI() {
    }

    @Service
    public static class TaxonomyAPI extends API {

        public List<Taxonomy> getTaxonomyList(String taxonomyName, String nodeId)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_LIST_BY_NODE, taxonomyName, nodeId, true);
            ResponseEntity<List<Taxonomy>> info = retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path,
                    new ParameterizedTypeReference<List<Taxonomy>>() {
                    });
            return info.getBody();
        }

        public List<Taxonomy> getTaxonomyListByNode(String taxonomyName, String nodeId)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_LIST_BY_CURRENT_NODE, taxonomyName, nodeId, true);
            ResponseEntity<List<Taxonomy>> info = retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path,
                    new ParameterizedTypeReference<List<Taxonomy>>() {
                    });
            return info.getBody();
        }

        public List<Taxonomy> getTaxonomyList(String taxonomyName, String[] keys)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_LIST_BY_KEY, taxonomyName, true);
            Taxonomy[] info = retryRestTemplate.post(Constant.TAXONOMY_SERVER_SERVICE, path, keys, Taxonomy[].class);
            return Arrays.asList(info);
        }

        public List<Taxonomy> getTaxonomyListByNodeIdAndName(String taxonomyName,
                                                             List<String> nodeIds)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_LIST_BY_NODE_ID, taxonomyName, true);
            Taxonomy[] info = retryRestTemplate.post(Constant.TAXONOMY_SERVER_SERVICE, path, nodeIds, Taxonomy[].class);
            return Arrays.asList(info);
        }


        public List<Taxonomy> getTaxonomyListByChildrenId(String taxonomyName,
                                                          String[] objectIdArray)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_LIST_BY_CHILDREN, taxonomyName, true);
            Taxonomy[] info = retryRestTemplate
                    .post(Constant.TAXONOMY_SERVER_SERVICE, path, objectIdArray, Taxonomy[].class);
            return Arrays.asList(info);
        }

        public Map<String, String> getParentPath(String taxonomyName, String[] nodeId)
                throws AMSRMIException {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : nodeId) {
                stringBuilder.append(str);
                stringBuilder.append(Constant.COMMA);
            }
            Integer size = stringBuilder.length();
            String nodeIdStr = stringBuilder.toString().substring(0, size - 1);
            String path = String
                    .format(Constant.GET_PARENT_PATH_BY_CHILDREN, taxonomyName, nodeIdStr, true);
            ResponseEntity<Map<String, String>> info = retryRestTemplate
                    .get(Constant.TAXONOMY_SERVER_SERVICE, path,
                            new ParameterizedTypeReference<Map<String, String>>() {
                            });
            return info.getBody();
        }

        public List<Taxonomy> getTaxonomyAttributeByKey(String taxonomyName,
                                                        String key)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_ATTRIBUTE_BY_KEY, taxonomyName, true);
            Taxonomy[] info = retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path, Taxonomy[].class, key);
            return Arrays.asList(info);
        }

        public List<String> getAttributeOwnerByNodeIdAndName(String taxonomyName,
                                                             List<NodeDTO> nodeDTOList)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_ATTRIBUTE_OWNER, taxonomyName, true);
            String[] info = retryRestTemplate.post(Constant.TAXONOMY_SERVER_SERVICE, path, nodeDTOList, String[].class);
            return Arrays.asList(info);
        }


        public List<OwnerAndDataType> getAttributeOwnerAndDataTypeByNodeIdAndName(String taxonomyName,
                                                                                  List<NodeDTO> nodeDTOList)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_ATTRIBUTE_OWNER_DATATYPE, taxonomyName, true);
            return retryRestTemplate
                    .post(Constant.TAXONOMY_SERVER_SERVICE, path, nodeDTOList, List.class);
        }

        public String getTaxonomyAllAttributeByName(String taxonomyName)
                throws AMSRMIException {
            String path = String.format(Constant.GET_TAXONOMY_ALL_ATTRIBUTE, taxonomyName);
            return retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path, String.class);
        }

        public void migrationTaxonomyTree(List<String> versionIdList) throws AMSRMIException {
            String path = String.format(Constant.MIGRATION_TAXONOMY_TREE);
            retryRestTemplate.put(Constant.TAXONOMY_SERVER_SERVICE, path, versionIdList, String.class);
        }

        public NodeCountAndDepthVO countMaxDepthAndNodeCount(String versionId)
                throws AMSRMIException {
            String path = String.format(Constant.COUNT_DEPTH_AND_NODE, versionId);
            return retryRestTemplate
                    .get(Constant.TAXONOMY_SERVER_SERVICE, path, NodeCountAndDepthVO.class);
        }

        public List<PriceAndOwnerVO> listPriceAndOwnerByTaxonomyId(String versionId, List<String> taxonomyIdList)
                throws AMSRMIException {
            String path = String.format(Constant.LIST_PRICE_AND_OWNER_BY_TAXONOMY_ID, versionId);
            PriceAndOwnerVO[] priceAndOwnerVOS = retryRestTemplate
                    .post(Constant.TAXONOMY_SERVER_SERVICE, path, taxonomyIdList, PriceAndOwnerVO[].class);
            return Arrays.asList(priceAndOwnerVOS);
        }

        public List<DataTypeAndPriceAndOwnerVO> listDataTypeAndPriceAndOwnerByTaxonomyIdList(String versionId,
                                                                                             List<String>
                                                                                                     taxonomyIdList)
                throws AMSRMIException {
            String path = String.format(Constant.LIST_DATATYPE_AND_PRICE_AND_OWNER_BY_TAXONOMY_ID, versionId);
            DataTypeAndPriceAndOwnerVO[] dataTypeAndPriceAndOwnerVOS = retryRestTemplate
                    .post(Constant.TAXONOMY_SERVER_SERVICE, path, taxonomyIdList, DataTypeAndPriceAndOwnerVO[].class);
            return Arrays.asList(dataTypeAndPriceAndOwnerVOS);
        }

        public List<Taxonomy> getTaxonomyEndTypeItemWithinLimit(String taxonomyName, Integer pageNo, Integer
                pageSize) throws AMSRMIException {
            String path = String.format(Constant.GET_ENDTYPE_TAXONOMY_BY_COND, taxonomyName, pageNo, pageSize);
            ResponseEntity<List<Taxonomy>> info = retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path,
                    new ParameterizedTypeReference<List<Taxonomy>>() {
                    });
            return info.getBody();
        }

        public List<Taxonomy> listAllUniverseAttributeByRootIdAndKey(String taxonomyName, String rootId)
                throws AMSRMIException {
            String path = String.format(Constant.LIST_UNIVERSE_ATTRIBUTE_BY_KEY, taxonomyName, rootId);
            Taxonomy[] info = retryRestTemplate.get(Constant.TAXONOMY_SERVER_SERVICE, path, Taxonomy[].class);
            return Arrays.asList(info);
        }

        public void deleteVersionByIdList(List<Long> versionIdList)
                throws AMSRMIException {
            retryRestTemplate.put(Constant.TAXONOMY_SERVER_SERVICE, Constant.DELETE_VERSION_BY_ID_LIST, versionIdList);
        }
    }

    @Service
    public static class AiAPI extends API {

        public String[] query(String key) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("aiuser", Constant.AI_USER);
            headers.add("aitoken", Constant.AI_TOKEN);
            headers.add("Content-Type", "application/json; charset=UTF-8");
            Map<String, String> map = new HashMap<>();
            map.put("query", key);
            HttpEntity formEntity = new HttpEntity(JSONObject.toJSONString(map), headers);
            JSONObject resultJson;
            try {
                resultJson = retryRestTemplate
                        .post(Constant.AI_SERVICE, Constant.AI_PARTICIPLE, formEntity,
                                JSONObject.class);
                LogUtils.debug("======> ai api response : " + resultJson.toJSONString());
            } catch (AMSRMIException e) {
                LogUtils.error(e);
                return new String[]{};
            }
            return resultJson.getJSONArray("result").toArray(new String[]{});
        }
    }

    @Service
    public static class BitmapAPI extends API {

        @Autowired
        ErrorMessageSourceHandler errorMessageSourceHandler;

        public String calculate(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_CALCULATE_V6, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_CALCULATE_V6, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String calculateV2(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_CALCULATE_V5, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_CALCULATE_V5, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String calculateForNonTV(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_CALCULATE_V7, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_CALCULATE_V7, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String getCampaignInfoV2(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.GET_CAMPAIGN_INFO, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.GET_CAMPAIGN_INFO, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String createBitmapForTV(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_CREATE_BITMAP_V5, reqParams,
                            String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_CREATE_BITMAP_V5, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String createBitmapV7(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_CREATE_BITMAP_V7, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_CREATE_BITMAP_V7, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String profiling(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_PROFILING_V6, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_PROFILING_V6, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String fillInsight(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_PROFILING_V5, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_PROFILING_V5, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String profilingV7(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_PROFILING_V7, reqParams, String.class);
            LogUtils.debug(String.format(BITMAP_API_RESPONSE, Constant.POST_PROFILING_V7, resp));
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String distributeV7(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_DISTRIBUTE_V7, reqParams, String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String distributeCampaign(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_DISTRIBUTE_V5, reqParams, String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        public Map<String, Long> listNodeCountByTaxonomyIds(TenantPo tenantPo, List<String> taxonomyIdList) throws AMSRMIException {
            String path = String.format(Constant.LIST_COUNT_BY_TAXONOMY_ID_VM, tenantPo.getPath(), StringUtils.join(taxonomyIdList, Constant.COMMA));
            String resp = retryRestTemplate.get(Constant.BITMAP_SERVER_SERVICE, path, String.class);
            bitmapExceptionHandle(resp);
            JSONObject object = JSONObject.parseObject(resp);
            Map<String, Long> map = new HashMap<>();
            try {
                JSONObject obj = object.getJSONObject("data");
                Set<String> keys = obj.keySet();
                for (String key : keys) {
                    map.put(key, obj.getLongValue(key));
                }
            } catch (NullPointerException | JSONException e) {
                throw new AMSRMIException(Constant.ERROR_CODE_0702,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0702));
            }
            return map;
        }

        public String createUniverse(String reqParams) throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.CREATE_UNIVERSE, reqParams,
                            String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String calculateUniverse(String reqParams)
                throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.UNIVERSE_CALCULATE, reqParams, String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String createAdvanceLookalike(String reqParams) throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.ADVANCE_CREATE_LOOKALIKE_SEED, reqParams,
                            String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        public String advanceLookalikeToBitmap(String reqParams) throws AMSRMIException {
            String resp = retryRestTemplate
                    .post(Constant.BITMAP_SERVER_SERVICE, Constant.ADVANCE_CREATE_LOOKALIKE_TO_BITMAP, reqParams,
                            String.class);
            bitmapExceptionHandle(resp);
            return resp;
        }

        private void bitmapExceptionHandle(String resp) throws AMSRMIException {
            if (null == resp) {
                throw new AMSRMIException(Constant.ERROR_CODE_0701,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0701));
            }
            try {
                JSONObject respJson = JSONObject.parseObject(resp);
                if (!respJson.getBoolean("success")) {
                    LogUtils.error(respJson.getString(ERROR));
                    JSONObject errorJson = respJson.getJSONObject(ERROR);
                    throw new AMSRMIException(errorJson.getString("code"),
                            errorJson.getString("message"), errorJson.get("data"));
                }
            } catch (JSONException | NullPointerException e) {
                throw new AMSRMIException(Constant.ERROR_CODE_0702,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0702));
            }
        }

        public String countUniverseBaseCountByUniverseSysName(String tenantPath, String universeSysName) throws AMSRMIException {
            String resp = retryRestTemplate
                    .get(Constant.BITMAP_SERVER_SERVICE, Constant.UNIVERSE_DEFAULT_COUNT_V5, String.class, tenantPath
                            , universeSysName);
            bitmapExceptionHandle(resp);
            return resp;
        }
    }

    @Service
    public static class LookalikeAPI extends API {

        public String getConfidenceByLevel(String tenantPath, String taxonomyId, String level)
                throws AMSRMIException {
            String path = String.format(Constant.GET_CONFIDENCE_BY_LEVEL, tenantPath, taxonomyId, level);
            String resp = retryRestTemplate.get(Constant.LOOKALIKE_SERVER_SERVICE, path, String.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, path, resp));
            return resp;
        }

        public String getConfidenceBySize(String tenantPath, String taxonomyId, Long size)
                throws AMSRMIException {
            String path = String.format(Constant.GET_CONFIDENCE_BY_SIZE, tenantPath, taxonomyId, size);
            String resp = retryRestTemplate.get(Constant.LOOKALIKE_SERVER_SERVICE, path, String.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, path, resp));
            return resp;
        }

        public String getConfidenceByTaxonomyId(String taxonomyId) throws AMSRMIException {
            String path = String.format(Constant.GET_CONFIDENCE_BY_TAXID, taxonomyId);
            String resp = retryRestTemplate.get(Constant.LOOKALIKE_SERVER_SERVICE, path, String.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, path, resp));
            return resp;
        }

        public String getConfidenceLiftByTaxonomyId(String taxonomyId) throws AMSRMIException {
            String path = String.format(Constant.GET_CONFIDENCE_LIFT_BY_TAXID, taxonomyId);
            String resp = retryRestTemplate.get(Constant.LOOKALIKE_SERVER_SERVICE, path, String.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, path, resp));
            return resp;
        }


        public Boolean createJobBySeed(Object reqParams) throws AMSRMIException {
            Boolean resp = retryRestTemplate.post(Constant.LOOKALIKE_SERVER_SERVICE, Constant.PUT_JOB_BY_SEED,
                    reqParams, Boolean.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, Constant.PUT_JOB_BY_SEED, resp));
            return resp;
        }

        public String getJobs() throws AMSRMIException {
            String resp = retryRestTemplate
                    .get(Constant.LOOKALIKE_SERVER_SERVICE, Constant.GET_JOB, String.class);
            LogUtils.debug(String.format(LOOKALIKE_API_RESPONSE, Constant.GET_JOB, resp));
            return resp;
        }
    }

    @Service
    public static class UserCenterAPI extends API {

        public String getPrincipal(String username, String appUrl) throws AMSRMIException {
            return retryRestTemplate
                    .get(Constant.SSO_SERVER_SERVICE, Constant.GET_PRINCIPAL, String.class, username, appUrl);
        }

        public String getAllTenant() throws AMSRMIException {
            String path = String.format(Constant.GET_ALL_TENANT, "00000000-0000-0000-0000-000000000000");
            return retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, String.class);
        }

        public TenantExtVo getTenantExtByKey(String tenantId, String key) throws AMSRMIException {
            String path = String.format(Constant.GET_TENANT_EXT_BY_KEY, tenantId);
            return retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, TenantExtVo.class, key);
        }

        public TenantVo getTenantById(String tenantId) throws AMSRMIException {
            String path = String.format(Constant.GET_SSO_TENANT_BY_ID, tenantId);
            return retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, TenantVo.class);
        }

        public Icon getIconById(Integer iconId) throws AMSRMIException {
            String path = String.format(Constant.GET_ICON_BY_ID, iconId);
            return retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, Icon.class);
        }

        public List<WhiteTenant> getWhiteListByTenantId(String tenantId) throws AMSRMIException {
            String path = String.format(Constant.GET_WHITE_LIST_BY_TENANT_ID, tenantId);
            WhiteTenant[] tenantVos = retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, WhiteTenant[].class);
            return Arrays.asList(tenantVos);
        }

        public List<TenantVo> listUcTenant() throws AMSRMIException {
            String path = String.format(Constant.GET_ALL_TENANT, "00000000-0000-0000-0000-000000000000");
            TenantVo[] tenantVos = retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, TenantVo[].class);
            return Arrays.asList(tenantVos);
        }

        public List<ShareVO> listShareBySourceTenantId(String tenantId) throws AMSRMIException {
            String path = String
                    .format(Constant.LIST_SHARE_BY_SOURCE_TENANT_ID, tenantId);
            ShareVO[] shareVOS = retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, ShareVO[].class);
            return Arrays.asList(shareVOS);
        }

        public List<TenantVo> getWhiteListRemoveBlackListByTenantId(String tenantId)
                throws AMSRMIException {
            String path = String.format(Constant.GET_WHITELIST_REMOVE_BLACKLIST_BY_TENANT_ID, tenantId);
            TenantVo[] resp = retryRestTemplate.get(Constant.SSO_SERVER_SERVICE, path, TenantVo[].class);
            return Arrays.asList(resp);
        }
    }

    @Service
    public static class DataSourceAPI extends API {

        public String getDataSourceList(String tenantId) throws AMSRMIException {
            String path = String.format(Constant.GET_DATASOURCE, tenantId);
            String resp = retryRestTemplate.get(Constant.DATASOURCE_SERVER_SERVICE, path, String.class);
            LogUtils.debug(String.format(DATASOURCE_API_RESPONSE, path, resp));
            return resp;
        }

        public String getOverlap(Integer primaryId, Integer secondaryId) throws AMSRMIException {
            String resp = retryRestTemplate.get(Constant.DATASOURCE_SERVER_SERVICE, Constant.GET_OVERLAP,
                    String.class, primaryId, secondaryId);
            LogUtils.debug(String.format(DATASOURCE_API_RESPONSE, Constant.GET_OVERLAP, resp));
            return resp;
        }

        public Long countOverlapsByMonth(String yearMonth, String tenantIdList) throws AMSRMIException {
            Long resp = retryRestTemplate.get(Constant.DATASOURCE_SERVER_SERVICE, Constant.GET_OVERLAP_COUNT,
                    Long.class, yearMonth, tenantIdList);
            LogUtils.debug(String.format(DATASOURCE_API_RESPONSE, Constant.GET_OVERLAP_COUNT, resp));
            return resp;
        }

        public List<String> getDatasourceByTenantId(String tenantId) throws AMSRMIException {
            String path = String.format(Constant.GET_DATASOURCE_BY_TENANT_ID, tenantId);
            ResponseEntity<List<String>> dataSourceTreePrefixList = retryRestTemplate
                    .get(Constant.DATASOURCE_SERVER_SERVICE, path, new ParameterizedTypeReference<List<String>>() {
                    });
            return dataSourceTreePrefixList.getBody();
        }

        public List<DataStoreNode> listNodeInfoByNodeIds(String tenantId, List<String> taxonomyIdList) throws
                AMSRMIException {
            String path = String.format(Constant.LIST_NODE_INFO_BY_NODE_ID, tenantId);
            DataStoreNode[] resp = retryRestTemplate
                    .post(Constant.DATASOURCE_SERVER_SERVICE, path, taxonomyIdList, DataStoreNode[].class);
            return Arrays.asList(resp);
        }

        public List<Taxonomy> listChildSharedTaxonomyByObjectId(String tenantId, String objectId) throws AMSRMIException {
            String path = String.format(Constant.LIST_SHARED_CHILD_TAXONOMY_BY_OBJECT_PID, tenantId);
            Taxonomy[] resp = retryRestTemplate
                    .get(Constant.DATASOURCE_SERVER_SERVICE, path, Taxonomy[].class, objectId);
            return Arrays.asList(resp);
        }

        public List<Taxonomy> searchSharedTaxonomyByKeys(String tenantId, String[] keys) throws AMSRMIException {
            String path = String.format(Constant.SEARCH_SHARED_TAXONOMY_BY_KEYS, tenantId);
            Taxonomy[] resp = retryRestTemplate
                    .post(Constant.DATASOURCE_SERVER_SERVICE, path, keys, Taxonomy[].class);
            return Arrays.asList(resp);
        }

        public List<Taxonomy> listAttributeSharedTaxonomyByNodeIds(String tenantId, List<String> nodeIds) throws AMSRMIException {
            String path = String.format(Constant.LIST_SHARED_ATTRIBUTE_TAXONOMY_BY_NODE_IDS, tenantId);
            Taxonomy[] resp = retryRestTemplate
                    .post(Constant.DATASOURCE_SERVER_SERVICE, path, nodeIds, Taxonomy[].class);
            return Arrays.asList(resp);
        }

        public Map<String, String> getParentPathByNodeId(String tenantId, String nodeId)
                throws AMSRMIException {
            String path = String
                    .format(Constant.GET_PARENT_PATH_BY_NODE_ID, tenantId, nodeId);
            return retryRestTemplate
                    .get(Constant.DATASOURCE_SERVER_SERVICE, path, Map.class);
        }

        public String getParentPathByTaxonomyId(String taxonomyId, String tenantId, Integer datasourceId)
                throws AMSRMIException {
            String path = String
                    .format(Constant.GET_PARENT_PATH_BY_TAXONOMY_ID, taxonomyId, tenantId, datasourceId);
            return retryRestTemplate
                    .get(Constant.DATASOURCE_SERVER_SERVICE, path, String.class);
        }

        public void refreshSharedDataSourceMappingByTaxonomyName(String taxonomyName, List<String> tenantIdList) throws AMSRMIException {
            String path = String
                    .format(Constant.REFRESH_SHARED_DATASOURCE_MAPPING_FILE, taxonomyName);
            retryRestTemplate.put(Constant.DATASOURCE_SERVER_SERVICE, path, tenantIdList);
        }

        public void updateUniverseStatusByUniverseSysName(String tenantId, String universeSysName) throws AMSRMIException {
            String path = String
                    .format(Constant.UPDATE_UNIVERSE_STATUS_BY_UNIVERSE_SYSTEM_NAME, tenantId, universeSysName);
            retryRestTemplate.put(Constant.DATASOURCE_SERVER_SERVICE, path, null);
        }

        public void createSharedDataSource(AudiencesShareDTO audiencesShareDTO) throws AMSRMIException {
            retryRestTemplate.post(Constant.DATASOURCE_SERVER_SERVICE, Constant.CREATE_SHARE_AUDIENCE, audiencesShareDTO, null);
        }

        public void refreshSharedDataSource(AudiencesShareDTO audiencesShareDTO) throws AMSRMIException {
            retryRestTemplate.put(Constant.DATASOURCE_SERVER_SERVICE, Constant.REFRESH_SHARE_AUDIENCE, audiencesShareDTO);
        }

        public List<Taxonomy> getSharedAudienceList(String tenantId, String objectId) throws AMSRMIException {
            Taxonomy[] resp = retryRestTemplate.get(Constant.DATASOURCE_SERVER_SERVICE, Constant.GET_SHARE_AUDIENCE, Taxonomy[].class, tenantId, objectId);
            return Arrays.asList(resp);
        }
    }

    @Service
    public static class MessageCenterAPI extends API {

        public void sendEmail(ReceiveMessageDTO receiveMessageDTO) throws
                AMSRMIException {
            retryRestTemplate.post(Constant.MESSAGE_CENTER_API_URL, Constant.SEND_EMAIL_BY_PARAM, receiveMessageDTO,
                    null);
        }
    }

    @Service
    public static class AdvanceLookalikeAPI extends API {

        public String buildModel(String accessKey, String secretKey, JSONObject reqParams) {
            TreeMap<String, String> awsHeaders = new TreeMap<String, String>();
            awsHeaders.put("host", Constant.ADVANCE_URL_HOST);
            awsHeaders.put("content-type", "application/json");

            Map<String, String> header = new AWSV4Auth.Builder(accessKey, secretKey)
                    .regionName(Constant.ADVANCE_URL_REGION)
                    .serviceName(Constant.ADVANCE_URL_SERVICE)
                    .httpMethodName(HttpMethod.POST.toString())
                    .canonicalURI(Constant.ADVANCE_URL_DIRECTORY.concat(Constant.ADVANCE_BUILD_MODEL))
                    .awsHeaders(awsHeaders)
                    .payload(reqParams.toJSONString())
                    .build()
                    .getHeaders();

            String path =
                    Constant.ADVANCE_URL_TYPE.concat("://").concat(Constant.ADVANCE_URL_HOST).concat(Constant.ADVANCE_URL_DIRECTORY).concat(Constant.ADVANCE_BUILD_MODEL);
            ResponseEntity<String> resp = retryRestTemplate.doPostWithHeader(path, header, reqParams,
                    new ParameterizedTypeReference<String>() {
                    });
            return resp.getBody();
        }

        public String getModel(String accessKey, String secretKey, String jobId) {
            TreeMap<String, String> params = new TreeMap<String, String>();
            params.put(Constant.ADVANCE_JOB_ID, jobId);

            TreeMap<String, String> awsHeaders = new TreeMap<String, String>();
            awsHeaders.put("host", Constant.ADVANCE_URL_HOST);

            Map<String, String> header = new AWSV4Auth.Builder(accessKey, secretKey)
                    .regionName(Constant.ADVANCE_URL_REGION)
                    .serviceName(Constant.ADVANCE_URL_SERVICE)
                    .httpMethodName(HttpMethod.GET.toString())
                    .canonicalURI(Constant.ADVANCE_URL_DIRECTORY.concat(Constant.ADVANCE_GET_MODEL))
                    .queryParametes(params)
                    .awsHeaders(awsHeaders)
                    .payload(null)
                    .build()
                    .getHeaders();

            String path =
                    Constant.ADVANCE_URL_TYPE.concat("://").concat(Constant.ADVANCE_URL_HOST).concat(Constant.ADVANCE_URL_DIRECTORY).concat(Constant.ADVANCE_GET_MODEL).concat("?job-id=").concat(jobId);
            try {
                ResponseEntity<String> resp = retryRestTemplate.doGetWithHeader(path, header,
                        new ParameterizedTypeReference<String>() {
                        });
                return resp.getBody();
            } catch (HttpClientErrorException e) {
                return e.getResponseBodyAsString();
            }
        }

        public String deployModel(String accessKey, String secretKey, JSONObject reqParams) {
            TreeMap<String, String> awsHeaders = new TreeMap<String, String>();
            awsHeaders.put("host", Constant.ADVANCE_URL_HOST);
            awsHeaders.put("content-type", "application/json");

            Map<String, String> header = new AWSV4Auth.Builder(accessKey, secretKey)
                    .regionName(Constant.ADVANCE_URL_REGION)
                    .serviceName(Constant.ADVANCE_URL_SERVICE)
                    .httpMethodName(HttpMethod.POST.toString())
                    .canonicalURI(Constant.ADVANCE_URL_DIRECTORY.concat(Constant.ADVANCE_DEPLOY_MODEL))
                    .awsHeaders(awsHeaders)
                    .payload(reqParams.toJSONString())
                    .build()
                    .getHeaders();

            String path =
                    Constant.ADVANCE_URL_TYPE.concat("://").concat(Constant.ADVANCE_URL_HOST).concat(Constant.ADVANCE_URL_DIRECTORY).concat(Constant.ADVANCE_DEPLOY_MODEL);
            ResponseEntity<String> resp = retryRestTemplate.doPostWithHeader(path, header, reqParams,
                    new ParameterizedTypeReference<String>() {
                    });
            return resp.getBody();
        }
    }
}