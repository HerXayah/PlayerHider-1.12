package meow.emily.patootie;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import meow.emily.patootie.events.PlayerEventHandler;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.*;
import net.labymod.user.User;
import net.labymod.user.util.UserActionEntry;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.logging.Logger;

public class Emily extends LabyModAddon {

    private static final Logger LOGGER = Logger.getLogger("PlayerHider");
    private static final String PREFIX = "[PH] ";

    private static Emily instance;
    private VoiceChat voiceChat;


    private boolean renderPlayers = true;
    // UUID VoiceCHat 1.12
    private final UUID vcUuid12 = UUID.fromString("24c0644d-ad56-4609-876d-6e9da3cc9794");
    private boolean muted = false;

    private boolean configMessage = true;
    // UUID VoiceChat 1.8
    private final UUID vcUuid8 = UUID.fromString("43152d5b-ca80-4b29-8f48-39fd63e48dee");
    public Map<UUID, Integer> playersToRender = new HashMap<>();

    private List<String> playersToRenderString = new ArrayList<>();
    private boolean voiceexist;

    private int key;

    public static Emily getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        api.registerForgeListener(new PlayerEventHandler());
        api.getEventManager().register(
                (user, entityPlayer, networkPlayerInfo, list) ->
                        list.add(createBlacklistEntry()));
        api.getEventManager().register(
                (user, entityPlayer, networkPlayerInfo, list) ->
                        list.add(createBlacklistRemoval())
        );
        System.out.println(PREFIX + "Enabled!");
        System.out.println(PREFIX + "Starting...");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!voiceexist) {
            for (LabyModAddon addon : AddonLoader.getAddons()) {
                if (addon == null || addon.about == null || addon.about.name == null) {
                    continue;
                }
                LabyModAddon voicechat = AddonLoader.getAddonByUUID(UUID.fromString(String.valueOf(vcUuid12)));
                if (voicechat instanceof VoiceChat && addon.about.name.equals("VoiceChat")) {
                    voiceChat = (VoiceChat) addon;
                    //LabyMod.getInstance().displayMessageInChat("VoiceChat addon found!");
                    voiceexist = true;
                } else {
                    //LabyMod.getInstance().displayMessageInChat("VoiceChat addon not found!");
                    voiceexist = false;
                }
            }
        }
    }

    private UserActionEntry createBlacklistEntry() {
        return new UserActionEntry(
                "Blacklist Player",
                UserActionEntry.EnumActionType.NONE,
                null,
                new UserActionEntry.ActionExecutor() {
                    @Override
                    public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                        // getConfig().addProperty("playersToRenderString", networkPlayerInfo.getGameProfile().getName());
                        //labyMod().displayMessageInChat("Name: " + getConfig().get("playersToRenderString"));
                        try {
                            playersToRender.put(networkPlayerInfo.getGameProfile().getId(), 0);
                            savePlayersToRender();
                            playersToRenderString.add(networkPlayerInfo.getGameProfile().getName());
                            savePlayersToRenderString();
                            saveConfig();
                        } catch (Exception e) {
                            e.printStackTrace();
                            labyMod().displayMessageInChat("Error: " + e.getMessage());
                        }
                    }

                    @Override
                    public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                        return true;
                    }
                }
        );
    }

    private UserActionEntry createBlacklistRemoval() {
        return new UserActionEntry(
                "Remove from Blacklist",
                UserActionEntry.EnumActionType.NONE,
                null,
                new UserActionEntry.ActionExecutor() {
                    @Override
                    public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                        //  getConfig().addProperty("playersToRenderString", networkPlayerInfo.getGameProfile().getName());
                        //labyMod().displayMessageInChat("Name: " + getConfig().get("playersToRenderString"));
                        try {
                            RemovePlayer(networkPlayerInfo.getGameProfile().getName());
                            UUID uuid = networkPlayerInfo.getGameProfile().getId();
                            if (isVoiceexist()) {
                                VoiceChat voiceChat = (VoiceChat) AddonLoader.getAddonByUUID(UUID.fromString(String.valueOf(vcUuid12)));
                                Map<UUID, Integer> volume = voiceChat.getPlayerVolumes();
                                volume.put(uuid, 100);
                                if (volume.containsKey(uuid)) {
                                    volume.put(uuid, voiceChat.getVolume(uuid));
                                } else {
                                    volume.put(uuid, 100);
                                }
                                voiceChat.savePlayersVolumes();
                            }
                            savePlayersToRender();
                            saveConfig();
                        } catch (Exception e) {
                            e.printStackTrace();
                            labyMod().displayMessageInChat("Error: " + e.getMessage());
                        }
                    }

                    @Override
                    public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                        return true;
                    }
                }
        );
    }

    @Override
    public void loadConfig() {
        JsonObject config = getConfig();
        this.renderPlayers = config.has("renderPlayers") && config.get("renderPlayers").getAsBoolean();
        this.key = config.has("key") ? config.get("key").getAsInt() : -1;
        this.configMessage = config.has("configMessage") && config.get("configMessage").getAsBoolean();
        if (config.has("playersToRenderString")) {
            JsonElement playerArray = config.get("playersToRenderString");
            if (playerArray.isJsonArray()) {
                JsonArray jsonArray = playerArray.getAsJsonArray();
                for (JsonElement jsonElement : jsonArray) {
                    String name = jsonElement.getAsString();
                    playersToRenderString.add(name);
                }
            } else {
                playersToRenderString.add(" ");
            }
        }
        if (config.has("playersToRender")) {
            JsonObject object = config.get("playersToRender").getAsJsonObject();
            Map<UUID, Integer> playersToRender = new HashMap<>();
            for (Map.Entry<String, JsonElement> playerVolumeEntry : object.entrySet()) {
                String id = playerVolumeEntry.getKey();
                int volume = playerVolumeEntry.getValue().getAsInt();
                playersToRender.put(UUID.fromString(id), volume);
            }
            this.playersToRender = playersToRender;
        } else {
            this.playersToRender = new HashMap<>();
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new HeaderElement(ModColor.cl('a') + "PlayerHider Settings"));
        subSettings.add(new BooleanElement(
                "Enable PlayerHider",
                this, new ControlElement.IconData(Material.REDSTONE),
                "renderPlayers", renderPlayers)
        );
        subSettings.add(new BooleanElement(
                "Enable Messages",
                this, new ControlElement.IconData(Material.WOOL),
                "configMessage", configMessage)
        );
        KeyElement keyElement = new KeyElement(
                "Key",
                new ControlElement.IconData(Material.REDSTONE_TORCH_ON), this.key, integer -> {
            this.key = integer;
            getConfig().addProperty("key", integer);
            saveConfig();
        });

        // If you know how to make both, THIS and the STRING Value to update simultaniously
        // feel free to edit this in, so we can use This
        /*
        StringElement Blacklistbutton = new StringElement(
                "Blacklist", new ControlElement.IconData(Material.COAL_BLOCK),
                String.join(",", playersToRenderString), this::AddPlayer);
        subSettings.add(new HeaderElement(ModColor.cl('a') + "Seperate them by Comma"));

         */
        subSettings.add(keyElement);
    }

    public void RemovePlayer(String s) {
        // remove from the list

        playersToRenderString.remove(s);
        savePlayersToRenderString();
        //  playersToRenderString.removeIf(player -> player.equals(s));
        saveConfig();
    }

    public void savePlayersToRender() {
        JsonObject object = new JsonObject();
        for (Map.Entry<UUID, Integer> uuidIntegerEntry : playersToRender.entrySet()) {
            String uuid = uuidIntegerEntry.getKey().toString();
            Integer volume = uuidIntegerEntry.getValue();
            object.addProperty(uuid, volume);
        }
        //labyMod().displayMessageInChat(playersToRender.toString());
        getConfig().add("playersToRender", object);
        saveConfig();
    }

    private LabyMod labyMod() {
        return LabyMod.getInstance();
    }

    public void savePlayersToRenderString() {
        JsonArray jsonArray = new JsonArray();
        for (String s : playersToRenderString) {
            // check if already in array
            if (!jsonArray.toString().contains(s)) {
                JsonElement jsonElement = new JsonPrimitive(s);
                jsonArray.add(jsonElement);
            }
        }
        getConfig().add("playersToRenderString", jsonArray);
        saveConfig();
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Map<UUID, Integer> getPlayersToRender() {
        return this.playersToRender;
    }

    public void setPlayersToRender(Map<UUID, Integer> playersToRender) {
        this.playersToRender = playersToRender;
    }

    public boolean isRenderPlayers() {
        return this.renderPlayers;
    }

    public void setRenderPlayers(boolean renderPlayers) {
        this.renderPlayers = renderPlayers;
    }

    public List<String> getPlayersToRenderString() {
        return this.playersToRenderString;
    }

    public List<String> setPlayersToRenderString(List<String> playersToRenderString) {
        return this.playersToRenderString = playersToRenderString;
    }

    public boolean isConfigMessage() {
        return this.configMessage;
    }

    public void setConfigMessage(boolean ConfigMessage) {
        this.configMessage = ConfigMessage;
    }

    public VoiceChat getVoiceChat() {
        return this.voiceChat;
    }

    public void setVoiceChat(VoiceChat voiceChat) {
        this.voiceChat = voiceChat;
    }


    public boolean isVoiceexist() {
        return voiceexist;
    }

    public void setVoiceexist(boolean voiceexist) {
        this.voiceexist = voiceexist;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }
}