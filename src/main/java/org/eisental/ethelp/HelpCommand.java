package org.eisental.ethelp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class HelpCommand implements CommandExecutor {
    private HelpPlugin plg;
    
    public HelpCommand(HelpPlugin plg) {
        this.plg = plg;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length==0) plg.listArticles(sender, null, false);        
        else if (args[0].equalsIgnoreCase("reload")) {
            plg.updateList();
            sender.sendMessage(HelpPlugin.infoColor + "Reloaded help article list.");
        } else {
            String articleId = args[0];
            boolean onepage = args.length>1 && args[1].equalsIgnoreCase("all");
            plg.readArticle(articleId, sender, onepage);
        } 
        
        return true;
    }
    
}
