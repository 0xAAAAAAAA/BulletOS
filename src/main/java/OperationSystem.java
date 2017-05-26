import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * 操作系统核心代码
 *
 * @author Bullet
 * @time 2017-05-17 0:50
 */
public class OperationSystem {

  private static String DEFAULT_BOOT_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/boot.bin";
  private static String DEFAULT_OS_PATH =
      new File((".")).getAbsolutePath() + "/src/main/resources/os.bin";


  public String buildOSFile(@Nullable String bootFile, @Nullable String osPath) throws IOException {
    if (bootFile == null) {
      bootFile = DEFAULT_BOOT_PATH;
    }
    if (osPath == null) {
      osPath = DEFAULT_OS_PATH;
    }
    return new Floopy().makeFloopyByBoot(bootFile, osPath);
  }

  public static void main(String[] args) throws IOException {
    OperationSystem os = new OperationSystem();
    os.buildOSFile(null, null);
  }

}
