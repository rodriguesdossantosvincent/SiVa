package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.siva.FileType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationRequestUtils {
    private static final String FILENAME_EXTENSION_SEPARATOR = ".";
    private static final String XROAD_XSD = "http://x-road.eu/xsd/xroad.xsd";
    private static final String UNIQUE_XROAD_ASICE_FILE = "message.xml";

    public static FileType getValidationServiceType(UploadedFile validationRequest) throws IOException {
        final String filename = validationRequest.getFilename();
        FileType parsedFileType = parseFileExtension(filename.substring(filename.lastIndexOf(FILENAME_EXTENSION_SEPARATOR) + 1));

        if (isAsiceFileExtension(parsedFileType)) {
            parsedFileType = FileType.BDOC;
        }

        if (isXroadAsiceContainer(validationRequest)) {
            parsedFileType = FileType.XROAD;
        }

        return parsedFileType;
    }

    static boolean isXroadAsiceContainer(UploadedFile validationRequest) throws IOException {
        String document = validationRequest.getEncodedFile();
        if (document == null) {
            return false;
        }

        try (InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(document.getBytes()))) {
            byte[] fileContents = ZipUtil.unpackEntry(stream, UNIQUE_XROAD_ASICE_FILE);
            if (fileContents == null) {
                return false;
            }

            String messageFile = new String(fileContents);
            return messageFile.contains(XROAD_XSD);
        }
    }

    static FileType parseFileExtension(String fileExtension) {
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
    }

    private static boolean isAsiceFileExtension(final FileType foundMatch) {
        return foundMatch != null && foundMatch == FileType.ASICE;
    }
}
