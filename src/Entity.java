import processing.core.PImage;

import java.util.List;

public interface Entity {
    String log();

    void addEntity(WorldModel world);
    Point getPosition();
    void setPosition(Point position);
    String getID();
    void setImageIndex(int imageIndex);

    int getImageIndex();

    List<PImage> getImages();


}
