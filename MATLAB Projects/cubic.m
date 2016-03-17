function [rts,info] = cubic(C)
%
% Finds the solutions to a polynomial equation using
% the coefficients provided in C.
%
% Written by Daniel Peterson for Math 128A, Spring 2015
% Discussion 107 3/3/15
%
if (length(C)~=4)
    error('Wrong input array size.');
end

format long

a = C(1);
b = C(2);
c = C(3);
d = C(4);

rts = [];

funcEval =@(x) ((a*x+b)*x+c)*x+d;
derivEval =@(x) (3*a*x+2*b)*x+c;

descrim = 18*a*b*c*d-4*b^3*d+b^2*c^2-4*a*a^3-27*a^2*d^2; % Not used.

% Checking if polynomial is not truly cubic.
if ((a==0) && (b==0) && (c==0)) % Checking if constant.
    return;
elseif ((a==0) && (b==0)) % Checking if linear.
    linPoly(1) = c;
    linPoly(2) = d;
    rts = linear(linPoly);
    return;    
elseif (a==0) % Checking if quadratic.
    quadPoly(1) = b;
    quadPoly(2) = c;
    quadPoly(3) = d;
    rts = quadratic(quadPoly);
    return;
end

% Finding first solution given a true cubic.
if (d==0)
    rts(1) = 0;
else % Determining lower bound. Adapted from
     % http://www.mathsisfun.com/algebra/polynomials-bounds-zeros.html
    if (a~=1) 
        bNew = abs(b/a);
        cNew = abs(c/a);
        dNew = abs(d/a);
        coeffArray = [bNew cNew dNew];
        maxCoef = max(coeffArray)+1;
        lowerBound = min(ceil(bNew+cNew+dNew), maxCoef);
    else
        bNew = abs(b);
        cNew = abs(c);
        dNew = abs(d);
        coeffArray = [bNew cNew dNew];
        maxCoef = max(coeffArray)+1;
        lowerBound = min(ceil(bNew+cNew+dNew), maxCoef);
    end
    % Finding interval for bisection.
    if (lowerBound < 2) % Small solutions.
        a1 = -1;
        b1 = 0;
        a2 = 0;
        b2 = 1;
        while ((sign(funcEval(a1)*funcEval(b1))==1)&&(sign(funcEval(a2)*funcEval(b2))==1))
            a1 = a1-10^-6;
            b1 = b1-10^-6;
            a2 = a2+10^-6;
            b2 = b2+10^-6;
        end
    elseif (lowerBound > 10^12) % Large solutions.
        a1 = -2;
        b1 = -0.5;
        a2 = 0.5;
        b2 = 2;
        while ((sign(funcEval(a1)*funcEval(b1))==1)&&(sign(funcEval(a2)*funcEval(b2))==1))
            a1 = 2*a1;
            b1 = 2*b1;
            a2 = 2*a2;
            b2 = 2*b2;
        end
    else % Normal solutions.
        a1 = -2;
        b1 = 0;
        a2 = 0;
        b2 = 2;
        while ((sign(funcEval(a1)*funcEval(b1))==1)&&(sign(funcEval(a2)*funcEval(b2))==1))
            a1 = a1-1;
            b1 = b1-1;
            a2 = a2+1;
            b2 = b2+1;
        end
    end
    % Solution either on the interval bound, or found with bisection
    % method.
    if (funcEval(a1)==0)
        x1 = a1;
        rts(1) = x1;
    elseif (funcEval(b2)==0)
        x1 = b2;
        rts(1) = x1;
    elseif (sign(funcEval(a1)*funcEval(b1))==-1)
        if (lowerBound < 2)
            x1 = bisection(funcEval, a1, b1, 10^-60, 1000);
            rts(1)=x1;
        elseif (lowerBound > 10^12)
            x1 = bisection(funcEval, a1, b1, 10^-4, 100);
            rts(1)=x1;
        else
            x1 = bisection(funcEval, a1, b1, 10^-16, 100);
            rts(1)=x1;
        end
    elseif (sign(funcEval(a2)*funcEval(b2))==-1)
        if (lowerBound < 2)
            x1 = bisection(funcEval, a2, b2, 10^-60, 1000);
            rts(1)=x1;
        elseif (lowerBound > 10^12)
            x1 = bisection(funcEval, a2, b2, 10^-4, 100);
            rts(1)=x1;
        else
            x1 = bisection(funcEval, a2, b2, 10^-16, 100);
            rts(1)=x1;
        end
    else
        error('First root not found.');
    end
end

if (a~=0) % Dividing out by first root.
    x1 = rts(1);
    quadPoly(1) = a;
    quadPoly(2) = a*x1+b;
    quadPoly(3) = a*x1^2+b*x1+c;
else
    error('No quadratic possible.');
end

qSol = quadratic(quadPoly);

% Because the results of deflating the polynomial are unknown we must check how
% many solutions are returned by the quadratic function.
%
% These values are then used as the initial approximations using Newton's
% Method.

if (isempty(qSol)) % If quadratic has no solution.
    return;
elseif (length(qSol)==1) % If quadratic has one solution (and so is acutally linear).
    rts(length(rts)+1) = newton(funcEval, derivEval, qSol(1), 10^-7, 100);
    return;
elseif (length(qSol)==2) % If quadratic has two solutions (the usual case).
    rts(length(rts)+1) = newton(funcEval, derivEval, qSol(1), 10^-7, 100);
    rts(length(rts)+1) = newton(funcEval, derivEval, qSol(2), 10^-7, 100);
    return;
else
    error('Invalid quadratic solutions returned.');
    
end

end


% Helper functions defined below (all written by me).
function y = bisection(func, a, b, tol, N)
fa = func(a);
fb = func(b);
if (b <= a)
    error('b must be greater than a.')
end
if (fa*fb > 0)
    error('Images of a and b must have opposite signs.')
end
i = 1;
while ((abs(a-b) > tol) && (i <= N))
    m = (a+b)/2;
    fm = func(m);
    if (fa*fm < 0)
        b = m;
        fb = fm;
    elseif (fb*fm < 0)
        a = m;
        fa = fm;
    else
        y = m;
        return;
    end
    i = i+1;
end
y = ((a + b)/2);
return;
end

function qrts = quadratic(Q)
format long;
x = Q(1);
y = Q(2);
z = Q(3);
if (x==0) % If polynomial passed in is actually linear.
    linPoly(1) = y;
    linPoly(2) = z;
    qrts=linear(linPoly);
    return;
elseif (y==0)
    qrts(1)=sqrt(-z/x);
    qrts(2)=-sqrt(-z/x);
    return;
else
    t = (1 + sqrt(1-(4*x*z/y^2)))/2;
    qrts(1) = (-y/x)*t;
    qrts(2) = -z/(y*t);
    return;
end
end

function lrt = linear(L)
m = L(1);
n = L(2);

if (m==0)
    lrt=[];
    return;
else
    lrt(1)=(-n/m);
    return;
end
end

function y = newton(func, deriv, p0, tol, N)
% Uses Newton's method to find a zero near initial
% x-value, p0. Finds solution within a relative
% error of tol.
% Based on Algorithm 2.3 on p68 of the text.
i = 1;
while (i <= N)
    p = p0 - (func(p0)/(deriv(p0)));
    if ((abs(p - p0)/p) < tol) % Relative error.
        y = p;
        return;
    end
    p0 = p;
    i = i + 1;
end
y = p; % Necessary for cubic to work (though this should throw an error).
end
