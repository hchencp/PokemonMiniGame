public final class Animation implements Action{
    public Entity entity;
    public int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler eventScheduler) {
        nextImage(entity);

        if (repeatCount != 1) {
            eventScheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount - 1, 0)), ((animatedEntity)entity).getAnimationPeriod());
        }
    }



    public void nextImage(Entity entity) {
        entity.setImageIndex(entity.getImageIndex()+1);
    }
}
