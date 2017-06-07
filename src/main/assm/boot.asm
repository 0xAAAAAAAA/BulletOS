;bullet-os
;TAB=4
;生成引导装载程序机器码，大小为512字节,装载到0x8000地址

org  0x7c00;

LOAD_ADDR  EQU  0X8000

; 标准FAT12格式软盘专用的代码 Stand FAT12 format floppy code

		JMP		entry
		DB		0x90
		DB		"BULLETBL"		; 启动扇区名称（8字节）,厂商名
		DW		512				    ; 每个扇区（sector）大小（必须512字节）
		DB		1				      ; 簇（cluster）大小（必须为1个扇区）
		DW		1				      ; FAT起始位置（一般为第一个扇区），BOOT记录占用扇区数
		DB		2				      ; FAT表个数（必须为2）
		DW		224				    ; 根目录文件数最大值（一般为224项）
		DW		2880			    ; 该磁盘大小（软盘，必须为2880扇区，1440*1024/512）
		DB		0xf0			    ; 磁盘类型（必须为0xf0），介质描述符
		DW		9				      ; 每FAT的长度（必须为9扇区）
		DW		18				    ; 一个磁道（track）有几个扇区（必须为18）
		DW		2				      ; 磁头数（必须为2）
		DD		0				      ; 不使用分区，必须是0，隐藏扇区数
		DD		2880			    ; 重写一次磁盘大小，如果BPB_TotSec16是0，由这个值记录扇区数
		DB		0,0,0x29		  ; 意义不明（固定），（中断13的驱动器号，未使用，扩展引导标记）
		DD		0xffffffff		; （可能是）卷标号码，卷序列号
		DB		"BULLET-OS  "	; 磁盘的名称（必须为11字节，不足填空格），卷标
		DB		"FAT12   "		; 磁盘格式名称（必须为8字节，不足填空格），文件系统类型
		RESB	18				    ; 先空出18字节

entry:
    mov  ax, 0
    mov  ss, ax
    mov  ds, ax
    mov  es, ax
    mov  si, ax

readFloppy:
    mov          CH, 1          ; CH 用来存储柱面号(0-79)
    mov          DH, 0          ; DH 用来存储磁头号(0-1)
    mov          CL, 2          ; CL 用来存储扇区号(1-18)（java代码中对应为1）

    mov          BX, LOAD_ADDR  ; ES:BX 数据存储缓冲区

    mov          AH, 0x02       ; AH = 02 表示要做的是读盘操作
    mov          AL,  1         ; AL 表示要练习读取几个扇区
    mov          DL, 0          ; 驱动器编号，一般我们只有一个软盘驱动器，所以写死
                                ; 为0
    INT          0x13           ; 调用BIOS中断实现磁盘读取功能

    JC           fin

    jmp          LOAD_ADDR

fin:
    HLT
    jmp  fin

;原书中使用下面指令，但这个在nasm中不通过，故使用times
;RESB   0x1fe - $
    times 510 - ($-$$) DB 0

		DB		0x55, 0xaa