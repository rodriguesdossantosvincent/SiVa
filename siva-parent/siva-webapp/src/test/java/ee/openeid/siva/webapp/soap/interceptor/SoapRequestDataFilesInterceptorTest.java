/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp.soap.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SoapRequestDataFilesInterceptorTest {

    private static final String INVALID_REQUEST = "Invalid request";
    private static final int EXPECTED_STATUS_CODE = 400;
    private static final String EXPECTED_FAULT_CODE = "Client";
    private static final String INVALID_BASE64 = "Document is not encoded in a valid base64 string";
    private static final String INVALID_DOCUMENT_TYPE = "Invalid document type. Can only return data files for DDOC type containers.";

    @Mock
    private SoapMessage message;
    @Mock
    private SOAPMessage soapMessage;
    @Mock
    private SOAPPart soapPart;
    @Mock
    private SOAPEnvelope envelope;
    @Mock
    private SOAPBody body;
    @Mock
    private Node documentNode;
    @Mock
    private Node documentTypeNode;

    @Mock
    private SoapInterceptor mockSaajIn;

    @InjectMocks
    private SoapRequestDataFilesInterceptor dataFilesInterceptor = new SoapRequestDataFilesInterceptor();

    @Before
    public void setUp() {
        doNothing().when(mockSaajIn).handleMessage(any());
    }

    @Test
    public void whenSoapMessageIsNullThenFaultIsThrownWithInvalidRequestMessage() {
        doReturn(null).when(message).getContent(any());
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_REQUEST);
    }

    @Test
    public void whenDocumentTypeIsInvalidThenFaultIsThrownWithInvalidDocumentTypeMessage() throws SOAPException {
        mockSoapMessage("AABBBAA", "BDOC");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_DOCUMENT_TYPE);
    }

    @Test
    public void whenDocumentIsInvalidThenFaultIsThrownWithInvalidDocumentMessage() throws SOAPException {
        mockSoapMessage("ÖÄÜ", "DDOC");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_BASE64);
    }

    @Test
    public void noSoapFaultIsThrownWithValidRequest() throws SOAPException {
        mockSoapMessage("AABBBAA", "DDOC");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    private void assertFaultWithExpectedMessage(Fault soapFault, String message) {
        assertNotNull(soapFault);
        assertEquals(EXPECTED_FAULT_CODE, soapFault.getFaultCode().toString());
        assertTrue(EXPECTED_STATUS_CODE == soapFault.getStatusCode());
        assertEquals(message, soapFault.getMessage());
    }

    private void mockSoapMessage(String document, String documentType) throws SOAPException {
        doReturn(body).when(envelope).getBody();
        doReturn(envelope).when(soapPart).getEnvelope();
        doReturn(soapPart).when(soapMessage).getSOAPPart();
        doReturn(soapMessage).when(message).getContent(SOAPMessage.class);
        mockDocumentNode(document);
        mockDocumentTypeNode(documentType);
    }

    private void mockDocumentNode(String document) {
        mockNode(documentNode, "Document", document);
    }

    private void mockDocumentTypeNode(String documentType) {
        mockNode(documentTypeNode, "DocumentType", documentType);
    }

    private void mockNode(Node node, String tagName, String value) {
        NodeList nodeList = new SoapRequestValidationInterceptorTest.MockNodeList(node);
        doReturn(node).when(node).getFirstChild();
        doReturn(value).when(node).getNodeValue();
        doReturn(nodeList).when(body).getElementsByTagName(tagName);
    }

    private Fault handleMessageInInterceptor(SoapMessage soapMessage) {
        try {
            dataFilesInterceptor.handleMessage(soapMessage);
        } catch (Fault soapFault) {
            return soapFault;
        }
        return null;
    }

}