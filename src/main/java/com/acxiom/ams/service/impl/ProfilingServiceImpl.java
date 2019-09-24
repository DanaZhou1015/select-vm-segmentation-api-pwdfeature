package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSFileIOException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.ProfilingPoMapper;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.dto.ProfilingDTO;
import com.acxiom.ams.model.dto.v2.ExportInsightDTO;
import com.acxiom.ams.model.po.ProfilingPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.ProfilingVo;
import com.acxiom.ams.repository.ProfilingJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.ProfilingService;
import com.acxiom.ams.service.TenantService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.*;
import java.util.*;

import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
@Service
public class ProfilingServiceImpl implements ProfilingService {
    @Autowired
    VersionPoJPA versionPoJPA;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    VersionPoMapper versionPoMapper;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    ProfilingJPA profilingJPA;
    @Autowired
    ProfilingPoMapper profilingPoMapper;
    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;
    @Autowired
    ServiceAPI.DataSourceAPI dataSourceAPI;
    @Autowired
    TenantService tenantService;

    private static final String INSIGHTS_SUFFIX = "insights";
    private static final String EXCEL_SUFFIX = ".xlsx";
    @Value("${temp.file.path}")
    private String tempFilePath;

    @Override
    public List<ProfilingVo> getProfilingByTenantId(Long tenantId, Long profilingId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        tenantPoJPA.findTenantPoById(tenantId);
        List<ProfilingPo> profilingPos;
        if (profilingId == -1) {
            //select all
            profilingPos = profilingJPA.findProfilingPoByTenantPo(tenantPo);
        } else {
            //select single
            ProfilingPo profilingPo = profilingJPA.findOne(profilingId);
            if (profilingPo.getTenantPo().getId() != tenantId) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0254,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0254));
            }
            profilingPos = new ArrayList<>();
            profilingPos.add(profilingPo);
        }
        return profilingPoMapper.map(profilingPos);
    }


    /**
     * @Author: Gavin
     * @Description: select using profiling
     * @Date: 4/2/2018\ 5:50 PM
     * @Params: * @param null
     */
    @Override
    public ProfilingVo getActiveProfilingByTenantId(Long tenantId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        tenantPoJPA.findTenantPoById(tenantId);
        ProfilingPo profilingPo = profilingJPA.findProfilingPoByTenantPoAndActive(tenantPo, true);
        return profilingPoMapper.map(profilingPo);
    }


    /**
     * @Author: Gavin
     * @Description: set active profiling
     * @Date: 4/2/2018\ 5:50 PM
     * @Params: * @param null
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setActiveProfiling(Long tenantId, Long profilingId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        profilingJPA.updateActiveByTenantPo(false, tenantPo);
        profilingJPA.updateActiveByTenantPoAndId(true, tenantPo, profilingId);
    }


    @Override
    public boolean deleteProfilingById(Long tenantId, Long profilingId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        if (profilingId > 0) {
            ProfilingPo profilingPo = profilingJPA.findProfilingPoByTenantPoAndId(tenantPo, profilingId);
            if (null == profilingPo) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0252,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0252));
            }
            profilingJPA.delete(profilingId);
            return true;
        } else {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0253,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0253));
        }
    }

    @Override
    public ProfilingVo getInsightByDestinationId(Long destinationId) {
        ProfilingPo profilingPo = profilingJPA.findByDestinationIdAndActive(destinationId, true);
        return profilingPoMapper.map(profilingPo);
    }

    @Override
    public long saveProfiling(Long tenantId, ProfilingDTO profilingDTO) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        ProfilingPo profilingPo;
        if (profilingDTO.getId() <= 0) {
            //insert
            profilingPo = profilingJPA.findProfilingPoByTenantPoAndName(tenantPo, profilingDTO.getProfilingName());
            if (null != profilingPo) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0251,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0251));
            }
            profilingPo = new ProfilingPo();
        } else {
            //update
            profilingPo = profilingJPA.findProfilingPoByTenantPoAndId(tenantPo, profilingDTO.getId());
            if (null == profilingPo) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0252,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0252));
            }
        }
        profilingPo.setName(profilingDTO.getProfilingName());
        profilingPo.setDescription(profilingDTO.getProfilingDescription());
        profilingPo.setJson(profilingDTO.getProfilingJson());
        profilingPo.setTenantPo(tenantPo);
        profilingPo.setCreatedBy(profilingDTO.getCreateBy());
        profilingJPA.save(profilingPo);
        return profilingPo.getId();
    }


    @Override
    public String profiling(Long tenantId, String req) throws AMSException {
        tenantService.getTenantById(tenantId);
        return bitmapAPI.profiling(req);
    }


    @Override
    public String fillInsight(Long tenantId, String req) throws AMSException {
        tenantService.getTenantById(tenantId);
        return bitmapAPI.fillInsight(req);
    }

    @Override
    public String profilingV7(Long tenantId, String req) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<String> dataSourceTreePrefixList = dataSourceAPI.getDatasourceByTenantId(tenantPo.getTenantId());
        JSONObject obj;
        try {
            obj = JSON.parseObject(req);
        } catch (JSONException e) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0232,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0232));
        }
        obj.put("dataSourceTreePrefixList", dataSourceTreePrefixList);
        return bitmapAPI.profilingV7(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
    }

    @Override
    public ResponseEntity<Resource> exportInsight(ExportInsightDTO exportInsightDTO)
            throws AMSException {
        HttpHeaders headers = new HttpHeaders();
        String time = Constant.formatDateByYearMonthDay(new Date());
        String fileName = exportInsightDTO.getAudienceName().concat(Constant.UNDER_LINE)
                .concat(time).concat(Constant.UNDER_LINE).concat(INSIGHTS_SUFFIX).concat
                        (EXCEL_SUFFIX);
        File file = new File(tempFilePath.concat(File.separator).concat(fileName));
        try {
            if (!file.createNewFile()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0256,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0256));
            }
            ExcelUtils.generateInsightExcel(exportInsightDTO, file);
        } catch (IOException e) {
            LogUtils.error(e);
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0256,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0256));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                bos.write(ch);
            }
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("charset", "utf-8");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0107,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0107));
        }
        Resource resource = new InputStreamResource(
                new ByteArrayInputStream(bos.toByteArray()));
        FileSystemUtils.deleteRecursively(file);
        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/x-msdownload")).body(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setActiveInsight(Long destinationId, Long profilingId) throws AMSInvalidInputException {
        ProfilingPo profilingPo = Optional.ofNullable(profilingJPA.findPoByDestinationIdAndId(destinationId,
                profilingId)).orElseThrow(() ->
                new AMSInvalidInputException(Constant.ERROR_CODE_0252,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0252)));
        profilingJPA.updateActiveByDestinationId(false, destinationId);
        profilingPo.setActive(true);
        profilingJPA.save(profilingPo);
    }

    @Override
    public long saveInsight(Long tenantId, Long destinationId, ProfilingDTO profilingDTO) throws
            AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        ProfilingPo profilingPo;
        if (profilingDTO.getId() <= 0) {
            //insert
            profilingPo = profilingJPA.findPoByDestinationIdAndName(destinationId, profilingDTO.getProfilingName());
            if (Optional.ofNullable(profilingPo).isPresent()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0251,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0251));
            }
            profilingPo = new ProfilingPo();
        } else {
            //update
            profilingPo = Optional.ofNullable(profilingJPA.findOne(profilingDTO.getId())).orElseThrow(() ->
                    new AMSInvalidInputException(Constant.ERROR_CODE_0252,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0252)));
        }
        profilingPo.setDestinationId(destinationId);
        profilingPo.setName(profilingDTO.getProfilingName());
        profilingPo.setDescription(profilingDTO.getProfilingDescription());
        profilingPo.setJson(profilingDTO.getProfilingJson());
        profilingPo.setTenantPo(tenantPo);
        profilingPo.setCreatedBy(profilingDTO.getCreateBy());
        profilingJPA.save(profilingPo);
        return profilingPo.getId();
    }

    @Override
    public List<ProfilingVo> listInsightsByDestinationId(Long destinationId) {
        return profilingPoMapper.map(profilingJPA.findByDestinationId(destinationId));
    }
}
