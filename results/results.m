
M = csvread('RHCRunner_vote.arff.csv', 1, 0);
M(:, 3) = 1 - M(:, 3);
plot_output2('Randomized Hill Climbing', 'Train/test split', 'Testing error (% incorrectly labeled)', M, 1, 3, 2, {})
M = csvread('SimulatedAnnealingRunner_vote.arff.csv', 1, 0);
M(:, 3) = 1 - M(:, 3);
plot_output2('Simulated Annealing', 'Train/test split', 'Testing error (% incorrectly labeled)', M, 1, 3, 2, {})
M = csvread('GeneticRunner_vote.arff.csv', 1, 0);
M(:, 3) = 1 - M(:, 3);
plot_output2('Genetic Algorithms', 'Train/test split', 'Testing error (% incorrectly labeled)', M, 1, 3, 2, {})

contPeaks = [];
fourPeaks = [];
tsp = [];

algo = {'SA', 'SGA', 'MIMIC'};
for i = 1:size(algo, 2)
    M = csvread(sprintf('ContinuousPeaksTest_%s.csv', algo{i}), 1, 0); 
    contPeaks = [ contPeaks ;
                  [repmat(i, size(M, 1), 1) M] ];
    M = csvread(sprintf('FlipFlopTest_%s.csv', algo{i}), 1, 0); 
    fourPeaks = [ fourPeaks ;
                  [repmat(i, size(M, 1), 1) M] ];
    M = csvread(sprintf('TravelingSalesmanTest_%s.csv', algo{i}), 1, 0); 
    tsp = [ tsp ;
                  [repmat(i, size(M, 1), 1) M] ];
end

plot_output2('Continuous Peaks', 'Algorithm', 'Optimal Value', contPeaks, 2, 3, 1, algo);
plot_output2('Four Peaks', 'Algorithm', 'Optimal Value', fourPeaks, 2, 3, 1, algo);
plot_output2('Traveling Salesman Problem', 'Algorithm', 'Optimal Value', tsp, 2, 3, 1, algo);