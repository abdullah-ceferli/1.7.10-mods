package personal.truehorror.entity;

import net.minecraft.world.World;
import personal.truehorror.core.CreatureKind;

public final class Monsters {
    public static final class Herobrine extends DreadEntity {public Herobrine(World w){super(w);}public CreatureKind kind(){return CreatureKind.HEROBRINE;}}
    public static final class Watcher extends DreadEntity {public Watcher(World w){super(w);}public CreatureKind kind(){return CreatureKind.WATCHER;}}
    public static final class Mimic extends DreadEntity {public Mimic(World w){super(w);}public CreatureKind kind(){return CreatureKind.MIMIC;}}
    public static final class Crawler extends DreadEntity {public Crawler(World w){super(w);}public CreatureKind kind(){return CreatureKind.CRAWLER;}}
    public static final class PaleMan extends DreadEntity {public PaleMan(World w){super(w);}public CreatureKind kind(){return CreatureKind.PALE_MAN;}}
    public static final class Shadow extends DreadEntity {public Shadow(World w){super(w);}public CreatureKind kind(){return CreatureKind.SHADOW;}}
    public static final class Dweller extends DreadEntity {public Dweller(World w){super(w);}public CreatureKind kind(){return CreatureKind.DWELLER;}}
    public static DreadEntity create(CreatureKind kind,World world){
        switch(kind){case HEROBRINE:return new Herobrine(world);case WATCHER:return new Watcher(world);case MIMIC:return new Mimic(world);
            case CRAWLER:return new Crawler(world);case PALE_MAN:return new PaleMan(world);case SHADOW:return new Shadow(world);default:return new Dweller(world);}
    }
}
