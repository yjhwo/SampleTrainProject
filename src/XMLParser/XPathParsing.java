package XMLParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * 열차정보서비스 API - XPath Parsing
 */

public class XPathParsing {
	public static ArrayList<String> depTrain = 
			new ArrayList<String>(Arrays.asList("NAT041595"));		//출발 지역의 열차
	public static ArrayList<String> arrTrain = 
			new ArrayList<String>(Arrays.asList("NAT041866","NAT041993"));		//도착 지역의 열차
	
	private static String DEP_PLACEID = "NAT881014"; // 출발지 ID(필수)
	private static String ARR_PLACEID = "NAT882034"; // 도착지 ID(필수)
	
	private static String DEP_PLAND_TIME = "20160715"; // 출발일(필수)
	private static String CATEGORY = "0";	//요일 분류 카테고리
	/*
	 * 월~일 : 0, 월~금 : 1, 금토일:2, 금토:3, 토일:4, 토 :5, 일:6
	 */
	//private static String TRAIN_GRADE_CODE = "02"; // 차량종류코드(선택) (KTX-00)
	/*
	 * 내일로 가능 열차 새마을 : 01, 무궁화 : 02, 통근열차 : 03, ITX-새마을 : 08, ITX-청춘 : 09
	 */
	

	public static void main(String[] args) throws Exception {
	
		// 1. list
		//xmlToList();
		
		// 2. default
		//xmlDefault();
	
	}

	
	
	private static void xmlDefault() throws Exception {	// 기본
		String url = URLBuilder() + "";
		xmlParsing(url);

		
	}

	private static void xmlToList() throws Exception {	// list 이용
		if (depTrain != null && depTrain.size() > 0 && arrTrain != null && arrTrain.size() > 0) {
			for(int i=0; i<depTrain.size(); i++){
				for(int j=0; j<arrTrain.size(); j++){
					String url = URLBuilder(depTrain.get(i), arrTrain.get(j))+"";
					xmlParsing(url);
				}
			}
		} // if end
	}


	private static void dbInsert(ArrayList<String> list) {		// DB에 넣는 작업
		StringBuilder urlBuilder = new StringBuilder("http://115.68.116.235/aradongbros/insertTrainDB.php"); /* URL */ 

		try {
			urlBuilder.append("?" + URLEncoder.encode("depPlaceID", "UTF-8") + "=" + URLEncoder.encode(DEP_PLACEID, "UTF-8")); /* 출발기차역ID */
			urlBuilder.append("&" + URLEncoder.encode("arrPlaceID", "UTF-8") + "=" + URLEncoder.encode(ARR_PLACEID, "UTF-8")); /* 도착기차역ID */
			urlBuilder.append("&" + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(CATEGORY, "UTF-8")); /* 요일구분 카테고리 */
			
			urlBuilder.append("&" + URLEncoder.encode("trainGradeName", "UTF-8") + "=" + URLEncoder.encode(list.get(5), "UTF-8")); /* 차량종류코드 */
			urlBuilder.append("&" + URLEncoder.encode("adultcharge", "UTF-8") + "=" + URLEncoder.encode(list.get(0), "UTF-8")); /* 운임 */
			urlBuilder.append("&" + URLEncoder.encode("arrPlaceName", "UTF-8") + "=" + URLEncoder.encode(list.get(1), "UTF-8")); /* 도착지 */
			urlBuilder.append("&" + URLEncoder.encode("arrPlandTime", "UTF-8") + "=" + URLEncoder.encode(list.get(2), "UTF-8")); /* 도착시간 */
			urlBuilder.append("&" + URLEncoder.encode("depPlaceName", "UTF-8") + "=" + URLEncoder.encode(list.get(3), "UTF-8")); /* 출발지 */
			urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(list.get(4), "UTF-8")); 	/* 출발시간 */
			
			URL url = new URL(urlBuilder.toString());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        System.out.println("Response code: " + conn.getResponseCode());
			
			//System.out.println("url:"+url);
	        conn.disconnect();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*	0		1	2		3	4		5
		 * [28600, 부산, 112300, 서울, 055000, 02]*/
	}


	private static void xmlParsing(String url)	throws Exception {

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);			// XML Document 객체 생성
		XPath xpath = XPathFactory.newInstance().newXPath();			// xpath 생성

		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> trainGradeList = new ArrayList<String>(
				Arrays.asList("01", "02", "03","08","09"));
		
		NodeList item = (NodeList) xpath.evaluate("//*/item", document, XPathConstants.NODESET);			// 모든 item 값을 가져오기
		
		System.out.print("item.getLength():"+item.getLength());
		
		if(item.getLength()==0){
			System.out.println(" "
					+ ""
					+ "조회 결과가 없습니다. ");
			return;
		}
		
		for (int idx = 0; idx < item.getLength(); idx++) {

			list.clear();	// 초기화
			
			NodeList nodeList = item.item(idx).getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				if(i == 2){
					list.add(nodeList.item(i).getTextContent().substring(8));
					continue;
				}else if(i == 4){
					String depTime = nodeList.item(i).getTextContent();		// 출발 요일
					list.add(depTime.substring(8));		// 출발 시각
				//	list.add(CATEGORY);		// category
					continue;
				}
				list.add(nodeList.item(i).getTextContent());
			}
			
			// 내일로 가능열차만 DB insert
			String trainGrade = list.get(list.size()-1);
			//System.out.println("열차번호:"+trainGrade);
			for(int j=0; j<trainGradeList.size(); j++){
				if( trainGradeList.get(j).equals(trainGrade) ){
					
					dbInsert(list);	// db에 넣는 작업
					System.out.println(list.toString());
				}
			}
			
		}
		
		/*0	  1   		2		3   	4		5
		28600 부산 20160713112300 서울 20160713055000 02 
		28600 부산 20160713120200 서울 20160713064400 02 
		28600 부산 20160713124100 서울 20160713072000 02 
		*/
	}

	public static URL URLBuilder(String dep, String arr){   // URL 만드는 작업 - list 사용
		StringBuilder urlBuilder = new StringBuilder(
				"http://openapi.tago.go.kr/openapi/service/TrainInfoService/getStrtpntAlocFndTrainInfo"); /* URL */
		String DEP_PLACEID = dep;
        String ARR_PLACEID = arr;

		try {
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=1lNUnb4O91tryLgoWS3mvwA2F44IzPR2BNEc0H14ApZEPmJkoyGXqeYYQN85m%2BvPZdqGZ094DiM01Yj6ym389Q%3D%3D"); /* Service Key */
			urlBuilder.append("&" + URLEncoder.encode("depPlaceId","UTF-8") + "=" + URLEncoder.encode(DEP_PLACEID, "UTF-8")); /*출발지ID*/
            urlBuilder.append("&" + URLEncoder.encode("arrPlaceId","UTF-8") + "=" + URLEncoder.encode(ARR_PLACEID, "UTF-8")); /*도착지ID*/
			urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(DEP_PLAND_TIME, "UTF-8")); /* 출발일 */
			//urlBuilder.append("&" + URLEncoder.encode("trainGradeCode", "UTF-8") + "=" + URLEncoder.encode(TRAIN_GRADE_CODE, "UTF-8")); /* 차량종류코드 */

			urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); /* 검색건수 */
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지 번호 */

			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());

			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static URL URLBuilder() { // URL 만드는 작업 - default
		StringBuilder urlBuilder = new StringBuilder(
				"http://openapi.tago.go.kr/openapi/service/TrainInfoService/getStrtpntAlocFndTrainInfo"); /* URL */

		try {
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=FYPMcB84UK5jn7XCnYqm1lJMqpuKakFgS4k3TII0xsef95IkojvpIVNDfBV6QQxm%2B9Dm9c7O%2FcIha6glL%2FVrEw%3D%3D"); /* Service Key */
			urlBuilder.append("&" + URLEncoder.encode("depPlaceId", "UTF-8") + "=" + URLEncoder.encode(DEP_PLACEID, "UTF-8")); /* 출발지ID */
			urlBuilder.append("&" + URLEncoder.encode("arrPlaceId", "UTF-8") + "=" + URLEncoder.encode(ARR_PLACEID, "UTF-8")); /* 도착지ID */
			urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(DEP_PLAND_TIME, "UTF-8")); /* 출발일 */
			//urlBuilder.append("&" + URLEncoder.encode("trainGradeCode", "UTF-8") + "=" + URLEncoder.encode(TRAIN_GRADE_CODE, "UTF-8")); /* 차량종류코드 */

			urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); /* 검색건수 */
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지 번호 */

			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());

			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

/*
 <response>
<header>
<resultCode>00</resultCode>
<resultMsg>NORMAL SERVICE.</resultMsg>
</header>
<body>
	<items>
		<item>
		<adultcharge>28600</adultcharge>
		<arrplacename>부산</arrplacename>
		<arrplandtime>20160713112300</arrplandtime>
		<depplacename>서울</depplacename>
		<depplandtime>20160713055000</depplandtime>
		<traingradename>02</traingradename>
		</item>
		<item>
		<adultcharge>28600</adultcharge>
		<arrplacename>부산</arrplacename>
		<arrplandtime>20160713120200</arrplandtime>
		<depplacename>서울</depplacename>
		<depplandtime>20160713064400</depplandtime>
		<traingradename>02</traingradename>
		</item>
	</items>
	<numOfRows>999</numOfRows>
	<pageNo>1</pageNo>
	<totalCount>17</totalCount>
</body>
</response>
 */

