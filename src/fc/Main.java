package fc;

import fc.FleetCommands;
import fc.ui.DetailController;
import fc.ui.MainController;
import fc.ui.ObjListController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    public static MainController mainController;
    private static DetailController detailController;
    private static ObjListController objListController;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("./ui/main.fxml"));
        primaryStage.setTitle("げ～むがめん");
        primaryStage.setScene(new Scene(mainLoader.load()));
        primaryStage.setResizable(false);
        primaryStage.setX(20);
        primaryStage.setY(20);
        primaryStage.show();
        primaryStage.setWidth(512);
        primaryStage.setHeight(537);
        primaryStage.showingProperty().addListener((value, oldValue, newValue) -> {
            if(!newValue){
                System.exit(0);
            }
        });
        mainController = mainLoader.getController();

        mainLoader = new FXMLLoader(getClass().getResource("./ui/aiMap.fxml"));
        Stage aiMapStage = new Stage();
        aiMapStage.setTitle("AI中心マップ");
        try {
            aiMapStage.setScene(new Scene(mainLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        aiMapStage.setResizable(false);
        aiMapStage.setY(primaryStage.getY() + 530);
        aiMapStage.setX(primaryStage.getX());
        aiMapStage.setWidth(512);
        aiMapStage.setHeight(512);

        mainLoader = new FXMLLoader(getClass().getResource("./ui/detail.fxml"));
        Stage detailStage = new Stage();
        detailStage.setTitle("詳細");
        try {
            detailStage.setScene(new Scene(mainLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        detailStage.setX(primaryStage.getX() + 520);
        detailStage.setY(primaryStage.getY() + 25);
        detailStage.setWidth(240);
        detailStage.setHeight(512);
        detailController = mainLoader.getController();

        mainLoader = new FXMLLoader(getClass().getResource("./ui/objList.fxml"));
        Stage objListStage = new Stage();
        objListStage.setTitle("オブジェクト詳細");
        try {
            objListStage.setScene(new Scene(mainLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        objListStage.setX(detailStage.getX() + 250);
        objListStage.setY(primaryStage.getY() + 25);
        objListStage.setWidth(400);
        objListStage.setHeight(400);
        objListController = mainLoader.getController();

        mainController.setDetailController(detailController);
        mainController.setPrimaryStage(primaryStage);
        mainController.setAiMapStage(aiMapStage);
        mainController.setDetailStage(detailStage);
        mainController.setObjListStage(objListStage);
        mainController.setObjListController(objListController);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                FleetCommands fc = new FleetCommands();
                fc.Start(args);
                return true;
            }
        };

        Thread t = new Thread(task);
        ExceptionHandler eHandler = new ExceptionHandler();
        t.setUncaughtExceptionHandler(eHandler);
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


    class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            e.printStackTrace();
        }
    }
}
