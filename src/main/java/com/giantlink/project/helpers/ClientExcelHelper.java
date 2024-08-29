package com.giantlink.project.helpers;

import com.giantlink.project.entities.Client;
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

public class ClientExcelHelper {
    public static String TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String TYPE_CSV = "text/csv";
    static String[] HEADER = { "id", "firstname", "lastname", "gsm" ,"tele" , "email" , "city" , "cp" , "address"};


    // function to check if the file is an excel/csv file
    public static int hasFormat(MultipartFile file) {
        if (TYPE_EXCEL.equals(file.getContentType())) {
            return 1;
        }else if(TYPE_CSV.equals(file.getContentType())){
            return 2;
        }
        return 0;
    }

    // Read the excel file and return a list of entity
    public static List<Client> excelToClients(InputStream is, String sheetNumber) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(Integer.parseInt(sheetNumber)-1);
            Iterator<Row> rows = sheet.iterator();

            List<Client> listClients = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Client client = new Client();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 1:
                            client.setFirstName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            client.setLastName(currentCell.getStringCellValue());
                            break;
                        case 3:
                            if(currentCell.getCellType() == CellType.NUMERIC) {
                                client.setGsm(String.valueOf((int)currentCell.getNumericCellValue()));
                            } else {
                                client.setGsm(currentCell.getStringCellValue());
                            }
                            break;
                        case 4:
                            if(currentCell.getCellType() == CellType.NUMERIC) {
                                client.setTele(String.valueOf((int)currentCell.getNumericCellValue()));
                            } else {
                                client.setTele(currentCell.getStringCellValue());
                            }
                            break;
                        case 5:
                            client.setEmail(currentCell.getStringCellValue());
                            break;
                        case 6:
                            client.setCity(currentCell.getStringCellValue());
                            break;
                        case 7:
                            if(currentCell.getCellType() == CellType.NUMERIC) {
                                client.setCp(String.valueOf((int)currentCell.getNumericCellValue()));
                            } else {
                                client.setCp(currentCell.getStringCellValue());
                            }
                            break;
                        case 8:
                            client.setAdress(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                listClients.add(client);
            }
            workbook.close();
            return listClients;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    // Write the list of entity to an excel file
    public static ByteArrayInputStream clientsToExcel(List<Client> clients) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Clients");

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
            for (Client client : clients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue((Long)client.getId());
                sheet.autoSizeColumn(0);
                row.createCell(1).setCellValue(client.getFirstName());
                sheet.autoSizeColumn(1);
                row.createCell(2).setCellValue(client.getLastName());
                sheet.autoSizeColumn(2);
                row.createCell(3).setCellValue(client.getGsm());
                sheet.autoSizeColumn(3);
                row.createCell(4).setCellValue(client.getTele());
                sheet.autoSizeColumn(4);
                row.createCell(5).setCellValue(client.getEmail());
                sheet.autoSizeColumn(5);
                row.createCell(6).setCellValue(client.getCity());
                sheet.autoSizeColumn(6);
                row.createCell(7).setCellValue(client.getCp());
                sheet.autoSizeColumn(7);
                row.createCell(8).setCellValue(client.getAdress());
                sheet.autoSizeColumn(8);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }



    // Read the csv file and return a list of clients
    public static List<Client> csvToClients(InputStream is) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withDelimiter(';'));
            List<Client> clients = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                Client client = new Client();
                client.setFirstName(csvRecord.get("firstname"));
                client.setLastName(csvRecord.get("lastname"));
                client.setGsm(csvRecord.get("gsm"));
                client.setTele(csvRecord.get("tele"));
                client.setEmail(csvRecord.get("email"));
                client.setCity(csvRecord.get("city"));
                client.setCp(csvRecord.get("cp"));
                client.setAdress(csvRecord.get("address"));
                clients.add(client);
            }
            return clients;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    // Write the list of entity to a csv file
    public static ByteArrayInputStream clientsToCsv(List<Client> clients) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withDelimiter(';'));

            // Header
            csvPrinter.printRecord(HEADER);
            for (Client client : clients) {
                List<String> data = Arrays.asList(
                        String.valueOf(client.getId()),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getGsm(),
                        client.getTele(),
                        client.getEmail(),
                        client.getCity(),
                        client.getCp(),
                        client.getAdress()
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
