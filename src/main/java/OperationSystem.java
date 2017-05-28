import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 操作系统核心代码
 *
 * @author Bullet
 * @time 2017-05-17 0:50
 */
public class OperationSystem {

  private static String BOOT_HELLO_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/bootHello.bin";
  private static String BOOT_HELLO_FLOPPY_PATH =
      new File((".")).getAbsolutePath() + "/src/main/resources/hello.img";

  private static String BOOT_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/boot.bin";
  private static String KERNEL_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/kernel.bin";
  private static String BULLET_OS_OUT_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/bulletos.img";

  /**
   * @param bootFile 引导文件地址，如果为null则使用默认地址
   * @param osPath 生成的软盘文件地址，如果为null则使用默认地址
   * @return 生成的软盘文件地址
   * @throws IOException 文件操作可能抛出的IOException
   */
  public String buildOSFile(@Nullable String bootFile, @Nullable String osPath) throws IOException {
    if (bootFile == null) {
      bootFile = BOOT_HELLO_PATH;
    }
    if (osPath == null) {
      osPath = BOOT_HELLO_FLOPPY_PATH;
    }
    return Floppy.makeFloppyByBoot(bootFile, osPath);
  }

  public static void main(String[] args) throws IOException {
    buildOnlyBootOS();
    buildBulletOs(BULLET_OS_OUT_PATH);
  }

  public static boolean buildOnlyBootOS() throws IOException {
    OperationSystem os = new OperationSystem();
    os.buildOSFile(null, null);
    return true;
  }

  public static boolean buildBulletOs(@NotNull String bulletOSPath) throws IOException {
    byte[] bootData = new byte[512];
    InputStream bootIn = new FileInputStream(BOOT_PATH);              // 大小为512字节
    bootIn.read(bootData);
    bootIn.close();

    byte[] kernelData = new byte[512];
    InputStream kernelIn = new FileInputStream(KERNEL_PATH);
    kernelIn.read(kernelData);
    kernelIn.close();

    Floppy floppy = new Floppy();
    floppy.writeSector(bootData, 0, 0, 0);
    floppy.writeSector(kernelData, 0, 1, 2);
    floppy.buildFloppyFile(bulletOSPath);

    return true;
  }

}
