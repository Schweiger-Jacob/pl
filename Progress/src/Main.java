public class Main
{
    public static void main(String[] args)
    {
        runPl(args);
    }

    public static void runPl(String[] args)
    {
        if (args.length == 0)
        {
            Player.main(args);
            ProgressTracker.main(args);
            return;
        }
        switch (args[0])
        {
            case "-r", "-l", "-la", "-h", "-x", "-xx", "--", "++" -> ProgressTracker.main(args);
            case "-n" -> Player.playCurrentEpisode();
            default -> Player.main(args);
        }
    }
}
