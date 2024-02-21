import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class DataStorer
{
    private final String locationOfHashMapStorageFile;
    private final String locationOfVideoPlayerCommandFile;
    DataStorer(String locationToStoreData)
    {
        this.locationOfHashMapStorageFile = locationToStoreData + "/HashMapStorage.ser";
        this.locationOfVideoPlayerCommandFile = locationToStoreData + "/VideoPlayerCommand.txt";
        ensureHashMapStorageFileExists();
        ensureVideoPlayerCommandFileExists();
    }

    @SuppressWarnings("all")
    private void ensureVideoPlayerCommandFileExists()
    {
        File videoPlayerCommand = new File(locationOfVideoPlayerCommandFile);
        if (!videoPlayerCommand.exists())
        {
            try
            {
                videoPlayerCommand.getParentFile().mkdirs();
                videoPlayerCommand.createNewFile();
                FileWriter fileWriter = new FileWriter(locationOfVideoPlayerCommandFile);
                fileWriter.write("mpv \n");
                System.out.println("mpv set as default video player");
                fileWriter.close();
            }catch (Exception ignored){}
        }
    }

    @SuppressWarnings("all")
    private void ensureHashMapStorageFileExists()
    {
        File hashMapStorage = new File(locationOfHashMapStorageFile);
        if (!hashMapStorage.exists())
        {
            try
            {
                hashMapStorage.getParentFile().mkdirs();
                hashMapStorage.createNewFile();
                putHashMapInFile(new HashMap<>());
            }catch (Exception ignored){}
        }
    }

    public String getVideoPlayerCommand()
    {
        String output = "mpv ";
        try
        {
        Scanner scnr = new Scanner(new File(locationOfVideoPlayerCommandFile));
        output = scnr.nextLine();
        }catch (Exception ignored){}
        return output;
    }

    public HashMap<String, Integer> getHashMapFromFile()
    {
        try
        {
            //reading from file
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(locationOfHashMapStorageFile));
            @SuppressWarnings("unchecked")
            HashMap<String, Integer> hashMap = (HashMap<String, Integer>) in.readObject();
            in.close();
            return hashMap;
        }catch (Exception e)
        {
            System.out.println("ERROR ERROR COULD NOT GET HASHMAP FROM FILE!!!!");
            return null;
        }
    }

    public void putHashMapInFile(HashMap<String, Integer> hashMap)
    {
        try
        {
            // Writing to the file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(locationOfHashMapStorageFile));
            out.writeObject(hashMap);
            out.close();
        }catch (Exception ignored){}
    }
}
