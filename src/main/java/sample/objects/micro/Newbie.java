package sample.objects.micro;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

import static javafx.util.Duration.millis;
import static javafx.util.Duration.seconds;
import sample.Main;
import sample.objects.macro.Macro;
import sample.objects.SeaOfThieves;

import static sample.Main.getRandom;

public class Newbie implements Cloneable, Comparable<Newbie> {

    protected static final double IMAGE_SIZE = 100 * SeaOfThieves.SIZE;
    protected static final double HEALTH_HEIGHT = 5 * SeaOfThieves.SIZE;
    protected static final double FONT_SIZE = 14 * SeaOfThieves.SIZE;
    protected static final Rectangle2D.Double UNIT_CONTAINER_BOUNDS = new Rectangle2D.Double(0, 0, IMAGE_SIZE, IMAGE_SIZE + HEALTH_HEIGHT + FONT_SIZE * 1.3 + 5);
    protected static final Point MAX_UNIT = new Point((int) (SeaOfThieves.MAX_X - 5 - UNIT_CONTAINER_BOUNDS.width), (int) (SeaOfThieves.MAX_Y - 5 - UNIT_CONTAINER_BOUNDS.height));
    protected static final Point MIN_UNIT = new Point(5, 5);
    public static final int SPEED = 5;

    public static final int MAX_LENGTH_NAME = 7;
    public static final int MAX_HEALTH = 100;
    public static final int MIN_HEALTH = 1;

    protected static final String[] TEAMS = {"GOOD", "BAD"};
    protected static final String[] NAMES = {"Adam", "Brandon", "Charles", "David", "Ethan", "Frank", "George", "Henry",
            "Isaac", "John", "Kevin", "Liam", "Matthew", "Nathan", "Oliver", "Peter", "Quentin",
            "Robert", "Samuel", "Thomas", "Ulysses", "Victor", "William", "Xavier", "Yves", "Zachary"};

    protected static int defaultValueHealth() {
        return getRandom().nextInt(MAX_HEALTH - MIN_HEALTH + 1) + MIN_HEALTH;
    }
    protected static int defaultValueX() {
        return getRandom().nextInt(MAX_UNIT.x - MIN_UNIT.x + 1) + MIN_UNIT.x;
    }
    protected static int defaultValueY() {
        return getRandom().nextInt(MAX_UNIT.y - MIN_UNIT.y + 1) + MIN_UNIT.y;
    }

    protected Group unitContainer;
    protected String type;
    protected Label unitName;
    protected double unitHealth;
    protected String unitTeam;
    protected int x;
    protected int y;
    protected String inMacro;
    protected ImageView unitImage;
    protected Rectangle healthBar;
    protected Rectangle healthBarBackground;
    protected DropShadow shadow;
    protected DropShadow shadowActive;
    protected boolean active;
    protected int direction;
    protected boolean order;
    protected Macro bigTarget;

    public Group getUnitContainer() {
        return unitContainer;
    }
    public String getUnitName() {
        return unitName.getText();
    }
    public double getUnitHealth() {
        return unitHealth;
    }
    public ImageView getUnitImage() {
        return unitImage;
    }
    public String getUnitTeam() {
        return unitTeam;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getInMacro() {
        return inMacro;
    }
    public int getDirection() {
        return direction;
    }
    public boolean isActive() {
        return active;
    }
    public static Point getMAX_UNIT() {
        return MAX_UNIT;
    }
    public static Point getMIN_UNIT() {
        return MIN_UNIT;
    }
    public boolean isOrder() {
        return order;
    }
    public Macro getBigTarget() {
        return bigTarget;
    }

    public void setUnitName(String name) {
        unitName.setText(name);
    }
    public void setUnitHealth(Double unitHealth) {
        this.unitHealth = unitHealth;
        double healthPercentage = this.unitHealth / MAX_HEALTH;
        if (healthPercentage > 0.7) {
            healthBar.setFill(Color.LIMEGREEN);
        } else if (healthPercentage > 0.4) {
            healthBar.setFill(Color.YELLOW);
        } else {
            healthBar.setFill(Color.RED);
        }
        healthBar.setWidth(healthPercentage * IMAGE_SIZE);
    }
    public void setUnitTeam(String team) {
        unitTeam = team;
        if (team.equals("GOOD")) {
            shadow.setColor(Color.BLUE);
        } else if (team.equals("BAD")) {
            shadow.setColor(Color.RED);
        }
    }
    public void setX(int x) {
        this.x = x;
        setCoordinates();
    }
    public void setY(int y) {
        this.y = y;
        setCoordinates();
    }
    public void setInMacro(String inMacro) {
        this.inMacro = inMacro;
    }
    public void setDirection(int direction) {
        this.direction = direction;
        unitImage.setScaleX(direction);
    }
    public void setActive(boolean active) {
        if (!active) {
            unitImage.setEffect(shadow);
            order = false;
            bigTarget = null;
        }
        else unitImage.setEffect(shadowActive);
        this.active = active;
    }
    public void setOrder(boolean order) {
        this.order = order;
    }
    public void setBigTarget(Macro bigTarget) {
        this.bigTarget = bigTarget;
    }
    public void setCoordinates() {
        unitName.setLayoutX(x);
        unitName.setLayoutY(y);

        healthBarBackground.setLayoutX(x);
        healthBarBackground.setLayoutY(unitName.getLayoutY() + unitName.getFont().getSize() * 1.3 + 5);
        healthBar.setLayoutX(x);
        healthBar.setLayoutY(healthBarBackground.getLayoutY());

        unitImage.setLayoutX(x);
        unitImage.setLayoutY(healthBarBackground.getLayoutY() + healthBarBackground.getHeight());
    }

    public Newbie(String name, double health, String team, int x, int y, boolean active) {

        type = "Newbie";
        inMacro = "null";

        unitName = new Label();
        Image img = new Image(Objects.requireNonNull(Main.class.getResource("images/newbie.png")).toString(), IMAGE_SIZE, IMAGE_SIZE, false, true);
        unitImage = new ImageView(img);
        healthBar = new Rectangle(0, 0, IMAGE_SIZE, HEALTH_HEIGHT);
        healthBarBackground = new Rectangle(0, 0, IMAGE_SIZE, HEALTH_HEIGHT);
        unitContainer = new Group();
        shadow = new DropShadow();
        shadowActive = new DropShadow();
        order = false;

        setUnitName(name);
        setUnitHealth(health);
        setUnitTeam(team);
        initialize();
        setX(x);
        setY(y);
        setActive(active);

        spawnTransition();

        unitContainer.getChildren().addAll(unitName, healthBarBackground, healthBar, unitImage);
    }

    public Newbie() {
        this(NAMES[getRandom().nextInt(NAMES.length)],
                defaultValueHealth(),
                TEAMS[getRandom().nextInt(TEAMS.length)],
                defaultValueX(),
                defaultValueY(),
                false);
        System.out.print("Random newbie appeared: " + this + "\n");
    }

    static {
        System.out.println("Та нехай почнеться битва!");
    }

    {
        System.out.println("Ласкаво просимо до світу піратів!");
    }

    private void initialize() {
        unitName.setFont(Font.font("System", FontWeight.BOLD, FONT_SIZE));
        unitName.setTextFill(Color.WHITE);
        unitName.setAlignment(Pos.CENTER);
        unitName.setPrefWidth(IMAGE_SIZE);
        unitName.setEffect(new Bloom());
        DropShadow nameEffect = new DropShadow();
        nameEffect.setBlurType(BlurType.GAUSSIAN);
        nameEffect.setColor(Color.BLACK);
        nameEffect.setRadius(4);
        nameEffect.setSpread(0.6);
        nameEffect.setInput(unitName.getEffect());
        unitName.setEffect(nameEffect);

        TranslateTransition nameTransition = new TranslateTransition();
        nameTransition.setNode(unitName);
        nameTransition.setDuration(seconds(0.4));
        nameTransition.setFromY(0);
        nameTransition.setToY(-5);
        nameTransition.setInterpolator(Interpolator.EASE_OUT);
        nameTransition.setCycleCount(Animation.INDEFINITE);
        nameTransition.setAutoReverse(true);
        nameTransition.play();

        healthBar.setStroke(Color.BLACK);
        healthBar.setStrokeWidth(0.4);
        healthBar.setArcWidth(5);
        healthBar.setArcHeight(5);
        healthBarBackground.setFill(Color.WHITE);
        healthBarBackground.setStroke(Color.BLACK);
        healthBarBackground.setStrokeWidth(0.4);
        healthBarBackground.setArcWidth(5);
        healthBarBackground.setArcHeight(5);

        direction = 1;
        unitImage.setPreserveRatio(true);
        unitImage.setSmooth(true);
        unitImage.setCache(true);
        unitImage.setCacheHint(CacheHint.QUALITY);

        shadow.setRadius(7);
        shadow.setSpread(0.8);
        unitImage.setEffect(shadow);

        shadowActive.setColor(Color.GREENYELLOW);
        shadowActive.setRadius(10);
        shadowActive.setSpread(0.8);
        shadowActive.setInput(unitImage.getEffect());

        unitImage.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            ScaleTransition scaleTransition = new ScaleTransition(millis(100), unitImage);
            scaleTransition.setToX(1.1 * direction);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });

        unitImage.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            ScaleTransition scaleTransition = new ScaleTransition(millis(100), unitImage);
            scaleTransition.setToX(direction);
            scaleTransition.setToY(1);
            scaleTransition.play();
        });
    }

    public static double parseValue(String value, double defaultValue, double minValue, double maxValue) {
        try {
            double result = Double.parseDouble(value);
            if (result < minValue) {
                return minValue;
            }
            if (result > maxValue) {
                return maxValue;
            }
            return result;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String limitString(String value, String defaultValue) {
        if (value.equals("")) {
            return defaultValue;
        }
        if (value.length() > Newbie.MAX_LENGTH_NAME) {
            return value.substring(0, Newbie.MAX_LENGTH_NAME);
        }
        return value;
    }

    public static String parseTeam(String sTeam) {
        String defaultValue = TEAMS[getRandom().nextInt(TEAMS.length)];
        try {
            if (sTeam.equals("")) {
                return defaultValue;
            }
            return sTeam;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void createNewUnit(String sName, String sHealth, String cTeam, String sX, String sY, boolean active) {
        String name = limitString(sName, NAMES[getRandom().nextInt(NAMES.length)]);
        double h = parseValue(sHealth, defaultValueHealth(), MIN_HEALTH, MAX_HEALTH);
        String team = parseTeam(cTeam);
        int x = (int) parseValue(sX, defaultValueX(), getMIN_UNIT().x, getMAX_UNIT().x);
        int y = (int) parseValue(sY, defaultValueY(), getMIN_UNIT().y, getMAX_UNIT().y);
        Newbie n = new Newbie(name, h, team, x, y, active);
        System.out.println(n);
        Main.getWorld().addNewUnit(n);
    }

    public static void changeUnit(int unitIndex, String sName, String sHealth, String cTeam, String sX, String sY, boolean active) {
        Newbie n = Main.getWorld().getUnits().get(unitIndex);

        String s1 = n.toString();

        n.setUnitName(limitString(sName, NAMES[getRandom().nextInt(NAMES.length)]));
        n.setUnitHealth(parseValue(sHealth, defaultValueHealth(), MIN_HEALTH, MAX_HEALTH));
        n.setUnitTeam(parseTeam(cTeam));
        n.setX((int) parseValue(sX, defaultValueX(), getMIN_UNIT().x, getMAX_UNIT().x));
        n.setY((int) parseValue(sY, defaultValueY(), getMIN_UNIT().y, getMAX_UNIT().y));
        n.setActive(active);

        String s2 = n.toString();
        if (!s1.equals(s2)) System.out.println("Edited:\n" + s1 + "\nto:\n" + s2);
    }

    public void spawnTransition() {
        TranslateTransition translateTransition = new TranslateTransition(millis(150), unitContainer);
        translateTransition.setToY(-100);
        translateTransition.setInterpolator(Interpolator.EASE_IN);
        TranslateTransition backTransition = new TranslateTransition(millis(150), unitContainer);
        backTransition.setToY(0);
        backTransition.setInterpolator(Interpolator.EASE_OUT);
        SequentialTransition sequentialTransition = new SequentialTransition(unitContainer, translateTransition, backTransition);
        sequentialTransition.play();
    }

    public void flipActivation() {
        if (active) {
            unitImage.setEffect(shadow);
            order = false;
            bigTarget = null;
        }
        else unitImage.setEffect(shadowActive);
        active = !active;
    }

    public boolean mouseIsOn(double mx, double my) {
        return unitImage.getBoundsInParent().contains(new Point2D(mx, my));
    }

    public void moveToBase(Macro m) {
        if (m == null) {
            for (Macro macro : Main.getWorld().getMacros()) {
                if (!macro.getTeam().equals(unitTeam)) continue;
                simpleMove(macro.getX(), macro.getY());
            }
        } else simpleMove(m.getX(), m.getY());
    }

    public void move(int dx, int dy, int dir) {
        int finalDX = x + dx;
        int finalDY = y + dy;
        setX(Math.max(Math.min(finalDX, MAX_UNIT.x), MIN_UNIT.x));
        setY(Math.max(Math.min(finalDY, MAX_UNIT.y), MIN_UNIT.y));
        if (dir != 0) {
            direction = dir;
            unitImage.setScaleX(direction);
        }
    }

    public void simpleMove(int x, int y) {
        int dx = 0;
        int dy = 0;
        int dir = 0;
        if (x < this.x - SPEED){
            dx = -SPEED;
            dir = 1;
        }
        else if (x > this.x + SPEED){
            dx = SPEED;
            dir = -1;
        }
        if (y > this.y + SPEED){
            dy = SPEED;
        }
        else if (y < this.y - SPEED){
            dy = -SPEED;
        }
        move(dx, dy, dir);
    }

    @Override
    public String toString() {
        return "Newbie{" +
                "name=" + getUnitName() +
                ", health=" + unitHealth +
                ", team=" + unitTeam +
                ", x=" + x +
                ", y=" + y +
                ", active=" + active +
                '}';
    }

    @Override
    public Newbie clone() throws CloneNotSupportedException {
        Newbie clone = (Newbie) super.clone();
        clone.unitName = new Label();
        clone.unitImage = new ImageView(this.unitImage.getImage());
        clone.healthBar = new Rectangle(0, 0, this.healthBar.getWidth(), this.healthBar.getHeight());
        clone.healthBarBackground = new Rectangle(0, 0, this.healthBarBackground.getWidth(), this.healthBarBackground.getHeight());
        clone.unitContainer = new Group();
        clone.shadow = new DropShadow();
        clone.shadowActive = new DropShadow();
        clone.active = false;
        clone.setUnitName(this.getUnitName());
        clone.setUnitHealth(this.getUnitHealth());
        clone.setUnitTeam(this.getUnitTeam());

        clone.initialize();

        clone.setX((int) parseValue(Double.toString(this.getX() + this.getUnitImage().getLayoutBounds().getMaxX() + 10), this.getX(), MIN_UNIT.x, MAX_UNIT.x));

        clone.spawnTransition();

        clone.unitContainer.getChildren().addAll(clone.unitName, clone.healthBarBackground, clone.healthBar, clone.unitImage);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Newbie newbie = (Newbie) o;
        return Double.compare(newbie.unitHealth, unitHealth) == 0 && x == newbie.x && y == newbie.y && active == newbie.active && direction == newbie.direction && order == newbie.order && Objects.equals(unitContainer, newbie.unitContainer) && Objects.equals(type, newbie.type) && Objects.equals(unitName, newbie.unitName) && Objects.equals(unitTeam, newbie.unitTeam) && Objects.equals(unitImage, newbie.unitImage) && Objects.equals(healthBar, newbie.healthBar) && Objects.equals(healthBarBackground, newbie.healthBarBackground) && Objects.equals(shadow, newbie.shadow) && Objects.equals(shadowActive, newbie.shadowActive) && Objects.equals(bigTarget, newbie.bigTarget);
    }
    @Override
    public int hashCode() {
        return Objects.hash(unitContainer, type, unitName, unitHealth, unitTeam, x, y, unitImage, healthBar, healthBarBackground, shadow, shadowActive, active, direction, order, bigTarget);
    }
    public int compareTo(Newbie o) {
        if( unitHealth<o.unitHealth )return -1;
        if( unitHealth>o.unitHealth )return 1;
        return 0;
    }
    public static Comparator<Newbie> HealthComparator
            = new Comparator<Newbie>() {
        @Override
        public int compare(Newbie f1, Newbie f2) {
            if (f1.unitHealth < f2.unitHealth) return -1;
            if (f1.unitHealth > f2.unitHealth) return 1;
            return 0;
        }
    };
    class NestedClass{
        String nest="It`s nested class";
        Collection<String> t1,t2;
        public void print()
        {
            System.out.println("Посилання на метод");
        }
        public  void ref()
        {
            NestedClass obj= new NestedClass();
            Reference refer=obj::print;
            refer.reference();
            long count = IntStream.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5).filter(w -> w > 0).count(); // Stream API
            System.out.println(count);
        }
    }
    void anonim() {
        Newbie anonimus = new Newbie() {
            void flyingMoto() {
                System.out.print("Анонімний клас");
            }
        };
    }
    @FunctionalInterface
    interface Reference {
        void reference();
    }
}
