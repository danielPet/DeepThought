function [Tout, Xout, DXout, info] = RKF(T0,Tfinal,X0,DX0,tol,A,Mu,omega)
%
% Written by Daniel Peterson for Math128A Spring 2015
%
% This is a method used to solve the van der Pol equation:
% x''-Mu(1-x^2)*x'+x-A*sin(omega*t)=0, where x' and x''
% are the first and second derivatives of x respectively.
%
% On input:
%   T0 is the initial start time.
%   Tfinal is the final time.
%   X0 is the initial x-value.
%   DX0 is the initial x'-value.
%   tol is the error tolerance of the approximation.
%   A, Mu, and omega are the values given in the van der Pol
%       equation described above.
%
% On output:
%   Tout is a vector of the t-values at accepted nodal points.
%   Xout is a vector of the approximated x-values at the nodal points.
%   DXout is a vector of the approximated x'-values at the nodal points.
%   info is a string message.
% 

% Initializing variables:
flg  = 0;
info = 'Solution brought to you by Daniel Peterson.';
i = 0;
a = T0;
b = Tfinal;

% Substitution functions for van der Pol equation:
func1 =@(t,x,dx)dx;
func2 =@(t,x,dx)Mu*(1 - x^2)*dx - x + A*sin(omega*t);

% Initializing return vectors' values.
Tout  = a;
Xout  = X0;
DXout = DX0;

h = 1e-3;
hmax = 0.1;



c = [0;      1/4;  3/8;        12/13;       1;    1/2 ];
d = [25/216 0    1408/2565  2197/4104  -1/5  0   ];
r = [1/360  0   -128/4275  -2197/75240  1/50 2/55];

KC = [0          0          0         0          0;
      1/4        0          0         0          0;
      3/32       9/32       0         0          0;
      1932/2197 -7200/2197  7296/2197 0          0;
      439/216   -8          3680/513  -845/4104  0;
      -8/27      2         -3544/2565 1859/4104 -11/40];

KX  = zeros(6,1); % Where K-values for the approximation
KDX = zeros(6,1);

while (flg == 0)
    % Running the RKF variable step-size method for systems.
    ti = Tout(end);
    wi = Xout(end);
    dwi = DXout(end);
    KX  = zeros(6,1);
    KDX = zeros(6,1);
    
    KX(1)  = h*func1(ti+c(1)*h, wi+KC(1,:)*KX(1:5), dwi+KC(1,:)*KDX(1:5));
    KDX(1) = h*func2(ti+c(1)*h, wi+KC(1,:)*KX(1:5), dwi+KC(1,:)*KDX(1:5));
    KX(2)  = h*func1(ti+c(2)*h, wi+KC(2,:)*KX(1:5), dwi+KC(2,:)*KDX(1:5));
    KDX(2) = h*func2(ti+c(2)*h, wi+KC(2,:)*KX(1:5), dwi+KC(2,:)*KDX(1:5));
    KX(3)  = h*func1(ti+c(3)*h, wi+KC(3,:)*KX(1:5), dwi+KC(3,:)*KDX(1:5));
    KDX(3) = h*func2(ti+c(3)*h, wi+KC(3,:)*KX(1:5), dwi+KC(3,:)*KDX(1:5));
    KX(4)  = h*func1(ti+c(4)*h, wi+KC(4,:)*KX(1:5), dwi+KC(4,:)*KDX(1:5));
    KDX(4) = h*func2(ti+c(4)*h, wi+KC(4,:)*KX(1:5), dwi+KC(4,:)*KDX(1:5));
    KX(5)  = h*func1(ti+c(5)*h, wi+KC(5,:)*KX(1:5), dwi+KC(5,:)*KDX(1:5));
    KDX(5) = h*func2(ti+c(5)*h, wi+KC(5,:)*KX(1:5), dwi+KC(5,:)*KDX(1:5));
    KX(6)  = h*func1(ti+c(6)*h, wi+KC(6,:)*KX(1:5), dwi+KC(6,:)*KDX(1:5));
    KDX(6) = h*func2(ti+c(6)*h, wi+KC(6,:)*KX(1:5), dwi+KC(6,:)*KDX(1:5));

% Check tolerance and accept approximation.
    R = max(abs(r*KX)/h,abs(r*KDX)/h);
    R = max(eps,R);
    
    % New values to add.
    newT = ti+h;
    newX = wi+d*KX;
    newDX = dwi+d*KDX;
    
    if (R <= tol)
        i = i + 1;
        Tout = [Tout; newT];
        Xout = [Xout; newX];
        DXout = [DXout; newDX];
    end

% Resize h:
    delta = 0.7*(tol/R)^(1/4);
    delta = min(4,max(0.1,delta));
    if (abs(DXout(end)) > 2)
        hmax = 0.005;
    elseif (abs(DXout(end)) > 5)
        hmax = 0.001;
    elseif (abs(DXout(end)) < 1)
        hmax = 0.1;
    elseif (Mu == 0)
        hmax = 0.1;
    end
    h = min(hmax, delta * h);
    h = min(h, b-Tout(end)); %This ends the loop.
    
    if (abs(h)<eps)
        return;
    end

    if (ti >= Tfinal)
        flg = 1;
        return;
    end
    
end
end