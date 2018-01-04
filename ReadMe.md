# ADS04 Java 


## 수업 내용

- MVC패턴을 활용한 MEMO를 학습
- File과 Database에 저장하는 것을 학습
- 간단한 SQL문을 학습
- 예외처리

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

- mvc패턴으로 만들었음.
- 일반적으로 게시판의 특성을 지닌 프로그램은 CRUD를 고려해서 만든다.
- CRUD는 대부분의 컴퓨터 소프트웨어가 가지는 기본적인 데이터 처리 기능인 Create(생성), Read(읽기), Update(갱신), Delete(삭제)를 묶어서 일컫는 말이다. 사용자 인터페이스가 갖추어야 할 기능(정보의 참조/검색/갱신)을 가리키는 용어로서도 사용
- mvc패턴으로 게시판의 특성을 가진 메모프로그램을 CRUD 방식으로 만들려고 했고, 관련 로직의 설명보다는 몰랐던 문법이나 함수등을 중점으로 설명할 것임(MEMO2에서 로직관련 부분을 다룰 것임.)

### Console 입출력

#### 츨력

- System.out.println, System.out.print, System.out.printf -> 콘솔출력
- %d(부호 o 10진수 정수),%o(부호 x 8진수 정수),%x(부호 x 16진수 정수) 등은 서식문자라고 함. 
- 문자열 내에서 특별한 의미로 해석되는 문자들을 이스케이프 시퀀스라함 /n(다음줄로 넘어감), /t(수평 탭을 입력),/''(큰 따옴표를 입력)
- 예제 코드

```Java
class Tutorial {

  public static void main(String args[]) {

    String name = "김철수";

    int age = 14;



    System.out.printf("안녕하세요. 제 이름은 %s 입니다.\n", name);

    System.out.printf("나나\b이는 \"%d\"살 입니다.", age);

  }

}
출처: http://blog.eairship.kr/124?category=431864 [누구나가 다 이해할 수 있는 프로그래밍 첫걸음]
```
- 결과
>> 안녕하세요. 제 이름은 김철수 입니다.
>> 나이는 "14"살 입니다.

#### 입력

- Scanner 클래스를 사용한다 
- 예제 코드 
```Java
class scantutorial {

  public static void main(String args[]) {

    Scanner sc = new Scanner(System.in);

    int data = sc.nextInt();

    System.out.println("사용자가 입력한 데이터: " + data);
  }
}
```

#### 예외처리

- 프로그램 실행 중 예외의 경우가 발생하여 비정상 종료가 되거나 잘 못 작동하는 상황을 말한다.
- 이를 처리하기 위해 try~catch 사용함

```Java
	try{
		예외가 발생할 위험이 있는 코드
	}catch(예외타입 예외명){
		예외를 처리하는 코드
	}
```
- finally : 예외상황을 무시하고 반드시 실행되어야 하는 코드가 있을 경우에 필요한 영역,  throw : 강제로 예외를 발생시킬 수 있게 하는 특징, throws : 메소드 호출 시 예외를 발생시키고 싶을 경우에 사용됨.

#### 파일 입출력

- 스트림은 1차원적인 데이터의 흐름을 의미함.
- 스트림은 흐름의 방향(입력스트림과 출력스트림)과 데이터의 형태(문자 스트림, 바이트 스트림)에 따라 다름

![Stream](http://cfile1.uf.tistory.com/image/1755CE495038B2801DF78B)

- 파일입출력 부분은 일정한 틀이 있기에 예제코드로 대체

1. FileReader

```Java
class FileTutorial {

	public static void main(String[] args) throws IOException {

		// 예외가 발생하면 외부로 던져버림!

		FileReader reader = new FileReader("c:\\test.txt");

		int ch;

		while((ch = reader.read()) != -1) { // 하나하나씩 받아오고 출력시킨다!

			System.out.print((char)ch);

		}

		reader.close(); // 스트림을 다 썼으면 닫아주어야 한다.

	}

}


```

2. FileWriter

```Java
class FileTutorial {

	public static void main(String[] args) throws IOException {

		FileWriter reader = new FileWriter("c:\\test.txt"); // 텍스트 파일이 없으면 새로 생성함!

				

		reader.write("입출력!"); // 파일에 "입출력!"을 저장함.

		reader.append('!'); // 파일의 끝에 ! 문자를 추가시킴.

		reader.close(); // 파일을 닫음.

	}

}

```

3. FileInputStream

```Java
class FileTutorial {

	public static void main(String[] args) throws IOException {

		FileInputStream in = new FileInputStream("C:\\test.txt");

		int ch;

		while((ch = in.read()) != -1) { // 하나하나씩 받아오고 출력시킨다!

			System.out.print((char)ch);

		}

		in.close(); // 파일을 닫음.

	}

}
```

4. FileOutputStream

```Java
class FileTutorial {

	public static void main(String[] args) throws IOException {

		FileOutputStream out = new FileOutputStream("C:\\test.txt", false);

		// true로 두면 이어서 쓰고, false로 두면 새로 쓴다.

		int ch;

		for(int i = 'a'; i <= 'z'; i++) {  // a부터 z까지 파일에 입력한다!

			out.write(i);

		}

		out.close(); // 파일을 닫음.

	}

}

```

- 보조 Stream이란 

다른 스트림과 연결되어 여러 가지 편리한 기능으로 문자변환, 입출력 성능향상, 기본 데이터 타입 입출력, 객체 입출력등의 기능을 제공해 주는 스트림입니다.
보조 스트림의 일부가 FilterInputStream, FilterOutputStream의 하위 클래스여서 필터(filter)스트림이라고도 합니다.
보조 스트림은 자체적으로 입출력을 수행할 수 없기 때문에 입력소스와 바로 연결되는 스트림과 출력소스와 바로 연결이 되는 스트림등에 연결해서 입출력을 수행합니다.

- Java Database Connectivity를 사용한 데이터베이스 연동
- JDBC는 자바 프로그램과 관계형 데이터 원본에 대한 인터페이스이다. JDBC라이브러리는 관계형 데이터베이스에 접근하고, SQL 쿼리문을 실행하는 방법을 제공

![JDBC 프로그램 작성단계](http://cfile27.uf.tistory.com/image/27669050526C94181B09CC) 

1. 1단계 (JDBC 드라이버 Load)

- 인터페이스 드라이버(interface driver)를 구현(implements)하는 작업으로, Class 클래스의 forName() 메소드를 사용해서 드라이버를 로드한다. forName(String className) 메소드는 분자열로 주어진 클래스나 인터페이스 이름을 객체로 리턴한다.

- MySQL 드라이버 로딩

```Java
Class.forName("com.mysql.jdbc.Driver");
```

- Oracle 드라이버 로딩

```Java
Class.forName("oracle.jdbc.driver.OracleDriver");
```
- Class.forName("com.mysql.jdbc.Driver") 은 드라이버들이 읽히기만 하면 자동 객체가 생성되고 DriverManager에 등록된다. 드라이버 로딩은 프로그램 수행 시 한 번만 필요하다.



2. 2단계 (Connection 객체 생성)

- Connection 객체를 연결하는 것으로 DriverManager에 등록된 각 드라이버들을 getConnection(String url) 메소드를 사용해서 식별한다. 이때 url 식별자와 같은 것을 찾아서 매핑(mapping)한다. 찾지 못하면 no suitable error 가 발생한다.

- MySQL 사용시 Connection 객체 생성

```Java
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jsptest", "jspid","jsptest");
```

- Oracle 사용시 Connection 객체 생성

```Java
Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:ora817", "scott", "tiger");
```


3. 3단계 (Statement/PreparedStatement/CallableStatement 객체 생성)

- sql 쿼리를 생성/실행하며, 반환된 결과를 가져오게 할 작업 영역을 제공한다.

- Statement 객체는 Connection 객체의 createStatement() 메소드를 사용하여 생성된다.

```Java
Statement stmt = conn.createStatement();
```

4. 4단계 (Query 수행)

- Statement 객체가 생성되면 Statement 객체의 executeQuery() 메소드나 executeUpdate() 메소드를 사용해서 쿼리를 처리한다.

- stmt.executeQuery : recordSet 반환 => Select 문에서 사용

```Java
ResultSet rs = stmt.executeQuery("select * from 소속기관");
```

- stmt.executeUpdate() : 성공한 row 수 반환 => Insert문, Update문, Delete문에서 사용

```Java
String sql = "update member1 set passwd = '3579' where id ='abc'";
stmt.executeUpdate(sql);
```

5. 5단계 (ResultSet 처리)

- executeQuery() 메소드는 결과로 ResultSet을 반환한다. 이 ResultSet으로부터 원하는 데이터를 추출하는 과정을 말한다.

- 데이터를 추출하는 방법은 ResultSet 에서 한 행씩 이동하면서 getXxx()를 이용해서 원하는 필드 값을 추출하는데, 이때 rs.getString("name") 혹은 rs.getString(1) 을 사용한다.

- ResultSet의 첫 번째 필드는 1 부터 시작한다.

- 한 행이 처리되고 다음 행으로 이동 시 next() 메소드를 사용한다.

```Java
while(rs.next()){

out.println(rs.getString("id"));

out.println(rs.getString("passwd");
```
}



#### 출처: http://hyeonstorage.tistory.com/110 [개발이 하고 싶어요]
#### 출처: http://codedragon.tistory.com/5482 [Code Dragon]
#### 출처: http://blog.eairship.kr/127?category=431864 [누구나가 다 이해할 수 있는 프로그래밍 첫걸음]

## TODO

- file관련 함수, DB관련 함수 및 사용하는 방법 익히기
- MVC패턴 익숙해지는 연습 필요
- 예외 상황시 나오는 메시지 알아두기(나중에 에러날시에 도움이 될듯함)
- 보조 스트림에 대해 추가적인 공부 필요(성능향상시 필요)
- DB 관련 문법 정리(RDBMS,NoSQL)


## Retrospect

- MVC패턴과 게시판, 그리고 파일, DB를 아우르는 내용이다 보니 공부할게 굉장히 많았음.
- 계속 보고 따라쳐보면서 연습필요.


## Output
- 생략