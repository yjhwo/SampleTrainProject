import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

/*
 * 출/도착지 기반 열차정보 조회
 * 열차(KTX)의 출발역, 도착역 정보를 조회하는 기능 제공
 */

public class TrainInfo {
	private static String DEP_PLACEID = "NAT750046";				// 출발지 ID(필수)
	private static String ARR_PLACEID = "NAT601774";				// 도착지 ID(필수)
	private static String DEP_PLAND_TIME = "20160715";				// 출발일(필수)
	//private static String TRAIN_GRADE_CODE = "00";					// 차량종류코드(선택) (KTX-00)
	/*내일로 가능 열차 
		새마을 : 01
		무궁화 : 02
		통근열차 : 03
		ITX-새마을 : 08
		ITX-청춘 : 09
	*/	
    public static void main(String[] args) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/TrainInfoService/getStrtpntAlocFndTrainInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=FYPMcB84UK5jn7XCnYqm1lJMqpuKakFgS4k3TII0xsef95IkojvpIVNDfBV6QQxm%2B9Dm9c7O%2FcIha6glL%2FVrEw%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("depPlaceId","UTF-8") + "=" + URLEncoder.encode(DEP_PLACEID, "UTF-8")); /*출발지ID*/
        urlBuilder.append("&" + URLEncoder.encode("arrPlaceId","UTF-8") + "=" + URLEncoder.encode(ARR_PLACEID, "UTF-8")); /*도착지ID*/
        urlBuilder.append("&" + URLEncoder.encode("depPlandTime","UTF-8") + "=" + URLEncoder.encode(DEP_PLAND_TIME, "UTF-8")); /*출발일*/
        //urlBuilder.append("&" + URLEncoder.encode("trainGradeCode","UTF-8") + "=" + URLEncoder.encode(TRAIN_GRADE_CODE, "UTF-8")); /*차량종류코드*/
       
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); /*검색건수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
       
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        System.out.println("url:"+url);
       
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
      
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());
    }
}