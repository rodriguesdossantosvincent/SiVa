package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XROADValidationServiceTest {

    private static final String XROAD_SIMPLE = "xroad-simple.asice";
    private static final String XROAD_BATCHSIGNATURE = "xroad-batchsignature.asice";

    private XROADValidationServiceProperties properties;
    private XROADValidationService validationService;

    @Before
    public void setUp() {
        properties = new XROADValidationServiceProperties();
        properties.setDefaultPath();
        validationService = new XROADValidationService();
        validationService.setProperties(properties);
        validationService.loadXroadConfigurationDirectory();
    }

    @Test
    @Ignore
    public void ValidatingXRoadSimpleContainerShouldHaveOnlyTheCNFieldOfTheSingersCerificateAsSignedByFieldInQualifiedReport() throws Exception {
        QualifiedReport report = validationService.validateDocument(buildValidationDocument(XROAD_SIMPLE));
        assertEquals("Riigi Infosüsteemi Amet", report.getSignatures().get(0).getSignedBy());
    }

    @Test
    @Ignore
    public void validationReportForXroadBatchSignatureShouldHaveCorrectSignatureForm() throws Exception {
        QualifiedReport report = validationService.validateDocument(buildValidationDocument(XROAD_BATCHSIGNATURE));
        assertEquals("ASiC_E_batchsignature", report.getSignatures().get(0).getSignatureForm());
    }

    private ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(testFile)
                .withName(testFile)
                .build();
    }
}
