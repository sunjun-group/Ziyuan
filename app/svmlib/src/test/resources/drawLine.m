function [ output_args ] = drawLine( a, b, c, minX, maxX, minY, maxY)
%Draw line ax + by = c where minX <= x <= maxX

if(a == 0)
    y = c/b;
    output_args = plot([minX, maxX], [y, y]);
else
    if (b == 0)
        x = c/a;
        output_args = plot([x, x], [minY, maxY]);
    else
        x = [minX, maxX];
        y = (c - a*x) / b;
        output_args = plot(x, y);
    end
end
axis([minX, maxX, minY, maxY]);
end

