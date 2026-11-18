package personal.truehorror.core;
public enum CreatureKind {
    HEROBRINE("herobrine","Herobrine",0.6F,1.8F,30,0.29,6),
    WATCHER("watcher","The Watcher",0.65F,4.0F,24,0.3,0),
    MIMIC("mimic","The Mimic",0.7F,1.8F,24,0.32,3),
    CRAWLER("crawler","The Crawler",1.1F,1.0F,22,0.36,4),
    PALE_MAN("pale_man","The Pale Man",0.6F,3.5F,32,0.27,6),
    SHADOW("shadow","The Shadow",0.6F,1.8F,18,0.3,2),
    DWELLER("dweller","The Dweller",1.2F,1.5F,44,0.31,7);
    public final String id,title;
    public final float width,height;
    public final double health,speed,damage;
    CreatureKind(String id,String title,float width,float height,double health,double speed,double damage){
        this.id=id;this.title=title;this.width=width;this.height=height;this.health=health;this.speed=speed;this.damage=damage;
    }
    public static CreatureKind fromId(String id){for(CreatureKind k:values())if(k.id.equals(id))return k;return null;}
}
