package testRoute;

// 지역 + 기차시간... test
// 최저시간 구하기

public class Test {

	public static void main(String[] args) {
		String[] str = { "서울:부전:20160708132500:20160708074800", "서울:부산:20160708120200:20160708064400",
				"서울:부산:20160708124100:20160708072000", "서울:부전:20160708132500:20160708074800",
				"서울:부전:20160708220200:20160708162500", "서울:구포:20160708110900:20160708055000",
				"청량리:부전:20160708161200:20160708082500" };
		
		int minTime = 125959;
		String time = "09";
		
		for (int i = 0; i < str.length; i++) {
			String[] info = str[i].split(":");

			for (String s : info) {
				System.out.println(s);
			}

			String depTime = info[3].substring(8);		// 출발
			String arrTime = info[2].substring(8);		// 도착
			
			System.out.println("depHour:"+depTime.substring(0, 2));
			String depHour = depTime.substring(0, 2);
			// --
			if(depHour.compareTo(time) < 0){
				System.out.println("depTime:"+depTime+", time:"+time);
				continue;
			}
			// --
			System.out.println("if탈출 depTime:"+depTime+", time:"+time);

			int operTime = Integer.parseInt(arrTime) - Integer.parseInt(depTime) - 4000;

			System.out.println(info[0] + info[1] + operTime);
			
			// -----
			if(operTime < minTime){
				minTime = operTime;
				System.out.println("minTime:"+minTime);
			}
			
		}
		
		System.out.println("최종minTime~~"+minTime);
		
		

	}

}
