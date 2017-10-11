package ee.openeid.siva.webapp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.signature.SignatureService;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.webapp.configuration.SivaWebApplicationConfigurationProperties;
import ee.openeid.siva.webapp.response.ValidationResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

/**
 * Alters the response by creating a signature from the existing response's body and adding the signature into the body.
 */
@RestControllerAdvice
public class ReportSignatureInterceptor implements ResponseBodyAdvice<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSignatureInterceptor.class);

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private SivaWebApplicationConfigurationProperties properties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object responseObject, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (properties.isReportSignatureEnabled()) {
            try {
                if (responseObject instanceof ValidationResponse && ((ValidationResponse) responseObject).getValidationReport() instanceof DetailedReport) {
                    LOGGER.debug("Starting to create report signature");
                    ValidationResponse validationResponse = (ValidationResponse) responseObject;
                    String validationReportJsonString = jacksonObjectMapper.writeValueAsString(validationResponse.getValidationReport());
                    byte[] reportSignatureBytes = signatureService.getSignature(validationReportJsonString.getBytes(), "validationReport.json", "application/json");
                    validationResponse.setValidationReportSignature(Base64.encodeBase64String(reportSignatureBytes));
                    LOGGER.debug("Finished creating report signature");
                    return validationResponse;
                }
            } catch (Exception e) {
                LOGGER.error("Error producing report signature", e);
                return responseObject;
            }
        }
        return responseObject;
    }

    public void setJacksonObjectMapper(ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    public void setProperties(SivaWebApplicationConfigurationProperties properties) {
        this.properties = properties;
    }

}