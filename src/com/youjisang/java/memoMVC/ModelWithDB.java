package com.youjisang.java.memoMVC;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;


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

