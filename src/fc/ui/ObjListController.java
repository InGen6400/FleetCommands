package fc.ui;

import fc.Ship;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class ObjListController implements Initializable {
    @FXML ListView<String> tankList;
    @FXML ListView<String> shipList;
    @FXML Label tankLabel;
    @FXML Label shipLabel;

    private ObservableList<String> sim;
    private ObservableList<String> tim;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tankLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        shipLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void UpdateList(Vector<int[]> energy_v, Hashtable<String, Ship> userTable){
        List<Map.Entry<String, Ship>> mapOrderList = new ArrayList<>(userTable.entrySet());
        mapOrderList.sort((o1, o2) -> o2.getValue().point - o1.getValue().point);

        List<String> ships = new LinkedList<String>();
        for(Map.Entry<String, Ship> entry: mapOrderList){
            ships.add(entry.getKey() + ": " + entry.getValue().point);
        }
        List<String> tanks = new LinkedList<String>();
        for(int i=0; i<energy_v.size(); i++){
            tanks.add(""+energy_v.get(i)[2]+": ("+energy_v.get(i)[0]+", "+energy_v.get(i)[1]+")");
        }
        sim = FXCollections.observableArrayList(ships.toArray(new String[ships.size()]));
        tim = FXCollections.observableArrayList(tanks.toArray(new String[tanks.size()]));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                shipList.setItems(sim);
                tankList.setItems(tim);
            }
        });
    }
}
