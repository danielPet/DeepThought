addiu $s0, $0, 38588 #halfword
addiu $s1, $0, 38588 #unsigned byte
addiu $s2, $0, 38588 #signed byte

	addiu	$sp, $sp, -12
	sh	$s0, 0($sp)
	sb	$s1, 4($sp)
	sb	$s2, 8($sp)

addiu $s0, $0, 666 #word
addiu $s1, $0, 666 #halfword
addiu $s2, $0, 666 #byte

	lb	$s2, 8($sp)
	lbu	$s1, 4($sp)
	lhu	$s0, 0($sp)
	addiu	$sp, $sp, 12
	
	addiu $s0, $s0, 9
