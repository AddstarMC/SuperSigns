package au.com.addstar.signmaker;

import au.com.addstar.signmaker.commands.SignMakerCommandRoot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

public class SignMakerPlugin extends JavaPlugin {
  private HashMap<String, TextSign> mSigns = new HashMap<>();
  private File mSignFolder;

  public static WeakHashMap<Player, TextSign> lastSign = new WeakHashMap<>();
  public static WeakHashMap<Player, String> lastSignName = new WeakHashMap<>();

  public static SignMakerPlugin instance;

  @Override
  public void onEnable() {
    instance = this;

    mFontFolder = new File(getDataFolder(), "fonts");
    mFontFolder.mkdirs();

    mSignFolder = new File(getDataFolder(), "signs");
    mSignFolder.mkdirs();


    mScriptsFolder = new File(getDataFolder(), "scripts");
    mScriptsFolder.mkdirs();
    TextWriter.reloadFonts();
    reloadSigns();

    SignMakerCommandRoot cmd = new SignMakerCommandRoot();
    cmd.registerAs(getCommand("signmaker"));
  }

  public void reloadSigns() {
    mSigns.clear();
    File[] files = mSignFolder.listFiles();
    if (files == null) {
      throw new IllegalPluginAccessException("No Sign Folder could be created or found");
    }
    for (File file : files) {
      try {
        mSigns.put(file.getName().toLowerCase(), TextSign.load(file));
      } catch (IOException e) {
        getLogger().warning("Could not load sign " + file.getName() + " (" + file.getPath() + ")");
        e.printStackTrace();
      } catch (InvalidConfigurationException e) {
        getLogger().warning("Could not load sign " + file.getName() + " (" + file.getPath() + ")");
        getLogger().warning(e.getMessage());
      }
    }
  }

  public void addSign(String name, TextSign sign) {
    mSigns.put(name.toLowerCase(), sign);
  }

  public void removeSign(String name) {
    mSigns.remove(name.toLowerCase());
  }

  public TextSign getSign(String name) {
    return mSigns.get(name.toLowerCase());
  }

  public void saveSign(String name) {
    TextSign sign = getSign(name);
    File file = new File(mSignFolder, name);
    if (sign == null) {
      if (!file.delete()) {
        getLogger().warning("Error removing file:" + file.getAbsolutePath());
      }
    } else {
      try {
        sign.save(file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public Collection<String> getSignNames() {
    return mSigns.keySet();
  }

  private static File mFontFolder;

  public static File getFontFolder() {
    return mFontFolder;
  }

  private static File mScriptsFolder;

  public static File getScriptsFolder() {
    return mScriptsFolder;
  }

  public static List<String> matchString(String string, Collection<String> strings) {
    ArrayList<String> matches = new ArrayList<>();
    string = string.toLowerCase();

    for (String s : strings) {
      if (s.toLowerCase().startsWith(string)) {
        matches.add(s);
      }
    }

    return matches;
  }
}
