addiu $s0, $0, 2
addu  $s1, $s0, $s0
addiu $s3, $0, 1
addiu $s4, $0, 27
subu $s0, $s0, $s3
loop:
	beq $s1, $0, end
	subu $s1, $s1, $s0
	jal funct
	subu $s2, $s2, $s4
	j loop
	
funct:
	addiu $s2, $0, 155
	jr $ra
	
end:
	addiu $s0, $0, 9
