public final class Activity implements Action{
    public Entity entity;
    public WorldModel world;
    public ImageStore imageStore;


    public Activity(Entity entity, WorldModel world, ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }
    public void executeAction(EventScheduler eventScheduler) {
        ((active)entity).executeActivity(world, imageStore, eventScheduler);
        }
    }

