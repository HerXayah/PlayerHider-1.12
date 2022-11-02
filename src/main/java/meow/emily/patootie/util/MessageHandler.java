package meow.emily.patootie.util;

import net.labymod.main.LabyMod;

public class MessageHandler {

    private static MessageHandler output;
    LabyMod labyMod = LabyMod.getInstance();

    public static MessageHandler getInstance() {
        return output;
    }

    public void sendMessage(String message) {

        labyMod.displayMessageInChat(message);

    }

}
