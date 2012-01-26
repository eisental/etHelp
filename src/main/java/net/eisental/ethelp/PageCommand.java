/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eisental.ethelp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
class PageCommand implements CommandExecutor {
    HelpPlugin plg;
    
    public PageCommand(HelpPlugin plg) {
        this.plg = plg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
