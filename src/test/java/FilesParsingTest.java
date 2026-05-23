import com.opencsv.CSVReader;
import model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;


public class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @Test
    void pdfFileParsingTest() throws Exception {

        try (ZipInputStream zis =
                     new ZipInputStream(cl.getResourceAsStream("testzip.zip"))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().equals("pdf_file.pdf")) {

                    byte[] pdfBytes = zis.readAllBytes();

                    try (PDDocument document =
                                 PDDocument.load(pdfBytes)) {

                        String pdfText =
                                new PDFTextStripper().getText(document);

                        assertTrue(pdfText.contains("Извещение о дорожно-транспортном происшествии"));
                    }
                }
            }
        }
    }


    @Test
    void xlsxFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("testzip.zip")
        )) {
            ZipEntry entry = zis.getNextEntry();

            assertNotNull(entry);

            boolean hasRequiredFile = false;

            do {

                String[] fileData = entry.getName().split("\\.");
                String fileType = fileData[fileData.length - 1];

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                byte[] fileBytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);

                if (!"xlsx".equals(fileType)) {
                    continue;
                }
                hasRequiredFile = true;
                try (Workbook workbook = WorkbookFactory.create(bais)) {


                    Sheet sheet = workbook.getSheet("Payments");
                    Row row = sheet.getRow(1);
                    Cell cell = row.getCell(3);

                    String value = cell.getStringCellValue();

                    assertEquals("42301810158742897803", value);
                }
            } while ((entry = zis.getNextEntry()) != null);
            assertTrue(hasRequiredFile);
        }
    }

    @Test
    void csvFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("testzip.zip")
        )) {
            ZipEntry entry = zis.getNextEntry();

            assertNotNull(entry);
            boolean hasRequiredFile = false;
            do {

                String[] fileData = entry.getName().split("\\.");
                String fileType = fileData[fileData.length - 1];

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                byte[] fileBytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);

                if (!"csv".equals(fileType)) {
                    continue;
                }
                hasRequiredFile = true;
                try (CSVReader csvReader = new CSVReader(new InputStreamReader(bais))) {

                    List<String[]> data = csvReader.readAll();
                    assertEquals(3, data.size());
                    Assertions.assertArrayEquals(
                            new String[]{"January", "first month of the year"},
                            data.get(0)
                    );
                    Assertions.assertArrayEquals(
                            new String[]{"February", "second month of the year"},
                            data.get(1)
                    );
                    Assertions.assertArrayEquals(
                            new String[]{"March", "third month of the year"},
                            data.get(2)
                    );
                }
            } while ((entry = zis.getNextEntry()) != null);
            assertTrue(hasRequiredFile);

        }
    }

    @Test
    void jsonFileParsingTest() throws Exception {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("user.json");

        assertNotNull(inputStream, "user.json не найден в resources!");

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(inputStream, User.class);

        assertEquals("TionBail", user.getUser());
        assertEquals(1, user.getId());
        assertEquals("Ivan Petrov", user.getName());

        assertEquals("ivan.petrov@test.com",
                user.getItems().getEmail());

        assertEquals("RU",
                user.getItems().getLanguage());


    }
}
