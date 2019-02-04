package fc.ui;

import fc.Ship;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

public class AiMapController implements Initializable {

    @FXML private Canvas aiCanvas;
    private GraphicsContext gc;

    private String myName;
    private Ship[] prevShip;
    private int prevNum = 3;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prevShip = new Ship[prevNum];
        gc = aiCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0,0, aiCanvas.getWidth(), aiCanvas.getHeight());
    }

    public void redraw(Vector<int[]> energy_v, Hashtable<String, Ship> userTable, String targetName){
        int x,y;
        int tx = userTable.get(targetName).x;
        int ty = userTable.get(targetName).y;
        Enumeration e;
        String name;

        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0,0, aiCanvas.getWidth(), aiCanvas.getHeight());
        gc.setFont(Font.font(null, 16));
        try {
            for( e = energy_v.elements(); e.hasMoreElements(); ){
                int[] p = (int [])e.nextElement();
                x = (p[0] - tx + 128)*2;
                y = (p[1] - ty + 128)*2;

                // 燃料タンクは,白抜きの赤丸で示します
                /*
                gc.setFill(Color.RED);
                gc.fillOval(x - 10, 532 - y - 10, 20, 20);
                gc.setFill(Color.WHITE);
                gc.fillOval(x - 6, 532 - y - 6, 12, 12);
                gc.setFill(Color.BLACK);
                gc.fillText(""+p[2], x-6+2, 532-y+6);
                */
                gc.setFill(Color.HOTPINK);
                gc.fillOval(x - 10, 532 - y - 10, 20, 20);
                if (x < 20)     gc.fillOval(532+x-10,532-y-10,20,20);
                if (x > 532-20) gc.fillOval(-532+x-10,532-y-10,20,20);
                if (y < 20)     gc.fillOval(x-10,-y-10,20,20);
                if (y > 532-20) gc.fillOval(x-10,532*2-y-10,20,20);

                gc.setFill(Color.WHITE);
                gc.fillOval(x - 6, 532 - y - 6, 12, 12);
                if (x < 12)     gc.fillOval(532+x-6,532-y-6,12,12);
                if (x > 532-12) gc.fillOval(-532+x-6,532-y-6,12,12);
                if (y < 12)     gc.fillOval(x-6,-y-6,12,12);
                if (y > 532-12) gc.fillOval(x-6,532*2-y-6,12,12);

                gc.setFill(Color.BLACK);
                gc.fillText(""+p[2], x-4, 532-y+5);
                if (x < 10) gc.fillText(""+p[2], 532+x-4, 532-y+5);
                if (x > 532-10) gc.fillText(""+p[2], x-532-4, 532-y+5);
                if (y < 10) gc.fillText(""+p[2], x-4, -y+5);
                if (y > 532-10) gc.fillText(""+p[2], x-4, 532*2-y+5);
            }
        } catch (Exception err) {
            System.out.println("error in paint:" + err);
        }

        for( int i=0; i<prevShip.length; i++){
            if(prevShip[i] == null)break;
            Ship ship = prevShip[i];
            x = (ship.x - tx + 128)*2;
            y = (ship.y - ty + 128)*2;

            gc.setFont(Font.font(null, 20));
            // 船を表示します
            gc.setFill(Color.color(1.0f, 0.64705884f, 0.0f, (prevShip.length-i)/(prevShip.length+1.0)));
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
        }

        for( e = userTable.keys(); e.hasMoreElements(); ){
            name = e.nextElement().toString();
            Ship ship = (Ship) userTable.get(name);
            x = (ship.x - tx + 128)*2;
            y = (ship.y - ty + 128)*2;

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

    public void SetName(String name){
        myName = name;
    }
}
