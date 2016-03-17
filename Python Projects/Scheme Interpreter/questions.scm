; Some utility functions that you may find useful.
(define (apply-to-all proc items)
  (if (null? items)
      '()
      (cons (proc (car items))
            (apply-to-all proc (cdr items)))))

(define (keep-if predicate sequence)
  (cond ((null? sequence) nil)
        ((predicate (car sequence))
         (cons (car sequence)
               (keep-if predicate (cdr sequence))))
        (else (keep-if predicate (cdr sequence)))))

(define (accumulate op initial sequence)
  (if (null? sequence)
      initial
      (op (car sequence)
          (accumulate op initial (cdr sequence)))))

(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))
(define (cadar x) (car (cdr (car x))))

; Problem 18
;; Turns a list of pairs into a pair of lists

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

(zip '())
; expect (() ())
(zip '((1 2)))
; expect ((1) (2))
(zip '((1 2) (3 4) (5 6)))
; expect ((1 3 5) (2 4 6))


; Problem 19

;; A list of all ways to partition TOTAL, where  each partition must
;; be at most MAX-VALUE and there are at most MAX-PIECES partitions.

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
; expects a permutation of ((4 1) (3 2))
(list-partitions 7 3 5)
; expects a permutation of ((5 2) (5 1 1) (4 3) (4 2 1) (3 3 1) (3 2 2))


; Problem 20
;; Returns a function that takes in an expression and checks if it is the special
;; form FORM
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

(analyze 1)
; expect 1
(analyze 'a)
; expect a
(analyze '(+ 1 2))
; expect (+ 1 2)

;; Quoted expressions remain the same
(analyze '(quote (let ((a 1) (b 2)) (+ a b))))
; expect (quote (let ((a 1) (b 2)) (+ a b)))

;; Lambda parameters not affected, but body affected
(analyze '(lambda (let a b) (+ let a b)))
; expect (lambda (let a b) (+ let a b))
(analyze '(lambda (x) a (let ((a x)) a)))
; expect (lambda (x) a ((lambda (a) a) x))

(analyze '(let ((a 1)
                (b 2))
            (+ a b)))
; expect ((lambda (a b) (+ a b)) 1 2)
(analyze '(let ((a (let ((a 2)) a))
                (b 2))
            (+ a b)))
; expect ((lambda (a b) (+ a b)) ((lambda (a) a) 2) 2)
(analyze '(let ((a 1))
            (let ((b a))
              b)))
; expect ((lambda (a) ((lambda (b) b) a)) 1)
