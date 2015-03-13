function [ output_args ] = drawLine( a, b, c, minX, maxX )
%Draw line ax + by = c where minX <= x <= maxX
x = linspace(minX, maxX);
y = (c - a*x) / b;
output_args = plot(x, y);

end

