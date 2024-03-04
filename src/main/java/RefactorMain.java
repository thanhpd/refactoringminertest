import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
  public static void main(String[] args) {
    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

    File f = new File("tmp/test-out.json");

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
          for (Refactoring ref : refactorings) {
            try {
              fw.write(ref.toJSON() + "\n");

            } catch (IOException e) {
              e.printStackTrace();
            }
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
    }
  }
}
