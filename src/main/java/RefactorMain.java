import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class RefactorMain {
  static FileWriter fw;
  static int count = 0;

  public static void main(String[] args) throws IOException {
    String repoUrl, repoName, startCommitSHA, endCommitSHA;

    try {
      if (args == null || args.length == 0) {
        throw new IllegalArgumentException("Please specify Repo URL, Repo Name, Start Commit SHA, and End Commit SHA as arguments.");
      }

      repoUrl = args[0];
      repoName = args[1];
      startCommitSHA = args[2];
      endCommitSHA = args[3];

      if (repoUrl == null || repoName == null) {
        throw new IllegalArgumentException("Repo URL and Repo Name are required");
      }

      if (startCommitSHA == null || endCommitSHA == null) {
          throw new IllegalArgumentException("Start and End Commit SHA are required");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

    // Make dir tmp output
    File dir2 = new File("tmp/output/" + repoName);
    dir2.mkdirs();

    PrintWriter writer;
    try {
       writer = new PrintWriter(new FileOutputStream("tmp/output/" + repoName + "/out.txt", true));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }


    try {
      Repository repo = gitService.cloneIfNotExists(
          "tmp/" + repoName,
          repoUrl);

      miner.detectBetweenCommits(repo, startCommitSHA, endCommitSHA, new RefactoringHandler() {
        @Override
        public void handle(String commitId, List<Refactoring> refactorings) {

          System.out.println("Refactorings at " + commitId);
          if (!refactorings.isEmpty()) {
            writer.println(commitId);
            try {
              fw = new FileWriter(new File("tmp/output/" + repoName + "/" + commitId + ".json"));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            for (Refactoring ref : refactorings) {
              try {
                fw.write(ref.toJSON() + "\n");
              } catch (IOException e) {
                e.printStackTrace();
              }
            }

            if (fw != null) {
              try {
                fw.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            count++;
          } else {
            System.out.println("no refactorings");
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fw != null) {
        try {
          fw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      writer.close();
    }
  }
}
