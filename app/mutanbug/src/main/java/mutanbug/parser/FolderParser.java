package mutanbug.parser;

import japa.parser.ParseException;
import japa.parser.ast.expr.FieldAccessExpr;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by sutd on 1/21/15.
 */
public class FolderParser
{
    private File rootFolder;
    private Map<String, ClassDescriptor> classDescriptors;

    public FolderParser(File folder) throws IOException, ParseException
    {
        rootFolder = folder;
        classDescriptors = new TreeMap<String, ClassDescriptor>();

        parseFolder(rootFolder);
    }

    private void parseFolder(File folder)
    {
        File[] files = folder.listFiles();

        if (files != null && files.length > 0)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    parseFolder(file);
                }
                else if (file.getName().endsWith(".java"))
                {
                    System.out.println("Parsing " + file.getName());
                    try
                    {
                        FileParser parser = new FileParser(file);
                        classDescriptors.putAll(parser.getClasses());
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
        }
    }

    public Map<String, ClassDescriptor> getClassDescriptors()
    {
        return classDescriptors;
    }

    public static void main(String[] args)
    {
        FolderParser folderParser = null;
        try
        {
            folderParser = new FolderParser(new File("/Users/sutd/IdeaProjects/MutantBug/src/"));
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }

        System.out.println(folderParser.classDescriptors);
    }
}
