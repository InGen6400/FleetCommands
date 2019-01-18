package fc.ui;

import fc.FleetCommands;
import fc.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.event.ActionEvent;

public class LoginController {

    @FXML private TextField ipInput;
    @FXML private TextField nameInput;
    @FXML private Text msgText;
    private int failCount;

    private MainController mc;

    @FXML private void OnLoginButtonClicked(){
        mc.Start(ipInput.getText(), nameInput.getText());
    }

    @FXML protected void OnCancelButtonClicked(){
        failCount = 0;
        msgText.setText("");
        mc.OnLoginCanceled();
    }

    public void OnLoginFailed(){
        msgText.setText("ログインに失敗しました\n　" + failMessage[Math.min(failCount, failMessage.length-1)]);
        failCount++;
    }
    public void OnLoginSuccess(){
        failCount = 0;
    }
    public void Already(){
        msgText.setText("AIが活動中です．切断するにはログオフしてください");
    }

    public void setMC(MainController c){ mc = c; }

    private String[] failMessage = new String[]{
            "サーバーのIPアドレスを間違えていませんか？",
            "ネットにはつながっていますか？",
            "同じ名前ですでにログインされていないですか？",
            "タイプミス・・・ではないですよね．",
            "まだログインできないんですか？",
            "粘りますね．．．",
            "そろそろ諦めてよいのでは？",
            "そろっとしゃべることがなくなってきました．．．",
            "...............ﾊﾞｲﾊﾞｲ(´･ω･`)ﾉｼ"
    };
}
