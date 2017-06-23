function [ output_args ] = readData( input_args )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here

close all;

data = load(input_args);

numberOfPositives = data(1:1,1);
numberOfNegatives = data(1:1,2);

%
firstLinePositives = 2;
lastLinePositives = firstLinePositives + numberOfPositives - 1;
firstLineNegatives = lastLinePositives + 1;
lastLineNegatives = firstLineNegatives + numberOfNegatives - 1;
positives = data(firstLinePositives:lastLinePositives, :);
negatives = data(firstLineNegatives:lastLineNegatives, :);

%draw
scatter(positives(:, 1), positives(:, 2), '+')
hold on
scatter(negatives(:, 1), negatives(:, 2), 'o')

end

