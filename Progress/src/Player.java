import java.io.File;
import java.util.*;

public class Player
{
    public static void main(String[] args)
    {
        processArguments(args);
    }

    static final String currentDirectory = System.getProperty("user.dir");
    static final String locationOfHashMapStorageFile = System.getProperty("user.home") + "/.pl episode data";
    static DataStorer storage = new DataStorer(locationOfHashMapStorageFile);
    static final String videoPlayerCommand = storage.getVideoPlayerCommand();
    public static void processArguments(String[] args)
    {
        if (args.length == 0)
        {
            playCurrentEpisode();
        }
        else
        {
            if (args[0].equals("-ln"))
            {
                try
                {
                int nextEpisode = getNextEpisode();
                System.out.println("next episode is number " + nextEpisode);
                System.out.println(findNameOfFileToPlay(nextEpisode));
                }
                catch (Exception e)
                {
                    System.out.println("could not list name of next episode");
                }
                return;
            }
            int episodeNumber;
            try
            {
                episodeNumber = Integer.parseInt(args[0]);
            }catch (NumberFormatException nfe)
            {
                System.out.println("First argument must be a number or valid command. Use -h for list of valid commands");
                return;
            }
            tryToPlayEpisode(episodeNumber);
        }
    }

    public static void playCurrentEpisode()
    {
        tryToPlayEpisode(getNextEpisode());
    }

    public static int getNextEpisode()
    {
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        if (!hashMap.containsKey(currentDirectory))
        {
            return 1;
        }
        return hashMap.get(currentDirectory) + 1;
    }

    public static void tryToPlayEpisode(int num)
    {
        String fileName;
        try
        {
            fileName = findNameOfFileToPlay(num);
            String[] command = new String[]{"bash","-c",videoPlayerCommand + fileName};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            process.destroy();
        }
        catch (Exception e)
        {
            System.out.println("file not found");
        }
    }

    public static String makeIntoValidFileName(String fileName)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (char x : fileName.toCharArray())
        {
            if (x == '\'')
            {
                stringBuilder.append("'\\''");
            }
            else
            {
                stringBuilder.append(x);
            }
        }
        stringBuilder.insert(0, '\'');
        stringBuilder.append('\'');
        return stringBuilder.toString();
    }

    public static String findNameOfFileToPlay(int num)
    {
        File[] files = new File(currentDirectory).listFiles();
        Objects.requireNonNull(files);
        List<String> fileNames = new ArrayList<>();
        for (File x : files)
        {
            if (!x.isDirectory())
            {
                fileNames.add(x.getName());
            }
        }
        if (num <= 0 || num > fileNames.size())
        {
            throw new RuntimeException("episode not valid");
        }
        Collections.sort(fileNames);
        return makeIntoValidFileName(fileNames.get(num - 1));
    }
}
