package org.acornmc.drchat;

import github.scarsz.discordsrv.DiscordSRV;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class ChatManager {
    ConfigManager configManager;
    public static HashMap<OfflinePlayer, Integer> spamCheck = new HashMap<>();
    public static Set<OfflinePlayer> ecoCheck = new HashSet<>();
    public static boolean chatIsFrozen = false;

    public ChatManager (ConfigManager configManager) {
        this.configManager = configManager;
    }

    public String fixSpacing(String message) {
        String[] words = message.split(" ");
        StringBuilder newMessage = new StringBuilder();
        int i = 0;
        while (i < words.length) {
            if (words.length >= i+3 && words[i].length() == 1 && words[i+1].length() == 1 && words[i+2].length() == 1) {
                while (words.length > i && words[i].length() == 1) {
                    newMessage.append(words[i]);
                    i++;
                }
            } else {
                newMessage.append(words[i]);
                i++;
            }
            newMessage.append(" ");
        }
        return newMessage.substring(0, newMessage.length() - 1);
    }

    public String fixCharacter(String message) {
        StringBuilder newMessage = new StringBuilder();
        int limit = configManager.get().getInt("checks.character.limit");
        boolean doingLink = false;
        for (int i = 0; i < limit && i < message.length(); i++) {
            newMessage.append(message.charAt(i));
        }
        for (int i = 0; i < message.length(); i++) {
            boolean allTheSame = true;
            for (int j = i+1; j < i + limit; j++) {
                if (j >= message.length() || message.charAt(j) != message.charAt(i)) {
                    allTheSame = false;
                    break;
                }
            }
            String word = message.substring(i);
            if (word.toLowerCase().startsWith("http://") || word.toLowerCase().startsWith("https://")) {
                boolean ignoreCharacterLinks = configManager.get().getBoolean("checks.character.ignore-links");
                if (ignoreCharacterLinks) {
                    doingLink = true;
                }
            }
            if (!allTheSame || doingLink) {
                newMessage.append(message.charAt(i));
            } else {
                List<Character> ignoredCharacters = configManager.get().getCharacterList("checks.character.exceptions");
                if (ignoredCharacters.contains(message.charAt(i))) {
                    newMessage.append(message.charAt(i));
                }
            }
            if (message.charAt(i) == ' ') {
                doingLink = false;
            }
        }
        return newMessage.toString();
    }

    public boolean hasPhrase(String message) {
        List<String> phrases = configManager.get().getStringList("checks.phrase");
        for (String phrase : phrases) {
            if (message.equals(phrase)) {
                return true;
            }
        }
        return false;
    }

    public String fixCapital(String message) {
        //Count number of capital letters
        int uppercaseCount = 0;
        String colorlessMessage = ChatColor.stripColor(message);
        for (int i = 0; i < colorlessMessage.length(); i++) {
            if (Character.isUpperCase(colorlessMessage.charAt(i))) {
                uppercaseCount++;
            }
        }

        if (uppercaseCount > configManager.get().getInt("checks.capital.limit")) {
            boolean ignoreCapitalLinks = configManager.get().getBoolean("checks.capital.ignore-links");
            if (ignoreCapitalLinks) {
                String[] words = message.split(" ");
                StringBuilder newMessage = new StringBuilder();
                for (String word : words) {
                    if (word.toLowerCase().startsWith("http://") || word.toLowerCase().startsWith("https://")) {
                        newMessage.append(word);
                    } else {
                        newMessage.append(word.toLowerCase());
                    }
                    newMessage.append(" ");
                }
                String str =  newMessage.toString();
                if (str.charAt(str.length() - 1) == ' ') {
                    str = str.substring(0, str.length() - 1);
                }
                return str;
            } else {
                return message.toLowerCase();
            }
        }
        return message;
    }

    public String fixFont(String message) {
        return message
                .replace("ａ", "a")
                .replace("ｂ", "b")
                .replace("ｃ", "c")
                .replace("ｄ", "d")
                .replace("ｅ", "e")
                .replace("ｆ", "f")
                .replace("ｇ", "g")
                .replace("ｈ", "h")
                .replace("ｉ", "i")
                .replace("ｊ", "j")
                .replace("ｋ", "k")
                .replace("ｌ", "l")
                .replace("ｍ", "m")
                .replace("ｎ", "n")
                .replace("ｏ", "o")
                .replace("ｐ", "p")
                .replace("ｑ", "q")
                .replace("ｒ", "r")
                .replace("ｓ", "s")
                .replace("ｔ", "t")
                .replace("ｕ", "u")
                .replace("ｖ", "v")
                .replace("ｗ", "w")
                .replace("ｘ", "x")
                .replace("ｙ", "y")
                .replace("ｚ", "z")
                .replace("Ａ", "a")
                .replace("Ｂ", "b")
                .replace("Ｃ", "c")
                .replace("Ｄ", "d")
                .replace("Ｅ", "e")
                .replace("Ｆ", "f")
                .replace("Ｇ", "g")
                .replace("Ｈ", "h")
                .replace("Ｉ", "i")
                .replace("Ｊ", "j")
                .replace("Ｋ", "k")
                .replace("Ｌ", "l")
                .replace("Ｍ", "m")
                .replace("Ｎ", "n")
                .replace("Ｏ", "o")
                .replace("Ｐ", "p")
                .replace("Ｑ", "q")
                .replace("Ｒ", "r")
                .replace("Ｓ", "s")
                .replace("Ｔ", "t")
                .replace("Ｕ", "u")
                .replace("Ｖ", "v")
                .replace("Ｗ", "w")
                .replace("Ｘ", "x")
                .replace("Ｙ", "y")
                .replace("Ｚ", "z")
                .replace("ᴀ", "a")
                .replace("ʙ", "b")
                .replace("ᴄ", "c")
                .replace("ᴅ", "d")
                .replace("ᴇ", "e")
                .replace("ꜰ", "f")
                .replace("ɢ", "g")
                .replace("ʜ", "h")
                .replace("ɪ", "i")
                .replace("ᴊ", "j")
                .replace("ᴋ", "k")
                .replace("ʟ", "l")
                .replace("ᴍ", "m")
                .replace("ɴ", "n")
                .replace("ᴏ", "o")
                .replace("ᴘ", "p")
                .replace("ʀ", "r")
                .replace("ꜱ", "s")
                .replace("ᴛ", "t")
                .replace("ᴜ", "u")
                .replace("ᴠ", "v")
                .replace("ᴡ", "w")
                .replace("ʏ", "y")
                .replace("ᴢ", "z")
                .replace("ⓐ", "a")
                .replace("ⓑ", "b")
                .replace("ⓒ", "c")
                .replace("ⓓ", "d")
                .replace("ⓔ", "e")
                .replace("ⓕ", "f")
                .replace("ⓖ", "g")
                .replace("ⓗ", "h")
                .replace("ⓘ", "i")
                .replace("ⓙ", "j")
                .replace("ⓚ", "k")
                .replace("ⓛ", "l")
                .replace("ⓜ", "m")
                .replace("ⓝ", "n")
                .replace("ⓞ", "o")
                .replace("ⓟ", "p")
                .replace("ⓠ", "q")
                .replace("ⓡ", "r")
                .replace("ⓢ", "s")
                .replace("ⓣ", "t")
                .replace("ⓤ", "u")
                .replace("ⓥ", "v")
                .replace("ⓦ", "w")
                .replace("ⓧ", "x")
                .replace("ⓨ", "y")
                .replace("ⓩ", "z")
                .replace("Ⓐ", "a")
                .replace("Ⓑ", "b")
                .replace("Ⓒ", "c")
                .replace("Ⓓ", "d")
                .replace("Ⓔ", "e")
                .replace("Ⓕ", "f")
                .replace("Ⓖ", "g")
                .replace("Ⓗ", "h")
                .replace("Ⓘ", "i")
                .replace("Ⓙ", "j")
                .replace("Ⓚ", "k")
                .replace("Ⓛ", "l")
                .replace("Ⓜ", "m")
                .replace("Ⓝ", "n")
                .replace("Ⓞ", "o")
                .replace("Ⓟ", "p")
                .replace("Ⓠ", "q")
                .replace("Ⓡ", "r")
                .replace("Ⓢ", "s")
                .replace("Ⓣ", "t")
                .replace("Ⓤ", "u")
                .replace("Ⓥ", "v")
                .replace("Ⓦ", "w")
                .replace("Ⓧ", "x")
                .replace("Ⓨ", "y")
                .replace("？", "?")
                .replace("．", ".")
                .replace("［", "[")
                .replace("］", "]")
                .replace("｛", "{")
                .replace("｝", "}")
                .replace("＋", "+")
                .replace("＝", "=")
                .replace("！", "!")
                .replace("＠", "@")
                .replace("＃", "#")
                .replace("＄", "$")
                .replace("％", "%")
                .replace("＾", "^")
                .replace("＆", "&")
                .replace("＊", "*")
                .replace("－", "-")
                .replace("＿", "_")
                .replace("｜", "|")
                .replace("；", ";")
                .replace("：", ":")
                .replace("｀", "`")
                .replace("＇", "'")
                .replace("１", "1")
                .replace("２", "2")
                .replace("３", "3")
                .replace("４", "4")
                .replace("５", "5")
                .replace("６", "6")
                .replace("７", "7")
                .replace("８", "8")
                .replace("９", "9")
                .replace("０", "0")
                .replace("（", "(")
                .replace("）", ")")
                .replace("，", ",");
    }

    public boolean isTooFrequent(OfflinePlayer player) {
        if (!spamCheck.containsKey(player)) {
            return false;
        }
        int limit = configManager.get().getInt("checks.frequency.limit");
        return spamCheck.get(player) >= limit;
    }

    public void increment(OfflinePlayer player) {
        int newCount = 1;
        if (spamCheck.containsKey(player)) {
            newCount = spamCheck.get(player) + 1;
        }
        spamCheck.put(player, newCount);
        int interval = configManager.get().getInt("checks.frequency.interval");
        new BukkitRunnable() {
            @Override
            public void run() {
                spamCheck.put(player, spamCheck.get(player) - 1);
                if (spamCheck.get(player) == 0) {
                    spamCheck.remove(player);
                }
            }
        }.runTaskLater(configManager.plugin, interval);
    }

    public void notifyModifiedMessage(String playerName, String message) {
        message = ChatColor.stripColor(message);
        List<String> notifyMessages = configManager.get().getStringList("messages.modify-notification");
        for (String notifyMessage : notifyMessages) {
            notifyMessage = ChatColor.translateAlternateColorCodes('&', notifyMessage);
            notifyMessage = notifyMessage.replace("%player%", playerName);
            notifyMessage = notifyMessage.replace("%original-message%", message);
            Bukkit.broadcast(notifyMessage, "drchat.notify.modify");
            Bukkit.getLogger().info(notifyMessage);
        }
    }

    public void notifyCancelledMessage(String playerName, String message) {
        message = ChatColor.stripColor(message);
        List<String> notifyMessages = configManager.get().getStringList("messages.cancel-notification");
        for (String notifyMessage : notifyMessages) {
            notifyMessage = ChatColor.translateAlternateColorCodes('&', notifyMessage);
            notifyMessage = notifyMessage.replace("%player%", playerName);
            notifyMessage = notifyMessage.replace("%original-message%", message);
            Bukkit.broadcast(notifyMessage, "drchat.notify.cancel");
            Bukkit.getLogger().info(notifyMessage);
        }
    }

    public void useTooFrequentCommands(OfflinePlayer player) {
        String playerName = player.getName();
        if (playerName == null) {
            playerName = "?";
        }
        List<String> stringList = configManager.get().getStringList("checks.frequency.commands");
        for (String command : stringList) {
            command = command.replace("%player%", playerName);
            final String finalCommand = command;
            Runnable runnable = () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            Bukkit.getScheduler().runTask(configManager.plugin, runnable);
        }
    }

    public boolean hasSwear(String message) {
        List<String> swears = configManager.get().getStringList("checks.swear.words");
        String lowerCaseMessage = message.toLowerCase();
        for (String swear : swears) {
            if (lowerCaseMessage.contains(swear)) {
                return true;
            }
        }
        return false;
    }

    public void useSwearCommands(OfflinePlayer player) {
        String playerName = player.getName();
        if (playerName == null) {
            playerName = "?";
        }
        List<String> cmds = configManager.get().getStringList("checks.swear.commands");
        for (String command : cmds) {
            command = command.replace("%player%", playerName);
            final String finalCommand = command;
            Runnable runnable = () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            Bukkit.getScheduler().runTask(configManager.plugin, runnable);
        }

    }

    public void reward(OfflinePlayer player) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        double money = configManager.get().getDouble("rewards.money");
        if (money == 0) {
            return;
        }
        if (!ecoCheck.contains(player)) {
            ecoCheck.add(player);
            int interval = configManager.get().getInt("rewards.cooldown");
            new BukkitRunnable() {
                @Override
                public void run() {
                    ecoCheck.remove(player);
                }
            }.runTaskLater(configManager.plugin, interval);
            Economy e = DrChat.getEconomy();
            if (e == null) {
                return;
            }
            e.depositPlayer(player, money);
        }
    }

    public static boolean chatIsFrozen() {
        return chatIsFrozen;
    }

    public static void toggleChatFreeze() {
        chatIsFrozen = !chatIsFrozen;
    }

    public void postSearchResults(String newMessage) {
        String trigger = configManager.get().getString("search.trigger");
        if (trigger != null) {
            if (newMessage.startsWith(trigger)) {
                newMessage = newMessage.substring(trigger.length());
                newMessage = newMessage.replace(" ", "%20");
                if (newMessage.length() >= 1) {
                    String regex = configManager.get().getString("search.regex");
                    if (regex != null && newMessage.matches(regex)) {
                        minecraftSearch(newMessage);
                        discordSearch(newMessage);
                    }
                }
            }
        }
    }

    public void minecraftSearch(String message) {
        String searchFormat = configManager.get().getString("messages.search.minecraft");
        if (searchFormat != null) {
            searchFormat = searchFormat.replace("%search%", message);
            String finalUrl = ChatColor.translateAlternateColorCodes('&', searchFormat);
            BukkitScheduler scheduler = configManager.plugin.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(configManager.plugin, () ->
                    Bukkit.broadcastMessage(finalUrl), 2L);
        }
    }

    public void discordSearch(String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            String searchFormat = configManager.get().getString("messages.search.discord");
            if (searchFormat != null) {
                String finalSearchFormat = searchFormat.replace("%search%", message);
                BukkitScheduler scheduler = configManager.plugin.getServer().getScheduler();
                String gameChannelName = configManager.get().getString("discord.channel-name");
                scheduler.scheduleSyncDelayedTask(configManager.plugin, () ->
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(gameChannelName).sendMessage(finalSearchFormat).queue(),
                        2L);
            }
        }
    }
}
