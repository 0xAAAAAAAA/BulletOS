; bullet-os
; TAB=4
; 生成引导扇区的打印程序二进制文件

		ORG		0x7c00			; 指明程序装载地址

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

; 程序主体

entry:
		MOV		AX,0			    ; 初始化寄存器
		MOV		SS,AX
		MOV		SP,0x7c00
		MOV		DS,AX
		MOV		ES,AX

		MOV		SI,msg
putloop:
		MOV		AL,[SI]
		ADD		SI,1			    ; 给SI加1
		CMP		AL,0
		JE		fin
		MOV		AH,0x0e			  ; 显示一个文字
		MOV		BX,15			    ; 指定字符颜色
		INT		0x10			    ; 调用显卡BIOS
		JMP		putloop
fin:
		HLT						      ; 让CPU停止，等待指令
		JMP		fin				    ; 无限循环

msg:
		DB		0x0a, 0x0a		; 换行两次
		DB		"hello, world!"
		DB		0x0a			    ; 换行
		DB		0

;原书中使用下面指令，但这个在nasm中不通过，故使用times
;RESB   0x1fe - $
    times 510 - ($-$$) DB 0

		DB		0x55, 0xaa