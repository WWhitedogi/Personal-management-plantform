package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

public class Track extends Save {
	public Track() {
		System.out.println("Track empty constructor");
		attribute_num = 5;
	}
	
	public Track(String company, String date, String status, String url, String comment) {
		System.out.println("Track full constructor");
		set("Company", company);
		set("Date", date);
		set("Status", status);
		set("Url", url);
		set("Comment", comment);
		attribute_num = 5;
	}
	
	public ArrayList<String> getKeys() {
		ArrayList<String> ans = new ArrayList<String>(Arrays.asList("Company", "Date", "Status", "Url", "Comment", "Operations"));
		return ans;
	}
	
	public ArrayList<String> getValues() {
		ArrayList<String> ans = new ArrayList<String>();
		for(Entry<String, String> entry : content.entrySet()) {
			ans.add(entry.getValue());
		}
		
		return ans;
	}
}
