package sample.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sample.Main;
import sample.MiniMap;
import sample.objects.macro.Macro;
import sample.objects.micro.Actions;
import sample.objects.micro.Newbie;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.abs;

public class SeaOfThieves {
    final public static int MAX_X = 3000;
    final public static  int MAX_Y = 2000;
    public static final double SIZE = 1.5;

    private final Pane root;
    private final ArrayList<Newbie> units;
    private final ArrayList<Macro> macros;
    private MiniMap miniMap;

    private final Text goodCoins;
    private final Text badCoins;
    private final Text goBaseText;
    private final Text activeUnits;

    private boolean goBase;

    private boolean victory = false;

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public boolean isGoBase() {
        return goBase;
    }

    public void flipChangeAuto() {
        goBase = !goBase;
    }

    public SeaOfThieves() {
        root = new Pane();
        root.setMinWidth(MAX_X);
        root.setMinHeight(MAX_Y);

        units = new ArrayList<>();
        macros = new ArrayList<>();

        Image mapImage = new Image(Objects.requireNonNull(Main.class.getResource("images/bg.jpeg")).toString());
        ImageView bg = new ImageView(mapImage);
        bg.setFitWidth(MAX_X);
        bg.setFitHeight(MAX_Y);
        bg.setPreserveRatio(false);
        bg.setSmooth(true);
        root.getChildren().add(bg);

        goodCoins = new Text();
        goodCoins.setFill(Color.WHITE);
        goodCoins.setFont(new Font("Monaco", 24));
        root.getChildren().add(goodCoins);

        badCoins = new Text();
        badCoins.setFill(Color.WHITE);
        badCoins.setFont(new Font("Monaco", 24));
        root.getChildren().add(badCoins);

        goBaseText = new Text();
        goBaseText.setFill(Color.WHITE);
        goBaseText.setFont(new Font("Monaco", 24));
        root.getChildren().add(goBaseText);

        activeUnits = new Text();
        activeUnits.setFill(Color.WHITE);
        activeUnits.setFont(new Font("Monaco", 24));
        root.getChildren().add(activeUnits);

        miniMap = new MiniMap();
        root.getChildren().addAll(miniMap.getPane());

        goBase = false;
    }

    public void askWorldwhatToDo(Newbie unit) {
        whatToDo(unit);
    }

    public void askWorldplanning(Newbie unit, Actions ac) {
        planning(unit, ac);
    }

    public void planning(Newbie unit, Actions ac)
    {
        unit.setProcessing(false);
        switch (ac) {
            case TAKECOINS -> Main.getWorld().getMacros().get(2).aimUnit(unit);
            case GOBASE -> {
                if (unit.getUnitTeam().equals("GOOD")) {
                    Main.getWorld().getMacros().get(0).aimUnit(unit);
                }
                else if (unit.getUnitTeam().equals("BAD")) {
                    Main.getWorld().getMacros().get(1).aimUnit(unit);
                }
            }
        }

    }

    public void whatToDo(Newbie unit){
        if (unit.isMonster()) {
            int countEated = 0;
            for (Newbie eatUnit : units) {
                if (eatUnit.isEated()) countEated++;
            }
            if (countEated == units.size()-2) {
                if (!victory) {
                    Newbie monster1 = null;
                    Newbie monster2 = null;
                    for (Newbie unitMonster : units) {
                        if (unitMonster.getMonsterTeam() == 1) monster1 = unitMonster;
                        if (unitMonster.getMonsterTeam() == 2) monster2 = unitMonster;
                    }
                    if (monster1.getCountEat() > monster2.getCountEat()) System.out.println("Монстр1 виграв");
                    else if (monster1.getCountEat() < monster2.getCountEat()) System.out.println("Монстр2 виграв");
                    else System.out.println("Переможців немає!");
                    victory = true;
                }
                return;
            }
            int distance = -1;
            Newbie finalUnit = null;
            for (Newbie unitTarget : units) {
                if (!unitTarget.isMonster() && !unitTarget.isEated()) {
                    int tmp;
                    tmp = abs(unitTarget.getX()-unit.getX()) + abs(unitTarget.getY()-unit.getY());
                    if (distance != -1) {
                        if (tmp < distance) {
                            finalUnit = unitTarget;
                            distance = tmp;
                        }
                    }
                    else {
                        finalUnit = unitTarget;
                        distance = tmp;
                    }
                }
            }

            unit.setUnitTarget(finalUnit);
            unit.setAim((int) (finalUnit.getUnitContainer().getBoundsInParent().getCenterX()-unit.getUnitContainer().getBoundsInParent().getWidth()/2), (int) (finalUnit.getUnitContainer().getBoundsInParent().getCenterY()-unit.getUnitContainer().getBoundsInParent().getHeight()/2));
            return;
        }

        if (Main.getWorld().isGoBase()) {
            if (unit.getUnitTeam().equals("GOOD")) {
                if (!macros.get(0).worksWith(unit)) {
                    macros.get(0).aimUnit(unit);
                    return;
                }
            } else {
                if (!macros.get(1).worksWith(unit)) {
                    macros.get(1).aimUnit(unit);
                    return;
                }
            }
        }

        if (unit.getUnitTeam().equals("GOOD")) {
            if (macros.get(0).worksWith(unit)) return;
        } else {
            if (macros.get(1).worksWith(unit)) return;
        }
        if( macros.get(2).worksWith(unit) )return;

        macros.get(2).aimUnit(unit);
    }

    public void unitsLifeCycle() {
        for (int i = 0; i < units.size(); i++) {
            Newbie unit = units.get(i);
            unit.autoMove();

            for (Newbie unitOn : units) {
                if (!unit.isEated()
                        && !unitOn.isEated()
                        && !unit.isMonster()
                        && !unitOn.isMonster()
                        && !unit.equals(unitOn)
                        && !unit.getUnitTeam().equals(unitOn.getUnitTeam())
                        && unit.getUnitImage().getBoundsInParent().intersects(unitOn.getUnitImage().getBoundsInParent())) {
                    int damage = 0;
                    switch (unitOn.getType()) {
                        case "Newbie" -> damage+=1;
                        case "Enjoyer" -> damage+=2;
                        case "Pro" -> damage+=3;
                    }
                    if (unit.takeDamage(damage)) {
                        deleteUnit(unit);
                        i--;
                        break;
                    }
                }
            }
        }
    }

    public void addNewUnit(Newbie unit) {
        units.add(unit);
        root.getChildren().add(unit.getUnitContainer());
        this.miniMap.addUnit(unit);
    }

    public void deleteUnit(Newbie unit) {
        root.getChildren().remove(unit.getUnitContainer());
        units.remove(unit);
        this.miniMap.deleteUnit(unit);
    }

    public void addNewMacro(Macro macro) {
        macros.add(macro);
        root.getChildren().add(macro.getMacroContainer());
        this.miniMap.addMacro(macro);
    }

    public void deleteMacro(Macro macro) {
        root.getChildren().remove(macro.getMacroContainer());
        macros.remove(macro);
        this.miniMap.deleteMacro(macro);
    }

    public MiniMap getMiniMap() {
        return miniMap;
    }

    public void lifeCycle() {
        macros.get(0).lifeCycle();
        macros.get(1).lifeCycle();
        macros.get(2).lifeCycle();

        unitsLifeCycle();
    }

    public Pane getRoot() {
        return root;
    }

    public Text getGoodCoins() {
        return goodCoins;
    }

    public Text getBadCoins() {
        return badCoins;
    }

    public Text getGoBaseText() {
        return goBaseText;
    }

    public Text getActiveUnits() {
        return activeUnits;
    }

    public ArrayList<Newbie> getUnits() {
        return units;
    }

    public ArrayList<Macro> getMacros() {
        return macros;
    }

    public ArrayList<String> getParamsToChange(int index ){
            Newbie n = units.get(index);
            ArrayList<String> arr= new ArrayList<>();
            arr.add( n.getType() );
            arr.add( n.getUnitName() );
            arr.add( Double.toString(n.getUnitHealth()) );
            arr.add( Integer.toString(n.getIntCoins()) );
            arr.add( n.getUnitTeam() );
            arr.add( Integer.toString(n.getX()) );
            arr.add( Integer.toString(n.getY()) );
            arr.add( Boolean.toString(n.isActive()));
            return arr;
        }

    public ArrayList<String> getUnitsNames() {
        ArrayList<String> arr = new ArrayList<>();
        units.forEach(n -> arr.add(n.toString()));
        return arr;
    }

    public ArrayList<String> getMacrosNames() {
        ArrayList<String> arr = new ArrayList<>();
        macros.forEach(n -> arr.add(n.toString()));
        return arr;
    }

    public void currentStatusINFO(){
        getGoodCoins().setText("Good coins: " + Main.getWorld().getMacros().get(0).getCoins());
        getBadCoins().setText("Bad coins: " + Main.getWorld().getMacros().get(1).getCoins());

        if (Main.getWorld().isGoBase()) getGoBaseText().setText("AutoMove: All Go To Base");
        else getGoBaseText().setText("AutoMove: Normal Auto Move");

        int count = 0;
        Newbie tmp = null;
        for (Newbie unit : units) {
            if (unit.isActive()) {
                tmp = unit;
                count++;
            }
        }
        if (count > 1) getActiveUnits().setText("Активовано " + count + " юнітів");
        else if (count == 1) getActiveUnits().setText("Активовано юніт " + tmp.getUnitName());
        else if (count == 0) getActiveUnits().setText("Немає активних юнітів");
    }

    public void updateChordINFO(){
        getGoodCoins().setX(Main.getScrollX() + 20);
        getGoodCoins().setY(Main.getScrollY() + 30);

        getBadCoins().setX(Main.getScrollX() + 20);
        getBadCoins().setY(Main.getScrollY() + 70);

        getGoBaseText().setX(Main.getScrollX() + 20);
        getGoBaseText().setY(Main.getScrollY() + 110);

        getActiveUnits().setX(Main.getScrollX() + 20);
        getActiveUnits().setY(Main.getScrollY() + 150);


    }

    public void infoOnTop() {
        root.getChildren().remove(getGoodCoins());
        root.getChildren().remove(getBadCoins());
        root.getChildren().remove(getGoBaseText());
        root.getChildren().remove(getActiveUnits());
        root.getChildren().remove(getMiniMap().getPane());

        root.getChildren().add(getGoodCoins());
        root.getChildren().add(getBadCoins());
        root.getChildren().add(getGoBaseText());
        root.getChildren().add(getActiveUnits());
        root.getChildren().add(getMiniMap().getPane());
    }

    public void updateMonsters() {
        for (Newbie unit : units) {
            unit.setMonster(false);
            unit.setEated(false);
            unit.clearAim();
            unit.setProcessing(false);
        }
    }
}
