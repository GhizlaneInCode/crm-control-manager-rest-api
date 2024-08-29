package com.giantlink.project.helpers;

import com.giantlink.project.entities.Option;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class OptionExcelHelper {
    public static String TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String TYPE_CSV = "text/csv";
    static String[] HEADER = { "id", "optionname", "projectname"};

    public static int hasFormat(MultipartFile file) {
        if (TYPE_EXCEL.equals(file.getContentType())) {
            return 1;
        }else if(TYPE_CSV.equals(file.getContentType())){
            return 2;
        }
        return 0;
    }

    private static com.giantlink.glintranetdto.models.responses.ProjectResponse getProject(String name){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<com.giantlink.glintranetdto.models.responses.ProjectResponse> result = restTemplate.getForEntity("http://localhost:8091/api/project/name/"+name,com.giantlink.glintranetdto.models.responses.ProjectResponse.class );
        return result.getBody();
    }

    private static com.giantlink.glintranetdto.models.responses.ProjectResponse getProjectById(Long id){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<com.giantlink.glintranetdto.models.responses.ProjectResponse> result = restTemplate.getForEntity("http://localhost:8091/api/project/"+id, com.giantlink.glintranetdto.models.responses.ProjectResponse.class );
        return result.getBody();
    }

    public static List<Option> excelToOptions(InputStream is, String sheetNumber) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(Integer.parseInt(sheetNumber)-1);
            Iterator<Row> rows = sheet.iterator();
            List<Option> listOptions = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Option option = new Option();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 1:
                            option.setOptionName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            com.giantlink.glintranetdto.models.responses.ProjectResponse project = getProject(currentCell.getStringCellValue());
                            option.setProjectId(project.getId());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                listOptions.add(option);
            }
            workbook.close();
            return listOptions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static List<Option> csvToOptions(InputStream is) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withDelimiter(';'));
            List<Option> listOptions = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                Option option = new Option();
                option.setOptionName(csvRecord.get("optionname"));
                com.giantlink.glintranetdto.models.responses.ProjectResponse project = getProject(csvRecord.get("projectname"));
                option.setProjectId(project.getId());
                listOptions.add(option);
            }
            return listOptions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    // Write the list of entity to an excel file
    public static ByteArrayInputStream optionsToExcel(List<Option> options) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Options");

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
            for (Option option : options) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue((Long)option.getId());
                sheet.autoSizeColumn(0);
                row.createCell(1).setCellValue(option.getOptionName());
                sheet.autoSizeColumn(1);
                com.giantlink.glintranetdto.models.responses.ProjectResponse projectName = getProjectById((Long)option.getProjectId());
                row.createCell(2).setCellValue(projectName.getName());
                sheet.autoSizeColumn(2);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }


    public static ByteArrayInputStream optionsToCsv(List<Option> options) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withDelimiter(';'));

            // Header
            csvPrinter.printRecord(HEADER);
            for (Option option : options) {
                com.giantlink.glintranetdto.models.responses.ProjectResponse projectName = getProjectById((Long)option.getProjectId());
                List<String> data = Arrays.asList(
                        String.valueOf(option.getId()),
                        option.getOptionName(),
                        projectName.getName()
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
