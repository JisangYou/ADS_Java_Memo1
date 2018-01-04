# ADS04 Java 


## 수업 내용

- MVC패턴을 활용한 MEMO를 학습
- File과 Database에 저장하는 것을 학습
- 간단한 SQL문을 학습

## Code Review

1. View

```Java
public class View {
	// 스캐너 컨트롤러와 독립적으로 구성
	Scanner scanner = new Scanner(System.in);
	// 키보드 입력을 받는 함수
	public Memo create(){
		// 글 하나를 저장하기 위한 메모리 공간 확보
		Memo memo = new Memo();
		
		println("이름을 입력하세요 :");
		memo.name = scanner.nextLine();
		println("내용을 입력하세요 :");
		memo.content = scanner.nextLine();
		// 날짜
		memo.datetime = System.currentTimeMillis();
		
		return memo;
	}
	
	public int readMemoNo(){
		println("글 번호를 입력하세요");
		// ------ 숫자가 입력되지 않았을 때의 예외 처리 --------------- //
		String tempNo = scanner.nextLine();
		try{
			return Integer.parseInt(tempNo);
		}catch(Exception e){
			return -1;
		}
	}
	
	public void showMemo(Memo memo){
		println("No:"+memo.no);
		println("Author:"+memo.name);
		println("Content:"+memo.content);
		
		// 숫자로 입력받은 날짜값을 실제 날짜로 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String formattedDate = sdf.format(memo.datetime);
		println("Date:"+formattedDate);
	}
	
	public void update(Memo memo){
		println("이름을 입력하세요 :");
		memo.name = scanner.nextLine();
		println("내용을 입력하세요 :");
		memo.content = scanner.nextLine();
		// 날짜
		memo.datetime = System.currentTimeMillis();
	}
	
	public void delete(){
		//TODO 추후에 할것
	}
	
	public void showList(ArrayList<Memo> list) {
		// ArrayList 저장소를 반복문을 돌면서 한줄씩 출력
		for(Memo memo : list){
			print("No:"+memo.no);
			print(" | Author:"+memo.name);
			println(" | Content:"+memo.content);
		}
	}
	
	public void print(String string){
		System.out.print(string);
	}
	
	public void println(String string){
		System.out.println(string);
	}
}

```

2. Model

```Java
//데이터를 저장하는 저장소를 관리하는 객체
public class Model {
	private final String DB_DIR = "c:/workspaces/java/database";
	// 데이터를 저장하기 위한 파일 저장소
	private final String DB_FILE = "memo.txt";
	// 인덱스를 저장하기 위한 파일 저장소
	private final String INDEX_FILE = "memo_index.txt";
	// mac 은 "/workspaces/java/database"
	private File database = null;
	private File database_index = null;

	// 전체 메모를 저장하는 저장소
	ArrayList<Memo> list = new ArrayList<>();
	// 마지막 글번호
	int lastIndex = 0;

	public Model() {
		// new 하는 순간 이 영역이 실행된다.
		File dir = new File(DB_DIR);
		// 디렉토리의 존재유무
		if (!dir.exists()) {
			dir.mkdirs(); // 경로상에 디렉토리가 없으면 자동생성
		}
		// window = \
		// mac = /
		// unix,linux = /
		File file = new File(DB_DIR + File.separator + DB_FILE);
		// 파일의 존재유무
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		database = file;

		File index = new File(DB_DIR + File.separator + INDEX_FILE);
		if (!index.exists()) {
			try {
				index.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		database_index = index;
	}

	private final String COLUMN_SEP = "::";

	// 생성
	public void create(Memo memo) {
		// 글번호
		memo.no = ++lastIndex;

		// 1. 쓰는 스트림을 연다
		try (FileOutputStream fos = new FileOutputStream(database, true)) {
			// 저장할 내용을 구분자로 분리하여 한줄의 문자열로 바꾼다.
			String row = memo.no + COLUMN_SEP + memo.name + COLUMN_SEP + memo.content + COLUMN_SEP + memo.datetime
					+ "\n";
			// 2. 스트림을 중간처리... (텍스트의 엔코딩을 변경...)
			OutputStreamWriter osw = new OutputStreamWriter(fos); // 래퍼스트림
			// 3. 버퍼처리...
			BufferedWriter bw = new BufferedWriter(osw);
			bw.append(row);
			bw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 글 하나를 저장한 메모리를 저장소로 이동
		// list.add(memo);
	}

	// 읽기
	public Memo read(int no) {
		for (Memo memo : list) {
			if (memo.no == no) {
				return memo;
			}
		}
		return null;
	}

	// 수정
	public void update(Memo memo) {

	}

	// 삭제
	public void delete(int no) {
		for (Memo memo : list) {
			if (memo.no == no) {
				list.remove(memo);
			}
		}
	}

	// 목록
	public ArrayList<Memo> getList() {

		// 데이터가 중복해서 쌓이지 않도록 저장소를 지워주는 작업이 필요한 경우가 있다.
		list.clear();

		// 1. 읽는 스트림을 연다
		try (FileInputStream fis = new FileInputStream(database)) { // try-with 절에서 자동으로 fis.close가 발생
			// 2. 실제 파일 엔코딩을 바꿔주는 래퍼 클래스를 사용
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			// 3. 버퍼처리
			BufferedReader br = new BufferedReader(isr);

			String row;
			// 새로운 줄을 한줄씩 읽어서 row에 저장하고
			// 더 이상 읽을 데이터가 없으면 null이 리턴되므로 로직이 종료된다.
			while ((row = br.readLine()) != null) {
				String tempRow[] = row.split(COLUMN_SEP);
				// 1::fds::fdsaf::1504839497021
				// tempRow[0] = 1
				// tempRow[1] = fds
				// tempRow[2] = fdsaf
				// tempRow[3] = 1504839497021
				Memo memo = new Memo();
				memo.no = Integer.parseInt(tempRow[0]);
				memo.name = tempRow[1];
				memo.content = tempRow[2];
				memo.datetime = Long.parseLong(tempRow[3]);

				list.add(memo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}

```

3. Control

```Java
public class Control {
	
	ModelWithDB model;
	View view;
	
	public Control(ModelWithDB model, View view){
		this.model = model;
		this.view = view;
	}
	
	public void process(){
		Scanner scanner = new Scanner(System.in);

		// 명령어를 입력받아서 후속 처리
		// c - create : 데이터 입력모드로 전환
		// r - read   : 데이터 읽기모드로 전환
		// u - update : 데이터 수정모드로 전환
		// d - delete : 데이터 삭제모드로 전환
		String command = "";
	    
		while(!command.equals("exit")){
			view.println("-------- 명령어을 입력하세요 ---------");
			view.println("c : 쓰기, r : 읽기, u : 수정, d : 삭제, l : 목록");
			view.println("exit : 종료");
			view.println("-------------------------------");
			command = scanner.nextLine(); 
			// 명령어를 분기처리
			switch(command){
			case "c":
				Memo memo = view.create();
				model.create(memo);
				break;
			case "r":
				int readNo = view.readMemoNo();
				if(readNo < 0){ // 모두 예외처리 필요
					view.println("글번호가 잘못되었습니다.");
					break;
				}
				Memo readMemo = model.read(readNo);
				view.showMemo(readMemo);
				break;
			case "u":
				int updateNo = view.readMemoNo();
				Memo updateMemo = model.read(updateNo);
				view.update(updateMemo);
				break;
			case "d":
				int deleteNo = view.readMemoNo();
				model.delete(deleteNo);
				break;
			case "l":
				ArrayList<Memo> list = model.getList();
				view.showList(list);
				break;
			}
		}
		
		view.println("시스템이 종료되었습니다!");
	}
}
```

4. ModelWithDB

```Java
public class ModelWithDB {
	
	private final String URL ="jdbc:mysql://localhost:3306/memo";
	private final String ID = "root";
	private final String PW = "mysql";
	
	Connection con = null;
	
	// 생성자
	public ModelWithDB(){
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 드라이버를 동적으로 로드
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	// 쓰기
	public void create(Memo memo){
		// 1. 데이터베이스 연결
		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {
			// 2. 쿼리를 실행
			// 2.1 쿼리 생성
			String query = " insert into memo(name,content,datetime) values(?,?,?)";
			// 2.2 쿼리를 실행 가능한 상태로 만들어준다
			PreparedStatement pstmt = con.prepareStatement(query);
			// 2.3 물음표에 값을 세팅
			pstmt.setString(1, memo.name);
			pstmt.setString(2, memo.content);
			pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			// 2.4 쿼리를 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}// 3. 데이터베이스 연곌해제
	}
	// 읽기
	public Memo read(int no){
		Memo memo = new Memo();
		
		// 1. 데이터베이스 연결
		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {
			// 2. 쿼리를 실행
			// 2.1 쿼리 생성
			String query = "select * from memo where no = "+no;
			// 2.2  앞으로 쿼리를 실행 가능한 상태로 만들어준다
			Statement stmt = con.createStatement();
			// 2.3 select한 결과값을 돌려받기 위해 쿼리를 실행
			ResultSet rs = stmt.executeQuery(query);
			// 결과셋을 반복하면서 하나씩 꺼낼 수 있다
			if(rs.next()){
				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.content = rs.getString("content");
				memo.datetime = rs.getLong("datetime");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}// 3. 데이터베이스 연곌해제
		
		return memo;
	}
	// 수정
	public void update(Memo memo){
		
	}
	// 삭제
	public void delete(int no){

	}
	// 목록
	public ArrayList<Memo> getList(){
		ArrayList<Memo> list = new ArrayList<>();
		
		// 1. 데이터베이스 연결
		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {
			// 2. 쿼리를 실행
			// 2.1 쿼리 생성
			String query = "select * from memo";
			// 2.2  앞으로 쿼리를 실행 가능한 상태로 만들어준다
			Statement stmt = con.createStatement();
			// 2.3 select한 결과값을 돌려받기 위해 쿼리를 실행
			ResultSet rs = stmt.executeQuery(query);
			// 결과셋을 반복하면서 하나씩 꺼낼 수 있다
			while(rs.next()){
				Memo memo = new Memo();
				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.content = rs.getString("content");
				memo.datetime = rs.getLong("datetime");
				list.add(memo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}// 3. 데이터베이스 연곌해제
		
		return list;
	}
}

```

5. Memo

```Java
//개별 글 한개 한개를 저장하는 클래스
public class Memo {
	int no;
	String name;
	String content;
	long   datetime;
}
```

6. Main

```Java
public class MemoMain {

	public static void main(String[] args) {
		ModelWithDB model = new ModelWithDB();
		View view = new View();
		
		Control control = new Control(model, view);
		control.process();
	}
}
```

## 보충설명




## TODO

- file관련 함수, DB관련 함수 및 사용하는 방법 익히기
- MVC패턴 익숙해지는 연습 필요


## Retrospect

- MVC패턴과 게시판, 그리고 파일, DB를 아우르는 내용이다 보니 공부할게 굉장히 많았음.
- 계속 보고 따라쳐보면서 연습필요.


## Output
- 생략