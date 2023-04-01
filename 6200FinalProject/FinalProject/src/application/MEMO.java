package application;


public class MEMO extends Save {
	public MEMO() {
		System.out.println("Memo empty constructor");
		attribute_num = 3;
	}
	
	public MEMO(String date, String title, String content) {
		System.out.println("Memo full constructor");
		set("Date", date);
		set("Title", title);
		set("Content", content);
		attribute_num = 3;
	}
}
