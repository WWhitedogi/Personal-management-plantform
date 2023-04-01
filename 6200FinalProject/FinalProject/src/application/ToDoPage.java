package application;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings("deprecation")
public class ToDoPage {
	
	private final static double TTodoGap = 16;
	private final static int TLIMIT = 40;
	private final static double TTodoX = 15;
	
	private final static double DETAIL_WIDTH = 298;
	
	private static int tmp;
	
	@FXML
	public void Refresh(Event event) throws Exception {
		Scene sc = ((Node)event.getSource()).getScene();
		Refresh(sc);
	}
	
	
	public void Refresh(Scene sc) throws Exception {
		ScrollPane pane = (ScrollPane)sc.lookup("#TODOList");
		ScrollPane content = (ScrollPane)sc.lookup("#TContent");
		((VBox)content.getContent()).getChildren().clear();
		ArrayList<TODO> lst = Main.todos;
		VBox p = (VBox)pane.getContent();
		p.getChildren().clear();
		p.setSpacing(TTodoGap);
		for(int i = 0; i < lst.size(); i++) {
			TODO t = lst.get(i);
			if(t.isDone())
				continue;
			Parent parent = FXMLLoader.load(ToDoPage.class.getResource("TToDo.fxml"));
			Timestamp time = Timestamp.valueOf(t.get("Date"));
			Text day = (Text)parent.lookup("#day");
			long remain = Math.round(Main.TimestampDifference(Timestamp.from(Instant.now()), time) / 86400000.0);
			day.setText((time.getDate() < 10 ? "0" : "") + time.getDate());
			Text month = (Text)parent.lookup("#month");
			month.setText(Main.Month[time.getMonth()]);
			if(remain < 0) {
				day.setFill(Color.RED);
				month.setFill(Color.RED);
			} else if(remain < 2) {
				day.setFill(Color.ORANGE);
				month.setFill(Color.ORANGE);
			} else {
				day.setFill(Color.GREEN);
				month.setFill(Color.GREEN);
			}
			String title = t.get("Title");
			if(title.length() > TLIMIT)
				title = title.substring(0, TLIMIT) + "...";
			((Text)parent.lookup("#name")).setText(title);
			
			((Text)parent.lookup("#remain")).setText(remain < 0 ? "Overdue" : Long.toString(remain) + (remain > 1 ? " Days" : " Day"));
			//parent.setLayoutY(pos);
			parent.setLayoutX(TTodoX);
			tmp = i;
			parent.setOnMouseClicked(new EventHandler<MouseEvent>() {
				int i = tmp;
				@Override
				public void handle(MouseEvent arg0) {
					try {
						ShowDetail(sc, i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			parent.lookup("#t_edit").setOnMouseClicked(new EventHandler<MouseEvent>() {
				int i = tmp;
				
				@Override
				public void handle(MouseEvent arg0) {
					try {
						Scene sc = EditDT(i);
						tmp = i;
						((Button)sc.lookup("#Done")).setOnAction(new EventHandler<ActionEvent>() {
							int i = tmp;
							@Override
							public void handle(ActionEvent event) {
								try {
									UpdateDT(i, event);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
			parent.lookup("#t_remove").setOnMouseClicked(new EventHandler<MouseEvent>() {
				int i = tmp;
				
				@Override
				public void handle(MouseEvent arg0) {
					try {
						remove(arg0, i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			parent.lookup("#t_done").setOnMouseClicked(new EventHandler<MouseEvent>() {
				int i = tmp;
				
				@Override
				public void handle(MouseEvent arg0) {
					try {
						done(arg0, i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			//pos += TTodoGap;
			p.getChildren().add(parent);
		}
		pane.setContent(p);
	}
	
	private void ShowDetail(Scene sc, int i) throws Exception {
		if(i >= Main.todos.size() || Main.todos.get(i).isDone()) {
			Refresh(sc);
			return;
		}
		TODO t = Main.todos.get(i);
		VBox pane = (VBox)((ScrollPane)sc.lookup("#TContent")).getContent();
		pane.setSpacing(10);
		pane.getChildren().clear();
		String str = "Task: " + t.get("Title");
		Text txt = new Text(str);
		txt.setWrappingWidth(DETAIL_WIDTH);
		pane.getChildren().add(txt);
		Timestamp time = Timestamp.valueOf(t.get("Date"));
		str = "Due Date: " + Main.Full_Month[time.getMonth()] + ". " + time.getDate();txt = new Text(str);
		txt.setWrappingWidth(DETAIL_WIDTH);
		pane.getChildren().add(txt);
		str = "Description: " + (t.get("Content").isEmpty() ? "You dont't write any description. " : t.get("Content"));
		txt = new Text(str);
		txt.setWrappingWidth(DETAIL_WIDTH);
		pane.getChildren().add(txt);
	}
	
	private void remove(MouseEvent event, int i) throws Exception {
		System.out.println("Remove at " + i);
		Main.todos.remove(i);
		Refresh(event);
		Main.SaveTodo();
		Refresh(Main.primary.getScene());
	}
	
	private void done(MouseEvent event, int i) throws Exception {
		System.out.println("Done at " + i);
		Main.todos.get(i).Done();
		Main.SaveTodo();
		Refresh(event);
	}
	
	private Scene EditDT(int i) throws IOException {
		if(Main.substage != null)
			return null;	
		Parent pane = FXMLLoader.load(getClass().getResource("EditTodo.fxml"));
		Scene sc = new Scene(pane);
		Stage s = new Stage();
		s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->SubstageClose());
		Main.substage = s;
		s.setScene(sc);
		((TextField)sc.lookup("#dt_w_name")).setText(Main.todos.get(i).get("Title"));
		Timestamp date = Timestamp.valueOf(Main.todos.get(i).get("Date"));
		((TextField)sc.lookup("#dt_w_year")).setText(Integer.toString(date.getYear() + 1900));
		((TextField)sc.lookup("#dt_w_month")).setText(Integer.toString(date.getMonth() + 1));
		((TextField)sc.lookup("#dt_w_day")).setText(Integer.toString(date.getDate()));
		s.show();
		return sc;
	}
	
	private void UpdateDT(int i, ActionEvent event) throws Exception {
		Scene sc = ((Node)event.getSource()).getScene();
		String title = ((TextField)sc.lookup("#dt_w_name")).getText();
		if(title.isEmpty()) {
			Main.Alert("Todo title cannot be empty!");
			return;
		}
		
		Main.todos.get(i).set("Title", title);
		String year = ((TextField)sc.lookup("#dt_w_year")).getText();
		String month = ((TextField)sc.lookup("#dt_w_month")).getText();
		String day = ((TextField)sc.lookup("#dt_w_day")).getText();
		Timestamp cur = Timestamp.from(Instant.now());
		
		if(year.isEmpty())
			year = Integer.toString(cur.getYear());
		if(month.isEmpty())
			month = Integer.toString(cur.getMonth());
		if(day.isEmpty())
			day = Integer.toString(cur.getDate());
		String date = year + "-" + month + "-" + day + " 00:00:00.0";
		Main.todos.get(i).set("Date", date);
		Main.substage.close();
		Main.substage = null;
		Main.SaveTodo();
		Refresh(Main.primary.getScene());
	}
	
	private void SubstageClose() {
		Main.substage = null;
	}
	
	@FXML
	public void ToDoAdd(ActionEvent event) throws Exception {
		if(Main.substage != null)
			return;
		
		Parent pane = FXMLLoader.load(getClass().getResource("AddToDo.fxml"));
		Scene sc = new Scene(pane);
		Stage s = new Stage();
		s.setScene(sc);
		s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->SubstageClose());
		s.show();
		Main.substage = s;
	}
	
	@FXML
	public void UpdateToDo(ActionEvent event) throws Exception {
		Scene sc = ((Node)event.getSource()).getScene();
		String title = ((TextField)sc.lookup("#title")).getText();
		String content = ((TextField)sc.lookup("#content")).getText();
		String date = "";
		int year, month, day;
		try {
			year = Integer.parseInt(((TextField)sc.lookup("#Year")).getText());
			month = Integer.parseInt(((TextField)sc.lookup("#Month")).getText());
			day = Integer.parseInt(((TextField)sc.lookup("#Day")).getText());
		} catch(ClassCastException ex) {
			Main.Alert("Date have to be numeric!");
			return;
		}
		date = year + "-" + month + "-" + day + " 00:00:00.0";
		Main.todos.add(new TODO(title, content, Timestamp.valueOf(date)));
	}
}
