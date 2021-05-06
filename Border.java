import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class Border {
    public Region drawRegion(){
        Region rect = new Region();
        rect.setStyle("-fx-background-color: transparent; -fx-border-style: solid; -fx-border-width: 5; -fx-border-color: black; -fx-min-width: 100; -fx-min-height:100; -fx-max-width:100; -fx-max-height: 100;");
        return rect;
    }
    public Rectangle drawRectangle(){
        Rectangle rect = new Rectangle();
        rect.setStyle("-fx-stroke: black; -fx-stroke-width: 5;-fx-min-width: 100; -fx-min-height:100; -fx-max-width:100; -fx-max-height: 100;");
        return rect;
    }
}
