package com.acxiom.ams.util;

import com.acxiom.ams.model.dto.v2.ExportInsightDTO;
import com.acxiom.ams.model.dto.v2.InsightRule;
import com.acxiom.ams.model.dto.v2.Item;
import com.acxiom.ams.model.dto.v2.SegmentInfo;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 2:24 PM 9/7/2018
 */
public class ExcelUtils {

    private static final String TITLE_AUDIENCE = "Audience:";
    private static final String TITLE_TARGET_GROUP = "Target Group(s): ";
    private static final String HEADER_TARGET = "Target";
    private static final String HEADER_INDEX = "Index";
    private static final String TITLE_CONNECTOR = " - ";
    private static final String NUMBER_FORMAT = "#,##0";
    private static final String LINE_BREAK = "\r\n";

    private ExcelUtils(){}

    private static void createInsightTitleAndHeader(XSSFWorkbook workbook, XSSFSheet sheet, String title,
                                                    List<String> headerList, int height) {
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setWrapText(true);
        XSSFRow row = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        row.setHeightInPoints((height + 2) * 20);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(titleStyle);
        cell.setCellValue(title);
        XSSFRow titleRow = sheet.createRow(1);
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setFont(font);
        XSSFCell titleCell;
        for (int i = 0; i < headerList.size(); i++) {
            titleCell = titleRow.createCell(i);
            titleCell.setCellValue(headerList.get(i));
            titleCell.setCellStyle(headerStyle);
        }
    }

    public static void generateInsightExcel(ExportInsightDTO exportInsightDTO, File file) throws IOException {
        OutputStream out;
        StringBuilder title = new StringBuilder().append(TITLE_AUDIENCE).append(exportInsightDTO.getAudienceName())
                .append(LINE_BREAK)
                .append(TITLE_TARGET_GROUP)
                .append(LINE_BREAK);
        int height = exportInsightDTO.getSegmentInfoList().size();
        for (SegmentInfo segmentInfo : exportInsightDTO.getSegmentInfoList()) {
            title.append(segmentInfo.getSegmentName()).append(TITLE_CONNECTOR).append(segmentInfo.getSegmentCount()).append(LINE_BREAK);
        }
        title.delete(title.lastIndexOf(LINE_BREAK), title.length());
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFDataFormat format = workbook.createDataFormat();
        for (InsightRule insightRule : exportInsightDTO.getInsightRuleList()) {
            XSSFSheet sheet = workbook.createSheet(insightRule.getTitle());
            List<String> titleList = new ArrayList<>();
            titleList.add(insightRule.getTitle());
            titleList.add(HEADER_TARGET);
            titleList.add(HEADER_INDEX);
            createInsightTitleAndHeader(workbook, sheet, title.toString(), titleList, height);
            int rowNum = 2;
            XSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            XSSFCell cell;
            if(!Optional.ofNullable(insightRule.getItemList()).isPresent()){
                continue;
            }
            for (Item item : insightRule.getItemList()) {
                XSSFRow row = sheet.createRow(rowNum);
                cell = row.createCell(0);
                cell.setCellStyle(style);
                cell.setCellValue(item.getName());
                style.setDataFormat(format.getFormat(NUMBER_FORMAT));
                cell = row.createCell(1);
                cell.setCellStyle(style);
                cell.setCellValue(item.getTargetValue());
                cell = row.createCell(2);
                cell.setCellStyle(style);
                cell.setCellValue(item.getIndexValue());
                rowNum++;
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
        }
        out = new FileOutputStream(file.getPath());
        workbook.write(out);
        out.close();
        workbook.close();
    }
}
