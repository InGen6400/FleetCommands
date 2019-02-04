package fc;

public class Ship {
    // 船の位置座標
    public int x;
    public int y;
    // 獲得した燃料タンクの個数
    public int point = 0;

    // コンストラクタ
    // 初期位置をセットします
    public Ship(int x, int y, int point){
        this.x = x;
        this.y = y;
        this.point = point;
    }

    public Ship(Ship ship){
        this.x = ship.x;
        this.y = ship.y;
        this.point = ship.point;
    }
}
