
package net.eisental.ethelp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.eisental.common.page.Pager;
import net.eisental.common.parsing.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Tal Eisenberg
 */
public class HelpPlugin extends JavaPlugin {
    Yaml yaml;
    public static final ChatColor infoColor = ChatColor.AQUA;
    public static final ChatColor errorColor = ChatColor.RED;
    
    private Map[] helpList;
    static File DataFolder;
    
    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        DataFolder = this.getDataFolder();
        
        yaml = new Yaml();
        
        getCommand("help").setExecutor(new HelpCommand(this));        
        
        //getCommand("page").setExecutor(new PageCommand(this));
        
        updateList();
    }

    public void listArticles(CommandSender sender, Pattern p, boolean onepage) {
        if (helpList==null || helpList.length==0) {
            sender.sendMessage("There are no help topics.");
        } else {
            List<Map> filter = new ArrayList<Map>();
            
            for (Map h : helpList) {
                String title = h.get("title").toString();
                String id = h.get("id").toString();
                if (p==null) filter.add(h);
                else if (title!=null && id!=null && (p.matcher(title).find() || p.matcher(id).find()))
                    filter.add(h);
            }
            
            if (filter.isEmpty()) {
                sender.sendMessage("Can't find matching help topics.");
            } else if (filter.size()==1) {
                readArticle(filter.get(0).get("id").toString(), sender, onepage);
            } else {

                List<String> help = new ArrayList<String>();

                for (Map h : filter) {
                    help.add(ChatColor.YELLOW + h.get("id").toString() + ChatColor.WHITE + " - " + h.get("title").toString());
                }
                Collections.sort(help);
                Pager.beginPaging(sender, "Help topics", help.toArray(new String[0]), infoColor, errorColor, Pager.MaxLines-2);
                sender.sendMessage("Use /help <article> to show a topic.");
            }
        }
    }

    public void readArticle(String articleId, CommandSender sender, boolean onepage) {
        try {
            HelpFile h = HelpFile.byArticleId(articleId);
            int linesPerPage = (onepage?h.getLineCount():Pager.MaxLines);
            Pager.beginPaging(sender, h.getTitle(), h.getContent(), infoColor, errorColor, linesPerPage);
        } catch (FileNotFoundException ie) {
            try {
                String pattern = ParsingUtils.convertGlobToRegex(articleId);
                listArticles(sender, Pattern.compile(pattern), onepage);
            } catch (PatternSyntaxException e) { 
                sender.sendMessage(errorColor + "Bad regex: " + e.getMessage());
            }
        }
    }

    public void updateList() {
        File folder = getDataFolder();
        File[] list = folder.listFiles();
        
        if (list!=null && list.length!=0) {
            List<Map> l = new ArrayList<Map>();
                        
            for (File f : list) {
                if (f.getName().endsWith(".help")) {
                    try {
                        HelpFile h = HelpFile.fromFile(f);
                        l.add(h.getHeaderMap());
                    } catch (FileNotFoundException ex) { }
                }
            }
            
            helpList = l.toArray(new Map[0]);
        }
        
    }
}
