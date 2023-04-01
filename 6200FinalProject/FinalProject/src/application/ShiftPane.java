package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ShiftPane {
	public static Scene ShiftToDashboard(ActionEvent event) throws Exception {
		Scene sc = shiftPane("DashBoard.fxml");
		Main m = new Main();
		m.RefreshDashboard(sc);
		return sc;
	}
	
	public static Scene ShiftToTODO() throws Exception {
		Scene sc = shiftPane("ToDoPage.fxml");
		ToDoPage t = new ToDoPage();
		t.Refresh(sc);
		return sc;
	}
	
	public static Scene ShiftToTrack() throws Exception {
		Scene scene =  shiftPane("Track.fxml");
		TrackPage trackPage = new TrackPage();
		trackPage.Refresh();
		return scene;
	}
	
	public static Scene ShiftToMemo() throws  Exception{
		Scene sc = shiftPane("MemoPage.fxml");
		MemoPage memoPage = new MemoPage();
		memoPage.Refresh();
		return sc;
	}
	private static Scene shiftPane(String name) throws Exception {
		Parent p = FXMLLoader.load(ShiftPane.class.getResource(name));
		Scene sc = Main.primary.getScene();
		Pane pane = (Pane)sc.lookup("#Content");
		pane.getChildren().clear();
		pane.getChildren().add(p);
		
		return sc;
	}
}
