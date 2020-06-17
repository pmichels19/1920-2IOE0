package Graphics.ColladaParser.Utils;

import java.io.*;

/**
 * Represents a "file" inside a Jar File. Used for accessing resources (models, textures), as they
 * are all inside a jar file when exported.
 *
 *
 */
public class XMLFile {

    private static final String FILE_SEPARATOR = "/";

    private String path;
    private String name;

    public XMLFile(String path) {
        this.path = path;
        String[] dirs = path.split(FILE_SEPARATOR);
        this.name = dirs[dirs.length - 1];
    }

    public XMLFile(String... paths) {
        this.path = "";
        for (String part : paths) {
            this.path += (FILE_SEPARATOR + part);
        }
        String[] dirs = path.split(FILE_SEPARATOR);
        this.name = dirs[dirs.length - 1];
    }

    public XMLFile(XMLFile file, String subFile) {
        this.path = file.path + FILE_SEPARATOR + subFile;
        this.name = subFile;
    }

    public XMLFile(XMLFile file, String... subFiles) {
        this.path = file.path;
        for (String part : subFiles) {
            this.path += (FILE_SEPARATOR + part);
        }
        String[] dirs = path.split(FILE_SEPARATOR);
        this.name = dirs[dirs.length - 1];
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return getPath();
    }

    public InputStream getInputStream() {
        return Class.class.getResourceAsStream(path);
    }

    public BufferedReader getReader() throws Exception {
        try {
            FileReader fr = null;

            try {
                // Try to open the correct obj file
                fr = new FileReader(new File(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fr);
            return reader;
        } catch (Exception e) {
            System.err.println("Couldn't get reader for " + path);
            throw e;
        }
    }

    public String getName() {
        return name;
    }

}
