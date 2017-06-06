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
public class Floppy {

  public static int BYTES_NUM_PER_SECTOR = 512;              // 每扇区字节数
  public static int SURFACE_NUM_PER_FLOPPY = 2;              // 每磁盘磁面数
  public static int CYLINDER_NUM_PER_SURFACE = 80;           // 每磁面磁道数
  public static int SECTOR_NUM_PER_CYLINDER = 18;            // 每磁道扇区数
  public static int FLOOPY_SIZE =
      BYTES_NUM_PER_SECTOR * SURFACE_NUM_PER_FLOPPY * CYLINDER_NUM_PER_SURFACE
          * SECTOR_NUM_PER_CYLINDER;                         // 软盘大小
  public static int SECTOR_TOTAL_NUM =
      SURFACE_NUM_PER_FLOPPY * CYLINDER_NUM_PER_SURFACE * SECTOR_NUM_PER_CYLINDER;

  private byte[][][][] sectorData = null;                    // 保存扇区数据

  /**
   * 初始化构造器，默认为全0数据
   */
  public Floppy() {
    sectorData = new byte[SURFACE_NUM_PER_FLOPPY][CYLINDER_NUM_PER_SURFACE]
        [SECTOR_NUM_PER_CYLINDER][BYTES_NUM_PER_SECTOR];
  }

  /**
   * 从软盘对象中读取一个扇区的文件
   *
   * @param surface 扇区所在表面
   * @param cylinder 扇区所在表面的磁道
   * @param sector 扇区所在磁道的扇区号
   * @return 如果读取成功返回512字节的byte数组， 失败返回null
   */
  public byte[] readSector(int surface, int cylinder, int sector) {
    if (!isParametersCorrect(surface, cylinder, sector)) {
      return null;
    }
    return sectorData[surface][cylinder][sector].clone();
  }

  /**
   * 检查给定的参数是否正确
   *
   * @param surface 磁面号
   * @param cylinder 磁道号
   * @param sector 扇区号
   * @return 参数正确返回true，参数错误返回false
   */
  private boolean isParametersCorrect(int surface, int cylinder, int sector) {
    if (surface >= 2 || cylinder >= 80 || sector >= 18) {
      return false;
    }
    return true;
  }

  /**
   * 向指定扇区写入数据，数据大小必须为512字节
   *
   * @param b 需要写入的数据
   * @param surface 磁面号
   * @param cylinder 磁道号
   * @param sector 扇区号
   * @return 成功返回true，失败返回false
   */
  public boolean writeSector(byte[] b, int surface, int cylinder, int sector) {
    if (!isParametersCorrect(b, surface, cylinder, sector)) {
      return false;
    }
    sectorData[surface][cylinder][sector] = b.clone();
    return true;
  }

  /**
   * 检查参数是否正确
   *
   * @param b 待写入数据
   * @param surface 磁面号
   * @param cylinder 磁道号
   * @param sector 扇区号
   */
  private boolean isParametersCorrect(byte[] b, int surface, int cylinder, int sector) {
    if ((b == null || b.length != 512) || !isParametersCorrect(surface, cylinder, sector)) {
      return false;
    }
    return true;
  }

  /**
   * 将软盘对象生成一个软盘文件
   *
   * @param floppyFilePath 要生成的文件的位置
   * @return 生成的文件位置
   * @throws IOException 文件IO中可能抛出的文件IOException
   */
  public String buildFloppyFile(@NotNull String floppyFilePath) throws IOException {
    BufferedOutputStream floppyOut = new BufferedOutputStream(new FileOutputStream(floppyFilePath));
    for (int i = 0; i < SURFACE_NUM_PER_FLOPPY; i++) {
      for (int j = 0; j < CYLINDER_NUM_PER_SURFACE; j++) {
        for (int k = 0; k < SECTOR_NUM_PER_CYLINDER; k++) {
          floppyOut.write(sectorData[i][j][k]);
        }
      }
    }
    floppyOut.flush();
    floppyOut.close();
    return floppyFilePath;
  }

  /**
   * 将一个引导扇区大小（512字节）的文件写入软盘的引导扇区，并生成软盘文件
   *
   * @param bootFilePath 引导文件地址
   * @param osFilePath 生成的软盘文件地址
   * @return 生成的软盘文件地址
   * @throws IOException 文件操作过程中可能出现的IOException
   */
  public static String makeFloppyByBoot(@NotNull String bootFilePath, @NotNull String osFilePath)
      throws IOException {
    BufferedInputStream bootIn = new BufferedInputStream(new FileInputStream(bootFilePath));
    BufferedOutputStream osOut = new BufferedOutputStream(new FileOutputStream(osFilePath));
    int sectorSize = 512;
    byte[] bootData = new byte[sectorSize];
    bootIn.read(bootData, 0, sectorSize);
    osOut.write(bootData);      // 写入引导扇区

    // 写入FAT表1和表2， 各9个扇区，两表完全相同
    for (int i = 0; i < 2; i++) {
      osOut.write((byte) 0xF0);
      osOut.write((byte) 0xFF);
      osOut.write((byte) 0xFF);
      for (int j = 3; j < 9 * BYTES_NUM_PER_SECTOR; j++) {
        osOut.write((byte) 0x00);
      }
    }

    // 填充其他空间为全0
    for (int i = 19;
        i < SECTOR_NUM_PER_CYLINDER * CYLINDER_NUM_PER_SURFACE * SURFACE_NUM_PER_FLOPPY; i++) {
      for (int j = 0; j < BYTES_NUM_PER_SECTOR; j++) {
        osOut.write((byte) 0x00);
      }
    }

    // 关闭打开的文件
    bootIn.close();
    osOut.close();
    return osFilePath;
  }

}
