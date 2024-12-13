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

        log.info("responseXml: {}", responseXml);

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

        // XPath 수정: 올바른 태그 경로 지정
        XPathExpression expr = xPath.compile("//SearchLifeAreaListItem");
        NodeList itemList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        if (itemList == null || itemList.getLength() == 0) {
            log.warn("XPath로 검색된 데이터가 없습니다.");
            return results;
        }

        for (int i = 0; i < itemList.getLength(); i++) {
            Element itemElement = (Element) itemList.item(i);

            String csmTtl = getTagValue("csmTtl", itemElement); // 제목
            String titleLink = getTagValue("titleLink", itemElement); // 링크

            if (csmTtl == null || titleLink == null) {
                log.warn("csmTtl 또는 titleLink 값이 없습니다. 항목을 스킵합니다.");
                continue;
            }

            results.add(Keyword.of(csmTtl, titleLink, chat));
        }

        log.info("파싱된 키워드 결과: {}", results);
        return results;
    }

    private String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList == null || nodeList.getLength() == 0) return null;
        return nodeList.item(0).getTextContent();
    }
}
