package com.youjisang.java.memoMVC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mysql.jdbc.PreparedStatement;

//�����͸� �����ϴ� ����Ҹ� �����ϴ� ��ü
public class ModelWithDB {

	private final String URL = "jdbc:mysql://localhost:3306/memo";
	private final String ID = "root";
	private final String PW = "879200y";
	Connection con = null;

	public ModelWithDB() {

		// Ư�� ��ǻ�͸� ã������ �ּ�ü��
		// ������ = 213.12.142.132
		// url = naver.com
		// Ư�� ���α׷��� �Ҵ�Ǵ� ���ι���
		// port = '1~6������'....
		// 2000���� ���� �̹� ǥ������ ���ǰ� �ִ�.

		// ����
		// ������+��Ʈ
		// ǥ�� ��������
		// http:// ������(�ּ�) : 80

		// Ư�� ���α׷��� ������ �ϱ����� �ּ�ü��
		// �������� �̸�:// ������(�ּ�) :��Ʈ

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void create(Memo memo) {
		// 1. �����ͺ��̽� ����

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {
			// �����ͺ��̽� �ּ�, ����ھƵ�. ��й�ȣ
			// 2. ������ ����
			// 2.1 ��������
			String query = "insert into memo(name,content,datetime) values(?,?,?)";
			// 2.2 ������ ���� ������ ���·� ������ش�.
			java.sql.PreparedStatement pstmt = con.prepareStatement(query);
			// 2. 3����ǥ�� ���� ����
			pstmt.setString(1, memo.name);
			pstmt.setString(2, memo.content);
			pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();
			System.out.println("���ӵǾ����ϴ�.");

		} catch (Exception e) {
			System.out.println("���� ����.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Memo read(int no) {
		Memo memo = new Memo();

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {// �����ͺ��̽�
																			// �ּ�,
																			// ����ھƵ�.
																			// ��й�ȣ
			// 2. ������ ����
			// 2.1 ��������
			String query = "Select * from memo where no =" + no;
			// 2.2 ������ ���� ������ ���·� ������ش�.
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement(query);
			// 2. 3����ǥ�� ���� ����
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.content = rs.getString("content");
				memo.datetime = rs.getLong("dataTime");

			}

			System.out.println("���ӵǾ����ϴ�.");
		} catch (Exception e) {
			System.out.println("���� ����.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return memo;
	}

	// ����
	public void update(Memo memo) {

	}

	// ����
	public void delete(int no) {

	}

	// ���
	public ArrayList<Memo> getList() {
		ArrayList<Memo> list = new ArrayList<>();

		try (Connection con = DriverManager.getConnection(URL, ID, PW);) {// �����ͺ��̽�
																			// �ּ�,
																			// ����ھƵ�.
																			// ��й�ȣ
			// 2. ������ ����
			// 2.1 ��������
			String query = "Select * from memo";
			// 2.2 ������ ���� ������ ���·� ������ش�.
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement(query);
			// 2. 3����ǥ�� ���� ����
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				Memo memo = new Memo();
				memo.no = rs.getInt("no");
				memo.name = rs.getString("name");
				memo.datetime = rs.getLong("dataTime");
				list.add(memo);
			}

			System.out.println("���ӵǾ����ϴ�.");
		} catch (Exception e) {
			System.out.println("���� ����.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
