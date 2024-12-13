package goodpartner.chat.soap;

import goodpartner.chat.entity.Chat;
import goodpartner.chat.entity.Keyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoapClient {

    @Value("${open_api.key}")
    private String openApiKey;

    @Value("${open_api.url}")
    private String openApiUrl;

    private final RestClient restClient = RestClient.create();

    public List<Keyword> searchByKeyword(String keyword, Chat chat) throws Exception {
        String requestXml = buildSoapRequest(keyword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/xml; charset=utf-8")); // Content-Type 설정
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);

        String responseXml = restClient.post()
                .uri(openApiUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestXml)
                .retrieve()
                .body(String.class);

        return parseResponse(responseXml, chat);
    }

    private String buildSoapRequest(String keyword) {
        return """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:head="http://apache.org/headers" xmlns:open="http://openapi.affis.go.kr">
               <soapenv:Header>
                  <head:ComMsgHeader>
                     <ServiceKey>%s</ServiceKey>
                  </head:ComMsgHeader>
               </soapenv:Header>
               <soapenv:Body>
                  <open:getSearchAllKeywordList>
                     <SearchAllKeywordListRequest>
                        <nowPageNo>1</nowPageNo>
                        <pageMg>10</pageMg>
                        <chkClsno1>1</chkClsno1>
                        <chkClsno8>8</chkClsno8>
                        <txtQuery>%s</txtQuery>
                     </SearchAllKeywordListRequest>
                  </open:getSearchAllKeywordList>
               </soapenv:Body>
            </soapenv:Envelope>
        """.formatted(openApiKey, keyword);
    }

    private List<Keyword> parseResponse(String responseXml, Chat chat) throws Exception {
        List<Keyword> results = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(responseXml.getBytes("UTF-8")));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        XPathExpression expr = xPath.compile("//SearchAskNoticeListItem");
        NodeList itemList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < itemList.getLength(); i++) {
            Element itemElement = (Element) itemList.item(i);

            String csmTtl = getTagValue("csmTtl", itemElement);
            String titleLink = getTagValue("titleLink", itemElement);

            if (csmTtl != null && titleLink != null) {
                results.add(Keyword.of(csmTtl, titleLink, chat));
            }
        }

        return results;
    }

    private String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList == null || nodeList.getLength() == 0) return null;
        return nodeList.item(0).getTextContent();
    }
}
