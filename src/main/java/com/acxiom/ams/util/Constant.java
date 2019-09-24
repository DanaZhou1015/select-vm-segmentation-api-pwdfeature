package com.acxiom.ams.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cldong on 12/5/2017.
 */
@Component
public class Constant {
    public static final String BLANK_STR = "    ";
    public static final String COPY_FIX = "_copy";
    public static final Integer LINE_HEIGHT = 230;
    public static String TAXONOMY_SERVER_SERVICE;
    public static String BITMAP_SERVER_SERVICE;
    public static String LOOKALIKE_SERVER_SERVICE;
    public static String SSO_SERVER_SERVICE;
    //public static String MOBILE_CONNECT_SERVER_SERVICE;
    public static String DATASOURCE_SERVER_SERVICE;
    public static final String UNDER_LINE = "_";
    public static final String COMMA = ",";
    public static final String PER_CENT = "%";
    // callback url
    public static String CALLBACK_URL;
    public static String AI_SERVICE;
    public static String AI_USER;
    public static String AI_TOKEN;
    public static Boolean AI_USE;

    public static final String EXPORT_TEMPLATE_FILE = "init/tmp/CampaignTemplate.xlsx";

    // TV-FE app url
    public static String TV_FE_APP_URL;

    public static final String GET_TAXONOMY_LIST_BY_NODE = "/v1/list/%s/%s/%s";
    public static final String GET_TAXONOMY_LIST_BY_CURRENT_NODE = "/v1/node/%s/%s/%s";
    public static final String GET_ENDTYPE_TAXONOMY_BY_COND = "/v1/endType/%s/%s/%s";
    public static final String GET_TAXONOMY_LIST_BY_KEY = "/v1/list/search/%s/%s";
    public static final String GET_TAXONOMY_LIST_BY_CHILDREN = "/v1/parent/list/%s/%s";
    public static final String GET_PARENT_PATH_BY_CHILDREN = "/v1/path/%s/%s/%s";
    public static final String GET_TAXONOMY_ATTRIBUTE_BY_KEY = "/v1/list/attribute/search/%s/%s?key={key}";
    public static final String GET_TAXONOMY_LIST_BY_NODE_ID = "/v2/taxonomy/list/%s/%s";
    public static final String GET_TAXONOMY_ATTRIBUTE_OWNER = "/v2/taxonomy/attribute/owner/%s/%s";
    public static final String GET_TAXONOMY_ATTRIBUTE_OWNER_DATATYPE = "/v2/taxonomy/attribute/ownerAndDataType/%s/%s";
    public static final String MIGRATION_TAXONOMY_TREE = "/v3/taxonomy/migration/tree/data";
    public static final String COUNT_DEPTH_AND_NODE = "/v3/taxonomy/count/%s";
    public static final String LIST_PRICE_AND_OWNER_BY_TAXONOMY_ID = "/v3/taxonomy/price/%s";
    public static final String LIST_DATATYPE_AND_PRICE_AND_OWNER_BY_TAXONOMY_ID = "/v3/taxonomy/data/price/owner/%s";
    public static final String LIST_UNIVERSE_ATTRIBUTE_BY_KEY = "/v2/taxonomy/universe/attribute/%s/%s";
    public static final String DELETE_VERSION_BY_ID_LIST = "/v2/taxonomy";

    // ai query
    public static final String AI_PARTICIPLE = "/query";

    //bitmap-api
    public static final String POST_CALCULATE_V5 = "/v5/bitmap/calculate";
    public static final String POST_CALCULATE_V6 = "/v6/bitmap/calculate";
    public static final String POST_CALCULATE_V7 = "/v7/bitmap/calculate";
    public static final String POST_CREATE_BITMAP_V5 = "/v5/bitmap/createBitmap";
    public static final String POST_CREATE_BITMAP_V7 = "/v7/bitmap/createbitmap";
    public static final String POST_PROFILING_V5 = "/v5/bitmap/profiling";
    public static final String POST_PROFILING_V7 = "/v7/bitmap/profiling";
    public static final String POST_PROFILING_V6 = "/v6/bitmap/profiling";
    public static final String POST_DISTRIBUTE_V7 = "/v7/bitmap/distribute";
    public static final String POST_DISTRIBUTE_V5 = "/v5/bitmap/distribute";
    public static final String GET_CAMPAIGN_INFO = "/v5/bitmap/campaignInfo";
    public static final String UNIVERSE_DEFAULT_COUNT_V5 = "/v5/bitmap/totalCount?tenantPath={tenantPath}&universeSysName={universeSysName}";
    public static final String LIST_COUNT_BY_TAXONOMY_ID_VM = "/v7/bitmap/attributes/%s/%s";

    // datasource-api
    public static final String GET_DATASOURCE = "/v1/datasource/tenant/%s";
    public static final String GET_OVERLAP = "/v1/datasource/overlap?primaryId={primaryId}&secondaryId={secondaryId}";
    public static final String GET_OVERLAP_COUNT = "/v2/datasource/metrics?yearMonth={yearMonth}&tenantIdList={tenantIdList}";
    public static final String GET_DATASOURCE_BY_TENANT_ID = "/v1/datasource/type/datastore/%s";
    public static final String LIST_NODE_INFO_BY_NODE_ID = "/v1/datasource//node/info/%s";
    public static final String LIST_SHARED_CHILD_TAXONOMY_BY_OBJECT_PID = "/v1/datasource/shared/taxonomy/%s?objectId={objectId}";
    public static final String SEARCH_SHARED_TAXONOMY_BY_KEYS = "/v1/datasource/search/shared/taxonomy/%s";
    public static final String LIST_SHARED_ATTRIBUTE_TAXONOMY_BY_NODE_IDS = "/v1/datasource/shared/attribute/taxonomy/%s";
    public static final String GET_PARENT_PATH_BY_NODE_ID = "/v1/datasource/shared/taxonomy/path/%s/%s";
    public static final String GET_PARENT_PATH_BY_TAXONOMY_ID = "/v1/datasource/parent/path/%s/%s/%s";
    public static final String REFRESH_SHARED_DATASOURCE_MAPPING_FILE = "/v1/datasource/refresh/mapping/%s";
    public static final String UPDATE_UNIVERSE_STATUS_BY_UNIVERSE_SYSTEM_NAME = "/v1/track/%s/%s";
    public static final String CREATE_SHARE_AUDIENCE = "/share/audience";
    public static final String REFRESH_SHARE_AUDIENCE = "/share/audience";
    public static final String GET_SHARE_AUDIENCE = "/share/audience?tenantId={tenantId}&objectId={objectId}";

    //lookalike  confidence
    public static final String GET_CONFIDENCE_BY_LEVEL = "/v1/confidence/by-level/%s/%s/%s";
    public static final String GET_CONFIDENCE_BY_SIZE = "/v1/confidence/by-size/%s/%s/%d";
    public static final String GET_CONFIDENCE_BY_TAXID = "/v1/confidence/%s";
    public static final String GET_CONFIDENCE_LIFT_BY_TAXID = "/v1/confidence/get-lift/%s";
    //lookalike job
    public static final String GET_JOB = "/v1/job";
    public static final String PUT_JOB_BY_SEED = "/v1/job/by-seed";

    public static final String GET_TAXONOMY_ALL_ATTRIBUTE = "/v2/taxonomy/all/attribute/%s";

    // sso api
    public static final String GET_PRINCIPAL = "/principal?login={username}&appurl={appUrl}";
    public static final String GET_ALL_TENANT = "/tenants/all/%s";
    public static final String GET_TENANT_EXT_BY_KEY = "/tenants-ext/%s?key={key}";
    public static final String GET_SSO_TENANT_BY_ID = "/tenants/%s";
    public static final String GET_ICON_BY_ID = "/v2/tenant/icon/%s";
    public static final String GET_WHITE_LIST_BY_TENANT_ID = "/v2/share/white-black-list/confirm/%s";
    public static final String LIST_SHARE_BY_SOURCE_TENANT_ID = "v2/share/list/%s";
    public static final String GET_WHITELIST_REMOVE_BLACKLIST_BY_TENANT_ID = "/v2/share/whitelist/%s";

    //lookalike callback
    public static final String GET_UPDATE_STATUS = "/v1/lookalike/updateStatus?taxid=${taxid}&status=${status}&email=${email}";

    public static String DISTRIBUTION_PLATFORM_VALUE;

    public static final String TAXONOMY_END_NODE_TYPE = "end";
    public static final String ATTRIBUTE_NODE_TYPE = "attribute";

    public static final String CAMPAIGN_EXPORT_NAME = "Audience_";

    public static final String TAXONOMY_NAME = "taxonomy";

    public static final String NONE = "NONE";

    //msg-center-api
    public static final String SEND_EMAIL_BY_PARAM = "/api/v1/message";
    public static String MESSAGE_CENTER_API_URL;
    public static String SEND_EMAIL_ROLE;

    //universe
    public static final String UNIVERSE_CALCULATE = "/v1/universe/calculate";
    public static final String CREATE_UNIVERSE = "/v1/universe/save";

    /**
     * universe activity
     */
    public static final String UNIVERSE_ACTIVITY_APPROVE = "approve";
    public static final String UNIVERSE_ACTIVITY_REJECT = "reject";

    /**
     *
     */
    public static Integer DEFAULT_PAGE_SIZE;

    @Value("${default.page.size}")
    public void setDefaultPageSize(Integer size) {
        DEFAULT_PAGE_SIZE = size;
    }

    /**
     * advance lookalike
     */
    public static String ADVANCE_URL_TYPE;
    public static String ADVANCE_URL_HOST;
    public static String ADVANCE_URL_DIRECTORY;
    public static String ADVANCE_URL_REGION;
    public static String ADVANCE_URL_SERVICE;

    @Value("${advance.url.type}")
    public void setAdvanceUrlType(String type) {
        ADVANCE_URL_TYPE = type;
    }

    @Value("${advance.url.host}")
    public void setAdvanceUrlHost(String host) {
        ADVANCE_URL_HOST = host;
    }

    @Value("${advance.url.directory}")
    public void setAdvanceUrlDirectory(String directory) {
        ADVANCE_URL_DIRECTORY = directory;
    }

    @Value("${advance.url.region}")
    public void setAdvanceUrlRegion(String region) {
        ADVANCE_URL_REGION = region;
    }

    @Value("${advance.url.service}")
    public void setAdvanceUrlService(String service) {
        ADVANCE_URL_SERVICE = service;
    }

    public static String ADVANCE_BASE_DATA;
    public static String ADVANCE_UNIVERSE_NAME;

    @Value("${advance.base.data}")
    public void setAdvanceBaseData(String baseData) {
        ADVANCE_BASE_DATA = baseData;
    }

    @Value("${advance.universe.name}")
    public void setAdvanceUniverseName(String universeName) {
        ADVANCE_UNIVERSE_NAME = universeName;
    }

    public static String ADVANCE_S3_PATH;

    @Value("${advance.s3.path}")
    public void setAdvanceS3Path(String path) {
        ADVANCE_S3_PATH = path;
    }

    public static final String ADVANCE_BASE_DATA_TENANT_EXT_KEY = "advanceBaseData";
    public static final String ADVANCE_UNIVERSE_NAME_TENANT_EXT_KEY = "advanceUniverseName";
    public static final String ADVANCE_USER_TENANT_EXT_KEY = "advanceUser";
    public static final String ADVANCE_ACCESS_KEY_TENANT_EXT_KEY = "advanceAccessKey";
    public static final String ADVANCE_SECRET_KEY_TENANT_EXT_KEY = "advanceSecretKey";

    public static final String ADVANCE_JOB_ID = "job-id";
    public static final String ADVANCE_REACH_VALUE = "reach-value";
    public static final String ADVANCE_MODEL_CLASS_BINARY = "binary";
    public static final String ADVANCE_MODEL_TYPE_LASSO = "lasso";

    public static final String ADVANCE_RESULT_UPLIFT = "uplift";
    public static final String ADVANCE_RESULT_REACH_VALUES = "reach-values";

    public static final String ADVANCE_CREATE_LOOKALIKE_SEED = "/v7/bitmap/create/seed";
    public static final String ADVANCE_CREATE_LOOKALIKE_TO_BITMAP = "/v7/bitmap/lookalike/to/bitmap";
    public static final String ADVANCE_BUILD_MODEL = "/build";
    public static final String ADVANCE_GET_MODEL = "/collect";
    public static final String ADVANCE_DEPLOY_MODEL = "/deploy";

    public static int ADVANCE_GET_API_DELAY_TIME;
    public static int ADVANCE_GET_API_CYCLE_TIME;
    public static int ADVANCE_DEPLOY_API_DELAY_TIME;
    public static int ADVANCE_DEPLOY_API_CYCLE_TIME;

    @Value("${advance.getApi.delayTime}")
    public void setAdvanceGetApiDelayTime(int delayTime) {
        ADVANCE_GET_API_DELAY_TIME = delayTime;
    }

    @Value("${advance.getApi.cycleTime}")
    public void setAdvanceGetApiCycleTime(int cycleTime) {
        ADVANCE_GET_API_CYCLE_TIME = cycleTime;
    }

    @Value("${advance.deployApi.delayTime}")
    public void setAdvanceDeployApiDelayTime(int delayTime) {
        ADVANCE_DEPLOY_API_DELAY_TIME = delayTime;
    }

    @Value("${advance.deployApi.cycleTime}")
    public void setAdvanceDeployApiCycleTime(int cycleTime) {
        ADVANCE_DEPLOY_API_CYCLE_TIME = cycleTime;
    }

    public static final String MESSAGE_SEND_TYPE = "Email";
    public static final String MESSAGE_TYPE = "actions and information";

    // 2p test control
    public static final String TEST_CONTROL_RUNNING = "testCtrlRunning";
    public static final String TEST_CONTROL_SUCCESS = "testCtrlSuccess";
    public static final String TEST_CONTROL_FAILED = "testCtrlFailed";


    // Error Code
    public static final String ERROR_CODE_0107 = "020107";
    public static final String ERROR_CODE_0108 = "020108";

    public static final String ERROR_CODE_0201 = "020201";
    public static final String ERROR_CODE_0202 = "020202";
    public static final String ERROR_CODE_0203 = "020203";
    public static final String ERROR_CODE_0204 = "020204";
    public static final String ERROR_CODE_0205 = "020205";
    public static final String ERROR_CODE_0206 = "020206";
    public static final String ERROR_CODE_0207 = "020207";
    public static final String ERROR_CODE_0209 = "020209";
    public static final String ERROR_CODE_0210 = "020210";
    public static final String ERROR_CODE_0211 = "020211";
    public static final String ERROR_CODE_0212 = "020212";
    public static final String ERROR_CODE_0213 = "020213";
    public static final String ERROR_CODE_0214 = "020214";
    public static final String ERROR_CODE_0215 = "020215";
    public static final String ERROR_CODE_0216 = "020216";
    public static final String ERROR_CODE_0217 = "020217";
    public static final String ERROR_CODE_0218 = "020218";
    public static final String ERROR_CODE_0219 = "020219";
    public static final String ERROR_CODE_0221 = "020221";
    public static final String ERROR_CODE_0224 = "020224";
    public static final String ERROR_CODE_0227 = "020227";
    public static final String ERROR_CODE_0228 = "020228";
    public static final String ERROR_CODE_0232 = "020232";
    public static final String ERROR_CODE_0233 = "020233";
    public static final String ERROR_CODE_0238 = "020238";
    public static final String ERROR_CODE_0239 = "020239";
    public static final String ERROR_CODE_0240 = "020240";
    public static final String ERROR_CODE_0242 = "020242";
    public static final String ERROR_CODE_0243 = "020243";
    public static final String ERROR_CODE_0246 = "020246";
    public static final String ERROR_CODE_0247 = "020247";
    public static final String ERROR_CODE_0248 = "020248";
    public static final String ERROR_CODE_0249 = "020249";
    public static final String ERROR_CODE_0251 = "020251";
    public static final String ERROR_CODE_0252 = "020252";
    public static final String ERROR_CODE_0253 = "020253";
    public static final String ERROR_CODE_0254 = "020254";
    public static final String ERROR_CODE_0255 = "020255";
    public static final String ERROR_CODE_0256 = "020256";
    public static final String ERROR_CODE_0257 = "020257";
    public static final String ERROR_CODE_0258 = "020258";
    public static final String ERROR_CODE_0259 = "020259";
    public static final String ERROR_CODE_0261 = "020261";
    public static final String ERROR_CODE_0268 = "020268";
    public static final String ERROR_CODE_0269 = "020269";
    public static final String ERROR_CODE_0281 = "020281";

    public static final String ERROR_CODE_0701 = "020701";
    public static final String ERROR_CODE_0702 = "020702";

    @Value("${ai.api.url}")
    public void setAiService(String aiService) {
        AI_SERVICE = aiService;
    }

    @Value("${ai.api.use}")
    public void setAiUse(Boolean aiUse) {
        AI_USE = aiUse;
    }

    @Value("${ai.api.user}")
    public void setAiUser(String aiUser) {
        AI_USER = aiUser;
    }

    @Value("${ai.api.token}")
    public void setAiToken(String aiToken) {
        AI_TOKEN = aiToken;
    }

    @Value("${taxonomy.server.service}")
    public void setTaxonomyServerService(String taxonomyServerService) {
        TAXONOMY_SERVER_SERVICE = taxonomyServerService;
    }

    @Value("${distribution.platform.value}")
    public void setDistributionValue(String distributionValue) {
        DISTRIBUTION_PLATFORM_VALUE = distributionValue;
    }

    @Value("${bitmap.server.service}")
    public void setBitmapServerService(String bitmapServerService) {
        BITMAP_SERVER_SERVICE = bitmapServerService;
    }

    @Value("${lookalike.server.service}")
    public void setLookalikeServerService(String lookalikeServerService) {
        LOOKALIKE_SERVER_SERVICE = lookalikeServerService;
    }

    @Value("${callback.url}")
    private void setCallbackUrl(String callbackUrl) {
        CALLBACK_URL = callbackUrl;
    }

    @Value("${sso.server.service}")
    public void setSsoServerService(String ssoServerService) {
        SSO_SERVER_SERVICE = ssoServerService;
    }

    @Value("${datasource.server.service}")
    public void setDatasourceServerService(String datasourceServerService) {
        DATASOURCE_SERVER_SERVICE = datasourceServerService;
    }

    @Value("${tv-fe.app.url}")
    public void setTvFeAppUrl(String tvFeAppUrl) {
        TV_FE_APP_URL = tvFeAppUrl;
    }

    @Value("${msg-backend}")
    public void setMessageCenterApiUrl(String messageCenterApiUrl) {
        MESSAGE_CENTER_API_URL = messageCenterApiUrl;
    }

    @Value("${send.email.role}")
    public void setSendEmailRole(String sendEmailRole) {
        SEND_EMAIL_ROLE = sendEmailRole;
    }

    public static String formatDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String date;
        try {
            format.setLenient(false);
            date = format.format(format.parse(strDate));
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    public static String formatDateByYearMonthDay(Date strDate) {
        SimpleDateFormat format = new SimpleDateFormat("MMddyy");
        String date;
        try {
            format.setLenient(false);
            date = format.format(strDate);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

}