import processing.core.PImage;

import java.util.List;

public final class Stump implements Entity{
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;

    public Stump(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void addEntity(WorldModel world) {
        if (world.withinBounds(this.position)) {
            world.setOccupancyCell(this,this.position);
            world.entities.add(this);
        }
    }
    public Point getPosition()
    {
        return position;
    }
    public void setPosition(Point position)
    {
        this.position = position;
    }
    public String getID()
    {
        return id;
    }
    public int getImageIndex()
    {
        return imageIndex;
    }
    public void setImageIndex(int imageIndex)
    {
        this.imageIndex = imageIndex;
    }
    public List<PImage> getImages(){ return images;}

}
