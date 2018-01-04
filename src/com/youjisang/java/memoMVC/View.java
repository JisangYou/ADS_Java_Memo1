package com.youjisang.java.memoMVC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 화면의 입출력을 담당하는 객체
 * @author pc
 *
 */
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