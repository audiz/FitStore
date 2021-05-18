package ru.otus.fitstore.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Класс ArchiveServiceTest")
public class ArchiveServiceTest {

    ArchiveService archiveService = new ArchiveService();

    @Test
    void testZipUnzip() {

        String zipObject = "Hello world";
        byte[] bytes = archiveService.zip(zipObject);
        String unzippedObject = archiveService.unzip(bytes);
        assertEquals(zipObject, unzippedObject);
    }
}
