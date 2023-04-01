package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;


@SuppressWarnings("deprecation")
public class Main extends Application {
	private static Stack<String> scenes;
	public static Stage primary;
	private final static String[] Greetings = new String[] {"Good morning! Hope you have a great day!", 
			"Hello! How are you doing today?"};
	public static String[] Month = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", 
			"Sep", "Oct", "Nov", "Dec"};
	public static String[] Full_Month = new String[] { "January", "February", "March", "April", "May", 
			"June", "July", "August", "September", "October", "November", "December" };
	public final static String[] Day = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	
	
	private static int tmp;
	private static String strtmp;
	
	
	//DP means dashboard project
	public static final String TODOpath = "save.txt";
	public static final String MEMOpath = "memo.txt";
	private static final String DPpath = "DashProject.txt";
	public static final String Trackpath = "track.txt";
	private static final String DFpath = "dfile.txt";
	private static String DPname;
	private static Timestamp DPdate;
	private static String DFname;
	private static String DFpic;
	
	
	public static ArrayList<TODO> todos;
	public static ArrayList<MEMO> memos;
	public static ArrayList<Track> tracks;
	public static Stage substage;
	

	private int DMemoLimit = 380;
	private final static double DTodoGap = 53;
	private final static double DMemoGap = 240;
	private final static int DTODO_LIMIT = 30;
	public final static int URL_LENGTH = 7;
	
    @FXML
    public void ShiftToDashboard(ActionEvent event) throws Exception {
        ShiftPane.ShiftToDashboard(event);
    }

    @FXML
    public void ShiftToTODO(Event event) throws Exception {
        ShiftPane.ShiftToTODO();
    }

	@FXML
	public void ShiftToMemo(Event event) throws Exception {
		ShiftPane.ShiftToMemo();
	}
	
	@FXML
	public void ShiftToTrack(Event event) throws Exception {
		ShiftPane.ShiftToTrack();
	}
	
	
	public static void Save() throws FileNotFoundException {
		SaveTodo();
		SaveDP();
		SaveMemo();
		SaveDF();
	}

    public static void SaveTodo() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(TODOpath));
        for (TODO todo : todos) {
            todo.save(pw);
        }

        pw.close();
    }

    public static void SaveDP() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(DPpath));
        pw.println(DPname);
        pw.println(DPdate.toString());
        pw.close();
    }

    public static void SaveMemo() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(MEMOpath));
        for (MEMO m : memos)
            m.save(pw);
        pw.close();
    }

    public static void SaveDF() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(DFpath));
        pw.println(DFname);
        pw.println(DFpic);
        pw.close();
    }
    
    public static void SaveTrack() throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter(new File(Trackpath));
    	for(Track t : tracks)
    		t.save(pw);
    	pw.close();
    }

    public static void Alert(String message) throws Exception {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public static long TimestampDifference(Timestamp cur, Timestamp end) {
        return (Timestamp.parse((end.getYear() + 1900) + "/" + (end.getMonth() + 1) + "/" + (end.getDate() + 1)) -
                Timestamp.parse((cur.getYear() + 1900) + "/" + (cur.getMonth() + 1) + "/" + (cur.getDate() + 1)));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void RefreshDashboard(Scene sc) throws Exception {
        Random r = new Random();
        ((Text) sc.lookup("#greeting")).setText(Greetings[r.nextInt(Greetings.length)]);
        RefreshDMemo(sc);
        RefreshDTodo(sc);
        RefreshDP(sc);
        RefreshTrack(sc);
        RefreshDF(sc);
    }

    private void RefreshDMemo(Scene sc) throws Exception {
        Pane pane = (Pane) sc.lookup("#Memo");
        if (pane == null)
            return;
        pane.getChildren().clear();
        double xPos = 0;

        Parent p;
        StringBuffer str;
        for (int i = 0; i < Math.min(2, memos.size()); i++) {
            p = FXMLLoader.load(getClass().getResource("DashMemo.fxml"));
            ((Text) p.lookup("#Day")).setText(Day[Timestamp.valueOf(memos.get(i).get("Date")).getDay()]);
            ((Text) p.lookup("#Title")).setText(memos.get(i).get("Title"));
            str = new StringBuffer();
            str.append(memos.get(i).get("Content"));
            if (str.length() > DMemoLimit) {
                str.delete(DMemoLimit, str.length() - 1);
                str.append("...");
            }
            ((Text) p.lookup("#Content")).setText(str.toString());
            p.setLayoutX(xPos);
            xPos += DMemoGap;
            pane.getChildren().add(p);
        }
    }

    private void RefreshDTodo(Scene sc) throws Exception {
        Pane pane = (Pane) sc.lookup("#TODO_Content");
        if (pane == null)
            return;
        pane.getChildren().clear();

        Parent p;
        int i = 0, indx = 0;
        double pos = 0;
        todos.sort(null);
        Timestamp time, cur;
        for (TODO todo : todos) {
            if (i >= 8)
                break;
            if (todo.isDone()) {
                indx++;
                continue;
            }
            time = Timestamp.valueOf(todo.get("Date"));
            p = FXMLLoader.load(getClass().getResource("ToDo.fxml"));
            String title = todo.get("Title");
            if (title.length() > DTODO_LIMIT)
                title = title.substring(0, DTODO_LIMIT) + "...";
            ((Text) p.lookup("#name")).setText(title);
            Text day = ((Text) p.lookup("#day"));
            Text month = ((Text) p.lookup("#month"));
            day.setText(((time.getDate() + 1) / 10 == 0 ? "0" : "") + time.getDate());
            month.setText(Month[time.getMonth()]);
            cur = Timestamp.from(Instant.now());
            long remain = Math.round(TimestampDifference(cur, time) / 86400000.0);
            if (remain < 0) {
                day.setFill(Color.RED);
                month.setFill(Color.RED);
            } else if (remain < 2) {
                day.setFill(Color.ORANGE);
                month.setFill(Color.ORANGE);
            } else {
                day.setFill(Color.GREEN);
                month.setFill(Color.GREEN);
            }
            ((Text) p.lookup("#remain")).setText(remain < 0 ? "Overdue" : Long.toString(remain) + (remain > 1 ? " Days" : " Day"));
            tmp = indx;
            p.lookup("#t_remove").setOnMouseClicked(new EventHandler<MouseEvent>() {
                int i = tmp;

                public void handle(MouseEvent arg0) {
                    try {
                        remove(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            p.lookup("#t_done").setOnMouseClicked(new EventHandler<MouseEvent>() {
                int i = tmp;

                public void handle(MouseEvent arg0) {
                    try {
                        done(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            p.lookup("#t_edit").setOnMouseClicked(new EventHandler<MouseEvent>() {
                int i = tmp;

                public void handle(MouseEvent arg0) {
                    try {
                        System.out.println("Edit at " + i);
                        Scene sc = EditDT(i);
                        if (sc == null)
                            return;
                        tmp = i;
                        ((Button) sc.lookup("#Done")).setOnAction(new EventHandler<ActionEvent>() {
                            int i = tmp;

                            public void handle(ActionEvent event) {
                                try {
                                    UpdateDT(i, event);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            p.setLayoutY(pos);
            pos += DTodoGap;
            pane.getChildren().add(p);
            i++;
            indx++;
        }
    }

    private void RefreshDP(Scene sc) throws Exception {
        ((Text) sc.lookup("#D_Project")).setText(DPname);
        if (DPdate == null)
            return;
        String date = Month[DPdate.getMonth()] + "." + (DPdate.getDate() / 10 == 0 ? "0" : "") + DPdate.getDate();
        int sub = 0;
        Timestamp cur = Timestamp.from(Instant.now());
        int dh = DPdate.getHours(), ch = cur.getHours();
        if (dh < ch) {
            dh += 24;
            sub++;
        }

        int hours = dh - ch;
        long diff = TimestampDifference(cur, DPdate) - hours - sub * 86400000;
        int days = (int) Math.round(diff / 86400000.0);
        ((Text) sc.lookup("#D_Hours")).setText(Integer.toString(hours));
        ((Text) sc.lookup("#D_Days")).setText(Integer.toString(days));
        ((Text) sc.lookup("#D_Date")).setText(date);
    }

    private void RefreshTrack(Scene sc) throws Exception {
        GridPane table = (GridPane) sc.lookup("#TrackTable");
        Text txt;
        StackPane p;
        Track t;
        String str;
        int indx = 4;
        for (int i = 0; i < 6; i++) {
            t = tracks.get(i);
            txt = new Text(t.get("Company"));
            p = (StackPane) table.getChildren().get(indx);
            indx++;
            p.getChildren().add(txt);

            Timestamp time = Timestamp.valueOf(t.get("Date"));
            str = Month[time.getMonth()] + "." + (time.getDate() < 10 ? "0" : "") + time.getDate() + ", " + (time.getYear() + 1900);
            txt = new Text(str);
            p = (StackPane) table.getChildren().get(indx);
            indx++;
            p.getChildren().add(txt);

            StackPane s = new StackPane();
            s.setPrefHeight(18);
            s.setPrefWidth(47.25);
            txt = new Text(t.get("Status"));
            txt.setFont(Font.font(8));
            s.getChildren().add(new Text(t.get("Status")));
            p = (StackPane) table.getChildren().get(indx);
            indx++;
            p.getChildren().add(s);

            str = t.get("Url").substring(0, 7) + "...";
            Hyperlink link = new Hyperlink(str);
            strtmp = t.get("Url");
            link.setOnAction(new EventHandler<ActionEvent>() {
                String str = strtmp;

                @Override
                public void handle(ActionEvent arg0) {
                    getHostServices().showDocument(str);
                }
            });
            p = (StackPane) table.getChildren().get(indx);
            indx++;
            p.getChildren().add(link);
        }
    }

    private void RefreshDF(Scene sc) throws Exception {
        ((Text) sc.lookup("#u_name")).setText(DFname);
        if (DFpic.isEmpty() || !((new File(DFpic)).exists()) || (new File(DFpic).isDirectory())) {
            System.out.println("User picture file " + DFpath + " NOT FOUND!");
            return;
        }
        ((ImageView) sc.lookup("#u_pic")).setImage(new Image(DFpic));
    }

    private void initialize() throws Exception {
        System.out.println("Initialize");
        initializeToDo();
        initializeMemo();
        initializeDP();
        initializeTrack();
        initializeDF();
    }

    private void initializeMemo() throws Exception {
        System.out.println("Initialize memo");
        memos = new ArrayList<MEMO>();
        MEMO m;
        try (Scanner s = new Scanner(new File(MEMOpath))) {
            while (s.hasNextLine()) {
                m = new MEMO();
                m.load(s);
                memos.add(m);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Memo file NOT FOUND!");
        }
    }

    private void initializeToDo() throws Exception {
        System.out.println("Initialize todo");
        todos = new ArrayList<TODO>();
        TODO t;
        try (Scanner s = new Scanner(new File(TODOpath))) {
            while (s.hasNextLine()) {
                t = new TODO();
                t.load(s);
                todos.add(t);
            }
        } catch (FileNotFoundException ex) {

        }
    }

    private void initializeDP() throws Exception {
        System.out.println("Initialize Dashboard Project");
        try (Scanner s = new Scanner(new File(DPpath))) {
            DPname = s.nextLine();
            DPdate = Timestamp.valueOf(s.nextLine());
        } catch (FileNotFoundException ex) {
            System.out.println("Dash Project File NOT FOUND!");
            DPname = "";
            DPdate = null;
        }
    }

    private void initializeTrack() throws Exception {
        System.out.println("Initialize Tracking");
        tracks = new ArrayList<Track>();
        try (Scanner s = new Scanner(new File(Trackpath))) {
            Track t;
            while (s.hasNextLine()) {
                t = new Track();
                t.load(s);
                tracks.add(t);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Tracking File NOT FOUND!");
        }
    }

    private void initializeDF() throws Exception {
        System.out.println("Initialize Dashboard File");
        try (Scanner s = new Scanner(new File(DFpath))) {
            DFname = s.nextLine();
            DFpic = s.nextLine();
        } catch (FileNotFoundException ex) {
            DFname = "";
            DFpic = "";
            System.out.println("DashFile File NOT FOUND!");
        }
    }

	/*private Scene shift(ActionEvent event, String name) throws Exception {
		Parent editParent = FXMLLoader.load(getClass().getResource(name));
		Scene sc = new Scene(editParent);
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(sc);
		window.show();
		return sc;
	}*/

    private Scene shiftPane(ActionEvent event, String name) throws Exception {
        Parent p = FXMLLoader.load(getClass().getResource(name));
        Scene sc = ((Node) event.getSource()).getScene();
        Pane pane = (Pane) sc.lookup("#Content");
        pane.getChildren().clear();
        pane.getChildren().add(p);

        return sc;
    }

    private Scene shiftPane(MouseEvent event, String name) throws Exception {
        Parent p = FXMLLoader.load(getClass().getResource(name));
        Scene sc = ((Node) event.getSource()).getScene();
        Pane pane = (Pane) sc.lookup("#Content");
        pane.getChildren().clear();
        pane.getChildren().add(p);

        return sc;
    }

    @FXML
    public void EditDashProject(Event event) throws Exception {
        if (substage != null)
            return;
        Parent pane = FXMLLoader.load(getClass().getResource("EditDashProject.fxml"));
        Scene sc = new Scene(pane);
        Stage s = new Stage();
        s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> SubstageClose());
        substage = s;
        s.setScene(sc);
        ((TextField) sc.lookup("#dp_w_name")).setText(DPname);
        ((TextField) sc.lookup("#dp_w_year")).setText(Integer.toString(DPdate.getYear() + 1900));
        ((TextField) sc.lookup("#dp_w_month")).setText(Integer.toString(DPdate.getMonth() + 1));
        ((TextField) sc.lookup("#dp_w_day")).setText(Integer.toString(DPdate.getDate()));
        s.show();
    }

    @FXML
    public void UpdateDashProject(ActionEvent event) throws Exception {
        SaveDP();
        if (!scenes.lastElement().equals("DashBoard.fxml"))
            return;

        Scene sc = ((Node) event.getSource()).getScene();

        DPname = ((TextField) sc.lookup("#dp_w_name")).getText();

        String year = ((TextField) sc.lookup("#dp_w_year")).getText();
        String month = ((TextField) sc.lookup("#dp_w_month")).getText();
        String day = ((TextField) sc.lookup("#dp_w_day")).getText();

        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            DPdate = null;
            return;
        }

        Timestamp cur = Timestamp.from(Instant.now());

        if (year.isEmpty())
            year = Integer.toString(cur.getYear());
        if (month.isEmpty())
            month = Integer.toString(cur.getMonth());
        if (day.isEmpty())
            day = Integer.toString(cur.getDate());

        String date = year + "-" + month + "-" + day + " 00:00:00.0";
        DPdate = Timestamp.valueOf(date);
        substage.close();
        substage = null;
        SaveDP();
        RefreshDP(primary.getScene());
    }

    private Scene EditDT(int i) throws IOException {
        if (substage != null)
            return null;
        Parent pane = FXMLLoader.load(getClass().getResource("EditTodo.fxml"));
        Scene sc = new Scene(pane);
        Stage s = new Stage();
        s.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> SubstageClose());
        substage = s;
        s.setScene(sc);
        ((TextField) sc.lookup("#dt_w_name")).setText(todos.get(i).get("Title"));
        Timestamp date = Timestamp.valueOf(todos.get(i).get("Date"));
        ((TextField) sc.lookup("#dt_w_year")).setText(Integer.toString(date.getYear() + 1900));
        ((TextField) sc.lookup("#dt_w_month")).setText(Integer.toString(date.getMonth() + 1));
        ((TextField) sc.lookup("#dt_w_day")).setText(Integer.toString(date.getDate()));
        s.show();
        return sc;
    }

    private void UpdateDT(int i, ActionEvent event) throws Exception {
        Scene sc = ((Node) event.getSource()).getScene();
        String title = ((TextField) sc.lookup("#dt_w_name")).getText();
        if (title.isEmpty()) {
            Alert("Todo title cannot be empty!");
            return;
        }

        todos.get(i).set("Title", title);
        String year = ((TextField) sc.lookup("#dt_w_year")).getText();
        String month = ((TextField) sc.lookup("#dt_w_month")).getText();
        String day = ((TextField) sc.lookup("#dt_w_day")).getText();
        Timestamp cur = Timestamp.from(Instant.now());

        if (year.isEmpty())
            year = Integer.toString(cur.getYear());
        if (month.isEmpty())
            month = Integer.toString(cur.getMonth());
        if (day.isEmpty())
            day = Integer.toString(cur.getDate());
        String date = year + "-" + month + "-" + day + " 00:00:00.0";
        todos.get(i).set("Date", date);
        substage.close();
        substage = null;
        SaveTodo();
        RefreshDTodo(primary.getScene());
    }

    @FXML
    public void EditDF(MouseEvent event) throws Exception {
        if (substage != null)
            return;
        Parent p = FXMLLoader.load(getClass().getResource("EditDFile.fxml"));
        ((TextField) p.lookup("#u_name")).setText(DFname);
        if (!DFpic.isEmpty() && (new File(DFpic)).exists()) {
            ((TextField) p.lookup("#u_pic")).setText(DFpic);
            ((ImageView) p.lookup("#picture")).setImage(new Image(DFpic));
        }

        Scene s = new Scene(p);
        Stage stage = new Stage();
        stage.setScene(s);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> SubstageClose());
        stage.show();
        substage = stage;
    }

    @FXML
    public void chooseDFPic(ActionEvent event) throws Exception {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open File");
        File file = fc.showOpenDialog(null);
        if (file == null)
            return;

        String pic = file.getPath();
        Scene sc = ((Node) event.getSource()).getScene();
        ((TextField) sc.lookup("#u_pic")).setText(pic);
        ((ImageView) sc.lookup("#picture")).setImage(new Image(pic));
    }

    @FXML
    public void UpdateDF(ActionEvent event) throws Exception {
        Scene sc = ((Node) event.getSource()).getScene();
        DFname = ((TextField) sc.lookup("#u_name")).getText();
        DFpic = ((TextField) sc.lookup("#u_pic")).getText();
        SaveDF();
        RefreshDF(primary.getScene());
        substage.close();
        substage = null;
    }

    @FXML
    public void TODOAddPage(ActionEvent event) throws Exception {
        scenes.push("AddToDo.fxml");
        shiftPane(event, "AddToDo.fxml");
    }

    public void ToDoDetail(MouseEvent event, int i) throws Exception {
        scenes.push("ToDoDetail.fxml");
        Scene sc = shiftPane(event, "ToDoDetail.fxml");
        TODO t = todos.get(i);
        tmp = i;
        ((Text) sc.lookup("#Title")).setText(t.get("Title"));
        ((Text) sc.lookup("#Date")).setText(t.get("Date"));
        Text txt = new Text(t.get("Content"));
        ((TextFlow) sc.lookup("#TODOContent")).getChildren().add(txt);
        double p = t.getCurrentDateProgress();
        ((ProgressBar) sc.lookup("#Progress")).setProgress(p);
    }

    @FXML
    public void AddToDo(ActionEvent event) throws Exception {
        File todo = new File(TODOpath);
        Scene sc = ((Node) event.getSource()).getScene();
        String title = ((TextField) sc.lookup("#title")).getText();
        String content = ((TextArea) sc.lookup("#content")).getText();
        Timestamp deadline;
        try {
            deadline = Timestamp.valueOf(((TextField) sc.lookup("#Year")).getText() + "-"
                    + ((TextField) sc.lookup("#Month")).getText() + "-"
                    + ((TextField) sc.lookup("#Day")).getText() + " 00:00:00.0");
        } catch (IllegalArgumentException e) {
            deadline = Timestamp.from(Instant.now());
        }
        todos.add(new TODO(title, content, deadline));
        todos.sort(null);

        try {
            SaveTodo();
            close(event);
            ShiftPane.ShiftToTODO();
        } catch (FileNotFoundException e) {
            Alert("Error! File \"" + todo.getName() + "\" Not Found!");
        }
    }

    @FXML
    public void Back(ActionEvent event) throws Exception {
        scenes.pop();
        Scene sc = shiftPane(event, scenes.lastElement());
        if (scenes.lastElement().equals("Main.fxml")) {
            RefreshDashboard(sc);
        }
    }

    private void remove(int i) throws Exception {
        System.out.println("Remove at " + i);
        todos.remove(i);
        SaveTodo();
        RefreshDTodo(primary.getScene());
    }

    private void done(int i) throws Exception {
        System.out.println("Done at " + i);
        todos.get(i).Done();
        SaveTodo();
        RefreshDTodo(primary.getScene());
    }

    @FXML
    public void close(ActionEvent event) throws Exception {
        substage.close();
        substage = null;
        //Stage cur = (Stage) ((Node)event.getSource()).getScene().getWindow();
        //cur.hide();
    }

    @FXML
    public void SubstageClose() {
        substage = null;
    }
	
	/*private Scene shift(ActionEvent event, String name) throws Exception {
		Parent editParent = FXMLLoader.load(getClass().getResource(name));
		Scene sc = new Scene(editParent);
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(sc);
		window.show();
		return sc;
	}*/

    @FXML
    public void onClose() {
        if (substage != null)
            substage.close();
        try {
            Save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scenes = new Stack<String>();
        scenes.push("Main.fxml");
        scenes.push("DashBoard.fxml");
        primary = primaryStage;
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> onClose());
        substage = null;
        initialize();
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Random r = new Random();
        ((Pane) root.lookup("#Content")).getChildren().add(FXMLLoader.load(getClass().getResource("DashBoard.fxml")));
        ((Text) root.lookup("#greeting")).setText(Greetings[r.nextInt(Greetings.length)]);
        Scene scene = new Scene(root);
        RefreshDashboard(scene);
        primaryStage.setTitle("6200 Final Project");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
