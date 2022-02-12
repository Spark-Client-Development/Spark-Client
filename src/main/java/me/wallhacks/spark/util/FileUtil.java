package me.wallhacks.spark.util;

import com.google.common.io.Files;
import org.jline.utils.InputStreamReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileUtil {
    public static String read(String path){
        // Read the content
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));
            String  str;
            String out = "";
            while ((str = in.readLine()) != null) {
                out = out + (out == "" ? "" : "\n") + str;

            }
            in.close();
            return out;
        } catch (IOException e) {
            return null;
        }

    }
    public static ArrayList<String> listFolderForFolder (String dir) {
        final File folder = new File(dir);
        ArrayList<String> s = new ArrayList<String>();
        if(folder.listFiles() != null)
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory())
                    s.add(fileEntry.getName());

            }
        return s;
    }
    public static boolean exists(String dir) {
        final File folder = new File(dir);

        return folder.exists();
    }
    public static boolean deleteDirectory(String dir) {
        final File folder = new File(dir);

        return deleteDirectoryRecursion(folder);
    }
    public static boolean deleteDirectoryRecursion(File file) {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    if(!deleteDirectoryRecursion(entry))
                        return false;
                }
            }
        }
        return file.delete();
    }

    public static boolean renameDirectory(String dir,String newDir) {
        final File folder = new File(dir);
        if(!folder.exists())
            return false;
        final File newFolder = new File(newDir);
        if(newFolder.exists())
            return false;

        return folder.renameTo(newFolder);
    }
    public static boolean write(String path, String content){
        File targetFile = new File(path);
        try{
            Files.createParentDirs(targetFile);
            Files.touch(targetFile);
            targetFile.delete();
            Files.touch(targetFile);

            File fileDir = new File(path);

            Writer out = new BufferedWriter(new OutputStreamWriter
                    (new FileOutputStream(fileDir), StandardCharsets.UTF_8));

            out.append(content);
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static void deleteFile(String Path) {
        new File(Path).delete();
    }

    public static String[] listFilesForFolder (String dir,String ex) {
        final File folder = new File(dir);


        if(folder.listFiles() != null){
            ArrayList<String> s = new ArrayList<String> ();

            for (final File fileEntry : folder.listFiles()) {

                try {
                    if (fileEntry.getName().substring(fileEntry.getName().lastIndexOf(".")).equalsIgnoreCase(ex))
                        s.add(fileEntry.getName());
                }catch (Exception e)
                {

                }

            }
            return s.toArray(new String[s.size()]);
        }

        return new String[0];


    }
}
