import java.io.*;
import java.util.*;

public class ProgressTracker
{
    public static void main(String[] args)
    {
        processArguments(args);
    }

    static final String currentDirectory = System.getProperty("user.dir");
    // this is where the episode progress is stored
    static final String locationOfHashMapStorageFile = System.getProperty("user.home") + "/.pl episode data";
    static DataStorer storage = new DataStorer(locationOfHashMapStorageFile);

    public enum UserResponse
    {
        YES,
        NO,
        QUIT,
    }
    public static void processArguments(String[] args)
    {
        try
        {
            if (args.length > 0)
            {
                // -r followed by a number is to change the current episode and -l is to list it
                switch (args[0])
                {
                    case "-r" -> processArgumentsForDashRCommand(args);
                    case "-l" -> printCurrentEpisode(args);
                    case "-la" -> displayAllCounts();
                    case "-h" -> help();
                    case "-x" -> removeEntry(args);
                    case "-xx" -> removeEntriesInDirectory(args);
                    case "--" -> changeRelativelyNumberOfEpsWatched(-1);
                    case "++" -> changeRelativelyNumberOfEpsWatched(1);
                    default -> System.out.println("Invalid arguments, use -h for help");
                }
            }
            else
            {
                changeRelativelyNumberOfEpsWatched(1);
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("File is not correct");
        }
    }

    public static void removeEntry(String[] args)
    {
        switch (args.length)
        {
            case 1 -> removeEntry(currentDirectory);
            case 2 -> removeEntry(args[1]);
            default -> System.out.println("There are too many arguments. There are supposed to be 2 but instead there are " + args.length);
        }
    }

    public static void removeEntry(String nameToRemove)
    {
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        if (hashMap.containsKey(nameToRemove))
        {
            System.out.println("remove " + nameToRemove + "? (y/n/q)");
            switch (getYesNoOrQuitFromUser())
            {
                case YES -> hashMap.remove(nameToRemove);
                case NO -> System.out.println("No file were removed");
            }
            storage.putHashMapInFile(hashMap);
        }
    }

    public static void removeEntriesInDirectory(String[] args) throws IOException, ClassNotFoundException
    {
        switch (args.length)
        {
            case 1 -> removeEntriesInDirectory(currentDirectory);
//            case 1 -> System.out.println("must be followed by string of entry to remove.");
            case 2 -> removeEntriesInDirectory(args[1]);
            default -> System.out.println("There are too many arguments. There are supposed to be 2 but instead there are " + args.length);
        }
    }

    public static void removeEntriesInDirectory(String nameToRemove)
    {
        nameToRemove = nameToRemove.toLowerCase();
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        List<String> entriesToRemove = new ArrayList<>();
        boolean hasUserQuit = false;
        for (Map.Entry<String, Integer> entry : hashMap.entrySet())
        {
            if (hasUserQuit)
            {
                break;
            }
            if (entry.getKey().toLowerCase().contains(nameToRemove))
            {
                System.out.println("remove " + entry.getKey() + "? (y/n/q)");
                switch (getYesNoOrQuitFromUser())
                {
                    case YES -> entriesToRemove.add(entry.getKey());
                    case QUIT -> hasUserQuit = true;
                }
            }
        }
        for (String entryKey : entriesToRemove)
        {
            hashMap.remove(entryKey);
        }
        if (entriesToRemove.isEmpty())
        {
            System.out.println("No file were removed");
//            System.out.println("No files on record match the string");
        }
        storage.putHashMapInFile(hashMap);
    }

    public static UserResponse getYesNoOrQuitFromUser()
    {
        Scanner scnr = new Scanner(System.in);
        String input = scnr.next().toLowerCase();
        while (!(input.equals("y") || input.equals("n") || input.equals("q")))
        {
            System.out.println("Input must be y, n, or q try again");
            input = scnr.next().toLowerCase();
        }
        return switch (input)
        {
            case "y" -> UserResponse.YES;
            case "n" -> UserResponse.NO;
            case "q" -> UserResponse.QUIT;
            default -> throw new RuntimeException("users response is not valid");
        };
    }

    public static void displayAllCounts() throws IOException, ClassNotFoundException
    {
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        List<String> locations = new ArrayList<>(hashMap.keySet());
        Collections.sort(locations);
        for (String location : locations)
        {
            System.out.print(location + ", ");
            printCurrentEpisode(location);
        }
    }

    public static void help()
    {
            System.out.println(
                """
                -r replaces episode count with new count. Must be followed by an integer.
                -l lists out the number of watched episodes for the current directory. It can be used for other directory's by following it with a directory's location.
                -la displays the number of episodes watched for all directory's on file.
                -ln displays the directory of the next episode.
                -h prints this help message.
                -x removes current directory. It's case insensitive and the user is asked to confirm before removing items.
                -xx removes entries that contain the string. It's case insensitive and the user is asked to confirm before removing items.
                -- decrements episode count by one""");
    }

    @SuppressWarnings("all")
    public static void printCurrentEpisode(String[] args) throws IOException, ClassNotFoundException
    {
        switch (args.length)
        {
            case 1 -> printCurrentEpisode(currentDirectory);
            case 2 -> printCurrentEpisode(args[1]);
            default -> System.out.println("There are too many arguments. There are supposed to be 2 but instead there are " + args.length);
        }
    }

public static void printCurrentEpisode(String location)
{
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        if (hashMap.containsKey(location))
        {
            System.out.println(hashMap.get(location) + " episodes watched");
        }
        else
        {
            System.out.println(location + " not in record");
//            System.out.println("0 episodes watched");
        }
    }

    public static void processArgumentsForDashRCommand(String[] args) throws IOException, ClassNotFoundException
    {
        if (args.length > 1)
        {
            try
            {
                int newEpCount = Integer.parseInt(args[1]);
                changeNumberOfEpsWatched(newEpCount);
            }
            catch (NumberFormatException e)
            {
                System.out.println("The second argument must be a number, try again");
            }
        }
        else
        {
            System.out.println("There should be a second argument and it should be a number ");
        }
    }


    public static void changeNumberOfEpsWatched(int newEpCount)
    {
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        hashMap.put(currentDirectory, newEpCount);
        storage.putHashMapInFile(hashMap);
    }

    // increases number of eps watched by 1
    public static void changeRelativelyNumberOfEpsWatched(int amountToChangeBy) throws IOException, ClassNotFoundException
    {
        HashMap<String, Integer> hashMap = storage.getHashMapFromFile();
        if (hashMap.containsKey(currentDirectory))
        {
            hashMap.put(currentDirectory, hashMap.get(currentDirectory) + amountToChangeBy);
        }
        else
        {
            hashMap.put(currentDirectory, amountToChangeBy);
        }
        storage.putHashMapInFile(hashMap);
        printCurrentEpisode(currentDirectory);
//        System.out.println(hashMap.get(currentDirectory) + " episodes watched");
    }
}
