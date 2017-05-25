import com.sun.istack.internal.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 操作系统核心代码
 *
 * @author Bullet
 * @time 2017-05-17 0:50
 */
public class OperationSystem {

  private static String BOOT_PATH =
      new File(".").getAbsolutePath() + "/src/main/resources/boot.bin";
  private static String OS_PATH = new File((".")).getAbsolutePath() + "/src/main/resources/os.bin";

  private static int BYTES_NUM_PER_SECTOR = 512;              // 每扇区字节数
  private static int SURFACE_NUM_PER_FLOOPY = 2;              // 每磁盘磁面数
  private static int CYLINDER_NUM_PER_SURFACE = 80;           // 每磁面磁道数
  private static int SECTOR_NUM_PER_CYLINDER = 18;            // 每磁道扇区数
  private static int FLOOPY_SIZE =
      BYTES_NUM_PER_SECTOR * SURFACE_NUM_PER_FLOOPY * CYLINDER_NUM_PER_SURFACE
          * SECTOR_NUM_PER_CYLINDER;


  public String makeFloopyByBoot(@Nullable String bootFilePath, @Nullable String osFilePath)
      throws IOException {
    System.out.println(BOOT_PATH);
    if (bootFilePath == null) {
      bootFilePath = BOOT_PATH;
    }
    if (osFilePath == null) {
      osFilePath = OS_PATH;
    }
    BufferedInputStream bootIn = new BufferedInputStream(new FileInputStream(bootFilePath));
    BufferedOutputStream osOut = new BufferedOutputStream(new FileOutputStream(osFilePath));
    int sectorSize = 512;
    byte[] bootData = new byte[sectorSize];
    bootIn.read(bootData, 0, sectorSize);
    osOut.write(bootData);      // 写入引导扇区

    // 写入FAT表1和表2， 各9个扇区，两表完全相同
    for (int i = 0; i < 2; i++) {
      osOut.write(0xF0);
      osOut.write(0xFF);
      osOut.write(0xFF);
      for (int j = 3; j < 9 * BYTES_NUM_PER_SECTOR; j++) {
        osOut.write(0x00);
      }
    }

    // 填充其他空间为全0
    for (int i = 19;
        i < SECTOR_NUM_PER_CYLINDER * CYLINDER_NUM_PER_SURFACE * SURFACE_NUM_PER_FLOOPY; i++) {
      for (int j = 0; j < BYTES_NUM_PER_SECTOR; j++) {
        osOut.write(0x00);
      }
    }

    // 关闭打开的文件
    bootIn.close();
    osOut.close();
    return osFilePath;
  }

  public static void main(String[] args) throws IOException {
    OperationSystem op = new OperationSystem();
    op.makeFloopyByBoot(null, null);
  }

}
