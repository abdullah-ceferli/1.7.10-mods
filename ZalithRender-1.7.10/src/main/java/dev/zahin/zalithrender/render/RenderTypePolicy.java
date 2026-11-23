package dev.zahin.zalithrender.render;

import java.lang.reflect.Method;

/** Inherited vanilla render types include ordinary modded ores and crops. Custom renderers stay native. */
public final class RenderTypePolicy {
    private RenderTypePolicy() {}
    public static boolean standard(Class<?> type) {
        if(type.getName().startsWith("net.minecraft.block."))return true;
        for(String name:new String[]{"getRenderType","func_149645_b"}) {
            try {
                Method method=type.getMethod(name);
                return method.getReturnType()==int.class&&method.getDeclaringClass().getName().startsWith("net.minecraft.block.");
            }catch(NoSuchMethodException ignored){}
        }
        return false;
    }
}
