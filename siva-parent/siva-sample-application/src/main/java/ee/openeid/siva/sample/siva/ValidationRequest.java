package ee.openeid.siva.sample.siva;

import lombok.Data;

@Data
class ValidationRequest {
    private ReportType reportType = ReportType.SIMPLE;
    private FileType documentType;
    private String filename;
    private String document;
}