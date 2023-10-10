import processing.core.PImage;

import java.util.List;
public final class Obstacle implements animatedEntity{
    public String id;
    public Point position;
    public List<PImage> images;
    public double animationPeriod;
    public int imageIndex;


    public Obstacle(String id, Point position,  double animationPeriod,List<PImage> images) {
        this.id = id;
        this.position = position;
        this.animationPeriod = animationPeriod;
        this.images = images;
        this.imageIndex = 0;
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
    public double getAnimationPeriod() {
        return this.animationPeriod;
    }
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());



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


