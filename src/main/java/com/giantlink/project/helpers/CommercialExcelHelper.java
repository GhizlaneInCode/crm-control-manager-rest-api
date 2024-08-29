package com.giantlink.project.helpers;

import com.giantlink.project.entities.Commercial;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommercialExcelHelper {
    public static String TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String TYPE_CSV = "text/csv";
    static String[] HEADER = { "id", "commercial_name","statut"};

    public static int hasFormat(MultipartFile file) {
        if (TYPE_EXCEL.equals(file.getContentType())) {
            return 1;
        }else if(TYPE_CSV.equals(file.getContentType())){
            return 2;
        }
        return 0;
    }

    public static List<Commercial> excelToCommercial(InputStream is, String sheetNumber) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(Integer.parseInt(sheetNumber)-1);
            Iterator<Row> rows = sheet.iterator();
            List<Commercial> listCommercial = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Commercial commercial = new Commercial();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 1:
                            commercial.setCommercialName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            if((int)currentCell.getNumericCellValue() == 1){
                                commercial.setStatut(true);
                            }else if((int)currentCell.getNumericCellValue() == 0){
                                commercial.setStatut(false);
                            }
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                listCommercial.add(commercial);
            }
            workbook.close();
            return listCommercial;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static List<Commercial> csvToCommercial(InputStream is) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withDelimiter(';'));
            List<Commercial> listCommercial = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                Commercial commercial = new Commercial();
                commercial.setCommercialName(csvRecord.get("commercial_name"));
                if (csvRecord.get("statut").equals("1")) {
                    commercial.setStatut(true);
                } else if (csvRecord.get("statut").equals("0")) {
                    commercial.setStatut(false);
                }
                listCommercial.add(commercial);
            }
            return listCommercial;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    // Write the list of entity to an excel file
    public static ByteArrayInputStream CommercialToExcel(List<Commercial> commercials) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Commercial");

            // Header
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setBold(true);
            font.setFontHeight(13);
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setFont(font);


            for (int col = 0; col < HEADER.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADER[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (Commercial commercial : commercials) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue((Long)commercial.getId());
                sheet.autoSizeColumn(0);
                row.createCell(1).setCellValue(commercial.getCommercialName());
                sheet.autoSizeColumn(1);
                row.createCell(2).setCellValue(commercial.getStatut()?1:0);
                sheet.autoSizeColumn(2);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }


    public static ByteArrayInputStream CommercialToCsv(List<Commercial> commercials) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withDelimiter(';'));

            // Header
            csvPrinter.printRecord(HEADER);
            for (Commercial commercial : commercials) {
                List<String> data = Arrays.asList(
                        String.valueOf(commercial.getId()),
                        commercial.getCommercialName(),
                        String.valueOf(commercial.getStatut())
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
