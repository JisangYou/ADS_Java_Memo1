package com.youjisang.java.memoMVC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mysql.jdbc.PreparedStatement;

//데이터를 저장하는 저장소를 관리하는 객체
public class ModelWithDB {

	private final String URL = "jdbc:mysql://localhost:3306/memo";
	private final String ID = "root";
	private final String PW = "879200y";
	Connection con = null;

	public ModelWithDB() {

		// 특정 컴퓨터를 찾기위한 주소체계
		// 아이피 = 213.12.142.132
		// url = naver.com
		// 특정 프로그램에 할당되는 세부번지
		// port = '1~6만번대'....
		// 2000번때 밑은 이미 표준으로 사용되고 있다.

		// 소켓
		// 아이피+포트
		// 표준 프로토콜
		// http:// 아이피(주소) : 80

		// 특정 프로그램에 엑세스 하기위한 주소체계
		// 프로토콜 이름:// 아이피(주소) :포트

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void create(Memo memo) {
		// 1. 데이터베이스 연결

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {
			// 데이터베이스 주소, 사용자아디. 비밀번호
			// 2. 쿼리를 실행
			// 2.1 쿼리생성
			String query = "insert into memo(name,content,datetime) values(?,?,?)";
			// 2.2 쿼리를 실행 가능한 상태로 만들어준다.
			java.sql.PreparedStatement pstmt = con.prepareStatement(query);
			// 2. 3물음표에 값을 세팅
			pstmt.setString(1, memo.name);
			pstmt.setString(2, memo.content);
			pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();
			System.out.println("접속되었습니다.");

		} catch (Exception e) {
			System.out.println("접속 오류.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Memo read(int no) {
		Memo memo = new Memo();

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {// 데이터베이스
																			// 주소,
																			// 사용자아디.
																			// 비밀번호
			// 2. 쿼리를 실행
			// 2.1 쿼리생성
			String query = "Select * from memo where no =" + no;
			// 2.2 쿼리를 실행 가능한 상태로 만들어준다.
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement(query);
			// 2. 3물음표에 값을 세팅
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.content = rs.getString("content");
				memo.datetime = rs.getLong("dataTime");

			}

			System.out.println("접속되었습니다.");
		} catch (Exception e) {
			System.out.println("접속 오류.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return memo;
	}

	// 수정
	public void update(Memo memo) {

	}

	// 삭제
	public void delete(int no) {

	}

	// 목록
	public ArrayList<Memo> getList() {
		ArrayList<Memo> list = new ArrayList<>();

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {// 데이터베이스
																			// 주소,
																			// 사용자아디.
																			// 비밀번호
			// 2. 쿼리를 실행
			// 2.1 쿼리생성
			String query = "Select * from memo";
			// 2.2 쿼리를 실행 가능한 상태로 만들어준다.
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement(query);
			// 2. 3물음표에 값을 세팅
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				Memo memo = new Memo();
				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.datetime = rs.getLong("dataTime");
				list.add(memo);
			}

			System.out.println("접속되었습니다.");
		} catch (Exception e) {
			System.out.println("접속 오류.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
