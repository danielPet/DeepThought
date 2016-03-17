;;; Test cases for Scheme.
;;;
;;; In order to run only a prefix of these examples, add the line
;;;
;;; (exit)
;;;
;;; after the last test you wish to run.

;;; **********************************
;;; * We added more of our own here! *
;;; **********************************

;   *********************Q2***********************
(define a '('hi 'there 'cs 'student))
; expect a
a
; expect ((quote hi) (quote there) (quote cs) (quote student))
(define a (1 . ))
; expect Error: unexpected token: )

(not_there)
; expect Error: unknown identifier: not_there

;   *********************Q4***********************
(+ 1 2)
; expect 3
(* 3 4 (- 5 2) 1)
; expect 36
(+ 1)
; expect 1
(odd? 31)
; expect True
(not #t)
; expect False
(eq? 6 (* 2 3))
; expect True
(pair? '(a . b))
; expect True
(pair? '(a b c))
; expect True
(pair? '())
; expect False
(pair? '(a b))
; expect True
(+ (* 3
      (+ (* 2 4)
         (+ 3 5)))
   (+ (- 10 7)
      6))
; expect 57
(>= 2 1)
; expect True
(define (func1 a)
  (define (func2 b)
    (define (func3 c)
      a
    )
    (func3 b)
  )
  (func2 a)
)

(func1 1)
; expect 1

;   *********************Q5A**********************
(define x 15)
; expect x
(define y (* 2 x))
; expect y
y
; expect 30
(+ y (* y 2) 1)
; expect 91
(define x 20)
; expect x
x
; expect 20
(define pi 3.14)
(* pi 2)
; expect 6.28
(define 0 1)
; expect Error: bad argument to define

;   *********************Q6B**********************
'hello
; expect hello
'(1 . 2)
; expect (1 . 2)
'(1 (2 three . (4 . 5)))
; expect (1 (2 three 4 . 5))
(car '(a b))
; expect a
(eval (cons 'car '('(1 2))))
; expect 1
(define x (list 'a 'b 'c))
; expect x
(define y x)
; expect y
(list? y)
; expect True
(eq? x y)
; expect True
(define will-pass '(/ 1 0))
; expect will-pass
will-pass
; expect (/ 1 0)
(define will-not-pass (/ 1 0))
; expect Error: division by zero

;   *********************Q7***********************
(begin (+ 2 3) (+ 5 6))
; expect 11
(define x (begin (display 3) (newline) (+ 2 3)))
; expect x
(+ x 3)
; expect 8
(begin (print 3) '(+ 2 3))
; expect (+ 2 3)

;   *********************Q8***********************
(lambda (x y) (+ x y))
; expect (lambda (x y) (+ x y))
(lambda (y) (print y) (* y 2))
; expect (lambda (y) (begin (print y) (* y 2)))

;   *********************Q9A**********************
(define f (lambda (x) (* x 2)))
; expect f
(f 3)
; expect 6
(define (f x) (* x 2))
; expect f
(f 3)
; expect 6
(define (square x) (* x x))
; expect square
square
; expect (lambda (x) (* x x))
(define (fib n)
  (if (< n 2)
    1
    (+ (fib (- n 1)) (fib (- n 2)))
  )
)
(fib 7)
; expect 21
(fib 1)
; expect 1


; **Q10-12: No scheme-specific tests**


;   *********************Q13A*********************
(if (= 4 2) #t #f)
; expect False
(if (= 4 4) (* 1 2) (+ 3 4))
; expect 2
(if (= 4 2) #t)
; expect okay

;   *********************Q14B*********************
(and)
; expect True
(or)
; expect False
(and 4 5 6)
; expect 6
(or 5 2 1)
; expect 5
(and #t #f 42 (/ 1 0))
; expect False
(or 4 #t (/ 1 0))
; expect 4

;   *********************Q15**********************
(cond ((= 4 3) 'nope)
           ((= 4 4) 'hi)
           (else 'wait))
; expect hi
(cond ((= 4 3) 'wat)
           ((= 4 4))
           (else 'hm))
; expect True
(cond ((= 4 4) 'here 42)
           (else 'wat 0))
; expect 42
(cond (12))
; expect 12
(cond ((= 4 3))
           ('hi))
; expect hi

;   *********************Q16**********************
(define x 'hi)
; expect x
(define y 'bye)
; expect y
(let ((x 42) (y (* 5 10)))
       (list x y))
; expect (42 50)
(list x y)
; expect (hi bye)
(let ((x 42)) x 1 2)
; expect 2

;   *********************Q17**********************
(define f (mu (x) (+ x y)))
; expect f
(define g (lambda (x y) (f (+ x x))))
; expect g
(g 3 7)
; expect 13

;   *********************Q18*********************
(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))
(define (cadar x) (car (cdr (car x))))

(define test-list '(1 (2 3) 4 5))
; expect test-list
(cadr test-list)
; expect (2 3)
(car (cadr test-list))
; expect 2

(define (zip pairs)
  (define (build first second pairs)
    (if (null? pairs) (cons (reverse first) (cons (reverse second) nil))
        (build (cons (caar pairs) first) (cons (cadar pairs) second) (cdr pairs))
    )
  )
  (build '() '() pairs)
)

(define (reverse list)
  (define (builder list new)
    (if (null? list) new
        (builder (cdr list) (cons (car list) new))
    )
  )
  (builder list '())
)

(zip '((1 2)))
; expect ((1) (2))
(zip '((1 2) (3 4) (5 6)))
; expect ((1 3 5) (2 4 6))

;   *********************Q19*********************
(define (list-partitions total max-pieces max-value)
  (cond
    ((>= 0 max-value) nil)
    ((>= 0 total) (cons '() nil))
    ((>= 1 total) (cons (cons total nil) nil))
    ((>= 1 max-pieces) (if (<= total max-value) (cons (cons total nil) nil) nil))
    ((<= max-value total) (append (distribute max-value (list-partitions (- total max-value ) (- max-pieces 1) max-value))
                                  (list-partitions total max-pieces (- max-value 1))))
    (else (list-partitions total max-pieces (- max-value 1)))
  )
)

(define (distribute value rest)
  (if (null? rest)
    nil
    (cons (cons value (car rest)) (distribute value (cdr rest)))
  )
)

(list-partitions 5 2 4)
; expect ((4 1) (3 2))
(list-partitions 7 3 5)
; expect ((5 2) (5 1 1) (4 3) (4 2 1) (3 3 1) (3 2 2))
(list-partitions 9 2 2)
; expect ()
(list-partitions 4 4 1)
; expect ((1 1 1 1))

;   *********************Q20*********************
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (analyze expr)
  (cond ((atom? expr) expr)
        ((quoted? expr) expr)
        
        ((or (lambda? expr) (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           (cons form (cons params (apply-to-all analyze body)))))

        ((let? expr)
         (let ((values (cadr (zip (cadr expr))))
               (body   (analyze (cddr expr)))
               (parameters (car (zip (cadr expr))))
               )
           (cons (cons 'lambda (cons parameters body)) (apply-to-all analyze values))
           
           ))
        ((list? expr)
            (apply-to-all analyze expr)
          )

        (else expr)
  )
)

(analyze (let ((a 1) (b 2)) (+ a b)))
; expect 3

;   *********************Q22EC*******************
(define (reverse lst)
(define (r-iter list so-far)
(if (null? list)
so-far
(r-iter (cdr list) (cons (car list) so-far))
)
)
(r-iter lst nil)
)

(reverse (cons 1 (cons 2 (cons 3 nil))))
; expect (3 2 1)
(car (reverse (cons 3 (cons 2 (cons 1 nil)))))
; expect 1


;;; **********************************

;;; These are examples from several sections of "The Structure
;;; and Interpretation of Computer Programs" by Abelson and Sussman.

;;; License: Creative Commons share alike with attribution

;;; 1.1.1

10
; expect 10

(+ 137 349)
; expect 486

(- 1000 334)
; expect 666

(* 5 99)
; expect 495

(/ 10 5)
; expect 2

(+ 2.7 10)
; expect 12.7

(+ 21 35 12 7)
; expect 75

(* 25 4 12)
; expect 1200

(+ (* 3 5) (- 10 6))
; expect 19

(+ (* 3 (+ (* 2 4) (+ 3 5))) (+ (- 10 7) 6))
; expect 57

(+ (* 3
      (+ (* 2 4)
         (+ 3 5)))
   (+ (- 10 7)
      6))
; expect 57


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Move the following (exit) line to run additional tests. ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;; 1.1.2

(define size 2)
; expect size
size
; expect 2

(* 5 size)
; expect 10

(define pi 3.14159)
(define radius 10)
(* pi (* radius radius))
; expect 314.159

(define circumference (* 2 pi radius))
circumference
; expect 62.8318

;;; 1.1.4

(define (square x) (* x x))
; expect square
(square 21)
; expect 441

(define square (lambda (x) (* x x))) ; See Section 1.3.2
(square 21)
; expect 441

(square (+ 2 5))
; expect 49

(square (square 3))
; expect 81

(define (sum-of-squares x y)
  (+ (square x) (square y)))
(sum-of-squares 3 4)
; expect 25

(define (f a)
  (sum-of-squares (+ a 1) (* a 2)))
(f 5)
; expect 136

;;; 1.1.6

(define (abs x)
  (cond ((> x 0) x)
        ((= x 0) 0)
        ((< x 0) (- x))))
(abs -3)
; expect 3

(abs 0)
; expect 0

(abs 3)
; expect 3

(define (a-plus-abs-b a b)
  ((if (> b 0) + -) a b))
(a-plus-abs-b 3 -2)
; expect 5

;;; 1.1.7

(define (sqrt-iter guess x)
  (if (good-enough? guess x)
      guess
      (sqrt-iter (improve guess x)
                 x)))
(define (improve guess x)
  (average guess (/ x guess)))
(define (average x y)
  (/ (+ x y) 2))
(define (good-enough? guess x)
  (< (abs (- (square guess) x)) 0.001))
(define (sqrt x)
  (sqrt-iter 1.0 x))
(sqrt 9)
; expect 3.00009155413138

(sqrt (+ 100 37))
; expect 11.704699917758145

(sqrt (+ (sqrt 2) (sqrt 3)))
; expect 1.7739279023207892

(square (sqrt 1000))
; expect 1000.000369924366

;;; 1.1.8

(define (sqrt x)
  (define (good-enough? guess)
    (< (abs (- (square guess) x)) 0.001))
  (define (improve guess)
    (average guess (/ x guess)))
  (define (sqrt-iter guess)
    (if (good-enough? guess)
        guess
        (sqrt-iter (improve guess))))
  (sqrt-iter 1.0))
(sqrt 9)
; expect 3.00009155413138

(sqrt (+ 100 37))
; expect 11.704699917758145

(sqrt (+ (sqrt 2) (sqrt 3)))
; expect 1.7739279023207892

(square (sqrt 1000))
; expect 1000.000369924366

;;; 1.3.1

(define (cube x) (* x x x))
(define (sum term a next b)
  (if (> a b)
      0
      (+ (term a)
         (sum term (next a) next b))))
(define (inc n) (+ n 1))
(define (sum-cubes a b)
  (sum cube a inc b))
(sum-cubes 1 10)
; expect 3025

(define (identity x) x)
(define (sum-integers a b)
  (sum identity a inc b))
(sum-integers 1 10)
; expect 55

;;; 1.3.2

((lambda (x y z) (+ x y (square z))) 1 2 3)
; expect 12

(define (f x y)
  (let ((a (+ 1 (* x y)))
        (b (- 1 y)))
    (+ (* x (square a))
       (* y b)
       (* a b))))
(f 3 4)
; expect 456

(define x 5)
(+ (let ((x 3))
     (+ x (* x 10)))
   x)
; expect 38

(let ((x 3)
      (y (+ x 2)))
  (* x y))
; expect 21

;;; 2.1.1

(define (add-rat x y)
  (make-rat (+ (* (numer x) (denom y))
               (* (numer y) (denom x)))
            (* (denom x) (denom y))))
(define (sub-rat x y)
  (make-rat (- (* (numer x) (denom y))
               (* (numer y) (denom x)))
            (* (denom x) (denom y))))
(define (mul-rat x y)
  (make-rat (* (numer x) (numer y))
            (* (denom x) (denom y))))
(define (div-rat x y)
  (make-rat (* (numer x) (denom y))
            (* (denom x) (numer y))))
(define (equal-rat? x y)
  (= (* (numer x) (denom y))
     (* (numer y) (denom x))))


(define x (cons 1 2))
(car x)
; expect 1

(cdr x)
; expect 2

(define x (cons 1 2))
(define y (cons 3 4))
(define z (cons x y))
(car (car z))
; expect 1

(car (cdr z))
; expect 3

z
; expect ((1 . 2) 3 . 4)

(define (make-rat n d) (cons n d))
(define (numer x) (car x))
(define (denom x) (cdr x))
(define (print-rat x)
  (display (numer x))
  (display '/)
  (display (denom x))
  (newline))
(define one-half (make-rat 1 2))
(print-rat one-half)
; expect 1/2 ; okay

(define one-third (make-rat 1 3))
(print-rat (add-rat one-half one-third))
; expect 5/6 ; okay

(print-rat (mul-rat one-half one-third))
; expect 1/6 ; okay

(print-rat (add-rat one-third one-third))
; expect 6/9 ; okay

(define (gcd a b)
  (if (= b 0)
      a
      (gcd b (remainder a b))))
(define (make-rat n d)
  (let ((g (gcd n d)))
    (cons (/ n g) (/ d g))))
(print-rat (add-rat one-third one-third))
; expect 2/3 ; okay

(define one-through-four (list 1 2 3 4))
one-through-four
; expect (1 2 3 4)

(car one-through-four)
; expect 1

(cdr one-through-four)
; expect (2 3 4)

(car (cdr one-through-four))
; expect 2

(cons 10 one-through-four)
; expect (10 1 2 3 4)

(cons 5 one-through-four)
; expect (5 1 2 3 4)

(define (map proc items)
  (if (null? items)
      nil
      (cons (proc (car items))
            (map proc (cdr items)))))
(map abs (list -10 2.5 -11.6 17))
; expect (10 2.5 11.6 17)

(map (lambda (x) (* x x))
     (list 1 2 3 4))
; expect (1 4 9 16)

(define (scale-list items factor)
  (map (lambda (x) (* x factor))
       items))
(scale-list (list 1 2 3 4 5) 10)
; expect (10 20 30 40 50)

(define (count-leaves x)
  (cond ((null? x) 0)
        ((not (pair? x)) 1)
        (else (+ (count-leaves (car x))
                 (count-leaves (cdr x))))))
(define x (cons (list 1 2) (list 3 4)))
(count-leaves x)
; expect 4

(count-leaves (list x x))
; expect 8

;;; 2.2.3

(define (odd? x) (= 1 (remainder x 2)))
(define (filter predicate sequence)
  (cond ((null? sequence) nil)
        ((predicate (car sequence))
         (cons (car sequence)
               (filter predicate (cdr sequence))))
        (else (filter predicate (cdr sequence)))))
(filter odd? (list 1 2 3 4 5))
; expect (1 3 5)

(define (accumulate op initial sequence)
  (if (null? sequence)
      initial
      (op (car sequence)
          (accumulate op initial (cdr sequence)))))
(accumulate + 0 (list 1 2 3 4 5))
; expect 15

(accumulate * 1 (list 1 2 3 4 5))
; expect 120

(accumulate cons nil (list 1 2 3 4 5))
; expect (1 2 3 4 5)

(define (enumerate-interval low high)
  (if (> low high)
      nil
      (cons low (enumerate-interval (+ low 1) high))))
(enumerate-interval 2 7)
; expect (2 3 4 5 6 7)

(define (enumerate-tree tree)
  (cond ((null? tree) nil)
        ((not (pair? tree)) (list tree))
        (else (append (enumerate-tree (car tree))
                      (enumerate-tree (cdr tree))))))
(enumerate-tree (list 1 (list 2 (list 3 4)) 5))
; expect (1 2 3 4 5)

;;; 2.3.1

(define a 1)

(define b 2)

(list a b)
; expect (1 2)

(list 'a 'b)
; expect (a b)

(list 'a b)
; expect (a 2)

(car '(a b c))
; expect a

(cdr '(a b c))
; expect (b c)

(define (memq item x)
  (cond ((null? x) false)
        ((eq? item (car x)) x)
        (else (memq item (cdr x)))))
(memq 'apple '(pear banana prune))
; expect False

(memq 'apple '(x (apple sauce) y apple pear))
; expect (apple pear)

(define (equal? x y)
  (cond ((pair? x) (and (pair? y)
                        (equal? (car x) (car y))
                        (equal? (cdr x) (cdr y))))
        ((null? x) (null? y))
        (else (eq? x y))))
(equal? '(1 2 (three)) '(1 2 (three)))
; expect True

(equal? '(1 2 (three)) '(1 2 three))
; expect False

(equal? '(1 2 three) '(1 2 (three)))
; expect False

;;; Peter Norvig tests (http://norvig.com/lispy2.html)

(define double (lambda (x) (* 2 x)))
(double 5)
; expect 10

(define compose (lambda (f g) (lambda (x) (f (g x)))))
((compose list double) 5)
; expect (10)

(define apply-twice (lambda (f) (compose f f)))
((apply-twice double) 5)
; expect 20

((apply-twice (apply-twice double)) 5)
; expect 80

(define fact (lambda (n) (if (<= n 1) 1 (* n (fact (- n 1))))))
(fact 3)
; expect 6

(fact 50)
; expect 30414093201713378043612608166064768844377641568960512000000000000

(define (combine f)
  (lambda (x y)
    (if (null? x) nil
      (f (list (car x) (car y))
         ((combine f) (cdr x) (cdr y))))))
(define zip (combine cons))
(zip (list 1 2 3 4) (list 5 6 7 8))
; expect ((1 5) (2 6) (3 7) (4 8))

(define riff-shuffle (lambda (deck) (begin
    (define take (lambda (n seq) (if (<= n 0) (quote ()) (cons (car seq) (take (- n 1) (cdr seq))))))
    (define drop (lambda (n seq) (if (<= n 0) seq (drop (- n 1) (cdr seq)))))
    (define mid (lambda (seq) (/ (length seq) 2)))
    ((combine append) (take (mid deck) deck) (drop (mid deck) deck)))))
(riff-shuffle (list 1 2 3 4 5 6 7 8))
; expect (1 5 2 6 3 7 4 8)

((apply-twice riff-shuffle) (list 1 2 3 4 5 6 7 8))
; expect (1 3 5 7 2 4 6 8)

(riff-shuffle (riff-shuffle (riff-shuffle (list 1 2 3 4 5 6 7 8))))
; expect (1 2 3 4 5 6 7 8)

;;; Additional tests

(apply square '(2))
; expect 4

(apply + '(1 2 3 4))
; expect 10

(apply (if false + append) '((1 2) (3 4)))
; expect (1 2 3 4)

(if 0 1 2)
; expect 1

(if '() 1 2)
; expect 1

(or false true)
; expect True

(or)
; expect False

(and)
; expect True

(or 1 2 3)
; expect 1

(and 1 2 3)
; expect 3

(and False (/ 1 0))
; expect False

(and True (/ 1 0))
; expect Error

(or 3 (/ 1 0))
; expect 3

(or False (/ 1 0))
; expect Error

(or (quote hello) (quote world))
; expect hello

(if nil 1 2)
; expect 1

(if 0 1 2)
; expect 1

(if (or false False #f) 1 2)
; expect 2

(define (loop) (loop))
(cond (false (loop))
      (12))
; expect 12

((lambda (x) (display x) (newline) x) 2)
; expect 2 ; 2

(define g (mu () x))
(define (high f x)
  (f))

(high g 2)
; expect 2

(define (print-and-square x)
  (print x)
  (square x))
(print-and-square 12)
; expect 12 ; 144

(/ 1 0)
; expect Error

(define addx (mu (x) (+ x y)))
(define add2xy (lambda (x y) (addx (+ x x))))
(add2xy 3 7)
; expect 13


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Scheme Implementations ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; len outputs the length of list s
(define (len s)
  (if (eq? s '())
    0
    (+ 1 (len (cdr s)))))
(len '(1 2 3 4))
; expect 4

;;;;;;;;;;;;;;;;;;;;
;;; Extra credit ;;;
;;;;;;;;;;;;;;;;;;;;

(exit)

; Tail call optimization test
(define (sum n total)
  (if (zero? n) total
    (sum (- n 1) (+ n total))))
(sum 1001 0)
; expect 501501
