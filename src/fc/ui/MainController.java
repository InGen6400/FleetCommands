package fc.ui;

import fc.FleetCommands;
import fc.Ship;
import fc.Admiral;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

public class MainController implements Initializable{
    @FXML private AnchorPane anchorPane;
    @FXML private Canvas mainCanvas;

    @FXML private CheckMenuItem aiMapCheck;
    @FXML private CheckMenuItem objListCheck;
    @FXML private CheckMenuItem detailCheck;

    @FXML private Label disco_label;

    private GraphicsContext gc;

    private Stage objListStage;
    private Stage detailStage;
    private Stage aiMapStage;
    private Stage primaryStage;
    private Stage loginStage;

    private DetailController detail;
    private ObjListController objListController;
    private AiMapController aiMapController;
    private LoginController loginController;

    private Thread aiThread = null;
    private Task<Integer> aiTask;

    private FleetCommands fc;

    private String myName;
    private Ship[] prevShip;
    private int prevNum = 3;

    private Admiral admiral;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fc = new FleetCommands();
        prevShip = new Ship[prevNum];
        gc = mainCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());

        aiMapCheck.selectedProperty().addListener((value, oldValue, newValue) -> {
            if(!oldValue && newValue){
                aiMapStage.showingProperty().addListener((observable, oldShow, newShow) -> {
                    if (oldShow && !newShow) {
                        aiMapCheck.setSelected(false);
                    }
                });
                aiMapStage.show();
            }else if(!newValue){
                aiMapStage.hide();
            }
        });

        detailCheck.selectedProperty().addListener((value, oldValue, newValue) -> {
            if(!oldValue && newValue){
                detailStage.showingProperty().addListener((observable, oldShow, newShow) -> {
                    if (oldShow && !newShow) {
                        detailCheck.setSelected(false);
                    }
                });
                detailStage.show();
            }else if(!newValue){
                detailStage.hide();
            }
        });

        objListCheck.selectedProperty().addListener((value, oldValue, newValue) -> {
            if(!oldValue && newValue){
                objListStage.showingProperty().addListener((observable, oldShow, newShow) -> {
                    if (oldShow && !newShow) {
                        objListCheck.setSelected(false);
                    }
                });
                objListStage.show();
            }else if(!newValue){
                objListStage.hide();
            }
        });
    }

    public void Start(String ip, String name) {
        if (aiThread==null || !aiThread.isAlive()) {
            admiral = new  Admiral(null);
            if (fc.login(ip, name)) {
                loginController.OnLoginSuccess();
                myName = name;
                aiMapController.SetName(name);
                Toast.makeText(primaryStage, "ログインしました", 2000, 500, 500);
                disco_label.setVisible(false);
                aiTask = new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        return fc.Start(admiral);
                    }
                };
                aiTask.setOnFailed(wse -> OnFCFailed(aiTask.getValue()));
                aiTask.setOnSucceeded(wse -> OnFCSuccess(aiTask.getValue()));
                aiThread = new Thread(aiTask);
                aiThread.setDaemon(true);
                aiThread.start();
                loginStage.hide();
            }else{
                System.out.println("Login Failed");
                loginController.OnLoginFailed();
            }
        }else{
            loginController.Already();
        }
    }

    public void ShowLoginWindow(){
        loginStage.show();
    }

    public void Logout(){
        if(aiTask != null && aiTask.isRunning()) {
            aiTask.cancel();
            Toast.makeText(primaryStage, "ログアウトしました", 2000, 500, 500);
            disco_label.setVisible(true);
        }else{
            Toast.makeText(primaryStage, "ログアウト済みです", 2000, 500, 500);
        }
    }

    private void OnFCFailed(int state){
        System.out.println("AI エラー終了");
    }

    private void OnFCSuccess(int state){
        switch (state){
            case -1:
                Toast.makeText(primaryStage, "AI エラー終了", 2000, 500, 500);
                break;
            case 1:
                Toast.makeText(primaryStage, "接続が切れました", 2000, 500, 500);
        }
        disco_label.setVisible(true);
    }

    public void OnLoginCanceled(){
        loginStage.hide();
    }

    public void Exit(){
        System.exit(0);
    }

    public void redraw(Vector<int[]> energy_v, Hashtable<String, Ship> userTable){
        int x,y;
        Enumeration e;
        String name;

        if(objListStage.isShowing()) {
            objListController.UpdateList(energy_v, userTable);
        }

        if(aiMapStage.isShowing()){
            aiMapController.redraw(energy_v, userTable, fc.getName());
        }

        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());
        gc.setFont(Font.font(null, 16));
        try {
            for( e = energy_v.elements(); e.hasMoreElements(); ){
                int[] p = (int [])e.nextElement();
                x = p[0]*2;
                y = p[1]*2;

                // 燃料タンクは,白抜きの赤丸で示します
                gc.setFill(Color.HOTPINK);
                gc.fillOval(x - 10, 532 - y - 10, 20, 20);
                gc.setFill(Color.WHITE);
                gc.fillOval(x - 6, 532 - y - 6, 12, 12);
                gc.setFill(Color.BLACK);
                gc.fillText(""+p[2], x-6+2, 532-y+6);
            }
        } catch (Exception err) {
            System.out.println("error in paint:" + err);
        }

        for( int i=0; i<prevShip.length; i++){
            if(prevShip[i] == null)break;
            Ship ship = prevShip[i];
            x = ship.x*2;
            y = ship.y*2;

            gc.setFont(Font.font(null, 20));
            // 船を表示します
            gc.setFill(Color.color(1.0f, 0.64705884f, 0.0f, (prevShip.length-i)/(prevShip.length+1.0)));
            gc.fillOval(x - 20, 532 - y - 20, 40, 40);
            if (x < 40)     gc.fillOval(532+x-20,532-y-20,40,40);
            if (x > 532-40) gc.fillOval(-532+x-20,532-y-20,40,40);
            if (y < 40)     gc.fillOval(x-20,-y-20,40,40);
            if (y > 532-40) gc.fillOval(x-20,1064-y-20,40,40);
            // 得点を船の右下に表示します
            gc.fillText(""+ship.point, x + 20, 532 - y + 20); ;
            if (x > 532-60) gc.fillText(""+ship.point,-532+x+20, 532-y+20);
            if (y < 40) gc.fillText(""+ship.point,x+20, -y+20);
            if (x>532-60&&y<40) gc.fillText(""+ship.point,-532+x+20,-y+20);
        }

        for( e = userTable.keys(); e.hasMoreElements(); ){
            name = e.nextElement().toString();
            Ship ship = (Ship) userTable.get(name);
            x = ship.x*2;
            y = ship.y*2;

            gc.setFont(Font.font(null, 20));
            // 影つきにする（黒で一回描画）
            // 船を表示します
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 19, 532 - y - 19, 40, 40);
            if (x < 40)     gc.fillOval(532+x-19,532-y-19,40,40);
            if (x > 532-40) gc.fillOval(-532+x-19,532-y-19,40,40);
            if (y < 40)     gc.fillOval(x-19,-y-19,40,40);
            if (y > 532-40) gc.fillOval(x-19,1064-y-19,40,40);
            // 得点を船の右下に表示します
            gc.fillText(""+ship.point, x + 21, 532 - y + 19) ;
            if (x > 532-60) gc.fillText(""+ship.point,-532+x+21, 532-y+19);
            if (y < 40) gc.fillText(""+ship.point,x+21, -y+19);
            if (x>532-60&&y<40) gc.fillText(""+ship.point,-532+x+21,-y+19);
            // 名前を船の右上に表示します
            gc.fillText(name, x+21, 532-y-19) ;
            if (x > 532-80) gc.fillText(name, -532+x+21, 532-y-19);
            if (y > 532-40) gc.fillText(name, x+21, 1064-y-19);
            if (x>532-80&&y>532-40) gc.fillText(name, -532+x+21, 1064-y-19);

            // 船を表示します
            if(name.equalsIgnoreCase(myName)){
                gc.setFill(Color.ORANGE);
                for(int i=prevShip.length-1; i>=1; i--){
                    prevShip[i] = prevShip[i-1];
                }
                prevShip[0] = new Ship(ship);
            }else {
                gc.setFill(Color.GREENYELLOW);
            }
            gc.fillOval(x - 20, 532 - y - 20, 40, 40);
            if (x < 40)     gc.fillOval(532+x-20,532-y-20,40,40);
            if (x > 532-40) gc.fillOval(-532+x-20,532-y-20,40,40);
            if (y < 40)     gc.fillOval(x-20,-y-20,40,40);
            if (y > 532-40) gc.fillOval(x-20,1064-y-20,40,40);
            // 得点を船の右下に表示します
            gc.fillText(""+ship.point, x + 20, 532 - y + 20); ;
            if (x > 532-60) gc.fillText(""+ship.point,-532+x+20, 532-y+20);
            if (y < 40) gc.fillText(""+ship.point,x+20, -y+20);
            if (x>532-60&&y<40) gc.fillText(""+ship.point,-532+x+20,-y+20);
            // 名前を船の右上に表示します
            gc.fillText(name, x+20, 532-y-20) ;
            if (x > 532-80) gc.fillText(name, -532+x+20, 532-y-20);
            if (y > 532-40) gc.fillText(name, x+20, 1064-y-20);
            if (x>532-80&&y>532-40) gc.fillText(name, -532+x+20, 1064-y-20);
        }
    }

    public void drawQ(double[] q_values, int action){
        detail.reQ(q_values, action);
        // 進行方向描画
        /*
        double baseX = prevShip[0].x*2;
        double baseY = prevShip[0].y*2;

        double

        gc.fillPolygon(new double[]{+0, prevShip[0].x*2+5, prevShip[0].x*2-5}, new double[]{+5+30, prevShip[0].y*2-5+30, prevShip[0].y*2-5+30}, 3);
        */
    }

    public void setDetailController(DetailController dc){
        detail = dc;
    }
    public void setObjListController(ObjListController c){
        objListController = c;
    }
    public void setLoginController(LoginController c){
        loginController = c;
    }
    public void setAiMapController(AiMapController c){
        aiMapController = c;
    }

    public void setPrimaryStage(Stage primary){
        primaryStage = primary;
    }
    public void setAiMapStage(Stage ai){
        aiMapStage = ai;
        aiMapCheck.setSelected(true);
    }
    public void setDetailStage(Stage s) {
        detailStage = s;
        detail.reQ(new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}, 0);
        detailCheck.setSelected(true);
    }
    public void setObjListStage(Stage s){
        objListStage = s;
        objListCheck.setSelected(true);
    }
    public void setLoginStage(Stage s){
        loginStage = s;
    }
}
