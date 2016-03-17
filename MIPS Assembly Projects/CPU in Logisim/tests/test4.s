addiu $s0, $0, 1
addiu $s1, $0, 1

beqal $s0, $s1, map
addiu $s2, $0, 5


map:
	addiu	$sp, $sp, -12
	sw	$ra, 0($sp)
	sw	$s0, 4($sp)
	sw	$s1, 8($sp)
	addiu $s0, $0, 15
	addiu $s1, $0, 17
	multu $s0, $s1
	mfhi $s0
	mflo $s1
	j done


done:
	lw	$s1, 8($sp)
	lw	$s0, 4($sp)
	lw	$ra, 0($sp)
	addiu	$sp, $sp, 12
	jr	$ra