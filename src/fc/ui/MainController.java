package fc.ui;

import fc.Ship;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

public class MainController implements Initializable{
    @FXML private AnchorPane anchorPane;
    @FXML private Canvas mainCanvas;
    private GraphicsContext gc;
    private DetailController detail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = mainCanvas.getGraphicsContext2D();
        draw();
    }

    public void redraw(Vector<int[]> energy_v, Hashtable<String, Ship> userTable){
        int x,y;
        Enumeration e;
        try {
            for( e = energy_v.elements(); e.hasMoreElements(); ){
                int[] p = (int [])e.nextElement();
                x = p[0]*2;
                y = p[1]*2;

                // 燃料タンクは,白抜きの赤丸で示します
                gc.setFill(Color.RED);
                gc.fillOval(x - 10, 532 - y - 10, 20, 20);
                gc.setFill(Color.WHITE);
                gc.fillOval(x - 6, 532 - y - 6, 12, 12);
                gc.setFill(Color.BLACK);
                gc.fillText(""+p[2], x-6+2, 532-y+6);
            }
        } catch (Exception err) {
            System.out.println("error in paint:" + err);
        }
    }

    private void draw(){
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLACK);//線を赤に変更
        gc.fillRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());
    }
    public void setDetailController(DetailController dc){
        detail = dc;
    }
}
