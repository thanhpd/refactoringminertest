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
    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

    File f = new File("tmp/test-out.json");
    PrintWriter writer;
    try {
       writer = new PrintWriter(new FileOutputStream("tmp/output/out.txt", true));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }


    try {
      Repository repo = gitService.cloneIfNotExists(
          "tmp/refactoring-toy-example",
          "https://github.com/apache/mina-sshd.git");

      fw = new FileWriter(f);

      miner.detectBetweenCommits(repo, "1a3da27b8c5667c9b335a8352ba6df5ca75167ba", "ea45ddc079a1dab67d590e800132088f" +
          "14756135", new RefactoringHandler() {
        @Override
        public void handle(String commitId, List<Refactoring> refactorings) {

          System.out.println("Refactorings at " + commitId);
          if (!refactorings.isEmpty()) {
            writer.println(commitId);
            try {
              fw = new FileWriter(new File("tmp/output/" + count + "-" + commitId + ".json"));
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
