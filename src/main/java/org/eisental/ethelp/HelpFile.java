/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eisental.ethelp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.tal.redstonechips.page.LineSource;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Tal Eisenberg
 */
public class HelpFile implements LineSource {

    String[] lines;
    String id;
    File file; 
    Map headerMap = null;
    
    private HelpFile(File f, String header, String[] lines) {
        this.lines = lines;
        String fname = f.getName();
        this.id = fname.substring(0, fname.length()-".help".length());
        file = f;

        Yaml yaml = new Yaml();
        Object y = yaml.load(header);
        
        if (y instanceof Map) {
            headerMap = (Map)y;
            headerMap.put("id", id);
            
            if (!headerMap.containsKey("title"))
                headerMap.put("title", id);
        }
        
    }
    
    @Override
    public String getLine(int idx) {
        return lines[idx];
    }

    public String getContent() { 
        StringBuilder b = new StringBuilder();
        for (String l : lines) {
            b.append(l);
            b.append("\n");
        }
        
        return b.toString();
    }
    
    @Override
    public int getLineCount() {
        return lines.length;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return headerMap.get("title").toString();
    }
    public Map getHeaderMap() {
        return headerMap;
    }
    

    public static HelpFile fromFile(File f) throws FileNotFoundException {
        if (!f.exists()) throw new FileNotFoundException();

        StringBuilder yamlBuilder = new StringBuilder();
        List<String> lines = new ArrayList<String>();
        
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(f));
        try {
            boolean inYamlBlock = false;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                if (line.trim().equals("---"))
                    inYamlBlock = !inYamlBlock;
                else if (inYamlBlock) {
                    yamlBuilder.append(line);
                    yamlBuilder.append(NL);
                } else lines.add(line);
            }
        } finally {
            scanner.close();
        }  

        return new HelpFile(f, yamlBuilder.toString(), lines.toArray(new String[0]));
    }
    
    static HelpFile byArticleId(String articleId) throws FileNotFoundException {
        File f = new File(HelpPlugin.DataFolder, articleId + ".help");
        return HelpFile.fromFile(f);
    }    
}
