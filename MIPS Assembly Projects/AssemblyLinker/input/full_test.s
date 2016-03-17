addu $v0, $a0, $a1 #simple
or $a2, $a3, $t0
slt $t2, $t3, $s0
sltu $s1, $s2, $s3
jr $t1
sll $sp, $ra, 3

addiu $t0, $t1, 1000 #rtypes
addiu $t0, $t1, -56
ori $t3, $a0, 30000
ori $t3, $a0, 0xD
lui $a0, 31
lui $a3, 0x3F3F
lb $s0, 0($a2)
lbu $s0, 128($a2)
lw $s1 156($a3)
sb $s2, -35($t2)
sw $s3 -999($t3)

		li $v0, 10
label1:	li $a1, -6000
		
label2:
		li $a2, 80000
		li $a3, 0xB0BACAFE
		blt $t3, $a0, label1
		blt $sp, $v0, label2
