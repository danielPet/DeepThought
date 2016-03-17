addiu $s0, $0, 5
addu  $s1, $s0, $s0
addiu $s0, $0, 1
loop:
	beq $s1, $0, end
	subu $s1, $s1, $s0
	addiu $s2, $s2, 1
	j loop
	
end:
	addiu $s0, $0, 9