import com.sun.istack.internal.NotNull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 软盘格式与读写
 *
 * @author Bullet
 * @time 2017-05-26 10:51
 */
public class Floopy {

  public static int BYTES_NUM_PER_SECTOR = 512;              // 每扇区字节数
  public static int SURFACE_NUM_PER_FLOOPY = 2;              // 每磁盘磁面数
  public static int CYLINDER_NUM_PER_SURFACE = 80;           // 每磁面磁道数
  public static int SECTOR_NUM_PER_CYLINDER = 18;            // 每磁道扇区数
  public static int FLOOPY_SIZE =
      BYTES_NUM_PER_SECTOR * SURFACE_NUM_PER_FLOOPY * CYLINDER_NUM_PER_SURFACE
          * SECTOR_NUM_PER_CYLINDER;


  public String makeFloopyByBoot(@NotNull String bootFilePath, @NotNull String osFilePath)
      throws IOException {
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

}
