package fc.ui;

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
import javafx.util.Callback;
import org.apache.commons.lang3.ArrayUtils;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class DetailController implements Initializable {
    @FXML Label qLabel;
    @FXML ListView<Double> qList;

    private ObservableList<Double> QItems;
    private double q_sum;
    private int action;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        qLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Callback<ListView<Double>, ListCell<Double>> cellFactory = p ->
        {
            // セルを作成
            ListCell<Double> cell = new ListCell<Double>()
            {
                /**
                 * 描画関数
                 * @param item
                 * @param empty
                 */
                @Override public void updateItem(Double item , boolean empty )
                {
                    // 元関数を呼び出し
                    super.updateItem(item, empty);

                    // Null対策
                    if( item == null ){ return; }

                    // セルのプロパティを設定
                    // テキスト
                    String str = action == getIndex() ? "☆ " : "　 ";
                    setText(str + Dir2Str[getIndex()] + ": " + String.format("%1$.3f", item));
                    double v = 255 * item * item * item * item;
                    setBackground(new Background(new BackgroundFill(
                            Color.rgb(255-Math.min(255, (int)(v /q_sum)),
                                    255,
                                    255-Math.min(255, (int)(v /q_sum))
                                    ),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
                }

            };

            return cell;
        };
        qList.setCellFactory(cellFactory);
    }

    public void reQ(double[] q_values, int a){
        q_sum = 0;
        action = a;
        for(double q: q_values){
            q_sum += q*q*q*q;
        }
        QItems = FXCollections.observableArrayList(ArrayUtils.toObject(q_values));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                qList.setItems(QItems);
            }
        });
    }

    private static String[] Dir2Str = new String[]{
            "右 -> 右",
            "右 -> 下",
            "右 -> 左",
            "右 -> 上",
            "下 -> 右",
            "下 -> 下",
            "下 -> 左",
            "下 -> 上",
            "左 -> 右",
            "左 -> 下",
            "左 -> 左",
            "左 -> 上",
            "上 -> 右",
            "上 -> 下",
            "上 -> 左",
            "上 -> 上",
    };
}
