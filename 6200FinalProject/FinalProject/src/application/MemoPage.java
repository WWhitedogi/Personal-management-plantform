package application;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings("deprecation")
public class MemoPage {
	
	private static int tmp;
	
	public void Refresh() throws Exception {
		Scene sc = Main.primary.getScene();
		ScrollPane content = (ScrollPane)sc.lookup("#M_Content");
		VBox pane = (VBox)content.getContent();
		pane.getChildren().clear();
		pane.setSpacing(20);
		ArrayList<MEMO> lst = Main.memos;
		HBox box = new HBox();
		box.getStyleClass().add("memos");
		box.setSpacing(20);
		Parent m;
		for(int i = 0; i < lst.size(); i++) {
			MEMO memo = lst.get(i);
			if(box.getChildren().size() >= 2) {
				pane.getChildren().add(box);
				box = new HBox();
				box.getStyleClass().add("memos");
				box.setSpacing(20);
			}
			m = FXMLLoader.load(getClass().getResource("Memo.fxml"));
			((Text)m.lookup("#memo_title")).setText(memo.get("Title"));
			((Text)m.lookup("#memo_text")).setText(memo.get("Content"));
			Timestamp time = Timestamp.valueOf(memo.get("Date"));
			String date = Main.Month[time.getMonth()] + "." + time.getDate() + " " + (time.getYear() + 1900);
			((Text)m.lookup("#memo_Date")).setText(date);
			((Text)m.lookup("#memo_Day")).setText(Main.Day[time.getDay()]);
			tmp = i;
			((Button)m.lookup("#delete_memo_btn")).setOnAction(new EventHandler<ActionEvent>() {
				int i = tmp;
				public void handle(ActionEvent event) {
					try {
						remove(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			box.getChildren().add(m);
		}
		
		if(!box.getChildren().isEmpty())
			pane.getChildren().add(box);
		
		System.out.println(pane.getChildren().size());
		content.setContent(pane);
	}
	
	public void remove(int i) throws Exception {
		Main.memos.remove(i);
		Main.SaveMemo();
		Refresh();
	}
	
	public void AddMemo(ActionEvent event) throws IOException {
		if(Main.substage != null)
			return;
		Parent p = FXMLLoader.load(getClass().getResource("MemoAdd.fxml"));
		Scene sc = new Scene(p);
		Stage s = new Stage();
		s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->SubstageClose());
		Main.substage = s;
		s.setScene(sc);
		s.show();
	}
	
	public void UpdateMemo(ActionEvent event) throws Exception {
		Scene sc = ((Node)event.getSource()).getScene();
		String title = ((TextField)sc.lookup("#title")).getText();
		String content = ((TextArea)sc.lookup("#content")).getText();
		Timestamp now = Timestamp.from(Instant.now());
		String year = ((TextField)sc.lookup("#Year")).getText().isEmpty() ? Integer.toString(now.getYear() + 1900) : ((TextField)sc.lookup("#Year")).getText();
		String month = ((TextField)sc.lookup("#Month")).getText().isEmpty() ? Integer.toString(now.getMonth() + 1) : ((TextField)sc.lookup("#Month")).getText();
		String day = ((TextField)sc.lookup("#Day")).getText().isEmpty() ? Integer.toString(now.getDate()) : ((TextField)sc.lookup("#Day")).getText();
		
		String date = year + "-" + month + "-" + day + " 00:00:00.0";
		MEMO m = new MEMO(date, title, content);
		Main.memos.add(m);
		Main.SaveMemo();
		close(event);
		Refresh();
	}
	
	private void SubstageClose() {
		Main.substage = null;
	}
	
	@FXML
	public void close(ActionEvent event) throws Exception {
		Main.substage.close();
		Main.substage = null;
	}
}
