/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundList;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

@SideOnly(value=Side.CLIENT)
public class SoundListSerializer
implements JsonDeserializer {
    private static final String __OBFID = "CL_00001124";

    public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) {
        JsonObject jsonobject = JsonUtils.getJsonElementAsJsonObject(p_deserialize_1_, "entry");
        SoundList soundlist = new SoundList();
        soundlist.setReplaceExisting(JsonUtils.getJsonObjectBooleanFieldValueOrDefault(jsonobject, "replace", false));
        SoundCategory soundcategory = SoundCategory.func_147154_a(JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonobject, "category", SoundCategory.MASTER.getCategoryName()));
        soundlist.setSoundCategory(soundcategory);
        Validate.notNull((Object)((Object)soundcategory), (String)"Invalid category", (Object[])new Object[0]);
        if (jsonobject.has("sounds")) {
            JsonArray jsonarray = JsonUtils.getJsonObjectJsonArrayField(jsonobject, "sounds");
            for (int i = 0; i < jsonarray.size(); ++i) {
                JsonElement jsonelement1 = jsonarray.get(i);
                SoundList.SoundEntry soundentry = new SoundList.SoundEntry();
                if (JsonUtils.jsonElementTypeIsString(jsonelement1)) {
                    soundentry.setSoundEntryName(JsonUtils.getJsonElementStringValue(jsonelement1, "sound"));
                } else {
                    JsonObject jsonobject1 = JsonUtils.getJsonElementAsJsonObject(jsonelement1, "sound");
                    soundentry.setSoundEntryName(JsonUtils.getJsonObjectStringFieldValue(jsonobject1, "name"));
                    if (jsonobject1.has("type")) {
                        SoundList.SoundEntry.Type type1 = SoundList.SoundEntry.Type.getType(JsonUtils.getJsonObjectStringFieldValue(jsonobject1, "type"));
                        Validate.notNull((Object)((Object)type1), (String)"Invalid type", (Object[])new Object[0]);
                        soundentry.setSoundEntryType(type1);
                    }
                    if (jsonobject1.has("volume")) {
                        float f = JsonUtils.getJsonObjectFloatFieldValue(jsonobject1, "volume");
                        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid volume", (Object[])new Object[0]);
                        soundentry.setSoundEntryVolume(f);
                    }
                    if (jsonobject1.has("pitch")) {
                        float f = JsonUtils.getJsonObjectFloatFieldValue(jsonobject1, "pitch");
                        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid pitch", (Object[])new Object[0]);
                        soundentry.setSoundEntryPitch(f);
                    }
                    if (jsonobject1.has("weight")) {
                        int j = JsonUtils.getJsonObjectIntegerFieldValue(jsonobject1, "weight");
                        Validate.isTrue((j > 0 ? 1 : 0) != 0, (String)"Invalid weight", (Object[])new Object[0]);
                        soundentry.setSoundEntryWeight(j);
                    }
                    if (jsonobject1.has("stream")) {
                        soundentry.setStreaming(JsonUtils.getJsonObjectBooleanFieldValue(jsonobject1, "stream"));
                    }
                }
                soundlist.getSoundList().add(soundentry);
            }
        }
        return soundlist;
    }
}

