package application;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Scanner;

public class TODO extends Save implements Comparable<TODO>{
	private Timestamp start;
	private boolean isDone;
	
	public TODO() {
		System.out.println("Todo empty constructor");
		start = Timestamp.from(Instant.now());
		isDone = false;
		attribute_num = 3;
	}
	
	public TODO(String title, String content, Timestamp d) {
		System.out.println("Todo partial constructor");
		set("Title", title);
		set("Content", content);
		set("Date", d.toString());
		start = Timestamp.from(Instant.now());
		isDone = false;
		attribute_num = 3;
	}
	
	public TODO(String title, String content, Timestamp d, boolean done) {
		System.out.println("Todo full constructor");
		set("Title", title);
		set("Content", content);
		set("Date", d.toString());
		start = Timestamp.from(Instant.now());
		isDone = done;
		attribute_num = 3;
	}
	
	public void Done() {
		isDone = true;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public Timestamp getStartDate() {
		return start;
	}
	
	public double getCurrentDateProgress() {
		System.out.println(get("Date"));
		Timestamp end = Timestamp.valueOf(get("Date"));
		if(end.getTime() == start.getTime())
			return 0;
		return (end.getTime() - (Timestamp.from(Instant.now())).getTime()) / (end.getTime() - start.getTime());
	}

	@Override
	public int compareTo(TODO o) {
		Timestamp d1 = Timestamp.valueOf(content.get("Date"));
		Timestamp d2 = Timestamp.valueOf(o.get("Date"));
		return d1.compareTo(d2);
	}
	
	@Override
	public void save(PrintWriter pw) {
		super.save(pw);
		pw.println("Done: " + isDone);
	}
	
	@Override
	public void load(Scanner s) {
		super.load(s);
		String[] parts = s.nextLine().split(": ");
		isDone = Boolean.parseBoolean(parts[1]);
	}
}
