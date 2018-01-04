package com.youjisang.java.memoMVC;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 로직을 담당하는 객체
 * @author pc
 *
 */
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