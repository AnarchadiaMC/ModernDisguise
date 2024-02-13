package dev.iiahmed.disguise;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

@SuppressWarnings("unused")
public final class Disguise {

    private final String name;
    private final Skin skin;
    private final EntityType entityType;

    private Disguise(final String name, final Skin skin, final EntityType entityType) {
        this.name = name;
        this.skin = skin;
        this.entityType = entityType;
    }

    /**
     * Returns a new instance of the Disguise.Builder class
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a {@link Boolean} that indicates whether the disguise is empty or not
     */
    public boolean isEmpty() {
        return !hasName() && !hasSkin() && !hasEntity();
    }

    /**
     * @return a {@link Boolean} that indicates whether the disguise will change the player's entity
     */
    public boolean hasEntity() {
        return entityType != null && entityType != EntityType.PLAYER;
    }

    /**
     * @return a {@link Boolean} that indicates whether the disguise will change the player's name
     */
    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    /**
     * @return a {@link Boolean} that indicates whether the disguise will change the player's skin
     */
    public boolean hasSkin() {
        return skin != null && skin.isValid();
    }

    /**
     * @return the name that the disguised player's name going to be changed for
     */
    public String getName() {
        return name;
    }

    /**
     * @return the textures that the disguised player's skin going to be changed for
     */
    public String getTextures() {
        if (skin == null) {
            return null;
        }
        return skin.getTextures();
    }

    /**
     * @return the signature that the disguised player's skin going to be changed for
     */
    public String getSignature() {
        if (skin == null) {
            return null;
        }
        return skin.getSignature();
    }

    /**
     * @return the entitytype that the disguised player's entity going to be changed for
     */
    public EntityType getEntityType() {
        return entityType == null ? EntityType.PLAYER : entityType;
    }

    /**
     * The builder class for the disguise system
     */
    public static class Builder {

        private String name;
        private Skin skin;
        private EntityType entityType;

        /* we don't allow constructors from outside */
        private Builder() {
        }

        /**
         * This method sets the new name of the nicked player
         *
         * @param name the replacement of the actual player name
         * @return the disguise builder
         */
        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @param name this is the Name of the needed player's skin
         * @return the disguise builder
         */
        @SuppressWarnings("deprecation")
        public Builder setSkin(final String name) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (player == null || player.getUniqueId() == null) {
                return this;
            }

            return setSkin(player.getUniqueId(), SkinAPI.MOJANG);
        }

        /**
         * @param uuid this is the UUID of the needed player's skin
         * @return the disguise builder
         */
        public Builder setSkin(final UUID uuid) {
            return setSkin(uuid, SkinAPI.MOJANG);
        }

        /**
         * @param name    this is the Name of the needed player's skin
         * @param skinAPI determines the SkinAPI type
         * @return the disguise builder
         */
        @SuppressWarnings("deprecation")
        public Builder setSkin(final String name, final SkinAPI skinAPI) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (player == null || player.getUniqueId() == null) {
                return this;
            }

            return setSkin(player.getUniqueId(), skinAPI);
        }

        /**
         * @param uuid    this is the UUID of the needed player's skin
         * @param skinAPI determines the SkinAPI type
         * @return the disguise builder
         */
        private Builder setSkin(final UUID uuid, final SkinAPI skinAPI) {
            final String urlString = skinAPI.format(uuid);
            final JSONObject object = DisguiseUtil.getJSONObject(urlString);

            if (object == null || object.isEmpty()) {
                return this;
            }

            String texture = null, signature = null;
            switch (skinAPI) {
                case MOJANG:
                case MINETOOLS:
                    final JSONArray array;
                    if (skinAPI.name().equals("MOJANG")) {
                        array = (JSONArray) object.get("properties");
                    } else {
                        array = (JSONArray) ((JSONObject) object.get("raw")).get("properties");
                    }
                    for (Object o : array) {
                        final JSONObject jsonObject = (JSONObject) o;
                        if (jsonObject == null) continue;

                        if (jsonObject.get("value") != null) {
                            texture = (String) jsonObject.get("value");
                        }

                        if (jsonObject.get("signature") != null) {
                            signature = (String) jsonObject.get("signature");
                        }
                    }
                    break;
                case MINESKIN:
                    final JSONObject dataObject = (JSONObject) object.get("data");
                    final JSONObject texturesObject = (JSONObject) dataObject.get("texture");
                    texture = (String) texturesObject.get("value");
                    signature = (String) texturesObject.get("signature");
                    break;
            }
            return setSkin(texture, signature);
        }

        /**
         * Sets the skin based on a texture and a signature
         *
         * @return the disguise builder
         */
        public Builder setSkin(final String textures, final String signature) {
            return setSkin(new Skin(textures, signature));
        }

        /**
         * Sets the skin based on a {@link Skin}
         *
         * @return the disguise builder
         */
        public Builder setSkin(final Skin skin) {
            this.skin = skin;
            return this;
        }

        /**
         * @param entityType the entity type the player should look like
         * @return the disguise builder
         */
        public Builder setEntityType(final EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        /**
         * @return a new instance of {@link Disguise} with the collected info
         */
        public Disguise build() {
            return new Disguise(name, skin, entityType);
        }

    }


}
