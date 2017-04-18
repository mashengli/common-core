package pers.mashengli.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author mashengli
 */
public class POIUtil {

    /**
     * 功能：拷贝sheet
     *
     * @param sourceSheet 源表格
     * @param targetSheet 目标表格
     * @param sourceWork 原工作簿
     * @param targetWork 目标工作簿
     */
    public static void copySheet(HSSFSheet sourceSheet, HSSFSheet targetSheet,
                                 HSSFWorkbook sourceWork, HSSFWorkbook targetWork) throws Exception {
        if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException("调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
        }

        //复制源表中的行
        int maxColumnNum = 0;

        Map styleMap = new HashMap();

        HSSFPatriarch patriarch = targetSheet.createDrawingPatriarch(); //用于复制注释
        for (int i = sourceSheet.getFirstRowNum(); i <= sourceSheet.getLastRowNum(); i++) {
            HSSFRow sourceRow = sourceSheet.getRow(i);
            HSSFRow targetRow = targetSheet.createRow(i);

            if (sourceRow != null) {
                copyRow(sourceRow, targetRow, sourceWork, targetWork, patriarch, styleMap);
                if (sourceRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = sourceRow.getLastCellNum();
                }
            }
        }

        //复制源表中的合并单元格
        mergerRegion(sourceSheet, targetSheet);

        //设置目标sheet的列宽
        for (int i = 0; i <= maxColumnNum; i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    /**
     * 功能：拷贝row
     *
     * @param sourceRow 源行数据
     * @param targetRow 目标行数据
     * @param sourceWork 源工作簿
     * @param targetWork 目标工作簿
     * @param targetPatriarch 目标
     * @param styleMap 样式
     */
    public static void copyRow(HSSFRow sourceRow, HSSFRow targetRow,
                               HSSFWorkbook sourceWork, HSSFWorkbook targetWork, HSSFPatriarch targetPatriarch, Map styleMap) throws Exception {
        if (targetRow == null || sourceRow == null || targetWork == null || sourceWork == null || targetPatriarch == null) {
            throw new IllegalArgumentException("调用PoiUtil.copyRow()方法时，targetRow、sourceRow、targetWork、sourceWork、targetPatriarch都不能为空，故抛出该异常！");
        }

        //设置行高
        targetRow.setHeight(sourceRow.getHeight());

        for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
            HSSFCell sourceCell = sourceRow.getCell(i);
            HSSFCell targetCell = targetRow.getCell(i);

            if (sourceCell != null) {
                if (targetCell == null) {
                    targetCell = targetRow.createCell(i);
                }

                //拷贝单元格，包括内容和样式
                copyCell(sourceCell, targetCell, sourceWork, targetWork, styleMap);

                //拷贝单元格注释
                copyComment(sourceCell, targetCell, targetPatriarch);
            }
        }
    }

    /**
     * 功能：拷贝cell，依据styleMap是否为空判断是否拷贝单元格样式
     *
     * @param sourceCell 源单元格
     * @param targetCell 目标单元格
     * @param sourceWork 源工作簿
     * @param targetWork 目标工作簿
     * @param styleMap 样式
     */
    public static void copyCell(HSSFCell sourceCell, HSSFCell targetCell, HSSFWorkbook sourceWork, HSSFWorkbook targetWork, Map styleMap) {
        if (targetCell == null || sourceCell == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException("调用PoiUtil.copyCell()方法时，targetCell、sourceCell、targetWork、sourceWork都不能为空，故抛出该异常！");
        }

        //处理单元格样式
        if (styleMap != null) {
            if (targetWork == sourceWork) {
                targetCell.setCellStyle(sourceCell.getCellStyle());
            } else {
                String stHashCode = "" + sourceCell.getCellStyle().hashCode();
                HSSFCellStyle targetCellStyle = (HSSFCellStyle) styleMap.get(stHashCode);
                if (targetCellStyle == null) {
                    targetCellStyle = targetWork.createCellStyle();
                    targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                    styleMap.put(stHashCode, targetCellStyle);
                }

                targetCell.setCellStyle(targetCellStyle);
            }
        }

        //处理单元格内容
        switch (sourceCell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                targetCell.setCellValue(sourceCell.getRichStringCellValue());
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                targetCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    /**
     * 功能：拷贝comment
     *
     * @param sourceCell 源单元格
     * @param targetCell 目标单元格
     * @param targetPatriarch 目标
     * @throws Exception
     */
    public static void copyComment(HSSFCell sourceCell, HSSFCell targetCell, HSSFPatriarch targetPatriarch) throws Exception {
        if (targetCell == null || sourceCell == null || targetPatriarch == null) {
            throw new IllegalArgumentException("调用PoiUtil.copyCommentr()方法时，targetCell、sourceCell、targetPatriarch都不能为空，故抛出该异常！");
        }

        //处理单元格注释
        HSSFComment comment = sourceCell.getCellComment();
        if (comment != null) {
            HSSFComment newComment = targetPatriarch.createComment(new HSSFClientAnchor());
            newComment.setAuthor(comment.getAuthor());
            newComment.setColumn(comment.getColumn());
            newComment.setFillColor(comment.getFillColor());
            newComment.setHorizontalAlignment(comment.getHorizontalAlignment());
            newComment.setLineStyle(comment.getLineStyle());
            newComment.setLineStyleColor(comment.getLineStyleColor());
            newComment.setLineWidth(comment.getLineWidth());
            newComment.setMarginBottom(comment.getMarginBottom());
            newComment.setMarginLeft(comment.getMarginLeft());
            newComment.setMarginTop(comment.getMarginTop());
            newComment.setMarginRight(comment.getMarginRight());
            newComment.setNoFill(comment.isNoFill());
            newComment.setRow(comment.getRow());
            newComment.setString(comment.getString());
            newComment.setVerticalAlignment(comment.getVerticalAlignment());
            newComment.setVisible(comment.isVisible());
            targetCell.setCellComment(newComment);
        }
    }

    /**
     * 功能：复制原有sheet的合并单元格到新创建的sheet
     *
     * @param sourceSheet 源单元格
     * @param targetSheet 目标单元格
     */
    public static void mergerRegion(HSSFSheet sourceSheet, HSSFSheet targetSheet) throws Exception {
        if (targetSheet == null || sourceSheet == null) {
            throw new IllegalArgumentException("调用PoiUtil.mergerRegion()方法时，targetSheet或者sourceSheet不能为空，故抛出该异常！");
        }

        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress oldRange = sourceSheet.getMergedRegion(i);
            CellRangeAddress newRange = new CellRangeAddress(
                    oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());
            targetSheet.addMergedRegion(newRange);
        }
    }

    /**
     * 功能：重新定义HSSFColor.PINK的色值
     *
     * @param workbook 工作簿
     * @return
     */

    public static HSSFColor setMBorderColor(HSSFWorkbook workbook) {
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;
        byte[] rgb = {(byte) 0, (byte) 128, (byte) 192};
        try {
            hssfColor = palette.findColor(rgb[0], rgb[1], rgb[2]);
            if (hssfColor == null) {
                palette.setColorAtIndex(HSSFColor.PINK.index, rgb[0], rgb[1],
                        rgb[2]);
                hssfColor = palette.getColor(HSSFColor.PINK.index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hssfColor;
    }
}
