package fc;

import fc.FleetCommands;
import fc.ui.DetailController;
import fc.ui.MainController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static MainController mainController;
    private static DetailController detailController;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("./ui/main.fxml"));
        primaryStage.setTitle("げ～むがめん");
        primaryStage.setScene(new Scene(mainLoader.load()));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setWidth(512);
        primaryStage.setHeight(537);

        Stage detailStage = new Stage();
        detailStage.initOwner(primaryStage);
        FXMLLoader detailLoader = new FXMLLoader(getClass().getResource("./ui/detail.fxml"));
        detailController = detailLoader.getController();
        detailStage.initStyle(StageStyle.UTILITY);
        detailStage.setTitle("詳細");
        detailStage.setScene(new Scene(detailLoader.load(), 240, 512));
        detailStage.show();
        detailStage.setX(primaryStage.getX() + 520);

        mainController = mainLoader.getController();
        mainController.setDetailController(detailController);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                FleetCommands fc = new FleetCommands();
                fc.Start(args);
                return true;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] command_args) {
        args = command_args;
        Thread.UncaughtExceptionHandler dueh = Thread.getDefaultUncaughtExceptionHandler();
        if (dueh == null) {
            Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                System.out.println("これはデフォルトのUncaughtExceptionHandlerで出力されています。");
                System.out.println("ThreadGroup=" + t.getThreadGroup().getName() + "," + "Thread=" + t.getName());
                e.printStackTrace();//適切なロガーで出力してください。
            });
        }
        launch();
    }
}
