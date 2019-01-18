package fc;

import fc.ui.MainController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class FleetCommands{

    private MainController ui;

    enum Dir{RIGHT, DOWN, LEFT, UP, NONE}
    // ロボットの動作タイミングを規定する変数sleeptime
    private int sleeptime = 500;
    // ロボットがlogoutするまでの時間を規定する変数timeTolive
    int timeTolive = 50 ;

    private String name;
    // 次の更新時の移動
    private Dir[] nextMove = new Dir[2];
    private int my_x, my_y;

    private static Vector<int[]> energy_v; // 燃料タンクの位置情報リスト
    private static Hashtable<String, Ship> userTable = null;
    private Admiral admiral;

    private boolean isAlive = true;

    public int Start(){
        ui = Main.mainController;
        userTable = new Hashtable<>();
        energy_v = new Vector<>();
        admiral = new Admiral();
        nextMove[0] = Dir.NONE;
        nextMove[1] = Dir.NONE;

        return mainLoop();
    }

    private int mainLoop(){
        while(isAlive) {
            try {
                int state = Reload();
                if(state != 0){
                    return state;
                }
                ui.redraw(energy_v, userTable);
                Admiral.DecideResult res = admiral.DecideMove(energy_v, userTable, my_x, my_y);
                int action = getMaxIndex(res.q_values);
                ui.drawQ(res.q_values, action);
                nextMove = Action2Move(action);
            }catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

            try {
                sleep(sleeptime);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    private int getMaxIndex(double[] doubles){
        double max = Double.MIN_VALUE;
        int ret = -1;
        for(int i=0; i<doubles.length; i++){
            if(doubles[i]>max){
                max = doubles[i];
                ret = i;
            }
        }
        return ret;
    }

    public String getName(){
        return name;
    }

    private Dir[] Action2Move(int action){
        switch(action) {
            case 0:
                return new Dir[]{Dir.RIGHT, Dir.RIGHT};
            case 1:
                return new Dir[]{Dir.RIGHT, Dir.DOWN};
            case 2:
                return new Dir[]{Dir.RIGHT, Dir.LEFT};
            case 3:
                return new Dir[]{Dir.RIGHT, Dir.UP};
            case 4:
                return new Dir[]{Dir.DOWN, Dir.RIGHT};
            case 5:
                return new Dir[]{Dir.DOWN, Dir.DOWN};
            case 6:
                return new Dir[]{Dir.DOWN, Dir.LEFT};
            case 7:
                return new Dir[]{Dir.DOWN, Dir.UP};
            case 8:
                return new Dir[]{Dir.LEFT, Dir.RIGHT};
            case 9:
                return new Dir[]{Dir.LEFT, Dir.DOWN};
            case 10:
                return new Dir[]{Dir.LEFT, Dir.LEFT};
            case 11:
                return new Dir[]{Dir.LEFT, Dir.UP};
            case 12:
                return new Dir[]{Dir.UP, Dir.RIGHT};
            case 13:
                return new Dir[]{Dir.UP, Dir.DOWN};
            case 14:
                return new Dir[]{Dir.UP, Dir.LEFT};
            case 15:
                return new Dir[]{Dir.UP, Dir.UP};
            default:
                System.out.println("Error: 不正なaction: " + action);
                return null;
        }
    }

    private void Move(Dir dir){
        switch (dir){
            case DOWN:
                out.println("down");
                break;
            case UP:
                out.println("up");
                break;
            case RIGHT:
                out.println("right");
                break;
            case LEFT:
                out.println("left");
                break;
        }
    }

    private int Reload(){

        Move(nextMove[0]);
        Move(nextMove[1]);

        // サーバにstatコマンドを送付し,盤面の様子などの情報を得ます
        out.println("stat");
        out.flush();

        try {
            String line = in.readLine();// サーバからの入力の読み込み

            //ship_infoから始まる船の情報の先頭行を探します
            while (!"ship_info".equalsIgnoreCase(line))
                line = in.readLine();

            // 船の情報ship_infoの表示
            // ship_infoはピリオドのみの行で終了です
            line = in.readLine();
            while (!".".equals(line)){
                StringTokenizer st = new StringTokenizer(line);
                // 名前を読み取ります
                String obj_name = st.nextToken().trim();

                // 船の位置座標を読み取ります
                int x = Integer.parseInt(st.nextToken()) ;
                int y = Integer.parseInt(st.nextToken()) ;
                int point = Integer.parseInt(st.nextToken()) ;

                // 船一覧に登録
                if(userTable.containsKey(obj_name)){
                    userTable.get(obj_name).x = x;
                    userTable.get(obj_name).y = y;
                    userTable.get(obj_name).point = point;
                }else{
                    userTable.put(obj_name, new Ship(x, y, point));
                }

                // 自分の船なら
                if(obj_name.equals(name)){
                    my_x = x;
                    my_y = y;
                }
                // 次の１行を読み取ります
                line = in.readLine();
            }

            // energy_infoから始まる,燃料タンクの情報を待ち受けます
            while (!"energy_info".equalsIgnoreCase(line))
                line = in.readLine();

            energy_v.clear();
            // 燃料タンクの情報energy_infoの表示
            // energy_infoはピリオドのみの行で終了です
            line = in.readLine();
            while (!".".equals(line)){
                StringTokenizer st = new StringTokenizer(line);

                // 燃料タンクの位置座標を読み取ります
                int x   = Integer.parseInt(st.nextToken()) ;
                int y   = Integer.parseInt(st.nextToken()) ;
                int ene = Integer.parseInt(st.nextToken()) ;
                int[] e = new int[4];
                // エネルギー一覧に登録
                e[0] = x;
                e[1] = y;
                e[2] = ene;
                // 1,2,3,4のどれでも割り切れる数12 -> 必ず整数に
                energy_v.addElement(e);

                // 次の１行を読み取ります
                line = in.readLine();
            }
        }catch (IOException e){
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    // login関連のオブジェクト
    private Socket server;// ゲームサーバとの接続ソケット
    private int port = 10000;// 接続ポート
    private BufferedReader in;// 入力ストリーム
    PrintWriter out;// 出力ストリーム
    // loginメソッド
    // サーバへのlogin処理を行います
    public boolean login(String host, String name){
        try {
            // サーバとの接続
            this.name = name;
            server = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(
                    server.getInputStream()));
            out = new PrintWriter(server.getOutputStream());

            // loginコマンドの送付
            out.println("login " + name);
            out.flush();
            sleep(10);
            out.write("");
        }catch(Exception e){
            return false;
        }
        return true;
    }
}
