import groovy.time.TimeDuration;
import groovy.time.TimeCategory;
import groovy.io.FileType;

class Search {
  public static void main(String[] args) {
    //Make sure that all arguments were passed in to the program.
    if(args.length < 3) {
      println("Please pass in all the necessary arguments: ");
      println("1. Directory");
      println("2. Original Pattern");
      println("3. Replacement String");
      System.exit(-1);
    }
    //set the directory name to the first argument and create the directory.
    def directoryName = args[0];
    def currentDir = new File(directoryName);
    //check if the directory passed in as a proper directory
    if (!currentDir.isDirectory()) {
      println("${directoryName} is not a directory");
      System.exit(-2);
    }
    def backupFile;
    def fileText;
    //set src and replace variables
    def srcExp = args[1]
    def replaceText = args[2]

    def timeStart = new Date();
    /*
    start traversing from set directory.
    I chose this algorithm because it does a recursive search through
    the directory and sub-directories giving me control over what I want to exclude.
    In this case I am only excluding files that don't match in pattern but I can
    easily make it exclude certain directories saving potential time.

    This algorithm can be made faster by simply not checking whether the file
    contains the pattern to create the backup. I could simply attempt to replaceAll
    and create a backup regardless of whether the file contains the pattern or not.
    This of course can lead to issues with memory. In a big directory with many files
    creating that many backups could get very bad.

    Currenty I am allowing backups to be backed up themeselves if the program is
    run again in the same directory. This is so that in the event that we change
    the original twice more than once we have all the original copies. We can
    easily remove that feature by adding an excludeFilterName or checking
    beforehand. This would also save time and space because we wouldn't need to
    go looking in those files.

    This can be done in other ways as well and the fastest way is using
    the boyer-moore method which is implemented in grep/egrep but I didn't want
    to use those because this is a more native way and in order to implemente
    my own boyer-moore alg it would take a lot more research and time learning
    groovy.

    */
    currentDir.traverse(
        type         : FileType.FILES,
        excludeFilter : {if (!it.text.contains(srcExp)) return it;},
    ) {
      println "${srcExp} was found in the file ${it}.";
      fileText = it.text;
      backupFile = new File(it.path + ".bak");
      backupFile.write(fileText);
      fileText = fileText.replaceAll(srcExp, replaceText)
      it.write(fileText);
    }
    def timeStop = new Date();
    TimeDuration duration = TimeCategory.minus(timeStop, timeStart)
    println("The program took a total of ${duration} to complete.");
  }
}
