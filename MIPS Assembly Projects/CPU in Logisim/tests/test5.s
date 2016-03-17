addi $s0, $0, -1
add $s1, $s0, $s0
addiu $s2, $s0, 6
addu $s2, $s1, $s1
and $s0, $s1, $s2
andi $s0, $s1, -2

addiu $s0, $0, 12
addiu $s1, $0, 3
divu $s0, $s1 # high = 0, low = 4
mfhi $s0
mflo $s1
addiu $s0, $0, 12
addiu $s1, $0, 3
multu $s0, $s1 # high = 0, low = 36
mfhi $s0
mflo $s1

lui $s0, 1
ori $s0, $s1, 1 #s0 is 37

addiu $s1, $0, 12
addiu $s2, $0, -3
or $s0, $s1, $s2

slt $s0, $s2, $s1 # s0 is 1
slti $s0, $s1, 12 # s0 is 0
sltu $s0, $s2, $s1 # s0 is 0

sll $s0, $s1, 5
sra $s0, $s2, 8
srl $s0, $s2, 8

sub $s0, $s1, $s2 #15
subu $s0, $s2, $s1


