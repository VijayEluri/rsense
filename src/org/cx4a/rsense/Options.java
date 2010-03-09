package org.cx4a.rsense;

import java.io.File;
import java.io.Reader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;

import org.cx4a.rsense.util.HereDocReader;

public class Options extends HashMap<String, List<String>> {
    private static final long serialVersionUID = 0L;

    private List<String> rest = new ArrayList<String>();

    public Options() {}

    public void addOption(String name) {
        addOption(name, null);
    }
    
    public void addOption(String name, String value) {
        List<String> list = get(name);
        if (list == null) {
            list = new ArrayList<String>();
            put(name, list);
        }
        if (value != null) {
            list.add(value);
        }
    }

    public void addOptions(String name, List<String> value) {
        List<String> list = get(name);
        if (list == null) {
            list = new ArrayList<String>();
            put(name, list);
        }
        if (value != null) {
            list.addAll(value);
        }
    }
        
    public boolean hasOption(String name) {
        return containsKey(name);
    }

    public String getOption(String name) {
        List<String> list = get(name);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public List<String> getOptions(String name) {
        return get(name);
    }

    public void addRestArg(String arg) {
        rest.add(arg);
    }

    public List<String> getRestArgs() {
        return rest;
    }

    public boolean isFormatGiven() {
        return hasOption("format");
    }

    public boolean isEncodingGiven() {
        return hasOption("encoding");
    }

    public String getFormat() {
        String format = getOption("format");
        return format != null ? format : defaultFormat();
    }

    public boolean isPlainFormat() {
        return "plain".equals(getFormat());
    }

    public boolean isEmacsFormat() {
        return "emacs".equals(getFormat());
    }

    public String getEncoding() {
        String encoding = getOption("encoding");
        return encoding != null ? encoding : defaultEncoding();
    }

    public String getPrompt() {
        return hasOption("no-prompt") ? "" : getOption("prompt");
    }

    public File getFile() {
        String file = getOption("file");
        return file == null ? null : new File(file);
    }

    public boolean isFileStdin() {
        return !hasOption("file") || "-".equals(getOption("file"));
    }

    public HereDocReader getHereDocReader(Reader reader) {
        return new HereDocReader(reader, "EOF");
    }

    public CodeAssist.Location getLocation() {
        String location = getOption("location");
        if (location == null) {
            return CodeAssist.Location.markLocation("_|_");
        }
        String[] lr = location.split(":");
        if (lr.length == 2) {
            return CodeAssist.Location.logicalLocation(Integer.parseInt(lr[0]), Integer.parseInt(lr[1]));
        } else {
            try {
                return CodeAssist.Location.offsetLocation(Integer.parseInt(lr[0]));
            } catch (NumberFormatException e) {
                return CodeAssist.Location.markLocation(lr[0]);
            }
        }
    }

    public String getEndMark() {
        return getOption("end-mark");
    }

    public boolean isDebug() {
        return hasOption("debug");
    }

    public String getLog() {
        return getOption("log");
    }

    public String getRsenseHome() {
        String rsenseHome = getOption("home");
        return rsenseHome != null ? rsenseHome : ".";
    }

    public List<String> getLoadPath() {
        List<String> loadPath = getPathList("load-path");
        String sep = File.separator;
        String psep = File.pathSeparator;

        // add stub path
        loadPath.add(getRsenseHome() + sep + "stubs" + sep + "1.8");
        
        return loadPath;
    }

    public List<String> getGemPath() {
        return getPathList("gem-path");
    }

    public String getProject() {
        return getOption("project");
    }

    public boolean isDetectProject() {
        return hasOption("detect-project");
    }

    public boolean isKeepEnv() {
        return hasOption("keep-env");
    }

    public boolean isTest() {
        return hasOption("test");
    }

    public boolean isTestColor() {
        return hasOption("test-color");
    }

    public String getTest() {
        return getOption("test");
    }

    public Set<String> getShouldContain() {
        return getStringSet("should-contain");
    }

    public Set<String> getShouldNotContain() {
        return getStringSet("should-not-contain");
    }

    public Set<String> getShouldBe() {
        if (hasOption("should-be-empty")) {
            return Collections.<String>emptySet();
        } else {
            return getStringSet("should-be");
        }
    }

    public boolean isShouldBeGiven() {
        return hasOption("should-be") || hasOption("should-be-empty");
    }

    public boolean isPrintAST() {
        return hasOption("print-ast");
    }

    public void inherit(Options parent) {
        addOption("home", parent.getRsenseHome());
        if (parent.isDebug()) {
            addOption("debug");
        }
        addOption("log", parent.getLog());
        addOptions("load-path", parent.getOptions("load-path"));
        addOptions("gem-path", parent.getOptions("gem-path"));
        addOption("format", parent.getFormat());
        addOption("encoding", parent.getEncoding());
        if (parent.isTestColor()) {
            addOption("test-color");
        }
    }

    public String getConfig() {
        return getOption("config");
    }
    
    private Set<String> getStringSet(String name) {
        Set<String> result;
        String str = getOption(name);
        if (str == null) {
            result = Collections.<String>emptySet();
        } else {
            result = new HashSet<String>(Arrays.asList(str.split(",")));
        }
        return result;
    }

    private List<String> getPathList(String name) {
        List<String> list = getOptions(name);
        List<String> result = new ArrayList<String>();
        if (list != null) {
            for (String paths : list) {
                for (String path : paths.split(File.pathSeparator)) {
                    result.add(path);
                }
            }
        }
        return result;
    }
    
    public static String defaultFormat() {
        return "plain";
    }

    public static String defaultEncoding() {
        return "UTF-8";
    }

    public static String defaultEndMark() {
        return "END";
    }

    public static String defaultPrompt() {
        return "> ";
    }
}
