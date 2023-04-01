package application;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public abstract class Save {
	protected HashMap<String, String> content;
	protected int attribute_num;
	
	public Save() {
		content = new HashMap<String, String>();
	}
	
	public void set(String m, String c) {
		content.put(m, c);
	}
	
	public boolean set(String[] parts) {
		StringBuffer c = new StringBuffer();
		for(int i = 1; i < parts.length; i++)
			c.append(parts[i]);
		
		set(parts[0], c.toString());
		return true;
	}
	
	public String get(String m) {
		if(!content.containsKey(m)) {
			System.out.println("Key " + m + " Not Found");
			return "";
		}
		return content.get(m);
	}
	
	public void save(PrintWriter pw) {
		for(Entry<String, String> entry : content.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			pw.println(key + ": " + value);
		}
	}
	
	public void load(Scanner s) {
		String[] parts;
		for(int i = 0; i < attribute_num; i++) {
			parts = s.nextLine().split(": ");
			set(parts);
		}
	}
}
