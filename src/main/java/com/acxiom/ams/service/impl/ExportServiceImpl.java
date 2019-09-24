package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSFileIOException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.po.UniverseActivityLogPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.repository.UniverseActivityLogPoJPA;
import com.acxiom.ams.service.BitmapService;
import com.acxiom.ams.service.ExportService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.UniverseService;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.*;

import static com.acxiom.ams.util.Constant.BLANK_STR;
import static com.acxiom.ams.util.Constant.LINE_HEIGHT;
import static com.acxiom.ams.util.StringUtil.*;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    UniverseActivityLogPoJPA universeActivityLogPoJPA;

    @Autowired
    UniverseService universeService;

    @Autowired
    TenantService tenantService;

    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;

    @Autowired
    BitmapService bitmapService;

    @Autowired
    ServiceAPI.DataSourceAPI dataSourceAPI;

    @Value("${temp.file.path}")
    private String tempFile;

    @Value("${export.template.file}")
    private String exportTemplateFile;

    private static final String SEGMENTS = "segments";
    private static final String INCLUDE = "include";
    private static final String EXCLUDE = "exclude";
    private static final String VALUES = "values";

    @Override
    public ResponseEntity<Resource> exportUniverseActivityLog(List<Long> idList, Long tenantId) throws AMSException {
        List<UniverseActivityLogPo> universeActivityLogPoList = universeActivityLogPoJPA.findAll(idList);
        List<String> nameList = new ArrayList<>();
        Map<String, Integer> countMap = new HashMap<>(16);
        for (UniverseActivityLogPo universeActivityLogPo : universeActivityLogPoList) {
            String name = getUniverseActivityName(universeActivityLogPo, countMap);
            nameList.add(name);
        }
        String fileName = "";
        File file;
        if (nameList.size() == 1) {
            file = new File(nameList.get(0));
            fileName = getFileNameByPath(nameList.get(0));
        } else {
            String zipPath = getZip(nameList);
            file = new File(zipPath);
            fileName = Constant.CAMPAIGN_EXPORT_NAME + getCurrentDate() + ".zip";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                bos.write(ch);
            }
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("charset", "utf-8");
            headers.add("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0107,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0107));
        }
        Resource resource = new InputStreamResource(
                new ByteArrayInputStream(bos.toByteArray()));
        FileSystemUtils.deleteRecursively(file);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/x-msdownload")).body(resource);
    }

    private String getUniverseActivityName(UniverseActivityLogPo universeActivityLogPo, Map<String, Integer> countMap)
            throws AMSException {
        UniversePo universePo = universeService.getUniverseById(universeActivityLogPo.getDestinationId());
        String name = universePo.getUniverseName() + "_" + universeActivityLogPo.getAudienceId();
        Integer count = countMap.get(name);
        if (count == null) {
            countMap.put(name, 0);
        } else {
            countMap.put(name, count++);
            name += "_" + count;
        }
        String exportName = tempFile + "/audience" + "_" + name + ".xlsx";
        File exportFile = new File(exportName);
        File templateFile = new File(exportTemplateFile);
        if(!templateFile.exists()){
            templateFile = new File(Constant.EXPORT_TEMPLATE_FILE);
        }
        try (FileInputStream fileInputStream = new FileInputStream(templateFile)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                bos.write(ch);
            }
            FileCopyUtils.copy(bos.toByteArray(), exportFile);
        } catch (IOException e) {
            throw new AMSFileIOException();
        }
        XSSFWorkbook wk = null;
        try {
            wk = new XSSFWorkbook(new FileInputStream(exportFile));
        } catch (IOException e) {
            LogUtils.error(e);
        }
        XSSFSheet sheet = wk.getSheetAt(0);
        CellStyle leftCellStyle = wk.createCellStyle();
        leftCellStyle.setAlignment(HorizontalAlignment.LEFT);
        leftCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftCellStyle.setWrapText(true);
        leftCellStyle.setBorderBottom(BorderStyle.THIN);
        leftCellStyle.setBorderLeft(BorderStyle.THIN);
        leftCellStyle.setBorderTop(BorderStyle.THIN);
        leftCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle midCellStyle = wk.createCellStyle();
        midCellStyle.setAlignment(HorizontalAlignment.CENTER);
        midCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        midCellStyle.setWrapText(true);
        midCellStyle.setBorderBottom(BorderStyle.THIN);
        midCellStyle.setBorderLeft(BorderStyle.THIN);
        midCellStyle.setBorderTop(BorderStyle.THIN);
        midCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle rightCellStyle = wk.createCellStyle();
        rightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightCellStyle.setWrapText(true);
        rightCellStyle.setBorderBottom(BorderStyle.THIN);
        rightCellStyle.setBorderLeft(BorderStyle.THIN);
        rightCellStyle.setBorderTop(BorderStyle.THIN);
        rightCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle noThinRightCellStyle = wk.createCellStyle();
        noThinRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        noThinRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        noThinRightCellStyle.setWrapText(true);
        CellStyle lastMidCellStyle = wk.createCellStyle();
        lastMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        lastMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        lastMidCellStyle.setWrapText(true);
        lastMidCellStyle.setBorderBottom(BorderStyle.THIN);
        lastMidCellStyle.setBorderLeft(BorderStyle.THIN);
        lastMidCellStyle.setBorderTop(BorderStyle.THIN);
        lastMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle endLeftCellStyle = wk.createCellStyle();
        endLeftCellStyle.setAlignment(HorizontalAlignment.LEFT);
        endLeftCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endLeftCellStyle.setWrapText(true);
        endLeftCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endLeftCellStyle.setBorderLeft(BorderStyle.THIN);
        endLeftCellStyle.setBorderTop(BorderStyle.THIN);
        endLeftCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endMidCellStyle = wk.createCellStyle();
        endMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        endMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endMidCellStyle.setWrapText(true);
        endMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endMidCellStyle.setBorderLeft(BorderStyle.THIN);
        endMidCellStyle.setBorderTop(BorderStyle.THIN);
        endMidCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endRightCellStyle = wk.createCellStyle();
        endRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        endRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endRightCellStyle.setWrapText(true);
        endRightCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endRightCellStyle.setBorderLeft(BorderStyle.THIN);
        endRightCellStyle.setBorderTop(BorderStyle.THIN);
        endRightCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endLastMidCellStyle = wk.createCellStyle();
        endLastMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        endLastMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endLastMidCellStyle.setWrapText(true);
        endLastMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endLastMidCellStyle.setBorderLeft(BorderStyle.THIN);
        endLastMidCellStyle.setBorderTop(BorderStyle.THIN);
        endLastMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle totalMidCellStyle = wk.createCellStyle();
        totalMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        totalMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalMidCellStyle.setWrapText(true);
        totalMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalMidCellStyle.setBorderLeft(BorderStyle.THIN);
        totalMidCellStyle.setBorderTop(BorderStyle.THIN);
        totalMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle totalRightCellStyle = wk.createCellStyle();
        totalRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalRightCellStyle.setWrapText(true);
        totalRightCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalRightCellStyle.setBorderLeft(BorderStyle.THIN);
        totalRightCellStyle.setBorderTop(BorderStyle.THIN);
        totalRightCellStyle.setBorderRight(BorderStyle.THIN);
        setCampaignName(sheet.getRow(0), universeActivityLogPo.getAudienceName());
        Integer startRow = sheet.getLastRowNum();
        JSONObject ruleJsonObject = JSONObject.parseObject(universeActivityLogPo.getAudienceRuleJson());
        Double testController = ruleJsonObject.getDouble("test-control");
        UniversePo universePo1 = universeService.getUniverseById(universeActivityLogPo.getDestinationId());
        JSONArray segmentCampaignArray = bitmapService
                .getCampaignInfoV2(Arrays.asList(universePo1), universeActivityLogPo.getAudienceRuleJson());
        List<JSONObject> campaignList = new ArrayList<>();
        for (Object object : segmentCampaignArray) {
            JSONObject segmentCampaignObject = JSONObject.parseObject(object.toString());
            campaignList.add(segmentCampaignObject);
        }
        JSONArray segmentArray = ruleJsonObject.getJSONArray(SEGMENTS);
        Integer segmentOrder = 0;
        Integer segmentSize = segmentArray.size();
        for (Object object : segmentArray) {
            JSONObject segmentObject = JSONObject.parseObject(object.toString());
            segmentOrder++;
            Row row = sheet.createRow(++startRow);
            Cell firstRowFirstCell = row.createCell(0);
            firstRowFirstCell.setCellValue(segmentOrder);
            Cell firstRowSecondCell = row.createCell(1);
            firstRowSecondCell.setCellValue(segmentObject.getString("name"));
            Cell ruleCell = row.createCell(2);
            if (segmentOrder == segmentSize) {
                setRule(ruleCell, segmentObject, endLeftCellStyle);
            } else {
                setRule(ruleCell, segmentObject, leftCellStyle);
            }
            JSONObject campaignInfo = campaignList.get(segmentOrder - 1);
            int rwsTemp = ruleCell.getStringCellValue().split("\n").length;
            row.setHeight((short) (rwsTemp * LINE_HEIGHT * 2));
            Cell allTestCountCell = row.createCell(3);
            allTestCountCell.setCellValue(
                    campaignInfo.getLong("allTestCount") > -1 ? getFormat(
                            campaignInfo.getLong("allTestCount")) : "\\");
            Cell testCountCell = row.createCell(4);
            testCountCell.setCellValue(campaignInfo.getLong("testCount") > -1 ? getFormat(
                    campaignInfo.getLong("testCount")) : "\\");
            Cell controlCountCell = row.createCell(5);
            controlCountCell.setCellValue(
                    campaignInfo.getLong("controlCount") > -1 ? getFormat(
                            campaignInfo.getLong("controlCount")) : "\\");
            Cell percentCell = row.createCell(6);
            percentCell.setCellValue(testController != 0 ? formatPercent(1 - testController) : "\\");
            if (segmentOrder == segmentSize) {
                firstRowFirstCell.setCellStyle(endMidCellStyle);
                firstRowSecondCell.setCellStyle(endMidCellStyle);
                testCountCell.setCellStyle(endRightCellStyle);
                allTestCountCell.setCellStyle(endRightCellStyle);
                controlCountCell.setCellStyle(endRightCellStyle);
                percentCell.setCellStyle(endLastMidCellStyle);
            } else {
                firstRowFirstCell.setCellStyle(midCellStyle);
                firstRowSecondCell.setCellStyle(midCellStyle);
                testCountCell.setCellStyle(rightCellStyle);
                allTestCountCell.setCellStyle(rightCellStyle);
                controlCountCell.setCellStyle(rightCellStyle);
                percentCell.setCellStyle(lastMidCellStyle);
            }
        }
        Row totalRow = sheet.createRow(++startRow);
        //set total count row
        setTotalInfo(totalRow, segmentCampaignArray, testController, totalMidCellStyle,
                totalRightCellStyle);
        //set extra info row
        setExtraInfo(sheet, startRow + 2, ruleJsonObject, universeActivityLogPo.getUpdateTime(),
                noThinRightCellStyle);
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(exportFile);
            wk.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            LogUtils.error(e);
            throw new AMSFileIOException();
        }
        return exportName;
    }

    private void setCampaignName(Row row, String campaignName) {
        Cell cell = row.getCell(1);
        String value = cell.getStringCellValue();
        value = value.replace("?", campaignName);
        cell.setCellValue(value);
    }

    private void setRule(Cell cell, JSONObject segmentObject, CellStyle cellStyle) {
        Boolean isFirst = true;
        JSONArray includeArray = segmentObject.getJSONArray(INCLUDE);
        JSONArray excludeArray = segmentObject.getJSONArray(EXCLUDE);
        StringBuilder newRule = new StringBuilder();
        if (includeArray.size() != 0) {
            newRule.append("INCLUDE \n");
            String rule = getRuleDisplay1(segmentObject.getJSONArray(INCLUDE), 1);
            newRule.append(rule);
        }
        if (excludeArray.size() != 0) {
            newRule.append("EXCLUDE \n");
            String rule = getRuleDisplay1(segmentObject.getJSONArray(EXCLUDE), 1);
            newRule.append(rule);
        }
        cell.setCellValue(new XSSFRichTextString(newRule.toString()));
        cell.setCellStyle(cellStyle);
    }

    private String getRuleDisplay1(JSONArray itemArray, Integer size) {
        StringBuilder rule = new StringBuilder();
        Boolean isFirstItem = true;
        for (Object itemObject : itemArray) {
            JSONObject itemJSONObject = JSONObject.parseObject(itemObject.toString());
            String value = getSelectDisplay(itemJSONObject);
            if (value != "") {
                if (!isFirstItem) {
                    rule.append(formatRule(itemJSONObject.getString("logic"), size, true));
                }
                isFirstItem = false;
                rule.append(formatRule(getSelectDisplay(itemJSONObject), size, false));
            }

            JSONArray childItemArray = itemJSONObject.getJSONArray("items");
            if (childItemArray != null && childItemArray.size() > 0) {
                String groupString = itemJSONObject.getString("origin");
                String logic = itemJSONObject.getString("logic");
                String newStr = "";
                for (int i = 0; i < logic.length(); i++) {
                    newStr += " ";
                }
                if ("group".equals(groupString) && !isFirstItem) {
                    rule.append(formatRule(logic, size, true));
                    rule.append("[");
                }
                rule.append(getRuleDisplay1(childItemArray, size));
                if ("group".equals(groupString) && !isFirstItem) {
                    rule.append(newStr + formatRule("]", size, true) + "\n");
                }
                isFirstItem = false;
            }
        }
        return rule.toString();
    }

    private String getSelectDisplay(JSONObject jsonObject) {
        String path = jsonObject.getString("name");
        String taxonomyId = "";
        JSONArray itemArray = jsonObject.getJSONArray(VALUES);
        String groupString = jsonObject.getString("origin");
        if ("group".equals(groupString)) {
            return "";
        }
        if (itemArray.size() == 0) {
            return "";
        }
        int flag = 0;
        for (Object item : itemArray) {
            JSONObject itemObject = JSONObject.parseObject(item.toString());
            if (Optional.ofNullable(itemObject.getString("node")).isPresent()) {
                taxonomyId = itemObject.getString("node");
            }
            if (Optional.ofNullable(itemObject.getString("name")).isPresent()) {
                if (flag == 0) {
                    path += " = ";
                }
                path += itemObject.getString("name") + ",";
                flag++;
            } else {
                path += " ";
            }
        }
        if ("3p".equals(jsonObject.getString("dataType")) && !taxonomyId.isEmpty() && taxonomyId.split("_").length == 3) {
            String[] taxonomyIds = taxonomyId.split("_");
            try {
                String tenantId = tenantService.getTenantById(Long.valueOf(taxonomyIds[0])).getTenantId();
                Integer datasourceId = Integer.valueOf(taxonomyIds[1]);
                path = dataSourceAPI.getParentPathByTaxonomyId(taxonomyId, tenantId, datasourceId) + " / " + path;
            } catch (AMSException e) {
                LogUtils.error(e);
            }
        }
        return path;
    }

    private String formatRule(String str, Integer size, Boolean isLogic) {
        if (StringUtils.equals(str, "")) {
            return "";
        }
        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < size; i++) {
            newStr.append(BLANK_STR);
        }
        if (isLogic) {
            newStr.append(StringUtils.upperCase(str));
        } else {
            newStr.append(str.substring(0, str.length() - 1));
            newStr.append("\n");
        }
        return newStr.toString();
    }

    private void setTotalInfo(Row totalRow, JSONArray segmentArray, Double testController,
                              CellStyle midCellStyle, CellStyle rightCellStyle) {
        Integer testCount = 0;
        Integer allTestCount = 0;
        Integer controlCount = 0;
        for (Object object : segmentArray) {
            JSONObject segmentObject = JSONObject.parseObject(object.toString());
            testCount += segmentObject.getInteger("testCount");
            allTestCount += segmentObject.getInteger("allTestCount");
            controlCount += segmentObject.getInteger("controlCount");
        }

        Cell fourthCell = totalRow.createCell(3);
        fourthCell.setCellValue(allTestCount > -1 ? getFormat(allTestCount) : "\\");
        fourthCell.setCellStyle(rightCellStyle);

        Cell fifthCell = totalRow.createCell(4);
        fifthCell.setCellValue(testCount > -1 ? getFormat(testCount) : "\\");
        fifthCell.setCellStyle(rightCellStyle);

        Cell sixCell = totalRow.createCell(5);
        sixCell.setCellValue(controlCount > -1 ? getFormat(controlCount) : "\\");
        sixCell.setCellStyle(rightCellStyle);

        Cell sevenCell = totalRow.createCell(6);
        sevenCell.setCellValue(testController != 0 ? formatPercent(1 - testController) : "\\");
        sevenCell.setCellStyle(midCellStyle);
    }

    private void setExtraInfo(XSSFSheet sheet, Integer startRow, JSONObject ruleJsonObject,
                              Date updateDate, CellStyle rightCellStyle) {
        Row firstRow = sheet.createRow(startRow++);
        firstRow.createCell(1).setCellValue("Addressable, Linear, or Both?");
        firstRow.createCell(2).setCellValue("Addressable");
        Row secondRow = sheet.createRow(startRow++);
        secondRow.createCell(1).setCellValue("De-duped?");
        Boolean isGross = ruleJsonObject.getBoolean("rm-duplicates");
        if (isGross) {
            secondRow.createCell(2).setCellValue("Yes");
        } else {
            secondRow.createCell(2).setCellValue("No");
        }
        Row thirdRow = sheet.createRow(startRow++);
        thirdRow.createCell(1).setCellValue("Cap:");
        Integer capCount = ruleJsonObject.getInteger("cap");
        if (capCount == 0) {
            thirdRow.createCell(2).setCellValue("No");
        } else {
            Cell thirdRowCell = thirdRow.createCell(2);
            thirdRowCell.setCellValue(getFormat(capCount));
            thirdRowCell.setCellStyle(rightCellStyle);
        }
        Row fourthRow = sheet.createRow(startRow++);
        fourthRow.createCell(1).setCellValue("Audience Update Date");
        fourthRow.createCell(2).setCellValue(getDateString(updateDate));
    }

    private String getZip(List<String> nameList) throws AMSFileIOException {
        byte[] buffer = new byte[1024];
        String zipPath = tempFile + "/audience_zip_" + System.currentTimeMillis() + ".zip";
        File tmpZip = new File(zipPath);
        try {
            if (!tmpZip.createNewFile()) {
                throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
            }
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
        }
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (String name : nameList) {
                File nameFile = new File(name);
                try (FileInputStream fis = new FileInputStream(nameFile)) {
                    out.putNextEntry(new ZipEntry(nameFile.getName()));
                    out.setEncoding("UTF-8");
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
                } finally {
                    out.closeEntry();
                    FileSystemUtils.deleteRecursively(nameFile);
                }

            }
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
        }
        return zipPath;
    }
}
