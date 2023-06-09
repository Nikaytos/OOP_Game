package sample.objects.micro;

import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Coins implements Cloneable {
    private Label count;

    public Coins (int f, int x, int y) {
        String str= Integer.toString(f);

        count = new Label(str);

        initialize();

        setCoordinates(x, y);
    }

    public Coins (int f) {
        String str= Integer.toString(f);

        count = new Label(str);

        initialize();
    }

    private void initialize() {
        count.setFont(Font.font("System", FontWeight.BOLD, count.getFont().getSize()));
        count.setTextFill(Color.YELLOW);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(4);

        count.setEffect(shadow);
    }

    public void setCoordinates(int x, int y){
        count.setLayoutX(x);
        count.setLayoutY(y);
    }

    public Label getCount() {
        return count;
    }

    public int getCoinsCount() {
        return Integer.parseInt(count.getText());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        String str = count.getText();
        int f = Integer.parseInt(str);
        int x= (int) count.getLayoutX();
        int y= (int) count.getLayoutY();

        Coins tmp = new Coins(f, x, y);
        return tmp;
    }
}
