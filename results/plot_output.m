%
%
%
function plot_output( name, data, x_col, y_col, chart_col )

    colors = ['r' 'g' 'b' 'c' 'm' 'y' 'k' ]
    plts = unique(data(:, chart_col));

    for i = 1:size(plts, 1)
        fig = figure(i)
        split = plts(i);
        
        rows = find(data(:, chart_col) == plts(i));

        colorInx = i % size(colors, 2); 
        lineSpec = sprintf('x-%s', colors(colorInx));
        plot(data(rows, x_col), 1 - data(rows, y_col), lineSpec);
        axis([0 max(data(:, x_col)) 0 1]);
        set(gca, 'XTickLabel', num2str(get(gca,'XTick')','%d'))  %#'
        %xticklabel_rotate([],45)                  %# rotate the xtick-labels 45 degrees

        if split == 0 || split == 100
            splitLbl = 'Training set';
        else
            splitLbl = sprintf('Percentage split: %d%%', split);
        end
        xlabel(sprintf('%s, %s', name, splitLbl));
        ylabel('Testing error (% labeled incorrect)')
        
        fileprefix = strrep(lower(name), ' ', '_');
        filename   = sprintf('img/%s_%d.jpg', fileprefix, split);
        print(fig, '-djpeg', filename);

    end

end

