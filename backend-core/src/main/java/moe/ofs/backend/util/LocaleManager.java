package moe.ofs.backend.util;

import moe.ofs.backend.UTF8Control;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LocaleManager is mainly used to select locale based on language and read localized message from properties files.
 * There are two types of localizations in Lava: gui and server
 *
 * GUI localization refers to the translation of button and label of GUI control panel, while server localization
 * refers to tailored messages/texts sent to a player in the server based on the client language of that player.
 *
 * DCS Lua server only provides a country code for client, such as RU, CN, EN, RU,
 */
public class LocaleManager {

    private static final Map<String, Locale> map = Stream.of(new Object[][] {
        { "en", new Locale("en", "US") },
        { "cn", new Locale("zh", "CN")},
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Locale) data[1]));


    /**
     * Gets the default locale of platform operating system.
     * For windows, it returns the Locale set in the language and international options.
     * @return return a Locale representing the current system default locale.
     */
    private Locale getGuiLocale() {
        return Locale.getDefault();
    }

    /**
     * Search and return a Locale from String-Locale map.
     * If the map does not have a key of this country code from player info, return default locale of en_US.
     * @param playerInfo player information containing id and connection status including DCS client language.
     * @return Locale
     */
    private Locale getPlayerLocale(PlayerInfo playerInfo) {
        return map.containsKey(playerInfo.getLang()) ? map.get(playerInfo.getLang()) : map.get("en");
    }

    /**
     * Get specific resource bundle using the given base name and locale
     * @param baseName The name of the module / package / class that need the resources
     * @param locale the locale to be used.
     * @return ResourceBundle instance of specified base name and locale
     */
    public ResourceBundle getResource(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale, new UTF8Control());
    }
}
