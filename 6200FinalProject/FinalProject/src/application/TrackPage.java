package application;

import javafx.application.Application;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

@SuppressWarnings("deprecation")
public class TrackPage extends Application {
	
	
	private final static int URL = 3;
	private final static int DATE = 1;
	private final static int OPS = 5;
	
	private static String strtmp;
	private static int tmp;
	
	// edit
	
	public Scene TrackAdd(ActionEvent event) throws Exception {
		if(Main.substage != null)
			return null;
		
		Parent pane = FXMLLoader.load(getClass().getResource("EditTrack.fxml"));
		Scene sc = new Scene(pane);
		Stage s = new Stage();
		s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->SubstageClose());
		s.setScene(sc);
		s.show();
		Main.substage = s;
		return s.getScene();
	}
	
	private void SubstageClose() {
		Main.substage = null;
	}
	
	@FXML
	public void Add(ActionEvent event) throws Exception{
		Scene sc = ((Node)event.getSource()).getScene();
		String company = ((TextField)sc.lookup("#track_company")).getText();
		String date = ((TextField)sc.lookup("#track_year")).getText() + "-" 
				+ ((TextField)sc.lookup("#track_month")).getText() + "-"
				+ ((TextField)sc.lookup("#track_date")).getText() + " 00:00:00.0";
		String status = ((TextField)sc.lookup("#track_status")).getText();
		String url = ((TextField)sc.lookup("#track_url")).getText();
		String comment = ((TextField)sc.lookup("#track_comment")).getText();
		Track t = new Track(company, date, status, url, comment);
		Main.tracks.add(t);
		Main.SaveTrack();
		close(event);
		ShiftPane.ShiftToTrack();
	}
	
	public void Edit(int i) throws IOException {
		if(Main.substage != null)
			return;
		
		Track track = Main.tracks.get(i);
		Parent operation = FXMLLoader.load(getClass().getResource("EditTrackDetail.fxml"));
		((TextField)operation.lookup("#track_company")).setText(track.get("Company"));
		Timestamp time = Timestamp.valueOf(track.get("Date"));
		((TextField)operation.lookup("#track_year")).setText(Integer.toString(time.getYear() + 1900));
		((TextField)operation.lookup("#track_month")).setText(Integer.toString(time.getMonth() + 1));
		((TextField)operation.lookup("#track_date")).setText(Integer.toString(time.getDate()));
		((TextField)operation.lookup("#track_status")).setText(track.get("Status"));
		((TextField)operation.lookup("#track_url")).setText(track.get("Url"));
		((TextField)operation.lookup("#track_comment")).setText(track.get("Comment"));
		Scene sc = new Scene(operation);
		Stage s = new Stage();
		s.setScene(sc);
		s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->SubstageClose());
		s.show();
		Main.substage = s;
		tmp = i;
	}
	
	public void Update(ActionEvent event) throws Exception {
		Scene sc = ((Node)event.getSource()).getScene();
		String company = ((TextField)sc.lookup("#track_company")).getText();
		String date = ((TextField)sc.lookup("#track_year")).getText() + "-" 
				+ ((TextField)sc.lookup("#track_month")).getText() + "-"
				+ ((TextField)sc.lookup("#track_date")).getText() + " 00:00:00.0";
		String status = ((TextField)sc.lookup("#track_status")).getText();
		String url = ((TextField)sc.lookup("#track_url")).getText();
		String comment = ((TextField)sc.lookup("#track_comment")).getText();
		Track t = new Track(company, date, status, url, comment);
		Main.tracks.set(tmp, t);
		Main.SaveTrack();
		close(event);
		ShiftPane.ShiftToTrack();
	}
	
	public void Refresh() throws Exception {
		Scene sc = Main.primary.getScene();
		ScrollPane pane = (ScrollPane)sc.lookup("#Track_Content");
		ArrayList<Track> lst = Main.tracks;
		GridPane p = (GridPane)pane.getContent();
		for(int i = 0; i < lst.size(); i++) {
			tmp = i;
			ArrayList<String> keys = lst.get(i).getKeys();
			for(int j = 0; j < keys.size(); j++) {
				String str = lst.get(i).get(keys.get(j));
				if(j == OPS) {
					Parent operation = FXMLLoader.load(getClass().getResource("TrackOps.fxml"));
					GridPane.setRowIndex(operation, i + 1);
					GridPane.setColumnIndex(operation, j);
					p.getChildren().add(operation);
					operation.lookup("#t_remove").setOnMouseClicked(new EventHandler<MouseEvent>() {
						int remove_tmp = tmp;
						public void handle(MouseEvent event) {
							try {
								Remove(remove_tmp);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					operation.lookup("#t_edit").setOnMouseClicked(new EventHandler<MouseEvent>() {
						int edit_tmp = tmp;
						public void handle(MouseEvent event) {
							try {
								Edit(edit_tmp);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					continue;
				}
				else if(j == URL) {
					strtmp = str;
					if(str.length() > Main.URL_LENGTH)
						str = str.substring(0, Main.URL_LENGTH) + "...";
					Hyperlink link = new Hyperlink(str);
					link.setOnAction(new EventHandler<ActionEvent>() {
						String str = strtmp;
						
						@Override
						public void handle(ActionEvent arg0) {
							getHostServices().showDocument(str);
						}
					});
					
					StackPane sp = new StackPane();
					sp.getChildren().add(link);
					GridPane.setRowIndex(sp, i + 1);
					GridPane.setColumnIndex(sp, j);
					p.getChildren().add(sp);
					continue;
					
				} else if(j == DATE){
					Timestamp time = Timestamp.valueOf(str);
					str = Main.Month[time.getMonth()] + "." + time.getDate() + ", " + (time.getYear() + 1900);
				}
				
				StackPane sp = new StackPane();
				Text txt = new Text(str);
				sp.getChildren().add(txt);
				GridPane.setRowIndex(sp, i + 1);
				GridPane.setColumnIndex(sp, j);
				p.getChildren().add(sp);
			}
		}
	}
	
	public void Remove(int i) throws Exception{
		Main.tracks.remove(i);
		Main.SaveTrack();
		ShiftPane.ShiftToTrack();
	}
	
	@FXML
	public void close(ActionEvent event) {
		Main.substage.close();
		Main.substage = null;
	}
	
	public void RefreshGrid() {
		Scene sc = Main.primary.getScene();
		GridPane pane = (GridPane)(((ScrollPane)sc.lookup("#Track_Content")).getContent());
		while(pane.getChildren().size() > 6) {
			pane.getChildren().remove(pane.getChildren().size() - 1);
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
